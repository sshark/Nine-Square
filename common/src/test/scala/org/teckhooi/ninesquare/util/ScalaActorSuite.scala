package org.teckhooi.ninesquare.util

import akka.actor.{ActorSystem, Props, Actor}

/**
 *
 *
 * @author Lim, Teck Hooi
 *
 */

/*
@RunWith(classOf[JUnitRunner])
class SolverActorSuite extends FunSuite {
  test("hellow world! actor") {
    val mySystem = ActorSystem("mySystem")
    val greeter = mySystem.actorOf(Props[Greeter], "greeter")
    for (i <- 1 to 100) greeter ! Greeter.Solve(i)
    greeter ! Greeter.Total
    mySystem.awaitTermination()
  }
}
*/

object SolverActorSuite extends App {
  val mySystem = ActorSystem("mySystem")
  val greeter = mySystem.actorOf(Props[Greeter], "greeter")
  for (i <- 1 to 1000) greeter ! Greeter.Solve(i)
  greeter ! Greeter.Total
  mySystem.awaitTermination()
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
}

class HelloWorld extends Actor {
  def receive = {
    case Greeter.Done =>
      println("Done... Hurrah!!!")
  }
}
