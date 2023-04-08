package controllers.post

import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

import javax.inject.{Inject, Singleton}

/**
 * Routes and URLs to the PostResource controller.
 */
@Singleton
class PostRouter @Inject() (controller: PostController) extends SimpleRouter {

  override def routes: Routes = {
    case GET(p"/") =>
      controller.getAll

    case GET(p"/$id") =>
      controller.getById(id.toLong)

    case POST(p"/") =>
      controller.create

    case PUT(p"/$id") =>
      controller.update(id.toLong)

    case DELETE(p"/$id") =>
      controller.delete(id.toLong)
  }

}
