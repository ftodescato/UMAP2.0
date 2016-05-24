package models

import java.util.UUID
import play.api.libs.json.Json

/**
 * The company object.
 *
 * @param companyID The unique ID of the company.
 * @param companyName Maybe the company's name.
 *
 */
case class Company(
  companyID: UUID,
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
