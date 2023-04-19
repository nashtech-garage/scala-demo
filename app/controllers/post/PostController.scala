package controllers.post

import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredActionBuilder
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import domain.models.Post
import httpclient.ExternalServiceException
import play.api.Logger
import play.api.data.Form
import play.api.libs.json.{JsString, Json}
import play.api.mvc._
import services.{ExternalPostService, PostService, UserService}
import utils.auth.{JWTEnvironment, WithRole}
import utils.logging.RequestMarkerContext

import java.time.LocalDateTime
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

case class PostFormInput(author: Long, title: String, content: String, description: Option[String])

/**
 * Takes HTTP requests and produces JSON.
 */
class PostController @Inject() (cc: ControllerComponents,
                                postService: PostService,
                                extPostService: ExternalPostService,
                                userService: UserService,
                                silhouette: Silhouette[JWTEnvironment])
                               (implicit ec: ExecutionContext)
  extends AbstractController(cc) with RequestMarkerContext {

  def SecuredAction: SecuredActionBuilder[JWTEnvironment, AnyContent] = silhouette.SecuredAction

  private val logger = Logger(getClass)

  private val form: Form[PostFormInput] = {
    import play.api.data.Forms._

    Form(
      mapping(
        "author" -> longNumber(min = 0),
        "title" -> nonEmptyText(maxLength = 128),
        "content" -> nonEmptyText,
        "description" -> optional(text)
      )(PostFormInput.apply)(PostFormInput.unapply)
    )
  }

  def getById(id: Long): Action[AnyContent] =
    SecuredAction(WithRole[JWTAuthenticator]("User", "Creator", "Contributor")).async { implicit request =>
      logger.trace(s"getById: $id")
      postService.find(id).map {
        case Some(post) => Ok(Json.toJson(PostResource.fromPost(post)))
        case None => NotFound
      }
    }

  def getAll: Action[AnyContent] =
    SecuredAction(WithRole[JWTAuthenticator]("User", "Creator", "Contributor")).async { implicit request =>
      logger.trace("getAll Posts")
      postService.listAll().map { posts =>
        Ok(Json.toJson(posts.map(post => PostResource.fromPost(post))))
      }
    }

  def create: Action[AnyContent] =
    SecuredAction(WithRole[JWTAuthenticator]("Creator")).async { implicit request =>
      logger.trace("create Post: ")
      processJsonPost(None)
    }

  def update(id: Long): Action[AnyContent] =
    SecuredAction(WithRole[JWTAuthenticator]("Creator", "Contributor")).async { implicit request =>
      logger.trace(s"update Post id: $id")
      processJsonPost(Some(id))
    }

  def delete(id: Long): Action[AnyContent] =
    SecuredAction(WithRole[JWTAuthenticator]("Creator")).async { implicit request =>
      logger.trace(s"Delete post: id = $id")
      postService.delete(id).map { deletedCnt =>
        if (deletedCnt == 1) Ok(JsString(s"Delete post $id successfully"))
        else BadRequest(JsString(s"Unable to delete post $id"))
      }
    }

  def getAllExternal: Action[AnyContent] =
    SecuredAction(WithRole[JWTAuthenticator]("User", "Creator", "Contributor")).async { implicit request =>
      logger.trace("getAll External Posts")

      // try/catch Future exception with transform
      extPostService.listAll().transform {
        case Failure(exception) => handleExternalError(exception)
        case Success(posts) => Try(Ok(Json.toJson(posts.map(post => PostResource.fromPost(post)))))
      }
    }

  def createExternal: Action[AnyContent] =
    SecuredAction(WithRole[JWTAuthenticator]("Creator")).async { implicit request =>
      logger.trace("create External Post: ")

      def failure(badForm: Form[PostFormInput]) = {
        Future.successful(BadRequest(JsString("Invalid Input")))
      }

      def success(input: PostFormInput) = {
        // create a post from given form input
        val post = Post(Some(999L), input.author, input.title, input.content, LocalDateTime.now(), input.description)

        extPostService.save(post).transform {
          case Failure(exception) => handleExternalError(exception)
          case Success(post) => Try(Created(Json.toJson(PostResource.fromPost(post))))
        }
      }

      form.bindFromRequest().fold(failure, success)
    }

  private def handleExternalError(throwable: Throwable): Try[Result] = {
    throwable match {
      case ese: ExternalServiceException =>
        logger.trace(s"An ExternalServiceException occurred: ${ese.getMessage}")
        if (ese.error.isEmpty)
          Try(BadRequest(JsString(s"An ExternalServiceException occurred. statusCode: ${ese.statusCode}")))
        else Try(BadRequest(Json.toJson(ese.error.get)))
      case _ =>
        logger.trace(s"An other exception occurred on getAllExternal: ${throwable.getMessage}")
        Try(BadRequest(JsString("Unable to create an external post")))
    }
  }

  private def processJsonPost[A](id: Option[Long])(implicit request: Request[A]): Future[Result] = {

    def failure(badForm: Form[PostFormInput]) = {
      Future.successful(BadRequest(JsString("Invalid Input")))
    }

    def success(input: PostFormInput) = {
      // create a post from given form input
      val post = Post(id, input.author, input.title, input.content, LocalDateTime.now(), input.description)

      postService.save(post).map { post =>
        Created(Json.toJson(PostResource.fromPost(post)))
      }
    }

    form.bindFromRequest().fold(failure, success)
  }
}
