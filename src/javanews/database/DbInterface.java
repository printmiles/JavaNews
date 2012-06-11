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

/* Class name: DbInterface
 * File name:  DbInterface.java
 * Project:    JavaNews - PSE Monitoring
 * Copyright:  © 2007-2009 Alexander J. Harris, all rights reserved
 * Created:    11-Jan-2009 15:12:48
 * Modified:   17-Jun-2009
 *
 * Version History:
 * ~ ~ ~ ~ ~ ~ ~ ~ ~
 * 1.000  17-Jun-2009 Code finalised and released.
 * 0.011  17-Jun-2009 Altered all methods to check whether the database connection
 *        has or is closing and stop them from executing further commands to the
 *        database.
 * 0.010  11-Jun-2009 Changed the way the connection is established to the database
 * 0.009  07-Jun-2009 Added support for comments to be saved in the database
 * 0.008  02-Jun-2009 Altered the storeMessage and lookupMessage methods to identify
 *        the recognised message ID and definition against the incoming message
 *        Also identified and fixed problems with ODBC connections not being closed
 * 0.007  28-May-2009 Amended the storeMessage to handle the messages UUID in place of
 *        of the Acknowledgement field. It also handles new links and links the received
 *        message to a recognised format and so provides a definition.
 * 0.006  19-Mar-2009 Added getMessagesByDate, getMessagesByLink and getMessagesByPSE methods to the class
 * 0.005  11-Mar-2009 Changed getCodexLinkID and lookupCodexLink to include COM port id when searching for matches
 * 0.004  26-Feb-2009 Changed storeMessage() to return an int value based on the execution outcome.
 *        Removed support for Apache Derby due to issues whilst testing.
 * 0.003  23-Feb-2009 Added the ability to work with ODBC and other database types (to be defined).
 * 0.002  30-Jan-2009 Added the ability to change the DSN name.
 *        The name is read from the preferences file, allowing for the user to
 *        change it during runtime. The change is effected on the next startup.
 * 0.001  11-Jan-2009 Initial build
 */

package javanews.database;
import java.sql.*;
import java.text.*;
import java.util.*;
import java.util.logging.*;
import java.util.prefs.*;
import javanews.object.*;
import javanews.object.table.*;

/**
 * This class is used to perform interactions between the application and the back-end database.
 * This class is the sole user of the SQLStatements class. The class establishes the link to the
 * database through a DSN defined by the user.
 * <p>It references two entries in the preferences file:
 * <ul><li><code>javanews.dsn.name</code> - Contains the name of the database to connect to.</li>
 * <li><code>javanews.db.type</code> - Contains the type of database being connected to.</li></ul>
 * @version 1.000
 * @author Alex Harris (email: Alexander.J.Harris(at)btinternet.com)
 */

public class DbInterface
{
  /** The name fo the JDBC:ODBC bridge driver class */
  private static final String jdbcodbcBridge = "sun.jdbc.odbc.JdbcOdbcDriver";
  /** The prefix for data source names using the JDBC:ODBC bridge */
  private static final String odbcPrefix = "jdbc:odbc:";
  /** The name of the data source being used */
  private String dsnName = "";
  /** The type of database being connected to */
  private String dbType = "";
  /** The unique name of the logger within the application */
  private static final String className = "javanews.database.DbInterface";
  /** The class used to retrieve SQL commands for Statements and PreparedStatements */
  private SQLStatements sqlS;
  /** The identifying name of the attached COM port */
  private String comPort;
  /** The Connection object used to attach to the database */
  private static Connection con;
  /** The logger to be used for writing messages to the application log */
  private static Logger log;
  /** A constant value used to represent the successful execution of a command or subroutine */
  private final int WORKED_OK = 1;
  /** A constant value used to represent a new link being discovered and added to the database */
  private final int DISCOVERED_NEW_LINK = 2;
  /** A constant value used to represent an issue where duplicates may have been found in a database index */
  private final int DISCOVERED_DUPLICATES = 3;
  /** A constant value used to represent the event that an error has been encountered during the execution of a method */
  private final int ERROR_ENCOUNTERED = 4;
  /** A constant value used to represent no entries or results being returned from a method or query */
  private final int NO_ENTRIES_FOUND = 5;
  /** A constant value used to represent a message having been received from a link that is classed as discovered */
  private final int DISCOVERED_LINK_MESSAGE = 6;
  /** The ID of a discovered link requested as part of a search */
  private String returnedNewLinkID;
  /** The message database containing recognised messages and their IDs from tblMessages within the back-end database */
  private static Hashtable<String,String> htMessageDb;
  /** Indicates whether the database connection is about to close */
  private static boolean isClosing = false;

  /**
   * Creates a new instance of DbInterface used to run queries to the database
   * @param comPort The identifier of the COM port used for this instance.
   */
  public DbInterface(String comPort)
  {
    log = LoggerFactory.getLogger(className);
    sqlS = new SQLStatements();
    log.finest("Created instance of SQLStatements");
    this.comPort = comPort;
    log.finest("Obtaining DSN name from the application preferences");
    Preferences prefs = JNPreferences.getJNPrefs();
    dsnName = prefs.get("javanews.dsn.name", "unknown");
    if (dsnName.equals("unknown"))
    {
      log.severe("Cannot find the correct preference node: DSN name missing.");
      System.err.println("MAJOR Fault: Cannot locate DSN name in the Preferences file.");
      System.err.println("Cannot connect to the database as the DSN is unknown.");
      System.err.println("The application must close.");
      System.exit(2);
    }
    log.finest("Obtaining database type from the application preferences");
    dbType = prefs.get("javanews.db.type", "unknown");
    if (dbType.equals("unknown"))
    {
      log.severe("Cannot find the correct preference node: Database type missing");
      System.err.println("MAJOR Fault: Cannot locate database typein the Preferences file.");
      System.err.println("Cannot connect to the database as its type is unknown.");
      System.err.println("The application must close.");
      System.exit(3);
    }
    log.config("Connecting to: " + dsnName + " of type: " + dbType);
    if (doesDbExist())
    {
      messageDbPreload();
    }
  }

