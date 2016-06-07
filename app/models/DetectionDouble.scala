package models

import java.util.UUID
import play.api.libs.json.Json
import play.api.libs.json._


 case class DetectionDouble(
  //measurementsID: UUID,
  sensor: String,
  value: Long
)

object DetectionDouble {

   /**
    * Converts the [Detection] object to Json and vice versa.
    */
   implicit val jsonFormatDetection = Json.format[DetectionDouble]
}
//  object MeasurementsInt {
// //
// //   /**
//   //  * Converts the [MeasurementsInt] object to Json and vice versa.
//   //  */
//    val writes: Writes[MeasurementsInt] = (
//      (__ \ 'name).write[String] and
//      (__ \ 'time).write[Date] and
//     (__ \ 'value).write[Int]
//    )(unlift(MeasurementsInt.unapply))
//
//    val reads: Reads[MeasurementsInt] = (
//      (__ \ 'name).read[String] and
//      (__ \ 'time).read[Date] and
//     (__ \ 'value).read[Int]
//   )(MeasurementsInt.apply _)
// }
