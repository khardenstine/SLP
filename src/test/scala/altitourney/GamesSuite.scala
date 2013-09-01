package altitourney

import org.scalatest.FunSuite
import altitourney.slp.games.{BallGame, PlayerSpawn, PerkData}
import org.joda.time.{DateTime, Duration}
import java.util.UUID
import scala.collection.JavaConversions

class GamesSuite  extends FunSuite{
	test("adding PerkData") {
		val one: PerkData = new PerkData(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, new Duration(0, 0))
		val two: PerkData = new PerkData(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, new Duration(0, 1234))

		val add: PerkData = one+Some(two)

		assert(add.kills == 1)
		assert(add.assists == 1)
		assert(add.deaths == 1)
		assert(add.exp == 1)
		assert(add.goals == 1)
		assert(add.goalAssists == 1)
		assert(add.goalSecondaryAssists == 1)
		assert(add.baseDamage == 1)
		assert(add.otherDamage == 1)
		assert(add.baseDestroys == 1)
		assert(add.otherDestroys == 1)
		assert(add.timeAlive.getMillis == 1234)
	}

	test("adding PlayerSpawn to PerkData") {
		val one: PerkData = new PerkData(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, new Duration(0, 0))
		val two: PlayerSpawn = new PlayerSpawn("pew pew", new DateTime())
		two.addAssist(5)
		two.addKill(20)
		two.addDeath(new DateTime())
		two.addSecondaryGoalAssist(5)
		two.addGoal(50)

		val add: PerkData = one+Some(two.getPerkData)

		assert(add.kills == 1)
		assert(add.assists == 1)
		assert(add.deaths == 1)
		assert(add.exp == 80)
		assert(add.goals == 1)
		assert(add.goalAssists == 0)
		assert(add.goalSecondaryAssists == 1)
		assert(add.baseDamage == 0)
		assert(add.otherDamage == 0)
		assert(add.baseDestroys == 0)
		assert(add.otherDestroys == 0)
		//assert(add.timeAlive.getMillis == 1234)
	}

	/*test("spawning a player in a game") {
		val somePlayer = UUID.randomUUID()

		val ballGame = new BallGame(new DateTime(), "ball_planepark", 5, 6)
		ballGame.changeTeam(somePlayer, 5)

		ballGame.spawn(somePlayer, "Trickster", new DateTime())
		ballGame.addKill(Some(somePlayer), UUID.randomUUID(), 19, new DateTime())
		ballGame.addGoal(Some(somePlayer), None, None, 50, new DateTime())

		ballGame.spawn(somePlayer, "Recoiless", new DateTime())
		ballGame.addKill(None, somePlayer, 19, new DateTime())

		ballGame.spawn(somePlayer, "Trickster", new DateTime())
		ballGame.addKill(Some(somePlayer), UUID.randomUUID(), 12, new DateTime())

		val row = JavaConversions.mapAsScalaMap(ballGame.dumpSpawnMap(new DateTime()).row(somePlayer))

		val recoiless = row.get("Recoiless")
		assert(recoiless.get.deaths == 1)

		val trick = row.get("Trickster")
		assert(trick.size == 1)
		assert(trick.isDefined)
		assert(trick.get.deaths == 0)
		assert(trick.get.kills == 2)
		assert(trick.get.goals == 1)
		assert(trick.get.exp == 81)

	}  */
}
