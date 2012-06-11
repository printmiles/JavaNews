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

/* Class name: SQLStatements
 * File name:  SQLStatements.java
 * Project:    JavaNews - PSE Monitoring
 * Copyright:  © 2007-2009 Alexander J. Harris, all rights reserved
 * Created:    08-Jan-2009 20:34:23
 * Modified:   17-Jun-2009
 *
 * Version History:
 * ~ ~ ~ ~ ~ ~ ~ ~ ~
 * 1.000  17-Jun-2009 Code finalised and released.
 * 0.007  17-Jun-2009 Added support for updating the details and description of
 *        an existing recognised link.
 * 0.006  12-Jun-2009 Added support for adding new recognised links and changing
 *        (cascading) the change of a discovered link ID to a recognised one.
 * 0.005  07-Jun-2009 Added support for storing new comments in the database
 * 0.004  02-Jun-2009 Added support for finding comments, new link details and
 *        message details
 * 0.003  28-May-2009 Changed sqlAddNewMessage to reflect removal of Acknowledge
 *        and addition of UUID. Also added support for new PSEs and Links with
 *        the inclusion of AddNewLink, GetNewLinkID and RemoveLink
 * 0.002  19-Mar-2009 Added extra methods to support retrieval of messages from
 *        the database by PSE name, link ID and date.
 * 0.001  08-Jan-2009 Initial build
 */

package javanews.database;
import java.util.logging.*;
import javanews.object.LoggerFactory;

/**
 * This class contains all SQL statements used by an instance of DbInterface.
 * There are no methods other than those returning a String value to be used as
 * part of a Statement or PreparedStatement.
 * <p>Those Strings to be used as part of a PreparedStatement can be identified
 * by the presence of a ? within the String.
 * @version 1.000
 * @author Alex Harris (email: Alexander.J.Harris(at)btinternet.com)
 * @see java.sql.PreparedStatement
 * @see java.sql.Statement
 * @see javanews.database.DbInterface
 */
