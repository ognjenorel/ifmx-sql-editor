// Copyright 2012 Ognjen Orel
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
import java.awt.Color
import java.awt.FlowLayout
import java.awt.event.MouseAdapter
import javax.swing.BorderFactory
import javax.swing.JScrollPane
import javax.swing.ListSelectionModel
import org.jdesktop.swingx.JXList
import org.jdesktop.swingx.decorator.HighlighterFactory
import ifmxsqleditor.db.ConnectionData
import ifmxsqleditor.db.ConnectionManager

/**
 * Shows all connections.
 */
public class ConnectionsWindow {

   // todo, implement check: conncection alias should be unique

   ConnectionManager connectionManager
   SqlEditorWindow mainWindow
   
   SwingBuilder swing

   def connList
   def connectionEditWindow = new ConnectionEditWindow()
   def connectionsWorkingCopy = new ArrayList<ConnectionData>() // working copy; if user canceles after changes; nothing happens, if saves, just switch

   def ConnectionsWindow(ConnectionManager connectionManager) {
      this.connectionManager = connectionManager
      
      swing = new SwingBuilder()
      connList = new JXList(connectionsWorkingCopy.toArray())
      connList.setCellRenderer new ConnectionListCellRenderer()
      connList.setSelectionMode ListSelectionModel.SINGLE_SELECTION
      connList.setRolloverEnabled true
      connList.setHighlighters HighlighterFactory.createSimpleStriping()

      def actionNew = swing.action(name: 'New', mnemonic: 'n') {
         def cd = connectionEditWindow.show(null)
         if (cd) {
            connectionsWorkingCopy.add cd
            refreshList()
         }
      }
      def actionEdit = swing.action(name: 'Edit', mnemonic: 'e') {
         def cd = connectionEditWindow.show(connList.selectedValue)
         if (cd) {
            def index = connectionsWorkingCopy.indexOf(connList.selectedValue)
            connectionsWorkingCopy.putAt(index, cd)
            refreshList()
         }
      }
      def actionDel = swing.action(name: 'Delete', mnemonic: 'd') {
         connList.selectedValue ? connectionsWorkingCopy.remove(connList.selectedValue) : GUIUtils.showMessage('Please select the connection to be deleted')
         refreshList()
      }
      def actionSave = swing.action(name: 'Save', mnemonic: 's') {
         connectionManager.replaceConnections connectionsWorkingCopy
         connectionManager.saveConnections()
         swing.connectionFrame.visible = false
         mainWindow.refreshConnections()
      }
      def actionCancel = swing.action(name: 'Cancel', mnemonic: 'c') {
         swing.connectionFrame.visible = false
      }

      connList.addMouseListener([mouseClicked: {event ->
         if (event.getClickCount() == 2) {
            actionEdit.closure.call()
         }
      }] as MouseAdapter)
      

      def frame = swing.frame(title: 'Connections', id: 'connectionFrame', resizable: true, alwaysOnTop: true) {
         vbox {
            scrollPane(horizontalScrollBarPolicy: JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED,
                       verticalScrollBarPolicy: JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED) {
               widget(connList)
            }
            panel (layout: new FlowLayout(FlowLayout.LEFT, 10, 5), border: BorderFactory.createLineBorder(Color.gray)) {
               button(actionNew)
               button(actionEdit)
               button(actionDel)
            }
            panel (layout: new FlowLayout(FlowLayout.RIGHT, 10, 20)) {
               button(actionSave)
               button(actionCancel)
            }
         }
      }
      frame.pack()
   }

   void show(SqlEditorWindow mainWindow) {
      this.mainWindow = mainWindow
      connectionsWorkingCopy.clear()
      connectionsWorkingCopy.addAll connectionManager.connections
      refreshList()
      swing.connectionFrame.pack()
      GUIUtils.centerOnScreen swing.connectionFrame
      swing.connectionFrame.setVisible true
   }

   private void refreshList() {
      connList.listData = connectionsWorkingCopy.toArray()
      if (connList.visible) {
         connList.repaint()
      }
   }
}