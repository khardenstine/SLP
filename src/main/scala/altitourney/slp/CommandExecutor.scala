package altitourney.slp

import java.io.{File, FileWriter, BufferedWriter}

class CommandExecutor(logFileLocation: String) {
	private final val COMMAND_FILE: File = new File(logFileLocation)

	private def writeCommand(port: Int, commandType: String, rawArguments: String*) {
		// Disallow newlines to avoid command injection
		val arguments = rawArguments.mkString(" ").replaceAll("[\r\n]+", "")
		val line = {
			port + ",console," + commandType + {
				if( arguments.length < 1)
					""
				else
					" " + arguments
			}
		}

		SLP.getLog.debug(COMMAND_FILE + ": " + line)
		try {
			val writer = new BufferedWriter(new FileWriter(COMMAND_FILE, true))
			try {
				writer.write(line)
				writer.newLine()
				writer.flush()
			}
			finally {
				writer.close()
			}
		} catch {
			case e: Exception => SLP.getLog.error(e, "Failed to execute: '" + line + "'")
		}
	}

	private def assignTeam(port: Int, playerNickName: String, team: Int) = {
		writeCommand(port, "assignTeam", playerNickName, team.toString)
	}

	def assignSpectate(port: Int, playerNickName: String) = {
		assignTeam(port, playerNickName, -1)
	}

	def assignLeftTeam(port: Int, playerNickName: String) = {
		assignTeam(port, playerNickName, 0)
	}

	def assignRightTeam(port: Int, playerNickName: String) = {
		assignTeam(port, playerNickName, 1)
	}

	def startTournament(port: Int) = {
		writeCommand(port, "startTournament")
	}

	def stopTournament(port: Int) = {
		writeCommand(port, "stopTournament")
	}

	def serverMessage(port: Int, message: String) {
		writeCommand(port, "serverMessage", message)
	}

	def serverMessage(port: Int, e: RuntimeException) {
		serverMessage(port, e.getMessage)
	}
}
