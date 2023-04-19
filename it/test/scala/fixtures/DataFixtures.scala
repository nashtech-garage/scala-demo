package fixtures

import com.mohiva.play.silhouette.password.BCryptSha256PasswordHasher
import domain.dao.{PostDao, UserDao}
import domain.models.{Post, User}
import org.scalatestplus.play.PlaySpec

import java.time.LocalDateTime
import scala.concurrent.Await
import scala.concurrent.duration._

abstract class DataFixtures extends PlaySpec with AbstractPersistenceTests {

  val postDao: PostDao = get[PostDao]
  val userDao: UserDao = get[UserDao]

  def createUsers(users: Seq[User]): Unit = {
    Await.result(userDao.saveAll(users), 5.seconds)
  }

  def createPosts(posts: Seq[Post]): Unit = {
    Await.result(postDao.saveAll(posts), 5.seconds)
  }

  object Users {
    val plainPassword: String = "fakeP@ssw0rd";
    val password: String = new BCryptSha256PasswordHasher().hash(plainPassword).password

    val admin: User = User(Some(1L), "user-admin@test.com", "Admin", "Admin" , "User", Some(password))
    val normalUser: User = User(Some(2L), "user-normal@test.com", "User", "Normal" , "User", Some(password))
    val creator: User = User(Some(3L), "user-creator@test.com", "Creator", "Creator" , "User", Some(password))
    val contributor: User = User(Some(4L), "user-contributor@test.com", "Contributor", "Contributor" , "User", Some(password))

    val allUsers: Seq[User] = Seq(admin, normalUser, creator, contributor)
  }

  object Posts {

    val post1: Post = Post(Some(1L), 101L, "Post Title 101-1", "Post Content 101-1", LocalDateTime.now(), Some("Post Desc 101-1"))
    val post2: Post = Post(Some(2L), 101L, "Post Title 101-2", "Post Content 101-2", LocalDateTime.now(), Some("Post Desc 101-2"))
    val post3: Post = Post(Some(3L), 102L, "Post Title 102-3", "Post Content 102-3", LocalDateTime.now(), Some("Post Desc 102-3"))
    val post4: Post = Post(Some(4L), 103L, "Post Title 103-4", "Post Content 103-4", LocalDateTime.now(), Some("Post Desc 103-4"))

    val allPosts: Seq[Post] = Seq(post1, post2, post3, post4)
  }
}
