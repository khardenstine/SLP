package altitourney.slp.commands

import altitourney.slp.SLP
import java.io.{File, FileWriter, BufferedWriter}

private class PortedCommandExecutor(commandFile: File, port: Int) extends CommandExecutor {
	private def buildCommand(commandType: String, rawArguments: String*): String = {
		implicit val sb = new StringBuilder
		sb.append(port.toString)
		sb.append(",console,")
		sb.append(commandType)
		rawArguments.foreach{arg =>
			sb.append(" ")
			escape(arg)
		}
		sb.toString()
	}

	// Disallow newlines to avoid command injection
	def escape(in: String)(implicit sb: StringBuilder): Unit = {
		sb.append(in.replaceAll("\\\\", "\\\\\\\\"))
	}

	private def writeCommand(commandType: String, rawArguments: String*): Unit = {
		writeCommands(buildCommand(commandType, rawArguments:_*))
	}

	private def writeCommands(commands: String*): Unit = {
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
			name => buildCommand("assignTeam", "\"" + name + "\"", team.toString)
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
		writeCommand("serverWhisper", "\"" + playerName + "\"", message)
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
}
