// package models.daos.measurements
//
// import models.Thing
// import models.Measurements
// import models.DetectionDouble
// import java.util.UUID
//
//
// import scala.collection.mutable
// import scala.concurrent.Future
//
// import javax.inject.Inject
// import play.api.libs.json._
// import scala.concurrent.ExecutionContext.Implicits.global
//
// import reactivemongo.api._
//
// import play.modules.reactivemongo.json._
// import play.modules.reactivemongo.json.collection._
//
// class MeasurementsDAOImpl @Inject() (db : DB) extends MeasurementsDAO {
//
//   def collection: JSONCollection = db.collection[JSONCollection]("measurements")
//
//   def add(measurements: Measurements): Future[Measurements] = {
//     collection.insert(measurements)
//     Future.successful(measurements)
//   }
//
//   def findByID(measurementsID: UUID): Future[Option[Measurements]] = {
//     collection.find(Json.obj("measurementsID" -> measurementsID)).one[Measurements]
//   }
//
//   def updateDectentionDouble(measurementsID: UUID, detectionDouble: DetectionDouble): Future[Measurements] = {
//     findByID(measurementsID).flatMap{
//       case Some(measurements) =>
//       val newSensors = measurements.sensors
//       newSensors += detectionDouble
//       val measurements2 = Measurements(
//         measurementsID =  measurements.measurementsID,
//         thingID = measurements.thingID,
//         dataTime = measurements.dataTime,
//         sensors = newSensors,
//         healty = measurements.healty
//       )
//       collection.update(Json.obj("measurementsID" -> measurementsID), measurements2)
//       Future.successful(measurements2)
//       case None =>
//       val measurementsNull = Measurements(
//         measurementsID = null,
//         thingID = null,
//         dataTime = "",
//         sensors = null,
//         healty = false
//       )
//         Future.successful(measurementsNull)
//     }
//   }
// }
