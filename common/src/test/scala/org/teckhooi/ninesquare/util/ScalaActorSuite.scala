package org.teckhooi.ninesquare.util

import akka.actor.{ActorSystem, Props, Actor}
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith

/**
 *
 *
 * @author Lim, Teck Hooi
 *
 */

@RunWith(classOf[JUnitRunner])
class SolverActorSuite extends FunSuite {
  test("hellow world! actor") {
    val mySystem = ActorSystem("mySystem")
    val greeter = mySystem.actorOf(Props[Greeter], "greeter")
    for (i <- 1 to 100) greeter ! Greeter.Solve(i)
    greeter ! Greeter.Total
    mySystem.awaitTermination() // needed in a test suite otherwise the the test will terminate abruptly
  }
}


object Greeter {
  case class Solve(x: Int)

  case class Done()

  case class Total()
}

class Greeter extends Actor {
  var counter: Int = 0

  val helloWorldActor = context.actorOf(Props[HelloWorld], "helloWorld")

  def receive = {
    case Greeter.Solve(x) =>
      println(x)
      counter = counter + 1
      helloWorldActor ! Greeter.Done

    case Greeter.Total =>
      println("Total : " + counter)
      context.system.shutdown()
  }

  override def postStop() = {
    println("Stopping actor " + self)
  }
}

class HelloWorld extends Actor {
  def receive = {
    case Greeter.Done =>
      println("Done... Hurrah!!!")
  }
}
