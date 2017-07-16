// Scala implementation using the algorithm from http://www.norvig.com/sudoku.html
// with a slight twist

package util

object Sudoku {
  val debug = false

  val digits = ('1' to '9').mkString
  val alphas = ('A' to 'I').mkString

  def cross(rows: String, cols: String) = (for {
    row <- rows
    col <- cols} yield {"" + row + col}).toSet

  val horizontals = digits.map(d => cross(alphas, d.toString)).toSet
  val verticals = alphas.map(a => cross(a.toString, digits)).toSet
  val blocks = for {
    colBlk <- alphas.grouped(3)
    rowBlk <- digits.grouped(3)
  } yield (cross(colBlk, rowBlk))

  val all = horizontals ++ verticals ++ blocks

  val peers = cross(alphas, digits).foldLeft(Map.empty[String, Set[Set[String]]])((m,a) =>
    m + (a -> (all.filter(_.contains(a)).map(_ - a))))

  def coord(ndx: Int) = "" + alphas(ndx / 9) + digits(ndx % 9)

  def addNth(l: List[List[String]], extras: List[String], newList: List[List[String]] = Nil, ndx: Int = 0): List[List[String]] =
    l match {
      case Nil => newList.reverse
      case xs +: xss if (ndx % 3 == 0 && ndx > 1) => addNth(xss, extras, xs +: extras +: newList, ndx + 1)
      case xs +: xss => addNth(xss, extras, xs +: newList, ndx + 1)
    }

  def prettyFormat(s: String) =
    List("+-----+-----+-----+") ++
      addNth(s.grouped(9).map(_.grouped(3).toList).toList, List("---", "---", "---"))
        .map(x => if (x(0).startsWith("-")) "+-----+-----+-----+" else s"| ${x(0)} | ${x(1)} | ${x(2)} |") ++
      List("+-----+-----+-----+")

  def init(puzzle: String): Map[String, Set[Char]] = {
    def fill(i: Char, cell: String, solution: Map[String, Set[Char]]) =
      peers(cell).flatten.foldLeft(solution)(
        (m,c) => m.updated(c, m(c).filterNot(_ == i)))

    def _init(cells: List[(Char, Int)], solution: Map[String, Set[Char]]): Map[String, Set[Char]] = cells match {
      case Nil => solution
      case (c, i) :: xs => _init(xs, fill(c, coord(i), solution))
    }

    val prefilledSolution = puzzle.zipWithIndex
      .foldLeft(Map.empty[String, Set[Char]]) {
        case (m, ('.', pos)) => m.updated(coord(pos), digits.toSet)
        case (m, (c, pos)) => m.updated(coord(pos), Set(c))
      }

    _init(puzzle.zipWithIndex.filterNot(_._2 == '.').toList, prefilledSolution)
  }

  def prettyPrint(puzzle: Map[String, Set[Char]]) =
    puzzle.toList.sortBy(_._1).map(_._2).grouped(9).toList.foreach(line => {
      line.foreach(x => {x.foreach(print); print(" " * (9 - x.size))})
      println
    })

  def print2List(puzzle: Map[String, Set[Char]]) = {
    print("List(")
    puzzle.toList.sortBy(_._1).map(_._2).grouped(9).toList.foreach(line => {
      line.foreach(x => {
        print("\"")
        x.foreach(print)
        print("\",")
      })
      println
    })
    print(")")
  }

  def eliminate(i: Char, cell: String, solution: Map[String, Set[Char]]): Option[Map[String, Set[Char]]] = {
    val neighbours = peers(cell).flatten
    val affectedCells = neighbours.filter(cell => {
      val guesses = solution(cell)
      guesses.size == 2 && guesses.contains(i)
    })

    val newSolution = neighbours.foldLeft(Option(solution))((s, c) => {
      s.flatMap(m => m(c) match {
        case xs if xs.contains(i) && xs.size == 1 => None
        case xs if xs.contains(i) => Some(m.updated(c, m(c) - i))
        case xs => s
      })
    })

    if (debug) {
      println(s"Base cell: $cell, targeted cell: $affectedCells, neighbours: $neighbours")
      newSolution.foreach(prettyPrint)
    }

    newSolution.flatMap(s =>
      affectedCells.foldLeft(Option(s))((s2, c) => s2.flatMap(m =>
        eliminate(m(c).head, c, m))))
  }

