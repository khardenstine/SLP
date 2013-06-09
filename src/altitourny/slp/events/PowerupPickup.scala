package altitourny.slp.events

import play.api.libs.json.JsValue

case class PowerupPickup(override val jsVal: JsValue) extends EventHandler(jsVal)
{

}

case object PowerupPickup extends Event
{
	val logType = "powerupPickup"

	def getEventHandler(jsVal: JsValue)
	{
		new PowerupPickup(jsVal)
	}
}
