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

import scala.concurrent.Future
import scala.collection.mutable.ListBuffer


class CompanyController @Inject() (
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

//metodo che fornisce l'elenco delle tutte company
  def showCompanies = SecuredAction(WithServices(Array("superAdmin"), true)).async{ implicit request =>
    val companies = companyDao.findAll()
    companies.flatMap{
      companies =>
        Future.successful(Ok(Json.toJson(companies)))
    }
  }

//metodo che fornisce una company cercata tramite il suo ID
  def showCompanyDetails(companyID: UUID) = SecuredAction(WithServices(Array("superAdmin"), true)).async{ implicit request =>
    val company = companyDao.findByID(companyID)
    company.flatMap{
      company =>
      Future.successful(Ok(Json.toJson(company)))
    }
  }

// metodo che elimina una company dato il suo ID
  def delete(companyID: UUID) = SecuredAction(WithServices(Array("superAdmin"), true)).async{ implicit request =>
    //ricerca la company da eliminre tramite ID
    companyDao.findByID(companyID).flatMap{
      case None =>
        Future.successful(BadRequest(Json.obj("message" -> Messages("company.notExists"))))
      //se esiste la company
      case Some (company) =>
      /*
      *  listThingTypeWithThisC = ListBuffer contenente la lista dei ThingType della company da eliminare
      *  listThingWithThisC = ListBuffer contenente la lista dei Thing della company da eliminare
      */
        var listThingTypeWithThisC = new ListBuffer[ThingType]
        var listThingWithThisC = new ListBuffer[Thing]

        //ciclo sulla lista dei thingType della company da eliminare
        for (thingTypeWithThisC <- thingTypeDao.findByCompanyID(companyID))
        {// prendo il thingType dalla lista dei thingType della company e lo inseristo in listThingTypeWithThisC
          for(thingType <- thingTypeWithThisC)
            listThingTypeWithThisC += thingType
        }

        //elimino tutti i thingType legati alla company da eliminare
        for (thingType <-listThingTypeWithThisC)
          thingTypeController.delete(thingType.thingTypeID)

        //stesso procedimento per eliminare tutti i thing della company
        for (thingWithThisC <- thingDao.findByCompanyID(companyID))
        {
          for(thing <- thingWithThisC)
            listThingWithThisC += thing
        }
        for (thing <-listThingWithThisC)
            thingController.delete(thing.thingID)
        for{
          //elimino tutti gli user della company
          company <- userDao.remove(companyID)
        }yield{
          //rimuovo la copany
          companyDao.remove(companyID)
          Ok(Json.obj("ok" -> "ok"))
       }
    }
  }

//metodo per modificare i campi di una company scelta passando il suo ID
  def updateCompany (companyID : UUID) = SecuredAction(WithServices(Array("superAdmin"), true)).async(parse.json) { implicit request =>
    //richiesta alla form forms.company.EditCompany
    request.body.validate[EditCompany.Data].map { data =>
      //ricerca della company da modificare all'interno del DB
      companyDao.findByID(companyID).flatMap{
        case None =>
          Future.successful(BadRequest(Json.obj("message" -> Messages("company.notExists"))))
        case Some (company) =>
        //listNameFunction = ListBuffer che conterrà i nomi delle funzioni disponibili alla company
          var listNameFunction = new ListBuffer[String]
          //functions = scala.concurrent.Future[models.Function] contenente tutte le funzioni disponibili in DB
          var functions = functionDao.findAll()
          // inserimento di tutte le funzioni presenti in DB in listNameFunction
          for(function <- functions)
            for(nameFunction <- function)
            listNameFunction += nameFunction.name
          // creazione di una nuova company con i campi modificati
          val newCompany = Company(
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
            //modifica della vecchia company con la nuova con i campi modificati
            company <- companyDao.update(companyID,newCompany)
          }yield {
            Ok(Json.obj("ok" -> "ok"))
           }
      }
    }.recoverTotal {
        case error =>
          Future.successful(Unauthorized(Json.obj("message" -> Messages("invalid.data"))))
      }
  }

//metodo per aggiungere una nuova company
  def addCompany = UserAwareAction.async(parse.json) { implicit request =>
    //richiesta alla form forms.company.AddCompany
    request.body.validate[AddCompany.Data].map { data =>
      //ricerca della company tramite Nome per evitare di inserirne una con nome uguale
      companyDao.findByPIVA(data.companyPIVA).flatMap{
        case Some(company) =>
          Future.successful(BadRequest(Json.obj("message" -> Messages("company.exists"))))
        case None  =>
        //listNameFunction = ListBuffer che conterrà i nomi delle funzioni disponibili alla company
          var listNameFunction = new ListBuffer[String]
          //functions = scala.concurrent.Future[models.Function] contenente tutte le funzioni disponibili in DB
          var functions = functionDao.findAll()
          // inserimento di tutte le funzioni presenti in DB in listNameFunction
          for(function <- functions)
            for(nameFunction <- function)
            listNameFunction += nameFunction.name
          //creazione di una nuova company
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
            //inserimento della nuova company nel DB
            company <- companyDao.save(company)
          } yield {
              Ok(Json.obj("ok" -> "ok"))
            }
      }
      }.recoverTotal {
          case error =>
            Future.successful(Unauthorized(Json.obj("message" -> Messages("invalid.data"))))
        }
  }

