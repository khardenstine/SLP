package altitourny.slp.events

import play.api.libs.json.JsValue

case class MapLoading(override val jsVal: JsValue) extends AbstractEventHandler(jsVal) {

}

case object MapLoading extends Event {
	val logType = "mapLoading"

	def getEventHandler(jsVal: JsValue) : EventHandler = {
		new MapLoading(jsVal)
	}
}
