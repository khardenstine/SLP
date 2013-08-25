package altitourney.slp.events.consoleCommands

import play.api.libs.json.JsValue
import altitourney.slp.events.{LobbyHandler, EventHandler}
import altitourney.slp.SLP
import altitourney.slp.events.exceptions.NotEnoughPlayers
import java.util.UUID

/**
 * {"port":27276,"time":22428,"arguments":["start_random"],"source":"79b7a12f-12b4-46ab-adae-580131833b88","command":"ladder","group":"Administrator","type":"consoleCommandExecute"}
 */
class Ladder(jsVal: JsValue) extends EventHandler(jsVal) {
	SLP.getRegistryFactory.getLadderRegistry.handle(jsVal)
}

class StartRandom(jsVal: JsValue) extends LobbyHandler(jsVal) {
	val playerList = getSharedEventData.getGame.listPlayers
	val teamSize = SLP.getLadderConfig.getInt("teamSize")
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
		"tbd_asteroids"
	}

	def getShouldSpec: Set[UUID] = {
		getSharedEventData.getGame.listPlayers.filter(uuid => !(teams._1 ++ teams._2).contains(uuid))
	}

	def buildTeams(playerList: Set[UUID]): (Set[UUID], Set[UUID]) = {
		val leftSplit = playerList.splitAt(teamSize)
		val left = leftSplit._1
		val rightSplit = leftSplit._2.splitAt(teamSize)
		val right = rightSplit._1
		(left, right)
	}

	implicit def uuids2Names(uuids: Set[UUID]): Seq[String] = uuids.map{uuid => getSharedEventData.getPlayerName(uuid)}.toSeq
}
