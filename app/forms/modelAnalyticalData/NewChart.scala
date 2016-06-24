package forms.modelAnalyticalData

import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json

import java.util.UUID


object NewChart {

  /**
   * A play framework form.
   */
  val form = Form(
    mapping(
      "functionName" -> nonEmptyText,
      "objectID" -> uuid,
      "thingOrModel" -> nonEmptyText,
      "datas" -> list(text)
    )(Data.apply)(Data.unapply)
  )


  case class Data(
    functionName: String,
    objectID: UUID,
    thingOrModel: String,
    datas: List[String]
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
