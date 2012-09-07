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


package ifmxsqleditor.ui

import java.awt.BorderLayout as BL

import groovy.swing.SwingBuilder
import groovy.ui.text.TextUndoManager
import java.awt.Dimension
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.sql.SQLException
import javax.swing.filechooser.FileFilter
import javax.swing.table.TableModel
import javax.swing.text.DefaultStyledDocument
import jsyntaxpane.DefaultSyntaxKit
import org.jdesktop.swingx.JXTable
import org.jdesktop.swingx.decorator.HighlighterFactory
import ifmxsqleditor.db.ConnectionData
import ifmxsqleditor.db.ConnectionManager
import ifmxsqleditor.db.DbManager
import ifmxsqleditor.db.SqlResult
import javax.swing.*
import java.awt.Cursor
import javax.swing.text.StyleContext
import javax.swing.text.AttributeSet
import javax.swing.text.SimpleAttributeSet
import javax.swing.text.StyleConstants
import java.awt.Color

public class SqlEditorWindow {

   static String TITLE = 'IFMX SQL Editor'
   static String OUTPUT = 'Output'
   static String PINNED = 'Pinned'

   static String ABOUT = """
    IFMX SQL Editor

    A GUI tool for executing SQL commands on Informix databases,
    multi-platform replacement for original Informix's SQL Editor.

    Find the source and latest binaries at:
    http://code.google.com/p/ifmx-sql-editor.

    By using this program, you agree to terms of the Informix JDBC Driver
    license (see the licenses/jdbc_license.txt file).

    --

    Copyright 2012 Ognjen Orel

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
   """

   String activeFile // path to file in editor
   SwingBuilder swing = new SwingBuilder()

   DbManager dbManager = new DbManager()
   def sqlFileDialog

   ConnectionManager connectionManager = new ConnectionManager()

   def keyListener
   

   def SqlEditorWindow() {

      UIManager.setLookAndFeel('com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel')

      DefaultSyntaxKit.initKit()
      DefaultSyntaxKit.setProperty ('Components', 'jsyntaxpane.components.PairsMarker')

      def undoManager = new TextUndoManager()
      def styledDocument = new DefaultStyledDocument()
      styledDocument.addUndoableEditListener undoManager


      JFrame frame = swing.frame(title: TITLE, id: 'mainFrame', defaultCloseOperation: WindowConstants.EXIT_ON_CLOSE) {

         // first define menubar and toolbar actions 
         actionConnect = swing.action(name: 'Connect', closure: this.&connect) 

         actionNew = swing.action(name: 'New', mnemonic: 'N', accelerator: 'ctrl N', smallIcon: imageIcon('/ifmxsqleditor/resources/document-new.png')) {
            setActiveFile null
            swing.SQL.text = ''
         }

         actionOpen = swing.action(name: 'Open', mnemonic: 'O', accelerator: 'ctrl O', smallIcon: imageIcon('/ifmxsqleditor/resources/document-open.png')) {
            def dialog = getSqlFileDialog()
            if ( dialog.showOpenDialog() != JFileChooser.APPROVE_OPTION ) return
            setActiveFile dialog.selectedFile.absolutePath
            showFileInEditor activeFile
         }

         actionExecuteSelected = swing.action(name: 'Execute selected',
                                              accelerator: KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_MASK),
                                              closure: this.&executeSelected)

         actionExecuteAll = swing.action(name: 'Execute all',
                                         accelerator: KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_MASK + InputEvent.SHIFT_MASK),
                                         /*smallIcon: imageIcon('../resources/media-playback-start.png'),*/
                                         closure: this.&executeAll)

         actionCopy = swing.action(name: 'Copy', accelerator: 'ctrl C', smallIcon: imageIcon('/ifmxsqleditor/resources/edit-copy.png')) { swing.SQL.copy() }

         actionCut = swing.action(name: 'Cut', accelerator: 'ctrl X', smallIcon: imageIcon('/ifmxsqleditor/resources/edit-cut.png')) { swing.SQL.cut() }

         actionPaste = swing.action(name: 'Paste', accelerator: 'ctrl V', smallIcon: imageIcon('/ifmxsqleditor/resources/edit-paste.png')) { swing.SQL.paste() }

         actionUndo = swing.action(name: 'Undo', accelerator: 'ctrl Z', smallIcon: imageIcon('/ifmxsqleditor/resources/edit-undo.png')) {
            if (undoManager.canUndo())
               undoManager.undo()
         }

         actionRedo = swing.action(name: 'Redo', accelerator: 'ctrl Y', smallIcon: imageIcon('/ifmxsqleditor/resources/edit-redo.png')) {
            if (undoManager.canRedo())
               undoManager.redo()
         }

