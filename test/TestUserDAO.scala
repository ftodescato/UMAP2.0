package test

import org.scalatest._
import models.daos.user.UserDAOImpl
import models.User
import com.mohiva.play.silhouette.api.{ Identity, LoginInfo }
import play.api.Play.current
import play.modules.reactivemongo._
import java.util.UUID
import scala.collection.mutable.ListBuffer
import scala.concurrent.{Future, Await}
import scala.concurrent.duration._
import play.api.test.{Helpers, FakeApplication}
import scala.language.postfixOps

class TestUserDAO extends TestSpec {

  lazy val reactiveMongoApi = current.injector.instanceOf[ReactiveMongoApi]

  lazy val uDAOi = new UserDAOImpl(reactiveMongoApi.db)

  val user = new User (UUID.fromString("999eb2cd-1234-4ffb-b5ca-2592daa02290"),
  "nome","cognome",new LoginInfo("provID","provKey"), "email",
  UUID.fromString("e131a823-657b-47f4-a978-44344e93097b"),
  false, "token", "ruolo", "secretString")

  val user1 = new User (UUID.fromString("111eb2cd-1234-4ffb-b5ca-2592daa02290"),
  "nome1","cognome1",new LoginInfo("provID1","provKey1"), "email1",
  UUID.fromString("1001a823-657b-47f4-a978-44344e93097b"),
  true, "token1", "ruolo1", "secretString1")

  val user2 = new User (UUID.fromString("222eb2cd-1234-4ffb-b5ca-2592daa02290"),
  "nome2","cognome2",new LoginInfo("provID2","provKey2"), "email2",
  UUID.fromString("2001a823-657b-47f4-a978-44344e93097b"),
  true, "token2", "ruolo2", "secretString2")

  val user3 = new User (UUID.fromString("333eb2cd-1234-4ffb-b5ca-2592daa02290"),
  "nome3","cognome3",new LoginInfo("provID3","provKey3"), "email3",
  UUID.fromString("3001a823-657b-47f4-a978-44344e93097b"),
  true, "token3", "ruolo3", "secretString3")

  val user4 = new User (UUID.fromString("444eb2cd-1234-4ffb-b5ca-2592daa02290"),
  "nome4","cognome4",new LoginInfo("provID4","provKey4"), "email4",
  UUID.fromString("4001a823-657b-47f4-a978-44344e93097b"),
  true, "token4", "ruolo4", "secretString4")

  val user5 = new User (UUID.fromString("500eb2cd-1234-4ffb-b5ca-2592daa02290"),
  "nome","cognome5",new LoginInfo("provID5","provKey5"), "email5",
  UUID.fromString("e131a823-657b-47f4-a978-44344e93097b"),
  false, "token", "ruolo", "secretString")

  val user6 = new User (UUID.fromString("600eb2cd-1234-4ffb-b5ca-2592daa02290"),
  "nome6","cognome",new LoginInfo("provID6","provKey6"), "email6",
  UUID.fromString("6001a823-657b-47f4-a978-44344e93097b"),
  false, "token", "ruolo", "secretString")

  val user7 = new User (UUID.fromString("600eb2cd-1234-4ffb-b5ca-2592daa02290"),
  "nome6","cognome",new LoginInfo("provID7","provKey7"), "email6",
  UUID.fromString("7001a823-657b-47f4-a978-44344e93097b"),
  false, "token", "ruolo", "secretString")

  val user8 = new User (UUID.fromString("600eb2cd-1234-4ffb-b5ca-2592daa02290"),
  "nome6","cognome",new LoginInfo("provID8","provKey8"), "email6",
  UUID.fromString("7001a823-657b-47f4-a978-44344e93097b"),
  false, "token", "ruolo", "secretString")

  val user9 = new User (UUID.fromString("600eb2cd-1234-4ffb-b5ca-2592daa02290"),
  "nome6","cognome",new LoginInfo("provID9","provKey9"), "email6",
  UUID.fromString("7001a823-657b-47f4-a978-44344e93097b"),
  false, "token", "ruolo", "secretString")

  "Save" should "return the user that you want to save" in {
    uDAOi.save(user5)
    uDAOi.save(user6)
    uDAOi.save(user7)
    uDAOi.save(user8)
    uDAOi.save(user9)
    val result = Await.result(uDAOi.save(user),3 seconds)
    assert(user.equals(result))
  }

  it should "save the user in the db" in {
    val result = Await.result(uDAOi.save(user4),3 seconds)
    val finded = Await.result(uDAOi.findByEmail(result.email),3 seconds).get
    assert(user4.equals(finded))
  }

