val digits = ('1' to '9').mkString
val alphas = ('A' to 'I').mkString

def cross(rows: String, cols: String) = for {
  col <- cols
  row <- rows} yield {"" + row + col}

val horizontals = digits.map(d => cross(alphas, d.toString))
val verticals = alphas.map(a => cross(a.toString, digits))
val blocks = for {
  colBlk <- alphas.grouped(3)
  rowBlk <- digits.grouped(3)
} yield (cross(colBlk, rowBlk))

val all = horizontals ++ verticals ++ blocks

val emptyPuzzle = "3..62..7....7...2...........5...81......4...8.........7.25......4....8........3.."

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

assert(all.filter(_.contains("A1")).flatten.toSet ==
  Set("A9", "G1", "A4", "C1", "H1", "A5", "D1", "I1", "E1", "B3", "A2", "A1", "A6", "B2", "F1", "C3", "A8", "A3", "B1", "A7", "C2"))

assert(all.filter(_.contains(pos(18))).flatten.toSet ==
  Set("C6", "G1", "C1", "H1", "C8", "C7", "D1", "I1", "E1", "C9", "B3", "A2", "C4", "A1", "B2", "F1", "C3", "C5", "A3", "B1", "C2"))

def eliminate(i: Char, ndx: Int, solution: Map[String, Set[Char]]): Map[String, Set[Char]] =
  eliminate(i, pos(ndx), solution)

def eliminate(i: Char, cell: String, solution: Map[String, Set[Char]]) = {
  val cells = all.filter(_.contains(cell)).flatten.toSet
  if (i == '0') {
    val cells = all.filter(_.contains(cell)).flatten.toSet
    cells.foldLeft(solution)((m,c) => m + m.get(c).map(x => c -> (x - i)).getOrElse(c -> (digits.toSet - i)))
  } else {
    (cells - cell).foldLeft(solution + (cell -> Set(i)))((m,c) =>
      m + m.get(c).map(x => c -> (x - i)).getOrElse(c -> (digits.toSet - i)))
  }
}

// println(eliminate('3', pos(6), Map[String, Set[Char]]()))

def init(puzzle: String): Map[String, Set[Char]] = {
  def _init(cells: List[(Char, Int)], solution: Map[String, Set[Char]]): Map[String, Set[Char]] = cells match {
    case Nil => solution
    case x :: xs => x match {
      case ('.', ndx) => _init(xs, eliminate('0', pos(ndx), solution))
      case (d, ndx) => _init(xs, eliminate(d, pos(ndx), solution))
    }
  }

  _init(puzzle.zipWithIndex.toList, Map[String, Set[Char]]())
}

/*
println(init(emptyPuzzle).toList.sortBy(_._1))
println
*/

println(init(emptyPuzzle).filter(_._2.size > 1).toList.sortBy(_._2.size))

def solve(puzzle: Map[String, Set[Char]]): Option[Map[String, Set[Char]]] = {
  def _solve(guesses: Set[Char], cord: String, solution: Map[String, Set[Char]]): Option[Map[String, Set[Char]]] = 
    guesses.toList match {
      case Nil => None
      case x +: xs => 
        val nextStage = eliminate(x, cord, solution)
        if (nextStage.exists(_._2.isEmpty)) _solve(guesses.tail, cord, solution)
        else Some(nextStage)
    }

  val sortedPuzzle = puzzle.filter(_._2.size > 1).toList.sortBy(_._2.size)
  sortedPuzzle match {
    case Nil => Some(puzzle)
    case (cord, nums) :: xs => _solve(nums, cord, xs.toMap) match {
      case None => None
      case Some(m) => solve(m)
    }
  }
}

val semiPuzzle = init(emptyPuzzle).filter(_._2.size > 1).toList.sortBy(_._2.size)

println(solve(init(emptyPuzzle)))
