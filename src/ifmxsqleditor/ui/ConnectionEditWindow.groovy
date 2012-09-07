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

import java.awt.FlowLayout as FL

import groovy.swing.SwingBuilder
import ifmxsqleditor.db.ConnectionData

/**
 * Window used to add/show/delete a single connection
 */
public class ConnectionEditWindow {

   ConnectionData connectionData // connection shown in this window, null if it is a new one

   def swing
   def frame

   boolean canceled

   def ConnectionEditWindow() {
      swing = new SwingBuilder()

      def actionSave = swing.action(name: 'Save', mnemonic: 's') {
         canceled = false
         frameToValues()
         frame.setVisible false
      }
      def actionCancel = swing.action(name: 'Cancel', mnemonic: 'c') {
         canceled = true
         frame.setVisible false
      }

      frame = swing.dialog(title: 'Connection data', alwaysOnTop: true, modal: true) {
         tableLayout (cellpadding: 5) {
            tr {
               td (align: 'right') {label 'Alias'}
               td (align: 'left') {textField (id: 'alias', columns: 20)}
            }
            tr {
               td (align: 'right') {label 'JDBC URL' }
               td (align: 'left') {textField (id: 'jdbcUrl', columns: 40)}
            }
            tr {
               td (align: 'right') { label 'Driver class' }
               td (align: 'left') { textField (id: 'driverClass', columns: 20) }
            }
            tr {
               td (align: 'right') {label 'Username'}
               td (align: 'left') {textField (id: 'user', columns: 10)}
            }
            tr {
               td (align: 'right', colspan: 2) {
                  panel (layout: new FL(FL.RIGHT, 10, 10)) {
                     button(actionSave)
                     button(actionCancel)
                  }
               }
            }
         }
      }
      frame.pack()
   }

   // return changed (if edited) or new (if added new) ConnectionData object, or null (if canceled)
   ConnectionData show(ConnectionData cd) {
      connectionData = cd
      if (!connectionData) {
         connectionData = new ConnectionData()
      }
      valuesToFrame()
      GUIUtils.centerOnScreen frame
      
      frame.setVisible (true)

      canceled ? null : connectionData
   }

   private void valuesToFrame() {
      swing.alias.text = connectionData.alias
      swing.jdbcUrl.text = connectionData.jdbcUrl
      swing.driverClass.text = connectionData.driverClass
      swing.user.text = connectionData.user
   }

   private void frameToValues() {
      connectionData.alias = swing.alias.text
      connectionData.jdbcUrl = swing.jdbcUrl.text
      connectionData.driverClass = swing.driverClass.text
      connectionData.user = swing.user.text
   }
}