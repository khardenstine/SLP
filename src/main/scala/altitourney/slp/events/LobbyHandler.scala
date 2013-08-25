package altitourney.slp.events

import altitourney.slp.events.exceptions.NotLobbyException
import play.api.libs.json.JsValue
import altitourney.slp.SLP

abstract class LobbyHandler(jsVal: JsValue) extends EventHandler(jsVal) {
	if (getSharedEventData.getGame.map != SLP.getLobbyMap)
	{
		throw new NotLobbyException("Must be in the lobby to execute this command.")
	}
}
