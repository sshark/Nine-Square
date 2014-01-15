package org.teckhooi.ninesquare.util

import NineSquareUtil._
import org.junit.Assert._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import scala.io.Source
import org.slf4j.LoggerFactory

/**
 * Copyright (C) March 21, 2013
 *
 * Test suite for Sudoku solution
 *
 * @author Lim, Teck Hooi
 *
 */

@RunWith(classOf[JUnitRunner])
class NineSquareUtilSuite extends FunSuite {

  val rightSolution = List(
    1, 2, 3, 4, 5, 6, 7, 8, 9,
    4, 5, 6, 7, 8, 9, 1, 2, 3,
    7, 8, 9, 1, 2, 3, 4, 5, 6,
    2, 3, 4, 5, 6, 7, 8, 9, 1,
    5, 6, 7, 8, 9, 1, 2, 3, 4,
    8, 9, 1, 2, 3, 4, 5, 6, 7,
    3, 4, 5, 6, 7, 8, 9, 1, 2,
    6, 7, 8, 9, 1, 2, 3, 4, 5,
    9, 1, 2, 3, 4, 5, 6, 7, 8)

  val wrongSolution = List(
    1, 2, 3, 4, 5, 6, 3, 8, 9,
    4, 5, 6, 7, 8, 9, 1, 2, 3,
    7, 8, 9, 1, 2, 3, 4, 5, 6,
    2, 3, 4, 5, 6, 7, 8, 9, 1,
    5, 6, 7, 8, 9, 1, 2, 3, 4,
    8, 9, 1, 2, 3, 4, 5, 6, 7,
    3, 4, 5, 6, 7, 8, 9, 1, 2,
    6, 7, 8, 9, 1, 2, 3, 4, 5,
    9, 1, 2, 3, 4, 5, 6, 7, 8)

  val veryHardSheet = List(
    0, 0, 1, 5, 0, 0, 8, 0, 0,
    9, 0, 0, 0, 0, 6, 0, 4, 0,
    0, 6, 0, 0, 0, 7, 0, 0, 0,
    0, 8, 0, 4, 0, 9, 0, 0, 0,
    7, 0, 0, 0, 8, 0, 1, 0, 0,
    0, 0, 0, 0, 0, 0, 4, 0, 3,
    0, 0, 0, 0, 5, 0, 0, 7, 2,
    3, 0, 0, 0, 0, 8, 9, 0, 0,
    0, 0, 2, 1, 0, 0, 0, 0, 0)

  val solutionToVeryHardSheet = List(
    4, 7, 1, 5, 9, 2, 8, 3, 6,
    9, 3, 5, 8, 1, 6, 2, 4, 7,
    2, 6, 8, 3, 4, 7, 5, 9, 1,
    1, 8, 6, 4, 3, 9, 7, 2, 5,
    7, 4, 3, 2, 8, 5, 1, 6, 9,
    5, 2, 9, 7, 6, 1, 4, 8, 3,
    8, 1, 4, 9, 5, 3, 6, 7, 2,
    3, 5, 7, 6, 2, 8, 9, 1, 4,
    6, 9, 2, 1, 7, 4, 3, 5, 8)

  val veryHardSheetInString =
    "001005300" + "050490000" + "000102064" +
    "000000750" + "600000001" + "035000000" +
    "460903000" + "000024090" + "003600100"

  val liveExample = List(
    7, 9, 0, 0, 3, 8, 0, 0, 4,
    0, 3, 0, 0, 0, 0, 0, 0, 0,
    0, 0, 8, 0, 6, 0, 0, 7, 0,
    0, 0, 6, 4, 0, 0, 0, 9, 0,
    0, 0, 3, 0, 5, 0, 8, 0, 0,
    0, 5, 0, 0, 0, 9, 7, 0, 0,
    0, 6, 0, 0, 2, 0, 1, 0, 0,
    0, 0, 0, 0, 0, 0, 0, 3, 0,
    8, 0, 0, 3, 1, 0, 0, 2, 5)

