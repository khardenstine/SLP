package altitourny.slp.events

import play.api.libs.json.JsValue

/**
 * {
  "participants":[0.0, 1.0, 2.0],
  "participantStatsByName":{
    "Damage Received":[810.0, 100.0, 660.0],
    "Damage Dealt":[280.0, 0.0, 380.0],
    "Experience":[14.0, 0.0, 222.0],
    "Ball Possession Time":[0.0, 0.0, 0.0],
	"Goals Scored":[0.0, 0.0, 0.0],
    "Kills":[1.0, 0.0, 6.0],
    "Crashes":[2.0, 1.0, 1.0],
    "Assists":[0.0, 0.0, 0.0],
    "Longest Life":[50.0, 12.0, 114.0],
    "Deaths":[8.0, 1.0, 2.0],
    "Goals Assisted":[0.0, 0.0, 0.0],
    "Kill Streak":[1.0, 0.0, 4.0],
    "Multikill":[0.0, 0.0, 0.0],
    "Damage Dealt to Enemy Buildings":[1.0, 0.0, 7184.0]
  },
  "port":27276.0,
  "winnerByAward":{
    "Demolition Expert":2.0,
    "Most Deadly":2.0,
    "Longest Life":2.0,
    "Most Helpful":2.0,
    "Best Kill Streak":2.0
  },
  "time":283873.0,
  "type":"roundEnd"
}
 */
case class RoundEnd(override val jsVal: JsValue) extends AbstractEventHandler(jsVal) {
	val game = getSharedEventData.getGame()
	getSharedEventData.clearGame()

	game.dump(getTime)
}

case object RoundEnd extends Event {
	val logType = "roundEnd"

	def getEventHandler(jsVal: JsValue): EventHandler = {
		new RoundEnd(jsVal)
	}
}
