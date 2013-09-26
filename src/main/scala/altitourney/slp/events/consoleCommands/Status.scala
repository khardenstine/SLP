package altitourney.slp.events.consoleCommands

import altitourney.slp.Util
import altitourney.slp.events.EventHandler
import altitourney.slp.games.{AbstractGame, Tournament, Ladder}
import play.api.libs.json.JsValue

class Status(jsVal: JsValue) extends EventHandler(jsVal) {
	val player = getUUID("source")

	getServerContext.serverWhisper(player, getGame match {
		case g: Ladder => printGameStart(g, "Ladder game")
		case g: Tournament => printGameStart(g, "Tournament")
		case _ => "No ladder game or tournament currently running."
	})

	getServerContext.getTournamentTeamLists.foreach{ tl =>
		getServerContext.serverWhisper(player, tl._1.map(getServerContext.getTournamentPlayerName).mkString("Left Team: [", ", ", "]"))
		getServerContext.serverWhisper(player, tl._2.map(getServerContext.getTournamentPlayerName).mkString("Right Team: [", ", ", "]"))
	}

	def printGameStart(game: AbstractGame, prefix: String): String = {
		"%s started %s ago at %s".format(prefix, Util.getFormattedTimeDifference(game.startTime), Util.formatFor(game.startTime, Util.TIME_FORMAT))
	}
}
