package altitourney.slp

import altitourney.slp.games.{Mode, Game, NoGame}
import com.google.common.collect.HashBiMap
import java.util.UUID
import org.joda.time.DateTime
import com.typesafe.config.{ConfigException, Config}

class ServerContext(val config: Config, val port: Int, private val startTime: DateTime, val name: String) {
	private val playerMap: HashBiMap[Int, UUID] = HashBiMap.create()
	private val playerNameMap: HashBiMap[UUID, String] = HashBiMap.create()
	private var game: Game = new NoGame
	val commandExecutor = SLP.getCommandExecutorFactory.getCommandExecutor(port)

	def getLadderMode: Option[Mode] = {
		try
		{
			Mode.withName(config.getString("ladder.mode"))
		} catch {
			case e: ConfigException.Missing => None
		}
	}

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
