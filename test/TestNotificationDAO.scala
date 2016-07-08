package test

import org.scalatest._
import models.daos.notification.NotificationDAOImpl
import models.Notification
import com.mohiva.play.silhouette.api.{ Identity, LoginInfo }
import play.api.Play.current
import play.modules.reactivemongo._
import java.util.UUID
import scala.collection.mutable.ListBuffer
import scala.concurrent.{Future, Await}
import scala.concurrent.duration._
import play.api.test.{Helpers, FakeApplication}
import scala.language.postfixOps

class TestNotificationDAO extends TestSpec {

  lazy val reactiveMongoApi = current.injector.instanceOf[ReactiveMongoApi]

  lazy val nDAOi = new NotificationDAOImpl(reactiveMongoApi.db)

  val notification = new Notification (UUID.fromString("999eb2cd-1234-4ffb-1234-2592daa02290"),
  "notifica","email","tipo",None,
  new Some(UUID.fromString("949eb2cd-4213-4ffb-b5ca-2592daa02290")),
  50,100,true)

  val notification1 = new Notification (UUID.fromString("111eb2cd-1234-4ffb-1234-2592daa02290"),
  "notifica1","email1","tipo1",None,
  new Some(UUID.fromString("100eb2cd-4213-4ffb-b5ca-2592daa02290")),
  50,100,true)

  val notification2 = new Notification (UUID.fromString("222eb2cd-1234-4ffb-1234-2592daa02290"),
  "notifica2","email2","tipo2",None,
  new Some(UUID.fromString("200eb2cd-4213-4ffb-b5ca-2592daa02290")),
  50,100,true)

  val notification3 = new Notification (UUID.fromString("333eb2cd-1234-4ffb-1234-2592daa02290"),
  "notifica3","email3","tipo3",
  new Some(UUID.fromString("3006d3ab-0d3a-455a-9473-6b45995d2d68")),
  None,50,100,true)

  val notification4 = new Notification (UUID.fromString("444eb2cd-1234-4ffb-1234-2592daa02290"),
  "notifica4","email4","tipo4",
  new Some(UUID.fromString("4006d3ab-0d3a-455a-9473-6b45995d2d68")),
  None,50,100,false)

  val notification5 = new Notification (UUID.fromString("555eb2cd-1234-4ffb-1234-2592daa02290"),
  "notifica5","email5","tipo5",
  new Some(UUID.fromString("5006d3ab-0d3a-455a-9473-6b45995d2d68")),
  None,50,100,false)

  val notification6 = new Notification (UUID.fromString("666eb2cd-1234-4ffb-1234-2592daa02290"),
  "notifica6","email6","tipo6",
  new Some(UUID.fromString("6006d3ab-0d3a-455a-9473-6b45995d2d68")),
  None,50,100,false)

  val notification7 = new Notification (UUID.fromString("777eb2cd-1234-4ffb-1234-2592daa02290"),
  "notifica7","email7","tipo7",
  new Some(UUID.fromString("6006d3ab-0d3a-455a-9473-6b45995d2d68")),
  None,50,100,false)

  val notification8 = new Notification (UUID.fromString("888eb2cd-1234-4ffb-1234-2592daa02290"),
  "notifica8","email8","tipo8",
  new Some(UUID.fromString("6006d3ab-0d3a-455a-9473-6b45995d2d68")),
  None,50,100,false)

  val notification9 = new Notification (UUID.fromString("999eb2cd-1234-4ffb-1234-2592daa02290"),
  "notifica9","email9","tipo9",None,
  new Some(UUID.fromString("6006d3ab-0d3a-455a-9473-6b45995d2d68")),
  50,100,false)

  val notification10 = new Notification (UUID.fromString("0000b2cd-1234-4ffb-1234-2592daa02290"),
  "notifica10","email10","tipo10",None,
  new Some(UUID.fromString("6006d3ab-0d3a-455a-9473-6b45995d2d68")),
  50,100,false)

  val notification11 = new Notification (UUID.fromString("1111b2cd-1234-4ffb-1234-2592daa02290"),
  "notifica11","email11","tipo11",None,
  new Some(UUID.fromString("6006d3ab-0d3a-455a-9473-6b45995d2d68")),
  50,100,false)

  val notification12 = new Notification (UUID.fromString("2222b2cd-1234-4ffb-1234-2592daa02290"),
  "notifica12","email12","tipo12",None,
  new Some(UUID.fromString("2222d3ab-0d3a-455a-9473-6b45995d2d68")),
  50,100,false)

