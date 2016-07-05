package forms.notification

import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.data.format.Formats._


object EditNotification {

//Form di Play! per modificare una notifica
  val form = Form(
    mapping(
      "notificationDescription" -> nonEmptyText,
      "valMax" -> of(doubleFormat),
      "valMin" -> of(doubleFormat)
    )(Data.apply)(Data.unapply)
  )


  case class Data(
    notificationDescription: String,
    valMax: Double,
    valMin: Double
    )

    //companion object
    object Data {
       // Converte l'oggetto [Data] in un Json e vice versa.
      implicit val jsonFormat = Json.format[Data]
    }
}
