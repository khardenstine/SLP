package altitourney.slp.events

import altitourney.slp.SLP
import altitourney.slp.events.exceptions.NotLobbyException
import play.api.libs.json.JsValue

abstract class LobbyHandler(jsVal: JsValue) extends EventHandler(jsVal) {
	if (getSharedEventData.getGame.map != SLP.getLobbyMap)
	{
		throw new NotLobbyException
	}
}
