package altitourny.slp.events

import play.api.libs.json.JsValue

case class StructureDestroy(override val jsVal: JsValue) extends EventHandler(jsVal)
{

}

case object StructureDestroy extends Event
{
	val logType = "structureDestroy"

	def getEventHandler(jsVal: JsValue)
	{
		new StructureDestroy(jsVal)
	}
}
