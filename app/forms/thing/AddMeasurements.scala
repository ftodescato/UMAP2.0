package forms.thing

import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import java.util.{Date, Locale}
import java.text.DateFormat
import java.text.DateFormat._
import models._
import java.util.UUID

import scala.collection.mutable.ListBuffer



object AddMeasurement {

  /**
   * A play framework form.
   */
  val form = Form(
    mapping(
      "thingID" -> uuid,
      "dataTime" -> nonEmptyText,
      "healty" -> boolean
        )(Data.apply)(Data.unapply)
  )


  case class Data(

    thingID: UUID,
    dataTime: String,
    healty: Boolean
  )


  /**
   * The companion object.
   */
  object Data {

    /**
     * Converts the [Date] object to Json and vice versa.
     */
    implicit val jsonFormat = Json.format[Data]
  }
}
