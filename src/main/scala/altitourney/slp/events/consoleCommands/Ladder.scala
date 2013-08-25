package altitourney.slp.events.consoleCommands

import play.api.libs.json.JsValue
import altitourney.slp.events.{LobbyHandler, EventHandler}
import altitourney.slp.SLP

/**
 * {"port":27276,"time":22428,"arguments":["start_random"],"source":"79b7a12f-12b4-46ab-adae-580131833b88","command":"ladder","group":"Administrator","type":"consoleCommandExecute"}
 */
class Ladder(jsVal: JsValue) extends EventHandler(jsVal) {
	SLP.getRegistryFactory.getLadderRegistry.handle(jsVal)
}

class StartRandom(jsVal: JsValue) extends LobbyHandler(jsVal) {
	val playerList = getSharedEventData.getGame.listPlayers
	if (playerList.size < mode.minSize)
	{
		errrrr
	}
	//	create two teams
	//	assign teams
	//	start tournament
	//	pick map
	//	changemap
}
