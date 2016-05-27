package controllers.superAdmin

import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import forms.thing._
import models.Thing
import models.User
import models.daos.company.CompanyDAO
import models.daos.thingType.ThingTypeDAO
import models.daos.thing.ThingDAO
import play.api.i18n.{ MessagesApi, Messages }
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import play.api.mvc.Action

//import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
//import com.mohiva.play.silhouette.api.services.AvatarService
//import com.mohiva.play.silhouette.api.util.PasswordHasher
//import com.mohiva.play.silhouette.impl.providers.CredentialsProvider

import scala.concurrent.Future


class ThingController @Inject() (
  //authInfoRepository: AuthInfoRepository,
  //avatarService: AvatarService,
  //passwordHasher: PasswordHasher,
  val messagesApi: MessagesApi,
  val env: Environment[User, JWTAuthenticator],
  thingDao: ThingDAO,
  thingTypeDao: ThingTypeDAO,
  companyDao: CompanyDAO)
extends Silhouette[User, JWTAuthenticator] {

  // def showCompanies = Action.async{ implicit request =>
  //   val companies = companyDao.findAll()
  //   companies.flatMap{
  //     companies =>
  //       Future.successful(Ok(Json.toJson(companies)))
  //   }
  // }
  //
  // def showCompanyDetails(companyID: UUID) = Action.async{ implicit request =>
  //   val company = companyDao.findByID(companyID)
  //   company.flatMap{
  //     company =>
  //     Future.successful(Ok(Json.toJson(company)))
  //   }
  // }
  //
  // def delete(companyID: UUID) = Action.async{ implicit request =>
  //   companyDao.findByID(companyID).flatMap{
  //     case None => Future.successful(BadRequest(Json.obj("message" -> Messages("company.notExists"))))
  //     case Some (company) =>
  //       for{
  //         company <- userDao.removeByCompany(companyID)
  //       }yield{
  //         companyDao.remove(companyID)
  //         //env.eventBus.publish(SignUpEvent(user, request, request2Messages))
  //         //env.eventBus.publish(LoginEvent(user, request, request2Messages))
  //         Ok(Json.obj("ok" -> "ok"))
  //        }
  //   }
  // }
  //
  // def updateCompany (companyID : UUID) = Action.async(parse.json) { implicit request =>
  //   request.body.validate[EditCompany.Data].map { data =>
  //     companyDao.findByID(companyID).flatMap{
  //       case None => Future.successful(BadRequest(Json.obj("message" -> Messages("company.notExists"))))
  //       case Some (company) =>
  //         val company2 = Company(
  //             companyID = company.companyID,
  //             companyName = Some(data.companyName)
  //         )
  //         for{
  //           company <- companyDao.update(companyID,company2)
  //         }yield {
  //           //env.eventBus.publish(SignUpEvent(user, request, request2Messages))
  //           //env.eventBus.publish(LoginEvent(user, request, request2Messages))
  //           Ok(Json.obj("ok" -> "ok"))
  //          }
  //     }
  //   }.recoverTotal {
  //       case error =>
  //         Future.successful(Unauthorized(Json.obj("message" -> Messages("invalid.data"))))
  //     }
  // }

  def addThing = Action.async(parse.json) { implicit request =>
    request.body.validate[AddThing.Data].map { data =>
      val companyInfo = data.company
      companyDao.findByID(companyInfo).flatMap{
        case Some(companyToAssign) =>
        val thingTypeInfo = data.thingTypeID
        thingTypeDao.findByID(thingTypeInfo).flatMap{
        case Some(thingTypeToAssign) =>

      //val authInfo = passwordHasher.hash(data.password)
      val thing = Thing(
          thingID = UUID.randomUUID(),
          name = data.thingName,
          serialNumber = data.serialNumber,
          description = data.description,
          thingTypeID = data.thingTypeID,
          companyID = data.company
      )
      for{
        thing <- thingDao.save(thing)
        //user <- userService.save(user.copy(avatarURL = avatar))
        //authInfo <- authInfoRepository.add(loginInfo, authInfo)
        //authenticator <- env.authenticatorService.create(loginInfo)
        //token <- env.authenticatorService.init(authenticator)
      } yield {
          //env.eventBus.publish(SignUpEvent(user, request, request2Messages))
          //env.eventBus.publish(LoginEvent(user, request, request2Messages))
          Ok(Json.obj("ok" -> "ok"))
        }
        case None =>
          Future.successful(BadRequest(Json.obj("message" -> Messages("thingType.notExists"))))
      }
        case None =>
          Future.successful(BadRequest(Json.obj("message" -> Messages("company.notExists"))))
      }
      }.recoverTotal {
          case error =>
            Future.successful(Unauthorized(Json.obj("message" -> Messages("invalid.data"))))
        }
  }
}
