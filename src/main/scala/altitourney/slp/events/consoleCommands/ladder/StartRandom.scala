package altitourney.slp.events.consoleCommands.ladder

import altitourney.slp.SLP
import altitourney.slp.events.consoleCommands.AbstractStart
import altitourney.slp.events.exceptions.{ServerMessageException, LadderNotConfigured}
import altitourney.slp.games.{LadderFactory, Mode, TBD, BALL}
import java.sql.ResultSet
import java.util.UUID
import play.api.libs.json.JsValue
import scala.util.Random

class StartRandom(jsVal: JsValue) extends AbstractStart(jsVal) {
	def maxVariance = 100

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

	case class RatingTuple(rating: Int, player: UUID){
		def variant(difToBalance: Int) = VariantTuple((rating * 2) - difToBalance, rating, player)
	}
	case class VariantTuple(variance:Int, rating: Int, player: UUID) {
		def isSmallerVariance(that: VariantTuple): Boolean = {
			this.variance <= that.variance
		}

		def smallerVariance(that: VariantTuple): VariantTuple = {
			if (isSmallerVariance(that)) this else that
		}
	}

	def buildTeams(): (Set[UUID], Set[UUID]) = {
		val ratingsSeq = Random.shuffle(ratings.toSeq.map(v => RatingTuple(v._2, v._1)))
		val leftTeam = ratingsSeq.slice(0, teamSize)
		val rightTeam = ratingsSeq.slice(teamSize, teamSize * 2)

		balance(leftTeam, rightTeam)
	}

	def balance(left: Seq[RatingTuple], right: Seq[RatingTuple]): (Set[UUID], Set[UUID]) = {
		def getSum(s: Seq[RatingTuple]): Int = s.foldLeft(0)((i: Int, tup: RatingTuple) => i + tup.rating)

		val leftSum = getSum(left)
		val rightSum = getSum(right)
		val sumDif = (leftSum - rightSum).abs

		if (sumDif <= maxVariance) {
			(left.map(_.player).toSet, right.map(_.player).toSet)
		} else {
			// slt._1 = smaller Seq
			// slt._2 = larger Seq
			val slt = if (leftSum < rightSum) (left, right) else (right, left)

			val swapList = slt._1.map { smallSwap: RatingTuple =>
				val difToBalance = (smallSwap.rating * 2) + sumDif

				val rightVariant = slt._2.foldLeft(slt._2.head.variant(difToBalance)){ (a: VariantTuple, b: RatingTuple) =>
					if (a.variance <= maxVariance) {
						a
					} else {
						a.smallerVariance(b.variant(difToBalance))
					}
				}

				(smallSwap.player, rightVariant)
			}

			val pair = swapList.reduceLeft((a: (UUID, VariantTuple), b: (UUID, VariantTuple)) => {
				if (a._2.isSmallerVariance(b._2))
					(a._1, a._2.player)
				else
					(b._1, b._2.player)
			})

			((slt._1.map(_.player).diff(Seq(pair._2)):+pair._2).toSet,
			(slt._2.map(_.player).diff(Seq(pair._1)):+pair._1).toSet)
		}
	}

}
