package org.teckhooi.ninesquare

import swing._
import event.ButtonClicked
import javax.swing.{KeyStroke, SwingUtilities, UIManager}
import javax.swing.plaf.ColorUIResource


/**
 *
 * @author Lim, Teck Hooi
 *
 *
 */

object DynamicWidgets extends SimpleSwingApplication {

  import scala.collection.JavaConversions._

  UIManager.getDefaults().put("Button.disabledText", new ColorUIResource(255, 0, 0))
  UIManager.getDefaults.entrySet().filter(_.getKey.toString.startsWith("Button.disabledText")).foreach(println)

  def top = new MainFrame {
    title = "Replacable Widgets"
    pack()
    visible = true
    resizable = false



    val sheetPanel = new GridPanel(4, 4)

    var labels = (1 to 16) map (x=> {
      val l = new Button(x.toString)
      l.preferredSize = new Dimension(80, 80)
      sheetPanel.contents += l
      listenTo(l)
      l
    })

/*
    var labels = NineSquareUtil.generateRandomSheet(NineSquareUtil.EASY_LEVEL)._1.zipWithIndex.map {
      case (x, pos) => if (x == 0) {
        val b = Cell(x, pos)
        listenTo(b)
        b
      } else ReadOnlyCell(x, pos)
    }
    labels foreach (x=> sheetPanel.contents += x)
*/

    contents = sheetPanel

    val redrawAction = Action("Redraw") {
      SwingUtilities.invokeLater(new Runnable {
        def run() {
          labels.foreach(l => deafTo(l))
          sheetPanel.contents.clear
          labels foreach (x => {
            x.text = (x.text.toInt + 1).toString
            sheetPanel.contents += x
            listenTo(x)
          })
          sheetPanel.revalidate()
          sheetPanel.repaint()
        }
      })
    }
    redrawAction.accelerator = Some(KeyStroke.getKeyStroke("F3"))

    reactions += {
      case ButtonClicked(n : Button) => println("clicked at..." + n.text)
    }

    menuBar = new MenuBar {
      contents += new MenuItem(redrawAction)
    }
  }
}
