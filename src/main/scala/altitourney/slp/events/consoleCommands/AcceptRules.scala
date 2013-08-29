package altitourney.slp.events.consoleCommands

import altitourney.slp.{SLP, Util}
import altitourney.slp.events.EventHandler
import play.api.libs.json.JsValue

class AcceptRules(jsVal: JsValue) extends EventHandler(jsVal) {
	val player = getUUID("source")
	val hash = Util.generateHash(player)

	if (hash == (jsVal \ "arguments")(0).as[String]) {
		getCommandExecutor.serverWhisper(getServerContext.getPlayerName(player), "Thank you, enjoy ladder.")
		SLP.preparedStatement(
			"""
			  |UPDATE players
			  |SET accepted_rules = TRUE
			  |WHERE vapor_id = ?;
			""".stripMargin
		)(_.setString(1, player.toString))
	} else {
		getCommandExecutor.serverWhisper(getServerContext.getPlayerName(player), "You entered the incorrect pass-phrase.  Did you read the rules (type 'listRules')?")
	}
}
