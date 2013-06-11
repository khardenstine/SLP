package altitourny.slp.matches

import java.util.UUID
import org.joda.time.DateTime

trait Match {
	def addKill(source: Option[UUID], victim: UUID, xp: Int)

	def addAssist(source: UUID, xp: Int)

	def addGoal(source: UUID, assist: Option[UUID], secondaryAssister: Option[UUID])

	def dump(endTime: DateTime)
}