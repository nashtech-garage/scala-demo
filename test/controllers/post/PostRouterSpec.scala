package controllers.post

import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.{SecuredAction, SecuredActionBuilder}
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import domain.models.Post
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._
import play.api.libs.json.Json
import play.api.mvc.{AnyContent, AnyContentAsEmpty, ControllerComponents, Result}
import play.api.test.CSRFTokenHelper._
import play.api.test.Helpers._
import play.api.test._
import services.{ExternalPostService, PostService, UserService}
import utils.auth.JWTEnvironment

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext.global
import scala.concurrent.Future

class PostRouterSpec extends PlaySpec with GuiceOneAppPerTest with MockitoSugar with Injecting with ScalaFutures {

  // create a mock dependency
  val mockPostService: PostService = mock[PostService]
  val mockExternalPostService: ExternalPostService = mock[ExternalPostService]
  val mockUserService: UserService = mock[UserService]
  val mockSecuredAction: SecuredActionBuilder[JWTEnvironment, AnyContent] = mock[SecuredActionBuilder[JWTEnvironment, AnyContent]]
  val mockAuthenticator: JWTAuthenticator = mock[JWTAuthenticator]

  val controllerComponents: ControllerComponents = Helpers.stubControllerComponents()
  val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()
  val silhouette: Silhouette[JWTEnvironment] = mock[Silhouette[JWTEnvironment]]

  val controller: PostController =
    new PostController(controllerComponents, mockPostService, mockExternalPostService, mockUserService, silhouette)(global)

  "PostRouter" should {

    "get all posts successfully" in {
      // will do the things later

//      when(silhouette.SecuredAction).thenReturn(mockSecuredAction)
//      val post = Post(Some(1L), 1L, "Title", "Content", LocalDateTime.now(), Some("Description"))
//
//      val request = FakeRequest(GET, "/v1/posts")
//      request.withHeaders(HOST -> "localhost:8080").withCSRFToken
//      val result:Future[Result] = route(app, request).get
//
//      val posts: Seq[PostResource] = Json.fromJson[Seq[PostResource]](contentAsJson(result)).get
//      posts.size mustEqual 0
    }
  }

}
