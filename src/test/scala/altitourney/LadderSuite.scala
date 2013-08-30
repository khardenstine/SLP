package altitourney

import org.scalatest.FunSuite
import scala.util.Random
import altitourney.slp.events.consoleCommands.ladder.LadderUtils.RatingTuple
import altitourney.slp.events.consoleCommands.ladder.LadderUtils
import java.util.UUID

class LadderSuite extends FunSuite{
	test("balance") {
		val teamSize = 6
		val ratings = (1 to 2).map(n=> 1 to 10).flatten.map(n => (UUID.randomUUID(), (n * 100) + 1000))
		val ratingsSeq = Random.shuffle(ratings.toSeq.map(v => RatingTuple(v._2, v._1)))
		val leftTeam = ratingsSeq.slice(0, teamSize)
		val rightTeam = ratingsSeq.slice(teamSize, teamSize * 2)

		val teams = LadderUtils.balance(leftTeam, rightTeam)

		assert(teams._1.size == teamSize)
		assert(teams._2.size == teamSize)
	}
}
