package models

import java.util.UUID
import play.api.libs.json._
import play.api.libs.json._
import play.api.libs.json.OWrites
import play.api.libs.functional.syntax._




trait Measurements

 object Measurements {
  implicit val measurementsWrites: Writes[Measurements] =
    new Writes[Measurements]{
      def writes(o: Measurements): JsValue = o match {
        case s: MeasurementsInt => MeasurementsInt.writes.writes(s)
      }
    }
}
 // object Measurements {
 //
 //   /**
 //    * Converts the [Measurements] object to Json and vice versa.
 //    */
 //   implicit val jsonFormatMeasurement = Json.format[Measurements]
 //
 // }
//  Writes[Measurements] {
//     case obj1: MeasurementsInt => Json.writes[MeasurementsInt].writes(obj1)
//  }
//  Reads[Measurements] {
//     case obj1: MeasurementsInt => Json.writes[MeasurementsInt].reads(obj1)
//  }
// }
