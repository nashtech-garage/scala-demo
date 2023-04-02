package controllers.auth

import com.google.inject.{Inject, Singleton}
import play.api.libs.json.JsString
import play.api.mvc.{Action, AnyContent, Request}

import scala.concurrent.ExecutionContext

/**
 * The UnsecuredResourceController class.
 */
@Singleton
class UnsecuredResourceController @Inject()(components: SilhouetteControllerComponents)
                                           (implicit ex: ExecutionContext)
  extends SilhouetteController(components) {

  def index: Action[AnyContent] = UnsecuredAction { implicit request: Request[AnyContent] =>
    Ok(JsString("Access Unsecured Endpoint Successfully"))
  }
}
