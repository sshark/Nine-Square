package org.teckhooi.ninesquare.misc

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.forkjoin.ForkJoinPool
import scala.concurrent.{Future, ExecutionContext, Await}
import scala.concurrent.duration._

/**
 *
 * @author Lim, Teck Hooi
 *
 *
 */

class ResponsiveActor extends Actor {
  def receive = {
    case x : Int =>
      val start = System.currentTimeMillis()
      Thread.sleep(900)
      println(x)
      sender ! System.currentTimeMillis() - start
  }

  override def postStop() {
    println("I have stopped.")
  }
}

object FuturesAndAwaitExample extends App {
  val actorSystem = ActorSystem("SystemStopExample")
  val numberOfActors = 3

  val actors = for (i <- 0 until numberOfActors) yield actorSystem.actorOf(Props[ResponsiveActor])
  implicit val timeout = Timeout(2 seconds)
  implicit val ec = ExecutionContext.fromExecutorService(new ForkJoinPool())

  val futures = (1 to 2 * numberOfActors) map {x => actors(x % 3) ? x}
  val f = Future.sequence(futures)
  val results = Await.result(f, Duration.Inf)
  results.foreach {x => println(x + "ms")}

  actorSystem.shutdown
}
