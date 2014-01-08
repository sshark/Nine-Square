package controllers

import play.api.mvc.{Action, Controller}
import scala.io.Source
import scala.util.Random
import play.api.libs.json.Json

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

  def pickRandomPuzzleFrom(level : Symbol) = {
    level match {
      case 'EASY => Json.toJson(randomlyPickAPuzzleFrom(easyPuzzles))
      case _ =>  Json.toJson(randomlyPickAPuzzleFrom(hardPuzzles))
    }
  }

  private def randomlyPickAPuzzleFrom(puzzles : List[String]) =
    puzzles(Random.nextInt(puzzles.size)).replace('.', '0').map(_ - 0x30)

}
