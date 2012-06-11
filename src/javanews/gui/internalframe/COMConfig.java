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

/* Class name: COMConfig
 * File name:  COMConfig.java
 * Project:    JavaNews - PSE Monitoring
 * Copyright:  © 2007-2009 Alexander J. Harris, all rights reserved
 * Created:    01-Jan-2009 14:47:07
 * Modified:   17-Jun-2009
 *
 * Version History:
 * ~ ~ ~ ~ ~ ~ ~ ~ ~
 * 1.000  17-Jun-2009 Code finalised and released.
 * 0.003  02-Feb-2009 Fixed problems in the class where previously saved configuration wasn't shown
 * 0.002  22-Jan-2009 Added Javadoc to the class
 * 0.001  01-Jan-2009 Initial build
 */

package javanews.gui.internalframe;
import javanews.gui.*;
import javanews.events.gui.*;
import javanews.object.*;
import javanews.object.table.*;
import gnu.io.CommPortIdentifier; // This is used to communicate with the COM ports through RxTx
import java.awt.*;
import java.util.*;
import java.util.logging.*;
import java.util.prefs.*;
import javax.swing.*;
import javax.swing.table.TableColumn;

/**
 * This class is used to display an instance of JInternalFrame within the JavaNews GUI.
 * The window will display a table containing a list of those COM ports discovered on the
 * machine along with a fake port (displayed as COM99) that can be used for testing.
 * <p>The user is able to interact with the table, allowing them to specify whether the
 * port should be monitored or not by clicking on a check box and configuring port settings
 * such as baud rate, length of data in bits, parity type and size of the stop bit.
 * <p>Once the user has finished customising the contents of the table they should then click
 * on the save button at the bottom of the window which uses the attached COMConfig_Save class
 * which can be found in the <code>javanews.events.gui package</code>.
 * @version 1.000
 * @author Alex Harris (email: Alexander.J.Harris(at)btinternet.com)
 * @see javanews.events.gui.COMConfig_Save
 */

public class COMConfig extends JInternalFrame
{
  /** The logger to be used for writing messages to the application log */
  private Logger log;
  /** The unique name of the logger within the application */
  private static final String className = "javanews.gui.internalframe.COMConfig";

