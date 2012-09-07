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

public class SqlStatement {


   String statementRegex // regex describing the sql command

   String succExecMsg // message shown if the command was successfully executed

   List<Closure> executeMethod // sql execution method (query, execute, executeUpdate)


   def SqlStatement(statementRegex, succExecMsg, executeMethod) {
      this.statementRegex = statementRegex
      this.succExecMsg = succExecMsg
      this.executeMethod = executeMethod
   }

   def SqlStatement(SqlStatement other) {
      this.statementRegex = other.statementRegex
      this.succExecMsg = other.succExecMsg
      this.executeMethod = (List<Closure>) other.executeMethod.clone()
   }

}