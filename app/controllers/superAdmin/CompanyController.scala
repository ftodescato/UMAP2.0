package controllers.superAdmin

import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AvatarService
import com.mohiva.play.silhouette.api.util.PasswordHasher
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import forms.formCompany._
import models.Company
import models.User
import models.daos.company.CompanyDAO
import play.api.i18n.{ MessagesApi, Messages }
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import play.api.mvc.Action

import scala.concurrent.Future


class CompanyController @Inject() (
  val messagesApi: MessagesApi,
  val env: Environment[User, JWTAuthenticator],
  companyDao: CompanyDAO)
  //authInfoRepository: AuthInfoRepository,
  //avatarService: AvatarService,
  //passwordHasher: PasswordHasher)
  extends Silhouette[User, JWTAuthenticator] {



def showCompanies = Action.async{ implicit request =>
 val companies = companyDao.findAll()
   companies.flatMap{
    companies =>
   Future.successful(Ok(Json.toJson(companies)))
   }
  //Future.successful(Ok(Json.obj("test"->"test")))
}
def showCompanyDetails(companyID: UUID) = Action.async{ implicit request =>
  val company = companyDao.findByID(companyID)
    company.flatMap{
     company =>
    Future.successful(Ok(Json.toJson(company)))
    }
}

def delete(companyID: UUID) = Action.async{ implicit request =>
  companyDao.findByID(companyID).flatMap{
      case None => Future.successful(BadRequest(Json.obj("message" -> "Company non trovata")))
      case Some (company) =>
        for{
          company <- companyDao.remove(companyID)
        }yield {
            //env.eventBus.publish(SignUpEvent(user, request, request2Messages))
            //env.eventBus.publish(LoginEvent(user, request, request2Messages))
            Ok(Json.obj("ok" -> "ok"))
          }
        }

 }


def updateCompany (companyID : UUID) = Action.async(parse.json) { implicit request =>
  request.body.validate[EditCompanyForm.Data].map { data =>
  companyDao.findByID(companyID).flatMap{
      case None => Future.successful(BadRequest(Json.obj("message" -> "Company non trovata")))
      case Some (company) =>
        val company2 = Company(
            companyID = company.companyID,
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
    request.body.validate[AddCompanyForm.Data].map { data =>
          //val authInfo = passwordHasher.hash(data.password)
          val company = Company(
            companyID = UUID.randomUUID(),
              companyName = Some(data.companyName)
          )
          for {
            //user <- userService.save(user.copy(avatarURL = avatar))
            company <- companyDao.save(company)
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
