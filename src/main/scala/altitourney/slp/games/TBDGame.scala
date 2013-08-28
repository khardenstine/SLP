package altitourney.slp.games

import java.util.UUID
import org.joda.time.DateTime

abstract class TBDGame(startTime: DateTime, map: String, leftTeamId: Int, rightTeamId: Int) extends AbstractGame(startTime, map, leftTeamId, rightTeamId) {
	def addGoal(source: Option[UUID], assist: Option[UUID], secondaryAssister: Option[UUID], xp: Int, time: DateTime) {}
}