package altitourny.slp.events

import play.api.libs.json.JsValue

case class PowerupDefuse(override val jsVal: JsValue) extends EventHandler(jsVal)
{

}

case object PowerupDefuse extends Event
{
	val logType = "powerupDefuse"

	def getEventHandler(jsVal: JsValue)
	{
		new PowerupDefuse(jsVal)
	}
}
