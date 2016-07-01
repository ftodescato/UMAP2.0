package controllers.superAdmin

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
import models.daos.thing.ThingDAO
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


class CompanyController @Inject() (
  //authInfoRepository: AuthInfoRepository,
  //avatarService: AvatarService,
  //passwordHasher: PasswordHasher,
  val messagesApi: MessagesApi,
  val env: Environment[User, JWTAuthenticator],
  companyDao: CompanyDAO,
  functionDao: FunctionDAO,
  thingTypeDao: ThingTypeDAO,
  thingDao: ThingDAO,
  thingTypeController: ThingTypeController,
  thingController: ThingController,
  userDao: UserDAO)
  extends Silhouette[User, JWTAuthenticator] {


  def showCompanies = SecuredAction(WithServices(Array("superAdmin"), true)).async{ implicit request =>
    val companies = companyDao.findAll()
    companies.flatMap{
      companies =>
        Future.successful(Ok(Json.toJson(companies)))
    }
  }

  def showCompanyDetails(companyID: UUID) = SecuredAction(WithServices(Array("superAdmin"), true)).async{ implicit request =>
    val company = companyDao.findByID(companyID)
    company.flatMap{
      company =>
      Future.successful(Ok(Json.toJson(company)))
    }
  }

  def delete(companyID: UUID) = SecuredAction(WithServices(Array("superAdmin"), true)).async{ implicit request =>
    companyDao.findByID(companyID).flatMap{
      case None => Future.successful(BadRequest(Json.obj("message" -> Messages("company.notExists"))))
      case Some (company) =>
      var listThingTypeWithThisC = new ListBuffer[ThingType]
      var listThingWithThisC = new ListBuffer[Thing]
      for (thingTypeWithThisC <- thingTypeDao.findByCompanyID(companyID))
      {
        for(thingType <- thingTypeWithThisC)
        {
          listThingTypeWithThisC += thingType
        }
      }
      for (thingType <-listThingTypeWithThisC)
      {
          thingTypeController.delete(thingType.thingTypeID)
      }

      //delete thing
      for (thingWithThisC <- thingDao.findByCompanyID(companyID))
      {
        for(thing <- thingWithThisC)
        {
          listThingWithThisC += thing
        }
      }
      for (thing <-listThingWithThisC)
      {
          thingController.delete(thing.thingID)
      }

        for{
          company <- userDao.remove(companyID)
        }yield{
          companyDao.remove(companyID)
          Ok(Json.obj("ok" -> "ok"))
         }
    }
  }

  def updateCompany (companyID : UUID) = SecuredAction(WithServices(Array("superAdmin"), true)).async(parse.json) { implicit request =>
    request.body.validate[EditCompany.Data].map { data =>
      companyDao.findByID(companyID).flatMap{
        case None => Future.successful(BadRequest(Json.obj("message" -> Messages("company.notExists"))))
        case Some (company) =>
          var listNameFunction = new ListBuffer[String]
          var functions = functionDao.findAll()
          for(function <- functions)
            for(nameFunction <- function)
            listNameFunction += nameFunction.name
          val company2 = Company(
              companyID = company.companyID,
              companyBusinessName = data.companyBusinessName,
              companyAddress = data.companyAddress,
              companyCity = data.companyCity,
              companyCAP = data.companyCAP,
              companyPIVA = data.companyPIVA,
              companyDescription = data.companyDescription,
              companyLicenseExpiration = data.companyLicenseExpiration,
              functionAlgList = listNameFunction,
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

  def addCompany = UserAwareAction.async(parse.json) { implicit request =>
    request.body.validate[AddCompany.Data].map { data =>
      companyDao.findByName(data.companyName).flatMap{
        case Some(company) => Future.successful(BadRequest(Json.obj("message" -> Messages("company.exists"))))
        case None  =>
      //val authInfo = passwordHasher.hash(data.password)
      var listNameFunction = new ListBuffer[String]
      var functions = functionDao.findAll()
      for(function <- functions)
        for(nameFunction <- function)
        listNameFunction += nameFunction.name
      val company = Company(
          companyID = UUID.randomUUID(),
          companyBusinessName = data.companyBusinessName,
          companyAddress = data.companyAddress,
          companyCity = data.companyCity,
          companyCAP = data.companyCAP,
          companyPIVA = data.companyPIVA,
          companyDescription = data.companyDescription,
          companyLicenseExpiration = data.companyLicenseExpiration,
          functionAlgList = listNameFunction,
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
      }
      }.recoverTotal {
          case error =>
            Future.successful(Unauthorized(Json.obj("message" -> Messages("invalid.data"))))
        }
  }

  def selectFunction = Action.async(parse.json) { implicit request =>
    request.body.validate[SelectFunction.Data].map { data =>
      companyDao.findByID(data.companyID).flatMap{
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

  def selectDataFromThingType = Action.async(parse.json) { implicit request =>
    request.body.validate[SelectData.Data].map { data =>
      thingTypeDao.findByID(data.thingTypeID).flatMap{
        case None => Future.successful(BadRequest(Json.obj("message" -> Messages("thingType.notExists"))))
        case Some(thingType) =>
          val dataList = new ListBuffer[String]   //list con valori temp etc spuntati
          for( thingTypeDate <- data.listData ){
            dataList += thingTypeDate
          }
          var count = 0
          var listDataTT = thingType.doubleValue.infos  //string e bool
          var newListDataTT = new ListBuffer[Info]
          for (allData <- listDataTT)
          {
            if (!(dataList.contains(allData.name)))
              {
                var name = listDataTT(count).name
                var newInfo = new Info(name, false)
                newListDataTT += newInfo
              }
              else{
                var name = listDataTT(count).name
                var newInfo = new Info(name, true)
                newListDataTT += newInfo
              }
              count = count + 1
          }
          var dataDouble = DataDouble(
          inUse = true,
          infos = newListDataTT
          )
          val newThingType = ThingType(
            thingTypeID = thingType.thingTypeID,
            thingTypeName = thingType.thingTypeName,
            companyID = thingType.companyID,
            doubleValue = dataDouble
          )
        for{
          thingType <- thingTypeDao.update(thingType.thingTypeID, newThingType)
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