  /**
   * Used to retrieve the contents of tblMessages from the database and load it
   * into memory to reduce the number of queries and improve response times for
   * the application.
   * <p>The database that this creates is a Hashtable containing a message as it
   * should be received as the key and the ID of the message within the table as
   * the value. The database is then used by the lookupMessage method to retrieve
   * the IDs.
   */
  private void messageDbPreload()
  {
    if (htMessageDb != null)
    {
      return;
    }
    htMessageDb = new Hashtable<String,String>();
    con = getConnection();
    try
    {
      Statement sT = con.createStatement();
      String sql = sqlS.getMessageIDs();
      log.entering("DbInterface.messageDbPreload", "Statement.executeQuery",sql);
      ResultSet rs = sT.executeQuery(sql);
      log.exiting("DbInterface.messageDbPreload", "Statement.executeQuery", rs);
      while (rs.next())
      {
        /*
         * Results are formatted as:
         * ID - String
         * Message - String
         */
        htMessageDb.put(rs.getString(2),rs.getString(1));
      }
    }
    catch (SQLException sqlX)
    {
      log.finer("SQL Exception occurred");
      log.throwing("DbInterface", "messageDbPreload()", sqlX);
      System.err.println("SQL Exception occurred in DbInterface.messageDbPreload() " + sqlX);
    }
  }

  /**
   * Examines the database for known severity 1 messages that can be generated
   * from a PSE and link. Given a message received on a COM port this method
   * attempts to relate the received message to one stored within the database.
   * <p>This method returns the ID of a message within the database that can
   * be stored against the received message to provide a definition of it for
   * reference.
   * @param aMessage The message received from the COM port
   * @return The ID of the related message
   */
  public String lookupMessage(String aMessage)
  {
    String foundID = "";
    Hashtable<String,Integer> htLikelyMatches = new Hashtable<String,Integer>();
    if (htMessageDb.containsKey(aMessage))
    {
      foundID = htMessageDb.get(aMessage);
      return foundID;
    }
    Enumeration eKeys = htMessageDb.keys();
    while (eKeys.hasMoreElements())
    {
      String aKey = (String) eKeys.nextElement();
      StringTokenizer stMsg = new StringTokenizer(aMessage);
      if (aKey.startsWith(stMsg.nextToken()))
      {
        // The two messages start with the same string
        htLikelyMatches.put(aKey, 1);
        while (stMsg.hasMoreTokens())
        {
          String token = stMsg.nextToken();
          if (aKey.contains(token))
          {
            // For every token that matches between our incoming message
            // and possible match we add one so that we should be able to
            // pick the most likely match by the highest figure
            htLikelyMatches.put(aKey, htLikelyMatches.get(aKey) + 1);
          }
        }
      }
    }
    Enumeration candidates = htLikelyMatches.keys();
    int highestSoFar = 0;
    int numberOfSimilarKeys = 0;
    while (candidates.hasMoreElements())
    {
      String key = (String) candidates.nextElement();
      if (highestSoFar < htLikelyMatches.get(key))
      {
        foundID = key;
        highestSoFar = htLikelyMatches.get(key);
        numberOfSimilarKeys = 1;
      }
      if (highestSoFar == htLikelyMatches.get(key))
      {
        numberOfSimilarKeys++;
      }
    }
    if (!foundID.equals(""))
    {
      foundID = htMessageDb.get(foundID);
    }
    if (numberOfSimilarKeys > 1)
    {
      foundID = "";
    }
    return foundID;
  }

  /**
   * Returns the details of a recognised link given its identifier.
   * @param codexID The codex ID
   * @return A vector containing: the PSE name, port ID, details and description.
   */
  public Vector lookupCodexLink(String codexID)
  {
    Vector vecReturn = new Vector();
    log.entering("DbInterface.lookupCodexLink","DbInterface.getCodexLinkID(String, String)");
    con = getConnection();
    try
    {
      PreparedStatement pSt = con.prepareStatement(sqlS.getLinkDetails());
      pSt.setString(1, codexID);
      String sql = sqlS.getLinkDetails();
      sql= sql.replaceFirst("[?]",  codexID);
      if (!isClosing)
      {
        log.entering("DbInterface.lookupCodexLink", "PreparedStatement.executeQuery",sql);
        ResultSet rs = pSt.executeQuery();
        log.exiting("DbInterface.lookupCodexLink", "PreparedStatement.executeQuery", rs);
        vecReturn.clear();
        while (rs.next())
        {
          /* Results are formatted as:
           * PSEName - String
           * PortID - String
           * Details - String
           * Description - String
           */
          vecReturn.add(new Object[] {
          rs.getString(1),
          rs.getString(2),
          rs.getString(3),
          rs.getString(4)});
        }
      }
    }
    catch (SQLException sqlX)
    {
      log.finer("SQL Exception occurred");
      log.throwing("DbInterface", "lookupCodexLink", sqlX);
    }
    finally
    {
      return vecReturn;
    }
  }

  /**
   * Obtain an instance of Connection with which to interface with the database.
   * @return Connection - The new instance to use.
   */
  private Connection getConnection()
  {
    if (con != null)
    {
      return con;
    }
    try
    {
      if (dbType.equals("odbc"))
      {
        Class.forName(jdbcodbcBridge);
        con = DriverManager.getConnection(odbcPrefix + dsnName);
        log.config("Connected to ODBC database: " + dsnName);
      }
      else
      {
        log.severe("Database type retrieved from the preferences file (" + dbType + ") is not a recongised type.");
        System.err.println("MAJOR Error: Cannot recognised the database type specified in the preferences file.");
        System.err.println("Cannot recognise type: " + dbType);
        System.exit(4);
      }
    }
    catch (ClassNotFoundException cnfX)
    {
      log.finer("ClassNotFoundException: Unable to load database driver class");
      log.throwing("DbInterface", "getConnection", cnfX);
      System.err.println("DbInterface.getConnection: Unable to load database driver class");
    }
    catch (SQLException sqlX)
    {
      log.finer("SQLException detected");
      log.throwing("DbInterface", "getConnection", sqlX);
      System.err.println("DbInterface.getConnection: SQL exception detected");
    }
    catch (Exception eX)
    {
      log.finer("Exception detected");
      log.throwing("DbInterface", "getConnection", eX);
      System.err.println("DbInterface.getConnection: General exception detected");
    }
    return con;
  }

