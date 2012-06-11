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

/* Class name: COMConfig_Save
 * File name:  COMConfig_Save.java
 * Project:    JavaNews - PSE Monitoring
 * Copyright:  © 2007-2009 Alexander J. Harris, all rights reserved
 * Created:    19-Jan-2009 10:25:00
 * Modified:   17-Jun-2009
 *
 * Version History:
 * ~ ~ ~ ~ ~ ~ ~ ~ ~
 * 1.000  17-Jun-2009 Code finalised and released.
 * 0.001  19-Jan-2009 Initial build
 */

package javanews.events.gui;
import javanews.gui.Client;
import javanews.gui.internalframe.*;
import javanews.object.*;
import javanews.object.table.*;
import java.awt.event.*;
import java.util.logging.*;
import java.util.prefs.*;

/**
 * This class is responsible for writing data to the preferences file and
 * initiating new threads to monitor COM ports based on user selections in the
 * COM Port Configuration window.
 * @version 1.000
 * @author Alex Harris (email: Alexander.J.Harris(at)btinternet.com)
 */

public class COMConfig_Save implements ActionListener
{
  /** The logger to be used for writing messages to the application log */
  private Logger log;
  /** The table containing the configuration of discovered COM ports */
  private GenericTable gt;
  /** The window to be read from */
  private COMConfig comCfg;
  /** The unique name of the logger within the application */
  private static final String className = "javanews.events.gui.COMConfig_Save";

  /**
   * Initialises the class and internal logger. It requires both the instance of
   * GenericTable and COMConfig to be passed so that the actionPerformed event can
   * execute correctly.
   * @param listOfCOMPorts The table displayed within the COMConfig window
   * @param parent The COMConfig window itself
   * @see javanews.object.table.GenericTable
   * @see javanews.gui.internalframe.COMConfig
   */
  public COMConfig_Save(GenericTable listOfCOMPorts, COMConfig parent)
  {
    log = LoggerFactory.getLogger(className);
    log.finest("Class initialised");
    gt = listOfCOMPorts;
    comCfg = parent;
  }

  /**
   * The user selection of the JMenuItem within the menu of the parent window.
   * @param ae The generated instance of <code>ActionEvent</code>.
   */
  public void actionPerformed(ActionEvent ae)
  {
    log.config("Saving configuration details");
    int y = gt.getRowCount();
    int numberOfPortsSaved = 0;
    Preferences prefs = JNPreferences.getJNPrefs();
    /**************************************************************************/
    /*   WIPE THE PREVIOUSLY STORED VALUES                                    */
    /**************************************************************************/
    int currentlyStored = (new Integer(prefs.get("javanews.comPort.numberToMonitor", "0"))).intValue();
    for (int x = 0; x < currentlyStored; x++)
    {
      log.finest("Removing configuration of COM port: " + prefs.get("javanews.comPort." + x + ".name", "Unknown"));
      prefs.remove("javanews.comPort." + x + ".name");
      prefs.remove("javanews.comPort." + x + ".baud");
      prefs.remove("javanews.comPort." + x + ".data");
      prefs.remove("javanews.comPort." + x + ".parity");
      prefs.remove("javanews.comPort." + x + ".stop");
    }
    /**************************************************************************/
    /*   STORE THE NEW VALUES FROM THE GUI                                    */
    /**************************************************************************/
    COMPortThreadManager cptm = COMPortThreadManager.getInstance();
    for (int x = 0; x < y; x++)
    {
      Object[] objRow = (Object[]) gt.getRow(x);
      Boolean bolMonitor = (Boolean) objRow[0];
      String name = (String) objRow[1];
      if (bolMonitor.booleanValue())
      {
        String type = (String) objRow[2];
        int baud = ((Integer) objRow[4]).intValue();
        int data = ((Integer) objRow[5]).intValue();
        String parity = (String) objRow[6];
        float stop = ((Float) objRow[7]).floatValue();
        if (type.equals("Serial") || type.equals("Test"))
        {
          log.finest("Saving configuration of: " + name);
          prefs.put("javanews.comPort." + numberOfPortsSaved + ".name", name);
          prefs.putInt("javanews.comPort." + numberOfPortsSaved + ".baud", baud);
          prefs.putInt("javanews.comPort." + numberOfPortsSaved + ".data", data);
          prefs.put("javanews.comPort." + numberOfPortsSaved + ".parity", parity);
          prefs.putFloat("javanews.comPort." + numberOfPortsSaved + ".stop", stop);
          numberOfPortsSaved++;
          log.config("Creating new monitoring thread");
          cptm.addNewWindow(name, baud, data, parity, stop);
        }
        else
        {
          javax.swing.JOptionPane.showMessageDialog(Client.getJNWindow().getDesktopPane(),"Cannot configure port types other than Serial");
        }
      }
      else
      {
        log.finest("Checking to see if a thread for " + name + " exists.");
        if (cptm.doesPortExist(name))
        {
          log.config("Removing thread for: " + name);
          cptm.stopThread(name);
        }
        else
        {
          log.finest("Thread not found. Continuing execution.");
        }
      }
    }
    prefs.putInt("javanews.comPort.numberToMonitor", numberOfPortsSaved);
    log.finest("Saving preferences to file system");
    JNPreferences.updateJNPrefs();
    comCfg.setVisible(false);
  }
}