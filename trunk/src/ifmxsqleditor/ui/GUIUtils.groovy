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

import java.awt.Component
import java.awt.Toolkit
import javax.swing.JOptionPane

class GUIUtils {

   static centerOnScreen(Component component) {
      def screen = Toolkit.getDefaultToolkit().getScreenSize()
      def comp = component.getSize()
      component.setLocation((int) ((screen.width - comp.width) / 2), (int) ((screen.height - comp.height) / 2))
   }

   static showMessage(String message) {
      JOptionPane.showMessageDialog(null, message, 'SQL editor', JOptionPane.INFORMATION_MESSAGE)
   }

   static showError(String message) {
      JOptionPane.showMessageDialog(null, message, 'SQL editor', JOptionPane.ERROR_MESSAGE)  
   }
}
