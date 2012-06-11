/******************************************************************************/
/*                    LICENCING INFORMATION & LEGAL STUFF                     */
/******************************************************************************/
/* This code is copyrighted by Alexander J. Harris on the date included in    */
/* the next set of comments in this code file.                                */
/*                                                                            */
/* All rights are reserved by the copyright holder, unless the code forms     */
/* part of one or more code libraries that are subject to "open source"       */
/* software agreements such as the GNU General Purpose License (GPL), Lesser  */
/* General Purpose Library (LGPL), Creative Commons etc. or any other         */
/* explicitly specified and mentioned licencing terms.                        */
/*                                                                            */
/* This program is intended for use by the recipient and their organisation   */
/* only (hereafter known as the "owner") and not to be redistributed, sold,   */
/* rented or otherwise transferred outside of the owner without express       */
/* written permission from the copyright holder.                              */
/*                                                                            */
/* This code has been tested and verified as bug-free to the best of the      */
/* copyright holder's abilities but makes no assertions as to its             */
/* compatibility on equipment held by the owner. The copyright holder accepts */
/* no liability for the loss of data, hardware or service through the use of  */
/* this software nor any financial losses that may result.                    */
/*                                                                            */
/* The copyright holder offers no warranty on the operation of the program    */
/* and/or code used within the program other than informal support offered on */
/* a basis of goodwill and best endeavours.                                   */
/*                                                                            */
/* This code may make use of libraries that are subject to alternative        */
/* licencing arrangements and uses them within the terms of their individual  */
/* terms and conditions. Any libraries distributed with this application may  */
/* be subject to additional or alternative licencing terms to those already   */
/* stated.                                                                    */
/*                                                                            */
/* Documentation within this program has been generated through the use of    */
/* Javadoc comments and tags. The resulting documentation may include         */
/* names and trademarks of other companies and entities which are owned by    */
/* their respective holders. The copyright holder of this work does not       */
/* intend to infringe on any copyrights, trade marks or registered trade      */
/* names.                                                                     */
/*                                                                            */
/* Any works by others will be acknowledged in a dedicated HTML file included */
/* in the program's root directory.                                           */
/******************************************************************************/
/* UPDATE                                                                     */
/******************************************************************************/
/* JavaNews has been released under the terms of a Creative Commons license   */
/* effective from 11 June 2012. The type of Creative Commons license is a:    */
/* Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License */
/* for more information please refer to the Creative Commons website at:      */
/* http://creativecommons.org/licenses/by-nc-sa/3.0/                          */
/******************************************************************************/

/* Class name: COMPortClient
 * File name:  COMPortClient.java
 * Project:    JavaNews - PSE Monitoring
 * Copyright:  © 2007-2009 Alexander J. Harris, all rights reserved
 * Created:    17-Dec-2008 21:12:48
 * Modified:   17-Jun-2009
 *
 * Version History:
 * ~ ~ ~ ~ ~ ~ ~ ~ ~
 * 1.000  17-Jun-2009 Code finalised and released.
 * 0.011  17-Jun-2009 Added support for methods changing links from discovered
 *        to recognised.
 * 0.010  07-Jun-2009 Added LinkDetail support for the discovered link table.
 * 0.009  28-May-2009 Changed support for the table to remove the Ack column and
 *        add the UUID column in its place.
 * 0.008  24-Mar-2009 Added support for charts to be displayed showing link activity.
 * 0.007  16-Mar-2009 Changed startThread and stopThread functions to use the COMPortThreadManager class.
 * 0.006  03-Mar-2009 Added save function to the menu bar.
 * 0.005  02-Mar-2009 Changed the GUI build to include borders around components to show their
 *        purpose to the user. Added code to read PSEs and links for report tree.
 * 0.004  26-Feb-2009 Added newLinkTable to the GUI, altered the reportTree to show the links
 *        and fixed issues relating to the hashtable monitoring thread execution.
 * 0.003  29-Jan-2009 Added startThread, stopThread, addAlarmData and setLinkData methods.
 *        Also added a menu button to control monitoring execution.
 * 0.002  22-Jan-2009 Changed visibility of the buildReportTree, buildAlarmTable and buildLinkTable
 *        methods from public to private. Added buildStatus and buildMenu methods to add a status bar
 *        and menu to the window.
 * 0.001  17-Dec-2008 Initial build
 */

