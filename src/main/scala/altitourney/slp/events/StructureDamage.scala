package altitourney.slp.events

import altitourney.slp.structures.Target
import play.api.libs.json.JsValue

/**
 * {"port":27276,"time":576638,"player":0,"target":"base","xp":31,"type":"structureDamage"}
 * {"port":27276,"time":579535,"player":0,"target":"turret","xp":2,"type":"structureDamage"}
 */
class StructureDamage(jsVal: JsValue) extends EventHandler(jsVal) {
	val target = getString("target")
	getGame.structureDamage(
		getUUIDfromPlayerNumber("player"),
		Target.withName(target),
		getInt("xp")
	)
}
