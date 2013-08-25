package altitourney.slp.registry

class RegistryFactory {
	def getEventRegistry: EventRegistry = RegistryFactory.eventRegistry
	def getStartUpRegistry: EventRegistry = RegistryFactory.startUpRegistry
	def getConsoleCommandRegistry: EventRegistry = RegistryFactory.consoleCommandRegistry
	def getLadderRegistry: EventRegistry = RegistryFactory.ladderRegistry
}

private object RegistryFactory {
	lazy val eventRegistry = new AllEvents
	lazy val startUpRegistry = new StartUpRegistry
	lazy val consoleCommandRegistry = new ConsoleCommandRegistry
	lazy val ladderRegistry = new LadderRegistry
}
