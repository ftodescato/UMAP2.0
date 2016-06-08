package controllers.superAdmin

import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import forms.company._
import models._
import models.Company
import models.User
import models.daos.user.UserDAO
import models.daos.company.CompanyDAO
import play.api.i18n.{ MessagesApi, Messages }
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import play.api.mvc.Action

//import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
//import com.mohiva.play.silhouette.api.services.AvatarService
//import com.mohiva.play.silhouette.api.util.PasswordHasher
//import com.mohiva.play.silhouette.impl.providers.CredentialsProvider

import scala.concurrent.Future


class CompanyController @Inject() (
  //authInfoRepository: AuthInfoRepository,
  //avatarService: AvatarService,
  //passwordHasher: PasswordHasher,
  val messagesApi: MessagesApi,
  val env: Environment[User, JWTAuthenticator],
  companyDao: CompanyDAO,
  userDao: UserDAO)
  extends Silhouette[User, JWTAuthenticator] {


  def showCompanies = SecuredAction(WithServices("superAdmin", true)).async{ implicit request =>
    val companies = companyDao.findAll()
    companies.flatMap{
      companies =>
        Future.successful(Ok(Json.toJson(companies)))
    }
  }

  def showCompanyDetails(companyID: UUID) = SecuredAction(WithServices("superAdmin", true)).async{ implicit request =>
    val company = companyDao.findByID(companyID)
    company.flatMap{
      company =>
      Future.successful(Ok(Json.toJson(company)))
    }
  }

  def delete(companyID: UUID) = SecuredAction(WithServices("superAdmin", true)).async{ implicit request =>
    companyDao.findByID(companyID).flatMap{
      case None => Future.successful(BadRequest(Json.obj("message" -> Messages("company.notExists"))))
      case Some (company) =>
        for{
          company <- userDao.remove(companyID)
        }yield{
          companyDao.remove(companyID)
          //env.eventBus.publish(SignUpEvent(user, request, request2Messages))
          //env.eventBus.publish(LoginEvent(user, request, request2Messages))
          Ok(Json.obj("ok" -> "ok"))
         }
    }
  }

  def updateCompany (companyID : UUID) = SecuredAction(WithServices("superAdmin", true)).async(parse.json) { implicit request =>
    request.body.validate[EditCompany.Data].map { data =>
      companyDao.findByID(companyID).flatMap{
        case None => Future.successful(BadRequest(Json.obj("message" -> Messages("company.notExists"))))
        case Some (company) =>
          val company2 = Company(
              companyID = company.companyID,
              companyBusinessName = data.companyBusinessName,
              companyAddress = data.companyAddress,
              companyCity = data.companyCity,
              companyCAP = data.companyCAP,
              companyPIVA = data.companyPIVA,
              companyDescription = data.companyDescription,
              companyLicenseExpiration = data.companyLicenseExpiration,
              companyName = Some(data.companyName)
          )
          for{
            company <- companyDao.update(companyID,company2)
          }yield {
            //env.eventBus.publish(SignUpEvent(user, request, request2Messages))
            //env.eventBus.publish(LoginEvent(user, request, request2Messages))
            Ok(Json.obj("ok" -> "ok"))
           }
      }
    }.recoverTotal {
        case error =>
          Future.successful(Unauthorized(Json.obj("message" -> Messages("invalid.data"))))
      }
  }

  def addCompany = Action.async(parse.json) { implicit request =>
    request.body.validate[AddCompany.Data].map { data =>
      //val authInfo = passwordHasher.hash(data.password)
      val company = Company(
          companyID = UUID.randomUUID(),
          companyBusinessName = data.companyBusinessName,
          companyAddress = data.companyAddress,
          companyCity = data.companyCity,
          companyCAP = data.companyCAP,
          companyPIVA = data.companyPIVA,
          companyDescription = data.companyDescription,
          companyLicenseExpiration = data.companyLicenseExpiration,
          companyName = Some(data.companyName)
      )
      for{
        company <- companyDao.save(company)
        //user <- userService.save(user.copy(avatarURL = avatar))
        //authInfo <- authInfoRepository.add(loginInfo, authInfo)
        //authenticator <- env.authenticatorService.create(loginInfo)
        //token <- env.authenticatorService.init(authenticator)
      } yield {
          //env.eventBus.publish(SignUpEvent(user, request, request2Messages))
          //env.eventBus.publish(LoginEvent(user, request, request2Messages))
          Ok(Json.obj("ok" -> "ok"))
        }
      }.recoverTotal {
          case error =>
            Future.successful(Unauthorized(Json.obj("message" -> Messages("invalid.data"))))
        }
  }
}
