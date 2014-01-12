package controllers

import play.api.mvc.{Action, Controller}
import scala.io.Source
import scala.util.Random
import play.api.libs.json.Json
import org.teckhooi.ninesquare.util.NineSquareUtil
import play.Logger

/**
 *
 * @author Lim, Teck Hooi
 *
 *
 */

object Puzzle extends Controller{

  lazy val easyPuzzles = Source.fromInputStream(getClass.getResourceAsStream("/easy.txt")).getLines.toList
  lazy val hardPuzzles = Source.fromInputStream(getClass.getResourceAsStream("/hard.txt")).getLines.toList

  def newEasyPuzzle() = Action {
    Ok(pickRandomPuzzleFrom('EASY))
  }

  def newHardPuzzle() = Action {
    Ok(pickRandomPuzzleFrom('HARD))
  }

  def checkPuzzle() = Action(parse.json) {request =>
    Logger.debug("Partial puzzle received = " + request.body)
    Ok(Json.toJson(Map("result" -> NineSquareUtil.isSheetOK(request.body.as[List[Int]]))))
  }

  def solvePuzzle = Action(parse.json) {request =>
    // TODO possiblely mark puzzle as tainted if user do not the solve puzzle on his own accord
    Logger.debug("Puzzle received = " + request.body)
    Ok(Json.toJson(NineSquareUtil.solve(request.body.as[List[Int]])))
  }

  def submitPuzzle = Action(parse.json) {request =>
    // TODO verify user has logged on otherwise this method is unauthorized. Record puzzle as solved in the user account
    Logger.debug("Puzzle received = " + request.body)
    Ok(Json.toJson(Map("result" -> NineSquareUtil.isSheetOK(request.body.as[List[Int]]))));
  }

  def pickRandomPuzzleFrom(level : Symbol) = {
    level match {
      case 'EASY => Json.toJson(randomlyPickAPuzzleFrom(easyPuzzles))
      case _ =>  Json.toJson(randomlyPickAPuzzleFrom(hardPuzzles))
    }
  }

  private def randomlyPickAPuzzleFrom(puzzles : List[String]) =
    puzzles(Random.nextInt(puzzles.size)).replace('.', '0').map(_ - 0x30)
}
