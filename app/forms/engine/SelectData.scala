package forms.engine

import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import java.util.UUID
//import scala.collection.mutable.ListBuffer


object SelectData {

  /**
   * A play framework form.
   */
  val form = Form(
    mapping(
      "thingTypeID" -> uuid,
      "listData" -> list(text)
    )(Data.apply)(Data.unapply)
  )


  case class Data(
    thingTypeID: UUID,
    listData: List[String]
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
