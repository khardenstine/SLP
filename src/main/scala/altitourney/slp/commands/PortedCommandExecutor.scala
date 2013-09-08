package altitourney.slp.commands

import altitourney.slp.SLP
import java.io.{File, FileWriter, BufferedWriter}

private class PortedCommandExecutor(commandFile: File, port: Int) extends CommandExecutor {
	private def buildCommand(commandType: String, rawArguments: String*): String = {
		val arguments = {
			if( rawArguments.length < 1)
				""
			else
				" " + rawArguments.mkString(" ")
		}
		// Disallow newlines to avoid command injection
		(port + ",console," + commandType + arguments).replaceAll("[\r\n]+", "")
	}

	private def writeCommand(commandType: String, rawArguments: String*) {
		writeCommands(buildCommand(commandType, rawArguments:_*))
	}

	private def writeCommands(commands: String*) {
		if (commands.length > 0)
		{
			commands.foreach( command =>
				SLP.getLog.debug(commandFile + ": " + command)
			)
			try {
				val writer = new BufferedWriter(new FileWriter(commandFile, true))
				try {
					commands.foreach{ command =>
						writer.write(command)
						writer.newLine()
					}
					writer.flush()
				}
				finally {
					writer.close()
				}
			} catch {
				case e: Exception => SLP.getLog.error(e)
			}
		}
	}

	private def assignTeam(team: Int, playerNickName: String*) = {
		writeCommands(playerNickName.map{
			name => buildCommand("assignTeam", "\"" + escapeBackSlashes(name) + "\"", team.toString)
		}:_*)
	}

	def assignSpectate(playerNickName: String*): Unit = {
		assignTeam(-1, playerNickName:_*)
	}

	def assignLeftTeam(playerNickName: String*): Unit = {
		assignTeam(0, playerNickName:_*)
	}

	def assignRightTeam(playerNickName: String*): Unit = {
		assignTeam(1, playerNickName:_*)
	}

	def startTournament() {
		writeCommand("startTournament")
	}

	def stopTournament(): Unit = {
		writeCommand("stopTournament")
	}

	def serverWhisper(playerName: String, message: String): Unit = {
		writeCommand("serverWhisper", "\"" + escapeBackSlashes(playerName) + "\"", message)
	}

	def serverWhisper(playerName: Option[String], message: String): Unit = {
		playerName.foreach(serverWhisper(_, message))
	}

	def serverMessage(message: String): Unit = {
		writeCommand("serverMessage", "\"" + message + "\"")
	}

	def serverMessage(e: RuntimeException): Unit = {
		serverMessage(e.getMessage)
	}

	def changeMap(mapName: String): Unit = {
		writeCommand("changeMap", mapName)
	}

	def logServerStatus(): Unit = {
		writeCommand("logServerStatus")
	}

	def escapeBackSlashes(in: String): String = {
		in.replace("\\", "\\\\")
	}
}
