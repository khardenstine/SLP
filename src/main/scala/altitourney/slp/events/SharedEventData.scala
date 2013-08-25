package altitourney.slp.events

import org.joda.time.DateTime
import java.util.UUID
import com.google.common.collect.HashBiMap
import altitourney.slp.games._
import altitourney.slp.SLP

class SharedEventData(val port: Int, private val startTime: DateTime, val name: String) {
	private val playerMap: HashBiMap[Int, UUID] = HashBiMap.create()
	private val playerNameMap: HashBiMap[UUID, String] = HashBiMap.create()
	private var game: Game = new NoGame
	val commandExecutor = SLP.getCommandExecutorFactory.getCommandExecutor(port)

	def getServerTime(time: Int): DateTime = {
		startTime.withDurationAdded(time.toLong, 1)
	}

	def getPlayer(player: Int): UUID = {
		playerMap.get(player)
	}

	def getPlayerName(player: Int): String = {
		getPlayerName(playerMap.get(player))
	}

	def getPlayerName(vapor: UUID): String = {
		playerNameMap.get(vapor)
	}

	def addPlayer(vapor: UUID, serverPlayer: Int, playerName: String) {
		playerMap.put(serverPlayer, vapor)
		playerNameMap.put(vapor, playerName)
	}

	def removePlayer(serverPlayer: Int) = {
		playerNameMap.remove(playerMap.remove(serverPlayer))
	}

	def updatePlayerName(vapor: UUID, playerName: String) {
		playerNameMap.forcePut(vapor, playerName)
	}

	def clearPlayers() {
		playerMap.clear()
		playerNameMap.clear()
	}

	def extractGame: Game = {
		synchronized{
			val finishedGame = game
			game = new NoGame
			finishedGame
		}
	}

	def getGame: Game = {
		synchronized(
			game
		)
	}

	def setGame(game: Game) {
		synchronized(
			this.game = game
		)
	}
}
