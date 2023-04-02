package controllers.post

import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredActionBuilder
import domain.models.Post
import play.api.Logger
import play.api.data.Form
import play.api.libs.json.{JsString, Json}
import play.api.mvc._
import services.{PostService, UserService}
import utils.auth.JWTEnvironment
import utils.logging.RequestMarkerContext

import java.time.LocalDateTime
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

case class PostFormInput(author: Long, title: String, content: String, description: Option[String])

/**
 * Takes HTTP requests and produces JSON.
 */
class PostController @Inject() (cc: PostControllerComponents, silhouette: Silhouette[JWTEnvironment])
                               (implicit ec: ExecutionContext)
  extends AbstractController(cc) with RequestMarkerContext {

  def SecuredAction: SecuredActionBuilder[JWTEnvironment, AnyContent] = silhouette.SecuredAction
  def postService: PostService = cc.postService
  def userService: UserService = cc.userService

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

  def getById(id: Long): Action[AnyContent] = SecuredAction.async { implicit request =>
    logger.trace(s"getById: $id")
    postService.find(id).map {
      case Some(post) => Ok(Json.toJson(PostResource.fromPost(post)))
      case None => NotFound
    }
  }

  def getAll: Action[AnyContent] =  SecuredAction.async { implicit request =>
    logger.trace("getAll Posts")
    postService.listAll().map { posts =>
      Ok(Json.toJson(posts.map(post => PostResource.fromPost(post))))
    }
  }

  def create: Action[AnyContent] = SecuredAction.async { implicit request =>
    logger.trace("create Post: ")
    processJsonPost(None)
  }

  def update(id: Long): Action[AnyContent] = SecuredAction.async { implicit request =>
    logger.trace(s"update Post id: $id")
    processJsonPost(Some(id))
  }

  def delete(id: Long): Action[AnyContent] = SecuredAction.async { implicit request =>
      logger.trace(s"Delete post: id = $id")
      postService.delete(id).map { deletedCnt =>
        if (deletedCnt == 1) Ok(JsString(s"Delete post $id successfully"))
        else BadRequest(JsString(s"Unable to delete post $id"))
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
