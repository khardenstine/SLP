package altitourney.slp.events

import altitourney.slp.games.Result
import play.api.libs.json.JsValue

/**
 * {"port":27276,"result":"decisive","time":319401,"winners":["8b96d3eb-5eb7-4435-a30e-8d3dd54c97ae"],"losers":["d0e8ad71-e540-4f5b-bb73-91a8960523de"],"type":"tournamentRoundEnd"}
 * {"port":27276,"result":"tie","time":612736,"winners":[],"losers":[],"type":"tournamentRoundEnd"}
 */
class TournamentRoundEnd(jsVal: JsValue) extends EventHandler(jsVal) {
	val result = getString("result")
	getGame.setResult(Result.withName(result).getOrElse(sys.error("Unable to reify " + result)))
}
