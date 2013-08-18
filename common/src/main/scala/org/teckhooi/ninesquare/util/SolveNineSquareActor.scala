package org.teckhooi.ninesquare.util

import akka.actor.Actor
import org.teckhooi.ninesquare.util.NineSquareUtil._
import org.teckhooi.ninesquare.util.SolveNineSquareActor.Solve
import java.util.concurrent.CountDownLatch

/**
 *
 * @author Lim, Teck Hooi
 *
 *
 */

object SolveNineSquareActor {
  case class Solve(x : List[Int])
  case class Done(min : Long, max : Long, avg : Long)
  case class Completed()
}

class SolveNineSquareActor(latch : CountDownLatch) extends Actor {

  def receive = {
    case Solve(l) =>

      val localStart = System.currentTimeMillis()

      val solution = search(NineSquareUtil.toMapWithGuesses(l)).toList.sortBy(_._1).foldLeft(List[Int]()){case (x,y) => x ++ y._2}
      val duration = System.currentTimeMillis() - localStart

      if (!NineSquareUtil.isSheetOK(solution)) throw new RuntimeException

  }

  override def postStop() {
    latch.countDown()
  }
}
