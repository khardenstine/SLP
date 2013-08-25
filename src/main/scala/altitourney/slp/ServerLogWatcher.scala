package altitourney.slp

// Code adapted from lamster
// Edited by: Michael Solomon
// 2-5-11
//
// Edited by: Karl Hardenstine
// June-2013

import play.api.libs.json.Json
import java.io._
import java.util.Date
import scala.io.BufferedSource

class ServerLogWatcher(val path: String) {
	SLP.getLog.debug("Going to initialize server log")
	SLP.getLog.debug("Log file is located at: " + path)
	var watcher = new Watcher(path, true)

	SLP.getLog.debug("About to parse old log")
	watcher.getLines.foreach {
		line => SLP.getRegistryFactory.getStartUpRegistry.handle(Json.parse(line))
	}
	SLP.getLog.debug("Finished parsing old log")
	SLP.getLog.debug("The reference file has a length of: " + watcher.getReferenceFileLength)

	def checkServerLogForNewData() {
		try {
			watcher.getLines.foreach {
				line =>
					SLP.getRegistryFactory.getStartUpRegistry.handle(Json.parse(line))
			}
		}
		catch {
			case e: Exception => SLP.getLog.error("Failed to read console command: " + e)
		}

		watcher = watcher.setReferenceFileLength()
	}
}


class Watcher(val path: String, val createFile: Boolean) {
	private val file: File = new File(path)
	if (createFile) { file.createNewFile() }
	SLP.getLog.debug("Initializing reader for " + file + " (" + file.lastModified + ") at " + (new Date).toString)
	private val reader: BufferedSource = {
		try {
			scala.io.Source.fromFile(file)
		}
		catch {
			case e: FileNotFoundException => {
				SLP.getLog.error("Failed to create reader for " + file + " " + e)
				throw e
			}
		}
	}
	private var referenceFileLength: Long = file.length()

	def setReferenceFileLength() : Watcher = {
		val newLength: Long = file.length()

		if (newLength < Math.max(referenceFileLength, newLength)) {
			SLP.getLog.debug("The reference file has changed")

			try {
				reader.close()
				SLP.getLog.debug("reader closed")
			}
			catch {
				case e: IOException => SLP.getLog.error("Failed to close reader " + e)
			}

			new Watcher(path, false)
		}
		else
		{
			referenceFileLength = newLength
			this
		}
	}

	def getReferenceFileLength : Long = referenceFileLength

	def getLines: Iterator[String] = {
		reader.getLines()
	}
}