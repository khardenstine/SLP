package altitourney.slp.events.consoleCommands.ladder

import altitourney.slp.events.consoleCommands.AbstractStart
import altitourney.slp.events.exceptions.LadderNotConfigured
import altitourney.slp.games.{LadderFactory, Mode, TBD, BALL}
import java.util.UUID
import play.api.libs.json.JsValue
import scala.util.Random

class StartRandom(jsVal: JsValue) extends AbstractStart(jsVal) {
	def preMapChange() = {
		getServerContext.setGameFactory(LadderFactory)
	}

	def getMode: Mode = {
		getServerContext.getLadderMode.getOrElse(throw new LadderNotConfigured)
	}

	def getMap: String = {
		Random.shuffle(getMapList).head
	}

	def getMapList: Seq[String] = {
		mode match {
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

	def buildTeams(playerList: Set[UUID]): (Set[UUID], Set[UUID]) = {
		val leftSplit = playerList.splitAt(teamSize)
		val left = leftSplit._1
		val rightSplit = leftSplit._2.splitAt(teamSize)
		val right = rightSplit._1
		(left, right)
	}
}
