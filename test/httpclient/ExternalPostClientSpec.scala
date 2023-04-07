package httpclient

import domain.models.Post
import org.mockito.{ArgumentMatchers, Mockito}
import org.mockito.Mockito.{never, times, verify, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.Configuration
import play.api.http.HttpVerbs
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{BodyWritable, WSAuthScheme, WSClient, WSRequest, WSRequestFilter, WSResponse}

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext.global
import scala.concurrent.Future

class ExternalPostClientSpec extends PlaySpec with MockitoSugar with ScalaFutures with BeforeAndAfterEach {

  val mockWSClient: WSClient = mock[WSClient]
  val wsRequest: WSRequest = mock[WSRequest]
  val wsResponse: WSResponse = mock[WSResponse]
  val mockConfig: Configuration = mock[Configuration]

  val httpClient: ExternalPostClient = new ExternalPostClient(mockWSClient, mockConfig)(global)

  val baseUrl = "https://test.com"
  val username = "username"
  val password = "password"
  val apiPath = "/external/posts"
  val post: Post = Post(Some(2L), 444L, "Post Title 222", "Post Content 222", LocalDateTime.now(), Some("Post Desc 222"))

  val postJsValue: JsValue = Json.toJson(post)

  override def beforeEach(): Unit = {
    super.beforeEach()
    when(mockConfig.get[String]("external.posts.auth.basic.url")).thenReturn(baseUrl)
    when(mockConfig.get[String]("external.posts.auth.basic.username")).thenReturn(username)
    when(mockConfig.get[String]("external.posts.auth.basic.password")).thenReturn(password)

    // Mocking WSClient behaviour
    when(mockWSClient.url(ArgumentMatchers.any[String])).thenReturn(wsRequest)
    when(wsRequest.withMethod(ArgumentMatchers.any[String])).thenReturn(wsRequest)
    when(wsRequest.withQueryStringParameters(ArgumentMatchers.any())).thenReturn(wsRequest)
    when(wsRequest.withBody(ArgumentMatchers.any[JsValue])(ArgumentMatchers.any[BodyWritable[JsValue]])).thenReturn(wsRequest)
    when(wsRequest.addHttpHeaders(ArgumentMatchers.any())).thenReturn(wsRequest)
    when(wsRequest.withAuth(ArgumentMatchers.any[String], ArgumentMatchers.any[String], ArgumentMatchers.any[WSAuthScheme]))
      .thenReturn(wsRequest)
    when(wsRequest.withRequestFilter(ArgumentMatchers.any[WSRequestFilter])).thenReturn(wsRequest)
  }

  "ExternalPostClient#get" should {

    "get an external post successfully" in {

      // Mocking data
      when(wsRequest.execute()).thenReturn(Future.successful(wsResponse)) // when request is executed, it should return a response
      when(wsResponse.status).thenReturn(200) // this is OK
      when(wsResponse.body[JsValue]).thenReturn(postJsValue)

      // Execute test
      val result = httpClient.get[Post](s"${apiPath}/2").futureValue

      // Verify data after tests
      verifyPost(result, post)
      verify(mockConfig, times(3)).get[String](ArgumentMatchers.any[String])(ArgumentMatchers.any())
      verify(mockWSClient, Mockito.only()).url(ArgumentMatchers.eq(s"${baseUrl}${apiPath}/2"))

      verify(wsRequest, times(1)).withMethod(ArgumentMatchers.eq(HttpVerbs.GET))
      verify(wsRequest, never()).withQueryStringParameters(ArgumentMatchers.any())
      verify(wsRequest, never()).withBody(ArgumentMatchers.any[JsValue])(ArgumentMatchers.any[BodyWritable[JsValue]])
      verify(wsRequest, never()).addHttpHeaders(ArgumentMatchers.any())
      verify(wsRequest, times(1))
        .withAuth(ArgumentMatchers.eq(username), ArgumentMatchers.eq(password), ArgumentMatchers.eq(WSAuthScheme.BASIC))
      verify(wsRequest, times(1)).withRequestFilter(ArgumentMatchers.any[WSRequestFilter])
    }
  }

  private def verifyPost(actual: Post, expected: Post): Unit = {
    actual.id.get mustEqual expected.id.get
    actual.author mustEqual expected.author
    actual.title mustEqual expected.title
    actual.content mustEqual expected.content
    actual.date mustEqual expected.date
    actual.description.get mustEqual expected.description.get
  }

  // Same for remaining methods
}
