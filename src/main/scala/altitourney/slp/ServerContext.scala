package altitourney.slp

import altitourney.slp.ServerContext.TournamentPlayer
import altitourney.slp.games.{Game, IGame, Ladder, GameFactory, LadderFactory, StandardFactory, Mode}
import com.google.common.collect.HashBiMap
import com.typesafe.config.Config
import java.util.UUID
import java.util.concurrent.CountDownLatch
import org.joda.time.DateTime
import scala.collection.mutable
import scala.util.Try

class ServerContext(config: Config, val port: Int, startTime: DateTime, val name: String) {
	private val lobbyMap: String = config.getString("lobby.map")
	private val playerMap: HashBiMap[Int, UUID] = HashBiMap.create()
	private val playerNameMap = new mutable.HashMap[UUID, String]
	val commandExecutor = SLP.getCommandExecutorFactory.getCommandExecutor(port)
	private var gameFactory: GameFactory = StandardFactory
	private var game: Game = gameFactory.buildNoGame()
	private var tournamentTeamLists: Option[(Set[TournamentPlayer], Set[TournamentPlayer])] = None
	private var priority2: Set[UUID] = Set.empty

	def getSecondPriorityPlayers: Set[UUID] = {
		priority2
	}

	def getTournamentTeamLists: Option[(Set[TournamentPlayer], Set[TournamentPlayer])] = {
		tournamentTeamLists
	}

	def clearTournamentTeamLists(): Unit = {
		tournamentTeamLists = None
	}

	def setTournamentTeamLists(_1: Set[TournamentPlayer], _2: Set[TournamentPlayer]): Unit = {
		tournamentTeamLists = Some(_1, _2)
	}

	def getTournamentPlayerName(player: TournamentPlayer): String = {
		getPlayerName(player.vaporId).getOrElse(player.name)
	}

	def setGameFactory(gameFactory: GameFactory): Unit = {
		synchronized(
			// If it is already a LadderFactory, we don't want to override it.
			// This is because StartTournament could be called after we establish the LadderFactory
			// and we don't want a TournamentFactory in that case
			this.gameFactory match {
				case f: LadderFactory => {
					SLP.getLog.debug("Not overwriting ladder factory.")
				}
				case _ => this.gameFactory = gameFactory
					SLP.getLog.debug("Overwriting game factory.")
			}
		)
	}

	lazy val getLadderMode: Try[Mode] = Try {
		Mode.withName(config.getString("ladder.mode"))
	}

	def serverWhisper(vapor: UUID, message: String): Unit = {
		commandExecutor.serverWhisper(getPlayerName(vapor), message)
	}

	def getServerTime(time: Int): DateTime = {
		startTime.withDurationAdded(time.toLong, 1)
	}

	def getPlayer(player: Int): Option[UUID] = {
		Option(playerMap.get(player))
	}

	def getPlayerUUID(playerName: String): Option[UUID] = {
		playerNameMap.toSeq.map(_.swap).toMap.get(playerName)
	}

	def getPlayerName(player: Int): Option[String] = {
		for {
			uuid <- getPlayer(player)
			name <- getPlayerName(uuid)
		} yield name
	}

	def getPlayerName(vapor: UUID): Option[String] = {
		playerNameMap.get(vapor)
	}

	def getPlayerNames(vapors: Iterable[UUID]): Seq[Option[String]] = {
		vapors.map(getPlayerName).toSeq
	}

	def assignTeams(teams: (Set[UUID], Set[UUID])): Unit = {
		implicit def uuids2Names(uuids: Iterable[UUID]): Seq[String] = getPlayerNames(uuids).flatten

		val shouldPlay = teams._1 ++ teams._2

		def getShouldSpec: Set[UUID] = {
			val activePlayers = tournamentTeamLists match {
				case None => getGame.listActivePlayers
				case Some(tl) => tl._1.map(_.vaporId) ++ tl._2.map(_.vaporId)
			}
			activePlayers.filterNot(shouldPlay.contains)
		}

		var sleep = true
		val latch = new CountDownLatch(1)
		val tournamentStartListener = (port, "tournamentStart", () => {
			sleep = false
			latch.countDown()
		})

		while (tournamentTeamLists.forall( tl => tl._1.map(_.vaporId) != teams._1 || tl._2.map(_.vaporId) != teams._2)) {
			sleep = true

			SLP.getRegistryFactory.getEventRegistry.addPortedEventListener(tournamentStartListener)
			commandExecutor.stopTournament()
			commandExecutor.assignLeftTeam(teams._1:_*)
			commandExecutor.assignRightTeam(teams._2:_*)
			commandExecutor.assignSpectate(getShouldSpec:_*)
			commandExecutor.startTournament()

			while(sleep) {
				latch.await()
			}
		}

		commandExecutor.startTournament()
	}

	def addPlayer(vapor: UUID, serverPlayer: Int, playerName: String): Unit = {
		playerMap.put(serverPlayer, vapor)
		playerNameMap.put(vapor, playerName)
	}

	def removePlayer(serverPlayer: Int): Unit = {
		playerNameMap.remove(playerMap.remove(serverPlayer))
	}

	def updatePlayerName(vapor: UUID, playerName: String): Unit = {
		playerNameMap.put(vapor, playerName)
	}

	def clearPlayers(): Unit = {
		playerMap.clear()
		playerNameMap.clear()
	}

	def extractGame: Game = {
		synchronized{
			val finishedGame = game
			gameFactory = StandardFactory
			game = gameFactory.buildNoGame()
			finishedGame match {
				case ladder: Ladder => priority2 = tournamentTeamLists.map(tl => tl._1++tl._2).getOrElse(Set.empty).map(_.vaporId)
				case _ => priority2 = Set.empty
			}
			finishedGame
		}
	}

	def getGame: IGame = {
		synchronized(
			game
		)
	}

	def newGame(mode: Mode, dateTime: DateTime, map: String, leftTeamId: Int, rightTeamId: Int): Unit = {
		synchronized(
			game = gameFactory.build(mode, dateTime, map, leftTeamId, rightTeamId)
		)
	}

	def getLobbyMap = lobbyMap
}

object ServerContext {
	case class TournamentPlayer(vaporId: UUID, protected[ServerContext] val name: String)
}