  /**
   * Used to initialise the COM port configuration window.
   */
  public COMConfig()
  {
    super("COM Port Configuration", true, true);
    super.setLayer(1);
    // Obtain an instance of Logger for the class
    log = LoggerFactory.getLogger(className);
    // Get the preferred user Locale and create a new instance
    Preferences prefs = JNPreferences.getJNPrefs();
    // Build the table headers
    Vector vecColumns = new Vector();
    vecColumns.add("Monitor");
    vecColumns.add("Name");
    vecColumns.add("Type");
    vecColumns.add("In Use?");
    vecColumns.add("Baud (bps)");
    vecColumns.add("Data Bits");
    vecColumns.add("Parity");
    vecColumns.add("Stop Bits");
    Vector vecRows = new Vector();
    Hashtable<String, Object[]> htSavedPorts = new Hashtable();
    // Iterate through the current preferences file to see if there are already some settings saved.
    int currentlyStored = (new Integer(prefs.get("javanews.comPort.numberToMonitor", "0"))).intValue();
    for (int x = 0; x < currentlyStored; x++)
    {
      String name = prefs.get("javanews.comPort." + x + ".name", "Unknown");
      Integer iBaud = new Integer(prefs.getInt("javanews.comPort." + x + ".baud", 9600)); // Overwrite the baud value
      Integer iData = new Integer(prefs.getInt("javanews.comPort." + x + ".data", 8)); // Overwrite the data value
      String parity = prefs.get("javanews.comPort." + x + ".parity", "None"); // Overwrite the parity setting
      Float fStop = new Float(prefs.getFloat("javanews.comPort." + x + ".stop", 1)); // Overwrite the stop bit setting
      Object[] objTemp = new Object[4];
      objTemp[0] = iBaud;
      objTemp[1] = iData;
      objTemp[2] = parity;
      objTemp[3] = fStop;
      htSavedPorts.put(name, objTemp);
    }
    // Get the COM ports of the local machine
    Enumeration enumPorts = CommPortIdentifier.getPortIdentifiers();
    while (enumPorts.hasMoreElements())
    {
      // For each COM port we create a new Object[] array
      CommPortIdentifier comPort = (CommPortIdentifier) enumPorts.nextElement();
      Object[] objItems = new Object[8];
      // Set monitor to false
      objItems[0] = new Boolean(false);
      // Obtain the name of the COM port
      objItems[1] = comPort.getName();
      // Identify the type of port
      switch (comPort.getPortType())
      {
        case CommPortIdentifier.PORT_PARALLEL:
        {
          objItems[2] = "Parallel";
          break;
        }
        case CommPortIdentifier.PORT_SERIAL:
        {
          objItems[2] = "Serial";
          break;
        }
        default:
        {
          objItems[2] = "Other";
          break;
        }
      }
      objItems[3] = new Boolean(comPort.isCurrentlyOwned()); // Is the port used elsewhere?
      objItems[4] = new Integer(9600);  // Set the baud rate
      objItems[5] = new Integer(8); // Set the data bits
      objItems[6] = "None"; // Set the parity
      objItems[7] = new Float(1.0); // Set the stop bits
      if (htSavedPorts.containsKey((String) objItems[1]))
      {
        // We've already found the configuration for this port
        Object[] tempSettings = (Object[]) htSavedPorts.get((String) objItems[1]);
        objItems[0] = new Boolean(true);
        objItems[4] = tempSettings[0];
        objItems[5] = tempSettings[1];
        objItems[6] = tempSettings[2];
        objItems[7] = tempSettings[3];
      }
      vecRows.add(objItems);
    }
    // Add a row for testing (COM99)
    Object[] objItems = new Object[8];
    objItems[0] = new Boolean(false); // Monitor?
    objItems[1] = "COM99"; // Port name
    objItems[2] = "Test"; // Type
    objItems[3] = new Boolean(false); // Used elsewhere?
    objItems[4] = new Integer(9600); // Baud rate
    objItems[5] = new Integer(8); // Data bits
    objItems[6] = "None"; // Parity
    objItems[7] = new Float(1.0); // Stop bits
    // Check to see if the test port has been saved
    if (htSavedPorts.containsKey("COM99"))
    {
      // We've already found the configuration for this port
      Object[] tempSettings = (Object[]) htSavedPorts.get((String) objItems[1]);
      objItems[0] = new Boolean(true);
      objItems[4] = tempSettings[0];
      objItems[5] = tempSettings[1];
      objItems[6] = tempSettings[2];
      objItems[7] = tempSettings[3];
    }
    vecRows.add(objItems);
    // Create a GenericTable for displaying the vector contents
    GenericTable gtCOM = new GenericTable();
    gtCOM.setData(vecColumns, vecRows);
    JTable jtCOMConfig = new JTable(gtCOM);
    TableColumn tcBaud = jtCOMConfig.getColumnModel().getColumn(4);
    tcBaud.setCellEditor(new DefaultCellEditor(buildBaudBox()));
    TableColumn tcData = jtCOMConfig.getColumnModel().getColumn(5);
    tcData.setCellEditor(new DefaultCellEditor(buildDataBox()));
    TableColumn tcParity = jtCOMConfig.getColumnModel().getColumn(6);
    tcParity.setCellEditor(new DefaultCellEditor(buildParityBox()));
    TableColumn tcStop = jtCOMConfig.getColumnModel().getColumn(7);
    tcStop.setCellEditor(new DefaultCellEditor(buildStopBox()));
    gtCOM.setEditableColumns(new int[] {0,4,5,6,7});
    JScrollPane jsp = new JScrollPane(jtCOMConfig);
    JButton jbSave = new JButton("Save");
    jbSave.addActionListener(new COMConfig_Save(gtCOM, this));
    this.add(jsp, BorderLayout.CENTER);
    JPanel jpBottom = new JPanel();
    jpBottom.add(jbSave, BorderLayout.EAST);
    this.add(jpBottom, BorderLayout.SOUTH);
    log.finest("Adding frame to the desktop");
    Client parent = Client.getJNWindow();
    parent.addToDesktop(this);
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    this.setSize(400, 200);
    this.setVisible(true);
  }

  /**
   * This method sets the supported baud rate values for the table entry.
   * @return A combobox with the available baud rates within
   */
  private JComboBox buildBaudBox()
  {
    log.finer("Entered method");
    JComboBox baudBox = new JComboBox();
    baudBox.addItem("110");
    baudBox.addItem("300");
    baudBox.addItem("600");
    baudBox.addItem("1200");
    baudBox.addItem("2400");
    baudBox.addItem("4800");
    baudBox.addItem("9600");
    baudBox.addItem("14400");
    baudBox.addItem("19200");
    baudBox.addItem("38400");
    baudBox.addItem("57600");
    baudBox.addItem("115200");
    baudBox.setSelectedItem("9600");
    return baudBox;
  }

  /**
   * This method sets the supported data bit length values for the table entry.
   * @return A combobox with the available data bit values within
   */
  private JComboBox buildDataBox()
  {
    log.finer("Entered method");
    JComboBox dataBox = new JComboBox();
    dataBox.addItem("5");
    dataBox.addItem("6");
    dataBox.addItem("7");
    dataBox.addItem("8");
    dataBox.setSelectedItem("8");
    return dataBox;
  }

  /**
   * This method sets the supported parity types for the table entry.
   * @return A combobox with the available parity values within
   */
  private JComboBox buildParityBox()
  {
    log.finer("Entered method");
    JComboBox parityBox = new JComboBox();
    parityBox.addItem("Even");
    parityBox.addItem("Odd");
    parityBox.addItem("Mark");
    parityBox.addItem("None");
    parityBox.setSelectedItem("None");
    return parityBox;
  }

  /**
   * This method sets the supported stop bit length values for the table entry.
   * @return A combobox with the available stop bit values within
   */
  private JComboBox buildStopBox()
  {
    log.finer("Entered method");
    JComboBox stopBox = new JComboBox();
    stopBox.addItem("1");
    stopBox.addItem("1.5");
    stopBox.addItem("2");
    stopBox.setSelectedItem("1");
    return stopBox;
  }
}