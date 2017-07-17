package controllers

import org.teckhooi.ninesquare.util.Sudoku._
import play.Logger
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

import scala.io.Source
import scala.util.Random

/**
 *
 * @author Lim, Teck Hooi
 *
 *
 */

class Puzzles extends Controller{

  lazy val easyPuzzles = Source.fromInputStream(getClass.getResourceAsStream("/easy.txt")).getLines.toList
  lazy val hardPuzzles = Source.fromInputStream(getClass.getResourceAsStream("/hard.txt")).getLines.toList

  def newEasyPuzzle() = Action {
    Ok(pickRandomPuzzleFrom('EASY))
  }

  def newHardPuzzle() = Action {
    Ok(pickRandomPuzzleFrom('HARD))
  }

  // TODO refactor with front end sending a String instead
  def checkPuzzle() = Action(parse.json) {request =>
    Logger.debug("Partial puzzle received = " + request.body)
    Ok(Json.toJson(Map("result" -> 
      isSolved(init(toStr(request.body.as[List[Int]]))))))
  }

  // TODO refactor with front end sending a String instead
  def solvePuzzle = Action(parse.json) {request =>
    // TODO possiblely mark puzzle as tainted if user do not the solve puzzle on his own accord
    Logger.debug("Puzzle received = " + request.body)
    solve(init(toStr(request.body.as[List[Int]]))).map(m => Ok(Json.toJson(toPuzzleString(m)))).get
  }

  // TODO refactor with front end sending a String instead
  def submitPuzzle = Action(parse.json) {request =>
    // TODO verify user has logged on otherwise this method is unauthorized. Record puzzle as solved in the user account
    Logger.debug("Puzzle received = " + request.body)
    Ok(Json.toJson(Map("result" ->
      isSolved(init(toStr(request.body.as[List[Int]]))))))
  }

  // TODO refactor with front end sending a String instead
  private def toStr(l: List[Int]) = l.foldLeft("")((s, x) => s + (if (x == 0) '.' else (0x30 + x).toChar))

  def pickRandomPuzzleFrom(level : Symbol) = {
    level match {
      case 'EASY => Json.toJson(randomlyPickAPuzzleFrom(easyPuzzles))
      case _ =>  Json.toJson(randomlyPickAPuzzleFrom(hardPuzzles))
    }
  }

  private def randomlyPickAPuzzleFrom(puzzles : List[String]) =
    puzzles(Random.nextInt(puzzles.size)).replace('.', '0').map(_ - 0x30)
}
