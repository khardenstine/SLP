package altitourney.slp.events

import altitourney.slp.structures.Target
import play.api.libs.json.JsValue

/**
 * {"port":27276,"time":588131,"player":0,"target":"turret","xp":10,"type":"structureDestroy"}
 * {"port":27276,"time":611567,"player":0,"target":"base","xp":30,"type":"structureDestroy"}
 */
class StructureDestroy(jsVal: JsValue) extends EventHandler(jsVal) {
	val target = getString("target")
	getGame.structureDestroy(
		getUUIDfromPlayerNumber("player"),
		Target.withName(target),
		getInt("xp")
	)
}
