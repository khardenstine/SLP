package altitourney.slp.events.exceptions

import java.util.UUID
import altitourney.slp.commands.CommandExecutor

abstract class ConsoleCommandException(message: String) extends RuntimeException(message) {
	def propagate(commandExecutor: CommandExecutor)
}

class ServerMessageException(message: String) extends ConsoleCommandException(message) {
	def propagate(commandExecutor: CommandExecutor) {
		commandExecutor.serverMessage(message)
	}
}

class ServerWhisperException(playerName: String, message: String) extends ConsoleCommandException(message) {
	def propagate(commandExecutor: CommandExecutor) {
		commandExecutor.serverWhisper(playerName, message)
	}
}

class NotLobbyException(message: String = "Must be in the lobby to execute this command.") extends ServerMessageException(message)

class NotEnoughPlayers(message: String = "Not enough players.") extends ServerMessageException(message)

class LadderNotConfigured(message: String = "Ladder is not configured on this server.") extends ServerMessageException(message)

class InvalidMapSelection(maps: Seq[String]) extends ServerMessageException("You have selected an invalid map. Please choose from: " + maps.mkString(","))
