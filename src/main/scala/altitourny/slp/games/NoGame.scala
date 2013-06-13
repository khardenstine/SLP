package altitourny.slp.games

import java.util.UUID
import org.joda.time.DateTime

class NoGame extends Game {
	def addKill(source: Option[UUID], victim: UUID, xp: Int) {}

	def addGoal(source: Option[UUID], assist: Option[UUID], secondaryAsssiter: Option[UUID], xp: Int) {}

	def addAssist(source: UUID, xp: Int) {}

	def dump(endTime: DateTime) {}

	def changeTeam(player: UUID, team: Int) {}
}
