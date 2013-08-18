package org.teckhooi.ninesquare.misc

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.forkjoin.ForkJoinPool
import scala.concurrent.{Future, ExecutionContext, Await}
import scala.concurrent.duration._
import scala.util.Random

/**
 *
 * @author Lim, Teck Hooi
 *
 *
 */

class ResponsiveActor extends Actor {
  def receive = {
    case x : Int =>
      Thread.sleep(Random.nextInt(500))
      println(x)
      sender ! "done"
  }

  override def postStop() {
    println("I have stopped.")
  }
}

object FuturesAndAwaitExample extends App {
  val actorSystem = ActorSystem("SystemStopExample")
  val numberOfActors = 3

  val actors = for (i <- 0 until numberOfActors) yield actorSystem.actorOf(Props[ResponsiveActor])
  implicit val timeout = Timeout(15 seconds)
  implicit val ec = ExecutionContext.fromExecutorService(new ForkJoinPool())

  val futures = (1 to 10 * numberOfActors) map {x => actors(x % 3) ? x}
  Await.result(Future.sequence(futures), Duration.Inf)
  println("Done")

  actorSystem.shutdown
}
