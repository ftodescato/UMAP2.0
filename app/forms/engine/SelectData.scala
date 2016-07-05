package forms.engine

import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import java.util.UUID



object SelectData {

  //form di play!
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


  //companion object
  object Data {
   // Converte l'oggetto [Data] in un Json e vice versa.
   implicit val jsonFormat = Json.format[Data]
  }
}
