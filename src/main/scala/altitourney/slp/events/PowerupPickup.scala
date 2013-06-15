package altitourney.slp.events

import play.api.libs.json.JsValue

case class PowerupPickup(override val jsVal: JsValue) extends AbstractEventHandler(jsVal) {

}

case object PowerupPickup extends Event {
	val logType = "powerupPickup"

	def getEventHandler(jsVal: JsValue): EventHandler = {
		new PowerupPickup(jsVal)
	}
}
