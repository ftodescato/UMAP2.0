package test

import org.scalatest._
import models.daos.modelLogReg.ModelLogRegDAOImpl
import models.LogRegModel
import play.api.Play.current
import play.modules.reactivemongo._
import java.util.UUID
import scala.collection.mutable.ListBuffer
import scala.concurrent.{Future, Await}
import scala.concurrent.duration._
import play.api.test.{Helpers, FakeApplication}
import scala.language.postfixOps

class TestModelLogRegDAO extends TestSpec {

  lazy val reactiveMongoApi = current.injector.instanceOf[ReactiveMongoApi]

  lazy val mlrDAOi = new ModelLogRegDAOImpl(reactiveMongoApi.db)

  val model = new LogRegModel (UUID.fromString("949eb2cd-4213-4ffb-AAAA-2592daa02290"),
  UUID.fromString("949eb2cd-4213-4ffb-b5ca-2592daa02290"),
  10,10,10, Array (1.0,2.0,3.0,4.0))

  val model1 = new LogRegModel (UUID.fromString("100eb2cd-4213-4ffb-AAAA-2592daa02290"),
  UUID.fromString("100eb2cd-4213-4ffb-b5ca-2592daa02290"),
  11,11,11, Array (1.0,2.0,3.0,4.0))

  val model2 = new LogRegModel (UUID.fromString("200eb2cd-4213-4ffb-AAAA-2592daa02290"),
  UUID.fromString("200eb2cd-4213-4ffb-b5ca-2592daa02290"),
  12,12,12, Array (1.0,2.0,3.0,4.0))

  val model3 = new LogRegModel (UUID.fromString("300eb2cd-4213-4ffb-AAAA-2592daa02290"),
  UUID.fromString("300eb2cd-4213-4ffb-b5ca-2592daa02290"),
  13,13,13, Array (1.0,2.0,3.0,4.0))

  val model4 = new LogRegModel (UUID.fromString("400eb2cd-4213-4ffb-AAAA-2592daa02290"),
  UUID.fromString("400eb2cd-4213-4ffb-b5ca-2592daa02290"),
  14,14,14, Array (1.0,2.0,3.0,4.0))

  val model5 = new LogRegModel (UUID.fromString("500eb2cd-4213-4ffb-AAAA-2592daa02290"),
  UUID.fromString("500eb2cd-4213-4ffb-b5ca-2592daa02290"),
  15,15,15, Array (1.0,2.0,3.0,4.0))

  val model6 = new LogRegModel (UUID.fromString("600eb2cd-4213-4ffb-AAAA-2592daa02290"),
  UUID.fromString("600eb2cd-4213-4ffb-b5ca-2592daa02290"),
  15,15,15, Array (1.0,2.0,3.0,4.0))

  "Save" should "return the model that you want to save" in {
    mlrDAOi.save(model5)
    mlrDAOi.save(model6)
    val result = Await.result(mlrDAOi.save(model),3 seconds)
    assert(model.equals(result))
  }

  it should "save the model in the db" in {
    val result = Await.result(mlrDAOi.save(model4),3 seconds)
    val finded = Await.result(mlrDAOi.findByThingID(result.thingID),3 seconds).get
    assert(model4.logRegModelID.equals(finded.logRegModelID) &&
           model4.thingID.equals(finded.thingID) &&
           model4.intercept.equals(finded.intercept) &&
           model4.numFeatures.equals(finded.numFeatures) &&
           model4.numClasses.equals(finded.numClasses))
  }

  "FindByThingID" should "return the model with the specified ID" in {
    val result = Await.result(mlrDAOi.findByThingID(model.thingID),3 seconds).get
    assert(model.logRegModelID.equals(result.logRegModelID) &&
           model.thingID.equals(result.thingID) &&
           model.intercept.equals(result.intercept) &&
           model.numFeatures.equals(result.numFeatures) &&
           model.numClasses.equals(result.numClasses))
  }

  it should "return nothing if the model doesn't exist" in {
    val result = mlrDAOi.findByThingID(UUID.fromString("00000000-0000-0000-0000-000000000000"))
    assert(result.value == None)
  }

  "Update" should "update the selected model" in {
    mlrDAOi.save(model1)
    mlrDAOi.update(model1.logRegModelID,model2)
    val finded = Await.result(mlrDAOi.findByThingID(model2.thingID),3 seconds).get
    assert(model2.logRegModelID.equals(finded.logRegModelID) &&
           model2.thingID.equals(finded.thingID) &&
           model2.intercept.equals(finded.intercept) &&
           model2.numFeatures.equals(finded.numFeatures) &&
           model2.numClasses.equals(finded.numClasses))
  }

  "Remove" should "return a list of model" in {
    val result = Await.result(mlrDAOi.remove(model6.logRegModelID),3 seconds)
    val list = List [LogRegModel] (model,model1)
    assert(result.getClass.equals(list.getClass))
  }

  it should "remove the model with the specified ID" in {
    mlrDAOi.remove(model5.logRegModelID)
    val result = mlrDAOi.findByThingID(model5.thingID)
    assert(result.value == None)
  }
}
