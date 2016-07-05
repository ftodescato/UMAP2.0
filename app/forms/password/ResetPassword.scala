package forms.password

import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json


object ResetPassword {

//Form di PLay!
val form = Form(
    mapping(
      "email" -> email,
      "secretString" -> nonEmptyText,
      "newPassword" -> nonEmptyText
    )(Data.apply)(Data.unapply)
  )

  case class Data(
    email: String,
    secretString: String,
    newPassword: String
  )

  //companion object
  object Data {
     // Converte l'oggetto [Data] in un Json e vice versa.
    implicit val jsonFormat = Json.format[Data]
  }
}
