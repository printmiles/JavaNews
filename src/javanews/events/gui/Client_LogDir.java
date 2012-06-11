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

/* Class name: Client_LogDir
 * File name:  Client_LogDir.java
 * Project:    JavaNews - PSE Monitoring
 * Copyright:  © 2007-2009 Alexander J. Harris, all rights reserved
 * Created:    14-Jan-2009 15:31:27
 * Modified:   17-Jun-2009
 *
 * Version History:
 * ~ ~ ~ ~ ~ ~ ~ ~ ~
 * 1.000  17-Jun-2009 Code finalised and released.
 * 0.003  19-Feb-2009 Amended key to save new values to from javanews.dir.newLog to javanews.new.dir.Log
 * 0.002  21-Jan-2009 Corrected code to manually
 * 0.001  14-Jan-2009 Initial build
 */

package javanews.events.gui;
import java.awt.event.*;
import java.io.File;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javanews.gui.Client;
import javanews.object.*;
import javax.swing.*;

/**
 * This class is responsible for handling events where the user wants to change
 * the directory used to store log files.
 * @version 1.000
 * @author Alex Harris (email: Alexander.J.Harris(at)btinternet.com)
 */

public class Client_LogDir implements ActionListener
{
  /** The unique name of the logger within the application */
  private static final String className = "javanews.events.gui.Client_LogDir";
  /** The parent window */
  private Client parent;
  /** The logger to be used for writing messages to the application log */
  private Logger log;

  /**
   * Initialises the class and attaches the current instance of Client so that
   * the dialogue can be displayed within the application window.
   * @param parentWindow The main instance of Client for the application
   */
  public Client_LogDir(Client parentWindow)
  {
    parent = parentWindow;
    log = LoggerFactory.getLogger(className);
    log.finest("Create log for the Client_LogDir class");
  }

  /**
   * The user selection of the JMenuItem within the menu of the parent window.
   * @param ae The generated instance of <code>ActionEvent</code>.
   */
  public void actionPerformed(ActionEvent ae)
  {
    log.finest("Setting log directory");
    JFileChooser jFC = new JFileChooser();
    // Get the current preferences file
    Preferences prefDir = JNPreferences.getJNPrefs();
    jFC.setDialogTitle("Select a Directory:");
    jFC.setFileSelectionMode(jFC.DIRECTORIES_ONLY);
    File fDir = new File(prefDir.get("javanews.dir.Log", System.getProperty("user.dir")));
    jFC.setSelectedFile(fDir);
    log.finest("Displaying FileChooser");
    int returnVal = jFC.showOpenDialog(parent);
    if (returnVal == 1)
    {
      // User cancelled the FileChooser dialog
      log.finest("User cancelled the file chooser");
      return;
    }
    fDir = jFC.getSelectedFile();
    prefDir.put("javanews.new.dir.Log", fDir.getPath() + System.getProperty("file.separator"));
    JNPreferences.updateJNPrefs();
    JOptionPane.showMessageDialog(Client.getJNWindow().getDesktopPane(), "The new log directory will be used the next time the application is started.","Log Directory Changed:",JOptionPane.INFORMATION_MESSAGE);
    log.config("Logging directory will be set to " + fDir.getAbsolutePath() + " the next time the application is restarted.");
    /* The code below this point is required to fix a problem. The problem is:
     * After the logging directory has changed and the code progresses to this point
     * (without exiting through the cancel button and the return statement above) halting
     * further execution creates problems when exiting the application. The error log and output
     * displays errors stemming from Threads in progress that are closed unexpectedly. These
     * Threads are from this class and the JFileChooser remaining open in the background despite
     * execution having already halted.
     * The code fixes this problem by removing the pointer to the JFileChooser and forcing the
     * Virtual Machine to perform a garbage collection and thus terminate the outstanding threads.
     */
    // Clean everything up
    jFC = null;
    prefDir = null;
    fDir = null;
    // Force a clean-up
    System.gc();
  }
}