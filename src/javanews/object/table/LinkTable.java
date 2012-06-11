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

/* Class name: LinkTable
 * File name:  LinkTable.java
 * Project:    JavaNews - PSE Monitoring
 * Copyright:  © 2007-2009 Alexander J. Harris, all rights reserved
 * Created:    19-Dec-2008 21:03:04
 * Modified:   17-Jun-2009
 *
 * Version History:
 * ~ ~ ~ ~ ~ ~ ~ ~ ~
 * 1.000  17-Jun-2009 Code finalised and released.
 * 0.005  11-Mar-2009 Changed structure of the class to improve reliability.
 * 0.004  02-Mar-2009 Added methods getKeys() and getKeys(String) to the class.
 * 0.003  27-Feb-2009 Altered structure of the class to use a generic instance of
 *        the TreeMap class
 * 0.002  12-Jan-2009 Added JavaDoc and amended logging statements to record the
 *        values resulting from the method calls.
 * 0.001  19-Dec-2008 Initial build
 */

package javanews.object.table;
import javanews.object.LoggerFactory;
import java.util.*;
import java.util.logging.*;

/**
 * This class is used to represent links on codexes. The class is designed to hold basic information
 * such as the pse, link id, status and the date the last change was received. This class is used by the COMPortClient
 * class to store information about existing links (those already in the database) and new links (those
 * discovered by the application). As each COMPortClient instance is related to a specific COM port there
 * should always be double the number of instances of this class than COMPortClient windows open on-screen.
 * @version 1.000
 * @author Alex Harris (email: Alexander.J.Harris(at)btinternet.com)
 */
public class LinkTable extends GenericTable
{
  /** The hashtable containing the date and time a status update was received */
  private Hashtable htTimeUpdated;
  /** The list of PSEs and links recognised within the database */
  private TreeMap<String, TreeMap<String,TreeMap<String,Integer>>> tmEntries;
  /** The identifying name of the owning COM port */
  private String comPort;
  /** The logger to be used for writing messages to the application log */
  private static Logger log;
  /** The unique name of the logger within the application */
  private static final String className = "javanews.table.LinkTable";
  /** The hashtable containing stats of link states */
  private Hashtable<String,Integer> htStats;
  /** The constant value representing a link state of up */
  private static final int LINK_STATE_UP = 1;
  /** The constant value representing a link state of down */
  private static final int LINK_STATE_DOWN = 0;
  /** The constant value representing a link state of unknown */
  private static final int LINK_STATE_UNKNOWN = 2;

  /**
   * Initialises the class and an associated instance of Logger to record events.
   * @param comPortName The identifier of the COM port that is being used with the class.
   */
  public LinkTable(String comPortName)
  {
    super();
    log = LoggerFactory.getLogger(className);
    htTimeUpdated = new Hashtable();
    htStats = new Hashtable<String,Integer>();
    htStats.put("up",0);  // Start the counters at zero
    htStats.put("down", 0);
    htStats.put("unknown", 0);
    tmEntries = new TreeMap<String,TreeMap<String,TreeMap<String,Integer>>>();
    comPort = comPortName;
    Vector columns = new Vector();
    Vector rows = new Vector();
    columns.add("PSE Name");
    columns.add("Link ID");
    columns.add("Status");
    super.setData(columns, rows);
  }

