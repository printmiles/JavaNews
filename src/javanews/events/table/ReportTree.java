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

/* Class name: ReportTree
 * File name:  ReportTree.java
 * Project:    JavaNews - PSE Monitoring
 * Copyright:  © 2007-2009 Alexander J. Harris, all rights reserved
 * Created:    19-Dec-2008 22:04:07
 * Modified:   17-Jun-2009
 *
 * Version History:
 * ~ ~ ~ ~ ~ ~ ~ ~ ~
 * 1.000  17-Jun-2009 Code finalised and released.
 * 0.003  17-Mar-2009 Amended valueChanged method to start calling methods to show filtered results.
 * 0.002  08-Jan-2009 Added JavaDoc
 * 0.001  19-Dec-2008 Initial build
 */

package javanews.events.table;
import javanews.database.*;
import javanews.gui.internalframe.*;
import javanews.object.LoggerFactory;
import javanews.object.table.GenericTable;
import java.util.Calendar;
import java.util.logging.*;
import javax.swing.event.*;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;

/**
 * This class is used to determine the node selected within the report tree object and
 * initiate the event that should be associated with it.
 * @version 1.000
 * @author Alex Harris (email: Alexander.J.Harris(at)btinternet.com)
 */
public class ReportTree extends Object implements TreeSelectionListener
{
  /** The parent window */
  private COMPortClient parent;
  /** The logger to be used for writing messages to the application log */
  private static Logger log;
  /** The unique name of the logger within the application */
  private static final String className = "javanews.events.table.ReportTree";
    
  /**
   * Creates a new instance of ReportTree and associates it with the parent GUI window
   * @param parentWindow The parent GUI window that contains the report tree we should be associated with
   */
  public ReportTree (COMPortClient parentWindow)
  {
    log = LoggerFactory.getLogger(className);
    parent = parentWindow;
  }

  /**
   * Uses the events generated from  the report tree nodes being selected to examine the node to act upon.
   * @param e The TreeSelectionListener used to generate events
   */
  public void valueChanged(TreeSelectionEvent e)
  { 
    log.finer("Entered method");
    DefaultMutableTreeNode nodeChild = new DefaultMutableTreeNode();
    DefaultMutableTreeNode nodeParent = new DefaultMutableTreeNode();
    DefaultMutableTreeNode nodeGrandparent = new DefaultMutableTreeNode();

    if (e != null)
    {
      try
      {
        nodeChild = (DefaultMutableTreeNode) e.getNewLeadSelectionPath().getLastPathComponent();
      }
      catch (NullPointerException npX)
      {
        log.throwing("ReportTree","valueChanged(TreeSelectionEvent)",npX);
        return;
      }
      
    }
    if (nodeChild == null || nodeChild.getLevel() < 2)
    {
      // If the child node is nothing then stop processing, or if the node is high up the heirarchy
      return;
    }
    String function;
    switch (nodeChild.getLevel())
    {
      case 2:
      {
        nodeParent = (DefaultMutableTreeNode) nodeChild.getParent();
        if (nodeParent.getUserObject().toString().equals("By PSE Link"))
        {
          function = "link";
        }
        else
        {
          function = "date";
        }
        break;
      }
      case 3:
      {
        nodeParent = (DefaultMutableTreeNode) nodeChild.getParent();
        nodeGrandparent = (DefaultMutableTreeNode) nodeParent.getParent();
        if (nodeGrandparent.getUserObject().toString().equals("By PSE Link"))
        {
          function = "link";
        }
        else
        {
          // This should happen as nodes under the "By Date" parent shouldn't have a level 3.
          return;
        }
        break;
      }
      default:
      {
        // This is to cover the event that another type of node is found.
        return;
      }
    }

    if (function.equals("date"))
    {
      DbInterface dbI = new DbInterface(parent.getPortName());
      Calendar cStart = Calendar.getInstance();
      
      if (nodeChild.getUserObject().toString().equals("Last 30 minutes"))
      {
        cStart.add(Calendar.MINUTE, -30);
        log.finest("Setting start time to: " + cStart.toString());
      }
      if (nodeChild.getUserObject().toString().equals("Last hour"))
      {
        cStart.add(Calendar.HOUR_OF_DAY, -1);
        log.finest("Setting start time to: " + cStart.toString());
      }
      if (nodeChild.getUserObject().toString().equals("Last 24 hours"))
      {
        cStart.add(Calendar.DAY_OF_MONTH, -1);
        log.finest("Setting start time to: " + cStart.toString());
      }
      if (nodeChild.getUserObject().toString().equals("Last 7 days"))
      {
        cStart.add(Calendar.DAY_OF_MONTH, -7);
        log.finest("Setting start time to: " + cStart.toString());
      }
      if (nodeChild.getUserObject().toString().equals("Last 30 days"))
      {
        cStart.add(Calendar.DAY_OF_MONTH, -30);
        log.finest("Setting start time to: " + cStart.toString());
      }
      GenericTable returnedTable = dbI.getMessagesByDate(cStart);
      GenericFrame gF = new GenericFrame("Query Results for the " + nodeChild.getUserObject().toString().toLowerCase(),returnedTable);
      return;
    }
    
    if (function.equals("link") && nodeParent.getUserObject().toString().equals("By PSE Link"))
    {
      // We're working at level 2 which indicates we're working with a whole PSE
      DbInterface dbI = new DbInterface(parent.getPortName());
      GenericTable rT = dbI.getMessagesByPSE(nodeChild.getUserObject().toString());
      GenericFrame gF = new GenericFrame("Messages received from " + nodeChild.getUserObject().toString() + ":",rT);
      return;
    }
    if (function.equals("link") && nodeGrandparent.getUserObject().toString().equals("By PSE Link"))
    {
      // We're working at level 3 which indicates a particular PSE link
      DbInterface dbI = new DbInterface(parent.getPortName());
      GenericTable rT = dbI.getMessagesByLink(nodeParent.getUserObject().toString(), nodeChild.getUserObject().toString());
      GenericFrame gF = new GenericFrame("Messages received from " + nodeChild.getUserObject().toString() + " on " + nodeParent.getUserObject().toString() + ":", rT);
      return;
    }
  }
}