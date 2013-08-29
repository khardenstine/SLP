package altitourney.slp.games

import altitourney.slp.{ServerContext, SLP}
import java.util.UUID
import org.joda.time.DateTime
import altitourney.slp.commands.CommandExecutor

abstract class Ladder(ratings: Map[UUID, Int], startTime: DateTime, map: String, leftTeamId: Int, rightTeamId: Int) extends AbstractGame(startTime, map, leftTeamId, rightTeamId) {
	def dump(endTime: DateTime, serverContext: ServerContext): Unit = {
		serverContext.commandExecutor.stopTournament()
		record(endTime)
		// TODO start Transaction
		updateRatings(leftTeam, rightTeam, serverContext)
		updateRatings(rightTeam, leftTeam, serverContext)
		// end Transaction
	}

	// TODO ties?
	// New Rating = Old Rating + [ 50 * ( S - E ) ]
	// E = 1 / [1 + 10^ ([(Avg rating of your opponents)-(Avg rating of you and your teammates)] / 400)]
	def updateRatings(team: Team, opposingTeam: Team, serverContext: ServerContext) = {
		val S = if (team.getScore > opposingTeam.getScore) 1 else 0
		val E = 1 / (1 + math.pow(10, (getTeamAvgRating(opposingTeam) - getTeamAvgRating(team)) / 400))
		team.players.foreach{ player =>
			try {
				val oldRating = ratings.get(player).getOrElse(sys.error("Could not find ranking for player: " + player))
				val newRating = (oldRating + (50 * (S - E))).toInt

				SLP.preparedStatement{
					val stmt = "UPDATE players SET " + mode + "_rating = ? WHERE vapor_id = ?;"
					SLP.getLog.debug(stmt + "  (" + player.toString + "," + newRating +")")
					stmt
				}{
					stmt =>
						stmt.setInt(1, newRating)
						stmt.setString(2, player.toString)

						stmt.execute()
				}

				serverContext.commandExecutor.serverWhisper(serverContext.getPlayerName(player),
					"New Rating: %s(%s)".format(newRating, (if(S == 1) "+" else "-") + (newRating - oldRating).abs)
				)
			} catch {
				case e: Exception => SLP.getLog.error(e)
			}
		}
	}

	def getTeamAvgRating(team: Team): Int = {
		team.players.map{
			p => ratings.get(p).getOrElse(sys.error("Could not find ranking for player: " + p))
		}.fold(0)(_+_) / mode.teamSize
	}

}