  "Save" should "return the notification that you want to save" in {
    nDAOi.save(notification5)
    nDAOi.save(notification6)
    nDAOi.save(notification7)
    nDAOi.save(notification8)
    nDAOi.save(notification9)
    nDAOi.save(notification10)
    nDAOi.save(notification11)
    nDAOi.save(notification12)
    val result = Await.result(nDAOi.save(notification),3 seconds)
    assert(notification.equals(result))
  }

  it should "save the notification in the db" in {
    val result = Await.result(nDAOi.save(notification4),3 seconds)
    val finded = Await.result(nDAOi.findByID(result.notificationID),3 seconds).get
    assert(notification4.equals(finded))
  }

  "FindByID" should "return the notification with the specified ID" in {
    val result = Await.result(nDAOi.findByID(notification4.notificationID),3 seconds).get
    assert(notification4.equals(result))
  }

  it should "return nothing if the notification doesn't exist" in {
    val result = nDAOi.findByID(UUID.fromString("00000000-0000-0000-0000-000000000000"))
    assert(result.value == None)
  }

  "FindAll" should "return a list of notification" in {
    val result = Await.result(nDAOi.findAll(),3 seconds)
    val list = List [Notification] (notification,notification1)
    assert(result.getClass.equals(list.getClass))
  }

  "FindNotificationofThingType" should "return a list of notification" in {
    val result = Await.result(nDAOi.findNotificationOfThingType(notification3.thingTypeID.get),3 seconds)
    val list = List [Notification] (notification,notification1)
    assert(result.getClass.equals(list.getClass))
  }

  it should "return a list of notification of the specified thing type" in {
    val result = Await.result(nDAOi.findNotificationOfThingType(notification3.thingTypeID.get),3 seconds)
    var it = result.iterator
    var find = it.forall(_.thingTypeID.get.equals(notification3.thingTypeID.get))
    assert(find.equals(true))
  }

  "FindNotificationofThing" should "return a list of notification" in {
    val result = Await.result(nDAOi.findNotificationOfThing(notification.thingID.get),3 seconds)
    val list = List [Notification] (notification,notification1)
    assert(result.getClass.equals(list.getClass))
  }

  it should "return a list of notification of the specified thing" in {
    val result = Await.result(nDAOi.findNotificationOfThing(notification.thingID.get),3 seconds)
    var it = result.iterator
    var find = it.forall(_.thingID.get.equals(notification.thingID.get))
    assert(find.equals(true))
  }

  "Update" should "return the modified notification" in {
    nDAOi.save(notification2)
    val result = Await.result(nDAOi.update(notification2.notificationID,notification3),3 seconds)
    assert(notification3.equals(result))
  }

  it should "update the selected notification" in {
    nDAOi.save(notification1)
    val result = Await.result(nDAOi.update(notification1.notificationID,notification2),3 seconds)
    val finded = Await.result(nDAOi.findByID(result.notificationID),3 seconds).get
    assert(notification2.equals(finded))
  }

  "Remove" should "return a list of notification" in {
    val result = Await.result(nDAOi.remove(notification.notificationID),3 seconds)
    val list = List [Notification] (notification,notification1)
    assert(result.getClass.equals(list.getClass))
  }

  it should "remove the notification with the specified ID" in {
    nDAOi.remove(notification5.notificationID)
    val result = nDAOi.findByID(notification5.notificationID)
    assert(result.value == None)
  }

  "RemoveByThingType" should "return a list of notification" in {
    val result = Await.result(nDAOi.removeByThingType(notification5.thingTypeID.get),3 seconds)
    val list = List [Notification] (notification, notification1)
    assert(result.getClass.equals(list.getClass))
  }

  it should "remove all the notification of the specified thing type" in {
    nDAOi.removeByThingType(notification6.thingTypeID.get)
    val result = nDAOi.findNotificationOfThingType(notification6.thingTypeID.get)
    assert(result.value == None)
  }

  "RemoveByThing" should "return a list of notification" in {
    val result = Await.result(nDAOi.removeByThing(notification12.thingID.get),3 seconds)
    val list = List [Notification] (notification, notification1)
    assert(result.getClass.equals(list.getClass))
  }

  it should "remove all the notification of the specified thing" in {
    nDAOi.removeByThing(notification9.thingID.get)
    val result = nDAOi.findNotificationOfThing(notification9.thingID.get)
    assert(result.value == None)
  }

}
