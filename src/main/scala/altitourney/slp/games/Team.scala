package altitourney.slp.games

import altitourney.slp.SLP
import java.sql.ResultSet
import java.util.UUID
import scala.collection.mutable

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
			val rostersResult = SLP.executeDBQuery[String](
				"""
				  |SELECT DISTINCT roster_id
				  |FROM rosters_r
				  |WHERE vapor_id IN (%s)
				""".stripMargin.format(players.map("'" + _ + "'").mkString(","))
				,(rs: ResultSet) => rs.getString(1)
			)
			if (rostersResult.isFailure)
				SLP.getLog.error(rostersResult.failed.get)

			val rosters = rostersResult.getOrElse(Seq())

			if (rosters.size == 1) {
				Some(rosters(0))
			} else {
				None
			}
		} else {
			None
		}
	}
}