  /**
   * Stores a new comment added by a user within the database.
   * @param messageID The identifier of the parent message
   * @param comment The comment itself
   * @return The result of the function. See the class variables for more information.
   */
  public int storeComment(String messageID, String comment)
  {
    int returnCode = WORKED_OK;
    try
    {
      con = getConnection();
      PreparedStatement pstAddComment = con.prepareStatement(sqlS.addComment());
      String newID = UUID.randomUUID().toString();
      log.finest("Adding comment: " + comment + ". To message: " + messageID);
      pstAddComment.setString(1, newID);
      pstAddComment.setString(2, messageID);
      pstAddComment.setString(3, System.getProperty("user.name"));
      Timestamp tsNow = new Timestamp(Calendar.getInstance().getTimeInMillis());
      pstAddComment.setTimestamp(4, tsNow);
      pstAddComment.setString(5, comment);
      String sql = sqlS.addComment();
      sql = sql.replaceFirst("[?]", newID);
      sql = sql.replaceFirst("[?]", messageID);
      sql = sql.replaceFirst("[?]", System.getProperty("user.name"));
      sql = sql.replaceFirst("[?]", comment);
      if (!isClosing)
      {
        log.entering("DbInterface.storeComment", "PreparedStatement.executeUpdate",sql);
        pstAddComment.executeUpdate();
      }
    }
    catch (SQLException sqlX)
    {
      log.finer("SQL Exception occurred");
      log.throwing("DbInterface", "storeComment", sqlX);
      returnCode = ERROR_ENCOUNTERED;
    }
    finally
    {
      return returnCode;
    }
  }

  /**
   * Stores a discovered link in the tblNewLinks table within the database. It returns
   * an integer value describing the result of the functions execution.
   * <p>The ID allocated to the newly stored link is stored in the private String
   * variable returnedNewLinkID.
   * @param pseName The name of the PSE
   * @param linkID The link ID
   * @return The result of the function. See the class variables for more information.
   */
  private int storeNewLink(String pseName, String linkID)
  {
    int returnCode = WORKED_OK;
    try
    {
      con = getConnection();
      PreparedStatement pstMsg = con.prepareStatement(sqlS.addNewLink());
      String newID = UUID.randomUUID().toString();
      log.finest("Discovered link on: " + comPort + "-" + pseName + "-" + linkID + " has been allocated an ID: " + newID);
      pstMsg.setString(1, newID);
      pstMsg.setString(2, comPort);
      pstMsg.setString(3, pseName);
      pstMsg.setString(4, linkID);
      String sql = sqlS.addNewLink();
      sql = sql.replaceFirst("[?]", newID);
      sql = sql.replaceFirst("[?]", comPort);
      sql = sql.replaceFirst("[?]", pseName);
      sql = sql.replaceFirst("[?]", linkID);
      if (!isClosing)
      {
        log.entering("DbInterface.storeNewLink", "PreparedStatement.executeUpdate",sql);
        pstMsg.executeUpdate();
        returnedNewLinkID = newID;
      }
    }
    catch (SQLException sqlX)
    {
      log.finer("SQL Exception occurred");
      log.throwing("DbInterface", "storeNewLink", sqlX);
      returnCode = ERROR_ENCOUNTERED;
    }
    finally
    {
      return returnCode;
    }
  }

  /**
   * Used to find the unique identifier of a discovered link given the PSE name,
   * link ID and (implicitly) the COM port which we're monitoring.
   * @param pseName The name of the PSE name
   * @param linkID The link ID
   * @return The universally unique identifier of the requested link
   */
  public String findDiscoveredLinkID(String pseName, String linkID)
  {
    String returnID = "";
    switch (findNewLink(pseName,linkID))
    {
      case NO_ENTRIES_FOUND:
      {
        log.finer("Could not find an entry for the given details. COM port: " + comPort +
                ", PSE Node: " + pseName + ", Link: " + linkID);
      }
      case WORKED_OK:
      {
        returnID = returnedNewLinkID;
        break;
      }
      case DISCOVERED_DUPLICATES:
      {
        log.finer("Found multiple entries with these given details. COM port: " + comPort +
                ", PSE Node: " + pseName + ", Link: " + linkID);
        returnID = returnedNewLinkID;
        break;
      }
      case ERROR_ENCOUNTERED:
      {
        log.finer("An error was encountered while searching for these given details. COM port: " + comPort +
                ", PSE Node: " + pseName + ", Link: " + linkID);
        break;
      }
      default:
      {
        log.severe("An unexpected return value was received from DbInterface.findDiscoveredLinkID while searching for COM port: " + comPort +
                ", PSE Node: " + pseName + ", Link: " + linkID);
      }
    }
    return returnID;
  }

