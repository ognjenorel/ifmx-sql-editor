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
import java.awt.FlowLayout

import javax.swing.JDialog

import javax.swing.KeyStroke

class LoginWindow {

   boolean userCanceled = false
   SwingBuilder swing
   JDialog dialog

   def LoginWindow() {

      swing = new SwingBuilder()

      def actionOK = swing.action(name: 'OK', accelerator: KeyStroke.getKeyStroke("ENTER")) {
         if ( swing.userField.getText().isEmpty() || String.valueOf(swing.pwdField.getPassword()).isEmpty() ) {
            GUIUtils.showError 'Both username and password must be entered'
            return
         }
         userCanceled = false
         swing.loginFrame.setVisible(false)
      }
      def actionCancel = swing.action(name: 'Cancel', accelerator: KeyStroke.getKeyStroke("ESCAPE")) {
         userCanceled = true
         swing.loginFrame.setVisible(false)
      }

      dialog = swing.dialog(title: 'Login', id: 'loginFrame', resizable: false, alwaysOnTop: true, modal: true) {
         vbox(border: emptyBorder(top: 10, left: 15, bottom: 10, right: 15)) { // can be done like this, but not clear enough imho: [10, 15, 10, 15]
            panel(layout: new FlowLayout(FlowLayout.RIGHT, 10, 5)) {
               label 'User'
               textField(id: 'userField', columns: 10)
            }
            panel(layout: new FlowLayout(FlowLayout.RIGHT, 10, 10)) {
               label 'Password'
               passwordField(id: 'pwdField', columns: 10)
            }
            hbox {
               button(action: actionOK)
               rigidArea()
               button(action: actionCancel)
            }
         }
      }
      dialog.pack()
      GUIUtils.centerOnScreen dialog
   }

   /**
    * Show the window and returns true if username and pwd has been entered, returns false if canceled
    */
   boolean show() {
      show(null)
   }

   boolean show(String username) {
      swing.userField.setText(username)
      dialog.setVisible(true)
      !userCanceled
   }

   String getUsername() {
      swing.userField.getText()
   }

   String getPwd() {
      String.valueOf(swing.pwdField.getPassword())
   }

}
