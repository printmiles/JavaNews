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

/* Class name: MsgFormatter
 * File name:  MsgFormatter.java
 * Project:    JavaNews - PSE Monitoring
 * Copyright:  © 2007-2009 Alexander J. Harris, all rights reserved
 * Created:    29-Jan-2009 15:01:51
 * Modified:   17-Jun-2009
 *
 * Version History:
 * ~ ~ ~ ~ ~ ~ ~ ~ ~
 * 1.000  17-Jun-2009 Code finalised and released.
 * 0.003  28-May-2009 Removed support for acknowledging messages but improved message
 *        identification within the program and added support for channel identification
 * 0.002  26-Mar-2009 Revised message algorithm to handle erroneous strings better than before
 * 0.001  29-Jan-2009 Initial build
 */

package javanews.object;
import javanews.database.*;
import javanews.gui.internalframe.COMPortClient;
import java.util.*;
import java.util.logging.*;

/**
 * Responsible for receiving messages from the COM port (through instances of
 * <code>PortReader</code>) and formatting the contents. It verifies that the
 * contents of the message is valid and stores it within the database and
 * displays it on-screen. If a message is not deemed to be valid then it is
 * rejected and no further processing occurs.
 * @version 1.000
 * @author Alex Harris (email: Alexander.J.Harris(at)btinternet.com)
 */

public class MsgFormatter
{
  /** The parent window to be used to displayed the results */
  private COMPortClient parent;
  /** The message as received from the COM port */
  private String incomingMessage;
  /** The processed message seperated into relevant fields for storage and display */
  private Object[] formattedMessage;
  /** The identifying name of the COM port */
  private String originatingPort;
  /** The logger to be used for writing messages to the application log */
  private static Logger log;
  /** The unique name of the logger within the application */
  private static final String className = "javanews.object.MsgFormatter";
  /** The constant value representing a link state of up */
  private static final int LINK_STATE_UP = 1;
  /** The constant value representing a link state of down */
  private static final int LINK_STATE_DOWN = 0;
  /** The constant value representing a link state of unknown */
  private static final int LINK_STATE_UNKNOWN = 2;

  /**
   * Receives a message from a COM port, processes it and outputs the formatted
   * result to the given COM Port Client window.
   * @param aMessage The received message from the COM port
   * @param cpcParent The parent window to which the formatted output will be sent
   */
  public MsgFormatter(String aMessage, COMPortClient cpcParent)
  {
    log = LoggerFactory.getLogger(className);
    parent = cpcParent;
    originatingPort = parent.getPortName();
    incomingMessage = aMessage;
    formattedMessage = new Object[6];
    formatMessage();
  }

