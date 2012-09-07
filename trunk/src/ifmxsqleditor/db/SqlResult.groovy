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


package ifmxsqleditor.db

/**
 * Contains result of a sql command execution.
 *
 * Result can be:
 *  boolean - command is successful or not (create, alter, set...)
 *  int - number of changed/inserted/deleted rows (insert, update, delete)
 *  TableModel - query results prepared for JTable as a TableModel (select, execute...)
 *  String - a message
 *  Exception
 *
 */
class SqlResult {

   String sql
   Object result
   Integer time // database execution time


   def SqlResult(final sql, final result, final time) {
      this.sql = sql;
      this.result = result;
      this.time = time;
   }
}
