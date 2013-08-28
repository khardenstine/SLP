package altitourney.slp.events.consoleCommands.ladder

import play.api.libs.json.JsValue
import altitourney.slp.events.EventHandler
import altitourney.slp.games.Ladder
import altitourney.slp.events.exceptions.ServerMessageException

class Stop(jsVal: JsValue) extends EventHandler(jsVal) {
	getServerContext.getGame match {
		case g: Ladder => {
			getCommandExecutor.changeMap(getServerContext.getLobbyMap)
			getCommandExecutor.stopTournament()
		}
		case _ => throw new ServerMessageException("Ladder is not currently running.")
	}
}
