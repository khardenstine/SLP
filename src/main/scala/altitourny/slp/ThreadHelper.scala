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

	private def createThread(runnable: Runnable, daemon: Boolean): Thread = {
		val thread: Thread = new Thread(runnable)
		thread.setDaemon(daemon)
		thread
	}

	def startThread(runnable: Runnable): Thread = {
		val thread: Thread = createThread(runnable, false)
		thread.start()
		thread
	}


	def startDaemonThread(runnable: Runnable): Thread = {
		val thread: Thread = createThread(runnable, true)
		thread.start()
		thread
	}

	def `yield`() {
		Thread.`yield`()
	}
}