  /**
   * Used to locate the ID of a discovered link given a PSE name and Link ID.
   * <p>It returns a status code to represent the outcome of running the method.
   * Valid codes that may be returned are:
   * <ul>
   *   <li>NO_ENTRIES_FOUND - Denoting that no results were returned from the query</li>
   *   <li>WORKED_OK - Denoting that a single result was found in the table</li>
   *   <li>DISCOVERED_DUPLICATES - Denoting that two or more results were returned</li>
   *   <li>ERROR_ENCOUNTERED - Denoting an exception was thrown during the method's execution</li>
   * </ul>
   * If the method returns either WORKED_OK or DISCOVERED_DUPLICATES then the first result returned
   * can be examined through the private String variable named returnedNewLinkID.
   * @param pseName The name of the PSE
   * @param linkID The ID of the link
   * @return The status code indicating whether the method ran correctly and what type of results were encountered
   */
  private int findNewLink(String pseName, String linkID)
  {
    int returnCode = WORKED_OK;
    try
    {
      con = getConnection();
      PreparedStatement pstMsg = con.prepareStatement(sqlS.getNewLinkID());
      pstMsg.setString(1, comPort);
      pstMsg.setString(2, pseName);
      pstMsg.setString(3, linkID);
      String sql = sqlS.getNewLinkID();
      sql = sql.replaceFirst("[?]", comPort);
      sql = sql.replaceFirst("[?]", pseName);
      sql = sql.replaceFirst("[?]", linkID);
      if (!isClosing)
      {
        log.entering("DbInterface.findNewLink", "PreparedStatement.executeQuery",sql);
        ResultSet rs = pstMsg.executeQuery();
        log.exiting("DbInterface.findNewLink", "PreparedStatement.executeQuery", rs);
        Vector vecReturn = new Vector();
        while (rs.next())
        {
          vecReturn.add(rs.getString(1));
        }
        switch (vecReturn.size())
        {
          case 0:
          {
            returnCode = NO_ENTRIES_FOUND;
            break;
          }
          case 1:
          {
            returnedNewLinkID = (String) vecReturn.get(0);
            break;
          }
          default:
          {
            returnedNewLinkID = (String) vecReturn.get(0);
            returnCode = DISCOVERED_DUPLICATES;
            break;
          }
        }
      }
    }
    catch (SQLException sqlX)
    {
      log.finer("SQL Exception occurred");
      log.throwing("DbInterface", "findNewLink", sqlX);
      returnCode = ERROR_ENCOUNTERED;
    }
    finally
    {
      return returnCode;
    }
  }

  /**
   * Given a formatted message received through a COM port. Stores the information in the database.
   * @param aMsg The formatted message to store.
   * @return The result of the operation:
   * <ol>
   * <li>Executed without any issues.</li>
   * <li>Detected a new link to be added to the database.</li>
   * <li>It detected duplicates in tblCodexLinks within the database. The index or table may be corrupted.</li>
   * <li>An exception occurred during execution.</li>
   * </ol>
   */
  public int storeMessage(Object[] aMsg)
  {
    int returnVal = WORKED_OK;
    String sCodexID = "";
    sCodexID = getCodexLinkID((String) aMsg[1], (String) aMsg[2]);
    String codexID = "";
    String tempID = "";
    String messageID = "";
    if (sCodexID.equals(""))
    {
      // We're handling a newly discovered link here
      int worked = 0;
      worked = findNewLink((String) aMsg[1], (String) aMsg[2]);
      switch (worked)
      {
        case WORKED_OK:
        {
          // Found the ID so we can use it for this message
          log.finest("Found entry under ID: " + returnedNewLinkID);
          tempID = returnedNewLinkID;
          returnVal = DISCOVERED_LINK_MESSAGE;
          break;
        }
        case DISCOVERED_DUPLICATES:
        {
          // Found duplicates, warn the user but use the first entry to save the message with
          log.warning("Found duplicate entries for " + (String) aMsg[1] + ", " + (String) aMsg[2]);
          log.finest("Using entry ID: " + returnedNewLinkID);
          tempID = returnedNewLinkID;
          break;
        }
        case NO_ENTRIES_FOUND:
        {
          // Completely new link. Store the link in the table and then use the ID
          log.finest("Could not find entry in the new links table. Saving new link");
          worked = storeNewLink((String) aMsg[1], (String) aMsg[2]);
          if (worked == WORKED_OK)
          {
            tempID = returnedNewLinkID;
            returnVal = DISCOVERED_NEW_LINK;
          }
          break;
        }
        default:
        {
          // If the code ends up here then there's been a problem in one of the other methods
          log.warning("A problem was encountered trying to work with a discovered link elsewhere in the code.");
        }
      }
    }
    else
    {
      codexID = sCodexID;
    }
    // Look for the message within the database
    messageID = lookupMessage((String) aMsg[3]);
    try
    {
      con = getConnection();
      PreparedStatement pstMsg = con.prepareStatement(sqlS.addMessage());
      Calendar calNow = Calendar.getInstance();
      pstMsg.setString(1, (String) aMsg[5]); // UUID of the message
      pstMsg.setString(2, codexID);          // ID of the recognised codex
      pstMsg.setString(3, tempID);           // ID of the discovered codex
      pstMsg.setString(4, (String) aMsg[4]); // Alarm date (PSE end)
      Timestamp tsNow = new Timestamp(calNow.getTimeInMillis());
      pstMsg.setTimestamp(5, tsNow);
      pstMsg.setString(6, messageID);        // Message ID
      pstMsg.setString(7, (String) aMsg[0]); // Severity
      pstMsg.setString(8, (String) aMsg[3]); // Message contents
      String sql = sqlS.addMessage();
      sql = sql.replaceFirst("[?]", (String) aMsg[5]);
      sql = sql.replaceFirst("[?]", codexID);
      sql = sql.replaceFirst("[?]", tempID);
      sql = sql.replaceFirst("[?]", (String) aMsg[4]);
      sql = sql.replaceFirst("[?]", calNow.getTime().toString());
      sql = sql.replaceFirst("[?]", messageID);
      sql = sql.replaceFirst("[?]", (String) aMsg[0]);
      sql = sql.replaceFirst("[?]", (String) aMsg[3]);
      if (!isClosing)
      {
        log.entering("DbInterface.storeMessage", "PreparedStatement.executeUpdate",sql);
        pstMsg.executeUpdate();
      }
    }
    catch (SQLException sqlX)
    {
      log.finer("SQL Exception occurred");
      log.throwing("DbInterface", "storeMessage", sqlX);
      return ERROR_ENCOUNTERED;
    }
    finally
    {
      return returnVal;
    }
  }