  def filterOption[A, B](xs: Map[A, B]) = if (xs.isEmpty) None else Some(xs)

  def solve(puzzle: Map[String, Set[Char]]): Option[Map[String, Set[Char]]] = {
    def solve_(nextPuzzle: Map[String, Set[Char]],
               cell: String,
               balance: Set[Char]): Option[Map[String, Set[Char]]] =
      if (balance.isEmpty) None
      else eliminate(balance.head, cell, puzzle.updated(cell, Set(balance.head))).flatMap(solve(_))
        .orElse(solve_(nextPuzzle, cell, balance.tail))

    filterOption(puzzle.filter(_._2.size > 1)).map(_.toList.minBy(_._2.size)).headOption match {
      case None => Some(puzzle)
      case Some((cord, nums)) => {

        if (debug) {
          println(s"$cord => $nums")
          prettyPrint(puzzle)
          println
        }

        solve_(puzzle, cord, nums)
      }
    }
  }

  def isSolved(solution: Map[String, Set[Char]]) = {
    val result = all.map(y => y.foldLeft(Set.empty[Char])((s,z) => s ++ solution(z)))
    result.size == 1 && result.headOption.map(_.size == 9).getOrElse(false)
  }
}

object SudokuApp extends App {

  import Sudoku._

  /* unit tests */

  assert(peers("A1") == Set(
    Set("C1", "B3", "A2", "B2", "C3", "A3", "B1", "C2"),
    Set("A9", "A4", "A5", "A2", "A6", "A8", "A3", "A7"),
    Set("G1", "C1", "H1", "D1", "I1", "E1", "F1", "B1")))

  /* unit tests end */

  val emptyPuzzle = "3..62..7....7...2...........5...81......4...8.........7.25......4....8........3.."

  // val emptyPuzzle = "8.....4..72......9..4.........1.7..23.5...9...4...........8..7..17..............."

  val startMills = System.currentTimeMillis

  prettyFormat(emptyPuzzle) foreach println

  solve(init(emptyPuzzle)) match {
    case Some(solved) =>
      if (isSolved(solved)) prettyFormat(solved.toList.sortBy(_._1).map(_._2.head).mkString).foreach(println)
      else println("Sudoku solution is incorrect")
    case None => println("Sudoku is not solved")
  }

  println(s"\nTime taken: ${System.currentTimeMillis - startMills}ms")
}

object ManySudokuApp extends App {

  import java.util.concurrent.ForkJoinPool

  import Sudoku._

  import scala.concurrent.duration._
  import scala.concurrent.{Await, ExecutionContext, Future}
  import scala.language.postfixOps

  implicit val ec = ExecutionContext.fromExecutorService(new ForkJoinPool)

  val verbose = false

  val defaultPuzzleFn = "hardest17.txt"
  val puzzlesFtr = Future(scala.io.Source.fromInputStream(
    getClass.getResourceAsStream(args.headOption.getOrElse(defaultPuzzleFn)))).map(_.getLines)

  val startMills = System.currentTimeMillis

  val solvedPuzzles = puzzlesFtr.flatMap(puzzles => Future.sequence(puzzles.map {puzzle =>
    Future {
      if (verbose) {
        prettyFormat(puzzle) foreach println

        solve(init(puzzle)) match {
          case Some(solved) =>
            if (isSolved(solved)) prettyFormat(solved.toList.sortBy(_._1).map(_._2.head).mkString).foreach(println)
            else println("Sudoku solution is incorrect")
          case None => println("Sudoku is not solved")
        }
      } else {
        solve(init(puzzle))
      }
    }
  }))

  Await.ready(solvedPuzzles, 20 minute)

  val output = solvedPuzzles
    .map(x => s"\nTime taken: ${System.currentTimeMillis - startMills}ms")
    .recover {case t => if (args.isEmpty) "Default puzzles not found" else s"${args.head} not found."}

  println(Await.result(output, 10 second))
}
