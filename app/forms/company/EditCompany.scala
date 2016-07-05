package forms.company

import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json

import java.util.Date

object EditCompany {

  //form di play! per modificare una company
  val form = Form(
    mapping(
      "companyBusinessName" -> nonEmptyText,
      "companyAddress" -> nonEmptyText,
      "companyCity" -> nonEmptyText,
      "companyCAP" -> nonEmptyText,
      "companyPIVA" -> nonEmptyText,
      "companyDescription" -> nonEmptyText,
      "companyLicenseExpiration" -> date,
      "companyName" -> nonEmptyText
    )(Data.apply)(Data.unapply)
  )


  case class Data(
    companyBusinessName: String,
    companyAddress: String,
    companyCity: String,
    companyCAP: String,
    companyPIVA: String,
    companyDescription: String,
    companyLicenseExpiration: Date,
    companyName: String
  )

  //companion object
  object Data {
     // Converte l'oggetto [Data] in un Json e vice versa.
    implicit val jsonFormat = Json.format[Data]
  }
}
