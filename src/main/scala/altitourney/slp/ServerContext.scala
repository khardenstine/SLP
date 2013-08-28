package altitourney.slp

import altitourney.slp.games.{GameFactory, StandardFactory, Mode, Game}
import com.google.common.collect.HashBiMap
import com.typesafe.config.Config
import java.util.UUID
import org.joda.time.DateTime
import scala.util.Try

class ServerContext(val config: Config, val port: Int, private val startTime: DateTime, val name: String) {
	private val lobbyMap: String = config.getString("lobby.map")
	private val playerMap: HashBiMap[Int, UUID] = HashBiMap.create()
	private val playerNameMap: HashBiMap[UUID, String] = HashBiMap.create()
	val commandExecutor = SLP.getCommandExecutorFactory.getCommandExecutor(port)
	private var gameFactory: GameFactory = StandardFactory
	private var game: Game = gameFactory.buildNoGame()

	def setGameFactory(gameFactory: GameFactory): Unit = {
		synchronized(
			this.gameFactory = gameFactory
		)
	}

	def getLadderMode: Try[Mode] = Try {
		Mode.withName(config.getString("ladder.mode"))
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
			gameFactory = StandardFactory
			game = gameFactory.buildNoGame()
			finishedGame
		}
	}

	def getGame: Game = {
		synchronized(
			game
		)
	}

	def newGame(mode: Mode, dateTime: DateTime, map: String, leftTeamId: Int, rightTeamId: Int) {
		synchronized(
			game = {
				if (map == getLobbyMap) {
					gameFactory.buildNoGame(dateTime, map, leftTeamId, rightTeamId)
				} else {
					gameFactory.build(mode, dateTime, map, leftTeamId, rightTeamId)
				}
			}
		)
	}

	def getLobbyMap = lobbyMap
}
