package altitourney.slp.games

import java.util.UUID
import scala.collection.mutable
import altitourney.slp.SLP
import java.sql.ResultSet

class Team(final val id: Int) {
	val players: mutable.Set[UUID] = mutable.Set()
	private var score: Int = 0

	def modifyScore(mod: Int) {
		this.synchronized(
			score += mod
		)
	}

	def getScore = score

	def guessRosterId: Option[String] = {
		if (players.size > 0)
		{
			SLP.executeDBQuery[String](
				"""
				  |SELECT DISTINCT roster_id
				  |FROM rosters_r
				  |WHERE vapor_id IN (%s)
				""".stripMargin.format(players.map("'" + _ + "'").mkString(","))
				,(rs: ResultSet) => rs.getString(1)
			).fold(e => {
				SLP.getLog.error(e)
				None
			}, ids => {
				if (ids.size == 1)
					Some(ids(0))
				else
					None
			})
		} else {
			None
		}
	}
}
