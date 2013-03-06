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

import groovy.xml.MarkupBuilder
import ifmxsqleditor.common.Utils

/**
 * Contains active ConnectionData objects list and methods for load and save connection data in xml file
 */
public class ConnectionManager {

   static String FILE = 'connections.xml'
   String fileName = Utils.getInstance().getApplicationPath() + FILE

   List<ConnectionData> connections = new ArrayList<ConnectionData>()

   def ConnectionManager() {
      readConnections()
   }

   private void readConnections() {
      def dbConnections
      try {
         dbConnections = new XmlParser().parse(new File(fileName))
      }
      catch (FileNotFoundException e) {
         println 'Error fetching connections: ' + e.getMessage()
         return
      }
      dbConnections.each { dbConnection ->
         connections.add(new ConnectionData(alias: dbConnection.alias[0].value()[0],
                                            jdbcUrl: dbConnection.jdbcUrl[0].value()[0],
                                            driverClass: dbConnection.driverClass[0].value()[0],
                                            user: dbConnection.user[0].value()[0]))
      }
   }

   private void saveConnections() {
      def writer = new FileWriter(fileName)
      def builder = new MarkupBuilder(writer)
      def dbConnections = builder.dbConnections {
         connections.each { connectionData ->
            dbConnection {
               alias(connectionData.alias)
               jdbcUrl(connectionData.jdbcUrl)
               driverClass(connectionData.driverClass)
               user(connectionData.user)
            }
         }
      }
   }

   def replaceConnections(List<ConnectionData> newCD) {
      connections.clear()
      newCD.each {
         connections.add(new ConnectionData(it))
      }
   }

   def getDataByAlias(String alias) {
      connections.find {it.alias.equals(alias)}
   }

}