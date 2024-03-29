package altitourney.slp.events

import altitourney.slp.events.exceptions.NotLobbyException
import play.api.libs.json.JsValue

abstract class LobbyHandler(jsVal: JsValue) extends EventHandler(jsVal) {
	if (getGame.map != getServerContext.getLobbyMap)
	{
		throw new NotLobbyException
	}
}
