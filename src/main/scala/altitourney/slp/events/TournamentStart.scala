package altitourney.slp.events

import play.api.libs.json.JsValue
import java.util.UUID

/**
 * {"port":27276,"time":197705,"team0":["8b96d3eb-5eb7-4435-a30e-8d3dd54c97ae"],"type":"tournamentStart","team1":["d0e8ad71-e540-4f5b-bb73-91a8960523de"]}
 */
class TournamentStart(jsVal: JsValue) extends EventHandler(jsVal) {
	val leftTeam = (jsVal \ "team0").as[Seq[JsValue]].map(v => UUID.fromString(v.as[String])).toSet
	val rightTeam = (jsVal \ "team1").as[Seq[JsValue]].map(v => UUID.fromString(v.as[String])).toSet

	getGame.tournamentTeamLists = Some(leftTeam, rightTeam)
}