  /**
   * This method can be used to add new link information to the link hashtable.
   * As the hashtable overwrites existing entries it can also be used to update the hashtable.
   * @param pse The pse name
   * @param link The link id within the named pse
   * @param state The current state of the named link
   */
  public void addLink(String pse, String link, Integer state)
  {
    String identifier = comPort + ":" + pse + ":" + link;
    log.finest("Searching the database for the following: " + identifier);
    if (htTimeUpdated.containsKey(identifier))
    {
      // The link already exists under the PSE so we're updating the state
      htTimeUpdated.put(identifier, Calendar.getInstance()); // Updates the time edited
      for (int i = 0; i < super.getRowCount(); i++)
      {
        String foundPSE = (String) super.getValueAt(i, 0);
        String foundLink = (String) super.getValueAt(i, 1);
        if (foundPSE.equals(pse) && foundLink.equals(link))
        {
          int oldVal = (Integer) super.getValueAt(i, 2);
          if (oldVal != state) // We only need to store the value if it's actually changed.
          {
            if (state == LINK_STATE_UP)
            {
              decrementStats(oldVal); // Take one away from the previous value
              incrementStats(state); // Add one to the new value
              super.setValueAt(state, i, 2);
            }
            else if (state == LINK_STATE_DOWN)
            {
              decrementStats(oldVal);
              incrementStats(state);
              super.setValueAt(state, i, 2);
            }
            else
            {
              /*
               * As existing links in the database already have a state of LINK_STATE_UNKNOWN
               * new messages received will be changing from UNKNOWN to another state but not
               * to UNKNOWN. This section is used for catching errors as well.
               */
            }
          }
          log.finest("Entry updated");
          return;
        }
      }
      log.fine("Entry not found (" + identifier + ")");
      System.out.println("Tried to edit a link that couldn't be found in the database: " + identifier);
    }
    else
    {
      // The pse isn't known to the database
      htTimeUpdated.put(identifier, Calendar.getInstance());
      log.finest("New entry added to the database");
      super.addRow(new Object[]{pse, link, state});
      // Enter the new entry into the TreeMap
      // Check to see if the COM port is recognised
      if (tmEntries.containsKey(comPort))
      {
        TreeMap tmPort = tmEntries.get(comPort);
        if (tmPort.containsKey(pse))
        {
          TreeMap tmPSE = (TreeMap) tmPort.get(pse);
          if (!tmPSE.containsKey(link))
          {
            // We don't need to update to update this map with the new status
            // so all we're doing is checking to see if the link hasn't been
            // recognised and so add it if it's new.
            tmPSE.put(link, state);
            tmPort.put(pse, tmPSE);
            incrementStats(state);
            tmEntries.put(comPort, tmPort);
          }
        }
        else
        {
          // The PSE hasn't been recognised so add a new entry to the port map.
          TreeMap tmLink = new TreeMap();
          tmLink.put(link, state);
          tmPort.put(pse, tmLink);
          incrementStats(state);
          tmEntries.put(comPort, tmPort);
        }
      }
      else
      {
        // The port hasn't been recognised so add a new entry for everything.
        TreeMap tmLink = new TreeMap();
        tmLink.put(link,state);
        TreeMap tmPSE = new TreeMap();
        tmPSE.put(pse,tmLink);
        tmEntries.put(comPort,tmPSE);
        incrementStats(state);
      }
    }
  }

  /**
   * This is a convinience method for setting a link state. It actually calls the addLink method.
   * @see #addLink(java.lang.String, java.lang.String, java.lang.Integer)
   * @param pse The pse name
   * @param link The link id within the named pse
   * @param state The current state of the named link
   */
  public void setLinkState(String pse, String link, Integer state)
  {
    addLink(pse, link, state);
  }

  /**
   * Returns the state of a given link within a given pse. States are:
   * <ul>
   * <li>0 - Link down</li>
   * <li>1 - Link up</li>
   * <li>2 - Link state unknown</li>
   * </ul>
   * @param pse The name of the PSE
   * @param link The name of the link within the named PSE
   * @return The state of the link or -1 for not found.
   */
  public Integer getLinkState(String pse, String link)
  {
    String identifier = comPort + ":" + pse + ":" + link;
    for (int i = 0; i < super.getRowCount(); i++)
    {
      String foundPSE = (String) super.getValueAt(i, 0);
      String foundLink = (String) super.getValueAt(i, 1);
      if (foundPSE.equals(pse) && foundLink.equals(link))
      {
        Integer intState = (Integer) super.getValueAt(i, 2);
        log.finest("Returning state for PSE: " + pse + ", Link: " + link);
        return intState;
      }
    }
    log.fine("Entry not found (" + identifier + ")");
    System.out.println("Tried to edit a link that couldn't be found in the database: " + identifier);
    return -1;
  }

  /**
   * Returns the date the status of the link was last changed.
   * @param pse The name of the PSE
   * @param link The name of the link within the named PSE
   * @return The date and time of the last status update on the link
   */
  public Calendar getUpdatedDate(String pse, String link)
  {
    String identifier = comPort + ":" + pse + ":" + link;
    return (Calendar) htTimeUpdated.get(identifier);
  }

