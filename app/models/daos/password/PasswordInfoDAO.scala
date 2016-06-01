package models.daos.password

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.impl.daos.DelegableAuthInfoDAO
import play.api.libs.concurrent.Execution.Implicits._
import java.util.UUID

import scala.collection.mutable
import scala.concurrent.Future

import javax.inject.Inject
import play.api.libs.json._
import scala.concurrent.ExecutionContext.Implicits.global

import reactivemongo.api._

import play.modules.reactivemongo.json._
import play.modules.reactivemongo.json.collection._
import models.PersistentPasswordInfo



class PasswordInfoDAO @Inject() (db : DB) extends DelegableAuthInfoDAO[PasswordInfo] {

  implicit val passwordInfoFormat = Json.format[PasswordInfo]

  def collection: JSONCollection = db.collection[JSONCollection]("password")

  def find(loginInfo: LoginInfo) = {

    val passwordInfo: Future[Option[PersistentPasswordInfo]] = collection
      .find(Json.obj( "loginInfo" -> loginInfo ))
      .one[PersistentPasswordInfo]

    passwordInfo.flatMap {
      case None =>
        Future.successful(Option.empty[PasswordInfo])
      case Some(persistentPasswordInfo) =>
        Future(Some(persistentPasswordInfo.authInfo))
    }
  }

  def add(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
    collection.insert(PersistentPasswordInfo(loginInfo, authInfo))
    Future.successful(authInfo)
  }


  def update(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
    val newPersistentPasswordInfo = PersistentPasswordInfo(loginInfo, authInfo)
    collection.update(Json.obj("loginInfo" -> loginInfo),newPersistentPasswordInfo)
    Future.successful(authInfo)
  }

  def updateNewLoginInfo(loginInfo: LoginInfo, loginInfoNew: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
    val newPersistentPasswordInfo = PersistentPasswordInfo(loginInfoNew, authInfo)
    collection.update(Json.obj("loginInfo" -> loginInfo),newPersistentPasswordInfo)
    Future.successful(authInfo)
  }

  def save(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
    find(loginInfo).flatMap {
      case Some(_) => update(loginInfo, authInfo)
      case None => add(loginInfo, authInfo)
    }
  }

  def remove(loginInfo: LoginInfo): Future[Unit] = {
    collection.remove(Json.obj("loginInfo" -> loginInfo))
    Future.successful(())
  }
}
