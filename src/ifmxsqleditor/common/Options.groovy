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

import groovy.xml.MarkupBuilder

/**
 * Holds initial defaults and options for the program
 */
class Options {

   static String fileName = Utils.getInstance().getApplicationPath() + 'options.xml'

   // implemented options:
   public static boolean stopOnError = false



   public static void readOptions() {
      def options
      try {
         options = new XmlParser().parse(new File(fileName))
      }
      catch (FileNotFoundException e) {
         return
      }
      stopOnError = Boolean.valueOf(options.stopOnError[0].value()[0])
   }

   public static void saveOptions() {
      def writer = new FileWriter(fileName)
      def builder = new MarkupBuilder(writer)
      def options = builder.options {
         stopOnError(stopOnError)
      }
   }

}
