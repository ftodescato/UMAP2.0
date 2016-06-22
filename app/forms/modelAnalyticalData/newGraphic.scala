package forms.modelAnalyticData

import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json

import java.util.UUID


object newGraphic {

  /**
   * A play framework form.
   */
  val form = Form(
    mapping(
      "functionName" -> nonEmptyText,
      "objectID" -> uuid,
      "datas" -> list(uuid)
    )(Data.apply)(Data.unapply)
  )


  case class Data(
    functionName: String,
    objectID: UUID,
    datas: List[UUID]
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
