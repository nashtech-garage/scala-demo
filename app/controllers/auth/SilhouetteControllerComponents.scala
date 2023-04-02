package controllers.auth

import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.{Clock, PasswordHasherRegistry}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import play.api.http.FileMimeTypes
import play.api.i18n.{Langs, MessagesApi}
import play.api.mvc._
import services.UserService
import utils.auth.JWTEnvironment

import javax.inject.Inject
import scala.concurrent.ExecutionContext

/**
 * Contains silhouette components
 */
trait SilhouetteComponents {
  type EnvType = JWTEnvironment
  type AuthType = EnvType#A
  type IdentityType = EnvType#I

  def userService: UserService
  def authInfoRepository: AuthInfoRepository
  def passwordHasherRegistry: PasswordHasherRegistry
  def clock: Clock
  def credentialsProvider: CredentialsProvider

  def silhouette: Silhouette[EnvType]
}

/**
 * Contains silhouette components and messages api components
 */
trait SilhouetteControllerComponents extends MessagesControllerComponents with SilhouetteComponents



/**
 * Default Silhouette controller implementation
 */
final case class DefaultSilhouetteControllerComponents @Inject() (silhouette: Silhouette[JWTEnvironment],
                                                                  userService: UserService,
                                                                  authInfoRepository: AuthInfoRepository,
                                                                  passwordHasherRegistry: PasswordHasherRegistry,
                                                                  clock: Clock,
                                                                  credentialsProvider: CredentialsProvider,
                                                                  messagesActionBuilder: MessagesActionBuilder,
                                                                  actionBuilder: DefaultActionBuilder,
                                                                  parsers: PlayBodyParsers,
                                                                  messagesApi: MessagesApi,
                                                                  langs: Langs,
                                                                  fileMimeTypes: FileMimeTypes,
                                                                  executionContext: ExecutionContext)
  extends SilhouetteControllerComponents
