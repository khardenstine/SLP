package altitourney.slp.events.consoleCommands.ladder

import altitourney.slp.events.consoleCommands.AbstractStart
import altitourney.slp.events.exceptions.{ServerMessageException, LadderNotConfigured}
import altitourney.slp.games.{LadderFactory, Mode, TBD, BALL}
import java.util.UUID
import play.api.libs.json.JsValue
import scala.util.Random
import altitourney.slp.SLP
import java.sql.ResultSet

class StartRandom(jsVal: JsValue) extends AbstractStart(jsVal) {
	val maxVariance = 100

	lazy val ratings: Map[UUID, Int] = {
		val mode = getMode

		val query = """
		  |SELECT players.vapor_id, players.%s_rating
		  |FROM players
		  |WHERE players.vapor_id IN (%s)
		""".stripMargin.format(mode, playerList.map("'" + _ + "'").mkString(","))

		val ratingsList = SLP.executeDBQuery(query, (rs: ResultSet) => (UUID.fromString(rs.getString("vapor_id")), rs.getInt("%s_rating".format(mode))))
		if (ratingsList.isFailure) {
			SLP.getLog.error(ratingsList.failed.get)
			throw new ServerMessageException("Error obtaining ratings.  Please try again.")
		} else {
			ratingsList.get.toMap
		}
	}

	def preMapChange() = {
		getServerContext.setGameFactory(new LadderFactory(ratings))
	}

	def getMode: Mode = {
		getServerContext.getLadderMode.getOrElse(throw new LadderNotConfigured)
	}

	def getMap: String = {
		Random.shuffle(getMapList).head
	}

	def getMapList: Seq[String] = {
		getMode match {
			case BALL =>
				Seq(
					"ball_cave",
					"ball_cross",
					"ball_darkwar",
					"ball_funnelpark",
					"ball_grotto",
					"ball_ice",
					"ball_mayhem2",
					"ball_planepark",
					"ball_ufo",
					"ball_reef"
				)
			case TBD =>
				Seq(
					"tbd_asteroids",
					"tbd_bowserscastle",
					"tbd_cave",
					"tbd_core",
					"tbd_fallout",
					"tbd_focus",
					"tbd_grotto",
					"tbd_heights",
					"tbd_justice",
					"tbd_lostcity",
					"tbd_mayhem",
					"tbd_scrapyard",
					"tbd_underpark",
					"tbd_woods"
				)
		}
	}

	// TODO this might never balance
	def buildTeams(): (Set[UUID], Set[UUID]) = {
		val ratingsSeq = Random.shuffle(ratings.toSeq.map(_.swap))
		val leftTeam = ratingsSeq.slice(0, teamSize)
		val rightTeam = ratingsSeq.slice(teamSize, teamSize * 2)

		balance(leftTeam, rightTeam).getOrElse(buildTeams())
	}

	def balance(a: Seq[(Int, UUID)], b: Seq[(Int, UUID)]): Option[(Set[UUID], Set[UUID])] = {
		def getSum(s: Seq[(Int, Any)]): Int = s.foldLeft(0)((i: Int, tup: (Int, Any)) => i + tup._1)

		val aSum = getSum(a)
		val bSum = getSum(b)
		val sumDif = (aSum - bSum).abs / 2
		val halfVariance = maxVariance / 2

		if (sumDif <= halfVariance) {
			Some(a.map(_._2).toSet, b.map(_._2).toSet)
		} else {
			// slt._1 = smaller Seq
			// slt._2 = larger Seq
			val slt = if (aSum < bSum) (a, b) else (b, a)

			val swapList = slt._1.flatMap { smallSwap: (Int, UUID) =>
				val lowRating = smallSwap._1 + sumDif - halfVariance
				val highRating = smallSwap._1 + sumDif + halfVariance
				for (
					largeSwap <- slt._2.find{r => r._1 >= lowRating && r._1 <= highRating}
				) yield (smallSwap._2, largeSwap._2)
			}
			if (swapList.size < 1) {
				None
			} else {
				val pair = swapList.head
				Some(
					(a.map(_._2).diff(Seq(pair._1)):+pair._2).toSet,
					(b.map(_._2).diff(Seq(pair._2)):+pair._1).toSet
				)
			}
		}
	}

}
