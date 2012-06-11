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

/* Class name: LinkChart
 * File name:  LinkChart.java
 * Project:    JavaNews - PSE Monitoring
 * Copyright:  © 2007-2009 Alexander J. Harris, all rights reserved
 * Created:    24-Mar-2009 17:06:37
 * Modified:   17-Jun-2009
 *
 * Version History:
 * ~ ~ ~ ~ ~ ~ ~ ~ ~
 * 1.000  17-Jun-2009 Code finalised and released.
 * 0.001  24-Mar-2009 Initial build
 */

package javanews.gui.internalframe;
import javanews.events.gui.*;
import javanews.gui.*;
import javanews.object.*;
import javanews.object.table.*;
import java.awt.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.*;
import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.category.*;
import org.jfree.data.general.*;

/**
 * Responsible for displaying information about the number of circuits and their
 * state as well as historical changes to availability of links. The window shows
 * two graphs, one showing the current number of links and their state as a pie
 * chart. The second is a line chart showing movements in availability and allowing
 * the user to hover the mouse over a point to show the date and time of activity.
 * @version 1.000
 * @author Alex Harris (email: Alexander.J.Harris(at)btinternet.com)
 */

public class LinkChart extends JInternalFrame
{
  /** The logger to be used for writing messages to the application log */
  private Logger log;
  /** The unique name of the logger within the application */
  private static final String className = "javanews.gui.internalframe.LinkFrame";
  /** The owning window containing source data */
  private COMPortClient owner;
  /** The identifying task for this instance of the class */
  private String identifier;
  /** The dataset for the pie chart showing current data */
  private DefaultPieDataset dpdCurrentData;
  /** The dataset for the line chart showing historical data */
  private DefaultCategoryDataset dcdPreviousData;

  /**
   * Initialises the class and internal logger. Uses the supplied arguments to
   * receive data from the application and add data to the charts dynamically.
   * @param title The title of the charts on display. Whether the displayed
   *               data is for <code>new</code> or <code>old</code> links.
   *               That is whether the data is for newly discovered links or
   *               existing (old) links already stored within the database.
   * @param parent The instance of <code>COMPortClient</code> that acts as
   *                the data source for the charts.
   */
  public LinkChart(String title, COMPortClient parent)
  {
    super("Charts", true, true, true, true);
    super.setLayer(1);
    identifier = title.toLowerCase();
    // Obtain an instance of Logger for the class
    log = LoggerFactory.getLogger(className);
    owner = parent;
    // Setup a hashtable to hold the values for up, down and unknown link states
    Hashtable<String, Integer> linkStats = new Hashtable<String, Integer>();
    if (identifier.equals("old"))
    {
      this.setTitle("Recognised Link Status on " + owner.getPortName() + ":");
      // Get the current figures from the link table
      linkStats = ((LinkTable) owner.getLinkTable().getModel()).getInitialFigures();
    }
    else if (identifier.equals("new"))
    {
      this.setTitle("Discovered Link Status on " + owner.getPortName() + ":");
      linkStats = ((LinkTable) owner.getNewLinkTable().getModel()).getInitialFigures();
    }
    else
    {
      // If the identifier was set to something other than old or new then it's not right.
      log.warning("An instance of LinkChart has been created for an unknown purpose.");
      return;
    }
    // Initialise the dataset for the pie chart
    dpdCurrentData = new DefaultPieDataset();
    dpdCurrentData.insertValue(0, "Link Down", linkStats.get("down"));
    dpdCurrentData.insertValue(1, "Link Up", linkStats.get("up"));
    dpdCurrentData.insertValue(2, "Link State Unknown", linkStats.get("unknown"));
    // Initialise the dataset for the line chart
    dcdPreviousData = new DefaultCategoryDataset();
    dcdPreviousData.addValue(linkStats.get("down"),"Link Down",Calendar.getInstance().getTime().toString());
    dcdPreviousData.addValue(linkStats.get("up"),"Link Up",Calendar.getInstance().getTime().toString());
    dcdPreviousData.addValue(linkStats.get("unknown"),"Link State Unknown",Calendar.getInstance().getTime().toString());
    // Set the variables we need for holding the charts
    JFreeChart jfcCurrentStatus; // This will be displayed as a pie chart
    JFreeChart jfcPreviousStatus; // This will be displayed as a line chart
    ChartPanel cpCurrent; // Chartpanels hold the JFreeChart
    ChartPanel cpPrevious;
    // Use the factory to create the charts
    jfcCurrentStatus = ChartFactory.createPieChart("Current Status", dpdCurrentData, true, true, false);
    jfcPreviousStatus = ChartFactory.createLineChart("Previous Status", "Time received", "Number of Links", dcdPreviousData, PlotOrientation.VERTICAL, true, true, false);
    // Add them to the chart panels
    cpCurrent = new ChartPanel(jfcCurrentStatus);
    cpPrevious = new ChartPanel(jfcPreviousStatus);
    // Add the chart panels to the content pane
    this.add(cpCurrent, BorderLayout.EAST);
    this.add(cpPrevious, BorderLayout.WEST);
    // Change the layout to show them next to each other
    this.setLayout(new GridLayout(1,2));
    // Add a listener to the window
    this.addInternalFrameListener(new CloseLinkChart(this));
    log.finest("Adding frame to the desktop");
    // Set the window properties and display it
    Client.getJNWindow().addToDesktop(this);
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    this.setSize(650,400);
    this.setVisible(true);
    owner.addChartWindow(title, this);
  }

  /**
   * Returns the identifier of the current instance of LinkChart. This indicates
   * the purpose of the window.
   * @return The purpose of the window
   */
  public String getIdentifier()
  {
    return identifier;
  }

  /**
   * Returns the owning instance of COMPortClient that this belongs to.
   * @return The owner of the current window
   */
  public COMPortClient getOwner()
  {
    return owner;
  }

  /**
   * Updates the data set for both charts with the contents of the supplied
   * Hashtable. The Hashtable is expected to contain the following items:
   * <ul>
   *   <li>down - The number of links currently in a down state</li>
   *   <li>up - The number of links currently in an up state</li>
   *   <li>unknown - The number of links currently in an unknown state</li>
   * </ul>
   * @param linkStats The hashtable containing the entries indicating current
   *                   link statistics.
   */
  public void updateData(Hashtable<String, Integer> linkStats)
  {
    dpdCurrentData.insertValue(0, "Link Down", linkStats.get("down"));
    dpdCurrentData.insertValue(1, "Link Up", linkStats.get("up"));
    dpdCurrentData.insertValue(2, "Link State Unknown", linkStats.get("unknown"));
    dcdPreviousData.addValue(linkStats.get("down"),"Link Down",Calendar.getInstance().getTime().toString());
    dcdPreviousData.addValue(linkStats.get("up"),"Link Up",Calendar.getInstance().getTime().toString());
    dcdPreviousData.addValue(linkStats.get("unknown"),"Link State Unknown",Calendar.getInstance().getTime().toString());
  }
}