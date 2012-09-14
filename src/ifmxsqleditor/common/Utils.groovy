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


package ifmxsqleditor.common

/**
 * Contains useful shared methods
 */
class Utils {

   static Utils utils;
   
   private Utils() { }

   public static Utils getInstance() {
      utils ? utils : (utils = new Utils())
   }

   public String getApplicationPath() {
      def separator = System.getProperty('file.separator')
      def path = getClass().protectionDomain.codeSource.location.path

      if (path.endsWith('.jar'))
         path = new File(path).getParent()

      if (!path.endsWith(separator))
         path += separator

      path
   }

}
