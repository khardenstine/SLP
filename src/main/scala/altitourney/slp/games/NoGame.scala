package altitourney.slp.games

import java.util.UUID
import org.joda.time.DateTime

class NoGame(startTime: DateTime = new DateTime(), map: String = " ", leftTeamId: Int = 0, rightTeamId: Int = 1) extends Game(startTime, map, leftTeamId, rightTeamId) {
	def addKill(source: Option[UUID], victim: UUID, xp: Int, time: DateTime) {}

	def addGoal(source: Option[UUID], assist: Option[UUID], secondaryAsssiter: Option[UUID], xp: Int, time: DateTime) {}

	def addAssist(source: UUID, xp: Int) {}

	def spawn(player: UUID, redperk: String, time: DateTime) {}

	def dump(endTime: DateTime) {}
}
