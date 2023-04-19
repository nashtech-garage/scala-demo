package controllers.auth

import com.mohiva.play.silhouette.api.actions.{SecuredActionBuilder, UnsecuredActionBuilder}
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AuthenticatorService
import com.mohiva.play.silhouette.api.util.{Clock, PasswordHasherRegistry}
import com.mohiva.play.silhouette.api.{EventBus, Silhouette}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc._
import services.UserService

/**
 * Abstract silhouette controller which contains all components to work with authentication
 */
abstract class SilhouetteController(override protected val controllerComponents: SilhouetteControllerComponents)
  extends MessagesAbstractController(controllerComponents) with SilhouetteComponents with I18nSupport with Logging {

  // handles requests only for authorized users. For our case, the user has to provide an X-Auth header with valid JWT token value.
  def SecuredAction: SecuredActionBuilder[EnvType, AnyContent] = controllerComponents.silhouette.SecuredAction

  // handles requests which don't need any authorization.
  def UnsecuredAction: UnsecuredActionBuilder[EnvType, AnyContent] = controllerComponents.silhouette.UnsecuredAction

  def userService: UserService = controllerComponents.userService
  def authInfoRepository: AuthInfoRepository = controllerComponents.authInfoRepository
  def passwordHasherRegistry: PasswordHasherRegistry = controllerComponents.passwordHasherRegistry
  def clock: Clock = controllerComponents.clock
  def credentialsProvider: CredentialsProvider = controllerComponents.credentialsProvider

  def silhouette: Silhouette[EnvType] = controllerComponents.silhouette
  def authenticatorService: AuthenticatorService[AuthType] = silhouette.env.authenticatorService
  def eventBus: EventBus = silhouette.env.eventBus

}
