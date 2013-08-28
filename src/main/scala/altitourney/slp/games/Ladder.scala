package altitourney.slp.games

import altitourney.slp.SLP
import java.util.UUID
import org.joda.time.DateTime

abstract class Ladder(ratings: Map[UUID, Int], startTime: DateTime, map: String, leftTeamId: Int, rightTeamId: Int) extends AbstractGame(startTime, map, leftTeamId, rightTeamId) {
	def dump(endTime: DateTime): Unit = {
		record(endTime)
		// TODO start Transaction
		updateRatings(leftTeam, rightTeam)
		updateRatings(rightTeam, leftTeam)
		// end Transaction
	}

	// New Rating = Old Rating + [ 50 * ( S - E ) ]
	// E = 1 / [1 + 10^ ([(Avg rating of your opponents)-(Avg rating of you and your teammates)] / 400)]
	def updateRatings(team: Team, opposingTeam: Team) = {
		val S = if (team.getScore > opposingTeam.getScore) 1 else 0
		val E = 1 / (1 + math.pow(10, (getTeamAvgRating(opposingTeam) - getTeamAvgRating(team)) / 400))
		team.players.foreach{ player =>
			try {
				val oldRating = ratings.get(player).getOrElse(sys.error("Could not find ranking for player: " + player))
				val newRating = oldRating + (50 * (S - E))
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