package javanews.gui.internalframe;
import javanews.database.*;
import javanews.events.gui.*;
import javanews.events.table.*;
import javanews.gui.*;
import javanews.object.*;
import javanews.object.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.text.*;
import javax.swing.tree.*;

/**
 * Represents and displays a monitoring thread for a specific COM port given a
 * set of compatible parameters. This class is responsible for monitoring a given
 * COM port specified by the user. The window displays those messages received
 * from that COM port that can be understood and interpreted as vaild PSE messages.
 * The window also lists PSEs and their configured links that have been entered
 * into the database with supporting information. The window can also recognise
 * those PSEs and links that have not been entered or acknowledged by the user
 * and lists these seperately for acknowledgement and further action from the user.
 * <p>This class forms the backbone of the rest of the application with many of
 * the other classes being called through interactions with this window.
 * @version 1.000
 * @author Alex Harris (email: Alexander.J.Harris(at)btinternet.com)
 */

public class COMPortClient extends JInternalFrame
{
  /** The logger to be used for writing messages to the application log */
  private Logger log;
  /** The unique name of the logger within the application */
  private static final String className = "javanews.gui.internalframe.COMPortClient";
  /** The list of the types of reports that can be run within the application */
  private JTree reportTree;
  /** The list of received alarms and messages from the PSE network */
  private JTable alarmTable;
  /** The list of PSEs and links that are known to the application and stored within the database */
  private JTable linkTable;
  /** The list of PSEs and links that have been discovered by the application and aren't stored in the database */
  private JTable newLinkTable;
  /** The window's menu bar */
  private JMenuBar jmbMenu;
  /** The status indicator to show whether data is being received and processed */
  private JLabel jlStatus;
  /** The windwo displaying information received directly from the COM port, if open */
  private RawDataWindow rdwData;
  /** The identifying name of the COM port the window is monitoring */
  private String portName;
  /** The type of parity being used with the COM port */
  private String parityType;
  /** The baud rate being used with the COM port */
  private int baudRate;
  /** The number of bits used to carry data with the COM port */
  private int dataBits;
  /** The size of the stop bits used with the COM port */
  private float stopBits;
  /** The list of charts open and their purpose */
  private Hashtable<String,LinkChart> htCharts;
  /** The constant used to indicate that the class is attached to an instance of the recognised link table */
  private final int RECOGNISED_LINK_TABLE = 1;
  /** The constant used to indicate that the class is attached to an instance of the alarm table */
  private final int ALARM_TABLE = 2;
  /** The constant used to indicate that the class is attached to an instance of the discovered link table */
  private final int DISCOVERED_LINK_TABLE = 3;
  /** Whether the comments table is empty or not */
  private boolean isAlarmTableEmpty;

