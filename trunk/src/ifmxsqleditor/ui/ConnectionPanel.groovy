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

import java.awt.Color
import javax.swing.JLabel
import javax.swing.JPanel
import java.awt.BorderLayout as BL
import javax.swing.BorderFactory as BF

class ConnectionPanel extends JPanel {
   def connectionData

   def ConnectionPanel(final connectionData) {
      this.connectionData = connectionData;
      layout = new BL()
      add(new JLabel(connectionData.alias), BL.NORTH)
      add(new JLabel(connectionData.jdbcUrl), BL.CENTER)
      add(new JLabel(connectionData.user), BL.SOUTH)
      setBorder BF.createCompoundBorder(BF.createLineBorder(Color.gray), BF.createEmptyBorder(5,5,5,5))
   }
}