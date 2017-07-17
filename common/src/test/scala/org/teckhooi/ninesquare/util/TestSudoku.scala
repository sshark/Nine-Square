package org.teckhooi.ninesquare.util

import org.junit.Assert._
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.slf4j.LoggerFactory
import org.teckhooi.ninesquare.util.Sudoku._

import scala.io.Source

/**
 * Copyright (C) March 21, 2.13
 *
 * Test suite for Sudoku solution
 *
 * @author Lim, Teck Hooi
 *
 */

@RunWith(classOf[JUnitRunner])
class TestSudoku extends FunSuite {

  val firstPuzzle =
    "..15..8..9....6.4..6...7....8.4.9...7...8.1........4.3....5..723....89....21....."

  val firstPuzzleSolution = List(
    4, 7, 1, 5, 9, 2, 8, 3, 6,
    9, 3, 5, 8, 1, 6, 2, 4, 7,
    2, 6, 8, 3, 4, 7, 5, 9, 1,
    1, 8, 6, 4, 3, 9, 7, 2, 5,
    7, 4, 3, 2, 8, 5, 1, 6, 9,
    5, 2, 9, 7, 6, 1, 4, 8, 3,
    8, 1, 4, 9, 5, 3, 6, 7, 2,
    3, 5, 7, 6, 2, 8, 9, 1, 4,
    6, 9, 2, 1, 7, 4, 3, 5, 8)

  val secondPuzzle = "79..38..4.3.........8.6..7...64...9...3.5.8...5...97...6..2.1.........3.8..31..25"

  val secondPuzzleSolution = List(
    7, 9, 5, 1, 3, 8, 2, 6, 4,
    6, 3, 1, 7, 4, 2, 9, 5, 8,
    4, 2, 8, 9, 6, 5, 3, 7, 1,
    1, 8, 6, 4, 7, 3, 5, 9, 2,
    9, 7, 3, 2, 5, 1, 8, 4, 6,
    2, 5, 4, 6, 8, 9, 7, 1, 3,
    3, 6, 7, 5, 2, 4, 1, 8, 9,
    5, 1, 2, 8, 9, 6, 4, 3, 7,
    8, 4, 9, 3, 1, 7, 6, 2, 5)

  def logger = LoggerFactory.getLogger(getClass)

  test("Solve first puzzle") {
    solve(init(firstPuzzle)).foreach(puzzle => assertEquals(firstPuzzleSolution, toPuzzleString(puzzle)))
  }

  test("Solve second puzzle") {
    solve(init(secondPuzzle)).foreach(puzzle => assertEquals(secondPuzzleSolution, toPuzzleString(puzzle)))
  }

  test("Solve easy, hard and hardest Sudoku puzzles. It will take a while longer to complete") {
    info("Solving easy puzzles...")
    logBasicStats(solvePuzzlesUsing("/easy.txt")) // easy puzzle
    info("Solving hard puzzles...")
    logBasicStats(solvePuzzlesUsing("/top95.txt")) // hard puzzle
    info("Solving hardest puzzles...")
    logBasicStats(solvePuzzlesUsing("/hardest17.txt")) // hardest puzzle
  }

  private def logBasicStats(durations: (Long, Long, Long, Long)) {
    val (total, min, max, avg) = durations
    logger.info(s"It took ${total}ms to solve. An average of ${avg}ms to complete a single " +
      s"puzzle. The maximum and minimum times taken to complete a puzzle were ${max}ms and ${min}ms")
  }

  private def solvePuzzlesUsing(filename : String ) = {
    val durations = Source.fromInputStream(getClass.getResourceAsStream(filename)).getLines().map {line => {
      val localStart = System.currentTimeMillis()
      solve(init(line))
      System.currentTimeMillis() - localStart
    }}.toList
    (durations.sum, durations.min, durations.max, durations.sum / durations.size)
  }
}
