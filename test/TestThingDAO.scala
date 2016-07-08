package test

import org.scalatest._
import models.daos.thing.ThingDAOImpl
import models.Thing
import models.Measurements
import models.DetectionDouble
import play.api.Play.current
import play.modules.reactivemongo._
import java.util.UUID
import scala.collection.mutable.ListBuffer
import scala.concurrent.{Future, Await}
import scala.concurrent.duration._
import play.api.test.{Helpers, FakeApplication}
import scala.language.postfixOps
import java.util.Date

class TestThingDAO extends TestSpec {

  lazy val reactiveMongoApi = current.injector.instanceOf[ReactiveMongoApi]

  lazy val tDAOi = new ThingDAOImpl(reactiveMongoApi.db)

  val sens = new DetectionDouble ("sens",0.0)
  val sens1 = new DetectionDouble ("sens1",1.0)
  val sens2 = new DetectionDouble ("sens2",2.0)
  val sens3 = new DetectionDouble ("sens3",3.0)
  val sens4 = new DetectionDouble ("sens4",4.0)

  val measure = new Measurements (UUID.fromString("AAA0b2cd-4213-4ffb-b5ca-2592daa02290"),
  UUID.fromString("500eb2cd-4213-4ffb-b5ca-2592daa02290"),
  new Date(), List [DetectionDouble] (sens,sens1,sens2,sens3), 0)

  val measure1 = new Measurements (UUID.fromString("AAA1b2cd-4213-4ffb-b5ca-2592daa02290"),
  UUID.fromString("500eb2cd-4213-4ffb-b5ca-2592daa02290"),
  new Date(), List [DetectionDouble] (sens,sens1,sens2,sens3), 1)

  val measure2 = new Measurements (UUID.fromString("AAA2b2cd-4213-4ffb-b5ca-2592daa02290"),
  UUID.fromString("500eb2cd-4213-4ffb-b5ca-2592daa02290"),
  new Date(), List [DetectionDouble] (sens,sens1,sens2,sens3), 2)

  val measure3 = new Measurements (UUID.fromString("AAA3b2cd-4213-4ffb-b5ca-2592daa02290"),
  UUID.fromString("500eb2cd-4213-4ffb-b5ca-2592daa02290"),
  new Date(), List [DetectionDouble] (sens,sens1,sens2,sens3), 3)

  val measure4 = new Measurements (UUID.fromString("AAA4b2cd-4213-4ffb-b5ca-2592daa02290"),
  UUID.fromString("500eb2cd-4213-4ffb-b5ca-2592daa02290"),
  new Date(), List [DetectionDouble] (sens4,sens4,sens4,sens4), 4)

  val thing = new Thing (UUID.fromString("949eb2cd-4213-4ffb-b5ca-2592daa02290"),
  "prova", "sn", "oggetto di prova",
  UUID.fromString("9aa6d3ab-0d3a-455a-9473-6b45995d2d68"),
  UUID.fromString("e131a823-657b-47f4-a978-44344e93097b"),
  new ListBuffer)

  val thing1 = new Thing (UUID.fromString("100eb2cd-4213-4ffb-b5ca-2592daa02290"),
  "prova1", "sn1", "oggetto di prova1",
  UUID.fromString("1006d3ab-0d3a-455a-9473-6b45995d2d68"),
  UUID.fromString("1001a823-657b-47f4-a978-44344e93097b"),
  new ListBuffer)

  val thing2 = new Thing (UUID.fromString("100eb2cd-4213-4ffb-b5ca-2592daa02290"),
  "prova2", "sn2", "oggetto di prova2",
  UUID.fromString("2006d3ab-0d3a-455a-9473-6b45995d2d68"),
  UUID.fromString("2001a823-657b-47f4-a978-44344e93097b"),
  new ListBuffer)

  val thing3 = new Thing (UUID.fromString("300eb2cd-4213-4ffb-b5ca-2592daa02290"),
  "prova3", "sn3", "oggetto di prova3",
  UUID.fromString("3006d3ab-0d3a-455a-9473-6b45995d2d68"),
  UUID.fromString("3001a823-657b-47f4-a978-44344e93097b"),
  new ListBuffer)

