package test

import org.scalatest._
import models.daos.password.PasswordInfoDAO
import models.PersistentPasswordInfo
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import play.api.Play.current
import play.modules.reactivemongo._
import java.util.UUID
import scala.collection.mutable.ListBuffer
import scala.concurrent.{Future, Await}
import scala.concurrent.duration._
import play.api.test.{Helpers, FakeApplication}
import scala.language.postfixOps

class TestPasswordInfoDAO extends TestSpec {

  lazy val reactiveMongoApi = current.injector.instanceOf[ReactiveMongoApi]

  lazy val piDAO = new PasswordInfoDAO(reactiveMongoApi.db)

  val password = new PersistentPasswordInfo(new LoginInfo("provID","provKey"),
  new PasswordInfo("provaHash","password",Some("test")))

  val password1 = new PersistentPasswordInfo(new LoginInfo("provID1","provKey1"),
  new PasswordInfo("provaHash1","password1",Some("test")))

  val password2 = new PersistentPasswordInfo(new LoginInfo("provID2","provKey2"),
  new PasswordInfo("provaHash2","password2",Some("test")))

  val password3 = new PersistentPasswordInfo(new LoginInfo("provID3","provKey3"),
  new PasswordInfo("provaHash3","password3",Some("test")))

  val password4 = new PersistentPasswordInfo(new LoginInfo("provID4","provKey4"),
  new PasswordInfo("provaHash4","password4",Some("test")))

  val password5 = new PersistentPasswordInfo(new LoginInfo("provID5","provKey5"),
  new PasswordInfo("provaHash5","password5",Some("test")))

  val password6 = new PersistentPasswordInfo(new LoginInfo("provID6","provKey6"),
  new PasswordInfo("provaHash6","password6",Some("test")))

  val password7 = new PersistentPasswordInfo(new LoginInfo("provID7","provKey7"),
  new PasswordInfo("provaHash7","password7",Some("test")))

  val password8 = new PersistentPasswordInfo(new LoginInfo("provID8","provKey8"),
  new PasswordInfo("provaHash8","password8",Some("test")))

  val password9 = new PersistentPasswordInfo(new LoginInfo("provID9","provKey9"),
  new PasswordInfo("provaHash9","password9",Some("test")))

  val password10 = new PersistentPasswordInfo(new LoginInfo("provID10","provKey10"),
  new PasswordInfo("provaHash10","password10",Some("test")))

  val password11 = new PersistentPasswordInfo(new LoginInfo("provID11","provKey11"),
  new PasswordInfo("provaHash11","password11",Some("test")))

  val password12 = new PersistentPasswordInfo(new LoginInfo("provID12","provKey12"),
  new PasswordInfo("provaHash12","password12",Some("test")))

  "Save" should "return the password that you want to save" in {
    piDAO.save(password1.loginInfo, password1.authInfo)
    //piDAO.save(password2.loginInfo, password2.authInfo)
    piDAO.update(password2.loginInfo,password3.authInfo)
    piDAO.save(password12.loginInfo, password12.authInfo)
    val result = Await.result(piDAO.save(password.loginInfo,password.authInfo),3 seconds)
    assert(password.authInfo.equals(result))
  }

  it should "save the password in the db" in {
    piDAO.save(password4.loginInfo,password4.authInfo)
    val finded = Await.result(piDAO.find(password4.loginInfo),3 seconds).get
    assert(password4.authInfo.equals(finded))
  }

  "Find" should "return the password with the specified LoginInfo" in {
    val result = Await.result(piDAO.find(password.loginInfo),3 seconds).get
    assert(password.authInfo.equals(result))
  }

  it should "return nothing if the password doesn't exist" in {
    val result = Await.result(piDAO.find(new LoginInfo("null", "null")),3 seconds)
    assert(result == None)
  }

  "Update" should "return the modified password" in {
    val result = Await.result(piDAO.update(password2.loginInfo,password3.authInfo),3 seconds)
    assert(password3.authInfo.equals(result))
  }

  it should "update the authInfo of the selected password" in {
    val finded = Await.result(piDAO.find(password2.loginInfo),3 seconds).get
    // assert(password3.authInfo.hasher.equals(result.hasher) &&
    //        password3.authInfo.password.equals(result.password))
    assert(password3.authInfo.equals(finded))
  }

  "UpdateNewLoginInfo" should "return the modified password" in {
    piDAO.save(password7.loginInfo, password7.authInfo)
    val result = Await.result(piDAO.updateNewLoginInfo(password7.loginInfo,password8.loginInfo,password8.authInfo),3 seconds)
    assert(password8.authInfo.equals(result))
  }

  it should "update the selected password" in {
    piDAO.save(password9.loginInfo, password9.authInfo)
    piDAO.updateNewLoginInfo(password9.loginInfo,password10.loginInfo,password10.authInfo)
    val finded = Await.result(piDAO.find(password10.loginInfo),3 seconds).get
    assert(password10.authInfo.equals(finded))
  }

  "Add" should "return the added password" in {
    val result = Await.result(piDAO.add(password5.loginInfo,password5.authInfo),3 seconds)
    assert(password5.authInfo.equals(result))
  }

  it should "add the password in the db" in {
    piDAO.add(password6.loginInfo,password6.authInfo)
    val finded = Await.result(piDAO.find(password6.loginInfo),3 seconds).get
    assert(password6.authInfo.equals(finded))
  }

  "Remove" should "remove the password with the specified loginInfo" in {
    piDAO.remove(password12.loginInfo)
    val result = piDAO.find(password12.loginInfo)
    assert(result.value == None)
  }

}
