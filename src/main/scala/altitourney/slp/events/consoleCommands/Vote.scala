package altitourney.slp.events.consoleCommands

import altitourney.slp.events.EventHandler
import altitourney.slp.events.consoleCommands.ladder.LadderUtils
import play.api.libs.json.JsValue

/**
 * {"port":27276,"time":66117655,"arguments":["ladder","start_random"],"source":"c0403b97-df6f-4b0d-b52c-76457d52d0d2","command":"vote","group":"Anonymous","type":"consoleCommandExecute"}
 */
class Vote(jsVal: JsValue) extends EventHandler(jsVal) {
	val arguments = getSeq("arguments").map(_.as[String])
	arguments match {
		case Seq("ladder", "start_random") => doLadderChecks()
		case _ =>
	}

	def doLadderChecks() {
		val ratings = LadderUtils.getRatings(getServerContext.getLadderMode, getGame.listActivePlayers)
		ratings.get(false).foreach(LadderUtils.shameAndSpectate)
	}
}
