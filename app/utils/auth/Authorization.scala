package utils.auth

import com.mohiva.play.silhouette.api.{Authenticator, Authorization}
import domain.models.User
import play.api.mvc.Request

import scala.concurrent.Future

/**
 * Grants only access if a user has authenticated with the given provider.
 *
 * @param provider The provider ID the user must authenticated with.
 * @tparam A The type of the authenticator.
 */
case class WithProvider[A <: Authenticator](provider: String) extends Authorization[User, A] {

  override def isAuthorized[B](user: User, authenticator: A)(implicit request: Request[B]): Future[Boolean] = {
    Future.successful(user.loginInfo.providerID == provider)
  }
}

/**
 * Only allows those users that have at least a role of the selected.
 * Admin role is always allowed.
 * Ex: WithRole("Viewer", "Creator") => only users with roles "User" OR "Creator" (or "Admin") are allowed.
 * Roles: "User" | "Creator" | "Contributor" |  "Admin"
 */
case class WithRole[A <: Authenticator](anyOf: String*) extends Authorization[User, A] {

  override def isAuthorized[B](user: User, authenticator: A)(implicit request: Request[B]): Future[Boolean] = {
    Future.successful(anyOf.contains(user.role) || user.role.equals("Admin"))
  }
}
