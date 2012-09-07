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

class StatementManager {

   Sql db

   List<SqlStatement> statements = [
            [/[Bb][Ee][Gg][Ii][Nn]\s+.*/, 'Transaction started', [db.&execute]] as SqlStatement,
            [/[Cc][Oo][Mm][Mm][Ii][Tt]\s+.*/, 'Transaction commited', [db.&execute]] as SqlStatement,
            [/[Rr][Oo][Ll][Ll][Bb][Aa][Cc][Kk]\s+.*/, 'Transaction rolled back', [db.&execute]] as SqlStatement,
            [/[Ss][Tt][Aa][Rr][Tt]\s+[Vv][Ii][Oo][Ll][Aa][Tt][Ii][Oo][Nn][Ss]\s+[Tt][Aa][Bb][Ll][Ee].*/, 'Violations table started', [db.&execute]] as SqlStatement,
            [/[Ss][Tt][Oo][Pp]\s+[Vv][Ii][Oo][Ll][Aa][Tt][Ii][Oo][Nn][Ss]\s+[Tt][Aa][Bb][Ll][Ee].*/, 'Violations table stopped', [db.&execute]] as SqlStatement,
            [/[Tt][Rr][Uu][Nn][Cc][Aa][Tt][Ee]\s+.*/, 'Table truncated', [db.&execute]] as SqlStatement,
            [/[Gg][Rr][Aa][Nn][Tt]\s+.*/, 'Permission granted', [db.&execute]] as SqlStatement,
            [/[Rr][Ee][Vv][Oo][Kk][Ee]\s+.*/, 'Permission revoked', [db.&execute]] as SqlStatement,
            [/[Uu][Pp][Dd][Aa][Tt][Ee]\s+[Ss][Tt][Aa][Tt][Ii][Ss][Tt][Ii][Cc][Ss]\s+.*/, 'Statistics updated', [db.&execute]] as SqlStatement,
            [/([Aa][Ll][Tt][Ee][Rr]\s+)(\w+)(.*)/, ' altered', [db.&execute]] as SqlStatement,
            [/([Cc][Rr][Ee][Aa][Tt][Ee]\s+)(\w+)(.*)/, ' created', [db.&execute]] as SqlStatement,
            [/([Dd][Rr][Oo][Pp]\s+)(\w+)(.*)/, ' dropped', [db.&execute]] as SqlStatement,
            [/([Rr][Ee][Nn][Aa][Mm][Ee]\s+)(\w+)(.*)/, ' renamed', [db.&execute]] as SqlStatement,
            [/([Ss][Ee][Tt]\s+)(\w+)(.*)/, ' set', [db.&execute]] as SqlStatement,
            [/[Ii][Nn][Ss][Ee][Rr][Tt]\s+.*/, ' row(s) inserted', [db.&executeUpdate]] as SqlStatement,
            [/[Uu][Pp][Dd][Aa][Tt][Ee]\s+.*/, ' row(s) updated', [db.&executeUpdate]] as SqlStatement, // todo add ^statistics
            [/[Dd][Ee][Ll][Ee][Tt][Ee]\s+.*/, ' row(s) deleted', [db.&executeUpdate]] as SqlStatement,
            [/[Ll][Oo][Aa][Dd]\s+.*/, ' row(s) inserted', [this.&executeLoad]] as SqlStatement,
            [/[Uu][Nn][Ll][Oo][Aa][Dd]\s+.*/, ' row(s) unloaded', [this.&executeUnload]] as SqlStatement,
            [/([Ee][Xx][Ee][Cc][Uu][Tt][Ee]\s+)(\w+)(.*)/, ' executed', [db.&query, db.&execute]] as SqlStatement,
            [/[Ss][Ee][Ll][Ee][Cc][Tt]\s+.*/, '', [db.&query, db.&execute]] as SqlStatement
         ]

