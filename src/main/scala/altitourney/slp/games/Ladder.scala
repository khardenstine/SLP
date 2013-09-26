package altitourney.slp.games

import altitourney.slp.ServerContext
import altitourney.slp.ServerContext.TournamentPlayer
import java.util.UUID
import org.joda.time.DateTime

abstract class Ladder(ratings: Map[UUID, Int], startTime: DateTime, map: String, leftTeamId: Int, rightTeamId: Int) extends AbstractGame(startTime, map, leftTeamId, rightTeamId) {
	def dump(gameId: UUID, endTime: DateTime, serverContext: ServerContext): Unit = {
		val tourneyTeamLists = serverContext.getTournamentTeamLists.getOrElse(sys.error("TournamentTeamList not populated for ladder, wtf?"))

		val leftTeamOldAvg = getTeamAvgRating(tourneyTeamLists._1)
		val rightTeamOldAvg = getTeamAvgRating(tourneyTeamLists._2)

		val leftRC = getTeamRatingsChange(tourneyTeamLists._1, getS(leftTeam), leftTeamOldAvg, rightTeamOldAvg, serverContext)
		val rightRC = getTeamRatingsChange(tourneyTeamLists._2, getS(rightTeam), rightTeamOldAvg, leftTeamOldAvg, serverContext)

		// TODO start Transaction
		record(gameId, endTime, Some(RatingsChange(leftTeamOldAvg, getNewAvg(leftRC)), RatingsChange(rightTeamOldAvg, getNewAvg(rightRC))))
		updatePlayersRatings(gameId, leftRC, serverContext)
		updatePlayersRatings(gameId, rightRC, serverContext)
		// end Transaction

		serverContext.commandExecutor.stopTournament()
	}

	def getS(team: Team): Double = {
		getVictor match {
			case Some(victor) => if (team == victor) 1 else 0
			case None => 0.5
		}
	}

	def updatePlayersRatings(gameId: UUID, rcs: Set[PlayerRatingsChange], serverContext: ServerContext): Unit = {
		rcs.foreach{
			rc =>
				serverContext.serverWhisper(rc.player,
					"New Rating: %s(%s)".format(rc.newRating, (if(rc.newRating < rc.oldRating) "-" else "+") + (rc.newRating - rc.oldRating).abs)
				)

				GameUtils.updatePlayerRating(rc.player, rc.newRating - rc.oldRating, mode)
				GameUtils.insertPlayerRatingsChange(gameId, rc)
		}
	}

	def getNewAvg(rcs: Set[PlayerRatingsChange]): Int = {
		rcs.foldLeft(0)(_ + _.newRating) / rcs.size
	}

	// New Rating = Old Rating + [ 50 * ( S - E ) ]
	// E = 1 / [1 + 10^ ([(Avg rating of your opponents)-(Avg rating of you and your teammates)] / 400)]
	def getTeamRatingsChange(players: Set[TournamentPlayer], S: Double, teamAvg: Int, opposingTeamAvg: Int, serverContext: ServerContext): Set[PlayerRatingsChange] = {
		val E: Double = 1 / (1 + math.pow(10, (opposingTeamAvg - teamAvg) / 400))

		players.map{ player =>
			val oldRating = ratings.get(player.vaporId).getOrElse(sys.error("Could not find ranking for player: " + player))
			val newRating = (oldRating + (50 * (S - E))).toInt
			PlayerRatingsChange(player.vaporId, oldRating, newRating)
		}
	}

	def getTeamAvgRating(players: Set[TournamentPlayer]): Int = {
		players.map{
			p => ratings.get(p.vaporId).getOrElse(sys.error("Could not find ranking for player: " + p.vaporId))
		}.fold(0)(_+_) / mode.teamSize
	}

}
