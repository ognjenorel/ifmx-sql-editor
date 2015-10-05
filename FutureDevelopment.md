Upcoming development and current needs are covered by the [issues](http://code.google.com/p/ifmx-sql-editor/issues/list) with the project.

### SQL Parser development ###

The project was ment (along with some other things) to demostrate the ease of development with Groovy. Because of that, the SQL Parser component is not fully featured. It should be developed as an automata machine.

### SQL Dialects ###

As most of the editor's code is SQL Dialect inaware, and it fairly easy to include another JDBC driver in the project, several changes could be done in order to support other databases. The parser and statement manager classes should become interfaces, with concrete implementations for all the supported databases, alongside some minor changes in other classes.

