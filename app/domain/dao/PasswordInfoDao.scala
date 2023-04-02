package domain.dao

import com.google.inject.{Inject, Singleton}
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO

import scala.concurrent.Future
import scala.reflect.ClassTag

/**
 * PasswordInfo Dao.
 */
@Singleton
class PasswordInfoDao @Inject() (userDao: UserDao)(implicit val classTag: ClassTag[PasswordInfo], ec: DbExecutionContext)
  extends DelegableAuthInfoDAO[PasswordInfo] {

  /**
   * Finds passwordInfo for specified loginInfo
   *
   * @param loginInfo user's email
   * @return user's hashed password
   */
  override def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]] =
    userDao.find(loginInfo.providerKey).map(_.map(_.passwordInfo))

  /**
   * Adds new passwordInfo for specified loginInfo
   *
   * @param loginInfo user's email
   * @param passwordInfo user's hashed password
   */
  override def add(loginInfo: LoginInfo, passwordInfo: PasswordInfo): Future[PasswordInfo] =
    update(loginInfo, passwordInfo)

  /**
   * Updates passwordInfo for specified loginInfo
   *
   * @param loginInfo user's email
   * @param passwordInfo user's hashed password
   */
  override def update(loginInfo: LoginInfo, passwordInfo: PasswordInfo): Future[PasswordInfo] =
    userDao.find(loginInfo.providerKey).flatMap {
      case Some(user) => userDao.update(user.copy(password = Some(passwordInfo.password))).map(_.passwordInfo)
      case None => Future.failed(new Exception("user not found"))
    }

  /**
   * Adds new passwordInfo for specified loginInfo
   *
   * @param loginInfo user's email
   * @param passwordInfo user's hashed password
   */
  override def save(loginInfo: LoginInfo, passwordInfo: PasswordInfo): Future[PasswordInfo] =
    update(loginInfo, passwordInfo)

  /**
   * Removes passwordInfo for specified loginInfo
   *
   * @param loginInfo user's email
   */
  override def remove(loginInfo: LoginInfo): Future[Unit] =
    update(loginInfo, PasswordInfo("", "")).map(_ => ())
}
