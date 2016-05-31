package models

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.impl.daos.DelegableAuthInfoDAO
import play.api.libs.concurrent.Execution.Implicits._

import scala.collection.mutable
import scala.concurrent.Future

import javax.inject.Inject
import play.api.libs.json._
import scala.concurrent.ExecutionContext.Implicits.global

import reactivemongo.api._

import play.modules.reactivemongo.json._
import play.modules.reactivemongo.json.collection._

case class PersistentPasswordInfo(
  loginInfo: LoginInfo, authInfo: PasswordInfo
)

object PersistentPasswordInfo {
   implicit val passwordInfoFormat = Json.format[PasswordInfo]
   implicit val persistentPasswordInfoFormat = Json.format[PersistentPasswordInfo]
 }
