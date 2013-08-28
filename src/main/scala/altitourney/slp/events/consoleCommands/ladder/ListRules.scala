package altitourney.slp.events.consoleCommands.ladder

import altitourney.slp.events.EventHandler
import play.api.libs.json.JsValue

class ListRules(jsVal: JsValue) extends EventHandler(jsVal) {
	val rules = Seq(
		"TODO",
		"Enter the command 'ladder rules accept' to acknowledge your acceptance."
	)
	// TODO investigate if custom arguments can be appended to commands
	// So we can generate a hash on the fly that the player would enter
	// after 'ladder rules accept' to prove they read the rules
	val player = getUUID("source")
	rules.foreach{ rule =>
		getCommandExecutor.serverWhisper(getServerContext.getPlayerName(player), "")
		Thread.sleep(1500) //TODO investigate if this is an appropriate spacing
	}
	//getServerContext.playerCanAccept(player)
}
