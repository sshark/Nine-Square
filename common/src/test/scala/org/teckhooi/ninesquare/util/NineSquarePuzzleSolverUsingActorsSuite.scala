package org.teckhooi.ninesquare.util

import akka.routing.RoundRobinRouter
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
import java.util.concurrent.{Executors, CountDownLatch}

/**
 * Copyright (C) March 21, 2013
 *
 * Test suite for Sudoku solution
 *
 * @author Lim, Teck Hooi
 *
 */

@RunWith(classOf[JUnitRunner])
class NineSquarePuzzleSolverUsingActorsSuite extends FunSuite {

  def logger = LoggerFactory.getLogger(getClass)

  test("Solve puzzles simultaneously using Actors") {
    val system = ActorSystem("nineSquareSystem")
    val solversRouter = system.actorOf(Props[NineSquareSolverActor]
      .withRouter(RoundRobinRouter(nrOfInstances = 8)), name = "solverActorRouted")

    logger.info("Solving easy puzzles...")
    val easyPuzzleStart = System.currentTimeMillis()
    val durationsToSolveEasyPuzzles = usingActorsToSolve("/easy.txt", solversRouter) // easy puzzle
    logger.info("All easy puzzles solved using " + (System.currentTimeMillis() - easyPuzzleStart) + "ms")

    logger.info("Solving hard puzzles...")
    val hardPuzzleStart = System.currentTimeMillis()
    val durationsToSolveHardPuzzles = usingActorsToSolve("/top95.txt", solversRouter) // tough puzzle
    logger.info("All hard puzzles solved using " + (System.currentTimeMillis() - hardPuzzleStart) + "ms")

    system.shutdown()
  }

  private def usingActorsToSolve(filename: String, puzzleSolvers: ActorRef) = {
    implicit val ec = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(8))

    val futures = Source.fromInputStream(getClass.getResourceAsStream(filename)).getLines().map {line =>
      ask(puzzleSolvers, NineSquareSolverActor.Solve(line.replace('.', '0').map(_ - 0x30).toList))(100 seconds).mapTo[Long]
    }

    Await.ready(Future.sequence(futures), Duration.Inf)
  }
}
