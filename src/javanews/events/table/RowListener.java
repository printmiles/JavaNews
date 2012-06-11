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

/* Class name: RowListener
 * File name:  RowListener.java
 * Project:    JavaNews - PSE Monitoring
 * Copyright:  © 2007-2009 Alexander J. Harris, all rights reserved
 * Created:    19-Dec-2008 22:05:13
 * Modified:   17-Jun-2009
 *
 * Version History:
 * ~ ~ ~ ~ ~ ~ ~ ~ ~
 * 1.000  17-Jun-2009 Code finalised and released.
 * 0.003  07-Jun-2009 Added support for MessageDetail class and discovered link
 *        table
 * 0.002  11-Jan-2009 Added JavaDoc and comments to the class
 * 0.001  19-Dec-2008 Initial build
 */

package javanews.events.table;
import javanews.gui.internalframe.*;
import javanews.object.LoggerFactory;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * This class is to be attached to instances of JTable shown within the main GUI windows.
 * It is responsible for listening for user-generated mouse clicks either on entries in the
 * messages table or the list of links. Once received the class determines which table
 * generated the event and calls the appropriate methods.
 * @version 1.000
 * @author Alex Harris (email: Alexander.J.Harris(at)btinternet.com)
 */
public class RowListener extends Object implements ListSelectionListener
{
  /** The type of table the class is attached to */
  private int type = 0;
  /** The source table it is attached to */
  private JTable table;
  /** The logger to be used for writing messages to the application log */
  private static Logger log;
  /** The unique name of the logger within the application */
  private static final String className = "javanews.events.table.RowListener";
  /** The parent window */
  private COMPortClient sourceWindow;
  /** The constant used to indicate that the class is attached to an instance of the recognised link table */
  private final int RECOGNISED_LINK_TABLE = 1;
  /** The constant used to indicate that the class is attached to an instance of the alarm table */
  private final int ALARM_TABLE = 2;
  /** The constant used to indicate that the class is attached to an instance of the discovered link table */
  private final int DISCOVERED_LINK_TABLE = 3;

  /**
   * This initialises the class and attaches the owners to it by including references to the owning
   * instances in the list of arguments.
   * @param type The type of table it is attached to:
   *   <ol>
   *     <li>Known Link table</li>
   *     <li>Alarm table</li>
   *     <li>Discovered Link table</li>
   *   </ol>
   * @param table The instance of JTable it's attached to
   * @param parent The parent window of the JTable instance
   */
  public RowListener(int type, JTable table, COMPortClient parent)
  {
    log = LoggerFactory.getLogger(className);
    log.finest("Entered method");
    this.type = type;
    this.table = table;
    sourceWindow = parent;
  }

  /**
   * The users mouse-click event triggers this method.
   * @param event The mouse event on the List.
   */
  public void valueChanged(ListSelectionEvent event)
  {
    log.finest("Entered method");
    if (event.getValueIsAdjusting())
    {
      log.finest("Exiting as ListSelectionEvent.getValueIsAdjusting() == true");
      return;
    }
    int selectedRow = table.getSelectionModel().getLeadSelectionIndex();
    if (selectedRow < 0)
    {
      //Ignore call to the method as the cursor position is -1 indicating that nothing is selected
      return;
    }
    switch (type)
    {
      case RECOGNISED_LINK_TABLE:
      {
        String pseName = (String) table.getValueAt(selectedRow, 0);
        String linkID = (String) table.getValueAt(selectedRow, 1);
        log.entering("valueChanged(ListSelectionEvent)", "LinkDetail(String, String, String, String, COMPortClient)",pseName + ": " + linkID);
        LinkDetail lD = new LinkDetail(sourceWindow.getPortName(), pseName, linkID, "known", sourceWindow);
        break;
      }
      case ALARM_TABLE:
      {
        String messageID = (String) table.getValueAt(selectedRow, 5);
        MessageDetail mD = new MessageDetail(sourceWindow.getPortName(), messageID, sourceWindow);
        break;
      }
      case DISCOVERED_LINK_TABLE:
      {
        String pseName = (String) table.getValueAt(selectedRow, 0);
        String linkID = (String) table.getValueAt(selectedRow, 1);
        log.entering("valueChanged(ListSelectionEvent)", "LinkDetail(String, String, String, String, COMPortClient)",pseName + ": " + linkID);
        LinkDetail lD = new LinkDetail(sourceWindow.getPortName(), pseName, linkID, "discovered", sourceWindow);
        break;
      }
      default:
      {
        log.warning("RowListener class has been attached to an unrecognised type: " + type);
      }
    }
  }
}