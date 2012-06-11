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

/* Class name: Client_SetDatabase
 * File name:  Client_SetDatabase.java
 * Project:    JavaNews - PSE Monitoring
 * Copyright:  © 2007-2009 Alexander J. Harris, all rights reserved
 * Created:    17-Mar-2009 15:52:47
 * Modified:   17-Jun-2009
 *
 * Version History:
 * ~ ~ ~ ~ ~ ~ ~ ~ ~
 * 1.000  17-Jun-2009 Code finalised and released.
 * 0.001  17-Mar-2009 Initial build
 */

package javanews.events.gui;
import javanews.gui.Client;
import javanews.object.*;
import java.awt.event.*;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.*;

/**
 * This class is used to handle events where the user wants to change the data source
 * name of the back-end database for the application.
 * @version 1.000
 * @author Alex Harris (email: Alexander.J.Harris(at)btinternet.com)
 */

public class Client_SetDatabase implements ActionListener
{
  /** The unique name of the logger within the application */
  private static final String className = "javanews.events.gui.Client_SetDatabase";
  /** The logger to be used for writing messages to the application log */
  private Logger log;

  /**
   * Initialises the class and internal logger.
   */
  public Client_SetDatabase()
  {
    log = LoggerFactory.getLogger(className);
    log.finest("Create log.");
  }

  /**
   * The user selection of the JMenuItem within the menu of the parent window.
   * @param ae The generated instance of <code>ActionEvent</code>.
   */
  public void actionPerformed(ActionEvent ae)
  {
    Preferences prefDir = JNPreferences.getJNPrefs();
    String databaseName = (String) prefDir.get("javanews.dsn.name", "Unknown");
    log.finest("Displaying FileChooser");
    String newDBName = JOptionPane.showInputDialog(Client.getJNWindow().getDesktopPane(),
            "Please enter the name of the database to connect to.\nThis must be the same name as shown in the ODBC configuration.",
            databaseName);
    if (databaseName.equals(newDBName))
    {
      // User cancelled the FileChooser dialog
      log.finest("The user hasn't changed the DSN name.");
      return;
    }
    else
    {
      if (newDBName != null && !newDBName.equals(""))
      {
        prefDir.put("javanews.new.dsn.name", newDBName);
        JNPreferences.updateJNPrefs();
        JOptionPane.showMessageDialog(Client.getJNWindow().getDesktopPane(), "The new database will be used the next time the application is started.","Database Changed:",JOptionPane.INFORMATION_MESSAGE);
        log.config("Database will be set to " + newDBName + " the next time the application is restarted.");
      }
    }
  }
}