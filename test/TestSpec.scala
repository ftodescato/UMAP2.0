package test

import org.scalatest._
import models.daos.thing.ThingDAOImpl
import models.Thing
import play.api.Play.current
import play.modules.reactivemongo._
import java.util.UUID
import scala.collection.mutable.ListBuffer
import scala.concurrent.{Future, Await}
import scala.concurrent.duration._
import play.api.test.{Helpers, FakeApplication}
import scala.language.postfixOps

abstract class TestSpec extends FlatSpec with Matchers with TestConfiguration {

  implicit val fakeApp = FakeApplication(additionalConfiguration = configuration)

  protected override def runTests(testName: Option[String], args: Args): Status = {
    Helpers.running(fakeApp) {
      super.runTests(testName, args)
    }
  }
}
