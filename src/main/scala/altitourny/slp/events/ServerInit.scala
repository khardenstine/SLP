package altitourny.slp.events

import play.api.libs.json.JsValue
import altitourny.slp.SLP

/**
 * {"port":27276,"time":73005,"name":"Altitude Server","type":"serverInit","maxPlayerCount":14}
 */
case class ServerInit(override val jsVal: JsValue) extends AbstractEventHandler(jsVal) {
	val port: Int = getInt("port")

	SLP.initServer(port)

	SLP.getLog.info(getString("name") + " started on port " + port + " at " + getTime)
}

case object ServerInit extends Event {
	val logType = "serverInit"

	def getEventHandler(jsVal: JsValue) : EventHandler = {
		new ServerInit(jsVal)
	}
}
