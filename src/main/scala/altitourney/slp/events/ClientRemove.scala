package altitourney.slp.events

import play.api.libs.json.JsValue

/**
 * {"port":27276,"message":"left","time":105531995,"player":2,"reason":"Client left.","nickname":"{ball}Carlos",
 * "vaporId":"79b7a12f-12b4-46ab-adae-580131833b88","type":"clientRemove","ip":"192.168.1.2:27272"}
 */
class ClientRemove(jsVal: JsValue) extends EventHandler(jsVal) {
	getSharedEventData.removePlayer(getInt("player"))
}
