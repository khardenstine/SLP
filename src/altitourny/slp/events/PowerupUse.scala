package altitourny.slp.events

import play.api.libs.json.JsValue

case class PowerupUse(override val jsVal: JsValue) extends EventHandler(jsVal)
{

}

case object PowerupUse extends Event
{
	val logType = "powerupUse"

	def getEventHandler(jsVal: JsValue)
	{
		new PowerupUse(jsVal)
	}
}
