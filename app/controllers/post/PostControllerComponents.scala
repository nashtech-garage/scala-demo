package controllers.post

import play.api.http.{FileMimeTypes, HttpVerbs}
import play.api.i18n.{Langs, MessagesApi}
import play.api.mvc._
import play.api.{Logger, MarkerContext}
import services.{ExternalPostService, PostService, UserService}
import utils.logging.RequestMarkerContext

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

/**
 * A wrapped request for post resources.
 *
 * This is commonly used to hold request-specific information like security credentials, and useful shortcut methods.
 */
trait PostRequestHeader extends MessagesRequestHeader with PreferredMessagesProvider

class PostRequest[A](request: Request[A], val messagesApi: MessagesApi)
  extends WrappedRequest(request) with PostRequestHeader

///**
// * The action builder for the Post resource.
// *
// * This is the place to put logging, metrics, to augment
// * the request with contextual data, and manipulate the
// * result.
// */
//class PostActionBuilder @Inject()(messagesApi: MessagesApi,
//                                  playBodyParsers: PlayBodyParsers)(
//                                   implicit val executionContext: ExecutionContext)
//  extends ActionBuilder[PostRequest, AnyContent] with RequestMarkerContext with HttpVerbs {
//
//  override val parser: BodyParser[AnyContent] = playBodyParsers.anyContent
//
//  type PostRequestBlock[A] = PostRequest[A] => Future[Result]
//
//  private val logger = Logger(this.getClass)
//
//  override def invokeBlock[A](request: Request[A],
//                              block: PostRequestBlock[A]): Future[Result] = {
//    // Convert to marker context and use request in block
//    implicit val markerContext: MarkerContext = requestHeaderToMarkerContext(
//      request)
//    logger.trace(s"invokeBlock: ")
//
//    val future = block(new PostRequest(request, messagesApi))
//
//    future.map { result =>
//      request.method match {
//        case GET | HEAD =>
//          result.withHeaders("Cache-Control" -> s"max-age: 100")
//        case other =>
//          result
//      }
//    }
//  }
//}

/**
 * Packages up the component dependencies for the post controller.
 *
 * This is a good way to minimize the surface area exposed to the controller, so the
 * controller only has to have one thing injected.
 */
case class PostControllerComponents @Inject()(externalPostService: ExternalPostService,
//                                               postActionBuilder: PostActionBuilder,
                                              postService: PostService,
                                              userService: UserService,
                                              actionBuilder: DefaultActionBuilder,
                                              parsers: PlayBodyParsers,
                                              messagesApi: MessagesApi,
                                              langs: Langs,
                                              fileMimeTypes: FileMimeTypes,
                                              executionContext: scala.concurrent.ExecutionContext)
  extends ControllerComponents


