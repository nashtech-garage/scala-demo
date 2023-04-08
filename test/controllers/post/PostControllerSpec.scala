package controllers.post

import com.google.inject.AbstractModule
import com.mohiva.play.silhouette.api.Environment
import com.mohiva.play.silhouette.password.BCryptPasswordHasher
import com.mohiva.play.silhouette.test.{FakeEnvironment, FakeRequestWithAuthenticator}
import domain.dao.{DaoRunner, PostDao, UserDao}
import domain.models.{Post, User}
import fixtures.TestApplication
import net.codingwell.scalaguice.ScalaModule
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import play.api.Application
import play.api.libs.json.Json
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test._
import services.{PostService, UserService}
import utils.auth.JWTEnvironment
import org.specs2.specification.Scope

import java.time.LocalDateTime
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class PostControllerSpec extends PlaySpecification with MockitoSugar with ScalaFutures {

  "PostController#getById(id: Long)" should {

    "get a post successfully" in new Context {
      new WithApplication(application) {

        // mock response data
        val id = 1L
        val post: Post = Post(Some(id), 444L, "Post Title 222", "Post Content 222", LocalDateTime.now(), Some("Post Desc 222"))
        when(mockUserService.retrieve(identity.loginInfo)).thenReturn(Future.successful(Some(identity)))
        when(mockPostService.find(ArgumentMatchers.eq(id))).thenReturn(Future.successful(Some(post)))

        // prepare test request
        val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, s"/v1/posts/${id}")
          .withHeaders(HOST -> "localhost:8080")
          .withAuthenticator[JWTEnvironment](identity.loginInfo)

        // Execute test and then extract result
        val result: Future[Result] = route(app, request).get

        // verify result after test
        status(result) mustEqual OK
        val resPost: PostResource = Json.fromJson[PostResource](contentAsJson(result)).get
        verifyPost(resPost, post)
      }
    }
  }

  // Same for remaining methods

  private def verifyPost(actual: PostResource, expected: Post): Unit = {
    actual.id mustEqual expected.id.get
    actual.author mustEqual expected.author
    actual.title mustEqual expected.title
    actual.content mustEqual expected.content
    actual.createdDate mustEqual expected.date
    actual.description.get mustEqual expected.description.get
  }

  trait Context extends Scope {
    val mockPostService: PostService = mock[PostService]
    val mockUserService: UserService = mock[UserService]
    val mockDaoRunner: DaoRunner = mock[DaoRunner]
    val mockUserDao: UserDao = mock[UserDao]
    val mockPostDao: PostDao = mock[PostDao]

    val password: String = new BCryptPasswordHasher().hash("fakeP@ssw0rd").password
    val identity: User = User(Some(1L), "user-admin@test.com", "Admin", "admin" , "user", Some(password))
    implicit val env: Environment[JWTEnvironment] = new FakeEnvironment[JWTEnvironment](Seq(identity.loginInfo -> identity))

    class FakeServiceModule extends AbstractModule with ScalaModule {
      override def configure(): Unit = {
        bind[Environment[JWTEnvironment]].toInstance(env)
        bind[PostService].toInstance(mockPostService)
        bind[UserService].toInstance(mockUserService)
        bind[DaoRunner].toInstance(mockDaoRunner)
        bind[UserDao].toInstance(mockUserDao)
        bind[PostDao].toInstance(mockPostDao)
      }
    }

    lazy val application: Application = TestApplication.appWithOverridesModule(module = new FakeServiceModule())
  }

}
