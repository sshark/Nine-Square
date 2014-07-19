package org.teckhooi.ninesquare.util

import java.util.concurrent.{ExecutorService, Executors}

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.slf4j.LoggerFactory

import scala.concurrent.duration.Duration
import scala.concurrent.{Future, Await, ExecutionContext}
import scala.io.Source

/**
 * Copyright (C) March 21, 2013
 *
 * Test suite for Sudoku solution
 *
 * @author Lim, Teck Hooi
 *
 */

@RunWith(classOf[JUnitRunner])
class NineSquarePuzzleUsingMultipleSolversSuite extends FunSuite {

  def logger = LoggerFactory.getLogger(getClass)

  test("Solve puzzles simultaneously using Futures") {
    val pool = Executors.newFixedThreadPool(8)

    logger.info("Solving easy puzzles...")
    val easyPuzzleStart = System.currentTimeMillis()
    val durationsToSolveEasyPuzzles = usingFuturesToSolve("/easy.txt", pool) // easy puzzle
    logger.info("All easy puzzles solved using " + (System.currentTimeMillis() - easyPuzzleStart) + "ms")

    logger.info("Solving hard puzzles...")
    val hardPuzzleStart = System.currentTimeMillis()
    val durationsToSolveHardPuzzles = usingFuturesToSolve("/top95.txt", pool) // tough puzzle
    logger.info("All hard puzzles solved using " + (System.currentTimeMillis() - hardPuzzleStart) + "ms")

    pool.shutdown()
  }


  private def usingFuturesToSolve(filename : String, pool : ExecutorService) = {
    implicit val ec =  ExecutionContext.fromExecutorService(pool)

    val futures = Source.fromInputStream(getClass.getResourceAsStream(filename)).getLines().map {line =>
      Future {
        NineSquareUtil.solve(line.replace('.', '0').map(_ - 0x30).toList)
      }
    }
    Await.result(Future.sequence(futures), Duration.Inf).toList
  }
}
