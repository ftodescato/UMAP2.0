package test

import org.scalatest._
import models.daos.company.CompanyDAOImpl
import models.Company
import play.api.Play.current
import play.modules.reactivemongo._
import java.util.UUID
import scala.collection.mutable.ListBuffer
import scala.concurrent.{Future, Await}
import scala.concurrent.duration._
import play.api.test.{Helpers, FakeApplication}
import scala.language.postfixOps
import java.util.Date

class TestCompanyDAO extends TestSpec {

  lazy val reactiveMongoApi = current.injector.instanceOf[ReactiveMongoApi]

  lazy val cDAOi = new CompanyDAOImpl(reactiveMongoApi.db)

  val company = new Company (UUID.fromString("e131a823-657b-47f4-a978-44344e93097b"),
  "companyProva", "via di prova", "città di prova", "CAP di prova",
  "partita IVA prova", "company di prova", new Date (0),
  new ListBuffer, new Some("nomeCompany"))

  val company1 = new Company (UUID.fromString("1001a823-657b-47f4-a978-44344e93097b"),
  "companyProva1", "via di prova1", "città di prova1", "CAP di prova1",
  "partita IVA prova1", "company di prova1", new Date (0),
  new ListBuffer, new Some("nomeCompany1"))

  val company2 = new Company (UUID.fromString("2001a823-657b-47f4-a978-44344e93097b"),
  "companyProva2", "via di prova2", "città di prova2", "CAP di prova2",
  "partita IVA prova2", "company di prova2", new Date (0),
  new ListBuffer, new Some("nomeCompany2"))

  val company3 = new Company (UUID.fromString("3001a823-657b-47f4-a978-44344e93097b"),
  "companyProva3", "via di prova3", "città di prova3", "CAP di prova3",
  "partita IVA prova3", "company di prova3", new Date (0),
  new ListBuffer, new Some("nomeCompany3"))

  val company4 = new Company (UUID.fromString("4001a823-657b-47f4-a978-44344e93097b"),
  "companyProva4", "via di prova4", "città di prova4", "CAP di prova4",
  "partita IVA prova4", "company di prova4", new Date (0),
  new ListBuffer, new Some("nomeCompany4"))

  val company5 = new Company (UUID.fromString("5001a823-657b-47f4-a978-44344e93097b"),
  "companyProva5", "via di prova5", "città di prova5", "CAP di prova5",
  "partita IVA prova5", "company di prova5", new Date (0),
  new ListBuffer, new Some("nomeCompany5"))

  val company6 = new Company (UUID.fromString("6001a823-657b-47f4-a978-44344e93097b"),
  "companyProva6", "via di prova6", "città di prova6", "CAP di prova6",
  "partita IVA prova6", "company di prova6", new Date (0),
  new ListBuffer, new Some("nomeCompany6"))

  "Save" should "return the company that you want to save" in {
    cDAOi.save(company5)
    cDAOi.save(company6)
    val result = Await.result(cDAOi.save(company),10 seconds)
    assert(company.equals(result))
  }

  it should "save the company in the db" in {
    val result = Await.result(cDAOi.save(company4),10 seconds)
    val finded = Await.result(cDAOi.findByName(result.companyName.get),10 seconds).get
    assert(company4.equals(finded))
  }

  "FindByName" should "return the company with the specified name" in {
    val result = Await.result(cDAOi.findByName(company.companyName.get),10 seconds).get
    assert(company.equals(result))
  }

  it should "return nothing if the company doesn't exist" in {
    val result = cDAOi.findByName("null")
    assert(result.value == None)
  }

  "FindAll" should "return a list of company" in {
    val result = Await.result(cDAOi.findAll(),3 seconds)
    val list = List [Company] (company,company1)
    assert(result.getClass.equals(list.getClass))
  }

  "FindByID" should "return the company with the specified ID" in {
    val result = Await.result(cDAOi.findByID(company.companyID),10 seconds).get
    assert(company.equals(result))
  }

  it should "return nothing if the company doesn't exist" in {
    val result = cDAOi.findByID(UUID.fromString("00000000-0000-0000-0000-000000000000"))
    assert(result.value == None)
  }

  "Update" should "return the modified company" in {
    cDAOi.save(company2)
    val result = Await.result(cDAOi.update(company2.companyID,company3),10 seconds)
    assert(company3.equals(result))
  }

  it should "update the selected company" in {
    cDAOi.save(company1)
    val result = Await.result(cDAOi.update(company1.companyID,company2),10 seconds)
    val finded = Await.result(cDAOi.findByName(result.companyName.get),10 seconds).get
    assert(company2.equals(finded))
  }

  "Remove" should "return a list of company" in {
    val result = Await.result(cDAOi.remove(company5.companyID),3 seconds)
    val list = List [Company] (company,company1)
    assert(result.getClass.equals(list.getClass))
  }

  it should "remove the company with the specified ID" in {
    cDAOi.remove(company6.companyID)
    val result = cDAOi.findByID(company6.companyID)
    assert(result.value == None)
  }
}
