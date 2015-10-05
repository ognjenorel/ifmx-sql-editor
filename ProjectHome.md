## IFMX SQL Editor ##
This is a GUI tool for executing SQL commands on Informix databases, replacement for original Informix's SQL Editor, with the following features:
  * fully keyboard centric
  * allows you to execute only a portion of SQL commands in editor
  * allows execution of multiple SQL commands at once, displaying query results in separate tabs
  * keep the results of an SQL command

Improvements (compared to original Informix SQL Editor):
  * multilevel undo/redo
  * SQL syntax highlighting (thanks to jsyntaxpane project)
  * modification of the results grid - ordering, show/hide columns (thanks to SwingX project)
  * opt whether to stop or continue on error when executing multiple SQL commands
  * set the window title (useful when working with multiple databases simultaneously)

For information about getting stared in using the SQL Editor, see the GettingStarted wiki page.

## Screenshots ##
![http://ifmx-sql-editor.googlecode.com/svn/wiki/Screenshot1.png](http://ifmx-sql-editor.googlecode.com/svn/wiki/Screenshot1.png)
![http://ifmx-sql-editor.googlecode.com/svn/wiki/Screenshot2.png](http://ifmx-sql-editor.googlecode.com/svn/wiki/Screenshot2.png)


## Groovy ##
Written in [Groovy](http://groovy.codehaus.org), this project is an excellent showcase of simplicity and efficiency of the Groovy programming language. The project consist of 15 classes only, with about 1500 lines of code.
If you are a Groovy student, check the code to see:
  * many usages of closures
  * ease of GUI building with SwingBuilder
  * reading and writing an XML document (XMLParser and MarkupBuilder)
  * power of using regular expressions with Groovy
  * database interactions
  * all other language features


## Used components ##
The editor uses the open source [jsyntaxpane component](http://code.google.com/p/jsyntaxpane/), released under the [Apache license 2.0](http://www.apache.org/licenses/LICENSE-2.0).

Query results are displayed in JXTable component, part of [SwingLabs SwingX project](http://java.net/projects/swingx), released under the [LGPL-2.1 license](http://www.opensource.org/licenses/lgpl-2.1.php).

Icons used are part of the [Tango Desktop Project](http://tango.freedesktop.org/), released under the [GPL license](http://www.gnu.org/copyleft/gpl.html).

The IBM Informix JDBC driver is freely available at [www.ibm.com](http://www.ibm.com), released under the IBM license (copy included in the binaries). You agree to the terms of this license by downloading and executing the binaries.