package altitourney.slp.events.consoleCommands

import altitourney.slp.SLP
import altitourney.slp.events.LobbyHandler
import altitourney.slp.events.exceptions.{ServerMessageException, NotEnoughPlayers}
import altitourney.slp.games.Mode
import java.util.UUID
import play.api.libs.json.JsValue

abstract class AbstractStart(jsVal: JsValue) extends LobbyHandler(jsVal) {
	val playerList = getGame.listActivePlayers
	val teamSize = getMode.teamSize

	verifyEnoughPlayers(playerList)

	val teams = buildTeams()
	if (teams._1.size != teamSize || teams._2.size != teamSize) {
		SLP.getLog.error("Expected teamsize of %s, found (%s, %s).".format(teamSize, teams._1.size, teams._2.size))
		throw new ServerMessageException("Something went wrong building the teams.  Please try again.")
	}
	getServerContext.assignTeams(teams)
	preMapChange()
	getCommandExecutor.changeMap(getMap)

	def verifyEnoughPlayers(itr: Iterable[Any]): Unit = {
		if (itr.size < (teamSize * 2))
		{
			throw new NotEnoughPlayers
		}
	}

	def getMap: String

	def getMode: Mode

	def buildTeams(): (Set[UUID], Set[UUID])

	/**
	 * Last chance do something before changeMap
	 */
	def preMapChange(): Unit
}

