package altitourny.slp.matches

import java.util.UUID
import org.joda.time.DateTime

class TBDMatch(startTime: DateTime, map: String) extends AbstractMatch(startTime, map)
{
	def addGoal(source: UUID, assist: Option[UUID], secondaryAssister: Option[UUID]) {}

	def getMode = "tbd_5v5"
}