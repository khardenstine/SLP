package altitourney.slp.events

import altitourney.slp.ServerContext.TournamentPlayer
import altitourney.slp.games.TournamentFactory
import java.util.UUID
import play.api.libs.json.JsValue
import altitourney.slp.SLP

/**
 * {"port":27276,"time":197705,"team0":["8b96d3eb-5eb7-4435-a30e-8d3dd54c97ae"],"type":"tournamentStart","team1":["d0e8ad71-e540-4f5b-bb73-91a8960523de"]}
 */
class TournamentStart(jsVal: JsValue) extends EventHandler(jsVal) {
	val team0 = getTournamentPlayers("team0")
	val team1 = getTournamentPlayers("team1")
	SLP.getLog.debug("Team 0: " + team0.mkString(", "))
	SLP.getLog.debug("Team 1: " + team1.mkString(", "))

	getServerContext.tournamentTeamLists = Some(team0, team1)
	getServerContext.setGameFactory(new TournamentFactory)

	private def getTournamentPlayers(team: String): Set[TournamentPlayer] = {
		(jsVal \ team).as[Seq[JsValue]].map{
			v =>
				val vapor = UUID.fromString(v.as[String])
				TournamentPlayer(vapor, getServerContext.getPlayerName(vapor).getOrElse("NULL"))
		}.toSet
	}
}
