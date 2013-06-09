package altitourny.slp.events

import org.joda.time.DateTime
import java.util.UUID
import com.google.common.collect.HashBiMap
import altitourny.slp.matches._

class SharedEventData(private val startTime: DateTime)
{
	private val playerMap: HashBiMap[Int, UUID] = HashBiMap.create()
	private val playerNameMap: HashBiMap[UUID, String] = HashBiMap.create()
	private var aMatch: Match = new NoMatch

	def getServerTime(time: Int): DateTime =
	{
		startTime.withDurationAdded(time.toLong, 1)
	}

	def getPlayer(player: Int): UUID =
	{
		playerMap.get(player)
	}

	def getPlayerName(player: Int): String =
	{
		getPlayerName(playerMap.get(player))
	}

	def getPlayerName(vapor: UUID): String =
	{
		playerNameMap.get(vapor)
	}

	def addPlayer(vapor: UUID, serverPlayer: Int, playerName: String)
	{
		playerMap.put(serverPlayer, vapor)
		playerNameMap.put(vapor, playerName)
	}

	def removePlayer(serverPlayer: Int) =
	{
		playerNameMap.remove(playerMap.remove(serverPlayer))
	}

	def updatePlayerName(vapor: UUID, playerName: String)
	{
		playerNameMap.forcePut(vapor, playerName)
	}

	def clearPlayers()
	{
		playerMap.clear()
		playerNameMap.clear()
	}

	def clearMatch()
	{
		aMatch = new NoMatch
	}

	def getMatch(): Match =
	{
		aMatch
	}

	def setMatch(aMatch: Match)
	{
		this.aMatch = aMatch
	}
}