  val thing4 = new Thing (UUID.fromString("400eb2cd-4213-4ffb-b5ca-2592daa02290"),
  "prova4", "sn4", "oggetto di prova4",
  UUID.fromString("4006d3ab-0d3a-455a-9473-6b45995d2d68"),
  UUID.fromString("4001a823-657b-47f4-a978-44344e93097b"),
  new ListBuffer)

  val thing5 = new Thing (UUID.fromString("500eb2cd-4213-4ffb-b5ca-2592daa02290"),
  "prova5", "sn5", "oggetto di prova5",
  UUID.fromString("5006d3ab-0d3a-455a-9473-6b45995d2d68"),
  UUID.fromString("5001a823-657b-47f4-a978-44344e93097b"),
  ListBuffer (measure, measure1, measure2, measure3))

  val thing6 = new Thing (UUID.fromString("600eb2cd-4213-4ffb-b5ca-2592daa02290"),
  "prova6", "sn6", "oggetto di prova6",
  UUID.fromString("6006d3ab-0d3a-455a-9473-6b45995d2d68"),
  UUID.fromString("6001a823-657b-47f4-a978-44344e93097b"),
  ListBuffer (measure, measure1, measure2, measure3))

  val thing7 = new Thing (UUID.fromString("700eb2cd-4213-4ffb-b5ca-2592daa02290"),
  "prova7", "sn7", "oggetto di prova7",
  UUID.fromString("7006d3ab-0d3a-455a-9473-6b45995d2d68"),
  UUID.fromString("6001a823-657b-47f4-a978-44344e93097b"),
  ListBuffer (measure, measure1, measure2, measure3))

  val thing8 = new Thing (UUID.fromString("800eb2cd-4213-4ffb-b5ca-2592daa02290"),
  "prova8", "sn8", "oggetto di prova8",
  UUID.fromString("7006d3ab-0d3a-455a-9473-6b45995d2d68"),
  UUID.fromString("6001a823-657b-47f4-a978-44344e93097b"),
  ListBuffer (measure, measure1, measure2, measure3))

  val thing9 = new Thing (UUID.fromString("900eb2cd-4213-4ffb-b5ca-2592daa02290"),
  "prova9", "sn9", "oggetto di prova9",
  UUID.fromString("7006d3ab-0d3a-455a-9473-6b45995d2d68"),
  UUID.fromString("6001a823-657b-47f4-a978-44344e93097b"),
  ListBuffer (measure, measure1, measure2, measure3))

  "Save" should "return the thing that you want to save" in {
    //tDAOi.save(thing5)
    tDAOi.addMeasurements(thing5.thingID,measure4)
    tDAOi.save(thing6)
    tDAOi.save(thing7)
    tDAOi.save(thing8)
    tDAOi.save(thing9)
    val result = Await.result(tDAOi.save(thing),3 seconds)
    assert(thing.equals(result))
  }

  it should "save the thing in the db" in {
    val result = Await.result(tDAOi.save(thing4),3 seconds)
    val finded = Await.result(tDAOi.findByName(result.name),3 seconds).get
    assert(thing4.equals(finded))
  }

  "FindByName" should "return the thing with the specified name" in {
    val result = Await.result(tDAOi.findByName(thing.name),3 seconds).get
    assert(thing.equals(result))
  }

  it should "return nothing if the thing doesn't exist" in {
    val result = tDAOi.findByName("null")
    assert(result.value == None)
  }

  "FindAll" should "return a list of thing" in {
    val result = Await.result(tDAOi.findAll(),3 seconds)
    val list = List [Thing] (thing,thing1)
    assert(result.getClass.equals(list.getClass))
  }

  "FindByCompanyID" should "return a list of thing" in {
    val result = Await.result(tDAOi.findByCompanyID(thing.companyID),3 seconds)
    val list = List [Thing] (thing,thing1)
    assert(result.getClass.equals(list.getClass))
  }

  it should "return a list of thing with the specified company ID" in {
    val result = Await.result(tDAOi.findByCompanyID(thing.companyID),3 seconds)
    var it = result.iterator
    var find = it.forall(_.companyID.equals(thing.companyID))
    assert(find.equals(true))
  }

  "FindByThingTypeID" should "return a list of thing" in {
    val result = Await.result(tDAOi.findByThingTypeID(thing.thingTypeID),3 seconds)
    val list = List [Thing] (thing,thing1)
    assert(result.getClass.equals(list.getClass))
  }

