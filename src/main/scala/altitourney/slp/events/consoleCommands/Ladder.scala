package altitourney.slp.events.consoleCommands

import altitourney.slp.SLP
import altitourney.slp.events.exceptions.{LadderNotConfigured, NotEnoughPlayers}
import altitourney.slp.events.{LobbyHandler, EventHandler}
import altitourney.slp.games.{BALL, TBD}
import java.util.UUID
import play.api.libs.json.JsValue
import scala.util.Random

/**
 * {"port":27276,"time":22428,"arguments":["start_random"],"source":"79b7a12f-12b4-46ab-adae-580131833b88","command":"ladder","group":"Administrator","type":"consoleCommandExecute"}
 */
class Ladder(jsVal: JsValue) extends EventHandler(jsVal) {
	SLP.getRegistryFactory.getLadderRegistry.handle(jsVal)
}

class StartRandom(jsVal: JsValue) extends LobbyHandler(jsVal) {
	val playerList = getServerContext.getGame.listPlayers
	val ladderMode = getServerContext.getLadderMode.getOrElse(throw new LadderNotConfigured)
	val teamSize = ladderMode.teamSize
	if (playerList.size < (teamSize * 2))
	{
		throw new NotEnoughPlayers
	}

	val teams = buildTeams(playerList)

	getCommandExecutor.assignLeftTeam(teams._1:_*)
	getCommandExecutor.assignRightTeam(teams._2:_*)

	while (getShouldSpec.size > 0) {
		getCommandExecutor.stopTournament()
		getCommandExecutor.assignSpectate(getShouldSpec:_*)
		getCommandExecutor.startTournament()
		Thread.sleep(50)
	}

	getCommandExecutor.startTournament()
	getCommandExecutor.changeMap(getMap)

	def getMap: String = {
		Random.shuffle(getMapList).head
	}

	def getMapList: Seq[String] = {
		ladderMode match {
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

	def getShouldSpec: Set[UUID] = {
		getServerContext.getGame.listPlayers.filter(uuid => !(teams._1 ++ teams._2).contains(uuid))
	}

	def buildTeams(playerList: Set[UUID]): (Set[UUID], Set[UUID]) = {
		val leftSplit = playerList.splitAt(teamSize)
		val left = leftSplit._1
		val rightSplit = leftSplit._2.splitAt(teamSize)
		val right = rightSplit._1
		(left, right)
	}

	implicit def uuids2Names(uuids: Set[UUID]): Seq[String] = uuids.map{uuid => getServerContext.getPlayerName(uuid)}.toSeq
}
