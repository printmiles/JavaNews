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

/* Class name: JNPreferences
 * File name:  JNPreferences.java
 * Project:    JavaNews - PSE Monitoring
 * Copyright:  © 2007-2009 Alexander J. Harris, all rights reserved
 * Created:    17-Nov-2008 15:19:54
 * Modified:   17-Jun-2009
 *
 * Version History:
 * ~ ~ ~ ~ ~ ~ ~ ~ ~
 * 1.000  17-Jun-2009 Code finalised and released.
 * 0.001  17-Nov-2008 Initial build
 */

package javanews.object;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.*;

/**
 * This class is used by others as the central means for access to the application preferences. These
 * are stored in a single XML file used by the whole application. Once instantiated, this class uses
 * instances of the <code>java.util.prefs.Preferences</code> class to retrieve and edit data within another
 * class. The other class then calls the static method <code>updateJNPrefs</code> to write values
 * into the XML file.
 * @version 1.000
 * @author Alex Harris (email: Alexander.J.Harris(at)btinternet.com)
 * @see java.util.prefs.Preferences
 */
public class JNPreferences
{
  /** The name of the applications preference file located within the main class directory */
  private static final String prefsFileName = "javanews.prefs.xml";
  /** The unique name of the logger within the application */
  private static final String className = "javanews.object.JNPreferences";
  /** The centralised instance of <code>Preferences</code> to be used for the whole application */
  private static Preferences prefsJN;
  
  /**
   * Used to return all of the Preferences used across the application.
   * Preferences can then be edited locally within the calling class.
   * @return A new instance of Preferences to be used locally
   */
  public static Preferences getJNPrefs()
  {
    // Obtain a logger to write progress to
    Logger log = LoggerFactory.getLogger(className);
    log.log(Level.FINEST, "Examining preferences to see if it's null or not.", prefsJN);
    if (prefsJN == null)
    {
      log.finest("Preferences object was null, creating a new one from the default file (javanews.prefs.xml).");
      try
      {
        // Obtain the FileInputStream for the central Preferences file
        FileInputStream fisXMLPrefs = new FileInputStream(prefsFileName);
        // Providing the file isn't null then import the preferences
        if (fisXMLPrefs != null)
        {
          prefsJN.importPreferences(fisXMLPrefs);
        }
      }
      catch (Exception x)
      {
        // If any errors are thrown then record them in the logger
        log.throwing("JNPreferences", "getJNPrefs()", x);
      }
    }
    // Obtain the Preferences for this class, this allows for everything to be centralised within the file
    prefsJN = Preferences.userNodeForPackage(JNPreferences.class);
    // Return the Preferences to the receiver.
    return prefsJN;
  }
  
  /**
   * This class provides exactly the same functions are the getJNPrefs method but doesn't record anything in the Log
   * <p>It should <b>only</b> be used by the LoggerFactory class to prevent a loop. All other classes should use the
   * getJNPrefs method to utilise the logger.
   * @return A new instance of Preferences to be used locally
   */
  public static Preferences getJNPrefsQuietly()
  {
    /* As mentioned in the JavaDoc comments, this is the same code but omitting the use of the log. It doesn't harm if
     * a class other than LoggerFactory uses it, we just lose the logs if something goes wrong.
     */
    if (prefsJN == null)
    {
      try
      {
        FileInputStream fisXMLPrefs = new FileInputStream(prefsFileName);
        if (fisXMLPrefs != null)
        {
          prefsJN.importPreferences(fisXMLPrefs);
        }
      }
      catch (Exception x)
      {
        // If any errors are thrown then put them in System.err rather than the Logger
        System.err.println("An error was caught while trying to obtain preferences for the logger.");
        x.printStackTrace();
      }
    }
    prefsJN = Preferences.userNodeForPackage(JNPreferences.class);
    return prefsJN;
  }
    
  /**
   * Used to update any changes within the preferences to the main XML file.
   */
  public static void updateJNPrefs()
  {
    // Obtain a logger to write progress to
    Logger log = LoggerFactory.getLogger(className);
    try
    {
      log.finest("Exporting preferences to file: " + prefsFileName);
      // Record all of the preferences in the javanews.prefs.xml file
      prefsJN.exportNode(new FileOutputStream(prefsFileName));
    }
    catch (Exception x)
    {
      // If an error was encountered during the update then write the details to the log
      log.throwing("JNPreferences", "updateJNPrefs()", x);
    }
  }
}