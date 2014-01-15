package org.teckhooi.ninesquare.gui

import swing._
import scala.swing.event.{Key, ButtonClicked}
import javax.swing.border.EmptyBorder
import java.lang.NumberFormatException
import java.io.File
import javax.swing.{ImageIcon, UIManager, KeyStroke, SwingUtilities}
import java.awt.event.{ActionEvent, KeyEvent}
import java.awt.Color
import scala.io.Source
import scala.util.Random
import org.slf4j.LoggerFactory
import org.teckhooi.ninesquare.util.NineSquareUtil

/**
 *
 * Main eSudoku application window.
 *
 * @author Lim, Teck Hooi
 *
 */
object NineSquareApp extends SimpleSwingApplication {

  def logger = LoggerFactory.getLogger(getClass)

  UIManager.getDefaults().put("Button.disabledText",Color.WHITE);

  lazy val easyPuzzles = Source.fromInputStream(getClass.getResourceAsStream("/easy.txt")).getLines.toList
  lazy val hardPuzzles = Source.fromInputStream(getClass.getResourceAsStream("/hard.txt")).getLines.toList

  val useScreenMenuBar = isOSX() && !isSystemPropertyTrueFor("ignore.useScreenMenu")

  if (useScreenMenuBar) System.setProperty("apple.laf.useScreenMenuBar", "true")

