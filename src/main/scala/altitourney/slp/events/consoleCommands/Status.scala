package altitourney.slp.events.consoleCommands

import altitourney.slp.events.EventHandler
import altitourney.slp.games.{Tournament, Ladder}
import play.api.libs.json.JsValue

class Status(jsVal: JsValue) extends EventHandler(jsVal) {
	val player = getUUID("source")

	getServerContext.serverWhisper(player, getGame match {
		case g: Ladder => "Ladder game started at " + g.startTime
		case g: Tournament => "Game started at " + g.startTime
		case _ => "No ladder game or tournament currently running."
	})

	getServerContext.tournamentTeamLists.foreach{ tl =>
		getServerContext.serverWhisper(player, tl._1.map(getServerContext.getTournamentPlayerName).mkString("Left Team: [", ",", "]"))
		getServerContext.serverWhisper(player, tl._2.map(getServerContext.getTournamentPlayerName).mkString("Right Team: [", ",", "]"))
	}
}
