package forms.engine

import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
//import scala.collection.mutable.ListBuffer


object SelectFunction {

  /**
   * A play framework form.
   */
  val form = Form(
    mapping(
      "companyName" -> nonEmptyText,
      "listFunction" -> list(text)
    )(Data.apply)(Data.unapply)
  )


  case class Data(
    companyName: String,
    listFunction: List[String]
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
