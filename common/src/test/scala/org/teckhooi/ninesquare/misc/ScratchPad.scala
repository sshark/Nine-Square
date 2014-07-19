package org.teckhooi.ninesquare.misc

import org.teckhooi.ninesquare.util.NineSquareUtil


object ScratchPad extends App {
  type Estimates = Map[Int, List[Int]]
  type Entry = (Int, List[Int])

  val byNumsSize: Ordering[Entry] = Ordering.by(_._2.size)

  val sudokuLine = "+-------" * 3 + "+" + "\n"

  def prettyPrint(board: List[Int]) = {
    val buffer = new StringBuilder(sudokuLine)
    val indexedBoard = board.grouped(9).zipWithIndex
    for (i <- indexedBoard) lineLayoutFor(buffer, i)
    buffer.toString
  }

  def lineLayoutFor(buffer: StringBuilder, numbers: (List[Int], Int)) = {
    buffer.append("|")
    for (i <- 0 to 8) {
      buffer.append(" " + numbers._1(i))
      if ((i + 1) % 3 == 0) buffer.append(" |")
    }
    buffer.append("\n")
    if ((numbers._2 + 1) % 3 == 0) buffer.append(sudokuLine)
  }

  //  	val l = "200000015006400070300060000800300200000104000400500000000023600010000000070000000".toCharArray.map(_ - 0x30).toList
  //	val l = "001005300000490000000102064000000750600000001035000000460903000000024090003600100".toCharArray.map(_ - 0x30).toList
  //	val l = "001005000000090000000102000000000000600000001030000000460003000000004090003600100".toCharArray.map(_ - 0x30).toList

  // no solution using previous method
//      val l = "000000015020060000000000408003000900000100000000008000150400000000070300800000060".toCharArray.map(_ - 0x30).toList

  // hardest problem in Norvig's website
//        val l = ".....6....59.....82....8....45........3........6..3.54...325..6..................".replace('.', '0').map(_ - 0x30).toList

  //  	val l = "...............9..97.3......1..6.5....47.8..2.....2..6.31..4......8..167.87......".replace('.', '0').map(_ - 0x30).toList // solved 3s
    	val l = ".........9......84.623...5....6...453...1...6...9...7....1.....4.5..2....3.8....9".replace('.', '0').map(_ - 0x30).toList // solved 6s
  //    val l = "..............53.89..3.8..2..9...5.7.5...2....7....69..8.....4.....43..54.2...8..".replace('.', '0').map(_ - 0x30).toList // solved 1s
//  val l = "4...3.......6..8..........1....5..9..8....6...7.2........1.27..5.3....4.9........".replace('.', '0').map(_ - 0x30).toList // solved 1s

  // obsolete
  def findGuesses(l: List[Int], pos: Int): List[Int] = (1 to 9).filter(!NineSquareUtil.isConflictAt(l, pos, _)).toList

  // obsolete
  def solveAll(xs: List[Int], combinations: List[(List[Int], Int)]): Stream[List[Int]] =
    if (combinations.isEmpty) Stream(xs) else {
      val h = combinations.head
      h._1.toStream.flatMap(x => if (NineSquareUtil.isConflictAt(xs.updated(h._2, 0), h._2, x)) Nil else
        solveAll(xs.updated(h._2, x), combinations.tail))
    }

  def bigCellTranslation(pos: Int, x: Int = 0) = pos / 27 * 27 + pos / 3 % 3 * 3 + x % 3 + x / 3 * 9

  def horizontalTranslation(pos: Int, x: Int = 0) = pos / 9 * 9 + x

  def verticalTranslation(pos: Int, x: Int = 0) = pos % 9 + x * 9

  val horizontal = (0 until 81 by 9).flatMap(x => (0 until 9) map (y => horizontalTranslation(x, y))).grouped(9).toSet
  val horizontalMap = horizontal map (v => v.foldLeft(Map[Int, Set[Int]]())((m, w) => m ++ Map(w -> (v.toBuffer - w).toSet))) reduce (_ ++ _)

  val vertical = (0 until 81).flatMap(x => (0 until 9) map (y => verticalTranslation(x, y))).grouped(9).toSet
  val verticalMap = vertical map (v => v.foldLeft(Map[Int, Set[Int]]())((m, w) => m ++ Map(w -> (v.toBuffer - w).toSet))) reduce (_ ++ _)

  val bigCell = (0 until 81 by 3).flatMap(x => (0 until 9) map (y => bigCellTranslation(x, y))).grouped(9).toSet
  val bigCellMap = bigCell map (v => v.foldLeft(Map[Int, Set[Int]]())((m, w) => m ++ Map(w -> (v.toBuffer - w).toSet))) reduce (_ ++ _)

  def search(estimates : Estimates) : Estimates =
    estimates
      .toSeq
      .filter(_._2.size != 1)
      .reduceOption[Entry](byNumsSize.min)
      .map(entry => (if (entry._2.size > 1) eliminate(estimates, entry) else None)
        .getOrElse(Map.empty))
      .getOrElse(estimates) // all sizes are equal to 1

  def eliminate(estimates : Estimates, entry: Entry) : Option[Estimates] = {
    val pos = entry._1
    entry._2
      .view // process the sequence lazily to run each step only when needed
      .map(n => search(eliminate(estimates.updated(pos, List(n)), pos, n)))
      .collectFirst({ case es if !es.isEmpty => es }) // better/safer than != Map.empty
  }

  def eliminate(estimates : Map[Int, List[Int]], position : Int, num : Int) : Map[Int, List[Int]] = {
    val peerPositions = verticalMap(position) ++ horizontalMap(position) ++ bigCellMap(position)
    val targetPositions = peerPositions.filter(pos => estimates(pos).size == 2 && estimates(pos).contains(num))
    val peers = peerPositions.foldLeft(estimates){case (m, pos) => m.updated(pos, m(pos).filterNot(_ == num))}
    targetPositions.foldLeft(peers){case (m, pos) => if (m(pos).isEmpty) m else m ++ eliminate(m, pos, m(pos).head)}
  }

  val start = System.currentTimeMillis()

  print("Solving... " + l + "\n")

  val solution = search(NineSquareUtil.toMapWithGuesses(l)).toList.sortBy(_._1).foldLeft(List[Int]()){case (x,y) => x ++ y._2}

  if (NineSquareUtil.isSheetOK(solution)) println(prettyPrint(solution)) else println(" failed")

  println("Done... took " + (System.currentTimeMillis() - start) + "ms")
}
