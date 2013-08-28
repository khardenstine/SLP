package altitourney.slp.events.exceptions

class ServerMessageException(message: String) extends RuntimeException(message)

class NotLobbyException(message: String = "Must be in the lobby to execute this command.") extends ServerMessageException(message)

class NotEnoughPlayers(message: String = "Not enough players.") extends ServerMessageException(message)

class LadderNotConfigured(message: String = "Ladder is not configured on this server.") extends ServerMessageException(message)
