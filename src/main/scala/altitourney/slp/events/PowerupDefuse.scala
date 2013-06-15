package altitourney.slp.events

import play.api.libs.json.JsValue

case class PowerupDefuse(override val jsVal: JsValue) extends AbstractEventHandler(jsVal) {

}

case object PowerupDefuse extends Event {
	val logType = "powerupDefuse"

	def getEventHandler(jsVal: JsValue): EventHandler = {
		new PowerupDefuse(jsVal)
	}
}
