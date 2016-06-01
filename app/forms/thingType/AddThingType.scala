package forms.thingType

import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json

import java.util.UUID


object AddThingType {

  /**
   * A play framework form.
   */
  val form = Form(
    mapping(
      "thingTypeName" -> nonEmptyText,
      "company" -> uuid,
      //array with number of parameters for each listIntValue and listStringValue and listDoubleValue
      "listQty" -> list(number),
      "listIntValue" -> list(text)
    )(Data.apply)(Data.unapply)
  )


  case class Data(
    thingTypeName: String,
    company: UUID,
    listQty: List[Int],
    listIntValue: List[String]
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
