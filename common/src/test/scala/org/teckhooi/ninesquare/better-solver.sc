object BetterSolverApp extends App {
  type Sheet = Map[String, Set[Char]]

  val digits = ('1' to '9').mkString
  val alphas = ('A' to 'I').mkString

  def cross(rows: String, cols: String) = for {
    row <- rows
    col <- cols} yield {"" + row + col}

  val horizontals = digits.map(d => cross(alphas, d.toString))
  val verticals = alphas.map(a => cross(a.toString, digits))
  val blocks = for {
    colBlk <- alphas.grouped(3)
    rowBlk <- digits.grouped(3)
  } yield (cross(colBlk, rowBlk))

  val all = horizontals ++ verticals ++ blocks

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

  assert(all.filter(_.contains("A1")).flatten.toSet ==
    Set("A9", "G1", "A4", "C1", "H1", "A5", "D1", "I1", "E1", "B3", "A2", "A1", "A6", "B2", "F1", "C3", "A8", "A3", "B1", "A7", "C2"))

  assert(all.filter(_.contains(pos(18))).flatten.toSet ==
    Set("C6", "G1", "C1", "H1", "C8", "C7", "D1", "I1", "E1", "C9", "B3", "A2", "C4", "A1", "B2", "F1", "C3", "C5", "A3", "B1", "C2"))

  // println(eliminate('3', pos(6), Map[String, Set[Char]]()))

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

  prettyPrint(init(emptyPuzzle))

  def isConflict(cell: String, solution: Map[String, Set[Char]]) = {
    def isConflictPerCat(xs: Seq[Set[Char]]): Boolean =  
      xs match {    
        case ys if ys.head.isEmpty => true
        case ys if ys.size == 1 => false
        case ys => ys.tail.contains(ys.head) || isConflictPerCat(xs.tail)
      }

    all.filter(_.contains(cell))
      .map(xs => xs.map(solution(_)).filter(_.size < 2))
      .exists(isConflictPerCat)
  }

  def eliminate(i: Char, cell: String, solution: Map[String, Set[Char]]): Option[Map[String, Set[Char]]] = {
    val cells = all.filter(_.contains(cell)).flatten.toSet - cell
    val newSolution = cells.foldLeft(solution)((m, c) => m + (c -> (m(c) - i)))
    if (isConflict(cell, newSolution)) None else Some(newSolution)
  }

  def solve(puzzle: Map[String, Set[Char]]): Option[Map[String, Set[Char]]] = {
    puzzle.filter(_._2.size > 1).toList.sortBy(_._2.size).headOption match {
      case None => Some(puzzle)
      case Some((cord, nums)) => {
        println(s"$cord => $nums")       
        prettyPrint(puzzle)
       nums.map(num => eliminate(num, cord, puzzle + (cord -> Set(num))) match {
          case Some(x) => solve(x)
          case _ => None
        }).flatten.headOption
      }
    }
  }

  println(solve(init(emptyPuzzle)))
}

    /*
    def _solve(guesses: Set[Char], cord: String, solution: Map[String, Set[Char]]): Option[Map[String, Set[Char]]] =
      guesses.toList match {
        case Nil => None
        case x +: xs =>
          val nextStage = eliminate(x, cord, solution)
          if (nextStage.exists(_._2.isEmpty)) _solve(guesses.tail, cord, solution)
          else solve(nextStage) match {
            case None =>  _solve(guesses.tail, cord, solution)
            case x => x
          }
      }

    val sortedPuzzle = puzzle.filter(_._2.size > 1).toList.sortBy(_._2.size)
    prettyPrint(puzzle)
    sortedPuzzle match {
      case Nil => Some(puzzle)
      case (cord, nums) :: xs => _solve(nums, cord, puzzle) match {
        case None => None
        case Some(m) => solve(m)
      }

def prettyPrintForInt(puzzle: Map[String, Set[Int]]) =
    puzzle.toList.sortBy(_._1).map(_._2).grouped(9).toList.foreach(line => {
      line.foreach(x => {x.foreach(print); print(" " * (9 - x.size))})
      println
    })

  val solved = Map("D7" -> Set(1), "C6" -> Set(3), "A9" -> Set(4), "G1" -> Set(7), "B5" -> Set(8), "H2" -> Set(4), "E8" -> Set(6), "I5" -> Set(1), "F9" -> Set(5), "A4" -> Set(6), "C1" -> Set(9), "D2" -> Set(5), "F4" -> Set(9), "G5" -> Set(9), "H1" -> Set(1), "E3" -> Set(3), "H6" -> Set(7), "B9" -> Set(3), "D8" -> Set(3), "C8" -> Set(8), "C7" -> Set(6), "A5" -> Set(2), "I2" -> Set(6), "D1" -> Set(6), "B6" -> Set(9), "I1" -> Set(5), "E9" -> Set(8), "I6" -> Set(2), "H5" -> Set(6), "F3" -> Set(1), "E2" -> Set(9), "G4" -> Set(5), "F7" -> Set(2), "E1" -> Set(2), "C9" -> Set(1), "D5" -> Set(7), "E6" -> Set(5), "F2" -> Set(7), "I8" -> Set(9), "B3" -> Set(6), "G8" -> Set(1), "H4" -> Set(3), "A2" -> Set(8), "I3" -> Set(8), "C4" -> Set(4), "H9" -> Set(2), "G3" -> Set(2), "A1" -> Set(3), "H8" -> Set(5), "B7" -> Set(5), "A6" -> Set(1), "B2" -> Set(1), "I7" -> Set(3), "G7" -> Set(4), "E5" -> Set(4), "D4" -> Set(2), "F6" -> Set(6), "D9" -> Set(9), "F1" -> Set(8), "C3" -> Set(7), "I9" -> Set(7), "E7" -> Set(7), "F8" -> Set(4), "A8" -> Set(7), "H3" -> Set(9), "B4" -> Set(7), "G2" -> Set(3), "C5" -> Set(5), "A3" -> Set(5), "D6" -> Set(8), "B1" -> Set(4), "I4" -> Set(8), "G9" -> Set(6), "H7" -> Set(8), "B8" -> Set(2), "G6" -> Set(4), "A7" -> Set(9), "F5" -> Set(3), "C2" -> Set(2), "D3" -> Set(4), "E4" -> Set(1))

  prettyPrintForInt(solved)

    */