  val liveExampleSolution = List(
    7, 9, 5, 1, 3, 8, 2, 6, 4,
    6, 3, 1, 7, 4, 2, 9, 5, 8,
    4, 2, 8, 9, 6, 5, 3, 7, 1,
    1, 8, 6, 4, 7, 3, 5, 9, 2,
    9, 7, 3, 2, 5, 1, 8, 4, 6,
    2, 5, 4, 6, 8, 9, 7, 1, 3,
    3, 6, 7, 5, 2, 4, 1, 8, 9,
    5, 1, 2, 8, 9, 6, 4, 3, 7,
    8, 4, 9, 3, 1, 7, 6, 2, 5)

  val solutionToVeryHardSheetInString = Vector(List(
    2, 4, 1, 8, 6, 5, 3, 7, 9,
    3, 5, 6, 4, 9, 7, 2, 1, 8,
    8, 7, 9, 1, 3, 2, 5, 6, 4,
    1, 9, 4, 3, 8, 6, 7, 5, 2,
    6, 8, 2, 5, 7, 9, 4, 3, 1,
    7, 3, 5, 2, 4, 1, 9, 8, 6,
    4, 6, 7, 9, 1, 3, 8, 2, 5,
    5, 1, 8, 7, 2, 4, 6, 9, 3,
    9, 2, 3, 6, 5, 8, 1, 4, 7))

  def logger = LoggerFactory.getLogger(getClass)

  test("No conflict for right sample solution sheet.") {
    assertTrue(rightSolution.zipWithIndex.forall {
      case (x, ndx) => !isConflictAt(rightSolution.updated(ndx, 0), ndx, x)
    })
  }

  test("Conflict found for wrong solution sheet.") {
    assertFalse(wrongSolution.zipWithIndex.forall {
      case (x, ndx) => !isConflictAt(rightSolution.updated(ndx, 0), ndx, x)
    })
  }

  test("Solve a very hard sheet.") {
    assertEquals(solutionToVeryHardSheet, search(NineSquareUtil.toMapWithGuesses(veryHardSheet)).toList.sortBy(_._1).flatMap(_._2))
  }

  test("Solve a very hard sheet presented in a long string.") {
    assertEquals(solutionToVeryHardSheetInString, solveAll(veryHardSheetInString.map(x => x - 0x30).toList))
  }

  test("Verify a good empty sheet") {
    assertTrue(isSheetOK(veryHardSheet))
  }

  test("False result for a bad empty sheet") {
    assertFalse(isSheetOK(veryHardSheet.updated(26, 2)))
  }

  test("Solve easy and hard Sudoku. This test will take a while to complete") {
    info("Solving easy puzzles...")
    logBasicStats(solvePuzzlesUsing("/easy.txt"), "/easy.txt") // easy puzzle
    info("Solving hard puzzles...")
    logBasicStats(solvePuzzlesUsing("/top95.txt"), "/top95.txt") // hardest puzzle
  }

  test("Solve a hard live example") {
    assertEquals(liveExampleSolution, search(liveExample).toList.sortBy(_._1).foldLeft(List[Int]()){case (x,y) => x ++ y._2})
  }

  private def logBasicStats(durations: (Long, Long, Long), puzzle: String) {
    val (min, max, avg) = durations
    logger.info("It took an average of " + avg + " ms to complete a single puzzle in " + puzzle +
      ". The maximum and minimum times taken to complete a puzzle were " + max + "ms and " + min + "ms")
  }


  private def solvePuzzlesUsing(filename : String ) = {
    val durations = Source.fromInputStream(getClass.getResourceAsStream(filename)).getLines().map {line => {
      val l = line.replace('.', '0').map(_ - 0x30).toList

      val localStart = System.currentTimeMillis()

      val solution = search(NineSquareUtil.toMapWithGuesses(l)).toList.sortBy(_._1).foldLeft(List[Int]()){case (x,y) => x ++ y._2}
      val duration = System.currentTimeMillis() - localStart

      if (NineSquareUtil.isSheetOK(solution)) info("Solving... " + l + " took " + duration + "ms") else
        fail("Failed to solve " + l)

      duration

    }}.toList
    (durations.min, durations.max, (durations.sum / durations.size))
  }
}