  /**
   * Examines the contents of the string passed as a received message and checks
   * it for problems. If there is any reasons why the string may be considered
   * erroneous then it is thrown out. Accepted strings are broken into their
   * constituent parts and stored in the back-end database and outputted to the
   * screen.
   * <p>Checks against the contents of the received message are performed in the
   * following order:
   * <ul>
   *   <li>Is the string greater than 33 characters?</li>
   *   <li>Does the string have '(', ')' or ' '?</li>
   *   <li>Is the first token (from the start of the string to the first space) in the format (n)?</li>
   *   <li>Is the token for the PSE name less than or equal to 8 characters?</li>
   * </ul>
   * If any of these checks fail then the string being processed is rejected and
   * considered to be a transmission error, error in configuration or link error.
   */
  public void formatMessage()
  {
    log.finer("Starting processing of message: " + incomingMessage);
    // Trim any whitespace from the string
    incomingMessage = incomingMessage.trim();
    if (incomingMessage.length() < 33)
    {
      // If the string is too short then throw it out
      return;
    }
    if (!incomingMessage.contains("(") && !incomingMessage.contains(")") && !incomingMessage.contains(" "))
    {
      // If the string doesn't contain any brackets (parentheses) or spaces then throw it out
      return;
    }
    StringTokenizer stMsg = new StringTokenizer(incomingMessage);
    String token = "";
    String nextToken = "";
    String sSev = "5"; // Messages received should vary from 1-4. This is to spot problems
    token = stMsg.nextToken();
    if (token.length() != 3 && !token.startsWith("(") && !token.endsWith(")"))
    {
      // The first token should be the severity level of the message and be in the format (n).
      // If it's too long or not in the right format then throw the message out.
      return;
    }
    else
    {
      sSev = token.substring(1, 2);
    }
    // Second token should be the PSE name
    token = stMsg.nextToken();
    nextToken = stMsg.nextToken();
    String sPSENode = "";
    if (token.length() < 8)
    {
      // See if the next token is part of the PSE name.
      // If the total length of the two strings is 8 or less then they're part of the PSE name
      if ((token.length() + nextToken.length()) <= 8)
      {
        sPSENode = token + nextToken;
        nextToken = "";
      }
      else
      {
        sPSENode = token;
      }

    }
    else if (token.length() == 8)
    {
      sPSENode = token;
    }
    else
    {
      // The value for the token is too long to be a valid PSE name so throw it out.
      return;
    }
    // Try and get the date. If the nextToken is blank then we need to get the
    // date. If not then it holds the date
    if (nextToken.equals(""))
    {
      token = stMsg.nextToken();  // This will put the date into the token string
      nextToken = stMsg.nextToken(); // This will put the time into the next string
    }
    else
    {
      token = nextToken.toString(); // Move the date into the first string
      nextToken = stMsg.nextToken(); // Get the time and place into the second string
    }
    String sDate = "";
    String sDay = token.substring(0, 2);
    String sMonth = token.substring(3, 6);
    String sYear = token.substring(7, 11);
    String sHour = nextToken.substring(0, 2);
    String sMin = nextToken.substring(3, 5);
    if (sMonth.equals("JAN")) { sMonth = "01"; }
    if (sMonth.equals("FEB")) { sMonth = "02"; }
    if (sMonth.equals("MAR")) { sMonth = "03"; }
    if (sMonth.equals("APR")) { sMonth = "04"; }
    if (sMonth.equals("MAY")) { sMonth = "05"; }
    if (sMonth.equals("JUN")) { sMonth = "06"; }
    if (sMonth.equals("JUL")) { sMonth = "07"; }
    if (sMonth.equals("AUG")) { sMonth = "08"; }
    if (sMonth.equals("SEP")) { sMonth = "09"; }
    if (sMonth.equals("OCT")) { sMonth = "10"; }
    if (sMonth.equals("NOV")) { sMonth = "11"; }
    if (sMonth.equals("DEC")) { sMonth = "12"; }
    sDate = new String(sDay + "/" + sMonth + "/" + sYear + " " + sHour + ":" + sMin);
    sDay = null;
    sMonth = null;
    sYear = null;
    sHour = null;
    sMin = null;

    String sLinkID = "?";
    String sError = "";
    token = stMsg.nextToken();
    boolean hasChannel = false;
    String sChannel = "";
    if ((token.length() < 7) && token.contains("-"))
    {
      // We've got to pick out the link id on the PSE
      sLinkID = token.toString();
      // Skip the next token as it's only a hyphen
      token = stMsg.nextToken();
    }
    if (token.length() < 11 && token.contains("(") && token.contains(")"))
    {
      // If the code enters here then the token includes the channel number X25-nn(xx)
      sLinkID = token.substring(0, token.indexOf("("));
      sChannel = "on channel " + token.substring(token.indexOf("(")+1, token.indexOf(")"));
      hasChannel = true;
      // Skip the next token as it's only a hyphen
      token = stMsg.nextToken();
    }
    if (token.equals("-"))
    {
      token = stMsg.nextToken();
      sError = sError + token + " ";
    }
    else
    {
      sError = sError + token + " ";
    }
    while (stMsg.hasMoreTokens())
    {
      token = stMsg.nextToken();
      sError = sError + token + " ";
    }
    if (hasChannel)
    {
      sError = sError + sChannel;
    }
    sError = sError.trim();
    log.finer(sSev + ", " + sPSENode + ", " + sDate + ", " + sLinkID + ", " + sError);

    //Severity, PSE, Link, Message, Received, Unique ID
    formattedMessage[0] = sSev;
    formattedMessage[1] = sPSENode;
    formattedMessage[2] = sLinkID;
    formattedMessage[3] = sError;
    formattedMessage[4] = sDate;
    formattedMessage[5] = UUID.randomUUID().toString();

    log.entering("formatMessage", "UnformattedMessage.storeDataInDb(String)", formattedMessage);
    storeDataInDb(formattedMessage);
    log.entering("formatMessage", "JNGui.Client.addAlarmData(String, String)", new Object[] {formattedMessage, originatingPort});
    parent.addAlarmData(formattedMessage);
  }

