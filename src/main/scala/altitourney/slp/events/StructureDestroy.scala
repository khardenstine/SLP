package altitourney.slp.events

import play.api.libs.json.JsValue

case class StructureDestroy(override val jsVal: JsValue) extends AbstractEventHandler(jsVal) {

}

case object StructureDestroy extends Event {
	val logType = "structureDestroy"

	def getEventHandler(jsVal: JsValue): EventHandler = {
		new StructureDestroy(jsVal)
	}
}