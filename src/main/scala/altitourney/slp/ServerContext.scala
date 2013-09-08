package altitourney.slp

import altitourney.slp.games.{GameFactory, LadderFactory, StandardFactory, Mode, Game}
import com.google.common.collect.HashBiMap
import com.typesafe.config.Config
import java.util.UUID
import org.joda.time.DateTime
import scala.util.Try
import java.util.concurrent.locks.ReentrantLock

class ServerContext(config: Config, val port: Int, startTime: DateTime, val name: String) {
	private val lobbyMap: String = config.getString("lobby.map")
	private val playerMap: HashBiMap[Int, UUID] = HashBiMap.create()
	private val playerNameMap: HashBiMap[UUID, String] = HashBiMap.create()
	val commandExecutor = SLP.getCommandExecutorFactory.getCommandExecutor(port)
	private var gameFactory: GameFactory = StandardFactory
	private var game: Game = gameFactory.buildNoGame()
	var tournamentTeamLists: Option[(Set[UUID], Set[UUID])] = None

	def setGameFactory(gameFactory: GameFactory): Unit = {
		synchronized(
			// If it is already a LadderFactory, we don't want to override it.
			// This is because StartTournament could be called after we establish the LadderFactory
			// and we don't want a TournamentFactory in that case
			this.gameFactory match {
				case f: LadderFactory => {
					SLP.getLog.debug("Setting ladder factory.")
				}
				case _ => this.gameFactory = gameFactory
					SLP.getLog.debug("Setting game factory.")
			}
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

		val shouldPlay = teams._1 ++ teams._2

		def getShouldSpec: Set[UUID] = {
			val activePlayers = tournamentTeamLists match {
				case None => getGame.listActivePlayers
				case Some(tl) => tl._1 ++ tl._2
			}
			activePlayers.filterNot(shouldPlay.contains)
		}

		var sleep = true
		val lock = new ReentrantLock()
		val lockCondition = lock.newCondition()
		val tournamentStartListener = (port, "tournamentStart", () => {
				sleep = false
				lockCondition.signal()
			}
		)

		while (tournamentTeamLists.forall( tl => tl._1.diff(teams._1).size > 0 || tl._2.diff(teams._2).size > 0)) {
			sleep = true
			SLP.getRegistryFactory.getEventRegistry.addPortedEventListener(tournamentStartListener)
			commandExecutor.stopTournament()
			commandExecutor.assignLeftTeam(teams._1:_*)
			commandExecutor.assignRightTeam(teams._2:_*)
			commandExecutor.assignSpectate(getShouldSpec:_*)
			commandExecutor.startTournament()

			lock.lock()
			try {
				while(sleep) {
					lockCondition.await()
				}
			} finally {
				lock.unlock()
			}
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
