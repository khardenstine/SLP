package altitourny.slp.events

import play.api.libs.json.JsValue

case class PowerupAutoUse(override val jsVal: JsValue) extends AbstractEventHandler(jsVal) {

}

case object PowerupAutoUse extends Event {
	val logType = "powerupAutoUse"

	def getEventHandler(jsVal: JsValue): EventHandler = {
		new PowerupAutoUse(jsVal)
	}
}
