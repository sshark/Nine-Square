package org.teckhooi.ninesquare.util

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import scala.io.Source
import org.slf4j.LoggerFactory
import akka.actor.{PoisonPill, ActorRef, Props, ActorSystem}
import akka.pattern.ask
import scala.concurrent.{Future, Await, ExecutionContext}
import scala.concurrent.forkjoin.ForkJoinPool
import scala.concurrent.duration._
import java.util.concurrent.CountDownLatch

/**
 * Copyright (C) March 21, 2013
 *
 * Test suite for Sudoku solution
 *
 * @author Lim, Teck Hooi
 *
 */

@RunWith(classOf[JUnitRunner])
class NineSquarePuzzleSolverActorSuite extends FunSuite {

  def logger = LoggerFactory.getLogger(getClass)

  test("Solve puzzles simultaneously") {
    val system = ActorSystem("nineSquareSystem")
    // this number depends on the number of puzzles to solve. 24 is a good number for solving both easy and hard puzzles
    val numberOfSolvers = 24
    val latch = new CountDownLatch(numberOfSolvers)
    val puzzleSolvers = for (i <- 0 until numberOfSolvers)
      yield system.actorOf(Props(new SolveNineSquareActor(latch)))

    info("Solving easy puzzles...")
    val durationsToSolveEasyPuzzles = usingActorsToSolve("/easy.txt", puzzleSolvers) // easy puzzle
    info("with the total time of " + durationsToSolveEasyPuzzles.sum + "ms, min of " +
      durationsToSolveEasyPuzzles.min + "ms, max of " +
      durationsToSolveEasyPuzzles.max + "ms and average of " +
      (durationsToSolveEasyPuzzles.sum / durationsToSolveEasyPuzzles.length) + "ms")

    info("Solving hard puzzles...")
    val durationsToSolveHardPuzzles = usingActorsToSolve("/top95.txt", puzzleSolvers) // tough puzzle
    info("with the total time of " + durationsToSolveHardPuzzles.sum + "ms, with min of " +
      durationsToSolveHardPuzzles.min + "ms, max of " +
      durationsToSolveHardPuzzles.max + "ms and average of " +
      (durationsToSolveHardPuzzles.sum / durationsToSolveHardPuzzles.length) + "ms")

    puzzleSolvers.foreach(_ ! PoisonPill)
    latch.await()
    system.shutdown()
  }

  private def usingActorsToSolve(filename: String, puzzleSolvers: Seq[ActorRef]) = {
    implicit val ec = ExecutionContext.fromExecutorService(new ForkJoinPool())

    val i = cyclicSolversIndex(puzzleSolvers.length)

    val futures = Source.fromInputStream(getClass.getResourceAsStream(filename)).getLines().map {line =>
      ask(puzzleSolvers(i.next), SolveNineSquareActor.Solve(line.replace('.', '0').map(_ - 0x30).toList))(50 seconds).mapTo[Long]
    }

    Await.result(Future.sequence(futures), Duration.Inf).toList
  }

  private def cyclicSolversIndex(numberOfSolvers : Int) = {
    Stream.continually((0 until numberOfSolvers).toStream).flatten.iterator
  }
}
