package altitourny.slp.games

import org.joda.time.{Duration, DateTime}

class PlayerSpawn(final val redPerk: String, var lifeStart: DateTime) {
	var kills = 0
	var assists = 0
	var exp = 0
	var deaths = 0
	var goals = 0
	var goalAssists = 0
	var secondaryGoalAssists = 0
	var life: Duration = new Duration(0, 0)
	private var addLife: Boolean = true

	def addKill(xp: Int) {
		kills += 1
		addXP(xp)
	}

	def addAssist(xp: Int) {
		assists += 1
		addXP(xp)
	}

	def addXP(xp: Int) {
		exp += xp
	}

	def addDeath(time: DateTime) {
		deaths += 1
		end(time)
	}

	def addGoal(xp: Int) {
		goals += 1
		addXP(xp)
	}

	def addGoalAssist(xp: Int) {
		goalAssists += 1
		addXP(xp)
	}

	def addSecondaryGoalAssist(xp: Int) {
		secondaryGoalAssists += 1
		addXP(xp)
	}

	def respawn(startTime: DateTime) {
		lifeStart = startTime
		addLife = true
	}

	def end(endTime: DateTime) {
		if (addLife) {
			life = life.plus(new Duration(lifeStart, endTime))
			addLife = false
		}
	}

	def getPerkData: PerkData = {
		new PerkData(kills, assists, deaths, exp, goals, goalAssists, secondaryGoalAssists, life)
	}
}
