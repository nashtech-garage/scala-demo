package domain.dao

import domain.models.Post
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar

import java.time.LocalDateTime

class PostDaoSpec extends AbstractDaoTest with MockitoSugar with ScalaFutures {

  val postDao: PostDao = get[PostDao]

  // insert some posts for testing, the Post's id will be generated by DB engine with auto increment
  val post1: Post = Post(None, 888L, "Post Title 111", "Post Content 111", LocalDateTime.now(), Some("Post Desc 111"))
  val post2: Post = Post(None, 888L, "Post Title 112", "Post Content 112", LocalDateTime.now(), Some("Post Desc 112"))
  val post3: Post = Post(None, 999L, "Post Title 113", "Post Content 113", LocalDateTime.now(), Some("Post Desc 113"))
  val post4: Post = Post(None, 999L, "Post Title 114", "Post Content 114", LocalDateTime.now(), Some("Post Desc 114"))

  override protected def beforeAll(): Unit = {

    // Save prepared data to db
    postDao.save(post1).futureValue // id = 1
    postDao.save(post2).futureValue // id = 2
    postDao.save(post3).futureValue // id = 3
    postDao.save(post4).futureValue // id = 4
  }

  "PostDao#find(id: Long)" should {

    "get a post successfully" in {
      val result = postDao.find(1L).futureValue
      result.isEmpty mustBe false
      val post = result.get
      post.id.get mustEqual 1L
      post.author mustEqual post1.author
      post.title mustEqual post1.title
      post.content mustEqual post1.content
      post.date mustEqual post1.date
      post.description.get mustEqual post1.description.get
    }

    "post not found" in {
      val result = postDao.find(5L).futureValue
      result.isEmpty mustBe true
    }
  }

  "PostDao#listAll" should {

    "get all posts successfully" in {
      val result = postDao.listAll().futureValue
      result.size mustBe 4
      result.map(_.id.get) must contain allOf(1L, 2L, 3L, 4L)
    }
  }

  "PostDao#save(post)" should {

    "save a post successfully" in {
      val post5 = Post(None, 111L, "Post Title 555", "Post Content 555", LocalDateTime.now(), Some("Post Desc 555"))
      postDao.save(post5).futureValue

      val result = postDao.find(5L).futureValue
      result.isEmpty mustBe false
      val post = result.get
      post.id.get mustEqual 5L // fifth post
      post.author mustEqual post5.author
      post.title mustEqual post5.title
      post.content mustEqual post5.content
      post.date mustEqual post5.date
      post.description.get mustEqual post5.description.get
    }
  }

  "PostDao#update(post)" should {

    "update a post successfully" in {
      val post2 = Post(Some(2L), 444L, "Updated Post Title 222", "Updated Post Content 222", LocalDateTime.now(), Some("Updated Post Desc 222"))
      postDao.update(post2).futureValue

      val result = postDao.find(2L).futureValue
      result.isEmpty mustBe false
      val post = result.get
      post.id.get mustEqual post2.id.get
      post.author mustEqual post2.author
      post.title mustEqual post2.title
      post.content mustEqual post2.content
      post.date mustEqual post2.date
      post.description.get mustEqual post2.description.get
    }
  }

  "PostDao#delete(id: Long)" should {

    "delete a post successfully" in {
      postDao.delete(3L).futureValue

      val result = postDao.find(3L).futureValue
      result.isEmpty mustBe true // post is no longer exists.

      val resultAll = postDao.listAll().futureValue
      resultAll.size mustBe 4
      resultAll.map(_.id.get) must contain allOf(1L, 2L, 4L, 5L) // post 5 is inserted in the above test
    }
  }
}