  /**
   * Returns all of the PSE names contained within the table as an instance of
   * <code>NavigableSet</code>
   * @return The names of all the known PSEs
   * @see java.util.NavigableSet
   */
  public NavigableSet getPSENames()
  {
    if (tmEntries.isEmpty())
    {
      TreeMap<String, TreeMap<String,TreeMap<String,Integer>>> tmTemp = new TreeMap<String,TreeMap<String,TreeMap<String,Integer>>>();
      TreeMap tmLink = new TreeMap();
      tmLink.put("No links found", LINK_STATE_UNKNOWN);
      TreeMap tmPort = new TreeMap();
      tmPort.put("No PSEs found", tmLink);
      tmTemp.put(comPort, tmPort);
      return tmTemp.get(comPort).navigableKeySet();
    }
    else
    {
      return tmEntries.get(comPort).navigableKeySet();
    }
  }

  /**
   * Returns all of the link names contained within the table given a PSE as an
   * instance of <code>NavigableSet</code>
   * @param pse The PSE name to be used
   * @return The names of all the known links under a PSE
   * @see java.util.NavigableSet
   */
  public NavigableSet getLinkNames(String pse)
  {
    if (tmEntries.isEmpty())
    {
      TreeMap<String, TreeMap<String,TreeMap<String,Integer>>> tmTemp = new TreeMap<String,TreeMap<String,TreeMap<String,Integer>>>();
      TreeMap tmLink = new TreeMap();
      tmLink.put("No links found", LINK_STATE_UNKNOWN);
      TreeMap tmPort = new TreeMap();
      tmPort.put("No PSEs found", tmLink);
      tmTemp.put(comPort, tmPort);
      return tmTemp.get(comPort).get(pse).navigableKeySet();
    }
    else
    {
      return tmEntries.get(comPort).get(pse).navigableKeySet();
    }
  }

  /**
   * Returns the stats of the links within the table. The <code>Hashtable</code>
   * should only contain three keys with integer values of the number of links
   * in each of those states. These states are:
   * <ul>
   *   <li>up - The number of links in an up state</li>
   *   <li>down - The number of links in a down state</li>
   *   <li>unknown - The number of links in an unknown state</li>
   * </ul>
   * @return The figures of links in each state
   * @see java.util.Hashtable
   */
  public Hashtable getInitialFigures()
  {
    return htStats;
  }

  /**
   * Decrements the counter of the given state by one.
   * @param state The state to decrement:
   *               <ul>
   *                 <li>0 - Link down</li>
   *                 <li>1 - Link up</li>
   *                 <li>2 - Link state unknown</li>
   *               </ul>
   */
  public void decrementStats(int state)
  {
    switch (state)
    {
      case LINK_STATE_DOWN:
      {
        // Link down
        htStats.put("down", (htStats.get("down") - 1));
        break;
      }
      case LINK_STATE_UP:
      {
        // Link up
        htStats.put("up", (htStats.get("up") - 1));
        break;
      }
      case LINK_STATE_UNKNOWN:
      {
        // Link state unknown
        htStats.put("unknown", (htStats.get("unknown") - 1));
        break;
      }
      default:
      {
        // The code shouldn't end up here.
        log.warning("Trying to parse an invalid state: " + state);
      }
    }
  }

  /**
   * Increments the counter of the given state by one.
   * @param state The state to increment:
   *               <ul>
   *                 <li>0 - Link down</li>
   *                 <li>1 - Link up</li>
   *                 <li>2 - Link state unknown</li>
   *               </ul>
   */
  public void incrementStats(int state)
  {
    switch (state)
    {
      case LINK_STATE_DOWN:
      {
        htStats.put("down", (htStats.get("down") + 1));
        break;
      }
      case LINK_STATE_UP:
      {
        htStats.put("up", (htStats.get("up") + 1));
        break;
      }
      case LINK_STATE_UNKNOWN:
      {
        htStats.put("unknown", (htStats.get("unknown") + 1));
        break;
      }
      default:
      {
        // The code shouldn't end up here.
        log.warning("Trying to parse an invalid state: " + state);
      }
    }
  }
}