  "FindSecretString" should "return the user secret string" in {
    val result = Await.result(uDAOi.findSecretString(user),3 seconds)
    assert(user.secretString.equals(result))
  }

  "FindByEmail" should "return the user with the specified email" in {
    val result = Await.result(uDAOi.findByEmail(user.email),3 seconds).get
    assert(user.equals(result))
  }

  it should "return nothing if the user doesn't exist" in {
    val result = uDAOi.findByEmail("null")
    assert(result.value == None)
  }

  "FindAll" should "return a list of user" in {
    val result = Await.result(uDAOi.findAll(),3 seconds)
    val list = List [User] (user, user1)
    assert(result.getClass.equals(list.getClass))
  }

  "FindByIDCompany" should "return a list of user" in {
    val result = Await.result(uDAOi.findByIDCompany(user.company),3 seconds)
    val list = List [User] (user,user1)
    assert(result.getClass.equals(list.getClass))
  }

  it should "return a list of user with the specified company ID" in {
    val result = Await.result(uDAOi.findByIDCompany(user.company),3 seconds)
    var it = result.iterator
    var find = it.forall(_.company.equals(user.company))
    assert(find.equals(true))
  }

  "FindByName" should "return a list of user" in {
    val result = Await.result(uDAOi.findByName(user.name),3 seconds)
    val list = List [User] (user,user1)
    assert(result.getClass.equals(list.getClass))
  }

  it should "return a list of user with the specified name" in {
    val result = Await.result(uDAOi.findByName(user.name),3 seconds)
    var it = result.iterator
    var find = it.forall(_.name.equals(user.name))
    assert(find.equals(true))
  }

  "FindBySurname" should "return a list of user" in {
    val result = Await.result(uDAOi.findBySurname(user.surname),3 seconds)
    val list = List [User] (user,user1)
    assert(result.getClass.equals(list.getClass))
  }

  it should "return a list of user with the specified surname" in {
    val result = Await.result(uDAOi.findBySurname(user.surname),3 seconds)
    var it = result.iterator
    var find = it.forall(_.surname.equals(user.surname))
    assert(find.equals(true))
  }

  "FindByID" should "return the user with the specified ID" in {
    val result = Await.result(uDAOi.findByID(user.userID),3 seconds).get
    assert(user.equals(result))
  }

  it should "return nothing if the user doesn't exist" in {
    val result = uDAOi.findByID(UUID.fromString("00000000-0000-0000-0000-000000000000"))
    assert(result.value == None)
  }

  "Find" should "return the user with the specified LoginInfo" in {
    val result = Await.result(uDAOi.find(user.loginInfo),3 seconds).get
    assert(user.equals(result))
  }

  it should "return nothing if the user doesn't exist" in {
    val result = uDAOi.find(new LoginInfo("null", "null"))
    assert(result.value == None)
  }

  "Update" should "return the modified user" in {
    uDAOi.save(user2)
    val result = Await.result(uDAOi.update(user2.userID,user3),3 seconds)
    assert(user3.equals(result))
  }

  it should "update the selected user" in {
    uDAOi.save(user1)
    val result = Await.result(uDAOi.update(user1.userID,user2),3 seconds)
    val finded = Await.result(uDAOi.findByEmail(result.email),3 seconds).get
    assert(user2.equals(finded))
  }

  "ConfirmedMail" should "set the mailConfirmed value to true for the specified user" in {
    val result = Await.result(uDAOi.confirmedMail(user),3 seconds)
    assert(user.mailConfirmed.equals(false))
  }

  "Remove" should "return a list of user" in {
    val result = Await.result(uDAOi.remove(user.userID),3 seconds)
    val list = List [User] (user,user1)
    assert(result.getClass.equals(list.getClass))
  }

  it should "remove the user with the specified ID" in {
    uDAOi.remove(user5.userID)
    val result = uDAOi.findByID(user5.userID)
    assert(result.value == None)
  }

  "RemoveByEmail" should "return a list of user" in {
    val result = Await.result(uDAOi.removeByEmail(user1.email),3 seconds)
    val list = List [User] (user,user1)
    assert(result.getClass.equals(list.getClass))
  }

  it should "remove the user with the specified Email" in {
    uDAOi.removeByEmail(user6.email)
    val result = uDAOi.findByEmail(user6.email)
    assert(result.value == None)
  }

  "RemoveByCompany" should "return a list of user" in {
    val result = Await.result(uDAOi.removeByCompany(user6.company),3 seconds)
    val list = List [User] (user, user1)
    assert(result.getClass.equals(list.getClass))
  }

  it should "remove all the user if the specified company" in {
    uDAOi.removeByCompany(user7.company)
    val result = uDAOi.findByIDCompany(user7.company)
    assert(result.value == None)
  }
}
