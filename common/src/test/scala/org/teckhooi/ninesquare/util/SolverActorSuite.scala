package org.teckhooi.ninesquare.util

import akka.actor.{ActorSystem, Props, Actor}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite

/**
 *
 *
 * @author Lim, Teck Hooi
 *
 */

@RunWith(classOf[JUnitRunner])
class SolverActorSuite extends FunSuite {


  test("hellow world! actor") {
    val system = ActorSystem("mySystem")
    val greeter = system.actorOf(Props[Greeter], "greeter")
    greeter ! Greeter.Solve(List(1,6,5,3))
    Thread.sleep(500) // if the test completed before the actors finish, there will be no output
  }
}

object Greeter {

  case class Solve(x : List[Int])
  case class Done(min : Long, max : Long, avg : Long)

}

class Greeter extends Actor {
  val helloWorldActor = context.actorOf(Props[HelloWorld], "helloWorld")
  def receive = {
    case Greeter.Solve(x) =>
      println(x)
      helloWorldActor ! Greeter.Done
  }
}

class HelloWorld extends Actor {

  def receive = {
    case Greeter.Done =>
      println("Done... Hurrah!!!")
      context.stop(self)
  }
}
