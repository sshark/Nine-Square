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
/*
  val peers = cross(alphas, digits).foldLeft(Map.empty[String, Set[String]])((m,a) => m + (a -> all.filter(_.contains(a))))
    .foldLeft(Set.empty[String])(_ ++ _.toSet)))
*/

  val peers = cross(alphas, digits).foldLeft(Map.empty[String, Seq[Seq[String]]])((m,a) => m + (a -> all.filter(_.contains(a))))

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

  def isConflict(solution: Map[String, Set[Char]]): Boolean = {
    def isConflictPerCat(xs: Seq[Set[Char]]): Boolean =
      if (xs.size < 2) false else xs.tail.contains(xs.head) || isConflictPerCat(xs.tail)
      
    if (solution.exists(_._2.isEmpty)) true else {
      val singleDigit = solution.filter(_._2.size == 1)
      val a = singleDigit.keys
        .map(k => peers(k))
        .map(v => v.map(cells => cells.flatMap(cell => singleDigit.get(cell))))
          
      a.exists(x => x.exists(isConflictPerCat))      
    }
 }

  def eliminate(i: Char, cell: String, solution: Map[String, Set[Char]], 
    neighbours: Set[String]): Option[Map[String, Set[Char]]] = {
    
    val affectedCells = neighbours.filter(cell => {
      val chars = solution(cell)
      chars.size == 2 && chars.contains(i)
    })

    val newSolution = cells.foldLeft(solution)((m, c) => m + (c -> (m(c) - i)))
    prettyPrint(newSolution)
    println(s"Affected: $affectedCells")

    affectedCells.foldLeft(Option(newSolution))((s, c) => s.flatMap(m => eliminate(i, c, m, neighbours - c)))
      .flatMap(xs => if (isConflict(xs)) None else Some(xs))
  } 

  def solve(puzzle: Map[String, Set[Char]]): Option[Map[String, Set[Char]]] = {
    puzzle.filter(_._2.size > 1).toList.sortBy(_._2.size).headOption match {
      case None => Some(puzzle)
      case Some((cord, nums)) => {
        println(s"$cord => $nums")
        prettyPrint(puzzle)
        println
        // print2List(puzzle)
        nums.map(num => eliminate(num, cord, puzzle + (cord -> Set(num)), all.filter(_.contains(cell)).flatten.toSet - cord) match {
          case Some(x) => solve(x)
          case _ => None
        }).flatten.headOption
      }
    }
  }

  // println(solve(init(emptyPuzzle)))

  val zero = List("3","9","841","6","2","41","5","7","41",
  "84561","861","84561","7","89513","49513","496","2","49613",
  "845612","86127","845617","84913","89513","49513","496","849613","49613",
  "4962","5","49673","923","9673","8","1","4963","496273",
  "9612","61273","96173","9123","4","9561273","9627","9563","8",
  "849612","861273","8496173","9123","956173","9561273","49627","49563","4956273",
  "7","8613","2","5","89613","49613","496","4961","4961",
  "9561","4","95613","9123","96173","961273","8","9561","956127",
  "89561","861","89561","84912","89617","496127","3","49561","4956127")

  val locs = cross(alphas, digits)

  def toNumSet(l: List[String]) = locs.zip(l).foldLeft(Map.empty[String, Set[Char]])((m, s) => m + (s._1 -> s._2.toSet))

  println(solve(toNumSet(zero)))
}
