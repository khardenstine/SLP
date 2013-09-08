package altitourney.slp.games

import altitourney.slp.{Util, SLP}
import java.util.UUID
import scala.collection.mutable
import scala.util.{Success, Failure}

class Team(final val id: Int) {
	protected[games] val players: mutable.Set[UUID] = mutable.Set()
	private var score: Int = 0

	protected[games] def modifyScore(mod: Int) {
		this.synchronized(
			score += mod
		)
	}

	def getScore = score

	def guessRosterId: Option[String] = {
		if (players.size < 1) {
			None
		} else {
			SLP.preparedQuery(
				"""
				  |SELECT DISTINCT roster_id
				  |FROM rosters_r
				  |WHERE vapor_id IN (%s);
				""".stripMargin.format(players.map(p => "'" + p.toString + "'").mkString(",")),
				Util.setListOnStatement(players, _),
				_.getString(1)
			) match {
				case Success(rosters) =>
					if (rosters.size == 1)
						Some(rosters.head)
					else
						None
				case Failure(e) =>
					SLP.getLog.error(e)
					None
			}
		}
	}
}