  it should "return a list of thing with the specified thing type ID" in {
    val result = Await.result(tDAOi.findByThingTypeID(thing.thingTypeID),3 seconds)
    var it = result.iterator
    var find = it.forall(_.thingTypeID.equals(thing.thingTypeID))
    assert(find.equals(true))
  }

  "FindByID" should "return the thing with the specified ID" in {
    val result = Await.result(tDAOi.findByID(thing.thingID),3 seconds).get
    assert(thing.equals(result))
  }

  it should "return nothing if the thing doesn't exist" in {
    val result = tDAOi.findByID(UUID.fromString("00000000-0000-0000-0000-000000000000"))
    assert(result.value == None)
  }

  "Find" should "return the thing with the specified Serial Number" in {
    val result = Await.result(tDAOi.find(thing.serialNumber),3 seconds).get
    assert(thing.equals(result))
  }

  it should "return nothing if the thing doesn't exist" in {
    val result = tDAOi.find("null")
    assert(result.value == None)
  }

  "FindMeasurements" should "return a list of measurements" in {
    val result = Await.result(tDAOi.findMeasurements(thing5.thingID),3 seconds)
    val list = List [Measurements] (measure, measure1)
    assert(result.getClass.equals(list.getClass))
  }

  it should "return a list of measurements with the specified thing ID" in {
    val result = Await.result(tDAOi.findMeasurements(thing5.thingID),3 seconds)
    var it = result.iterator
    var find = it.forall(_.thingID.equals(thing5.thingID))
    assert(find.equals(true))
  }

  "FindListLabel" should "return a list of double" in {
    val result = tDAOi.findListLabel(thing5)
    val list = List [Double] (0.0, 1.0)
    assert(result.getClass.equals(list.getClass))
  }

  it should "return a list of label of the measure of the specified object" in {
    val result = tDAOi.findListLabel(thing5)
    val measures = Await.result(tDAOi.findMeasurements(thing5.thingID),3 seconds)
    var itm = measures.iterator
    var it = result.iterator
    var find = true
    while (itm.hasNext && it.hasNext && find){
      if (!itm.next.label.equals(it.next))
        find = false
    }
    assert(find)

  }

  "FindListArray" should "return a list of array of double" in {
    val result = tDAOi.findListArray(thing5)
    val list = List [Array[Double]] (Array (0.0, 1.0), Array(2.0,3.0))
    assert(result.getClass.equals(list.getClass))
  }

  it should "return a list of array of the sensor of the measure of the specified object" in {
    val result = tDAOi.findListArray(thing5)
    var it = result.iterator
    var find = true
    while (it.hasNext && find){
      val array = it.next
      for (i <- 0 to array.length-1) {
        find = (array(i) == i)
      }
    }
    assert(find)
  }

  "Update" should "return the modified thing" in {
    tDAOi.save(thing2)
    val result = Await.result(tDAOi.update(thing2.thingID,thing3),3 seconds)
    assert(thing3.equals(result))
  }

  it should "update the selected thing" in {
    tDAOi.save(thing1)
    val result = Await.result(tDAOi.update(thing1.thingID,thing2),3 seconds)
    val finded = Await.result(tDAOi.findByName(result.name),3 seconds).get
    assert(thing2.equals(finded))
  }

  "AddMeasurements" should "add a measure to the specified thing and return the thing" in {
    val measures = Await.result(tDAOi.findMeasurements(thing5.thingID),3 seconds)
    var it = measures.iterator
    assert(it.exists(_.equals(measure4)))
  }

  "Remove" should "return a list of thing" in {
    val result = Await.result(tDAOi.remove(thing.thingID),3 seconds)
    val list = List [Thing] (thing,thing1)
    assert(result.getClass.equals(list.getClass))
  }

  it should "remove the thing with the specified ID" in {
    tDAOi.remove(thing6.thingID)
    val result = tDAOi.findByID(thing6.thingID)
    assert(result.value == None)
  }

  "RemoveByThingTypeID" should "return a list of thing" in {
    val result = Await.result(tDAOi.removeByThingTypeID(thing6.thingTypeID),3 seconds)
    val list = List [Thing] (thing,thing1)
    assert(result.getClass.equals(list.getClass))
  }

  it should "remove all the thing with the specified thing type ID" in {
    tDAOi.removeByThingTypeID(thing7.thingTypeID)
    val result = tDAOi.findByThingTypeID(thing7.thingTypeID)
    assert(result.value == None)
  }
}
