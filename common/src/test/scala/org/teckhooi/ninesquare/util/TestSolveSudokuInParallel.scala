package org.teckhooi.ninesquare.util

import java.util.concurrent.ForkJoinPool

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.slf4j.LoggerFactory
import util.Sudoku.{init, solve}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.language.postfixOps


/**
 * Copyright (C) March 21, 2013
 *
 * Test suite for Sudoku solution
 *
 * @author Lim, Teck Hooi
 *
 */

@RunWith(classOf[JUnitRunner])
class TestSolveSudokuInParallel extends FunSuite {

  def logger = LoggerFactory.getLogger(getClass)

  test("Solve easy puzzles simultaneously") {
    logger.info("Solving easy puzzles...")
    val easyPuzzleStart = System.currentTimeMillis()
    solveAll("/easy.txt")
    logger.info("All easy puzzles solved using " + (System.currentTimeMillis() - easyPuzzleStart) + "ms")
  }

  test("Solve hard puzzles simultaneously") {
    logger.info("Solving hard puzzles...")
    val hardPuzzleStart = System.currentTimeMillis()
    solveAll("/top95.txt")
    logger.info("All hard puzzles solved using " + (System.currentTimeMillis() - hardPuzzleStart) + "ms")
  }

  private def solveAll(filename : String) = {
    implicit val ec = ExecutionContext.fromExecutorService(new ForkJoinPool)

    val puzzlesFtr = Future(scala.io.Source.fromInputStream(
      getClass.getResourceAsStream(filename))).map(_.getLines)

    val startMills = System.currentTimeMillis

    val solvedPuzzles = puzzlesFtr.flatMap(puzzles => Future.sequence(puzzles.map {puzzle =>
      Future(solve(init(puzzle)))
    }))

    Await.ready(solvedPuzzles, 2 minute)

    solvedPuzzles.map(_ => s"\nTime taken: ${System.currentTimeMillis - startMills}ms")
  }
}
