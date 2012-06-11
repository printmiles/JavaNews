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

/* Class name: LinkRenderer
 * File name:  LinkRenderer.java
 * Project:    JavaNews - PSE Monitoring
 * Copyright:  © 2007-2009 Alexander J. Harris, all rights reserved
 * Created:    19-Dec-2008 21:02:38
 * Modified:   17-Jun-2009
 *
 * Version History:
 * ~ ~ ~ ~ ~ ~ ~ ~ ~
 * 1.000  17-Jun-2009 Code finalised and released.
 * 0.002  12-Jan-2009 Added JavaDoc
 * 0.001  19-Dec-2008 Initial build
 */

package javanews.object.table;
import javanews.object.LoggerFactory;
import java.awt.*;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;

/**
 * This class is responsible for rendering the colour of the link state column
 * in the link table to indicate the known or unknown state of a link.
 * @version 1.000
 * @author Alex Harris (email: Alexander.J.Harris(at)btinternet.com)
 */
public class LinkRenderer extends JLabel implements TableCellRenderer
{
  /** Whether the cell should be displayed with a border */
  private boolean isBordered;
  /** The logger to be used for writing messages to the application log */
  private static Logger log;
  /** The unique name of the logger within the application */
  private static final String className = "javanews.object.table.LinkRenderer";
  /** The constant value representing a link state of up */
  private static final int LINK_STATE_UP = 1;
  /** The constant value representing a link state of down */
  private static final int LINK_STATE_DOWN = 0;

  /**
   * Initialises the class and related variables
   * @param isBordered Whether the cell to be rendered should have a border or not.
   */
  public LinkRenderer(boolean isBordered)
  {
    log = LoggerFactory.getLogger(className);
    this.isBordered = isBordered;
    setOpaque(true); //MUST do this for background to show up.
  }

  /**
   * Sets the colour of the cell to be rendered.
   * @param table The table to edit
   * @param state The state of the link being edited
   * @param isSelected Whether the cell is selected or not
   * @param hasFocus Whether the cell has the users focus
   * @param row The row index of the cell to be altered
   * @param column The column index of the cell to be altered
   * @return The component to be rendered
   */
  public Component getTableCellRendererComponent(JTable table, Object state, boolean isSelected, boolean hasFocus, int row, int column)
  {
    Color newColor = new Color(175,175,175);
    
    switch (((Integer) state).intValue())
    {
      case LINK_STATE_DOWN:
      {
        log.finer("Link at position " + row + "," + column + " is down");
        newColor = new Color(204,0,0);
        break;
      }
      case LINK_STATE_UP:
      {
        log.finer("Link at position " + row + "," + column + " is up");
        newColor = new Color(51,204,0);
        break;
      }
    }
    setBackground(newColor);
    return this;
  }
}