//metodo che permette di di modificare le funzioni disponibili per una company
  def selectFunction = Action.async(parse.json) { implicit request =>
    //richiesta alla form forms.engine.SelectFunction
    request.body.validate[SelectFunction.Data].map { data =>
      //ricerca nel DB di una company tramite ID
      companyDao.findByID(data.companyID).flatMap{
        case None =>
          Future.successful(BadRequest(Json.obj("message" -> Messages("company.notExists"))))
        case Some(company) =>
        //functionList = ListBuffer che conterrà le nuove funzioni disponibili alla company
          val functionList = new ListBuffer[String]
          //ciclo sulla lista di funzioni inserita nella form
          for( functionAlgList <- data.listFunction ){
            //inserimento delle funzioni dalla form a functionList
            functionList += functionAlgList
          }
          //creazione nuova company con lista funzioni aggiornata
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
            //modifica della company
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

//metodo per selezionare i tipi di dati che potranno essere utilizzati dalle funzioni
  def selectDataFromThingType = Action.async(parse.json) { implicit request =>
    //richiesta alla form forms.engine.SelectData
    request.body.validate[SelectData.Data].map { data =>
      //ricerca nel DB del thingType tramite ID
      thingTypeDao.findByID(data.thingTypeID).flatMap{
        case None =>
          Future.successful(BadRequest(Json.obj("message" -> Messages("thingType.notExists"))))
        case Some(thingType) =>
        //dataList = ListBuffer che conterrà la lista di dati che potranno essere utilizzati dalle funzioni
          val dataList = new ListBuffer[String]
          //inserimento dei tipi di dati inseriti nella form in dataList
          for( thingTypeDate <- data.listData ){
            dataList += thingTypeDate
          }
          var count = 0       //count = contatore per trovare Info all'interno listDataTT
          var listDataTT = thingType.doubleValue.infos    // listDataTT = lista di Info del thingType ottenuto dalla ricerca
          var newListDataTT = new ListBuffer[Info]        // newListDataTT = ListBuffer che conterrà Info con visibilità modificata
          //ciclo sulla lista di Info del thingType
          for (allData <- listDataTT)
          {//verifico che dentro alla lista della form non ci sia il dato presente in listDataTT
            if (!(dataList.contains(allData.name)))
              {//imposto a false la visibilità di un dato se non è all'interno della lista fornita dalla form
                var name = listDataTT(count).name
                var newInfo = new Info(name, false)
                //inserisco il nuovo oggetto Info con visibilità a false in newListDataTT
                newListDataTT += newInfo
              }
              else{
                //altrimenti imposto a true
                var name = listDataTT(count).name
                var newInfo = new Info(name, true)
                //inserisco il nuovo oggetto Info con visibilità a true in newListDataTT
                newListDataTT += newInfo
              }
              count = count + 1
          }
          //nuovo DataDouble con la lista di dati uguale a newListDataTT
          var dataDouble = DataDouble(
          inUse = true,
          infos = newListDataTT
          )
          //creazione nuovo ThingType con DataDouble modificati
          val newThingType = ThingType(
            thingTypeID = thingType.thingTypeID,
            thingTypeName = thingType.thingTypeName,
            companyID = thingType.companyID,
            doubleValue = dataDouble
          )
        for{
          //modifica del ThingType
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
