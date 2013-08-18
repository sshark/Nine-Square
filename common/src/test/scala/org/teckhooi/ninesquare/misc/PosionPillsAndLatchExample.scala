package org.teckhooi.ninesquare.misc

import akka.actor._
import akka.util.Timeout
import java.util.concurrent.CountDownLatch
import scala.concurrent.forkjoin.ForkJoinPool
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.Random

/**
 *
 * @author Lim, Teck Hooi
 *
 *
 */

class MyTestActor(latch : CountDownLatch) extends Actor {
  def receive = {
    case x : Int =>
      Thread.sleep(Random.nextInt(500))
      println(x)
  }

  override def postStop() = {
    latch.countDown()
    println("I have stopped.")
  }
}

object PoisonPillsAndLatchExample extends App {
  val actorSystem = ActorSystem("SystemStopExample")

  val numberOfActors = 10
  val latch = new CountDownLatch(numberOfActors)

  val actors = for (i <- 0 until numberOfActors) yield actorSystem.actorOf(Props(new MyTestActor(latch)))
  implicit val timeout = Timeout(15 seconds)
  implicit val ec = ExecutionContext.fromExecutorService(new ForkJoinPool())

  (1 to numberOfActors * 3) map {x => actors(x % 3) ! x }
  actors.foreach(_ ! PoisonPill)
  latch.await()
  println("Done")

  actorSystem.shutdown()
}