  /**
   * Returns the ID of the Codex given the PSE Name and link ID.
   * @param pseName The name of the PSE we know
   * @param linkID The name of the link we know
   * @return A string containing the Codex ID.
   */
  public String getCodexLinkID(String pseName, String linkID)
  {
    String sReturn = "";
    con = getConnection();
    try
    {
      PreparedStatement pSt = con.prepareStatement(sqlS.getCodexID());
      pSt.setString(1, comPort);
      pSt.setString(2, pseName);
      pSt.setString(3, linkID);
      String sql = sqlS.getCodexID();
      sql = sql.replaceFirst("[?]", comPort);
      sql = sql.replaceFirst("[?]", pseName);
      sql = sql.replaceFirst("[?]", linkID);
      if (!isClosing)
      {
        log.entering("DbInterface.getCodexLinkID", "PreparedStatement.executeQuery",sql);
        ResultSet rs = pSt.executeQuery();
        log.exiting("DbInterface.getCodexLinkID", "PreparedStatement.executeQuery", rs);
        while (rs.next())
        {
          sReturn = rs.getString(1);
        }
      }
    }
    catch (SQLException sqlX)
    {
      log.finer("SQL Exception occurred");
      log.throwing("DbInterface", "getCodexLinkID", sqlX);
    }
    finally
    {
      return sReturn;
    }
  }

  /**
   * Obtains a list of all recognised PSEs and links.
   * @return The vector containing the PSE Name (String), and Port (String).
   */
  public Vector getCodexLinks()
  {
    Vector vecReturn = new Vector();
    con = getConnection();
    try
    {
      Statement st = con.createStatement();
      if (!isClosing)
      {
        log.entering("DbInterface.getCodexLinks", "Statement.executeQuery",sqlS.getCodexLinks());
        ResultSet result = st.executeQuery(sqlS.getCodexLinks());
        log.exiting("DbInterface.getCodexLinks", "Statement.executeQuery", result);
        while (result.next())
        {
          vecReturn.add(new Object[] {
          result.getString(1),    // PSE Name - String
          result.getString(2)});  // Port - String
        }
      }
    }
    catch (SQLException sqlX)
    {
      log.finer("SQL Exception occurred");
      log.throwing("DbInterface", "getCodexLinks", sqlX);
    }
    finally
    {
      return vecReturn;
    }
  }

  /**
   * Searches for messages received between two dates, namely the supplied date and the current date/time.
   * @param limit The date from which the report should be run.
   * @return Whether the query has executed correctly and placed the results within the supplied ResultSet.
   * @see javanews.gui.internalframe.GenericFrame
   * @see java.util.Calendar
   */
  public GenericTable getMessagesByDate(Calendar limit)
  {
    GenericTable gtReturn = null;
    con = getConnection();
    try
    {
      Calendar now = Calendar.getInstance();
      Timestamp tsThen = new Timestamp(limit.getTimeInMillis());
      Timestamp tsNow = new Timestamp(now.getTimeInMillis());
      PreparedStatement pSt = con.prepareStatement(sqlS.getMessagesByDate());
      pSt.setTimestamp(1, tsThen);
      pSt.setTimestamp(2, tsNow);
      String sql = sqlS.getMessagesByDate();
      sql = sql.replaceFirst("[?]", tsThen.toString());
      sql = sql.replaceFirst("[?]", tsNow.toString());
      gtReturn = createGenericTable(pSt, sql);
    }
    catch (SQLException sqlX)
    {
      System.err.println("A problem was encountered while setting the parameters within a prepared statement.");
      sqlX.printStackTrace();
      log.throwing("DBInterface", "getMessagesByDate(Calendar)", sqlX);
    }
    finally
    {
      return gtReturn;
    }
  }

  /**
   * Searches for messages received from a PSE and any links configured on it.
   * @param pse The name of the PSE to search for.
   * @return An instance of GenericTable that can be placed within a GenericFrame.
   */
  public GenericTable getMessagesByPSE(String pse)
  {
    GenericTable gtReturn = null;
    con = getConnection();
    try
    {
      PreparedStatement pSt = con.prepareStatement(sqlS.getMessagesByPSE());
      pSt.setString(1, comPort);
      pSt.setString(2, pse);
      String sql = sqlS.getMessagesByPSE();
      sql = sql.replaceFirst("[?]", comPort);
      sql = sql.replaceFirst("[?]", pse);
      gtReturn = createGenericTable(pSt, sql);
    }
    catch (SQLException sqlX)
    {
      System.err.println("A problem was encountered while setting the parameters within a prepared statement.");
      sqlX.printStackTrace();
      log.throwing("DBInterface", "getMessagesByPSE(String)", sqlX);
    }
    finally
    {
      return gtReturn;
    }
  }

  /**
   * Searches for messages from a given link within a given PSE. All messages originating
   * from the link across all dates will be returned. <p>If the query returns no results
   * then the table will contain a single row informing the user that no results have
   * been returned.
   * @param pse The name of the PSE to be used
   * @param link The ID of the PSE to be used
   * @return An instance of GenericTable containing the results of the query.
   * @see javanews.gui.internalframe.GenericFrame
   */
  public GenericTable getMessagesByLink(String pse, String link)
  {
    GenericTable gtReturn = null;
    con = getConnection();
    try
    {
      PreparedStatement pSt = con.prepareStatement(sqlS.getMessagesByPSEandLink());
      // Parameters that need to be set are: COM Port, PSE ID, Port ID
      pSt.setString(1, comPort);
      pSt.setString(2, pse);
      pSt.setString(3, link);
      String sql = sqlS.getMessagesByPSEandLink();
      sql = sql.replaceFirst("[?]", comPort);
      sql = sql.replaceFirst("[?]", pse);
      sql = sql.replaceFirst("[?]", link);
      gtReturn = createGenericTable(pSt, sql);
    }
    catch (SQLException sqlX)
    {
      System.err.println("A problem was encountered while setting the parameters within a prepared statement.");
      sqlX.printStackTrace();
      log.throwing("DBInterface", "getMessagesByLink(String,String)", sqlX);
    }
    finally
    {
      return gtReturn;
    }
  }

