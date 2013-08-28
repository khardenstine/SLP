package altitourney.slp.events.consoleCommands

import play.api.libs.json.JsValue
import altitourney.slp.events.LobbyHandler
import altitourney.slp.events.exceptions.NotEnoughPlayers
import altitourney.slp.games.Mode
import java.util.UUID

abstract class AbstractStart(jsVal: JsValue) extends LobbyHandler(jsVal) {
	val playerList = getGame.listActivePlayers
	val mode = getMode
	val teamSize = mode.teamSize
	if (playerList.size < (teamSize * 2))
	{
		throw new NotEnoughPlayers
	}

	val teams = buildTeams(playerList)
	assignTeams(teams)
	preMapChange()
	getCommandExecutor.changeMap(getMap)

	def assignTeams(teams: (Set[UUID], Set[UUID])): Unit = {
		implicit def uuids2Names(uuids: Set[UUID]): Seq[String] = uuids.map{uuid => getServerContext.getPlayerName(uuid)}.toSeq

		def getShouldSpec: Set[UUID] = {
			getGame.listActivePlayers.filter(uuid => !(teams._1 ++ teams._2).contains(uuid))
		}

		getCommandExecutor.assignLeftTeam(teams._1:_*)
		getCommandExecutor.assignRightTeam(teams._2:_*)
		getCommandExecutor.assignSpectate(getShouldSpec:_*)
		getCommandExecutor.startTournament()

		// TODO should also check that left and right team are correct so no 7v5s
		while (getShouldSpec.size > 0) {
			getCommandExecutor.stopTournament()
			getCommandExecutor.assignSpectate(getShouldSpec:_*)
			getCommandExecutor.startTournament()
			Thread.sleep(50)
		}

		getCommandExecutor.startTournament()
	}

	def getMap: String

	def getMode: Mode

	def buildTeams(playerList: Set[UUID]): (Set[UUID], Set[UUID])

	/**
	 * Last chance do something before changeMap
	 */
	def preMapChange(): Unit
}

