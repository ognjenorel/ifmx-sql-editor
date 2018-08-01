// Copyright 2018 Ognjen Orel
//
// This file is part of IFMX SQL Editor.
//
// IFMX SQL Editor is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// IFMX SQL Editor is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with IFMX SQL Editor.  If not, see <http://www.gnu.org/licenses/>.

package ifmxsqleditor.ui

import groovy.swing.SwingBuilder

import javax.imageio.ImageIO
import javax.imageio.stream.ImageInputStream
import javax.swing.ImageIcon
import javax.swing.JLabel
import javax.swing.JScrollPane
import java.awt.Dimension
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.image.BufferedImage
import java.awt.image.Raster

/**
 * Created by IntelliJ IDEA.
 * User: ognjen
 * Date: 11.05.17.
 */
class CellDetailsWindow {

   SwingBuilder swing
   def keyListener

   def CellDetailsWindow() {

      swing = new SwingBuilder()

      def frame = swing.frame(title: 'Details', id: 'dataDetailsFrame', resizable: true, alwaysOnTop: true) {

         keyListener = [
               keyPressed: {event ->
                     if (event.keyCode == KeyEvent.VK_ESCAPE) {
                           swing.dataDetailsFrame.setVisible false
                     }
                  },
               keyReleased: {},
               keyTyped: {}
         ] as KeyListener

         vbox {
            sp1 = scrollPane(horizontalScrollBarPolicy: JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED,
                       verticalScrollBarPolicy: JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                       visible: true) {
               ta = swing.textArea(text: '', rows: 18, columns: 40, editable: false, autoscrolls: true, lineWrap: true, wrapStyleWord: true)
            }
            sp2 = scrollPane(horizontalScrollBarPolicy: JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED,
                       verticalScrollBarPolicy: JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                       visible: false) {
               im = swing.label( )
            }
         }
      }
      frame.pack()
      swing.dataDetailsFrame.addKeyListener keyListener
      swing.ta.addKeyListener keyListener
      swing.im.addKeyListener keyListener
   }

   void show(def data, def parent) {

      if (data instanceof byte[] && ResultSetTableModel.isImage(data)) {
         swing.im.setIcon(new ImageIcon(data))
         swing.im.resize(swing.im.icon.iconWidth, swing.im.icon.iconHeight)
         swing.sp1.setVisible false
         swing.sp2.setVisible true
      }
      else {
         swing.ta.text = data
         swing.sp1.setVisible true
         swing.sp2.setVisible false
      }
      //GUIUtils.centerOnScreen swing.dataDetailsFrame
      swing.dataDetailsFrame.setLocationRelativeTo parent
      swing.dataDetailsFrame.setVisible true
      swing.ta.requestFocus()
   }
}


