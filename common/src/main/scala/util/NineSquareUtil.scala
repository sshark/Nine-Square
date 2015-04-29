package org.teckhooi.ninesquare.util

/**
 * Copyright (C) March 21, 2013
 *
 * Solves a 9x9 Suduko puzzle with guidance and hints from the following websites,
 *
 * <a>http://stackoverflow.com/questions/15469303/how-do-i-accumulate-results-without-using-a-mutable-arraybuffer</a>
 * <a>http://scala-programming-language.1934581.n4.nabble.com/25-lines-Sudoku-solver-in-Scala-td1987506.html</a>
 *
 * Finally, using constraint propagation and search as demonstrated by Peter Norvig here, <a>http://norvig.com/sudoku.html</a>,
 * using Python, I managed to solve each puzzle in less than a second.
 *
 * search and eliminate functions were refactored into functional style after seeking advices from the community,
 * please refer to
 *
 * http://stackoverflow.com/questions/17771573/suggestions-to-refactor-a-scala-function-with-multiple-exits/17783001
 *
 * for the discussion details.
 *
 * @author Lim, Teck Hooi
 *
 */
object NineSquareUtil {
  type Estimates = Map[Int, List[Int]]
  type Entry = (Int, List[Int])

  val byNumsSize: Ordering[Entry] = Ordering.by(_._2.size)

  /**
   * Translate row and column to a linear position arranged in a big cell, a 3x3 cells block, orientation.
   *
   * For example, the positions are layout as shown even though the numbers are stored in a single dimension
   * consecutive List
   *
   * 0 1 2 |  9 10 11 | 18 19 20
   * 3 4 5 | 12 13 14 | 21 22 23
   * 6 7 8 | 15 16 17 | 24 25 26
   *
   * @param row row together with column to translate
   * @param column column together with row to translate
   * @return an index ranging from 0 to 81 (exclusive)
   */
  def bigCellTranslation(row : Int, column : Int) = row / 27 * 27 + row / 3 % 3 * 3 + column % 3 + column / 3 * 9

  /**
   * Translate row and column to a linear position arranged horizontally.
   *
   * For example,
   *
   *  0  1  2 |  3  4  5 |  6  7  8
   *  9 10 11 | 12 13 14 | 15 16 17
   * 18 19 20 | 21 22 23 | 24 25 26
   *
   * @param row row together with column to translate
   * @param column column together with row to translate
   * @return an index ranging from 0 to 81 (exclusive)
   */
  def horizontalTranslation(row : Int, column: Int) = row / 9 * 9 + column

  /**
   * Translate row and column to a linear position arranged vertically.
   *
   * For example,
   *
   *  0  9 18
   *  1 10 19
   *  2 11 20
   *  -------
   *  3 12 21
   *  4 13 22
   *  5 14 23
   *  -------
   *  6 15 24
   *  7 16 25
   *  8 17 26
   *
   * @param row row together with column to translate
   * @param column column together with row to translate
   * @return an index ranging from 0 to 81 (exclusive)
   */
  def verticalTranslation(row : Int, column : Int) = row % 9 + column * 9

  /**
   * The big cell, a 3x3 cells block, index for a given position in a linear array.
   *
   * @param pos a position in a single dimension List for a 9x9 board.
   * @return the region where the given position in
   */
  def bigCellIndexAt(pos : Int) = pos / 27 * 3 + pos / 3 % 3

  // find all horizontal positions and convert them to a map of position and list of related positions excluding itself.
  private lazy val horizontal = (0 until 81 by 9).flatMap(x => (0 until 9) map (y => horizontalTranslation(x, y))).grouped(9).toSet
  private lazy val horizontalMap = horizontal map (v => v.foldLeft(Map[Int, Set[Int]]())((m, w) => m ++ Map(w -> (v.toBuffer - w).toSet))) reduce (_ ++ _)

  // find all vertical positions and convert them to a map of position and list of related positions excluding itself.
  private lazy val vertical = (0 until 81).flatMap(x => (0 until 9) map (y => verticalTranslation(x, y))).grouped(9).toSet
  private lazy val verticalMap = vertical map (v => v.foldLeft(Map[Int, Set[Int]]())((m, w) => m ++ Map(w -> (v.toBuffer - w).toSet))) reduce (_ ++ _)

  // find all big cell positions and convert them to a map of position and list of related positions excluding itself.
  private lazy val bigCell = (0 until 81 by 3).flatMap(x => (0 until 9) map (y => bigCellTranslation(x, y))).grouped(9).toSet
  private lazy val bigCellMap = bigCell map (v => v.foldLeft(Map[Int, Set[Int]]())((m, w) => m ++ Map(w -> (v.toBuffer - w).toSet))) reduce (_ ++ _)

