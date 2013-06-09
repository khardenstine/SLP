package altitourny.slp.events

import play.api.libs.json.JsValue

case class StructureDamage(override val jsVal: JsValue) extends EventHandler(jsVal)
{

}

case object StructureDamage extends Event
{
	val logType = "structureDamage"

	def getEventHandler(jsVal: JsValue)
	{
		new StructureDamage(jsVal)
	}
}