         actionClearOutput = swing.action(name: 'Clear output', accelerator: 'ctrl D') { swing.outputPane.text = '' }

         actionSaveAs = swing.action(name: 'Save as...', mnemonic: 'a', smallIcon: imageIcon('/ifmxsqleditor/resources/document-save-as.png')) {
            def dialog = getSqlFileDialog()
            if ( dialog.showSaveDialog() != JFileChooser.APPROVE_OPTION ) return
            setActiveFile dialog.selectedFile.absolutePath // remember this
            saveFile activeFile
         }

         actionSave = swing.action(name: 'Save', mnemonic: 'S', accelerator: 'ctrl S', smallIcon: imageIcon('/ifmxsqleditor/resources/document-save.png')) {
            if (!activeFile) {
               actionSaveAs.closure.call()
            }
            else {
               saveFile activeFile
            }
         }

         actionPrevTab = swing.action(name : 'Previous tab', mnemonic:'p', accelerator: KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, InputEvent.CTRL_MASK)) {
            JTabbedPane tb = swing.tabs
            if (tb.selectedIndex - 1 < 0) {
               tb.setSelectedIndex tb.tabCount-1
            }
            else {
               tb.setSelectedIndex (tb.selectedIndex - 1)
            }
         }

         actionNextTab = swing.action(name : 'Next tab', mnemonic:'n', accelerator: KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, InputEvent.CTRL_MASK)) {
            JTabbedPane tb = swing.tabs
            if (tb.selectedIndex + 1 >= tb.tabCount) {
               tb.setSelectedIndex 0
            }
            else {
               tb.setSelectedIndex (tb.selectedIndex + 1)
            }
         }

         actionKeepTab = swing.action(name: 'Keep tab', accelerator: 'ctrl K') {
            JTabbedPane tb = swing.tabs
            if (tb.selectedIndex > 0 && !tb.getTitleAt(tb.selectedIndex).startsWith('*')) {
               tb.setTitleAt (tb.selectedIndex, '*' + tb.getTitleAt(tb.selectedIndex))
            }
         }

         actionRemoveTab = swing.action(name: 'Remove tab', accelerator: 'ctrl R') {
            JTabbedPane tb = swing.tabs
            if (tb.getTitleAt(tb.selectedIndex).startsWith('*')) {
               tb.remove tb.selectedIndex
            }
         }

         actionAbout = swing.action(name: 'About') {  GUIUtils.showMessage ABOUT  }

         actionTest = swing.action(name: 'Test', closure: this.&testMethod)

         actionEditConnections = swing.action(name: 'Edit connections') {
            ConnectionsWindow cw = new ConnectionsWindow(connectionManager)
            cw.show(this)
         }

         actionExit = swing.action(name: 'Exit', mnemonic: 'x', accelerator: 'ctrl Q') { System.exit 0 }

         keyListener = [
               keyPressed: {event ->
                     if (event.modifiers == KeyEvent.CTRL_MASK) {
                        if (event.keyCode == KeyEvent.VK_PAGE_DOWN)
                           actionNextTab.closure.call()
                        if (event.keyCode == KeyEvent.VK_PAGE_UP)
                           actionPrevTab.closure.call()
                     }
                  },
               keyReleased: {},
               keyTyped: {}
         ] as KeyListener
         
         // menu
         menuBar {
            menu('File', mnemonic: 'F') {
               menuItem(action: actionNew)
               menuItem(action: actionOpen)
               menuItem(action: actionSave)
               menuItem(action: actionSaveAs)
               separator()
               menuItem(action: actionExit)
            }
            menu('Edit', mnemonic: 'E') {
               menuItem(action: actionUndo)
               menuItem(action: actionRedo)
               separator()
               menuItem(action: actionCut)
               menuItem(action: actionCopy)
               menuItem(action: actionPaste)
               separator()
               menuItem(action: actionClearOutput)
            }
            menu('SQL', mnemonic: 'S') {
               menuItem(action: actionExecuteSelected)
               menuItem(action: actionExecuteAll)
            }
            menu('Options', mnemonic: 'O') {
               menuItem(action: actionEditConnections)
//               menuItem 'Preferences'
            }
            menu('View', mnemonic: 'V') {
               menuItem(action: actionPrevTab)
               menuItem(action: actionNextTab)
               separator()
               menuItem(action: actionKeepTab)
               menuItem(action: actionRemoveTab)
            }
            menu('Help', mnemonic: 'H') {
                menuItem(action: actionAbout)
            }
         }

         // window content
         panel(layout: new BL()) {
            hbox(constraints: BL.NORTH) {
               toolBar {
                  button(action: actionNew, text:null)
                  button(action: actionOpen, text:null)
                  button(action: actionSave, text:null)
                  separator()
                  button(action: actionUndo, text:null)
                  button(action: actionRedo, text:null)
                  separator()
                  button(action: actionCut, text:null)
                  button(action: actionCopy, text:null)
                  button(action: actionPaste, text:null)
                  separator()
                  button(action: actionExecuteSelected)
                  button(action: actionExecuteAll)
                  glue()
                  label 'Connection '
                  comboBox(id: 'connectionsCombo',
                        action: actionConnect,
                        items: [' '] + connectionManager.connections.collect {it.alias})
               }
            }
            tabbedPane(id: 'tabs', tabPlacement: JTabbedPane.TOP) {
               splitPane(title: 'SQL', orientation: JSplitPane.VERTICAL_SPLIT, constraints: BL.CENTER) {
                  scrollPane(horizontalScrollBarPolicy: JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED,
                             verticalScrollBarPolicy: JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED) {
                     editorPane(id: 'SQL', preferredSize: new Dimension(700, 450), document: styledDocument, contentType: 'text/sql')
                  }
                  scrollPane(
                        horizontalScrollBarPolicy: JScrollPane.HORIZONTAL_SCROLLBAR_NEVER,
                        verticalScrollBarPolicy: JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED) {
                     //textArea(id: 'outputPane', rows: 5, lineWrap: true, wrapStyleWord: true, editable: false, autoscrolls: true)
                     textPane(id: 'outputPane', preferredSize: new Dimension(700, 80), editable: false)
                  }
               }
            }
         }
      }

      swing.SQL.addKeyListener keyListener
      frame.pack()
      GUIUtils.centerOnScreen frame
      frame.show()
      swing.SQL.requestFocus()
   }


   // file handling methods - begin
   private void setActiveFile(String file) {
      activeFile = file
      if (activeFile) {
         if (!activeFile.toLowerCase().endsWith('.sql')) {
            activeFile += '.sql'
         }
         swing.mainFrame.title = TITLE + ' - ' + activeFile
      }
      else
         swing.mainFrame.title = TITLE
   }

   private def showFileInEditor(String path) {
      File file = new File(path)
      def content = ''
      file.eachLine { redak ->
         content += redak + '\n'
      }
      swing.SQL.text = content
   }

   private def saveFile(String path) {
      File file = new File(path)
      file.delete()
      file.createNewFile()
      file << swing.SQL.text
   }

   private def getSqlFileDialog() {
      sqlFileDialog ?: (sqlFileDialog = swing.fileChooser(
            dialogTitle: "Choose an sql file",
            id: "sqlFileDialog",
            fileSelectionMode: JFileChooser.FILES_ONLY,
            fileFilter: [getDescription: {-> "*.sql"}, accept: {file -> file ==~ /.*?\.sql/ || file.isDirectory() }] as FileFilter) { }
      )
   }
   // file handling methods - end


   // database connection and query execution methods - begin
   private def connect(event) {
      if (swing.connectionsCombo.selectedItem.toString().trim().isEmpty()) {
         swing.connectionsCombo.toolTipText = 'Active user: none'
         return
      }

      ConnectionData cd = connectionManager.getDataByAlias(swing.connectionsCombo.selectedItem)
      LoginWindow lw = new LoginWindow()
      if (lw.show(cd.user)) {
         cd.user = lw.getUsername()
         cd.pwd = lw.getPwd()
         try {
            dbManager.connect(cd)
            appendToOutput 'Connected to ' + cd.alias + ' as ' + cd.user
            swing.connectionsCombo.toolTipText = 'Active user: ' + cd.user
         }
         catch (SQLException e) {
            GUIUtils.showError e.getMessage()
         }
      }
      else {
         swing.connectionsCombo.selectedItem = ' '
      }
      swing.SQL.requestFocus()
   }

   public def refreshConnections() {
      def activeConnection = swing.connectionsCombo.selectedItem
      def newItems = [' '] + connectionManager.connections.collect {it.alias}
      List<Object> alreadyInList = new ArrayList<Object>()
      for (int i = 0; i < swing.connectionsCombo.getItemCount(); i++) {
         def item = swing.connectionsCombo.getItemAt(i)
         if (newItems.contains(item)) {
            // this one is the same, remove from the new items
            newItems.remove(item)
            if (alreadyInList.contains(item))
               swing.connectionsCombo.removeItem(item)
            else
               alreadyInList.add(item)
         }
         else {
            // this one should be deleted, but if it is the active connection, disconnect first
            if (activeConnection.equals(item)) {
               activeConnection = ' '
               dbManager.disconnect()
            }
            swing.connectionsCombo.removeItem(item)
         }
      }
      // now add all new items
      newItems.each {
         swing.connectionsCombo.addItem(it);
      }
      swing.connectionsCombo.selectedItem = activeConnection
   }

   private def executeAll(event) {
      clearOutputTabs()
      if (swing.SQL.text)
         execute(swing.SQL.text)
   }

   private def executeSelected(event) {
      if (swing.SQL.selectedText?.trim()?.length() > 0) {
         clearOutputTabs()
         execute(swing.SQL.selectedText)
      }
   }


   private def execute(String sql) {
      // calls for sql execution and shows results
      if (!dbManager.connected() || swing.connectionsCombo.selectedItem.toString().trim().isEmpty()) {
         GUIUtils.showMessage 'Connection to a database not established yet'
         return
      }

      swing.mainFrame.cursor = Cursor.WAIT_CURSOR
      List<SqlResult> result = dbManager.executeSql(sql)

      result.each { it ->
         appendToOutput it.sql
         switch(it.result.class) {
            case SQLException:
               appendToOutput '-->' + getErrorText(it.result), true
               break
            case String:
               appendToOutput '-->' + it.result
               appendToOutput '--> Execution time: ' + it.time + ' ms'
               break
            case TableModel:
               if (it.result.rowCount > 0)
                  appendToOutput '-->' + it.result.rowCount + ' rows shown in tab ' + addOutputTab(it.sql, it.result)
               else
                  appendToOutput '--> Executed, no rows returned '
               appendToOutput '--> Execution time: ' + it.time + ' ms'
               break
            default:
               appendToOutput '-->No handler for this result class: ' + it.result.class
         }
         appendToOutput('')
      }
      swing.mainFrame.cursor = Cursor.DEFAULT_CURSOR
   }
   // database connection and query execution methods - end


   // result tabs handling methods - begin
   def clearOutputTabs() {
      // remove all unpinned output tabs
      JTabbedPane tp = swing.tabs
      if (tp.tabCount > 1) {
         for (i in tp.tabCount - 1 .. 0) {
            if (tp.getTitleAt(i).startsWith(OUTPUT)) {
               tp.remove i
            }
         }
      }
   }

   def addOutputTab(String sql, TableModel tm) {
      JTabbedPane tp = swing.tabs
      def j = 1
      for (i in 0 .. tp.tabCount - 1) {
         if (tp.getTitleAt(i).startsWith(OUTPUT))
            j++
      }
      def title = OUTPUT + j
      tp.add(title,
         swing.splitPane(orientation: JSplitPane.VERTICAL_SPLIT) {
            scrollPane(horizontalScrollBarPolicy: JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED,
                       verticalScrollBarPolicy: JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED) {
               ta = swing.textArea(text: sql, rows: 3, editable: false, autoscrolls: true, lineWrap: true, wrapStyleWord: true)
               ta.addKeyListener keyListener
            }
            widget(getTable(tm))
      })
      return title
   }

   private def getTable(TableModel tm) {
      // creates new results table
      JXTable table = new JXTable(model: tm, sortable: true, columnControlVisible: true, autoResizeMode: JXTable.AUTO_RESIZE_OFF, cellSelectionEnabled: true/*, showVerticalLines: true*/)
      table.addHighlighter HighlighterFactory.createSimpleStriping();
      table.addKeyListener keyListener
      table.packAll()
      table.requestFocus()

      new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED)
   }
   // result tabs handling methods - end

   
   // utility methods - begin
   private String getErrorText(SQLException e) {
      def out = e.errorCode + ': ' + e.message
      def next = e.getNextException()
      if (next) {
         out += '(ISAM: ' + next.errorCode + ': ' + next.message + ')'
      }
      return out
   }

   private def appendToOutput (String s, boolean isException = false) {
      //swing.outputPane.append s + '\n'
      s += '\n'

      Color c
      if (s.startsWith('-->'))
         if (isException)
            c = Color.red.darker()
         else
            c = Color.green.darker()
      else
         c = Color.black

      StyleContext sc = StyleContext.getDefaultStyleContext()
      AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY,
                                          StyleConstants.Foreground, c)

      int len = swing.outputPane.getDocument().getLength() // same value as getText().length();

      swing.outputPane.editable = true
      swing.outputPane.setCaretPosition(len)  // place caret at the end (with no selection)
      swing.outputPane.setCharacterAttributes(aset, false)
      swing.outputPane.replaceSelection(s) // there is no selection, so inserts at caret
      swing.outputPane.editable = false
   }
   // utility methods - end




   def testMethod(event) {
   }
}