  def top = new MainFrame {
    title = "Nine Square"
    resizable = false
    iconImage = toolkit.getImage((getClass.getResource("/images/nine-square.png")))
    val numberPadDialog = new NumberPadDialog(owner)
    var cells : List[Button] = randomlyPickAPuzzleFrom(easyPuzzles).zipWithIndex.map {
      case (x, pos) => if (x == 0) Cell(x, pos) else ReadOnlyCell(x, pos)
    }.toList

    // Grid panel
    val sheetPanel = new GridPanel(9, 9) {
      border = new EmptyBorder(4, 4, 4, 4)
      hGap = 4
      vGap = 4
    }

    // File menu
    val loadMenuItem = new MenuItem(Action("Load") {
      val puzzlePicker = new FileChooser(new File("."))
      puzzlePicker.title = "Load Puzzle"
      val result = puzzlePicker.showOpenDialog(null)
      if (result == FileChooser.Result.Approve) {
        // TODO load puzzle
        logger.debug("Picked puzzle file " + puzzlePicker.selectedFile.toString)
      }
    })

/*
    val saveMenuItem = new MenuItem(Action("Save") {

    })
*/

    val quitMenuItem = new MenuItem(Action("Quit") {
      System.exit(0)
    })

    // Puzzle menu
    val newEasyGameAction = Action("New Easy Puzzle") {
      logger.debug("F2 pressed")
      refresh(randomlyPickAPuzzleFrom(easyPuzzles).zipWithIndex.map {
        case (x, pos) => if (x == 0) Cell(x, pos) else ReadOnlyCell(x, pos)
      }.toList)
    }
    newEasyGameAction.mnemonic = KeyEvent.VK_E
    newEasyGameAction.accelerator = Some(KeyStroke.getKeyStroke("F2"))

    val newHardGameAction = Action("New Hard Puzzle") {
      logger.debug("F4 pressed")
      refresh(randomlyPickAPuzzleFrom(hardPuzzles).zipWithIndex.map {
        case (x, pos) => if (x == 0) Cell(x, pos) else ReadOnlyCell(x, pos)
      }.toList)
    }
    newHardGameAction.mnemonic = KeyEvent.VK_H
    newHardGameAction.accelerator = Some(KeyStroke.getKeyStroke("F4"))

    val fileMenu = new Menu("File") {
      contents += new MenuItem(newEasyGameAction)
      contents += new MenuItem(newHardGameAction)
      contents += new Separator
      contents += loadMenuItem
//      contents += saveMenuItem

      // quit is provided by the standard OS X menu. But remember to include -Dapple.laf.useScreenMenuBar=true
      // during runtime
      if (!useScreenMenuBar) {
        contents += new Separator
        contents += quitMenuItem
      }

      mnemonic = Key.F
    }

    val solveAction = Action ("Solve...") {
      val emptySheet = sheetPanel.contents.map{
        case _ : Cell => 0
        case ReadOnlyCell(x, _) => x
      }

      val solvedSheet = NineSquareUtil.search(emptySheet.toList).toList.sortBy(_._1).flatMap(_._2)
      refresh(solvedSheet.zipWithIndex.map {
           case (x, pos) => if (emptySheet(pos) == 0) Cell(x, pos, false, false) else ReadOnlyCell(x, pos)
         })
    }

/*
    val verifyInputsAction = Action("Check Puzzle...") {

    }
*/

    val clearPuzzleAction = Action("Clear Puzzle") {
      invokeLater {
        sheetPanel.contents.foreach {
          case c:Cell => {
            c.value = 0
            c.text = ""
          }
          case _ =>
        }
        sheetPanel.repaint()
      }
    }
    clearPuzzleAction.accelerator = Some(KeyStroke.getKeyStroke("F7"))

    val puzzleMenu = new Menu("Puzzle") {
      contents += new MenuItem(solveAction)
//      contents += new MenuItem(verifyInputsAction)
      contents += new Separator
      contents += new MenuItem(clearPuzzleAction)

      mnemonic = Key.Z
    }

    val dialogIcon = new ImageIcon(getClass.getResource("/images/nine-square_48x48.png")) // icon for dialog boxes

    // Help menu
    val helpAction = Action("How to play...") {
      Dialog.showMessage(null,
        "Fill in the grid so that every row, every column and every 3x3 box contains the digits 1 through 9.",
        "How to play...",
        Dialog.Message.Question,
        dialogIcon)
    }
    helpAction.mnemonic = KeyEvent.VK_H
    helpAction.accelerator = Some(KeyStroke.getKeyStroke("F1"))
    val helpMenuItem = new MenuItem(helpAction) {
      mnemonic = Key.P
    }

    val aboutAction = Action("About Nine Square") {
      Dialog.showMessage(null, "Copyright 2013 Lim, Teck Hooi\n\n" +
        "Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
        "you may not use this file except in compliance with the License.\n" +
        "You may obtain a copy of the License at\n\n" +
        "    http://www.apache.org/licenses/LICENSE-2.0\n\n" +
        "Unless required by applicable law or agreed to in writing, software\n" +
        "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
        "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
        "See the License for the specific language governing permissions and\n" +
        "limitations under the License.",
        "About...", Dialog.Message.Info, dialogIcon)
    }
    aboutAction.mnemonic = KeyEvent.VK_A
    aboutAction.accelerator = Some(KeyStroke.getKeyStroke('A', ActionEvent.CTRL_MASK))
    val aboutMeuItem = new MenuItem(aboutAction)

    val helpMenu = new Menu("Help") {
      contents += helpMenuItem
      contents += new Separator()
      contents += aboutMeuItem

      mnemonic = Key.H
    }

    menuBar = new MenuBar {
      contents += fileMenu
      contents += puzzleMenu
      contents += helpMenu
    }

    cells.foreach(c => listenTo(c))
    reactions += {
      case ButtonClicked(cell: Cell) => {
        val userClickedNum = numberPadDialog.edit(cell)
        logger.debug("User clicked " + userClickedNum)
        userClickedNum match {
          case "" => {
            invokeLater {
                cell.value = 0
                cell.text = ""
                cell.revalidate()
            }
          }

          case num => {
            invokeLater {
                try {
                  cell.value = userClickedNum.toInt
                  cell.text = userClickedNum
                  cell.repaint()
                } catch {
                  case _ : NumberFormatException => // nothing
                }
            }
          }
        }
      }
    }

    contents = sheetPanel
    cells.foreach(c => sheetPanel.contents += c)

    centerOnScreen()
    pack()
    visible = true

    private def refresh(newCells : List[Button]) {
      invokeLater {
        cells.foreach(c => deafTo(c))
        sheetPanel.contents.clear()
        newCells.foreach(c => {
          sheetPanel.contents += c
          listenTo(c)
        })
        cells = newCells
        sheetPanel.revalidate()
        sheetPanel.repaint()
      }
    }

    private def invokeLater(f: Unit) {
      SwingUtilities.invokeLater(new Runnable {
        def run() = f
      })
    }
  }

  private def randomlyPickAPuzzleFrom(puzzles : List[String]) =
    puzzles(Random.nextInt(puzzles.size)).replace('.', '0').map(_ - 0x30)

  private def isOSX() = System.getProperty("os.name").contains("OS X")

  private def isSystemPropertyTrueFor(name : String) = sys.props.get(name) exists (_ equalsIgnoreCase "true")
}
