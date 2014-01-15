package org.teckhooi.ninesquare.gui

import swing._
import event.ButtonClicked
import javax.swing.border.EmptyBorder
import java.awt.Dimension
import org.slf4j.LoggerFactory

/**
 *
 *
 * @author Lim, Teck Hooi
 *
 */
class NumberPadDialog(owner : Window) extends Dialog(owner) {

  def logger = LoggerFactory.getLogger(getClass)

  val buttonPreferredSize = new Dimension(60, 60)
  var value = ""
  modal = true
  resizable = false

  val closeButton = new Button("Close") {
    font = font.deriveFont(8f)
    preferredSize = buttonPreferredSize
  }
  listenTo(closeButton)

  val clearButton = new Button("Clear") {
    font = font.deriveFont(8f)
    preferredSize = buttonPreferredSize
  }
  listenTo(clearButton)

  contents = new GridPanel(4, 3) {
    border = new EmptyBorder(2, 2, 2, 2)
    (1 to 9) foreach (x => {
      val b = new Button(x.toString) {
        preferredSize = buttonPreferredSize
      }
      NumberPadDialog.this.listenTo(b)
      contents += b
    })

    contents += closeButton

    val blank = new Label(" ")
    blank.preferredSize = buttonPreferredSize
    contents += blank

    contents += clearButton
  }
  pack()
  centerOnScreen()

  reactions += {
    case ButtonClicked(`closeButton`) => {
      value = "CLOSE"
      close()
    }

    case ButtonClicked(`clearButton`) => {
      value = ""
      close()
    }

    case ButtonClicked(n) => {
      value = n.text
      close()
    }
  }

  def edit(c : Cell) = {
    logger.debug("Edit " + c.value + " at " + c.pos)
    if (c.value != 0) value = c.value.toString
    open()
    value
  }
}
