package altitourney.slp

import altitourney.slp.games.{GameFactory, StandardFactory, Mode, Game}
import com.google.common.collect.HashBiMap
import com.typesafe.config.Config
import java.util.UUID
import org.joda.time.DateTime
import scala.util.Try

class ServerContext(config: Config, val port: Int, startTime: DateTime, val name: String) {
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

	lazy val getLadderMode: Try[Mode] = Try {
		Mode.withName(config.getString("ladder.mode"))
	}

	def getServerTime(time: Int): DateTime = {
		startTime.withDurationAdded(time.toLong, 1)
	}

	def getPlayer(player: Int): UUID = {
		playerMap.get(player)
	}

	def getPlayerUUID(playerName: String): Option[UUID] = {
		Option(playerNameMap.inverse().get(playerName))
	}

	def getPlayerName(player: Int): String = {
		getPlayerName(playerMap.get(player))
	}

	def getPlayerName(vapor: UUID): String = {
		playerNameMap.get(vapor)
	}

	def getPlayerNames(vapors: Iterable[UUID]): Seq[String] = {
		vapors.map(getPlayerName).toSeq
	}

	def assignTeams(teams: (Set[UUID], Set[UUID])): Unit = {
		implicit def uuids2Names(uuids: Iterable[UUID]): Seq[String] = getPlayerNames(uuids)

		def getShouldSpec: Set[UUID] = {
			getGame.listActivePlayers.filter(uuid => !(teams._1 ++ teams._2).contains(uuid))
		}

		commandExecutor.assignLeftTeam(teams._1:_*)
		commandExecutor.assignRightTeam(teams._2:_*)
		commandExecutor.assignSpectate(getShouldSpec:_*)
		commandExecutor.startTournament()

		// TODO should also check that left and right team are correct so no 7v5s
		while (getShouldSpec.size > 0) {
			commandExecutor.stopTournament()
			commandExecutor.assignSpectate(getShouldSpec:_*)
			commandExecutor.startTournament()
			Thread.sleep(50)
		}

		commandExecutor.startTournament()
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
			game = gameFactory.build(mode, dateTime, map, leftTeamId, rightTeamId)
		)
	}

	def getLobbyMap = lobbyMap
}
