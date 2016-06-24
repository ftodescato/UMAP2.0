package controllers.admin

import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
//import com.mohiva.play.silhouette.api.services.AvatarService
//import com.mohiva.play.silhouette.api.util.PasswordHasher
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.impl.exceptions.IdentityNotFoundException

import com.mohiva.play.silhouette.impl.providers.CredentialsProvider

import forms.modelAnalyticalData._

import models._
import models.daos.chart._
import models.Chart

import play.api.i18n.{ MessagesApi, Messages }
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import play.api.mvc.Action

import scala.concurrent.Future
import scala.collection.mutable.ListBuffer



class ChartController @Inject() (
  val messagesApi: MessagesApi,
  val env: Environment[User, JWTAuthenticator],
  //userService: UserService,
  chartDao: ChartDAO
  //userDao: UserDAO,
  //companyDao: CompanyDAO
)
  extends Silhouette[User, JWTAuthenticator] {


  def addChart = Action.async(parse.json) { implicit request =>
    request.body.validate[NewChart.Data].map { data =>
      var dataList = new ListBuffer[String]
      for(nameData <- data.datas){
        dataList += nameData
      }
      if(data.thingOrModel == "Oggetto")
        {val chart = Chart(
          chartID = UUID.randomUUID(),
          functionName = data.functionName,
          thingID = data.objectID,
          thingTypeID = null,
          infoDataName = dataList
        )
        for{
          chart <- chartDao.save(chart)
          //user <- userService.save(user.copy(avatarURL = avatar))
          //authInfo <- authInfoRepository.add(loginInfo, authInfo)
          //authenticator <- env.authenticatorService.create(loginInfo)
          //token <- env.authenticatorService.init(authenticator)
        } yield {
            //env.eventBus.publish(SignUpEvent(user, request, request2Messages))
            //env.eventBus.publish(LoginEvent(user, request, request2Messages))
            Ok(Json.obj("ok" -> "ok"))
          }
        }
      else{
        val chart = Chart(
          chartID = UUID.randomUUID(),
          functionName = data.functionName,
          thingID = null,
          thingTypeID = data.objectID,
          infoDataName = dataList
        )
        for{
          chart <- chartDao.save(chart)
          //user <- userService.save(user.copy(avatarURL = avatar))
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
