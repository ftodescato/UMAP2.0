package test

import org.scalatest._
import models.daos.function.FunctionDAOImpl
import models.Function
import play.api.Play.current
import play.modules.reactivemongo._
import java.util.UUID
import scala.collection.mutable.ListBuffer
import scala.concurrent.{Future, Await}
import scala.concurrent.duration._
import play.api.test.{Helpers, FakeApplication}
import scala.language.postfixOps

class TestFunctionDAO extends TestSpec {

  lazy val reactiveMongoApi = current.injector.instanceOf[ReactiveMongoApi]

  lazy val fDAOi = new FunctionDAOImpl(reactiveMongoApi.db)

  val function = new Function(UUID.fromString("000eb2cd-4213-4ffb-b5ca-1234daa02290"),
  "Funzione")
  val function1 = new Function(UUID.fromString("111eb2cd-4213-4ffb-b5ca-1234daa02290"),
  "Funzione1")
  val function2 = new Function(UUID.fromString("222eb2cd-4213-4ffb-b5ca-1234daa02290"),
  "Funzione2")
  val function3 = new Function(UUID.fromString("333eb2cd-4213-4ffb-b5ca-1234daa02290"),
  "Funzione3")
  val function4 = new Function(UUID.fromString("444eb2cd-4213-4ffb-b5ca-1234daa02290"),
  "Funzione4")
  val function5 = new Function(UUID.fromString("555eb2cd-4213-4ffb-b5ca-1234daa02290"),
  "Funzione5")
  val function6 = new Function(UUID.fromString("666eb2cd-4213-4ffb-b5ca-1234daa02290"),
  "Funzione6")


  "Save" should "return the function that you want to save" in {
    fDAOi.save(function5)
    fDAOi.save(function6)
    val result = Await.result(fDAOi.save(function),3 seconds)
    assert(function.equals(result))
  }

  it should "save the function in the db" in {
    val result = Await.result(fDAOi.save(function4),3 seconds)
    val finded = Await.result(fDAOi.find(result.name),3 seconds).get
    assert(function4.equals(finded))
  }

  "Find" should "return the function with the specified name" in {
    val result = Await.result(fDAOi.find(function.name),3 seconds).get
    assert(function.equals(result))
  }

  it should "return nothing if the function doesn't exist" in {
    val result = fDAOi.find("null")
    assert(result.value == None)
  }

  "FindAll" should "return a list of function" in {
    val result = Await.result(fDAOi.findAll(),3 seconds)
    val list = ListBuffer [Function] (function,function1)
    assert(result.getClass.equals(list.getClass))
  }

  "Update" should "return the modified function" in {
    fDAOi.save(function2)
    val result = Await.result(fDAOi.update(function2.name,function3),3 seconds)
    assert(function3.equals(result))
  }

  it should "update the selected function" in {
    fDAOi.save(function1)
    val result = Await.result(fDAOi.update(function.name,function2),3 seconds)
    val finded = Await.result(fDAOi.find(result.name),3 seconds).get
    assert(function2.equals(finded))
  }

  "Remove" should "return a list of function" in {
    val result = Await.result(fDAOi.remove(function5.name),3 seconds)
    val list = List [Function] (function,function1)
    assert(result.getClass.equals(list.getClass))
  }

  it should "remove the thing with the specified ID" in {
    fDAOi.remove(function6.name)
    val result = fDAOi.find(function6.name)
    assert(result.value == None)
  }
}
