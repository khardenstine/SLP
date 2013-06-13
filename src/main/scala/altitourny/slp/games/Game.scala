package altitourny.slp.games

import java.util.UUID
import org.joda.time.DateTime

trait Game {
	def addKill(source: Option[UUID], victim: UUID, xp: Int)

	def addAssist(source: UUID, xp: Int)

	def addGoal(source: Option[UUID], assist: Option[UUID], secondaryAssister: Option[UUID], xp: Int)

	def changeTeam(player: UUID, team: Int)

	def dump(endTime: DateTime)
}
