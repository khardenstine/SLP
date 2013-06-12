package altitourny.slp

// Code adapted from lamster
// Edited by: Michael Solomon
// 2-5-11
//
// Edited by: Karl Hardenstine
// June-2013
object ThreadHelper {
	def sleep(ms: Int) {
		try {
			Thread.sleep(ms)
		}
		catch {
			case e: InterruptedException => {
				SLP.getLog.error(e.getMessage)
				throw e
			}
		}
	}

	def createDaemonThread(runnable: Runnable): Thread = {
		val thread: Thread = new Thread(runnable)
		thread.setDaemon(false)
		thread
	}

	def startDaemonThread(runnable: Runnable): Thread = {
		val thread: Thread = createDaemonThread(runnable)
		thread.start()
		thread
	}

	def `yield`() {
		Thread.`yield`()
	}
}



