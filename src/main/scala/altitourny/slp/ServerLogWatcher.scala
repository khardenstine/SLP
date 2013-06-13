package altitourny.slp

// Code adapted from lamster
// Edited by: Michael Solomon
// 2-5-11
//
// Edited by: Karl Hardenstine
// June-2013

import altitourny.slp.events.{ServerInit, SessionStart, Events}
import play.api.libs.json.Json
import java.io.BufferedReader
import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.IOException
import java.util.Date

class ServerLogWatcher(val path: String) {
	private var file: File = new File(path)
	file.createNewFile
	private var reader: BufferedReader = new BufferedReader(new FileReader(file))
	private var referenceFileLength: Long = 0L
	private var sourceFileChanged: Boolean = false
	private var running: Boolean = false

	SLP.getLog.debug("Going to initialize server log")
	SLP.getLog.debug("Log file is located at: " + path)

	initializeCheckServerLog()

	private def initializeCheckServerLog() {
		SLP.getLog.debug("About to parse old log")

		{
			var line: String = null
			while ( {
				line = reader.readLine
				line
			} != null) {
				val jsVal = Json.parse(line)
				(jsVal \ "type").as[String] match {
					case SessionStart.logType => SessionStart.getEventHandler(jsVal)
					case ServerInit.logType => ServerInit.getEventHandler(jsVal)
					case _ => {}
				}
			}
		}

		SLP.getLog.debug("Finished parsing old log")
		referenceFileLength = file.length
		SLP.getLog.debug("The reference file has a length of: " + referenceFileLength)
		running = true
		ThreadHelper.startDaemonThread(new Runnable {
			def run() {
				while (running) {
					try {
						checkServerLogForNewData()
						ThreadHelper.sleep(200)
					}
					catch {
						case e: Exception => {
							SLP.getLog.error("Process failed " + e)
						}
					}
				}
			}
		})

		// Callback thread
		ThreadHelper.startDaemonThread(new Runnable {
			def run() {
				while (running) {
					try {
						SLP.callback()
						ThreadHelper.sleep(600000)
					}
					catch {
						case e: Exception => {
							SLP.getLog.error("Process failed " + e)
						}
					}
				}
			}
		})
	}

	private def checkServerLogForNewData() {
		try {
			var line: String = null
			while ( {
				line = reader.readLine
				line
			} != null) {
				Events.handle(Json.parse(line))
			}
		}
		catch {
			case e: Exception => SLP.getLog.error("Failed to read console command: " + e)
		}

		if (sourceFileChanged) {
			sourceFileChanged = false
			if (reader != null) {
				try {
					reader.close()
					SLP.getLog.debug("reader closed")
				}
				catch {
					case e: IOException => SLP.getLog.error("Failed to close reader " + e)
				}
			}
			file = new File(path)
			referenceFileLength = file.length
			SLP.getLog.debug("Initializing reader for " + file + " (" + file.lastModified + ") at " + (new Date).toString)
			try {
				reader = new BufferedReader(new FileReader(file))
			}
			catch {
				case e: FileNotFoundException => SLP.getLog.error("Failed to create reader for " + file + " " + e)
			}
		}
		val newLength: Long = file.length
		referenceFileLength = Math.max(referenceFileLength, newLength)
		if (newLength < referenceFileLength) {
			SLP.getLog.debug("The reference file has changed")
			sourceFileChanged = true
		}
	}

	def shutdown() {
		running = false
	}
}
