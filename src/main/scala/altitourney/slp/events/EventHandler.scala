package altitourney.slp.events

import org.joda.time.DateTime
import play.api.libs.json.JsValue
import altitourney.slp.SLP
import java.util.UUID

abstract class EventHandler(jsVal: JsValue) {
	final def getSharedEventData: SharedEventData = {
		SLP.getSharedEventData(port)
	}

	final val port: Int = getInt("port")

	final def getTime: DateTime = {
		getSharedEventData.getServerTime(getInt("time"))
	}

	final def getUUIDfromPlayerNumber(name: String): Option[UUID] = {
		val player: Int = getInt(name)
		if (player == -1) {
			None
		}
		else {
			Some(getSharedEventData.getPlayer(player))
		}
	}

	final def getUUID(name: String): UUID = {
		UUID.fromString((jsVal \ name).as[String])
	}

	final def getInt(name: String): Int = {
		(jsVal \ name).as[Int]
	}

	final def getString(name: String): String = {
		(jsVal \ name).as[String]
	}
}