  /**
   * Intialises the class, internal logger and set the parameters for the COM port
   * connection.
   * @param comPortName The identifying name of the COM port
   * @param baud The requested baud rate for the COM port
   * @param data The length of the data in bits
   * @param parity The parity type to be used for error correction
   * @param stop The length of the stop bits
   */
  public COMPortClient(String comPortName, int baud, int data, String parity, float stop)
  {
    super("Events from COM Port: "+comPortName+" "+baud+"-"+data+"-"+parity.toUpperCase().charAt(0)+"-"+stop, true, true, true, true);
    super.setLayer(1);
    portName = comPortName;
    parityType = parity;
    baudRate = baud;
    dataBits = data;
    stopBits = stop;
    isAlarmTableEmpty = true;
    htCharts = new Hashtable<String,LinkChart>();
    // Obtain an instance of Logger for the class
    log = LoggerFactory.getLogger(className);

    // Building the link table (existing links)
    linkTable = buildLinkTable();
    JScrollPane scrlExistingLinks = new JScrollPane(linkTable);
    JPanel jpOldLinks = new JPanel();
    jpOldLinks.add(scrlExistingLinks);
    TitledBorder tbOldLinks = BorderFactory.createTitledBorder("Known Links:");
    jpOldLinks.setBorder(tbOldLinks);
    SpringLayout slOldLinks = new SpringLayout();
    jpOldLinks.setLayout(slOldLinks);
    slOldLinks.putConstraint(SpringLayout.NORTH, scrlExistingLinks, 2, SpringLayout.NORTH, jpOldLinks);
    slOldLinks.putConstraint(SpringLayout.EAST, scrlExistingLinks, -2, SpringLayout.EAST, jpOldLinks);
    slOldLinks.putConstraint(SpringLayout.SOUTH, scrlExistingLinks, -2, SpringLayout.SOUTH, jpOldLinks);
    slOldLinks.putConstraint(SpringLayout.WEST, scrlExistingLinks, 2, SpringLayout.WEST, jpOldLinks);

    // Building the newly discovered link table
    newLinkTable = buildNewLinks();
    JScrollPane scrlNewLinks = new JScrollPane(newLinkTable);
    JPanel jpNewLinks = new JPanel();
    jpNewLinks.add(scrlNewLinks);
    TitledBorder tbNewLinks = BorderFactory.createTitledBorder("Discovered Links:");
    jpNewLinks.setBorder(tbNewLinks);
    SpringLayout slNewLinks = new SpringLayout();
    jpNewLinks.setLayout(slNewLinks);
    slNewLinks.putConstraint(SpringLayout.NORTH, scrlNewLinks, 2, SpringLayout.NORTH, jpNewLinks);
    slNewLinks.putConstraint(SpringLayout.EAST, scrlNewLinks, -2, SpringLayout.EAST, jpNewLinks);
    slNewLinks.putConstraint(SpringLayout.SOUTH, scrlNewLinks, -2, SpringLayout.SOUTH, jpNewLinks);
    slNewLinks.putConstraint(SpringLayout.WEST, scrlNewLinks, 2, SpringLayout.WEST, jpNewLinks);

    // Building the alarm table
    alarmTable = buildAlarmTable();
    // Adding it to a scroll pane
    JScrollPane scrlAlarms = new JScrollPane(alarmTable);
    alarmTable.setFillsViewportHeight(true);
    // Adding the scroll pane to a JPanel to display a border
    JPanel jpAlarms = new JPanel();
    jpAlarms.add(scrlAlarms);
    // Put a titled border around the component to show the user what it's for
    TitledBorder tbAlarms = BorderFactory.createTitledBorder("Received Alarms:");
    jpAlarms.setBorder(tbAlarms);
    // Setting the layout type and constraints
    SpringLayout slAlarms = new SpringLayout();
    jpAlarms.setLayout(slAlarms);
    slAlarms.putConstraint(SpringLayout.NORTH, scrlAlarms, 2, SpringLayout.NORTH, jpAlarms);
    slAlarms.putConstraint(SpringLayout.EAST, scrlAlarms, -2, SpringLayout.EAST, jpAlarms);
    slAlarms.putConstraint(SpringLayout.SOUTH, scrlAlarms, -2, SpringLayout.SOUTH, jpAlarms);
    slAlarms.putConstraint(SpringLayout.WEST, scrlAlarms, 2, SpringLayout.WEST, jpAlarms);

    // And finally for the report tree
    reportTree = buildReportTree();
    JScrollPane scrlReports = new JScrollPane(reportTree);
    JPanel jpReports = new JPanel();
    jpReports.add(scrlReports);
    TitledBorder tbReports = BorderFactory.createTitledBorder("Reports:");
    jpReports.setBorder(tbReports);
    SpringLayout slReports = new SpringLayout();
    jpReports.setLayout(slReports);
    slReports.putConstraint(SpringLayout.NORTH, scrlReports, 2, SpringLayout.NORTH, jpReports);
    slReports.putConstraint(SpringLayout.EAST, scrlReports, -2, SpringLayout.EAST, jpReports);
    slReports.putConstraint(SpringLayout.SOUTH, scrlReports, -2, SpringLayout.SOUTH, jpReports);
    slReports.putConstraint(SpringLayout.WEST, scrlReports, 2, SpringLayout.WEST, jpReports);

    // Add the items to splitpanes in the right order
    JSplitPane spltLinks = new JSplitPane(JSplitPane.VERTICAL_SPLIT, jpOldLinks, jpNewLinks);
    JSplitPane spltLeft = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,jpReports,jpAlarms);
    JSplitPane spltRight = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,spltLeft,spltLinks);
    // Set the divider locations and splitpane properties
    spltLinks.setContinuousLayout(true);
    spltLinks.setDividerLocation(200);
    spltLeft.setContinuousLayout(true);
    spltLeft.setDividerLocation(200);
    spltRight.setContinuousLayout(true);
    spltRight.setDividerLocation(550);

    buildMenu();
    // Settting the JMenuBar
    setJMenuBar(jmbMenu);

    // Add the splitpane to the desktop
    Container contFrame = getContentPane();
    contFrame.add(spltRight, BorderLayout.CENTER);
    contFrame.add(buildStatus(), BorderLayout.SOUTH);
    this.addInternalFrameListener(new CloseComPortClient(this));
    log.finest("Adding frame to the desktop");
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    this.setSize(800, 400);
    Client parent = Client.getJNWindow();
    parent.addToDesktop(this);
    this.setVisible(true);
  }

  /**
   * Builds the menu for the window and attaches mnemonics and events to entries
   * within the menu.
   */
  private void buildMenu()
  {
    jmbMenu = new JMenuBar();
/* ~ ~ ~ ~ ~ ~ FILE MENU ~ ~ ~ ~ ~ ~ */
    JMenu jmFile = new JMenu("File");
    jmFile.setMnemonic(KeyEvent.VK_F);
    JMenuItem jmiFile_Save = new JMenuItem("Save As");
    jmiFile_Save.setMnemonic(KeyEvent.VK_S);
    jmiFile_Save.addActionListener(new COMPortClient_Save(this));
    JMenuItem jmiFile_Exit = new JMenuItem("Exit");
    jmiFile_Exit.setMnemonic(KeyEvent.VK_E);
    jmiFile_Exit.addActionListener(new CloseComPortClient(this));
    jmFile.add(jmiFile_Save);
    jmFile.add(jmiFile_Exit);
    jmbMenu.add(jmFile);
/* ~ ~ ~ ~ ~ ~ VIEW MENU ~ ~ ~ ~ ~ ~ */
    JMenu jmView;
    jmView = new JMenu("View");
    JMenuItem jmiView_RawData = new JMenuItem("Raw Data");
    jmiView_RawData.setMnemonic(KeyEvent.VK_R);
    jmiView_RawData.addActionListener(new COMPortClient_ShowRawData(this));
    JMenuItem jmiView_OldChart = new JMenuItem("Known Link Stats");
    jmiView_OldChart.setMnemonic(KeyEvent.VK_K);
    jmiView_OldChart.addActionListener(new COMPortClient_ShowCharts("old",this));
    JMenuItem jmiView_NewChart = new JMenuItem("Discovered Link Stats");
    jmiView_NewChart.setMnemonic(KeyEvent.VK_D);
    jmiView_NewChart.addActionListener(new COMPortClient_ShowCharts("new",this));
    jmView.add(jmiView_RawData);
    jmView.addSeparator();
    jmView.add(jmiView_OldChart);
    jmView.add(jmiView_NewChart);
    jmbMenu.add(jmView);
  }

  /**
   * Builds the status bar at the bottom of the window containing the status
   * "light" indicating that data is being received and processed.
   * @return The panel containing the status "light" to be placed in the window
   */
  private JPanel buildStatus()
  {
    JPanel jpBottom = new JPanel();
    jlStatus = new JLabel(new ImageIcon("images/rLightOn.png"));
    turnStatusOff();
    SpringLayout slBottom = new SpringLayout();
    jpBottom.setLayout(slBottom);
    slBottom.putConstraint(SpringLayout.EAST, jlStatus, -5, SpringLayout.EAST, jpBottom);
    slBottom.putConstraint(SpringLayout.SOUTH, jpBottom, 5, SpringLayout.SOUTH, jlStatus);
    jpBottom.add(jlStatus);
    return jpBottom;
  }

  /**
   * Builds the tree used to contain entries allowing the user to run reports
   * between certain dates or for PSEs or specific links on a PSE.
   * @return The JTree to be placed in the window
   */
  private JTree buildReportTree()
  {
    log.finest("Building the report tree");
    JTree tree;
    DefaultMutableTreeNode parent = new DefaultMutableTreeNode("JavaNews Reports");
    DefaultTreeModel treeModel = new DefaultTreeModel(parent);
    tree = new JTree(treeModel);
    tree.addTreeSelectionListener(new ReportTree(this));
    DefaultMutableTreeNode c1 = new DefaultMutableTreeNode("By PSE Link");
    DefaultMutableTreeNode c2 = new DefaultMutableTreeNode("By Date");
      DefaultMutableTreeNode c21 = new DefaultMutableTreeNode("Last 30 minutes");
      DefaultMutableTreeNode c22 = new DefaultMutableTreeNode("Last hour");
      DefaultMutableTreeNode c23 = new DefaultMutableTreeNode("Last 24 hours");
      DefaultMutableTreeNode c24 = new DefaultMutableTreeNode("Last 7 days");
      DefaultMutableTreeNode c25 = new DefaultMutableTreeNode("Last 30 days");
    parent.add(c1);
    parent.add(c2);
    c2.add(c21);
    c2.add(c22);
    c2.add(c23);
    c2.add(c24);
    c2.add(c25);
    // Iterate through the links we know about and add them to the nodes.
    LinkTable lT = (LinkTable) linkTable.getModel();
    if (lT == null)
    {
      return null; // This shouldn't occur but if it does then exit
    }
    Iterator iPSE = lT.getPSENames().iterator();
    while (iPSE.hasNext())
    {
      String aPSE = (String) iPSE.next();
      DefaultMutableTreeNode dmtnTemp = new DefaultMutableTreeNode(aPSE); // Create a new parent node for the pse to add links to as children
      c1.add(dmtnTemp);
      Iterator iLinks = lT.getLinkNames(aPSE).iterator();
      while (iLinks.hasNext())
      {
        String aLink = (String) iLinks.next();
        dmtnTemp.add(new DefaultMutableTreeNode(aLink));
      }
    }
    tree.collapseRow(1);
    tree.expandRow(0);
    return tree;
  }

  /**
   * Builds the table used to display received alarms.
   * @return The table to be used for displaying alarms ready to be placed in the window
   */
  private JTable buildAlarmTable()
  {
    log.finest("Building the alarm table");
    log.entering("buildAlarmTable()", "GenericTable.GenericTable()");
    GenericTable gT = new GenericTable();
    JTable jT = new JTable(gT);
    jT.setRowSorter(new TableRowSorter(gT));
    jT.setRowSelectionAllowed(true);
    jT.setColumnSelectionAllowed(false);
    log.finest("Adding RowListener to Alarm Table");
    jT.getSelectionModel().addListSelectionListener(new RowListener(ALARM_TABLE,jT,this));
    Vector vecColumnNames = new Vector();
    Vector vecRows = new Vector();
    vecColumnNames.add("Severity");
    vecColumnNames.add("PSE");
    vecColumnNames.add("Link");
    vecColumnNames.add("Message");
    vecColumnNames.add("Sent");
    vecColumnNames.add("UUID");

    Object[] aRow = new Object[7];
    aRow[0] = "Started logging";
    aRow[1] = "";
    aRow[2] = "";
    aRow[3] = "";
    aRow[4] = Calendar.getInstance().getTime().toString();
    aRow[5] = "";
    vecRows.add(aRow);

    log.entering("buildAlarmTable()", "GenericTable.setData(Vector, Vector)", new Object[] {vecColumnNames, vecRows});
    gT.setData(vecColumnNames, vecRows);
    return jT;
  }

  /**
   * Retrieves the data from the database about links known to the system. On
   * initial launch all of the links will be shown with a link state of unknown
   * until the application receives an explicit LINK UP or LINK DOWN message from
   * a link. At this point the state will be updated until further UP or DOWN
   * messages are received.
   * @return The known links in a table for display in the window
   */
  private JTable buildLinkTable()
  {
    log.finest("Building the link table");
    log.entering("buildLinkTable()","LinkTable.LinkTable()");
    LinkTable lT = new LinkTable(portName);
    JTable jT = new JTable(lT);
    jT.setDefaultRenderer(Integer.class, new LinkRenderer(true));
    jT.setRowSorter(new TableRowSorter(lT));
    jT.setRowSelectionAllowed(true);
    jT.setColumnSelectionAllowed(false);
    log.finest("Adding RowListener to Link Table");
    jT.getSelectionModel().addListSelectionListener(new RowListener(RECOGNISED_LINK_TABLE,jT,this));
    DbInterface dbI = new DbInterface("System");
    Vector vecDBRows = dbI.getCodexLinks();
    for (int i=0; i < vecDBRows.size(); i++)
    {
      Object[] temp = (Object[]) vecDBRows.get(i);
      lT.addLink(((String) temp[0]),((String) temp[1]),new Integer(2));
    }
    return jT;
  }

  /**
   * Builds a blank table for newly discovered links to be added. As they are
   * discovered by the application they are added with a link state if a LINK UP
   * or LINK DOWN message is received.
   * @return The discovered links in a table for display in the window.
   */
  private JTable buildNewLinks()
  {
    log.finest("Building the table for newly discovered links");
    LinkTable lT = new LinkTable(portName);
    JTable jT = new JTable(lT);
    jT.setDefaultRenderer(Integer.class, new LinkRenderer(true));
    jT.setRowSorter(new TableRowSorter(lT));
    jT.setRowSelectionAllowed(true);
    jT.setColumnSelectionAllowed(false);
    log.finest("Adding RowListener to Link Table");
    jT.getSelectionModel().addListSelectionListener(new RowListener(DISCOVERED_LINK_TABLE,jT,this));
    return jT;
  }

  /**
   * Returns the instance of <code>JTree</code> used by the user to select
   * reports either by date/time or by PSE/link.
   * @return The tree used for report generation
   * @see javax.swing.JTree
   */
  public JTree getReportTree()
  {
    log.finest("Returning the report tree to the receiver");
    return reportTree;
  }

  /**
   * Returns the instance of <code>JTable</code> used to display alarms to the user.
   * @return The table used to hold received alarms
   * @see javax.swing.JTable
   */
  public JTable getAlarmTable()
  {
    log.finest("Returning the alarm table to the receiver");
    return alarmTable;
  }

  /**
   * Returns the instance of <code>JTable</code> used to display recognised links.
   * Recognised links are those stored within the back-end database in persistent
   * storage.
   * @return The table used to hold existing links
   * @see javax.swing.JTable
   */
  public JTable getLinkTable()
  {
    log.finest("Returning the link table to the receiver");
    return linkTable;
  }

  /**
   * Returns the instance of <code>JTable</code> used to display discovered links.
   * Discovered links are those stored in memory and will be lost on application
   * shut-down unless acknowledge by the user and thus moved into the back-end
   * database and "existing links".
   * @return The table used to hold discovered links
   * @see javax.swing.JTable
   */
  public JTable getNewLinkTable()
  {
    log.finest("Returning the newly discovered link table to the receiver");
    return newLinkTable;
  }

  /**
   * Changes the status light in the bottom right-hand corner of the screen to
   * the illuminated (on) icon. This should be done while the application receives
   * data and is processing it until the point that data is stored in the database.
   */
  public void turnStatusOn()
  {
    jlStatus.setIcon(new ImageIcon("images/rLightOn.png"));
    jlStatus.repaint();
  }

  /**
   * Changes the status light in the bottom right-hand corner of the screen to
   * the unilluminated (off) icon. This is the default status for the icon.
   */
  public void turnStatusOff()
  {
    jlStatus.setIcon(new ImageIcon("images/rLightOff.png"));
    jlStatus.repaint();
  }

  /**
   * Adds an instance of <code>RawDataWindow</code> to show that the user wants to see the
   * received data on the COM port.
   * @param rdwNew The instance of <code>RawDataWindow</code> to be displayed
   * @see javanews.gui.internalframe.RawDataWindow
   */
  public void addRawDataWindow(RawDataWindow rdwNew)
  {
    rdwData = rdwNew;
  }

  /**
   * Removes an instance of <code>RawDataWindow</code> from the class.
   * This should be called from the <code>windowClosing()</code> event as the
   * window closes.
   */
  public void removeRawDataWindow()
  {
    rdwData = null;
  }

  /**
   * To be used with <code>isRawDataWindowOpen()</code> to determine whether
   * an instance is being used and return it if necessary.
   * @return The instance of <code>RawDataWindow</code> attached to this thread.
   * @see javanews.gui.internalframe.RawDataWindow
   */
  public RawDataWindow getRawDataWindow()
  {
    return rdwData;
  }

  /**
   * Indicates whether there is an instance of <code>RawDataWindow</code> currently
   * attached to this class. This can be used in conjunction with other methods
   * to manipulate the presence of a window displaying raw data.
   * @return Whether there is a window open displaying raw data from the COM port
   */
  public boolean isRawDataWindowOpen()
  {
    if (rdwData == null)
    {
      return false;
    }
    else
    {
      return true;
    }
  }

  /**
   * Returns the name of the COM port this class is configured to use.
   * @return The name of the COM port
   */
  public String getPortName()
  {
    return portName;
  }

  /**
   * Returns the type of parity being used for error correction.
   * @return The parity type being used
   */
  public String getParityType()
  {
    return parityType;
  }

  /**
   * Returns the baud rate of the monitored COM port.
   * @return The baud rate
   */
  public int getBaudRate()
  {
    return baudRate;
  }

  /**
   * Returns the length of the data being received on the COM port in bits.
   * @return The lenght of the data words
   */
  public int getDataBits()
  {
    return dataBits;
  }

  /**
   * Returns the length of the stop bits being used on the COM port.
   * @return The length of the stop bits
   */
  public float getStopBits()
  {
    return stopBits;
  }

  /**
   * Stops the current thread from executing and receiving any further messages
   * on the COM port.
   */
  public void stopThread()
  {
    COMPortThreadManager cptm = COMPortThreadManager.getInstance();
    cptm.stopThread(portName);
  }

  /**
   * Adds a new row to the alarm table within the window. The supplied array
   * must be composed as follows:
   * <ul>
   * <li>[0] Message severity</li>
   * <li>[1] PSE which generated the message</li>
   * <li>[2] Originating link</li>
   * <li>[3] Error message which was generated</li>
   * <li>[4] Date when the message was generated (according to the PSE)</li>
   * <li>[5] The Universally Unique Identifier of the message</li>
   * </ul>
   * @param newRow The new row to be added to the table containing the columns
   *                mentioned above.
   */
  public void addAlarmData(Object[] newRow)
  {
    log.entering("addAlarmData(Object[])","GenericTable()");
    GenericTable gT = new GenericTable();
    gT = (GenericTable) alarmTable.getModel();
    if (isAlarmTableEmpty)
    {
      Vector rows = new Vector();
      rows.add(newRow);
      gT.setData(rows);
      isAlarmTableEmpty = false;
    }
    else
    {
      gT.addRow(newRow);
    }
  }

  /**
   * Sets the state of an existing link. The supplied array must be composed as
   * follows:
   * <ul>
   * <li>[0] PSE Name</li>
   * <li>[1] Link ID on the named PSE</li>
   * <li>[2] Status of the link, where:</li>
   *   <ul>
   *   <li>0 - Link Down</li>
   *   <li>1 - Link Up</li>
   *   <li>2 - Default state, unknown</li>
   *   </ul>
   * </ul>
   * @param newRowData The new row to be added to the table containing the columns
   *                mentioned above.
   */
  public void setLinkData(Object[] newRowData)
  {
    log.entering("setLinkData(Object[])","LinkTable()");
    LinkTable lT = new LinkTable(portName);
    lT = (LinkTable) linkTable.getModel();
    lT.setLinkState((String) newRowData[0], (String) newRowData[1], (Integer) newRowData[2]);
    if (containsChartWindow("old"))
    {
      LinkChart lc = getChartWindow("old");
      lc.updateData(lT.getInitialFigures());
    }
  }

  /**
   * Adds a newly discovered link to the discovered link table along with its
   * current state.
   * @param pse The name of the PSE the link is attached to
   * @param linkID The link ID that has been discovered
   * @param state The state of the link:
   *   <ul>
   *   <li>0 - Link Down</li>
   *   <li>1 - Link Up</li>
   *   <li>2 - Default state, unknown</li>
   *   </ul>
   */
  public void addNewLink(String pse, String linkID, int state)
  {
    log.fine("Adding new link to the list: " + pse + " " + linkID);
    LinkTable lT = new LinkTable(portName);
    lT = (LinkTable) newLinkTable.getModel();
    lT.addLink(pse, linkID, state);
    if (containsChartWindow("new"))
    {
      LinkChart lc = getChartWindow("new");
      lc.updateData(lT.getInitialFigures());
    }
  }

  /**
   * Attaches a chart window to the current window.
   * @param name The name and purpose of the chart window
   * @param lcWindow The window itself
   * @see javanews.gui.internalframe.LinkChart
   */
  public void addChartWindow(String name, LinkChart lcWindow)
  {
    htCharts.put(name,lcWindow);
  }

  /**
   * Returns the chart window if one is present.
   * @param name The name of the chart window to obtain.
   * @return The requested instance of <code>LinkChart</code>
   * @see javanews.gui.internalframe.LinkChart
   */
  public LinkChart getChartWindow(String name)
  {
    return htCharts.get(name);
  }

  /**
   * Whether the window contains an instance of <code>LinkChart</code> with the
   * supplied name
   * @param name The name of the <code>LinkChart</code> to find
   * @return Whether the name is present in the Hashtable
   */
  public boolean containsChartWindow(String name)
  {
    return htCharts.containsKey(name);
  }

  /**
   * Remove and close the chart window from the current window.
   * @param name The name of the <code>LinkChart</code> to be removed.
   */
  public void removeChartWindow(String name)
  {
    htCharts.remove(name);
  }

  /**
   * Migrates the named link from the discovered link table to the recognised
   * table.
   * @param discoveredId The ID of the link within the tblNewLinks table
   * @param recognisedId The ID of the link within the tblCodexLinks table
   */
  public void migrateLink(String discoveredId, String recognisedId)
  {
    final int WORKED_OK = 1;
    DbInterface dbI = new DbInterface(portName);
    int result = dbI.updateLinkIDs(recognisedId, discoveredId);
    if (result == WORKED_OK)
    {
      log.finest("Link ID has been updated from a discovered link ID of " + discoveredId + " to a recognised link with the ID of " + recognisedId);
    }
    else
    {
      log.warning("Problem while migrating discovered link (" + discoveredId + ") to a recognised link (" + recognisedId + ")");
    }
  }

  /**
   * Removes the specified link from the new links table within the database and
   * from the GUI.
   * @param discoveredLinkId The universally unique identifier of the discovered
   * link to be removed from the database.
   * @return The state of the link when it was removed. -1 if it wasn't found.
   */
  public Integer removeDiscoveredLink(String discoveredLinkId)
  {
    final int WORKED_OK = 1;
    LinkTable ltNewLinks = (LinkTable) newLinkTable.getModel();
    DbInterface dbI = new DbInterface(portName);
    Vector vecRows = ltNewLinks.getRows();
    for (int i = 0; i < vecRows.size(); i++)
    {
      Object[] objTemp = (Object[]) vecRows.get(i);
      String foundID = dbI.findDiscoveredLinkID((String) objTemp[0], (String) objTemp[1]);
      if (foundID.equals(discoveredLinkId))
      {
        Integer linkState = (Integer) objTemp[2];
        log.finest("Removing row " + i + "from the table. ID:" + foundID + " State: " + linkState);
        int result = dbI.removeDiscoveredLink(foundID);
        if (result == WORKED_OK)
        {
          ltNewLinks.decrementStats(linkState);
          if (containsChartWindow("new"))
          {
            LinkChart lc = getChartWindow("new");
            lc.updateData(ltNewLinks.getInitialFigures());
          }
          ltNewLinks.removeRow(i);
          return (Integer) objTemp[2];
        }
        else
        {
          log.warning("Problem encountered while trying to remove the record. ID: " + discoveredLinkId);
          return -1; // We use this to indicate a fault
        }
      }
    }
    log.warning("Tried to remove a link from the database without finding the record. ID: " + discoveredLinkId);
    return -1; // We use this to indicate a fault
  }

  /**
   * Adds a new recognised PSE to the GUI. It forces the link table and
   * report tree to be refreshed with the new link but does not save the data in
   * the database
   * @param pseName The name of the PSE
   * @param linkID The link ID
   * @param state The current state of the link
   */
  public void addRecognisedLink(String pseName, String linkID, Integer state)
  {
    LinkTable ltOldLinks = (LinkTable) linkTable.getModel();
    ltOldLinks.addLink(pseName, linkID, state);
    if (containsChartWindow("old"))
    {
      LinkChart lc = getChartWindow("old");
      lc.updateData(ltOldLinks.getInitialFigures());
    }
    TreePath tpParent = reportTree.getNextMatch(pseName, 0, Position.Bias.Forward);
    DefaultTreeModel dtmNodes = (DefaultTreeModel) reportTree.getModel();
    if (tpParent == null)
    {
      // The PSE node isn't part of the report tree so we must add it
      TreePath tpPseNode = reportTree.getNextMatch("By PSE Link", 0, Position.Bias.Forward);
      if (tpPseNode == null)
      {
        // We have some serious problems as the main PSE node can't be found
        log.severe("Cannot locate the main PSE node to add the new link and PSE to. Exiting");
        return;
      }
      else
      {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tpPseNode.getLastPathComponent();
        DefaultMutableTreeNode pse = new DefaultMutableTreeNode(pseName);
        DefaultMutableTreeNode link = new DefaultMutableTreeNode (linkID);
        pse.add(link);
        node.add(pse);
        dtmNodes.nodeStructureChanged(node);
      }
    }
    else
    {
      // The PSE has been found, we can assume that the link isn't already added
      // so we'll add it under the PSE node.
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) tpParent.getLastPathComponent();
      DefaultMutableTreeNode link = new DefaultMutableTreeNode (linkID);
      node.add(link);
      dtmNodes.nodeStructureChanged(node);
    }
    reportTree.repaint();
  }
}