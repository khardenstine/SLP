package altitourney.slp.commands

import java.io.File

class CommandExecutorFactory(logFileLocation: String) {
	private final val COMMAND_FILE: File = new File(logFileLocation)

	def getCommandExecutor(port: Int): CommandExecutor = {
		new PortedCommandExecutor(COMMAND_FILE, port)
	}
}
