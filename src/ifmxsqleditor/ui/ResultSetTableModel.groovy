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

import com.informix.jdbc.IfxBblob
import com.informix.jdbc.IfxBlob

import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.table.AbstractTableModel
import java.sql.ResultSet

class ResultSetTableModel extends AbstractTableModel {

   List<String> colNames = new ArrayList<String>()
   List<List<Object>> values = new ArrayList<List<Object>>()

   // key = row-column hash, value = data
   Map<Integer, Object> blobValues = new HashMap<Integer, Object>()

   // key = row index, value = max height of cell in that row, IF it is other than default
   Map<Integer, Integer> maxRowHeights = new HashMap<Integer, Integer>()

   def MAX_ICON_HEIGHT_SHOWING = 500
   def BLOB_VALUES_ROW_MAGIC = 1000000
   def BLOB_DATA_MSG = '<<double-click to see data>>'

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
            else {
               if (obj instanceof IfxBlob) {
                  obj = processBlob(rowCount, i, ((IfxBlob) obj).toBytes())
               }
               if (obj instanceof IfxBblob) {
                  obj = processBlob(rowCount, i, ((IfxBblob)obj).getBytes(1, (int) ((IfxBblob)obj).length()))
               }
               if (obj instanceof byte[]) {
                  obj = processBlob(rowCount, i, obj)
               }
               list.add(obj)
            }
         }
         values.add(list)
         rowCount++
      }
   }

   Object processBlob(int row, int column, Object obj) {
      // 1. add the blob in the values map
      // 2. if the blob is image, figure out the size
      // 3. if the size is smaller, show it in the grid, otherwise show just the message
      blobValues.put(row * BLOB_VALUES_ROW_MAGIC + column, obj)

      if (isImage(obj)) {
         obj = new ImageIcon(obj)
         def iconHeight = ((ImageIcon) obj).iconHeight
         if (iconHeight > MAX_ICON_HEIGHT_SHOWING)
            obj = BLOB_DATA_MSG
         else {
            def currentMax = maxRowHeights.get(row)
            if (currentMax == null || currentMax < iconHeight)
               maxRowHeights.put(row, iconHeight)
         }
      }
      else
         obj = BLOB_DATA_MSG

      obj
   }

   def public static boolean isImage(byte[] data) {
      ImageIO.read(new ByteArrayInputStream(data)) != null
   }

   def public boolean customRowHeightExists() {
      !maxRowHeights.isEmpty()
   }

   def public Integer getMaxRowHeight(int row) {
      maxRowHeights.get(row)
   }

   def boolean isCellEditable(int rowIndex, int columnIndex) {
      false
   }

   def String getColumnName(int column) {
      colNames[column]
   }

   Class<?> getColumnClass(int columnIndex) {
      getValueAt(0, columnIndex).getClass()
   }

   Object getValueAt(int rowIndex, int columnIndex) {
      values.get(rowIndex).get(columnIndex)
   }

   Object getRawValueAt(int rowIndex, int columnIndex) {
      def retVal = blobValues.get(rowIndex * BLOB_VALUES_ROW_MAGIC + columnIndex + 1)
      if (retVal == null) retVal = getValueAt(rowIndex, columnIndex)
      retVal
   }
}
