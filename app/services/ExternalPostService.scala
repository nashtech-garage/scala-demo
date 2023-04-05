package services

import com.google.inject.{ImplementedBy, Inject, Singleton}
import domain.models.Post
import httpclient.ExternalPostClient
import play.api.Configuration
import play.api.libs.json.Json

import scala.concurrent.{ExecutionContext, Future}

/**
 * ExternalPostService.
 */
@ImplementedBy(classOf[ExternalPostServiceImpl])
trait ExternalPostService {

  /**
   * List all External Posts.
   *
   * @return All external posts.
   */
  def listAll(): Future[Iterable[Post]]

  /**
   * Saves an external post.
   *
   * @param post The external post to save.
   * @return The saved post.
   */
  def save(post: Post): Future[Post]

}

/**
 * Handles actions to External Posts.
 *
 * @param client  The HTTP Client instance
 * @param ex      The execution context.
 */
@Singleton
class ExternalPostServiceImpl @Inject()(client: ExternalPostClient, config: Configuration)
                                       (implicit ex: ExecutionContext)
  extends ExternalPostService {

  def getAllPosts: String = config.get[String]("external.posts.getAllPosts")

  def createPost: String = config.get[String]("external.posts.createPost")

  override def listAll(): Future[Iterable[Post]] = client.get[Seq[Post]](getAllPosts)

  override def save(post: Post): Future[Post] = client.post[Post](createPost, Some(Json.toJson(post)))

}


