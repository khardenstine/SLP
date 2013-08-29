package altitourney.slp.log

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.Date

// This class takes in various pieces of information
// and outputs it to a log file
//
// Code adapted from Michael Solomon
// Edited by: Karl Hardenstine
// June-2013
class Logger(logFileLocation: String, logLevel: LogLevel) {
	private final val LOG_FILE: File = new File(logFileLocation)

	private def log(level: LogLevel, line: String) {
		if (logLevel.shouldLog(level)) {
			try {
				val writer = new BufferedWriter(new FileWriter(LOG_FILE, true))
				writer.write(new Date().toString + " [" + level.toString + "] " + line)
				writer.newLine()
				writer.close()
			}
			catch {
				case e: IOException => e.printStackTrace()
			}
		}
	}

	def error(line: String) {
		log(ERROR, line)
	}

	def error(e: Throwable) {
		error(e, e.getMessage)
	}

	def error(e: Throwable, message: String) {
		error(message + "\n\t\t" + e.getStackTrace.map(_.toString).mkString("\n\t\t"))
	}

	def warn(line: String) {
		log(WARN, line)
	}

	def info(line: String) {
		log(INFO, line)
	}

	def debug(line: String) {
		log(DEBUG, line)
	}

	def clean() {
		try {
			val writer = new BufferedWriter(new FileWriter(LOG_FILE))
			writer.close()
		}
		catch {
			case e: IOException => e.printStackTrace()
		}
	}
}
