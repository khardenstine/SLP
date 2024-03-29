package altitourney.slp.games

import altitourney.slp.ServerContext
import altitourney.slp.structures.Target
import java.util.UUID
import org.joda.time.DateTime

class NoGame(map: String, leftTeamId: Int, rightTeamId: Int) extends Game(map, leftTeamId, rightTeamId) {
	def addKill(source: Option[UUID], victim: UUID, xp: Int, time: DateTime) {}

	def addGoal(source: Option[UUID], assist: Option[UUID], secondaryAsssiter: Option[UUID], xp: Int, time: DateTime) {}

	def addAssist(source: UUID, xp: Int) {}

	def spawn(player: UUID, redperk: String, time: DateTime) {}

	def structureDamage(source: Option[UUID], target: Option[Target], xp: Int) {}

	def structureDestroy(source: Option[UUID], target: Option[Target], xp: Int) {}

	def setResult(result: Result): Unit = {}

	def end(endTime: DateTime, serverContext: ServerContext) {}
}
