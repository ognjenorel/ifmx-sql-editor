First download the featured executables download from the project's main page.

This program is released under GNU GPL. Please find the copy of the license attached in file COPYING.txt.

IMPORTANT: By downloading and using the program you agree with all terms of IBM Informix JDBC Driver License, attached in the file licenses/informix\_jdbc.txt.


## Starting up ##

You'll need Java installed on your computer in order to start this program. Most likely you already have it, but if not, get it at www.java.com.

Simply extract the zip content to a directory of your preference. Run the main Java archive (named ifmxsqleditor.jar) by double clicking on it, or execute command:

`java -jar ifmxsqleditor.jar`

Depending on the operating system you use, you may want to create a launcher or batch script to start the program.


## Define the connections ##

Before you can execute any SQL, first define the connection(s) the menu Options->Edit connections. Each connection represents the connection to one database, as the connections are made via JDBC protocol. There is already a demo
connection data in the window that can be used as a template. Define a unique alias for your connection, and modify JDBC URL template with your own server (IP or server name), port (if different from default one), database name and INFORMIXSERVER name. Add any additional connection parameters at the end of the JDBC URL separated with semi-colon.

E.g. connection to a database with Croatian locale should be like this:

```
jdbc:informix-sqli://my_host:9088/my_db:INFORMIXSERVER=my_ifmxserv;DB_LOCALE=hr_HR.8859-2;CLIENT_LOCALE=hr_HR.CP1250
```

The name of the JDBC driver should be the same if Informix JDBC driver is used (com.informix.jdbc.IfxDriver). If experimenting with other drivers, change this.
Username can be changed at login time.


## Connect and execute commands ##

Use connection list (combo) in the upper right corner of the main window to select the connection and display the login dialog. Upon successful authentication, just start typing SQL commands. Execute all commands in the editor or select some and execute only those. Watch for the response in the output pane on the bottom of the window and new tabs appearing containing fetched data.

Some useful key shortcuts:

  * Ctrl+Enter - execute selected
  * Ctrl+Shift+Enter - execute all commands in editor
  * Ctrl+PgDn, Ctrl+PgUp - rotate through displayed output tabs and the editor
  * Ctrl+K - keep the active output tab
  * Ctrl+R - disregard the active output tab

Other commands have the usual shortcuts: Ctrl+N (new), Ctrl+O (open), etc.