public class SQLStatements
{
  /** The logger to be used for writing messages to the application log */
  private Logger log;
  /** The unique name of the logger within the application */
  private static final String className = "javanews.database.SQLStatements";
  /** The SQL command to be used for adding comments to the database */
  private static final String sqlAddNewComment = "INSERT INTO tblAckComments (ID,MessageID,AddedBy,AddedDate,Comments) VALUES (?,?,?,?,?)";
  /** The SQL command to be used for adding a newly discovered link to the database */
  private static final String sqlAddNewLink = "INSERT INTO tblNewLinks (ID,COMPort,PSEName,PortID) VALUES (?,?,?,?)";
  /** The SQL command to be used for retrieving a newly discovered link ID from the database */
  private static final String sqlGetNewLinkID = "SELECT ID FROM tblNewLinks WHERE COMPort = ? AND PSEName = ? AND PortID = ?";
  /** The SQL command to be used for removing a link from the new links table */
  private static final String sqlRemoveNewLink = "DELETE FROM tblNewLinks WHERE ID = ?";
  /** The SQL command to be used for adding a formatted message to the database */
  private static final String sqlAddNewMessage = "INSERT INTO tblReceivedMessages (ID,PSEId,TempID,AlarmDate,ReceivedDate,MessageID,Severity,FullMessage) VALUES (?,?,?,?,?,?,?,?)";
  /** The SQL command to be used for retrieving all PSEs and their links */
  private static final String sqlGetCodexLinkInfo = "SELECT PSEName, PortID FROM tblCodexLinks ORDER BY PSEName, PortID";
  /** The SQL command to be used for retrieving an ID when given a PSE name and link ID */
  private static final String sqlGetCodexID = "SELECT ID FROM tblCodexLinks WHERE (COMPort = ? AND PSEName = ? AND PortID = ?)";
  /** The SQL command to be used for retrieving information about a link when given its ID */
  private static final String sqlGetCodexLinkDetails = "SELECT PSEName, PortID, Details, Description FROM tblCodexLinks WHERE ID = ?";
  /** The SQL command to be used for retrieving messages from a specific PSE */
  private static final String sqlGetMessagesByPSE = "SELECT rm.ID AS [Message ID], cl.PortID AS [Link Name], rm.AlarmDate AS [Generated Date], rm.ReceivedDate AS Received, rm.FullMessage AS Message " +
                              "FROM tblReceivedMessages AS rm INNER JOIN tblCodexLinks AS cl ON rm.PSEId = cl.ID " +
                              "WHERE rm.PSEId IN (SELECT ID FROM tblCodexLinks WHERE COMPort = ? AND PSEName = ?)";
  /** The SQL command to be used for retrieving messages from a specific PSE and link */
  private static final String sqlGetMessagesByPSEandLink = "SELECT rm.ID AS [Message ID], rm.AlarmDate AS [Generated Date], rm.ReceivedDate AS Received, rm.FullMessage AS Message " +
                              "FROM tblReceivedMessages AS rm INNER JOIN tblCodexLinks AS cl ON rm.PSEId = cl.ID " +
                              "WHERE rm.PSEId IN (SELECT ID FROM tblCodexLinks WHERE COMPort = ? AND PSEName = ? AND PortID = ?)";
  /** The SQL command to be used for retrieving messages between specific dates and times */
  private static final String sqlGetMessagesByDate = "SELECT tblCodexLinks.PSEName AS [PSE Name], tblCodexLinks.PortID AS [Link ID], " +
                              "tblReceivedMessages.AlarmDate AS [PSE Date], tblReceivedMessages.ReceivedDate AS [Received], " +
                              "tblReceivedMessages.FullMessage AS [Message] " +
                              "FROM tblReceivedMessages INNER JOIN tblCodexLinks ON tblReceivedMessages.PSEId = tblCodexLinks.ID " +
                              "WHERE (tblReceivedMessages.ReceivedDate BETWEEN ? AND ?)";
  /** The SQL command to be used for retrieving the contents of tblMessages */
  private static final String sqlGetMessageIDs = "SELECT ID, Message FROM tblMessages ORDER BY Message";
  /** The SQL command to be used for retrieving information about a discovered port */
  private static final String sqlGetDiscoveredByID = "SELECT PSEName, PortID FROM tblNewLinks WHERE ID = ?";
  /** The SQL command to be used for retrieving the comments attached to a given message given its ID */
  private static final String sqlGetComments = "SELECT AddedBy, AddedDate, Comments FROM tblAckComments WHERE MessageID = ?";
  /** The SQL command to be used for retrieving a message's contents given a specific message ID */
  private static final String sqlGetMessageByID = "SELECT PSEId, TempID, Severity, AlarmDate, ReceivedDate, FullMessage, MessageID FROM tblReceivedMessages WHERE ID = ?";
  /** The SQL command to be used for retrieving a message's definition from the messages table */
  private static final String sqlGetMessageDefinition = "SELECT Error FROM tblMessages WHERE ID = ?";
  /** The SQL command to be used for pushing updates to the discovered link ID and changing the link to recognised */
  private static final String sqlCascadeNewLinkID = "UPDATE tblReceivedMessages SET PSEId = ?, TempID = '' WHERE TempID = ?";
  /** The SQL command to be used for storing the details from a previously discovered link in the recognised link table */
  private static final String sqlStoreRecognisedLink = "INSERT INTO tblCodexLinks (ID,COMPort,PSEName,PortID,Details,Description) VALUES (?,?,?,?,?,?)";
  /** The SQL command to be used for updating the details and description of a recognised link within the tblCodexLinks table */
  private static final String sqlUpdateDetails = "UPDATE tblCodexLinks SET Details = ?, Description = ? WHERE ID = ?";
  /** The SQL command to be used for removing old IDs from the discovered link table */
  private static final String sqlRemoveDiscoveredLinks = "DELETE * FROM tblNewLinks";

  /**
   * The only constructor for the class. Used to initialise the logger before further class use.
   */
  public SQLStatements()
  {
    log = LoggerFactory.getLogger(className);
  }

  /**
   * Returns the SQL string used to update the details and description of a
   * recognised link within the database. The string must be used as part of a
   * PreparedStatement and parameters passed as follows:
   * <ol>
   * <li>The details of the link to be saved</li>
   * <li>The description of the link to be saved</li>
   * <li>The universally unique identifier of the link to be updated</li>
   * </ol>
   * @return The SQL string
   * @see java.sql.PreparedStatement
   */
  public String updateDetails()
  {
    log.finest("Returning: " + sqlUpdateDetails);
    return sqlUpdateDetails;
  }

  /**
   * Returns the SQL string used to remove all discovered links from the
   * database during the application shutdown. The string must be used as part
   * of a Statement.
   * @return The SQL string
   * @see java.sql.Statement
   */
  public String removeAllNewLinks()
  {
    log.finest("Returning: " + sqlRemoveDiscoveredLinks);
    return sqlRemoveDiscoveredLinks;
  }

