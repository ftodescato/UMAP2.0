package controllers.admin

import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import forms.company._
import forms.engine._
import models._
import models.Company
import models.User
import models.Function
import models.daos.user.UserDAO
import models.daos.thingType.ThingTypeDAO
import models.daos.function.FunctionDAO
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
import scala.collection.mutable.ListBuffer


class FunctionController @Inject() (
  //authInfoRepository: AuthInfoRepository,
  //avatarService: AvatarService,
  //passwordHasher: PasswordHasher,
  val messagesApi: MessagesApi,
  val env: Environment[User, JWTAuthenticator],
  companyDao: CompanyDAO,
  functionDao: FunctionDAO,
  thingTypeDao: ThingTypeDAO,
  userDao: UserDAO)
  extends Silhouette[User, JWTAuthenticator] {



  def selectFunction = SecuredAction(WithServices("admin", true)).async(parse.json){ implicit request =>
    request.body.validate[SelectFunctionAdmin.Data].map { data =>
      companyDao.findByID(request.identity.company).flatMap{
        case None => Future.successful(BadRequest(Json.obj("message" -> Messages("company.notExists"))))
        case Some(company) =>
          val functionList = new ListBuffer[String]
          for( functionAlgList <- data.listFunction ){
            functionList += functionAlgList
          }
          val newCompany = Company(
              companyID = company.companyID,
              companyBusinessName = company.companyBusinessName,
              companyAddress = company.companyAddress,
              companyCity = company.companyCity,
              companyCAP = company.companyCAP,
              companyPIVA = company.companyPIVA,
              companyDescription = company.companyDescription,
              companyLicenseExpiration = company.companyLicenseExpiration,
              functionAlgList = functionList,
              companyName = company.companyName
          )
          for{
            company <- companyDao.update(company.companyID, newCompany)
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

}
