package models.daos.modelLogReg

import java.util.UUID

import models.LogRegModel

import scala.concurrent.Future

/**
 * Give access to the chart object.
 */
trait ModelLogRegDAO {

  def findByThingID(thingID: UUID): Future[Option[LogRegModel]]

  def save(logRegModelID: LogRegModel): Future[LogRegModel]

  def update(logRegModelID: UUID, newModelLogReg: LogRegModel)

  def remove(logRegModelID: UUID): Future[List[LogRegModel]]

}