  /**
   * Generates a GenericTable instance from the results returned from a query.
   * @param pSt An instance of PreparedStatement that has already had its parameters set.
   * @param sql The SQL command to be executed as a String. This is used for logging purposes.
   * @return An instance of the GenericTable contain data resulting from the statement's execution.
   * @see javanews.object.table.GenericTable
   */
  private GenericTable createGenericTable(PreparedStatement pSt, String sql)
  {
    Vector cols = new Vector();
    Vector rows = new Vector();
    try
    {
      if (!isClosing)
      {
        if (pSt.execute())
        {
          log.finest("SQL query executed: " + sql);
          ResultSet sqlResults = pSt.getResultSet();
          try
          {
            ResultSetMetaData rsmd = sqlResults.getMetaData();
            for (int i = 0; i < rsmd.getColumnCount(); i++)
            {
              cols.add(rsmd.getColumnLabel(i+1));
            }
            while (sqlResults.next())
            {
              Object[] tempArray = new Object[rsmd.getColumnCount()];
              for (int i = 0; i < rsmd.getColumnCount(); i++)
              {
                Object obj = sqlResults.getObject(i+1);
                String objClass = obj.getClass().getName();
                if (objClass.equals("java.sql.Timestamp"))
                {
                  java.sql.Timestamp ts = (java.sql.Timestamp) obj;
                  java.util.Date d = new java.util.Date(ts.getTime());
                  DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
                  tempArray[i] = df.format(d);
                }
                else
                {
                  tempArray[i] = obj;
                }
              }
              rows.add(tempArray);
            }
          }
          catch (SQLException sqlX)
          {
            System.err.println("A problem was encountered while accessing the ResultSetMetaData.");
            sqlX.printStackTrace();
            log.throwing("DbInterface", "createGenericTable(PreparedStatement,String)", sqlX);
          }
        }
        else
        {
          System.err.println("Query failed: " + sql);
          cols.clear();
          rows.clear();
          cols.add("Query Failed");
          rows.add(new Object[]{"The SQL syntax was wrong or the query could not be executed."});
        }
      }
    }
    catch (SQLException sqlX)
    {
      System.err.println("A problem was encountered while building the GenericTable.");
      sqlX.printStackTrace();
      log.throwing("DbInterface", "createGenericTable(PreparedStatement,String)", sqlX);
    }
    finally
    {
      if (rows.size() == 0)
      {
        cols.clear();
        rows.clear();
        cols.add("Empty ResultSet");
        rows.add(new Object[]{"There were no results returned from the query."});
      }
      GenericTable gT = new GenericTable();
      gT.setData(cols, rows);
      return gT;
    }
  }

  /**
   * Returns a hashtable containing certain information about a newly discovered
   * link that has been specified by the provided UUID (universally unique identifier)
   * that identifies a single entry within the tblNewLinks table.
   * <p>Should the query return two entries using the same UUID the last of the
   * two entries will be contained within the hashtable.
   * <p>The contents of hashtable will contain the following keys with no value
   * (an empty string )if there wasn't anything returned through the SQL query.
   * @param uuid The identifier of the discovered link to be used
   * @return The hashtable containing the results of the query. If no matches
   * found then the values will contain empty strings. The keys present within
   * the hashtable will be:
   * <ul>
   *   <li>PSEName - The name of the PSE</li>
   *   <li>LinkID - The link identifier</li>
   * </ul>
   */
  public Hashtable<String,String> getDiscoveredLinkDetails(String uuid)
  {
    con = getConnection();
    Hashtable<String,String> htDetails = new Hashtable<String,String>();
    htDetails.put("PSEName", "");
    htDetails.put("LinkID", "");
    try
    {
      PreparedStatement pSt = con.prepareStatement(sqlS.getNewLinkDetails());
      pSt.setString(1, uuid);
      String sql = sqlS.getNewLinkDetails();
      sql = sql.replaceFirst("[?]", uuid);
      if (!isClosing)
      {
        log.entering("DbInterface.getDiscoveredLinkDetails", "PreparedStatement.executeQuery",sql);
        ResultSet rs = pSt.executeQuery();
        while (rs.next())
        {
          // This query should only return one entry in the ResultSet but if there
          // are multiple entries then the last one will be passed out as the result
          // due to the nature of the Hashtable
          htDetails.put("PSEName", rs.getString(1));
          htDetails.put("LinkID", rs.getString(2));
        }
      }
    }
    catch (SQLException sqlX)
    {
      log.throwing("DBInterface", "getDiscoveredLinkDetails(String)", sqlX);
    }
    finally
    {
      return htDetails;
    }
  }

