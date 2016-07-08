package test

import org.scalatest._
import models.daos.thingType.ThingTypeDAOImpl
import models.Thing
import models.ThingType
import models.DataDouble
import play.api.Play.current
import play.modules.reactivemongo._
import java.util.UUID
import scala.collection.mutable.ListBuffer
import scala.concurrent.{Future, Await}
import scala.concurrent.duration._
import play.api.test.{Helpers, FakeApplication}
import scala.language.postfixOps

class TestThingTypeDAO extends TestSpec {

  lazy val reactiveMongoApi = current.injector.instanceOf[ReactiveMongoApi]

  lazy val ttDAOi = new ThingTypeDAOImpl(reactiveMongoApi.db)

  val thingT = new ThingType (UUID.fromString("9aa6d3ab-0d3a-455a-9473-6b45995d2d68"),
  "tipoProva",
  ListBuffer (UUID.fromString("6001a823-657b-47f4-a978-44344e93097b"),
              UUID.fromString("5001a823-657b-47f4-a978-44344e93097b"),
              UUID.fromString("4001a823-657b-47f4-a978-44344e93097b")),
  new DataDouble(true, new ListBuffer))

  val thingT1 = new ThingType (UUID.fromString("1006d3ab-0d3a-455a-9473-6b45995d2d68"),
  "tipoProva",
  ListBuffer (UUID.fromString("6001a823-657b-47f4-a978-44344e93097b"),
              UUID.fromString("5001a823-657b-47f4-a978-44344e93097b")),
  new DataDouble(true, new ListBuffer))

  val thingT2 = new ThingType (UUID.fromString("2006d3ab-0d3a-455a-9473-6b45995d2d68"),
  "tipoProva",
  ListBuffer (UUID.fromString("6001a823-657b-47f4-a978-44344e93097b"),
              UUID.fromString("4001a823-657b-47f4-a978-44344e93097b")),
  new DataDouble(true, new ListBuffer))

  val thingT3 = new ThingType (UUID.fromString("3006d3ab-0d3a-455a-9473-6b45995d2d68"),
  "tipoProva",
  ListBuffer (UUID.fromString("4001a823-657b-47f4-a978-44344e93097b")),
  new DataDouble(true, new ListBuffer))

  val thingT4 = new ThingType (UUID.fromString("4006d3ab-0d3a-455a-9473-6b45995d2d68"),
  "tipoProva", new ListBuffer, new DataDouble(true, new ListBuffer))

  val thingT5 = new ThingType (UUID.fromString("5006d3ab-0d3a-455a-9473-6b45995d2d68"),
  "tipoProva", new ListBuffer, new DataDouble(true, new ListBuffer))

  "Save" should "return the thing type that you want to save" in {
    ttDAOi.save(thingT5)
    val result = Await.result(ttDAOi.save(thingT),3 seconds)
    assert(thingT.equals(result))
  }

  it should "save the thing type in the db" in {
    val result = Await.result(ttDAOi.save(thingT4),3 seconds)
    val finded = Await.result(ttDAOi.findByID(result.thingTypeID),3 seconds).get
    assert(thingT4.equals(finded))
  }

  "FindAll" should "return a list of thingType" in {
    val result = Await.result(ttDAOi.findAll(),3 seconds)
    val list = List [ThingType] (thingT,thingT1)
    assert(result.getClass.equals(list.getClass))
  }

  "FindByCompanyID" should "return a list of thing type" in {
    val result = Await.result(ttDAOi.findByCompanyID(UUID.fromString("6001a823-657b-47f4-a978-44344e93097b")),3 seconds)
    val list = List [ThingType] (thingT,thingT1)
    assert(result.getClass.equals(list.getClass))
  }

  it should "return a list of thing type with the specified company ID" in {
    val result = Await.result(ttDAOi.findByCompanyID(UUID.fromString("6001a823-657b-47f4-a978-44344e93097b")),3 seconds)
    var it = result.iterator
    var find = it.forall(_.companyID.iterator.exists(_.equals(UUID.fromString("6001a823-657b-47f4-a978-44344e93097b"))))
    assert(find.equals(true))
  }

  "FindByID" should "return the thing type with the specified ID" in {
    val result = Await.result(ttDAOi.findByID(thingT.thingTypeID),3 seconds).get
    assert(thingT.equals(result))
  }

  it should "return nothing if the thing type doesn't exist" in {
    val result = ttDAOi.findByID(UUID.fromString("00000000-0000-0000-0000-000000000000"))
    assert(result.value == None)
  }

  "Update" should "return the modified thing type" in {
    ttDAOi.save(thingT2)
    val result = Await.result(ttDAOi.update(thingT2.thingTypeID,thingT3),3 seconds)
    assert(thingT3.equals(result))
  }

  it should "update the selected thing type" in {
    ttDAOi.save(thingT1)
    val result = Await.result(ttDAOi.update(thingT1.thingTypeID,thingT2),3 seconds)
    val finded = Await.result(ttDAOi.findByID(result.thingTypeID),3 seconds).get
    assert(thingT2.equals(finded))
  }

  "Remove" should "return a list of thing type" in {
    val result = Await.result(ttDAOi.remove(thingT.thingTypeID),3 seconds)
    val list = List [ThingType] (thingT,thingT1)
    assert(result.getClass.equals(list.getClass))
  }

  it should "remove the thing type with the specified ID" in {
    ttDAOi.remove(thingT5.thingTypeID)
    val result = ttDAOi.findByID(thingT5.thingTypeID)
    assert(result.value == None)
  }



}
