package org.teckhooi.ninesquare

import java.awt.{Font, Color, Dimension}
import swing.Button
import util.NineSquareUtil

/**
 *
 * @author Lim, Teck Hooi
 *
 *
 */

case class Cell(var value : Int, pos : Int, error : Boolean = false, editable : Boolean = true) extends Button {
  preferredSize = new Dimension(50, 50)
  text = if (value == 0) " " else value.toString
  opaque = true
  enabled = editable
  background = if (error) Color.RED
    else if (NineSquareUtil.bigCellIndexAt(pos) % 2 == 0) Color.LIGHT_GRAY else Color.DARK_GRAY
}

case class ReadOnlyCell(var value : Int, pos : Int) extends Button(value.toString) {
  font = font.deriveFont(18f).deriveFont(Font.BOLD)
  preferredSize = new Dimension(50, 50)
  opaque = true
  foreground = Color.BLACK
  background = if (NineSquareUtil.bigCellIndexAt(pos) % 2 == 0) Color.LIGHT_GRAY else Color.DARK_GRAY
}


