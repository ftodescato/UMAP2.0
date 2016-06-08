package models

import java.util.UUID
import play.api.libs.json.Json
import java.util.Date


case class Company(
  companyID: UUID,
  companyBusinessName: String,
  companyAddress: String,
  companyCity: String,
  companyCAP: String,
  companyPIVA: String,
  companyDescription: String,
  companyLicenseExpiration: Date,
  companyName: Option[String]

   )


/**
 * The companion object.
 */
object Company {

  /**
   * Converts the [Company] object to Json and vice versa.
   */
  implicit val jsonFormatCompany = Json.format[Company]

}
