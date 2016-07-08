package test

import org.scalatest._
import models.daos.chart.ChartDAOImpl
import models.Chart
import play.api.Play.current
import play.modules.reactivemongo._
import java.util.UUID
import scala.collection.mutable.ListBuffer
import scala.concurrent.{Future, Await}
import scala.concurrent.duration._
import play.api.test.{Helpers, FakeApplication}
import scala.language.postfixOps

class TestChartDAO extends TestSpec {

  lazy val reactiveMongoApi = current.injector.instanceOf[ReactiveMongoApi]

  lazy val cDAOi = new ChartDAOImpl(reactiveMongoApi.db)

  val chart = new Chart (UUID.fromString("123eb2cd-4213-4ffb-b5ca-1234daa02290"),
  "chart di prova",
  UUID.fromString("949eb2cd-4213-4ffb-b5ca-2592daa02290"),
  "prova")

  val chart1 = new Chart (UUID.fromString("123100cd-4213-4ffb-b5ca-1234daa02290"),
  "chart di prova1",
  UUID.fromString("100eb2cd-4213-4ffb-b5ca-2592daa02290"),
  "prova1")

  val chart2 = new Chart (UUID.fromString("123200cd-4213-4ffb-b5ca-1234daa02290"),
  "chart di prova2",
  UUID.fromString("949eb2cd-4213-4ffb-b5ca-2592daa02290"),
  "prova2")

  val chart3 = new Chart (UUID.fromString("123300cd-4213-4ffb-b5ca-1234daa02290"),
  "chart di prova3",
  UUID.fromString("3006d3ab-0d3a-455a-9473-6b45995d2d68"),
  "prova3")

  val chart4 = new Chart (UUID.fromString("123400cd-4213-4ffb-b5ca-1234daa02290"),
  "chart di prova4",
  UUID.fromString("4006d3ab-0d3a-455a-9473-6b45995d2d68"),
  "prova4")

  val chart5 = new Chart (UUID.fromString("123100cd-4213-4ffb-b5ca-1234daa02290"),
  "chart di prova",
  UUID.fromString("4006d3ab-0d3a-455a-9473-6b45995d2d68"),
  "prova5")

  val chart6 = new Chart (UUID.fromString("123600cd-4213-4ffb-b5ca-1234daa02290"),
  "chart di prova6",
  UUID.fromString("6006d3ab-0d3a-455a-9473-6b45995d2d68"),
  "prova5")

  val chart7 = new Chart (UUID.fromString("123700cd-4213-4ffb-b5ca-1234daa02290"),
  "chart di prova7",
  UUID.fromString("7006d3ab-0d3a-455a-9473-6b45995d2d68"),
  "prova5")

  val chart8 = new Chart (UUID.fromString("1238b2cd-4213-4ffb-b5ca-1234daa02290"),
  "chart di prova8",
  UUID.fromString("949eb2cd-4213-4ffb-b5ca-2592daa02290"),
  "prova")

  val chart9 = new Chart (UUID.fromString("1239b2cd-4213-4ffb-b5ca-1234daa02290"),
  "chart di prova9",
  UUID.fromString("949eb2cd-4213-4ffb-b5ca-2592daa02290"),
  "prova")

  "Save" should "return the chart that you want to save" in {
    cDAOi.save(chart6)
    cDAOi.save(chart7)
    cDAOi.save(chart8)
    cDAOi.save(chart9)
    val result = Await.result(cDAOi.save(chart),3 seconds)
    assert(chart.equals(result))
  }

  it should "save the chart in the db" in {
    val result = Await.result(cDAOi.save(chart4),3 seconds)
    val finded = Await.result(cDAOi.find(result.chartID),3 seconds).get
    assert(chart4.equals(finded))
  }

  "Find" should "return the chart with the specified ID" in {
    val result = Await.result(cDAOi.find(chart.chartID),3 seconds).get
    assert(chart.equals(result))
  }

  it should "return nothing if the chart doesn't exist" in {
    val result = cDAOi.find(UUID.fromString("00000000-0000-0000-0000-000000000000"))
    assert(result.value == None)
  }

  "FindAll" should "return a list of chart" in {
    val result = Await.result(cDAOi.findAll(),3 seconds)
    val list = List [Chart] (chart,chart1)
    assert(result.getClass.equals(list.getClass))
  }

  "FindByThingID" should "return a list of chart" in {
    val result = Await.result(cDAOi.findByThingID(chart.thingID),3 seconds)
    val list = List [Chart] (chart,chart1)
    assert(result.getClass.equals(list.getClass))
  }

  it should "return a list of chart with the specified thing ID" in {
    val result = Await.result(cDAOi.findByThingID(chart.thingID),3 seconds)
    var it = result.iterator
    var find = it.forall(_.thingID.equals(chart.thingID))
    assert(find.equals(true))
  }

  "Update" should "update the selected chart" in {
    cDAOi.save(chart1)
    cDAOi.update(chart1.chartID,chart5)
    val result = Await.result(cDAOi.find(chart5.chartID),3 seconds).get
    assert(chart5.equals(result))
  }

  "Remove" should "return a list of chart" in {
    val result = Await.result(cDAOi.remove(chart.chartID),3 seconds)
    val list = List [Chart] (chart,chart1)
    assert(result.getClass.equals(list.getClass))
  }

  it should "remove the chart with the specified ID" in {
    cDAOi.remove(chart6.chartID)
    val result = cDAOi.find(chart6.chartID)
    assert(result.value == None)
  }

  "RemoveByThing" should "return a list of chart" in {
    val result = Await.result(cDAOi.removeByThing(chart6.thingID),3 seconds)
    val list = List [Chart] (chart,chart1)
    assert(result.getClass.equals(list.getClass))
  }

  it should "remove all the chart if the the specified thing" in {
    cDAOi.removeByThing(chart7.thingID)
    val result = cDAOi.find(chart7.chartID)
    assert(result.value == None)
  }
}
