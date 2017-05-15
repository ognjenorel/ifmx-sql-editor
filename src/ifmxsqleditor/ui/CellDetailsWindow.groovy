// Copyright 2017 Ognjen Orel
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
import javax.swing.JScrollPane
import java.awt.event.KeyEvent
import java.awt.event.KeyListener

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
            scrollPane(horizontalScrollBarPolicy: JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED,
                       verticalScrollBarPolicy: JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED) {
               ta = swing.textArea(text: '', rows: 18, columns: 40, editable: false, autoscrolls: true, lineWrap: true, wrapStyleWord: true)
            }
         }
      }
      frame.pack()
      swing.dataDetailsFrame.addKeyListener keyListener
      swing.ta.addKeyListener keyListener
   }

   void show(def data, def parent) {
      swing.ta.text = data
      //GUIUtils.centerOnScreen swing.dataDetailsFrame
      swing.dataDetailsFrame.setLocationRelativeTo parent
      swing.dataDetailsFrame.setVisible true
      swing.ta.requestFocus()
   }
}


