package controllers.post

import com.google.inject.AbstractModule
import com.mohiva.play.silhouette.api.Environment
import com.mohiva.play.silhouette.password.BCryptPasswordHasher
import com.mohiva.play.silhouette.test._
import domain.dao._
import domain.models._
import fixtures.TestApplication
import net.codingwell.scalaguice.ScalaModule
import org.scalatest.Suite
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import services._
import utils.auth.JWTEnvironment

import scala.concurrent.ExecutionContext.Implicits.global

class ControllerFixture extends PlaySpec with Suite with GuiceOneAppPerSuite with MockitoSugar with ScalaFutures {
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

  implicit override lazy val app: Application = TestApplication.appWithOverridesModule(module = new FakeServiceModule())
}
