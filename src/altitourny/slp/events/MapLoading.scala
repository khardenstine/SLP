package altitourny.slp.events

import play.api.libs.json.JsValue

case class MapLoading(override val jsVal: JsValue) extends EventHandler(jsVal)
{

}

case object MapLoading extends Event
{
	val logType = "mapLoading"

	def getEventHandler(jsVal: JsValue)
	{
		new MapLoading(jsVal)
	}
}
