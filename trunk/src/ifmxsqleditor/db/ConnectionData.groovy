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
 * Database connection data
 */

public class ConnectionData {

   String alias
   String jdbcUrl
   String driverClass
   String user

   // password is not stored in xml file, it is kept in here after user is asked for credentials
   String pwd



   def ConnectionData() {
      // in groovy, if there is another constructor, then there also has to be
      // this one without arguments, so the properties could be set
   }

   def ConnectionData(ConnectionData other) {
      this.alias = other.alias
      this.jdbcUrl = other.jdbcUrl
      this.driverClass = other.driverClass
      this.user = other.user
      this.pwd = other.pwd
   }
}