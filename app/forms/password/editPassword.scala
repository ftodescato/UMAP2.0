package forms.password

import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json


object EditPassword {

//Form di Play!
  val form = Form(
    mapping(
      "newPassword" -> nonEmptyText
    )(Data.apply)(Data.unapply)
  )


  case class Data(
    newPassword: String
    )

    //companion object
    object Data {
      // Converte l'oggetto [Data] in un Json e vice versa.
      implicit val jsonFormat = Json.format[Data]
    }
}
