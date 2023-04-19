package services

import com.google.inject.{ImplementedBy, Inject, Singleton}
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import domain.dao.UserDao
import domain.models.User

import scala.concurrent.{ExecutionContext, Future}

/**
 * UserService is a type of an IdentityService that can be used to authenticate users.
 */
@ImplementedBy(classOf[UserServiceImpl])
trait UserService extends IdentityService[User] {

  /**
   * Finds a user by id.
   *
   * @param id User's id.
   * @return The found user or None if no user for the given id could be found.
   */
  def find(id: Long): Future[Option[User]]

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
   * Updates a user.
   *
   * @param user The user to update.
   * @return The updated user.
   */
  def update(user: User): Future[User]

  /**
   * Deletes a user.
   *
   * @param email The user's email to delete.
   * @return The deleted user.
   */
  def delete(email: String): Future[Int]
}

/**
 * Handles actions to users.
 *
 * @param userDao The user DAO implementation.
 * @param ex      The execution context.
 */
@Singleton
class UserServiceImpl @Inject() (userDao: UserDao)(implicit ex: ExecutionContext) extends UserService {

  /**
   * Retrieves a user that matches the specified login info.
   *
   * @param loginInfo The login info to retrieve a user.
   * @return The retrieved user or None if no user could be retrieved for the given login info.
   */
  override def retrieve(loginInfo: LoginInfo): Future[Option[User]] = userDao.find(loginInfo.providerKey)

  override def find(id: Long): Future[Option[User]] = userDao.find(id)

  override def listAll(): Future[Iterable[User]] = userDao.listAll()

  override def save(user: User): Future[User] = userDao.save(user)

  override def update(user: User): Future[User] = userDao.update(user)

  override def delete(email: String): Future[Int] = userDao.delete(email)
}


