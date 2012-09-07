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

public class SqlParser {

   def regexpMap = [
      'procedure': /[Cc][Rr][Ee][Aa][Tt][Ee]\s+[Pp][Rr][Oo][Cc][Ee][Dd][Uu][Rr][Ee].*/,
      'endProcedure': /.*[Ee][Nn][Dd]\s+[Pp][Rr][Oo][Cc][Ee][Dd][Uu][Rr][Ee].*/,
      'function': /[Cc][Rr][Ee][Aa][Tt][Ee]\s+[Ff][Uu][Nn][Cc][Tt][Ii][Oo][Nn].*/,
      'endfunction': /.*[Ee][Nn][Dd]\s+[Ff][Uu][Nn][Cc][Tt][Ii][Oo][Nn].*/,
      'endExternal': /(.*[Ll][Aa][Nn][Gg][Uu][Aa][Gg][Ee] )([Cc]|[Jj][Aa][Vv][Aa])(.*)/
   ]

   /**
    * parses the sql argument and calls the given closure for each of the sql commands in it
    * in closure goes:
    *  - the original command
    *  - cleaned command for easier parsing of the statement itself
    */
   void eachStatement(String sql, Closure closure) {
      StringBuffer origStatement = new StringBuffer()
      StringBuffer clearedStatement = new StringBuffer()

      boolean inProc = false
      boolean inComment = false

      // first eliminate single-line comments

      String cleanLine // line with no comment

      String newSql = ''
      sql.eachLine {String line ->
         cleanLine = line
         // is there a single-line comment
         if ( cleanLine.contains('--') ) {
            cleanLine = line.substring(0, line.indexOf('--'))
         }
         if (!cleanLine.trim().isEmpty())
            newSql += cleanLine + System.getProperty("line.separator")
      }
      // if there is no semi-colon at the end, add one
      newSql = newSql.trim()
      if ( !newSql.endsWith(';') )
         newSql += ';'

      newSql.eachLine {String line ->

         origStatement << line + System.getProperty("line.separator")
         cleanLine = line

         // if in procedure, add the whole line
         if ( inProc ) {
            clearedStatement << line + System.getProperty("line.separator")
         }
         else {
            // if in multi-line comment and no end in this line, just skip it
            if ( inComment && !cleanLine.contains('}') )
               return 
            // if in multi-line comment and there is end of it in this line, add the rest
            if ( inComment && cleanLine.contains('}') ) {
               cleanLine = cleanLine.substring(cleanLine.indexOf('}') + 1)
               inComment = false
            }
            // if there is the whole {..} comment in this line (or more), clean it
            if ( cleanLine.contains('{') && cleanLine.contains('}') )
               cleanLine = cleanOneLineBracketComment(cleanLine)
            // if not in multi-line comment, and there is start of it, add the stuff before
            if ( !inComment && cleanLine.contains('{') ) {
               cleanLine = cleanLine.substring(0, cleanLine.indexOf('{'))
               inComment = true
            }

            cleanLine = cleanQuoted(cleanLine)

            // add to buffer what is left
            if ( !cleanLine.trim().empty )
               clearedStatement << cleanLine + System.getProperty("line.separator")
         }

         // procedure/function begin
         if ( cleanLine ==~ regexpMap.procedure || cleanLine ==~ regexpMap.function )
            inProc = true

         // procedure/function end
         if ( cleanLine ==~ regexpMap.endProcedure || cleanLine ==~ regexpMap.endFunction || cleanLine ==~ regexpMap.endExternal )
            inProc = false

         // when the last line of the statement is hit, call a given closure on it and reset the buffer
         if ( !inProc && cleanLine.contains(';') ) {
            closure(origStatement, clearedStatement)
            origStatement.delete 0, origStatement.length()
            clearedStatement.delete 0, clearedStatement.length()
         }
      }
   }

   private String cleanOneLineBracketComment(String line) {
      def nl = line
      // remove all inside the comment, and call again to clear if there are more comments inside this one
      if ( nl.contains('{') && nl.contains('}') )
         return cleanOneLineBracketComment(nl.substring(0, nl.indexOf('{')) + nl.substring(nl.indexOf('}') + 1, nl.length()))
      else
         return nl
   }

   private String cleanQuoted(String line) {
      def nl = line

      for (quote in ['\'', '"']) {
         if (nl.indexOf(quote) > -1) {
            return cleanQuoted(nl.substring(0, nl.indexOf(quote)) + nl.substring(nl.indexOf(quote, nl.indexOf(quote) + 1) + 1, nl.length()))
         }
      }
      nl
   }
   
}

