object BetterSolverApp extends App {
  type Sheet = Map[String, Set[Char]]

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

  val emptyPuzzle = "3..62..7....7...2...........5...81......4...8.........7.25......4....8........3.."

  // val emptyPuzzle = "8.....4..72......9..4.........1.7..23.5...9...4...........8..7..17..............."

  def pos(ndx: Int) = "" + alphas(ndx / 9) + digits(ndx % 9)

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

  prettyFormat(emptyPuzzle) foreach println

/* test cases will be moved to unit tests
  assert(all.filter(_.contains("A1")).flatten.toSet ==
    Set("A9", "G1", "A4", "C1", "H1", "A5", "D1", "I1", "E1", "B3", "A2", "A1", "A6", "B2", "F1", "C3", "A8", "A3", "B1", "A7", "C2"))

  assert(all.filter(_.contains(pos(18))).flatten.toSet ==
    Set("C6", "G1", "C1", "H1", "C8", "C7", "D1", "I1", "E1", "C9", "B3", "A2", "C4", "A1", "B2", "F1", "C3", "C5", "A3", "B1", "C2"))

  println(eliminate('3', pos(6), Map[String, Set[Char]]()))

*/

  def init(puzzle: String): Map[String, Set[Char]] = {
    def fill(i: Char, cell: String, solution: Map[String, Set[Char]]) = {
      val cells = all.filter(_.contains(cell)).flatten.toSet
      if (i == '0') all.filter(_.contains(cell)).flatten.toSet
        .foldLeft(solution)((m, c) => m + m.get(c).map(x => c -> (x - i)).getOrElse(c -> (digits.toSet - i)))
      else (cells - cell).foldLeft(solution + (cell -> Set(i)))((m,c) =>
        m + m.get(c).map(x => c -> (x - i)).getOrElse(c -> (digits.toSet - i)))
    }

    def _init(cells: List[(Char, Int)], solution: Map[String, Set[Char]]): Map[String, Set[Char]] = cells match {
      case Nil => solution
      case x :: xs => x match {
        case ('.', ndx) => _init(xs, fill('0', pos(ndx), solution))
        case (d, ndx) => _init(xs, fill(d, pos(ndx), solution))
      }
    }

    _init(puzzle.zipWithIndex.toList, Map[String, Set[Char]]())
  }

  // println(init(emptyPuzzle).filter(_._2.size > 1).toList.sortBy(_._2.size))

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

  prettyPrint(init(emptyPuzzle))

 def eliminate(i: Char, cell: String, solution: Map[String, Set[Char]]): Option[Map[String, Set[Char]]] = {

    val neighbours = peers(cell).flatten
    val affectedCells = neighbours.filter(cell => {
      val guesses = solution(cell)
      guesses.size == 2 && guesses.contains(i)
    })

    val newSolution = neighbours.foldLeft(Option(solution))((s, c) => {
      s.flatMap(m => if ((m(c) - i).isEmpty) None else Option(m + (c -> (m(c) - i))))
    })

/*    newSolution.foreach(prettyPrint)
    println(s"Base cell: $cell, targeted cell: $affectedCells, neighbours: $neighbours")
*/
    newSolution.flatMap(s => 
      affectedCells.foldLeft(Option(s))((s2, c) => s2.flatMap(m => eliminate(m(c).head, c, m))))
  }

  def filterOption[A, B](xs: Map[A, B]) = if (xs.isEmpty) None else Some(xs)

  def solve(puzzle: Map[String, Set[Char]]): Option[Map[String, Set[Char]]] = {
    def solve_(nextPuzzle: Map[String, Set[Char]], 
      cell: String,      
      balance: Set[Char]): Option[Map[String, Set[Char]]] = 
      if (balance.isEmpty) None
      else eliminate(balance.head, cell, puzzle + (cell -> Set(balance.head))).flatMap(solve(_)).orElse(solve_(nextPuzzle, cell, balance.tail))

    filterOption(puzzle.filter(_._2.size > 1)).map(_.toList.minBy(_._2.size)).headOption match {
      case None => Some(puzzle)
      case Some((cord, nums)) => {
/*        
        println(s"$cord => $nums")
        prettyPrint(puzzle)
        println
*/        // print2List(puzzle)

        solve_(puzzle, cord, nums)

      }
    }
  }

  // println(solve(init(emptyPuzzle)))

  solve(init(emptyPuzzle)).headOption.foreach(x => prettyFormat(x.toList.sortBy(_._1).map(_._2.head).mkString).foreach(println))
}
