package controllers.superAdmin

import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AvatarService
import com.mohiva.play.silhouette.api.util.PasswordHasher
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import forms.CompanyForm
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



  def addCompany = Action.async(parse.json) { implicit request =>
    request.body.validate[CompanyForm.Data].map { data =>
      val companyInfo = data.companyName
      companyDao.find(companyInfo).flatMap {
        case Some(company) =>
          Future.successful(BadRequest(Json.obj("message" -> Messages("company.exists"))))
        case None =>
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
      }
    }.recoverTotal {
      case error =>
        Future.successful(Unauthorized(Json.obj("message" -> Messages("invalid.data"))))
    }
  }
}
