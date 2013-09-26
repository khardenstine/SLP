package altitourney.slp.games

import altitourney.slp.ServerContext
import altitourney.slp.structures.Target
import java.util.UUID
import org.joda.time.DateTime

abstract class IGame(val map: String) {
	def listActivePlayers: Set[UUID]
	def leftPlayers: Set[UUID]
	def rightPlayers: Set[UUID]

	def changeTeam(player: UUID, team: Int, serverContext: ServerContext): Unit
	def addKill(source: Option[UUID], victim: UUID, xp: Int, time: DateTime)
	def addAssist(source: UUID, xp: Int)
	def addGoal(source: Option[UUID], assist: Option[UUID], secondaryAssister: Option[UUID], xp: Int, time: DateTime)
	def spawn(player: UUID, perk: String, time: DateTime)
	def structureDamage(source: Option[UUID], target: Option[Target], xp: Int)
	def structureDestroy(source: Option[UUID], target: Option[Target], xp: Int)
	def setResult(result: Result): Unit
}

abstract class Game(map: String, leftTeamId: Int, rightTeamId: Int) extends IGame(map) {
	protected val leftTeam: Team = new Team(leftTeamId)
	protected val rightTeam: Team = new Team(rightTeamId)

	def listActivePlayers: Set[UUID] = {
		leftPlayers ++ rightPlayers
	}

	def leftPlayers = leftTeam.players.toSet
	def rightPlayers = rightTeam.players.toSet

	def changeTeam(player: UUID, team: Int, serverContext: ServerContext) {
		team match {
			case leftTeam.id => {
				leftTeam.players.add(player)
				rightTeam.players.remove(player)
			}
			case rightTeam.id => {
				rightTeam.players.add(player)
				leftTeam.players.remove(player)
			}
			case 2 => {
				if (map == serverContext.getLobbyMap)
				{
					leftTeam.players.remove(player)
					rightTeam.players.remove(player)
				}
			}
			case _ => sys.error("No team found for: " + team)
		}
	}

	def end(endTime: DateTime, serverContext: ServerContext): Unit
}
