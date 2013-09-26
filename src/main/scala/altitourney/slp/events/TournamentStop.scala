package altitourney.slp.events

import play.api.libs.json.JsValue

/**
 * {"port":27276,"time":197754,"type":"tournamentStop"}
 */
class TournamentStop(jsVal: JsValue) extends EventHandler(jsVal) {
	getServerContext.clearTournamentTeamLists()
}
