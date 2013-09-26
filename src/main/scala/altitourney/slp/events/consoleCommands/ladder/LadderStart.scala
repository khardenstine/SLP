package altitourney.slp.events.consoleCommands.ladder

import altitourney.slp.events.consoleCommands.AbstractStart
import altitourney.slp.events.consoleCommands.ladder.LadderUtils.RatingTuple
import altitourney.slp.events.exceptions.LadderNotConfigured
import altitourney.slp.games.{LadderFactory, Mode, TBD, BALL}
import java.util.UUID
import play.api.libs.json.JsValue
import scala.util.Random

abstract class LadderStart(jsVal: JsValue) extends AbstractStart(jsVal) {
	lazy val ratings: Map[UUID, Int] = {
		val bucketedRatings = LadderUtils.getRatings(getMode, playerList).groupBy(_._3)
		val cannotPlay = bucketedRatings.get(false)
		val cannotPlayNames = getServerContext.getPlayerNames(cannotPlay.getOrElse(Seq.empty).map(_._1))
		cannotPlayNames.map(getCommandExecutor.serverWhisper(_, "You must read and accept the rules (type the command '/listRules') before you can play any ladder games."))
		getCommandExecutor.assignSpectate(cannotPlayNames.flatten:_*)

		getCommandExecutor.serverMessage("The following players have not accepted the rules yet and cannot play:")
		getCommandExecutor.serverMessage(cannotPlayNames.flatten.mkString(", "))

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
		val prioritizedRatings = ratings.groupBy(rating => getServerContext.getSecondPriorityPlayers.contains(rating._1))
		val highPriority = prioritizedRatings.get(false).getOrElse(Seq.empty)
		val pullFromLowPriority = (teamSize * 2) - highPriority.size
		val lowPriority = Random.shuffle(prioritizedRatings.get(true).getOrElse(Seq.empty))

		val shouldPlay = highPriority ++ lowPriority.take(pullFromLowPriority)

		val ratingsSeq = Random.shuffle(shouldPlay.toSeq.map(v => RatingTuple(v._2, v._1)))

		val leftTeam = ratingsSeq.take(teamSize)
		val rightTeam = ratingsSeq.slice(teamSize, teamSize * 2)

		LadderUtils.balance(leftTeam, rightTeam)
	}
}
