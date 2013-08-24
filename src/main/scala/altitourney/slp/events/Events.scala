package altitourney.slp.events

import org.joda.time.DateTime
import altitourney.slp.SLP
import java.util.UUID
import play.api.libs.json.JsValue

object Events {
	private type REGISTER = (String, (JsValue) => EventHandler)

	val START_UP_REGISTRY: Seq[REGISTER] = Seq(
		("serverStart",				new ServerStart(_)),
		("sessionStart",			new SessionStart(_))
	)

	val EVENT_REGISTRY: Seq[REGISTER] =
		START_UP_REGISTRY ++ Seq(
		("assist",					new Assist(_)),
		("clientAdd",				new ClientAdd(_)),
		("clientNicknameChange",	new ClientNicknameChange(_)),
		("clientRemove",			new ClientRemove(_)),
		("consoleCommandExecute",	new ConsoleCommandExecute(_)),
		("gaol",					new Goal(_)),
		("kill",					new Kill(_)),
		("mapChange",				new MapChange(_)),
		("mapLoading",				new MapLoading(_)),
		("pingSummary",				new PingSummary(_)),
		("powerupAutoUse",			new PowerupAutoUse(_)),
		("powerupDefuse",			new PowerupDefuse(_)),
		("powerupPickup",			new PowerupPickup(_)),
		("powerupUse",				new PowerupUse(_)),
		("roundEnd",				new RoundEnd(_)),
		("serverInit",				new ServerInit(_)),
		("spawn",					new Spawn(_)),
		("structureDamage",			new StructureDamage(_)),
		("structureDestroy",		new StructureDestroy(_)),
		("teamChange",				new TeamChange(_))
	)

	def handle(jsVal: JsValue, registry: Seq[REGISTER] = EVENT_REGISTRY) {
		// Ignore bot events
		// this isnt correct
		//val vaporId = (jsVal \ "vaporId").as[String]
		//if (vaporId == JsUndefined || vaporId != "00000000-0000-0000-0000-000000000000")
		try {
			registry
				.filter(_._1 == jsVal \ "type")
				.foreach{ e =>
					SLP.getLog.debug("Handling event: " + jsVal \ "type")
					e._2(jsVal)
				}
		}
		catch {
			case e: Exception => SLP.getLog.error(e)
		}

	}
}

abstract class EventHandler(jsVal: JsValue) {
	final def getSharedEventData: SharedEventData = {
		SLP.getSharedEventData((jsVal \ "port").as[Int])
	}

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
