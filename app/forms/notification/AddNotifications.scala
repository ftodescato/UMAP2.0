package forms.notification

import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.data.format.Formats._

import java.util.UUID


object AddNotification {

//Form di Play! per creare una nuova notifica
  val form = Form(
    mapping(
      "description" -> nonEmptyText,
      "objectID" -> uuid,
      "modelOrThing" -> nonEmptyText,
      "parameter" -> nonEmptyText,
      "minValue" -> of(doubleFormat),
      "maxValue" -> of(doubleFormat)
    )(Data.apply)(Data.unapply)
  )


  case class Data(
    description: String,
    objectID: UUID,
    modelOrThing: String,
    parameter: String,
    minValue: Double,
    maxValue: Double)


    //companion object
    object Data {
       // Converte l'oggetto [Data] in un Json e vice versa.
      implicit val jsonFormat = Json.format[Data]
    }
}
