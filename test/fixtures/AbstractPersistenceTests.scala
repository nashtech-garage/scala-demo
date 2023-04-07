package fixtures

import com.opentable.db.postgres.embedded.EmbeddedPostgres
import org.scalatest.{BeforeAndAfterAll, Suite, TestSuite}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application

import scala.reflect.ClassTag

trait AbstractPersistenceTests extends Suite with GuiceOneAppPerSuite with BeforeAndAfterAll {
  self: TestSuite =>

  implicit override lazy val app: Application = TestApplication.app(
    additionalConfiguration = Map(
      "slick.dbs.default.db.url" -> postgres.getJdbcUrl("postgres", "postgres")
    )
  )

  def get[T : ClassTag]: T = app.injector.instanceOf[T]

  val postgres: EmbeddedPostgres = EmbeddedPostgres.builder()
    .setCleanDataDirectory(true)
    .start()

  override protected def afterAll(): Unit = {
    postgres.close()
  }
}
