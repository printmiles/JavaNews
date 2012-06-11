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

/* Class name: GenericFrame
 * File name:  GenericFrame.java
 * Project:    JavaNews - PSE Monitoring
 * Copyright:  © 2007-2009 Alexander J. Harris, all rights reserved
 * Created:    18-Mar-2009 19:13:20
 * Modified:   17-Jun-2009
 *
 * Version History:
 * ~ ~ ~ ~ ~ ~ ~ ~ ~
 * 1.000  17-Jun-2009 Code finalised and released.
 * 0.003  11-Jun-2009 Fixed problem with exporting data due to a null variable.
 * 0.002  27-Mar-2009 Added Save As CSV button under the Export Data jMenu.
 * 0.001  18-Mar-2009 Initial build
 */

package javanews.gui.internalframe;
import javanews.events.gui.*;
import javanews.gui.*;
import javanews.object.*;
import javanews.object.table.*;
import java.awt.event.*;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 * Used for displaying the results of SQL queries from the database. It provides
 * a simple means for displaying the results of the query in a table that can be
 * sorted by the user, by clicking on the column headers. It also provides a
 * means for the data to be exported to aother program for further analysis or
 * presentation through the open CSV (comma seperated value) format.
 * @version 1.000
 * @author Alex Harris (email: Alexander.J.Harris(at)btinternet.com)
 */

public class GenericFrame extends JInternalFrame
{
  /** The logger to be used for writing messages to the application log */
  private Logger log;
  /** The unique name of the logger within the application */
  private static final String className = "javanews.gui.internalframe.GenericFrame";
  /** The table holding the data to be displayed */
  private GenericTable table;

  /**
   * Initialises the class and internal logger as well as displaying the data
   * contained within the supplied instance of <code>GenericTable</code>.
   * <p>The instance of <code>GenericTable</code> passed as an argument will
   * have already had information about its structure defined through its own
   * initialisation. This allows for the this class to simply wrap it in an
   * instance of <code>JScrollPane</code> and present it to the user with a
   * basic menu allowing for the data contents to be exported.
   * <p>The class is primarily used to show the results from the tree of PSEs,
   * links, dates and times shown on the left of the monitoring screen.
   * @param title The title of the window.
   * @param gT The data to be displayed in the window.
   * @see javanews.object.table.GenericTable
   */
  public GenericFrame(String title, GenericTable gT)
  {
    super(title, true, true, true, true);
    super.setLayer(1);
    // Obtain an instance of Logger for the class
    log = LoggerFactory.getLogger(className);
    JTable jT = new JTable(gT);
    jT.setRowSorter(new TableRowSorter(gT));
    JScrollPane jspTable = new JScrollPane(jT);
    this.add(jspTable);
    table = gT;
    SpringLayout slGenericFrame = new SpringLayout();
    this.setLayout(slGenericFrame);
    slGenericFrame.putConstraint(SpringLayout.NORTH, jspTable, 2, SpringLayout.NORTH, this.getContentPane());
    slGenericFrame.putConstraint(SpringLayout.EAST, jspTable, -2, SpringLayout.EAST, this.getContentPane());
    slGenericFrame.putConstraint(SpringLayout.SOUTH, jspTable, -2, SpringLayout.SOUTH, this.getContentPane());
    slGenericFrame.putConstraint(SpringLayout.WEST, jspTable, 2, SpringLayout.WEST, this.getContentPane());
    JMenuBar jmb = new JMenuBar();
    JMenu jmExport = new JMenu("Export Data");
    JMenuItem jmiCSV = new JMenuItem("Save As CSV");
    jmiCSV.setMnemonic(KeyEvent.VK_C);
    jmiCSV.addActionListener(new GenericFrame_Export(this));
    jmExport.add(jmiCSV);
    jmb.add(jmExport);
    this.setJMenuBar(jmb);
    Client.getJNWindow().addToDesktop(this);
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    this.setSize(500,350);
    this.setVisible(true);
    log.finest("Displaying new generic frame for: " + title);
  }

  /**
   * Returns the instance of <code>GenericTable</code> used to contain data
   * displayed within the window.
   * @return The data model displayed in the window.
   * @see javanews.object.table.GenericTable
   */
  public GenericTable getGenericTable()
  {
    return table;
  }
}