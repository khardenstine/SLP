package altitourney.slp.events.consoleCommands

import altitourney.slp.events.EventHandler
import altitourney.slp.{SLP, Util}
import play.api.libs.json.JsValue

class AcceptRules(jsVal: JsValue) extends EventHandler(jsVal) {
	val player = getUUID("source")
	val hash = Util.generateHash(player)

	if (hash == (jsVal \ "arguments")(0).as[String]) {
		getCommandExecutor.serverWhisper(getServerContext.getPlayerName(player), "Thank you, enjoy ladder.")
		SLP.preparedStatement(
			"""
			  |UPDATE ratings
			  |SET    accepted_rules = TRUE
			  |WHERE  vapor_id = ?
			  |       AND season_id = Current_ladder_season();
			  |
			  |INSERT INTO ratings
			  |            (vapor_id,
			  |             season_id,
			  |             accepted_rules)
			  |SELECT ?,
			  |       Current_ladder_season(),
			  |       true
			  |WHERE  NOT EXISTS (SELECT 1
			  |                   FROM   ladder_ranks
			  |                   WHERE  vapor_id = ?);
			""".stripMargin
		){
			stmt =>
				stmt.setString(1, player.toString)
				stmt.setString(2, player.toString)
				stmt.setString(3, player.toString)
		}
	} else {
		getCommandExecutor.serverWhisper(getServerContext.getPlayerName(player), "You entered the incorrect pass-phrase.  Did you read the rules (type 'listRules')?")
	}
}
