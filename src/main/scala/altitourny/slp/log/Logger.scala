package altitourny.slp.log

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

	private def log(line: String) {
		try {
			val writer = new BufferedWriter(new FileWriter(LOG_FILE, true))
			writer.write(new Date().toString + " " + line)
			writer.newLine()
			writer.close()
		}
		catch {
			case e: IOException => e.printStackTrace()
		}
	}

	def error(line: String) {
		if (logLevel.shouldLog(ERROR)) {
			log("ERROR: " + line)
		}
	}

	def warn(line: String) {
		if (logLevel.shouldLog(WARN)) {
			log("WARN: " + line)
		}
	}

	def info(line: String) {
		if (logLevel.shouldLog(INFO)) {
			log("INFO: " + line)
		}
	}

	def debug(line: String) {
		if (logLevel.shouldLog(DEBUG)) {
			log("DEBUG: " + line)
		}
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