  /**
   * Guess the list of possible numbers to solve the puzzle for all the un-filled squares in an "empty" puzzle. It is not
   * totally empty but filled with seeds.
   *
   * @param l seed board in a List. The List of numbers must be arranged horizontally
   * @param pos guess the possible numbers for this position
   * @return List of valid numbers for the given position
   */
  def findGuesses(l: List[Int], pos: Int): List[Int] = (1 to 9).filter(!isConflictAt(l, pos, _)).toList

  def search(estimates : List[Int]) : Map[Int, List[Int]] = search(toMapWithGuesses(estimates))

  /**
   * Search for the solution based on the given board estimation. Each estimation is closer to the correct solution.
   *
   * @param estimates an estimated solution
   * @return an estimation closer to the solution than the previous estimation. Otherwise, it will return an empty Map if
   *         it cannot find the solution
   */
  def search(estimates : Estimates) : Estimates =
  // and finally,
    estimates
      .toSeq
      .filter(_._2.size != 1)
      .reduceOption[Entry](byNumsSize.min)
      .map(entry => (if (entry._2.size > 1) eliminate(estimates, entry) else None)
        .getOrElse(Map.empty))
      .getOrElse(estimates) // all sizes are equal to 1

  /**
   * It is a helper method to search function for elimination
   *
   * @param estimates an estimation
   * @param entry position and list of numbers to eliminate
   * @return an estimation closer to the solution
   */
  def eliminate(estimates : Estimates, entry: Entry) : Option[Estimates] = {
    val pos = entry._1
    entry._2
      .view // process the sequence lazily to run each step only when needed
      .map(n => search(eliminate(estimates.updated(pos, List(n)), pos, n)))
      .collectFirst({ case es if !es.isEmpty => es }) // better/safer than != Map.empty
  }

  /**
   * Eliminate the other invalid numbers in relation to the current cell that are located horizontally, vertically
   * or within the big cell.
   *
   * @param estimates an estimation
   * @param position position of the cell to set for the given number
   * @param num number to set for the given position
   * @return an estimation with reduced number of guesses. The reduction is based on their positions relative to the
   *         given number and position
   */
  def eliminate(estimates : Map[Int, List[Int]], position : Int, num : Int) : Map[Int, List[Int]] = {
    val peerPositions = verticalMap(position) ++ horizontalMap(position) ++ bigCellMap(position)
    val targetPositions = peerPositions.filter(pos => estimates(pos).size == 2 && estimates(pos).contains(num))
    val peers = peerPositions.foldLeft(estimates){case (m, pos) => m.updated(pos, m(pos).filterNot(_ == num))}
    targetPositions.foldLeft(peers){case (m, pos) => if (m(pos).isEmpty) m else m ++ eliminate(m, pos, m(pos).head)}
  }

  /**
   * Solves the puzzle starting.
   *
   * @param puzzle solve puzzle
   *
   */
  def solve(puzzle : List[Int]) = {
    search(NineSquareUtil.toMapWithGuesses(puzzle)).toList.sortBy(_._1).flatMap(_._2)
  }

  /**
   * Check if any conflict within the horizontal row, vertical row and within the cell. Skips positions with zeros
   *
   * This method is deprecated because brute force is not used to solve the puzzle.
   *
   * @param board array to check
   * @param pos position to check
   * @param n number to check for duplication
   * @param i checking index
   * @return false if there is no conflict else true
   *
   */
  def isConflictAt(board: List[Int], pos: Int, n: Int, i: Int = 0): Boolean =
    i < 9 && (board(horizontalTranslation(pos, i)) == n || board(verticalTranslation(pos, i)) == n ||
      board(bigCellTranslation(pos, i)) == n || isConflictAt(board, pos, n, i + 1))

  /**
   * Verify the sheet is populated with the right numbers.
   *
   * @param board List of numbers to verify
   * @param i position to verify
   * @return true if the sheet is populated with the right numbers. Otherwise, it is false.
   */
  def isSheetOK(board: List[Int], i: Int = 0): Boolean =
    if (i == board.length) true
    else if (board(i) == 0) isSheetOK(board, i + 1)
    else if (isConflictAt(board.updated(i, -1), i, board(i))) false
    else isSheetOK(board, i + 1)

  def toMapWithGuesses(board : List[Int]) = board.zipWithIndex.map {
    case (0, pos) => (pos, findGuesses(board, pos))
    case (x, pos) => (pos, List(x))
  }.toMap
}