  /**
   * Returns a hashtable containing certain information about a received message
   * that has been specified by the provided UUID (universally unique identifier)
   * that identifies a single entry within the tblReceivedMessages table.
   * <p>Should the query return two entries using the same UUID the last of the
   * two entries will be contained within the hashtable.
   * <p>The contents of hashtable will contain the following keys with no value
   * (an empty string )if there wasn't anything returned through the SQL query.
   * @param uuid The identifier of the received message to be used
   * @return The hashtable containing the results of the query. If no matches
   * found then the values will contain empty strings. The keys present within
   * the hashtable will be:
   * <ul>
   *   <li>PSEId - A foreign key for the known PSE that sent the message <strong>This is mutually exclusive with the TempID in that both keys are present but only one will have a value</strong></li>
   *   <li>TempID - A foreign key for the discovered PSE that sent the message <strong>This is mutually exclusive with the PSEId in that both keys are present but only one will have a value</strong></li>
   *   <li>Severity - The severity of the received message</li>
   *   <li>AlarmDate - The date when the message was sent according to the far-end (PSE)</li>
   *   <li>ReceivedDate - The date when the message was entered into the database</li>
   *   <li>FullMessage - The full message contents received</li>
   *   <li>MessageID - The foreign key for the message definition</li>
   * </ul>
   */
  public Hashtable getMessageDetails(String uuid)
  {
    con = getConnection();
    Hashtable<String,String> htDetails = new Hashtable<String,String>();
    htDetails.put("PSEId", "");
    htDetails.put("TempID", "");
    htDetails.put("Severity", "");
    htDetails.put("AlarmDate", "");
    htDetails.put("ReceivedDate", "");
    htDetails.put("FullMessage", "");
    htDetails.put("MessageID", "");
    try
    {
      PreparedStatement pSt = con.prepareStatement(sqlS.getMessageDetails());
      pSt.setString(1, uuid);
      String sql = sqlS.getMessageDetails();
      sql = sql.replaceFirst("[?]", uuid);
      if (!isClosing)
      {
        log.entering("DbInterface.getMessageDetails", "PreparedStatement.executeQuery",sql);
        ResultSet rs = pSt.executeQuery();
        while (rs.next())
        {
          // This query should only return one entry in the ResultSet but if there
          // are multiple entries then the last one will be passed out as the result
          // due to the nature of the Hashtable
          htDetails.put("PSEId", rs.getString(1));
          htDetails.put("TempID", rs.getString(2));
          htDetails.put("Severity", rs.getString(3));
          Calendar calPSE = Calendar.getInstance();
          Calendar calReceived = Calendar.getInstance();
          Timestamp tsPSE = rs.getTimestamp(4);
          calPSE.setTimeInMillis(tsPSE.getTime());
          Timestamp tsReceived = rs.getTimestamp(5);
          calReceived.setTimeInMillis(tsReceived.getTime());
          htDetails.put("AlarmDate", calPSE.getTime().toString());
          htDetails.put("ReceivedDate", calReceived.getTime().toString());
          htDetails.put("FullMessage", rs.getString(6));
          htDetails.put("MessageID", rs.getString(7));
        }
      }
    }
    catch (SQLException sqlX)
    {
      log.throwing("DBInterface", "getMessageDetails(String)", sqlX);
    }
    finally
    {
      return htDetails;
    }
  }

  /**
   * Returns a vector containing individual entries for each comment added to a
   * given message.
   * <p>Comments are stored in a three field String array containing the name of the
   * person adding the comment, when the comment was added and the comment
   * respectively.
   * @param messageID The ID of the message to return comments for
   * @return The vector containing all of the comments for the given message
   */
  public Vector getComments(String messageID)
  {
    con = getConnection();
    Vector vecReturn = new Vector();
    try
    {
      PreparedStatement pSt = con.prepareStatement(sqlS.getComments());
      pSt.setString(1, messageID);
      String sql = sqlS.getComments();
      sql = sql.replaceFirst("[?]", messageID);
      if (!isClosing)
      {
        log.entering("DbInterface.getComments", "PreparedStatement.executeQuery",sql);
        ResultSet rs = pSt.executeQuery();
        while (rs.next())
        {
          Calendar calTemp = Calendar.getInstance();
          String[] values = new String[3];
          values[0] = rs.getString(1);
          Timestamp tsTemp = rs.getTimestamp(2);
          calTemp.setTimeInMillis(tsTemp.getTime());
          values[1] = calTemp.getTime().toString();
          values[2] = rs.getString(3);
          vecReturn.add(values);
          calTemp = null;
        }
      }
    }
    catch (SQLException sqlX)
    {
      log.throwing("DBInterface", "getComments(String)", sqlX);
    }
    finally
    {
      return vecReturn;
    }
  }

  /**
   * Returns a string containing the definition of a given message.
   * @param messageID The ID of the message to return the definition for
   * @return The message's definition
   */
  public String getMessageDefinition(String messageID)
  {
    con = getConnection();
    String sReturn = new String();
    try
    {
      PreparedStatement pSt = con.prepareStatement(sqlS.getDefinition());
      pSt.setString(1, messageID);
      String sql = sqlS.getDefinition();
      sql = sql.replaceFirst("[?]", messageID);
      if (!isClosing)
      {
        log.entering("DbInterface.getMessageDefinition", "PreparedStatement.executeQuery",sql);
        ResultSet rs = pSt.executeQuery();
        while (rs.next())
        {
          sReturn = rs.getString(1);
        }
      }
    }
    catch (SQLException sqlX)
    {
      log.throwing("DBInterface", "getDefinition(String)", sqlX);
    }
    finally
    {
      return sReturn;
    }
  }

  /**
   * Closes the connection to the back-end database when the application closes.
   */
  public void closeConnection()
  {
    if (doesDbExist())
    {
      try
      {
        isClosing = true;
        Statement sT = con.createStatement();
        String sql = sqlS.removeAllNewLinks();
        log.entering("DbInterface.closeConnection", "Statement.execute(String)", sql);
        sT.execute(sqlS.removeAllNewLinks());
        con.close();
      }
      catch (SQLException sqlX)
      {
        log.finer("SQL Exception occurred while trying to close the database connection");
        log.throwing("DbInterface", "closeConnection()", sqlX);
        System.err.println("SQL Exception occurred in DbInterface.closeConnection() when closing the database connection. " + sqlX);
      }
    }
  }

