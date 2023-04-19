package domain.dao

import com.google.inject.{ImplementedBy, Inject, Singleton}
import domain.models.User
import domain.tables.UserTable
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

@ImplementedBy(classOf[UserDaoImpl])
trait UserDao {

  /**
   * Finds a user by id.
   *
   * @param id User's id.
   * @return The found user or None if no user for the given id could be found.
   */
  def find(id: Long): Future[Option[User]]

  /**
   * Finds a user by email.
   *
   * @param email User's email.
   * @return The found user or None if no user for the given email could be found.
   */
  def find(email: String): Future[Option[User]]


  /**
   * List all users.
   *
   * @return All existing users.
   */
  def listAll(): Future[Iterable[User]]

  /**
   * Saves a user.
   *
   * @param user The user to save.
   * @return The saved user.
   */
  def save(user: User): Future[User]

  /**
   * Saves all users.
   *
   * @param list The users to save.
   * @return The saved users.
   */
  def saveAll(list: Seq[User]): Future[Seq[User]]

  /**
   * Updates a user.
   *
   * @param user The user to update.
   * @return The saved user.
   */
  def update(user: User): Future[User]

  /**
   * Deletes a user
   * @param email The user's email to delete.
   * @return The deleted user.
   */
  def delete(email: String): Future[Int]
}

/**
 * UserDao implementation class
 *
 * @param daoRunner DaoRunner for running query in a transaction
 * @param ec Execution context
 */
@Singleton
class UserDaoImpl @Inject()(daoRunner: DaoRunner)(implicit ec: DbExecutionContext)
  extends UserDao {

  private val users = TableQuery[UserTable]

  override def find(id: Long): Future[Option[User]] = daoRunner.run {
    users.filter(_.id === id).result.headOption
  }

  override def find(email: String): Future[Option[User]] = daoRunner.run {
    users.filter(_.email === email).result.headOption
  }

  override def listAll(): Future[Iterable[User]] = daoRunner.run {
    users.result
  }

  override def save(user: User): Future[User] = daoRunner.run {
    users returning users += user
  }

  override def saveAll(list: Seq[User]): Future[Seq[User]] = daoRunner.run {
    (users ++= list).map(_ => list)
  }

  override def update(user: User): Future[User] = daoRunner.run {
    users.filter(_.email === user.email).update(user).map(_ => user)
  }

  override def delete(email: String): Future[Int] = daoRunner.run {
    users.filter(_.email === email).delete
  }
}
