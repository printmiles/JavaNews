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

/* Class name: COMPortThreadManager
 * File name:  COMPortThreadManager.java
 * Project:    JavaNews - PSE Monitoring
 * Copyright:  © 2007-2009 Alexander J. Harris, all rights reserved
 * Created:    13-Mar-2009 16:01:18
 * Modified:   17-Jun-2009
 *
 * Version History:
 * ~ ~ ~ ~ ~ ~ ~ ~ ~
 * 1.000  17-Jun-2009 Code finalised and released.
 * 0.004  18-Mar-2009 Removed code that stops and restarts code execution.
 * 0.003  17-Mar-2009 Fixed threading code to alter variables within the PortReader class
 *        rather than editing the thread itself.
 * 0.002  15-Mar-2009 Added threading code to start and stop methods and getStatusTable.
 * 0.001  13-Mar-2009 Initial build
 */

package javanews.object;
import javanews.gui.internalframe.*;
import javanews.object.table.*;
import java.util.*;
import java.util.logging.*;

/**
 * Used to contain threads collecting data from one or more COM ports. It maintains
 * two hashtables that should both contain identical keys and the same number of
 * entries. This is used to manage both the data collection thread (instances of
 * <code>PortReader</code>) and the display of that data to and interaction with
 * the user (instances of <code>COMPortClient</code>).
 * @version 1.000
 * @author Alex Harris (email: Alexander.J.Harris(at)btinternet.com)
 * @see javanews.object.PortReader
 * @see javanews.gui.internalframe.COMPortClient
 */

public class COMPortThreadManager
{
  /** The unique name of the logger within the application */
  private static final String className = "javanews.object.COMPortThreadManager";
  /** The unique name of the logger within the application */
  private Logger log;
  /** The store of executing threads against their identifying COM port names */
  private Hashtable<String, PortReader> htPortManager;
  /** The store of displayed windows against their identifying COM port names */
  private Hashtable<String, COMPortClient> htCOMPortClientManager;
  /** The instance of the class that has been created and should be returned for any subsequent calls */
  private static COMPortThreadManager cptmPorts;

  /**
   * Initialises the class, internal logger and hashtables used to store data entries.
   */
  public COMPortThreadManager()
  {
    log = LoggerFactory.getLogger(className);
    htPortManager = new Hashtable<String, PortReader>();
    htCOMPortClientManager = new Hashtable<String, COMPortClient>();
    log.finest("Class initialised");
    cptmPorts = this;
  }

  /**
   * Used to return the instance of the class used by the main application.
   * <p>After the class has been created and is running in the background through
   * a high-level GUI class (such as <code>Client</code>) then further calls
   * to this class should be made through this method to obtain the original
   * instance rather than creating fresh instances of it.
   * @return The instance being used within the application
   */
  public static COMPortThreadManager getInstance()
  {
    return cptmPorts;
  }

  /**
   * Creates both a new thread for data collection (<code>PortReader</code>)
   * with the supplied parameters and a new data display window
   * (<code>COMPortClient</code>).
   * @param portName The identifying name of the COM port to monitor
   * @param baudRate The COM port's required baud rate
   * @param dataBits The length of the data in bits
   * @param parityType The type of parity to be used for error correction
   * @param stopBits The size of the stop bits
   */
  public void addNewWindow(String portName, int baudRate, int dataBits, String parityType, float stopBits)
  {
    log.config("Creating new window to monitor port: " + portName + " with the following settings: " +
      baudRate + "," + dataBits + "," + parityType + "," + stopBits);
    COMPortClient cpc = new COMPortClient(portName, baudRate, dataBits, parityType, stopBits);
    htCOMPortClientManager.put(portName,cpc);
    log.fine("New COMPortClient window created and added to the hashtable for: " + portName);
    PortReader pr = new PortReader(portName, baudRate, dataBits, parityType, stopBits, cpc);
    htPortManager.put(portName, pr);
    log.fine("PortReader thread for " + portName + " started.");
    new Thread(pr).start();
  }

  /**
   * Returns the name instance of <code>COMPortClient</code> from the hashtable.
   * @param identifier The unique name of the required instance
   * @return The instance of <code>COMPortClient</code> with the provided name
   * @see javanews.gui.internalframe.COMPortClient
   */
  public COMPortClient getCOMPortClient(String identifier)
  {
    return htCOMPortClientManager.get(identifier);
  }

  /**
   * Terminates all executing threads collecting data through the COM port and
   * any associated windows within the GUI. All entries are cleared from the
   * hashtables as well.
   */
  public void haltThreads()
  {
    Enumeration eKeys = htCOMPortClientManager.keys();
    while (eKeys.hasMoreElements())
    {
      String identifier = (String) eKeys.nextElement();
      log.config("Closing thread for " + identifier);
      COMPortClient cpcWindow = htCOMPortClientManager.get(identifier);
      cpcWindow.setVisible(false);
      cpcWindow = null;
      htCOMPortClientManager.remove(identifier);
      htPortManager.remove(identifier);
    }
  }

  /**
   * Returns an instance of <code>GenericTable</code> containing a list of
   * executing threads and their current status.
   * @return An instance of <code>GenericTable</code>
   * @see javanews.object.table.GenericTable
   */
  public GenericTable getStatusTable()
  {
    GenericTable gT = new GenericTable();
    Vector vecCols = new Vector();
    Vector vecRows = new Vector();
    vecCols.add("COM Port");
    vecCols.add("Client Window");
    vecCols.add("Thread Status");
    
    Enumeration eKeys = htCOMPortClientManager.keys();
    while (eKeys.hasMoreElements())
    {
      Object[] objRow = new Object[3];
      String portName = (String) eKeys.nextElement();
      COMPortClient cpcWindow = htCOMPortClientManager.get(portName);
      String comPortShown = "Not Visible";
      String threadState = "Not running";
      if (cpcWindow.isVisible())
      {
        comPortShown = "Visible";
        threadState = "Running";
      }
      PortReader pr = htPortManager.get(portName);
      
      objRow[0] = portName;
      objRow[1] = comPortShown;
      objRow[2] = threadState;
      vecRows.add(objRow);
    }
    gT.setData(vecCols, vecRows);
    return gT;
  }

  /**
   * Shows whether or not a port is already configured to run within the hashtables.
   * @param identifier The identifying name of the COM port
   * @return Whether the COM port is recognised or not
   */
  public boolean doesPortExist(String identifier)
  {
    return htCOMPortClientManager.containsKey(identifier);
  }

  /**
   * Stops the execution of a named thread. It also closes its associated window
   * and removes its entries from the hashtables.
   * @param identifier The identifying name of the COM port
   */
  public void stopThread(String identifier)
  {
    COMPortClient cpc = htCOMPortClientManager.remove(identifier);
    if (cpc != null)
    {
      PortReader pr = htPortManager.remove(identifier);
      pr.setShouldRun(false);
      pr = null;
      cpc.setVisible(false);
      cpc = null;
      System.gc();
      log.finest("Window closed for: " + identifier);
      return;
    }
    else
    {
      log.finest("Tried shutting a COM Port Client window which doesn't exist.");
      return;
    }
  }
}