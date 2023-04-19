package controllers.auth

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.util.Credentials
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import domain.models.User
import play.api.i18n.Lang
import play.api.libs.json.{JsString, Json, OFormat}
import play.api.mvc.{Action, AnyContent, Request}

import scala.concurrent.{ExecutionContext, Future}

class AuthController @Inject() (components: SilhouetteControllerComponents)
                               (implicit ex: ExecutionContext)
  extends SilhouetteController(components) {

  case class SignInModel(email: String, password: String)

  implicit val signInFormat: OFormat[SignInModel] = Json.format[SignInModel]

  implicit val userFormat: OFormat[IdentityType] = Json.format[User]

  /**
   * Handles sign up request
   *
   * @return The result to display.
   */
  def signUp: Action[AnyContent] = UnsecuredAction.async { implicit request: Request[AnyContent] =>

    implicit val lang: Lang = supportedLangs.availables.head

    request.body.asJson.flatMap(_.asOpt[User]) match {
      case Some(newUser) if newUser.password.isDefined =>
        userService.retrieve(LoginInfo(CredentialsProvider.ID, newUser.email)).flatMap {
          case Some(_) =>
            Future.successful(Conflict(JsString(messagesApi("user.already.exist"))))
          case None =>
            val authInfo = passwordHasherRegistry.current.hash(newUser.password.get)
            val user = newUser.copy(password = Some(authInfo.password))
            userService.save(user).map(u => Ok(Json.toJson(u.copy(password = None))))
        }
      case _ => Future.successful(BadRequest(JsString(messagesApi("invalid.body"))))
    }
  }

  /**
   * Handles sign in request
   *
   * @return JWT token in header if login is successful or Bad request if credentials are invalid
   */
  def signIn: Action[AnyContent] = UnsecuredAction.async { implicit request: Request[AnyContent] =>

    implicit val lang: Lang = supportedLangs.availables.head

    request.body.asJson.flatMap(_.asOpt[SignInModel]) match {
      case Some(signInModel) =>

        val credentials = Credentials(signInModel.email, signInModel.password)

        credentialsProvider.authenticate(credentials).flatMap { loginInfo =>
          userService.retrieve(loginInfo).flatMap {
            case Some(_) =>
              for {
                authenticator <- authenticatorService.create(loginInfo)
                token <- authenticatorService.init(authenticator)
                result <- authenticatorService.embed(token, Ok)
              } yield {
                logger.debug(s"User ${loginInfo.providerKey} signed success")
                result
              }
            case None => Future.successful(BadRequest(JsString(messagesApi("could.not.find.user"))))
          }
        }.recover {
          case _: ProviderException => BadRequest(JsString(messagesApi("invalid.credentials")))
        }
      case None => Future.successful(BadRequest(JsString(messagesApi("could.not.find.user"))))
    }
  }

}