  /**
   * Removes a named entry from tblNewLinks (discovered links table)
   * @param id The unique identifier of the link to remove from the table.
   * @return The work code indicating whether the function executed successfully or not.
   */
  public int removeDiscoveredLink(String id)
  {
    con = getConnection();
    int resultCode = WORKED_OK;
    try
    {
      PreparedStatement pSt = con.prepareStatement(sqlS.removeNewLink());
      pSt.setString(1, id);
      String sql = sqlS.removeNewLink();
      sql = sql.replaceFirst("[?]", id);
      if (!isClosing)
      {
        log.entering("DbInterface.removeDiscoveredLink", "PreparedStatement.executeQuery",sql);
        pSt.executeUpdate();
      }
    }
    catch (SQLException sqlX)
    {
      log.throwing("DbInterface","removeDiscoveredLink(String)",sqlX);
      resultCode = ERROR_ENCOUNTERED;
    }
    finally
    {
      return resultCode;
    }
  }

  /**
   * Stores a link within the recognised link table (tblCodexLinks) in the database.
   * @param newId The universally unique identifier for the given link
   * @param comPort The COM port that the link is expected to be reported through
   * @param pseName The PSE name of the node the link is connected to
   * @param linkId The name of the link
   * @param details Any user supplied details to be stored with the link
   * @param description Any additional user supplied information to be stored with the link
   * @return The work code indicating whether the function executed successfully or not.
   */
  public int storeNewRecognisedLink(String newId, String comPort, String pseName,
          String linkId, String details, String description)
  {
    con = getConnection();
    int resultCode = WORKED_OK;
    try
    {
      PreparedStatement pSt = con.prepareStatement(sqlS.storeRecognisedLink());
      pSt.setString(1, newId);
      pSt.setString(2, comPort);
      pSt.setString(3, pseName);
      pSt.setString(4, linkId);
      pSt.setString(5, details);
      pSt.setString(6, description);
      String sql = sqlS.storeRecognisedLink();
      sql = sql.replaceFirst("[?]", newId);
      sql = sql.replaceFirst("[?]", comPort);
      sql = sql.replaceFirst("[?]", pseName);
      sql = sql.replaceFirst("[?]", linkId);
      sql = sql.replaceFirst("[?]", details);
      sql = sql.replaceFirst("[?]", description);
      if (!isClosing)
      {
        log.entering("DbInterface.storeNewRecognisedLink", "PreparedStatement.executeQuery",sql);
        pSt.executeUpdate();
      }
    }
    catch (SQLException sqlX)
    {
      log.throwing("DbInterface","storeNewRecognisedLink(String,String,String,String,String,String)",sqlX);
      resultCode = ERROR_ENCOUNTERED;
    }
    finally
    {
      return resultCode;
    }
  }

  /**
   * Changes the values within the tblReceivedMessages table within the database
   * for a discovered PSE link. The function selects all instances within the table
   * of records with a temporary (discovered link) ID equal to that of the supplied
   * <code>discoveredId</code> argument. The values in the temporary ID field are removed
   * (changed to '') and the <code>recognisedId</code> inserted into the recognised ID
   * field of the record.
   * @param recognisedId The recognised link ID to be attached to each record
   * @param discoveredId The discovered link ID to be attached to each record
   * @return The work code indicating whether the function executed successfully or not.
   */
  public int updateLinkIDs(String recognisedId, String discoveredId)
  {
    con = getConnection();
    int resultCode = WORKED_OK;
    try
    {
      PreparedStatement pSt = con.prepareStatement(sqlS.updateNewLinkID());
      pSt.setString(1, recognisedId);
      pSt.setString(2, discoveredId);
      String sql = sqlS.updateNewLinkID();
      sql = sql.replaceFirst("[?]", recognisedId);
      sql = sql.replaceFirst("[?]", discoveredId);
      if (!isClosing)
      {
        log.entering("DbInterface.updateLinkIDs","PreparedStatement.executeQuery", sql);
        pSt.executeUpdate();
      }
    }
    catch (SQLException sqlX)
    {
      log.throwing("DbInterface","updateLinkIDs(String,String)",sqlX);
      resultCode = ERROR_ENCOUNTERED;
    }
    finally
    {
      return resultCode;
    }
  }

  /**
   * Updates the details and description fields of a specific link within the
   * recognised link table. The link is specified by the universally unique
   * identifier supplied as an argument.
   * @param linkId The identifier of the link to be updated
   * @param details The new contents for the details field
   * @param description The new contents for the description field
   * @return The work code indicating whether the function executed successfully or not.
   */
  public int updateDetailsAndDescription(String linkId, String details, String description)
  {
    con = getConnection();
    int resultCode = WORKED_OK;
    try
    {
      PreparedStatement pSt = con.prepareStatement(sqlS.updateDetails());
      pSt.setString(1, details);
      pSt.setString(2, description);
      pSt.setString(3, linkId);
      String sql = sqlS.updateDetails();
      sql = sql.replaceFirst("[?]", details);
      sql = sql.replaceFirst("[?]", description);
      sql = sql.replaceFirst("[?]", linkId);
      if (!isClosing)
      {
        log.entering("DbInterface.updateDetailsAndDescription","PreparedStatement.executeQuery", sql);
        pSt.executeUpdate();
      }
    }
    catch (SQLException sqlX)
    {
      log.throwing("DbInterface", "updateDetailsAndDescription(String,String,String)", sqlX);
      resultCode = ERROR_ENCOUNTERED;
    }
    finally
    {
      return resultCode;
    }
  }

  /**
   * Attempts to connect to the database. If this method encounters any problems
   * while connecting to the database then false is returned to the caller.
   * @return Whether the database exists and can be connected to.
   */
  public boolean doesDbExist()
  {
    boolean canConnectToDb = true;
    try
    {
      Connection conTemp = DriverManager.getConnection(odbcPrefix + dsnName);
      if (conTemp == null)
      {
        canConnectToDb = false;
      }
    }
    catch (Exception x)
    {
      /*
       * As an error was thrown then we can assume an error exists while trying
       * to connect to the database. Therefore we'll warn the user by setting
       * this as null.
       */
      canConnectToDb = false;
    }
    finally
    {
      return canConnectToDb;
    }
  }
}