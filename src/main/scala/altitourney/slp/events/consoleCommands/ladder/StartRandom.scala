package altitourney.slp.events.consoleCommands.ladder

import altitourney.slp.events.consoleCommands.AbstractStart
import altitourney.slp.events.consoleCommands.ladder.LadderUtils.RatingTuple
import altitourney.slp.events.exceptions.{ServerMessageException, LadderNotConfigured}
import altitourney.slp.games.{LadderFactory, Mode, TBD, BALL}
import altitourney.slp.SLP
import java.util.UUID
import play.api.libs.json.JsValue
import scala.util.{Success, Failure, Random}

class StartRandom(jsVal: JsValue) extends AbstractStart(jsVal) {
	lazy val ratings: Map[UUID, Int] = {
		val bucketedRatings = LadderUtils.getRatings(getMode, playerList).groupBy(_._3)
		val cannotPlay = bucketedRatings.get(false)
		val cannotPlayNames = getServerContext.getPlayerNames(cannotPlay.getOrElse(Seq.empty).map(_._1))
		cannotPlayNames.map(getCommandExecutor.serverWhisper(_, "You must read and accept the rules (type the command '/listRules') before you can play any ladder games."))
		getCommandExecutor.assignSpectate(cannotPlayNames:_*)

		val canPlay = bucketedRatings.get(true).getOrElse(Seq.empty)
		verifyEnoughPlayers(canPlay)
		canPlay.map(t => (t._1, t._2)).toMap
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

	def buildTeams(): (Set[UUID], Set[UUID]) = {
		val ratingsSeq = Random.shuffle(ratings.toSeq.map(v => RatingTuple(v._2, v._1)))
		val leftTeam = ratingsSeq.slice(0, teamSize)
		val rightTeam = ratingsSeq.slice(teamSize, teamSize * 2)

		LadderUtils.balance(leftTeam, rightTeam)
	}
}

object LadderUtils {
	val ladderStartingRating: Int = 1500
	val maxVariance = 100

	def getRatings(mode: Mode, playerList: Set[UUID]): Seq[(UUID, Int, Boolean)] = {
		val query =
			"""
			  |SELECT v.vapor_id,
			  |       COALESCE(ladder_ranks.%s_rating, %s)     AS rating,
			  |       COALESCE(ladder_ranks.accepted_rules, false) AS accepted_rules
			  |FROM   (SELECT vapor_id
			  |        FROM   players
			  |        WHERE  vapor_id IN ( %s )) v
			  |       LEFT JOIN ladder_ranks
			  |              ON ladder_ranks.vapor_id = v.vapor_id;
			""".stripMargin.format(mode, ladderStartingRating, playerList.map(p => "'" + p.toString + "'").mkString(","))

		SLP.preparedQuery(query)(
			rs => (UUID.fromString(rs.getString("vapor_id")), rs.getInt("rating"), rs.getBoolean("accepted_rules"))
		) match {
			case Failure(e) =>
				SLP.getLog.error(e)
				throw new ServerMessageException("Error obtaining ratings.  Please try again.")
			case Success(ratingsList) => ratingsList
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

			val pair = swapList.reduceLeft((a: (UUID, VariantTuple), b: (UUID, VariantTuple)) =>
				if (a._2.isSmallerVariance(b._2)) a else b
			)

			((slt._1.map(_.player).diff(Seq(pair._1)):+pair._2.player).toSet,
			(slt._2.map(_.player).diff(Seq(pair._2.player)):+pair._1).toSet)
		}
	}
}