  /**
   * Returns the SQL string used to retrieve the comments attached to a given
   * message. The string must be used as part of a PreparedStatement and
   * parameters passed as follows:
   * <ol>
   * <li>The ID of the message</li>
   * </ol>
   * @return The SQL string
   * @see java.sql.PreparedStatement
   */
  public String getComments()
  {
    log.finest("Returning: " + sqlGetComments);
    return sqlGetComments;
  }

  /**
   * Returns the SQL string used to retrieve the definition of a message given
   * its ID. The string must be used as part of a PreparedStatement and
   * parameters passed as follows:
   * <ol>
   * <li>The ID of the message</li>
   * </ol>
   * @return The SQL string
   * @see java.sql.PreparedStatement
   */
  public String getDefinition()
  {
    log.finest("Returning: " + sqlGetMessageDefinition);
    return sqlGetMessageDefinition;
  }

  /**
   * Returns the SQL string used to retrieve the details of a received message
   * from the database. The string must be used as part of a PreparedStatement
   * and parameters passed as follows:
   * <ol>
   * <li>The ID of the message</li>
   * </ol>
   * @return The SQL string
   * @see java.sql.PreparedStatement
   */
  public String getMessageDetails()
  {
    log.finest("Returning: " + sqlGetMessageByID);
    return sqlGetMessageByID;
  }

  /**
   * Returns the SQL string used to retrieve the details of a discovered link
   * from the database. The string must be used as part of a PreparedStatement
   * and parameters passed as follows:
   * <ol>
   * <li>The ID of the discovered link</li>
   * </ol>
   * @return The SQL string
   * @see java.sql.PreparedStatement
   */
  public String getNewLinkDetails()
  {
    log.finest("Returning: " + sqlGetDiscoveredByID);
    return sqlGetDiscoveredByID;
  }

  /**
   * Returns the SQL string used to add a newly discovered link to the database.
   * The string must be used as part of a PreparedStatement and parameters
   * passed as follows:
   * <ol>
   * <li>The ID of the link to be used for future reference</li>
   * <li>The name of the COM Port being monitored</li>
   * <li>The name of the PSE in question</li>
   * <li>The name of the link on the named PSE</li>
   * </ol>
   * @return The SQL string
   * @see java.sql.PreparedStatement
   */
  public String addNewLink()
  {
    log.finest("Returning: " + sqlAddNewLink);
    return sqlAddNewLink;
  }

  /**
   * Returns the SQL string to be used to list the contents of the tblMessages
   * table within the database.
   * @return The SQL string
   * @see java.sql.Statement
   */
  public String getMessageIDs()
  {
    log.finest("Returning: " + sqlGetMessageIDs);
    return sqlGetMessageIDs;
  }

  /**
   * Returns the SQL string used to obtain a link ID of a discovered link. The string
   * must be used as part of a PreparedStatement and parameters passed as follows:
   * <ol>
   * <li>The name of the COM Port being monitored</li>
   * <li>The name of the PSE in question</li>
   * <li>The name of the link on the named PSE</li>
   * </ol>
   * @return The SQL string
   * @see java.sql.PreparedStatement
   */
  public String getNewLinkID()
  {
    log.finest("Returning: " + sqlGetNewLinkID);
    return sqlGetNewLinkID;
  }

  /**
   * Returns the SQL string used to remove a designated link from the new links table.
   * The string must be used as part of a PreparedStatement and parameters passed
   * as follows:
   * <ol>
   * <li>The ID of the link within the table</li>
   * </ol>
   * @return The SQL string
   * @see java.sql.PreparedStatement
   */
  public String removeNewLink()
  {
    log.finest("Returning: " + sqlRemoveNewLink);
    return sqlRemoveNewLink;
  }

  /**
   * Returns the SQL string used to obtain link details. The string
   * must be used as part of a PreparedStatement and parameters passed as follows:
   * <ol>
   * <li>Unique identifier of the PSE and link within tblCodexLinks</li>
   * </ol>
   * @return The SQL string
   * @see java.sql.PreparedStatement
   */
  public String getLinkDetails()
  {
    log.finest("Returning: " + sqlGetCodexLinkDetails);
    return sqlGetCodexLinkDetails;
  }

  /**
   * Returns the SQL string used to add a message to the database. The string
   * must be used as part of a PreparedStatement and parameters passed as follows:
   * <ol>
   * <li>The ID of the message</li>
   * <li>The ID of the recognised PSE node (this is mutually exclusive with the next field)</li>
   * <li>The ID of the discovered PSE node (this is mutually exclusive with the previous field)</li>
   * <li>The date of the alarm according to the local PSE's clock</li>
   * <li>The ID of the message against the list of recognised messages</li>
   * <li>The severity of the received message</li>
   * <li>The full contents of the message</li>
   * </ol>
   * @return The SQL string
   * @see java.sql.PreparedStatement
   */
  public String addMessage()
  {
    log.finest("Returning: " + sqlAddNewMessage);
    return sqlAddNewMessage;
  }

