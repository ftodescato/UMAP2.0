package controllers.admin

import java.util.UUID

import javax.inject.Inject

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator

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

import scala.concurrent.Future
import scala.collection.mutable.ListBuffer


class FunctionController @Inject() (
  val messagesApi: MessagesApi,
  val env: Environment[User, JWTAuthenticator],
  companyDao: CompanyDAO,
  functionDao: FunctionDAO,
  thingTypeDao: ThingTypeDAO,
  userDao: UserDAO)
  extends Silhouette[User, JWTAuthenticator] {

  /*
  *   metodo che permette agli admin di selezionare le funzioni
  *   da rendere disponibile all'utente nel calcolo dei valori dei grafici
  */
  def selectFunction = SecuredAction(WithServices(Array("admin"), true)).async(parse.json){ implicit request =>
    // richiesta alla form forms.engine.SelectFunctionAdmin
    request.body.validate[SelectFunctionAdmin.Data].map { data =>
      // verifica all'interno del DB dell'esistenza della company dell'admin autenticato
      companyDao.findByID(request.identity.company).flatMap{
        case None => Future.successful(BadRequest(Json.obj("message" -> Messages("company.notExists"))))
        case Some(company) =>
          val functionList = new ListBuffer[String]  //ListBuffer che conterrà le nuove funzioni a disposizione dell'utente
          //ciclo sulla lista di funzioni passate tramite form
          for(functionAlgList <- data.listFunction){
            //riempimento di functionList con tutte le funzioni passate dalla form
            functionList += functionAlgList
          }
          //nuova company contenente le nuove funzioni
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
            //sostituzione della vecchia company con quella aggiornata alle ultime funzioni scelte dall'admin
            company <- companyDao.update(company.companyID, newCompany)
          }yield {
            Ok(Json.obj("ok" -> "ok"))
           }
      }
    }.recoverTotal {
          case error =>
            Future.successful(Unauthorized(Json.obj("message" -> Messages("invalid.data"))))
        }
  }

}
