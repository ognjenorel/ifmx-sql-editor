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

import java.sql.ResultSet
import javax.swing.table.AbstractTableModel

class ResultSetTableModel extends AbstractTableModel {

   List<String> colNames = new ArrayList<String>()
   List<List<Object>> values = new ArrayList<List<Object>>()
   int rowCount = 0
   int columnCount = 0

   def ResultSetTableModel(ResultSet resultSet) {
      def meta = resultSet.metaData
      columnCount = meta.columnCount
      for(i in 0..<meta.columnCount) {
         colNames.add(meta.getColumnLabel(i+1))
      }
      def obj
      while (resultSet.next()) {
         def list = new ArrayList<Object>()
         for (i in 1..columnCount) {
            obj = resultSet.getObject(i)
            if (obj?.class?.methods?.name?.grep('trim')?.size() > 0)
               list.add(obj.trim())
            else
               list.add(obj)
         }
         values.add(list)
         rowCount++
      }
   }

   def boolean isCellEditable(int rowIndex, int columnIndex) {
      false
   }

   def String getColumnName(int column) {
      colNames[column]
   }

   Object getValueAt(int rowIndex, int columnIndex) {
      values.get(rowIndex).get(columnIndex)
   }
}