  /**
   * Takes the provided array of objects and stores the contents in the back-end
   * database. The array is expected to contain the following objects:
   * <ul>
   * <li>0 - Severity (String)</li>
   * <li>1 - PSE node name (String)</li>
   * <li>2 - Link ID (String)</li>
   * <li>3 - Received Error Message (String)</li>
   * <li>4 - Date received from the PSE (String)</li>
   * <li>5 - The universally unique identifier of the message (String)</li>
   * </ul>
   * @param dataToStore An object array in the format mentioned above.
   */
  public void storeDataInDb(Object[] dataToStore)
  {
    final int WORKED_OK = 1;
    final int DISCOVERED_NEW_LINK = 2;
    final int DISCOVERED_DUPLICATES = 3;
    final int ERROR_ENCOUNTERED = 4;
    final int NO_ENTRIES_FOUND = 5;
    final int DISCOVERED_LINK_MESSAGE = 6;

    DbInterface dbI = new DbInterface(originatingPort);
    int x = dbI.storeMessage(formattedMessage);
    switch (x)
    {
      case WORKED_OK:
      {
        // storeMessage worked without any problems.
        log.finest("Message saved in the database with a UUID of: " + formattedMessage[5]);
        String msg = (String) formattedMessage[3];
        Object[] linkState = new Object[3];
        linkState[0] = formattedMessage[1];
        linkState[1] = formattedMessage[2];
        linkState[2] = new Integer(LINK_STATE_UNKNOWN);
        if (msg.equals("LINK DOWN"))
        {
          linkState[2] = new Integer(LINK_STATE_DOWN);
          parent.setLinkData(linkState);
        }
        if (msg.equals("LINK UP"))
        {
          linkState[2] = new Integer(LINK_STATE_UP);
          parent.setLinkData(linkState);
        }
        break;
      }
      case DISCOVERED_NEW_LINK:
      {
        // storeMessage detected that this link was from a new link not in the database.
        String msg = (String) formattedMessage[3];
        if (msg.equals("LINK DOWN"))
        {
          parent.addNewLink((String) formattedMessage[1], (String) formattedMessage[2], LINK_STATE_DOWN);
        }
        else if (msg.equals("LINK UP"))
        {
          parent.addNewLink((String) formattedMessage[1], (String) formattedMessage[2], LINK_STATE_UP);
        }
        else
        {
          parent.addNewLink((String) formattedMessage[1], (String) formattedMessage[2], LINK_STATE_UNKNOWN);
        }
        break;
      }
      case DISCOVERED_DUPLICATES:
      {
        // storeMessage detected duplicates in the database. Generate a warning to the user.
        log.severe("Duplicates in the index of tblCodexLinks were detected while trying to save the received message.");
        System.err.println("Duplicates were detected in the index of tblCodexLinks. Your database may be corrupt.");
        break;
      }
      case ERROR_ENCOUNTERED:
      {
        // storeMessage encountered an exception
        log.warning("storeMessage() encountered an exception. The message it was trying to save may not have been entered into the database.");
        System.err.println("An exception was detected while trying to save the message in the database. The data may not have been saved, please check the logs for more information.");
        break;
      }
      case DISCOVERED_LINK_MESSAGE:
      {
        String msg = (String) formattedMessage[3];
        if (msg.equals("LINK DOWN"))
        {
          parent.addNewLink((String) formattedMessage[1], (String) formattedMessage[2], LINK_STATE_DOWN);
        }
        else if (msg.equals("LINK UP"))
        {
          parent.addNewLink((String) formattedMessage[1], (String) formattedMessage[2], LINK_STATE_UP);
        }
        else
        {
          parent.addNewLink((String) formattedMessage[1], (String) formattedMessage[2], LINK_STATE_UNKNOWN);
        }
        break;
      }
      default:
      {
        // storeMessage returned a value that wasn't recognised
        log.warning("storeMessage returned an unexpected value: " + x);
        break;
      }
    }
  }
}