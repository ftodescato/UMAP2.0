package controllers.shared.adminUser

import java.util.UUID
import javax.inject.Inject
import models.DataDouble

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import forms.thingType._
import models.ThingType
import models._
import models.Thing
import models.User
import models.Info
import models.daos.company.CompanyDAO
import models.daos.thingType.ThingTypeDAO
import models.daos.thing.ThingDAO
import play.api.i18n.{ MessagesApi, Messages }
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import play.api.mvc.Action
import scala.collection.mutable.ListBuffer

//import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
//import com.mohiva.play.silhouette.api.services.AvatarService
//import com.mohiva.play.silhouette.api.util.PasswordHasher
//import com.mohiva.play.silhouette.impl.providers.CredentialsProvider

import scala.concurrent.Future


class ThingTypeController @Inject() (
  //authInfoRepository: AuthInfoRepository,
  //avatarService: AvatarService,
  //passwordHasher: PasswordHasher,
  val messagesApi: MessagesApi,
  val env: Environment[User, JWTAuthenticator],
  thingDao: ThingDAO,
  thingTypeDao: ThingTypeDAO,
  companyDao: CompanyDAO)
extends Silhouette[User, JWTAuthenticator] {

  def showThingType = SecuredAction(WithServices(Array("admin","user"), true)).async{ implicit request =>
   val thingType = thingTypeDao.findByCompanyID(request.identity.company)
   thingType.flatMap{
    thingType =>
     Future.successful(Ok(Json.toJson(thingType)))
   }
  }

  def showThingTypeDetails(id: UUID) = SecuredAction(WithServices(Array("admin","user"), true)).async{ implicit request =>
   val thingType = thingTypeDao.findByID(id)
   thingType.flatMap{
    thingType =>
     Future.successful(Ok(Json.toJson(thingType)))
   }
  }

  def showThings  = SecuredAction(WithServices(Array("admin","user"), true)).async{ implicit request =>
   val things = thingDao.findByCompanyID(request.identity.company)
   things.flatMap{
    things =>
     Future.successful(Ok(Json.toJson(things)))
   }
  }

  def showThingDetails(id: UUID) = SecuredAction(WithServices(Array("admin","user"), true)).async{ implicit request =>
   val thing = thingDao.findByID(id)
   thing.flatMap{
    thing =>
     Future.successful(Ok(Json.toJson(thing)))
   }
  }
}
