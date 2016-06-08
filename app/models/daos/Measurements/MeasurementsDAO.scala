// package models.daos.measurements
//
// import java.util.UUID
//
// import models.Thing
// import models.Measurements
// import models.DetectionDouble
//
// import scala.concurrent.Future
//
// /**
//  * Give access to the thing object.
//  */
// trait MeasurementsDAO {
//
//   def findByID(measurementsID: UUID): Future[Option[Measurements]]
//
//   def add(measurements: Measurements): Future[Measurements]
//
//   def updateDectentionDouble(measurementsID: UUID, detectionDouble: DetectionDouble): Future[Measurements]
//
// }
