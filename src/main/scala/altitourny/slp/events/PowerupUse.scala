package altitourny.slp.events

import play.api.libs.json.JsValue

case class PowerupUse(override val jsVal: JsValue) extends AbstractEventHandler(jsVal) {

}

case object PowerupUse extends Event {
	val logType = "powerupUse"

	def getEventHandler(jsVal: JsValue) : EventHandler = {
		new PowerupUse(jsVal)
	}
}
