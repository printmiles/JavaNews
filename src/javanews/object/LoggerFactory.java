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

/* Class name: LoggerFactory
 * File name:  LoggerFactory.java
 * Project:    JavaNews - PSE Monitoring
 * Copyright:  © 2007-2009 Alexander J. Harris, all rights reserved
 * Created:    17-Nov-2008 15:14:47
 * Modified:   17-Jun-2009
 *
 * Version History:
 * ~ ~ ~ ~ ~ ~ ~ ~ ~
 * 1.000  17-Jun-2009 Code finalised and released.
 * 0.004  19-Feb-2009 Moved code to search for new settings to RunJavaNews
 * 0.003  21-Jan-2009 Changed the getLogDir method to look for any changes to the log directory
 * 0.002  19-Jan-2009 Removed some of the verbose output to the log.
 *                    Changed the Hashtable to use generic types.
 * 0.001  17-Nov-2008 Initial build. Code adapted from MARS, another of my projects.
 */

package javanews.object;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.logging.*;
import java.util.prefs.*;

/**
 * This class is used to control instances of <code>java.util.logging.Logger</code> used within the application.
 * An instance of <code>Logger</code> can be obtained by passing the class name of the source to the factory
 * via the <code>getLogger(String)</code> method. An instance of <code>java.util.logging.Logger</code> is
 * returned to the receiver that has either been newly instantiated or retrieved from a hashtable depending on
 * whether an instance already exists.
 * <p>The files are saved using the XML format defined by Sun for log records and are stored
 * relative to the directory the code is executed from, in the Logs directory.
 * <p>Log records are named in the format of <code>classname-u.log.xml</code> where <code>parentClassName</code> is 
 * the supplied class name, which should be the fully qualified package and class name and <code>u</code> is a unique number
 * <p>The objective of this class is to reduce the overall number of logs kept within the system and to provide
 * a central point for amending settings such as log levels.
 * @version 1.000
 * @author Alex Harris (email: Alexander.J.Harris(at)btinternet.com)
 * @see java.util.logging.Logger
 */
public class LoggerFactory
{
  /** The central store of all loggers created throughout the application */
  private static Hashtable<String, Logger> htJNLogs;
  /** The logger to be used for writing messages to the application log */
  private static Logger logSelf;
  /** The unique name of the logger within the application */
  private static final String className = "javanews.object.LoggerFactory";
  
  /**
   * Used to return an existing instance of Logger if one exists for the supplied class name.
   * Or to create a new instance, configure the FileHandler, set the file location and logging level.
   * @see java.util.logging.Logger
   * @param parentClassName The fully qualified class name identifying the Logger
   * @return The instance of Logger to be used
   */
  public static Logger getLogger(String parentClassName)
  {
    // Check to make sure that all variables are initialised, if not then initialise them
    if (htJNLogs == null)
    {
      htJNLogs = new Hashtable<String, Logger>();
    }
    // Get the Preferences for the application so that we can read the preferred logging level
    Preferences prefLogLevel = JNPreferences.getJNPrefsQuietly();
    if (logSelf == null)
    {
      logSelf = Logger.getLogger(className);
      htJNLogs.put(className, logSelf);
      try
      {
        FileHandler fhXMLLog = new FileHandler(getLogDir() + className + "-%u.log.xml");
        logSelf.addHandler(fhXMLLog);
        // Get the log level from the preferences. If it can't be obtained then default to All
        logSelf.setLevel(Level.parse((prefLogLevel.get("javanews.logLevel", "All")).toUpperCase()));
        logSelf.finest("Created new logger for the LoggerFactory class");
        logSelf.config("Started new log");
      }
      catch (IOException ioX)
      {
        logSelf.throwing("LoggerFactory", "getLogger(String)", ioX);
        System.err.println("Unable to create logger for class: " + parentClassName);
      }
    }
    
    // Does the hashtable already contain a logger for the given classname?
    if(htJNLogs.containsKey(parentClassName))
    {
      logSelf.finest("Found logger for " + parentClassName + " and returning it to the receiver.");
      return htJNLogs.get(parentClassName);
    }
    // If it doesn't then set one up using the stored preferences and
    // run-time properties (to obtain directory information).
    else
    {
      Logger logNewClass = Logger.getLogger(parentClassName);
      if (logSelf == null)
      {
        logSelf = logNewClass;
      }
      logSelf.finest("Could not find a logger for " + parentClassName + ", creating a new one.");
      try
      {
        FileHandler fhXMLLog = new FileHandler(getLogDir() + parentClassName + "-%u.log.xml");
        logNewClass.addHandler(fhXMLLog);
        // Get the log level from the preferences. If it can't be obtained then default to All
        logNewClass.setLevel(Level.parse((prefLogLevel.get("javanews.logLevel", "All")).toUpperCase()));
        logNewClass.config("Started new log");
        logSelf.finest("Created new logger for class " + parentClassName + " at location: " + getLogDir() + parentClassName + ".log.xml");
      }
      catch (IOException ioX)
      {
        logSelf.severe("Unable to create logger for class: " + parentClassName);
        logSelf.throwing("LoggerFactory", "getLogger(String)", ioX);
        System.err.println("Unable to create logger for class: " + parentClassName + ". See the log for more information.");
      }

      logSelf.finest("Adding new logger to the hashtable");
      htJNLogs.put(parentClassName, logNewClass);
      return logNewClass;
    }
  }
  
