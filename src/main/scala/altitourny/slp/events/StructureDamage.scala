package altitourny.slp.events

import play.api.libs.json.JsValue

case class StructureDamage(override val jsVal: JsValue) extends AbstractEventHandler(jsVal) {

}

case object StructureDamage extends Event {
	val logType = "structureDamage"

	def getEventHandler(jsVal: JsValue): EventHandler = {
		new StructureDamage(jsVal)
	}
}
