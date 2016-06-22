package forms.function

import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json


object EditFunction {

  /**
   * A play framework form.
   */
  val form = Form(
    mapping(
      "functionName" -> nonEmptyText
    )(Data.apply)(Data.unapply)
  )


  case class Data(
    functionName: String
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
