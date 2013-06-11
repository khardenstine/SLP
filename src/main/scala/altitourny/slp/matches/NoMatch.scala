package altitourny.slp.matches

import java.util.UUID
import org.joda.time.DateTime

class NoMatch extends Match {
	def addKill(source: Option[UUID], victim: UUID, xp: Int) {}

	def addGoal(source: UUID, assist: Option[UUID], secondaryAsssiter: Option[UUID]) {}

	def addAssist(source: UUID, xp: Int) {}

	def dump(endTime: DateTime) {}
}
