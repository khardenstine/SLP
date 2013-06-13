package altitourny.slp.events

import org.joda.time.DateTime
import altitourny.slp.SLP
import java.util.UUID
import play.api.libs.json.JsValue

object Events {
	val ALL: Seq[Event] = Seq(
		Assist,
		ClientAdd,
		ClientNicknameChange,
		ClientRemove,
		ConsoleCommandExecute,
		Goal,
		Kill,
		MapChange,
		MapLoading,
		PingSummary,
		PowerupAutoUse,
		PowerupDefuse,
		PowerupPickup,
		PowerupUse,
		RoundEnd,
		ServerInit,
		ServerStart,
		SessionStart,
		Spawn,
		StructureDamage,
		StructureDestroy,
		TeamChange
	)

	def handle(jsVal: JsValue) {
		// Ignore bot events
		// this isnt correct
		//val vaporId = (jsVal \ "vaporId").as[String]
		//if (vaporId == JsUndefined || vaporId != "00000000-0000-0000-0000-000000000000")
		try {
			SLP.getLog.debug("Handling event: " + jsVal \ "type")
			ALL.map(_.handle(jsVal))
		}
		catch {
			case e: Exception => SLP.getLog.error(e)
		}

	}
}

trait EventHandler

abstract class AbstractEventHandler(val jsVal: JsValue) extends EventHandler {
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

trait Event {
	val logType: String

	def getEventHandler(jsVal: JsValue): EventHandler

	final def handle(jsVal: JsValue) {
		if ((jsVal \ "type").as[String] == this.logType) {
			getEventHandler(jsVal)
		}
	}
}
