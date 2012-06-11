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

/* Class name: RunJavaNews
 * File name:  RunJavaNews.java
 * Project:    JavaNews - PSE Monitoring
 * Copyright:  © 2007-2009 Alexander J. Harris, all rights reserved
 * Created:    13-Oct-2008 13:41:52
 * Modified:   17-Jun-2009
 *
 * Version History:
 * ~ ~ ~ ~ ~ ~ ~ ~ ~
 * 1.000  17-Jun-2009 Code finalised and released.
 * 0.003  13-Jan-2009 Changed SplashScreen initialisation variable from 7 to 6 to hide the splash screen
 * 0.002  17-Nov-2008 Added main code
 * 0.001  13-Oct-2008 Initial build
 */

package javanews.run;
import javanews.gui.*;
import javanews.object.*;
import java.util.prefs.*;
import java.util.logging.Logger;

/**
 * Used to create the application environment and launch the main GUI. The system will exit with one of the
 * following codes:
 * <ol>
 *   <li>User requested application to shut down (normal shut-down).</li>
 *   <li>Application could not find the database DSN name to connect to (abnormal shut-down).</li>
 *   <li>Application could not find the database type to connect to (abnormal shut-down).</li>
 * </ol>
 * @version 1.000
 * @author Alex Harris (email: Alexander.J.Harris(at)btinternet.com)
 */

public class RunJavaNews
{
  /** The unique name of the logger within the application */
  private static final String className = "javanews.run.RunJavaNews";
  /** The logger to be used for writing messages to the application log */
  private static Logger log;

  /**
   * Executes the main application.
   * <p>It requires no specific arguments and so doesn't support additional
   * arguments being passed to it.
   * @param args Any additional arguments - <em>Not used</em>
   */
  public static void main (String[] args)
  {
    // Create the splash screen
    SplashScreen ssSplash = new SplashScreen(6);
    ssSplash.increaseProgress("Loading Loggers...");
    log = LoggerFactory.getLogger(className);
    ssSplash.increaseProgress("Reading new settings...");
    checkPreferences();
    log.finest("Starting application");
    log.entering("RunJavaNews","main()");
    ssSplash.increaseProgress("Loading GUI...");
    Client javanewsClient = new Client(ssSplash);
  }

  /**
   * Checks for any preferences stored within the preferences file that were
   * updated during the last run of the application. If new values are found
   * the key is changed from <code>javanews.new.x</code> to <code>javanews.x</code>
   * so that they are used in the current application run. The previous values
   * are overwritten and the temporary keys removed.
   */
  private static void checkPreferences()
  {
    // Check the preferences file for changes to the log directory during the last run-time.
    try
    {
      Preferences prefLogDir = JNPreferences.getJNPrefs();
      // Obtain all of the entries stored in our preferences node
      String[] nodeKeys = prefLogDir.keys();
      // Iterate through them to find node named javanews.new.xxx and rename them as javanews.xxx
      for (String name : nodeKeys)
      {
        if (name.startsWith("javanews.new."))
        {
          String altName = "javanews" + name.substring(12);
          // Move the contents of the named node (javanews.new.xxx) with the node it should replace
          log.config("Moving the contents of " + name + " node to " + altName);
          prefLogDir.put(altName, prefLogDir.get(name, ""));
          // Remove the newLog node
          log.finer("Removing preferences node " + name);
          prefLogDir.remove(name);
        }
      }
      // Store the changes to the preferences file
      JNPreferences.updateJNPrefs();
    }
    catch (BackingStoreException bsX)
    {
      log.severe("There was a problem (BackingStoreException) trying to access the preferences file.");
      log.throwing("RunJavaNews", "checkPreferences()", bsX);
      System.err.println("Problem trying to access the preferences XML file. See the log for more information.");
    }
  }
}