package altitourney.slp.events

import play.api.libs.json.JsValue

/**
 * {"port":27276,"time":13919,"plane":"Explodet","player":5,"perkRed":"Remote Mine","perkGreen":"Flexible Wings",
 * "team":3,"type":"spawn","perkBlue":"Turbocharger","skin":"No Skin"}
 */
class Spawn(jsVal: JsValue) extends EventHandler(jsVal) {
	 getSharedEventData.getGame.spawn(getUUIDfromPlayerNumber("player").getOrElse(throw new RuntimeException("UUID not found")), getString("perkRed").replace(" ", "_"), getTime)
}
