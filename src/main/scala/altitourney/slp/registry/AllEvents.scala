package altitourney.slp.registry

import altitourney.slp.SLP
import altitourney.slp.events._
import play.api.libs.json.JsValue

protected class AllEvents extends EventRegistry{
	val REGISTRY: Map[String, REGISTER] =
		(new StartUpRegistry).REGISTRY ++ Map(
			("assist",					new Assist(_)),
			("chat",					EmptyRegister),
			("clientAdd",				new ClientAdd(_)),
			("clientNicknameChange",	new ClientNicknameChange(_)),
			("clientRemove",			new ClientRemove(_)),
			("consoleCommandExecute",	SLP.getRegistryFactory.getConsoleCommandRegistry.handle(_)),
			("goal",					new Goal(_)),
			("kill",					new Kill(_)),
			("logServerStatus",			new LogServerStatus(_)),
			("mapChange",				new MapChange(_)),
			("mapLoading",				new MapLoading(_)),
			("pingSummary",				new PingSummary(_)),
			("powerupAutoUse",			new PowerupAutoUse(_)),
			("powerupDefuse",			new PowerupDefuse(_)),
			("powerupPickup",			new PowerupPickup(_)),
			("powerupUse",				new PowerupUse(_)),
			("roundEnd",				new RoundEnd(_)),
			("serverHitch",				new ServerHitch(_)),
			("serverStart",				new ServerStart(_)),
			("spawn",					new Spawn(_)),
			("structureDamage",			new StructureDamage(_)),
			("structureDestroy",		new StructureDestroy(_)),
			("teamChange",				new TeamChange(_)),
			("tournamentRoundEnd",		new TournamentRoundEnd(_)),
			("tournamentStart",			new TournamentStart(_)),
			("tournamentStop",			new TournamentStop(_))
		)

	def getEventName(jsVal: JsValue): String = (jsVal \ "type").as[String]
}