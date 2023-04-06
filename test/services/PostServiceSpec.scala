package services

import domain.dao.PostDao
import domain.models.Post
import org.mockito.ArgumentMatchers.{anyInt, anyLong}
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext.global
import scala.concurrent.Future

class PostServiceSpec extends PlaySpec with MockitoSugar with ScalaFutures {

  val mockPostDao: PostDao = mock[PostDao]

  val postService: PostService = new PostServiceImpl(mockPostDao)(global)

  "PostService#find(id: Long)" should {
    "get a post successfully" in {
      val post = Post(Some(2L), 444L, "Post Title 222", "Post Content 222", LocalDateTime.now(), Some("Post Desc 222"))
      when(mockPostDao.find(anyLong())).thenReturn(Future.successful(Some(post)))

      val result = postService.find(1L).futureValue
      result.isEmpty mustBe false
      val actual = result.get
      actual.id.get mustEqual post.id.get
      actual.author mustEqual post.author
      actual.title mustEqual post.title
      actual.content mustEqual post.content
      actual.date mustEqual post.date
      actual.description.get mustEqual post.description.get
    }

    "post not found" in {
      when(mockPostDao.find(anyLong())).thenReturn(Future.successful(None))

      val result = postService.find(1L).futureValue
      result.isEmpty mustBe true
    }
  }

  // Same for remaining methods
}