  /**
   * Returns the SQL string used to add a comment to the database. The string
   * must be used as part of a PreparedStatement and parameters passed as follows:
   * <ol>
   * <li>The ID of the comment</li>
   * <li>The ID of the parent message</li>
   * <li>The name of the person adding the comment</li>
   * <li>The contents of the comment</li>
   * </ol>
   * @return The SQL string
   * @see java.sql.PreparedStatement
   */
  public String addComment()
  {
    log.finest("Returning: " + sqlAddNewComment);
    return sqlAddNewComment;
  }

  /**
   * Returns the SQL string used to obtain the codex links. The string
   * must be used as part of a Statement.
   * @return The SQL string
   * @see java.sql.Statement
   */
  public String getCodexLinks()
  {
    log.finest("Returning: " + sqlGetCodexLinkInfo);
    return sqlGetCodexLinkInfo;
  }

  /**
   * Returns the SQL string used to obtain a codex ID. The string
   * must be used as part of a PreparedStatement and parameters passed as follows:
   * <ol>
   * <li>The name of the COM Port being monitored</li>
   * <li>The name of the PSE in question</li>
   * <li>The link name within the named PSE</li>
   * </ol>
   * @return The SQL string
   * @see java.sql.PreparedStatement
   */
  public String getCodexID()
  {
    log.finest("Returning: " + sqlGetCodexID);
    return sqlGetCodexID;
  }

  /**
   * Returns the SQL string used to obtain messages received between particular dates.
   * The string must be used as part of a PreparedStatement and parameters passed as follows:
   * <ol>
   * <li>The starting date and time for the range</li>
   * <li>The end date and time for the range</li>
   * <li>The name of the COM Port being monitored</li>
   * </ol>
   * @return The SQL string
   * @see java.sql.PreparedStatement
   */
  public String getMessagesByDate()
  {
    log.finest("Returning: " + sqlGetMessagesByDate);
    return sqlGetMessagesByDate;
  }

  /**
   * Returns the SQL string used to obtain messages received from all links on a named PSE.
   * The string must be used as part of a PreparedStatement and parameters passed as follows:
   * <ol>
   * <li>The name of the COM Port being monitored</li>
   * <li>The name of the PSE in question</li>
   * </ol>
   * @return The SQL string
   * @see java.sql.PreparedStatement
   */
  public String getMessagesByPSE()
  {
    log.finest("Returning: " + sqlGetMessagesByPSE);
    return sqlGetMessagesByPSE;
  }

  /**
   * Returns the SQL string used to obtain messages received from a named link on a named PSE.
   * The string must be used as part of a PreparedStatement and parameter passed as follows:
   * <ol>
   * <li>The name of the COM Port being monitored</li>
   * <li>The name of the PSE in question</li>
   * <li>The link name within the named PSE</li>
   * </ol>
   * @return The SQL string
   * @see java.sql.PreparedStatement
   */
  public String getMessagesByPSEandLink()
  {
    log.finest("Returning: " + sqlGetMessagesByPSEandLink);
    return sqlGetMessagesByPSEandLink;
  }

  /**
   * Returns the SQL string used to change a link id from discovered to recognised.
   * The string must be used as part of a PreparedStatement and parameter passed as follows:
   * <ol>
   * <li>The ID of the recognised link to be used</li>
   * <li>The ID of the discovered link to be changed</li>
   * </ol>
   * @return The SQL string
   * @see java.sql.PreparedStatement
   */
  public String updateNewLinkID()
  {
    log.finest("Returning: " + sqlCascadeNewLinkID);
    return sqlCascadeNewLinkID;
  }

  /**
   * Returns the SQL string used to store the details for a recognised link.
   * The string must be used as part of a PreparedStatement and parameter passed as follows:
   * <ol>
   * <li>The link's identifier</li>
   * <li>The COM port identifier</li>
   * <li>The PSE name</li>
   * <li>The link ID or port name</li>
   * <li>The details of the link</li>
   * <li>The description of the link</li>
   * </ol>
   * @return The SQL string
   * @see java.sql.PreparedStatement
   */
  public String storeRecognisedLink()
  {
    log.finest("Returning: " + sqlStoreRecognisedLink);
    return sqlStoreRecognisedLink;
  }
}