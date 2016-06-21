package models

import java.util.UUID
import play.api.libs.json.Json


case class Function(
  name: String
   )

object Function {

  implicit val jsonFormatFunction = Json.format[Function]

}
