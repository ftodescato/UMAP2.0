package forms.modelAnalyticalData

import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json

import java.util.UUID


object NewChart {

//Form di Play! per creare un nuvo Chart
  val form = Form(
    mapping(
      "functionName" -> nonEmptyText,
      "objectID" -> uuid,
      "parameter" -> nonEmptyText
    )(Data.apply)(Data.unapply)
  )

  case class Data(
    functionName: String,
    objectID: UUID,
    parameter: String
    )

    //companion object
    object Data {
       // Converte l'oggetto [Data] in un Json e vice versa.
      implicit val jsonFormat = Json.format[Data]
    }
}
