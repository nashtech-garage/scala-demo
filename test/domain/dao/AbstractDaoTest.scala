package domain.dao

import fixtures.AbstractPersistenceTests
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec

abstract class AbstractDaoTest extends PlaySpec
  with MockitoSugar
  with AbstractPersistenceTests
  with ScalaFutures {

}
