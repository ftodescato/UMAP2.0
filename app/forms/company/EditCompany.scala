package forms.company

import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import java.util.Date

object EditCompany {

  /**
   * A play framework form.
   */
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


  object Data {

  /**
   * Converts the [Date] object to Json and vice versa.
   */
  implicit val jsonFormat = Json.format[Data]
  }
}
