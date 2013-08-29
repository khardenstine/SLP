package altitourney.slp.events.consoleCommands

import altitourney.slp.events.EventHandler
import play.api.libs.json.JsValue
import altitourney.slp.Util

class ListRules(jsVal: JsValue) extends EventHandler(jsVal) {
	val player = getUUID("source")
	val rules = Seq(
		"Ladder Bannable Offenses",
		"1) Leaving in the middle of a game and not returning.",
		"2) Playing in a maxPing=300 server when you know your connection is too bad to not get continuously disconnected in the middle of a game, causing repeated disruption.",
		"3) Speccing because you didn't know you were supposed to be in the game",
		"4) Speccing as a player for any other reason, either repeatedly or without giving warning.",
		"5) Vote initiation of the following form:",
		"-ANY vote by a spectator, when there is no consensus that a vote should be called",
		"-A vote by a player with the intent to disrupt the game (calling vote stop tournament when the game is about to end, etc).",
		"6) Acing in the middle of a game.",
		"7) Playing a ladder game while under level 13 of any ace rank.",
		"8) Spectators not using team-chat after being asked to by a player. All-chat is fine until someone asks them to team-chat. In addition, players who continually try to engage spectators in conversation after spec-chat has been called will be banned as well.",
		"9) Racism/Bigotry. General immaturity. Excessive or unrelenting trash talk targeting specific players.",
		"10) Playing random, full or custom.",
		"11) Playing on multiple accounts (i.e. smurfing). Alternate accounts are subject to permabanning (the main account receives a regular ban).",
		"12) Any activity not listed above that can cause a negative experience for others while playing ladder.",
		"13) Showboating in any manner.",
		"You alone are responsible for all activity on your account. Please do not let others play on your account.",
		"Enter the command 'acceptRules %s' to acknowledge your acceptance.".format(Util.generateHash(player))
	)

	rules.foreach{ rule =>
		getCommandExecutor.serverWhisper(getServerContext.getPlayerName(player), rule)
		Thread.sleep(3000)
	}
}