   def unloadRegex = /([Uu][Nn][Ll][Oo][Aa][Dd]\s+[Tt][Oo]\s+['"])(\S+)(["']\s+[Dd][Ee][Ll][Ii][Mm][Ii][Tt][Ee][Rr]\s+['"])(\S)(['"])(.*)/
   def loadRegex = /([Ll][Oo][Aa][Dd]\s+[Ff][Rr][Oo][Mm]\s+['"])(\S+)(["']\s+[Dd][Ee][Ll][Ii][Mm][Ii][Tt][Ee][Rr]\s+['"])(\S)(['"])(.*)/

   def StatementManager(final db) {
      this.db = db;
   }

   SqlStatement getStatement(String origSql, String clearSql, Closure queryClosure) {
      clearSql = clearSql.replace('\n', ' ').replace('\r', '').trim()
      for (statement in statements) {
         if (clearSql ==~ statement.statementRegex) {

            def retStmt = new SqlStatement(statement)
            if (statement.statementRegex.contains('\\w')) {
               updateMsg clearSql, retStmt
            }

            def ex = checkExistingWherePart(clearSql)
            if (!ex.isEmpty()) {
               retStmt.executeMethod.set(0, this.&throwException)
            }

            for (int i = 0; i < retStmt.executeMethod.size(); i++) {
               switch (retStmt.executeMethod.get(i).getMethod()) {
                  case 'query':
                     retStmt.executeMethod.set(i, statement.executeMethod.get(i).curry(origSql, queryClosure))
                     break
                  case 'executeUnload':
                  case 'executeLoad':
                     retStmt.executeMethod.set(i, statement.executeMethod.get(i).curry(origSql, clearSql))
                     break
                  case 'throwException':
                     retStmt.executeMethod.set(i, retStmt.executeMethod.get(i).curry(origSql, ex))
                     break
                  default:
                     retStmt.executeMethod.set(i, statement.executeMethod.get(i).curry(origSql))
               }
            }
            return retStmt
         }
      }
      null
   }

   private def updateMsg(String sql, SqlStatement statement) {
      (sql =~ statement.statementRegex).each {match ->
         statement.succExecMsg = match[2] + statement.succExecMsg
      }     
   }

   private String checkExistingWherePart(String clearSql) {
      // update and delete command must have where part
      def lClearSql = clearSql.toLowerCase()
      if (lClearSql ==~ /update\s+.*/ && !lClearSql.contains('statistics') && !(lClearSql ==~ /update\s+.*(\s+where\s+).*/)) {
         return 'Update statement MUST have WHERE part'
      }
      if (lClearSql ==~ /delete\s+.*/ && !(lClearSql ==~ /delete\s+.*(\s+where\s+).*/)) {
         return 'Delete statement MUST have WHERE part'
      }
      ''
   }

   private def throwException(String origSql, String message) {
      throw new SQLException(message)
   }

   private def executeUnload (String origSql, String clearSql) throws SQLException {
      // first decide where the unload is taking place, which delimiter is used, then execute it
      // syntax: unload to 'file_name' delimiter '#' select...
      // it is expected that unload is formatted ok, so original statement is used
      String fileName = null
      def delimiter = null
      def query = null
      (origSql =~ unloadRegex).each { match ->
         fileName = match[2]
         delimiter = match[4]
         query = match[6]
      }
      if (fileName == null || delimiter == null || query == null) {
         throw new SQLException("""Malformed unload statement, should be "unload to 'path' delimiter 'char' select ..."  """)
      }
      def count = 0
      
      File newFile = new File(fileName)
      newFile.delete()
      newFile.createNewFile()
      db.query (query) { rs ->
         def columnCount = rs.metaData.columnCount
         def line, obj
         while (rs.next()) {
            line = ''
            for (i in 1..columnCount) {
               obj = rs.getObject(i)
               if (obj?.class?.methods?.name?.grep('trim')?.size() > 0)
                  line += obj.trim() + delimiter
               else {
                  //line += obj + delimiter
                  line << obj
                  line << delimiter
               }

            }
            newFile << line + System.getProperty('line.separator')
            count++
         }
      }
      count
   }

   private def executeLoad (String origSql, String clearSql) throws SQLException {
      // decide which file to load, iterate through it and load line by line
      String fileName = null
      def delimiter = null
      def query = null
      (origSql =~ loadRegex).each { match ->
         fileName = match[2]
         delimiter = match[4]
         query = match[6]
      }
      if (fileName == null || delimiter == null || query == null) {
         throw new SQLException("""Malformed load statement, should be "load from 'path' delimiter 'char' insert into ..."  """)
      }
      def count = 0

      File newFile = new File(fileName)
      if (!newFile.exists()) {
         throw new SQLException("Load file $fileName not found")
      }
      String insertStatement = null
      newFile.eachLine { line ->
         // using prepared statement
         if (insertStatement == null) {
            insertStatement = prepareInsertStatement(query, line, delimiter)
         }
         try {
            db.execute insertStatement, line.tokenize(delimiter)
         }
         catch (SQLException e) {
            throw new SQLException("Exception while loading line ${count+1} (previous lines are loaded): ${e.getMessage()}", e)
         }
         count++
      }
      count
   }

   String prepareInsertStatement (String query, String line, String delimiter) {
      def count = line.tokenize(delimiter).size()
      String ret = query.replace(';', ' ') + ' values ('
      for (i in 1..count) {
         if (i == count)
            ret += '?)'
         else
            ret += '?,'
      }
      ret
   }
}
