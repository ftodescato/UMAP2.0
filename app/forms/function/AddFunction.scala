package forms.function

import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json


object AddFunction {

//Form di Play! per aggiungere una funzione 
  val form = Form(
    mapping(
      "functionName" -> nonEmptyText
    )(Data.apply)(Data.unapply)
  )


  case class Data(
    functionName: String
    )

//companion object
    object Data {
       // Converte l'oggetto [Data] in un Json e vice versa.
      implicit val jsonFormat = Json.format[Data]
    }
}
