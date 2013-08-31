package altitourney.slp.commands

abstract class CommandExecutor {
	def assignSpectate(playerNickName: String*): Unit
	def assignLeftTeam(playerNickName: String*): Unit
	def assignRightTeam(playerNickName: String*): Unit
	def startTournament(): Unit
	def stopTournament(): Unit
	def serverWhisper(playerName: String, message: String): Unit
	def serverMessage(message: String): Unit
	def serverMessage(e: RuntimeException): Unit
	def changeMap(mapName: String): Unit
	def logServerStatus(): Unit
}

