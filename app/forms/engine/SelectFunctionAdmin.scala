package forms.engine

import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json

import java.util.UUID

object SelectFunctionAdmin {

/*
* Form di Play! per la scelta delle funzioni da mettere a disposizione agli
* utenti della company del'admin per la generazione di grafici
*/
  val form = Form(
    mapping(
      "listFunction" -> list(text)
    )(Data.apply)(Data.unapply)
  )


  case class Data(
    listFunction: List[String]
    )

    //companion object
    object Data {
       // Converte l'oggetto [Data] in un Json e vice versa.
      implicit val jsonFormat = Json.format[Data]
    }
}
