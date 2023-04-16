package post

import controllers.post.PostResource
import domain.models.{Post, User}
import fixtures.DataFixtures
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json.{JsValue, Json, OFormat}
import play.api.test._
import play.api.libs.ws._
import play.api.test.Helpers._

class PostIntegrationSpec extends DataFixtures with MockitoSugar with ScalaFutures {

  case class LoginBody(email: String, password: String)
  implicit val format: OFormat[LoginBody] = Json.format[LoginBody]
  val authHeaderKey: String = "X-Auth"

  override protected def beforeAll(): Unit = {
    // insert data before tests
    createUsers(Users.allUsers)
    createPosts(Posts.allPosts)
  }

  "GET /posts/:id" should {

    "get a post successfully" in new WithServer(app) {

      val post: Post = Posts.post1
      val user: User = Users.normalUser
      val loginBody: LoginBody = LoginBody(user.email, Users.plainPassword)

      // login to get access token
      val loginRes: WSResponse = await(WsTestClient.wsUrl("/signIn").post(Json.toJson(loginBody)))
      val accessToken: Option[String] = loginRes.header(authHeaderKey)
      accessToken.isEmpty mustBe false

      // Execute test and then extract result
      val getPostRes: WSResponse = await(
        WsTestClient.wsUrl("/v1/posts/1")
          .addHttpHeaders(authHeaderKey -> accessToken.get)
          .get()
      )

      // verify result after test
      getPostRes.status mustEqual 200
      val actualPost: PostResource = getPostRes.body[JsValue].as[PostResource]
      verifyPost(actualPost, post)
    }
  }

  private def verifyPost(actual: PostResource, expected: Post): Unit = {
    actual.id mustEqual expected.id.get
    actual.author mustEqual expected.author
    actual.title mustEqual expected.title
    actual.content mustEqual expected.content
    actual.createdDate mustEqual expected.date
    actual.description.get mustEqual expected.description.get
  }

}
