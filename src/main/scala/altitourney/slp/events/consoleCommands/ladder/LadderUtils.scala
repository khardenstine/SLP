package altitourney.slp.events.consoleCommands.ladder

import altitourney.slp.commands.CommandExecutor
import altitourney.slp.events.exceptions.ServerMessageException
import altitourney.slp.games.Mode
import altitourney.slp.{ServerContext, Util, SLP}
import java.util.UUID
import scala.util.{Success, Failure}

object LadderUtils {
	val ladderStartingRating: Int = 1500
	val maxVariance = 100

	def getRatings(mode: Mode, playerList: Set[UUID]): Map[Boolean, Seq[(UUID, Int, Boolean)]] = {
		playerList.isEmpty match {
			case true => Map.empty
			case false => {
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
					""".stripMargin.format(mode, ladderStartingRating, Util.listToQuestionMarks(playerList))

				SLP.preparedQuery(
					query,
					Util.setListOnStatement(playerList, _),
					rs => (UUID.fromString(rs.getString("vapor_id")), rs.getInt("rating"), rs.getBoolean("accepted_rules"))
				) match {
					case Failure(e) =>
						SLP.getLog.error(e)
						throw new ServerMessageException("Error obtaining ratings.  Please try again.")
					case Success(ratingsList) => ratingsList.groupBy(_._3)
				}
			}
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

	def shameAndSpectate(cannotPlay: Seq[(UUID, Int, Boolean)])(implicit serverContext: ServerContext): Unit = {
		shameAndSpectate(serverContext.getPlayerNames(cannotPlay.map(_._1)), serverContext.commandExecutor)
	}

	def shameAndSpectate(cannotPlayNames: Seq[Option[String]], commandExecutor: CommandExecutor): Unit = {
		cannotPlayNames.map(commandExecutor.serverWhisper(_, "You must read and accept the rules (type the command '/listRules') before you can play any ladder games."))
		commandExecutor.assignSpectate(cannotPlayNames.flatten:_*)

		commandExecutor.serverMessage("The following players have not accepted the rules yet and cannot play:")
		commandExecutor.serverMessage(cannotPlayNames.flatten.mkString(", "))
	}
}
