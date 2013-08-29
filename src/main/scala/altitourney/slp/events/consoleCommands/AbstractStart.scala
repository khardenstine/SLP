package altitourney.slp.events.consoleCommands

import altitourney.slp.events.LobbyHandler
import altitourney.slp.events.exceptions.{ServerMessageException, NotEnoughPlayers}
import altitourney.slp.games.Mode
import java.util.UUID
import play.api.libs.json.JsValue
import altitourney.slp.SLP

abstract class AbstractStart(jsVal: JsValue) extends LobbyHandler(jsVal) {
	val playerList = getGame.listActivePlayers
	val teamSize = getMode.teamSize
	if (playerList.size < (teamSize * 2))
	{
		throw new NotEnoughPlayers
	}

	val teams = buildTeams()
	if (teams._1.size != teamSize || teams._2.size != teamSize) {
		SLP.getLog.error("Expected teamsize of %s, found (%s, %s).".format(teamSize, teams._1.size, teams._2.size))
		throw new ServerMessageException("Something went wrong building the teams.  Please try again.")
	}
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

	def buildTeams(): (Set[UUID], Set[UUID])

	/**
	 * Last chance do something before changeMap
	 */
	def preMapChange(): Unit
}

