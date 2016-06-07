package models.daos.detectionDouble

import java.util.UUID

import models.Thing
import models.Measurements
import models.DetectionDouble

import scala.concurrent.Future


trait DetectionDoubleDAO {

  def add(detectionDouble: DetectionDouble): Future[DetectionDouble]

}