  /**
   * Examines the application preferences and returns the localised string of the current log file directory.
   * If one cannot be found then it examines the current location the application is being run in and creates
   * a Logs directory specifically for JavaNews.
   * <p>The returned value is localised to the OS so Windows would return C:\Logs and Unix C:/Logs
   * @return The current directory used for Log file storage.
   */
  private static String getLogDir()
  {
    String strClassDir, strUserDir;
    strClassDir = System.getProperty("java.class.path"); // Returns the current class path
    strUserDir = System.getProperty("user.dir"); // If the class path doesn't return the directory then this will
    if (strClassDir.indexOf(System.getProperty("file.separator")) > -1)
    {
      // See if the class directory contains the full class path (that the string contains a file seperator character)
      // If it does then manipulate the string so that it returns the file directory but without the class file name
      strUserDir = strClassDir.substring(0, (strClassDir.lastIndexOf(System.getProperty("file.separator"))) + 1);
      strUserDir = strUserDir + "Logs" + System.getProperty("file.separator");
    }
    else
    {
      // If the class path only provide the class file name then it is being run from the same directory
      strUserDir = strUserDir + System.getProperty("file.separator") + "Logs" + System.getProperty("file.separator");
    }
    // Get the Preferences for JavaNews
    Preferences prefLogDir = JNPreferences.getJNPrefsQuietly();
    // See if there's already an entry for the Log files to be recorded
    // If one exists then use that, if not then use the string we've just created
    strUserDir = prefLogDir.get("javanews.dir.Log", strUserDir);

    if (!(new File(strUserDir).exists()))  // Check to see whether the logs directory exists
    {
      logSelf.warning("Logs directory doesn't exist, trying to create the directory");
      // If it doesn't then try to create the directory
      boolean success = (new File(strUserDir)).mkdirs();
      if (!success)
      {
        // Could not create the directory so throw an exception
        logSelf.severe("Couldn't create a directory for the loggers, notifying System.err.");
        System.err.println("Unable to create directory: " + strUserDir);
      }
    }
    return strUserDir;
  }
  
  /**
   * Changes all of the Loggers stored within the classes hastable to the specified level
   * @param newLevel The new level of logging requested. This must match the string equivalent of the level itself.
   */
  public static void setLogLevel(String newLevel)
  {
    newLevel = newLevel.toUpperCase();
    Level lNew = Level.parse(newLevel.toUpperCase());
    Enumeration eHTKeys = htJNLogs.keys();
    while (eHTKeys.hasMoreElements())
    {
      String strLogName = eHTKeys.nextElement().toString();
      Logger logTemp = htJNLogs.get(strLogName);
      logTemp.setLevel(lNew);
      htJNLogs.put(strLogName, logTemp);
    }
  }
  
  /**
   * Close the logs gracefully and ensure that the logs has been closed correctly.
   */
  public static void closeLogs()
  {
    Enumeration eHTKeys = htJNLogs.keys();
    while (eHTKeys.hasMoreElements())
    {
      Logger aLog = htJNLogs.get(eHTKeys.nextElement().toString());
      // Write a quick note to the logger before closing it
      aLog.config("Application closing, shutting the logger down.");
      // Turn the logging off
      aLog.setLevel(Level.OFF);
      // Set the logger to null which closes the file
      aLog = null;
    }
  }
}