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

import groovy.sql.Sql
import java.sql.SQLException

import ifmxsqleditor.ui.ResultSetTableModel
import ifmxsqleditor.common.Options

class DbManager {

   Sql db
   SqlParser sqlParser = new SqlParser()
   List<SqlResult> returnList
   StatementManager statementManager

   def queryResult

   Closure queryClosure =
      { rs ->
         queryResult = new ResultSetTableModel(rs)
      }

   Sql connect(ConnectionData connectionData) throws SQLException {
      db = Sql.newInstance(connectionData.jdbcUrl, connectionData.user, connectionData.pwd, connectionData.driverClass)
      db.connection.setAutoCommit (true)
      statementManager = new StatementManager(db)
      return db
   }

   boolean disconnect() {
      db?.close()
   }

   boolean connected() {
      db != null
   }
   
   def executeSql(String sqlToExecute) {
      if ( db == null ) {
         return
      }
      returnList = new ArrayList<SqlResult>()
      queryResult = null
      boolean exceptionRaised = false

      sqlParser.eachStatement(sqlToExecute) { origStatement, clearedStatement ->

         // if we stop on error and there was one, skip further iterations
         if (Options.stopOnError && exceptionRaised)
            return

         def statement = statementManager.getStatement(origStatement.toString(), clearedStatement.toString(), queryClosure)

         try {
            def ret

            def time0 = Calendar.getInstance().getTimeInMillis()
            try {
               ret = statement.executeMethod.get(0).call()
            }
            catch (SQLException e) {
               // if this is a procedure or function call, try execute instead of query
               if (e.errorCode == -79750) {
                  ret = statement.executeMethod.get(1).call()
               }
               else
                  throw e
            }
            def duration = Calendar.getInstance().getTimeInMillis() - time0

           
            if (ret == null && queryResult != null)
               returnList.add(new SqlResult(origStatement.toString(), queryResult, duration))
            else {
               switch (ret.getClass()) {
                  case Integer:
                     statement.succExecMsg = ret + statement.succExecMsg
                  case Boolean:
                     returnList.add(new SqlResult(origStatement.toString(), statement.succExecMsg, duration))
                     break
                  case null:
                     returnList.add(new SqlResult(origStatement.toString(), queryResult, duration))
                     break
                  default:
                     returnList.add(new SqlResult(origStatement.toString(), ret, duration))
                     break
               }
            }
         }
         catch (SQLException e) {
            returnList.add(new SqlResult(origStatement.toString(), e, 0))
            exceptionRaised = true
         }
      }
      return returnList

   }
//      try {
//         db.query (sqlToExecute) { rs ->
//            returnList.add(new SqlResult(sqlToExecute, new ResultSetTableModel(rs)))
//         }
//      }
//      catch (SQLException e) {
//         returnList.add(new SqlResult(sqlToExecute, e))
//      }

}
