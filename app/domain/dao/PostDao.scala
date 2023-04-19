package domain.dao

import com.google.inject.{ImplementedBy, Inject, Singleton}
import domain.models.Post
import domain.tables.PostTable
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

/**
 * A pure non-blocking interface for accessing Posts table
 */
@ImplementedBy(classOf[PostDaoImpl])
trait PostDao {

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
   * Saves all posts.
   *
   * @param list The posts to save.
   * @return The saved posts.
   */
  def saveAll(list: Seq[Post]): Future[Seq[Post]]

  /**
   * Updates a post.
   *
   * @param post The post to update.
   * @return The saved post.
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
 * PostDao implementation class
 *
 * @param daoRunner DaoRunner for running query in a transaction
 * @param ec Execution context
 */
@Singleton
class PostDaoImpl @Inject()(daoRunner: DaoRunner)(implicit ec: DbExecutionContext)
  extends PostDao {

  private val posts = TableQuery[PostTable]

  override def find(id: Long): Future[Option[Post]] = daoRunner.run {
    posts.filter(_.id === id).result.headOption
  }

  override def listAll(): Future[Iterable[Post]] = daoRunner.run {
    posts.result
  }

  override def save(post: Post): Future[Post] = daoRunner.run {
    posts returning posts += post
  }

  override def saveAll(list: Seq[Post]): Future[Seq[Post]] = daoRunner.run {
    (posts ++= list).map(_ => list)
  }

  override def update(post: Post): Future[Post] = daoRunner.run {
    posts.filter(_.id === post.id).update(post).map(_ => post)
  }

  override def delete(id: Long): Future[Int] = daoRunner.run {
    posts.filter(_.id === id).delete
  }
}
