package altitourney.slp.events

import altitourney.slp.commands.CommandExecutor
import altitourney.slp.games.IGame
import altitourney.slp.{ServerContext, SLP}
import java.util.UUID
import org.joda.time.DateTime
import play.api.libs.json.JsValue

abstract class EventHandler(jsVal: JsValue) {
	final def getServerContext: ServerContext = {
		SLP.getServerContext(port)
	}

	final def getGame: IGame = {
		getServerContext.getGame
	}

	final def getCommandExecutor: CommandExecutor = {
		getServerContext.commandExecutor
	}

	final implicit val port: Int = getInt("port")

	final def getTime: DateTime = {
		getServerContext.getServerTime(getInt("time"))
	}

	final def getUUIDfromPlayerNumber(name: String): Option[UUID] = {
		val player: Int = getInt(name)
		if (player == -1) {
			None
		}
		else {
			getServerContext.getPlayer(player)
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
