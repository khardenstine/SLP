package altitourney.slp.games

import java.util.UUID
import org.joda.time.DateTime

abstract class Game(startTime: DateTime, val map: String, leftTeamId: Int, rightTeamId: Int) {
	protected val leftTeam: Team = new Team(leftTeamId)
	protected val rightTeam: Team = new Team(rightTeamId)

	def listPlayers: Set[UUID] = {
		(leftTeam.players ++ rightTeam.players).toSet
	}

	def changeTeam(player: UUID, team: Int) {
		team match {
			case leftTeam.id => {
				leftTeam.players.add(player)
				rightTeam.players.remove(player)
			}
			case rightTeam.id => {
				rightTeam.players.add(player)
				leftTeam.players.remove(player)
			}
			case 2 => {}
			case _ => throw new RuntimeException("No team found for: " + team)
		}
	}

	def addKill(source: Option[UUID], victim: UUID, xp: Int, time: DateTime)

	def addAssist(source: UUID, xp: Int)

	def addGoal(source: Option[UUID], assist: Option[UUID], secondaryAssister: Option[UUID], xp: Int, time: DateTime)

	def spawn(player: UUID, perk: String, time: DateTime)

	def dump(endTime: DateTime)
}
