package controllers.post

import com.mohiva.play.silhouette.test._
import domain.models._
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.when
import play.api.libs.json.Json
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test._
import utils.auth.JWTEnvironment
import play.api.test.Helpers._

import java.time.LocalDateTime
import scala.concurrent.Future

class PostControllerSpec extends ControllerFixture {

  "PostController#getById(id: Long)" should {

    "get a post successfully" in {

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

  // Same for remaining methods

  private def verifyPost(actual: PostResource, expected: Post): Unit = {
    actual.id mustEqual expected.id.get
    actual.author mustEqual expected.author
    actual.title mustEqual expected.title
    actual.content mustEqual expected.content
    actual.createdDate mustEqual expected.date
    actual.description.get mustEqual expected.description.get
  }

}
