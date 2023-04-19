package services

import com.google.inject.{ImplementedBy, Inject, Singleton}
import domain.dao.PostDao
import domain.models.Post

import scala.concurrent.{ExecutionContext, Future}

/**
 * PostService.
 */
@ImplementedBy(classOf[PostServiceImpl])
trait PostService {

  /**
   * Finds a post by id.
   *
   * @param id The post id to find.
   * @return The found post or None if no post for the given id could be found.
   */
  def find(id: Long): Future[Option[Post]]

  /**
   * List all posts.
   *
   * @return All existing posts.
   */
  def listAll(): Future[Iterable[Post]]

  /**
   * Saves a post.
   *
   * @param post The post to save.
   * @return The saved post.
   */
  def save(post: Post): Future[Post]

  /**
   * Updates a post.
   *
   * @param post The post to update.
   * @return The updated post.
   */
  def update(post: Post): Future[Post]

  /**
   * Deletes a post
   * @param id The post's id to delete.
   * @return The deleted post.
   */
  def delete(id: Long): Future[Int]
}

/**
 * Handles actions to posts.
 *
 * @param postDao The post DAO implementation.
 * @param ex      The execution context.
 */
@Singleton
class PostServiceImpl @Inject() (postDao: PostDao)(implicit ex: ExecutionContext) extends PostService {

  override def find(id: Long): Future[Option[Post]] = postDao.find(id)

  override def listAll(): Future[Iterable[Post]] = postDao.listAll()

  override def save(post: Post): Future[Post] = postDao.save(post)

  override def update(post: Post): Future[Post] = postDao.update(post)

  override def delete(id: Long): Future[Int] = postDao.delete(id)
}


