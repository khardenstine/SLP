package altitourney.slp

// Code adapted from lamster
// Edited by: Michael Solomon
// 2-5-11
//
// Edited by: Karl Hardenstine
// June-2013

import java.io._
import scala.io.BufferedSource

class ServerLogWatcher(val path: String) {
	var watcher = new Watcher(path, true)

	SLP.getLog.debug("Parsing old log")
	SLP.getRegistryFactory.getStartUpRegistry.handleIterator(watcher.getLines)
	SLP.getLog.debug("Finished parsing old log")
	SLP.getLog.debug("The reference file has a length of: " + watcher.getReferenceFileLength)

	def checkServerLogForNewData() {
		SLP.getRegistryFactory.getEventRegistry.handleIterator(watcher.getLines)

		watcher = watcher.setReferenceFileLength()
	}
}


class Watcher(val path: String, val createFile: Boolean) {
	private val file: File = new File(path)
	if (createFile) { file.createNewFile() }
	SLP.getLog.debug("Initializing reader for " + file + " (" + file.lastModified + ")")
	private val reader: BufferedSource = {
		try {
			scala.io.Source.fromFile(file)
		}
		catch {
			case e: FileNotFoundException => {
				SLP.getLog.error(e, "Failed to create reader for " + file)
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
				SLP.getLog.debug("Reader closed")
			}
			catch {
				case e: IOException => SLP.getLog.error(e, "Failed to close reader.")
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