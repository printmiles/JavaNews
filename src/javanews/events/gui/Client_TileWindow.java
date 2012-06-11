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


/* Class name: Client_TileWindow
 * File name:  Client_TileWindow.java
 * Project:    JavaNews - PSE Monitoring
 * Copyright:  © 2007-2009 Alexander J. Harris, all rights reserved
 * Created:    02-Feb-2009 20:24:46
 * Modified:   17-Jun-2009
 *
 * Version History:
 * ~ ~ ~ ~ ~ ~ ~ ~ ~
 * 1.000  17-Jun-2009 Code finalised and released.
 * 0.001  02-Feb-2009 Initial build
 */

package javanews.events.gui;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.logging.*;
import javanews.gui.Client;
import javanews.object.*;
import javax.swing.*;

/**
 * This class is used to tile all internal frames within the DesktopPane. All frames
 * regardless of state (minimised, maximised, iconified etc.) are arranged on screen
 * in the same state.
 * @version 1.000
 * @author Alex Harris (email: Alexander.J.Harris(at)btinternet.com)
 */
public class Client_TileWindow implements ActionListener
{
  /** The unique name of the logger within the application */
  private static final String className = "javanews.events.gui.Client_TileWindow";
  /** The logger to be used for writing messages to the application log */
  private Logger log;

  /**
   * Initialises the class and the internal logger.
   */
  public Client_TileWindow()
  {
    log = LoggerFactory.getLogger(className);
  }

  /**
   * The user selection of the JMenuItem within the menu of the parent window.
   * @param ae The generated instance of <code>ActionEvent</code>.
   */
  public void actionPerformed(ActionEvent ae)
  {
    // Find out how many frames we have to deal with
    JInternalFrame[] frames = Client.getJNWindow().getDesktopPane().getAllFrames();
    int frameCount = frames.length;
    // If there aren't any frames open then stop execution
    if (frameCount == 0)
    {
      return;
    }
    // Work out the rough size of a grid
    int sqrt = (int) Math.sqrt(frameCount);
    int rows = sqrt;
    int cols = sqrt;
    // We need to fix outstanding rounding problems from the int conversion above
    if ((rows * cols) < frameCount)
    {
      cols++;
    }
    if ((rows * cols) < frameCount)
    {
      rows++;
    }
    // Get the JDesktopPane so we can find out its size
    Dimension desktopSize = Client.getJNWindow().getDesktopPane().getSize();
    int w, h, x, y;
    w = desktopSize.width / cols;
    h = desktopSize.height / rows;
    x = 0;
    y = 0;

    // Iterate through the frames, displaying, resizing and repositioning them
    for (int i = 0; i < rows; i++)
    {
      for (int j = 0; j < cols && ((i*cols) + j < frameCount); j++)
      {
        JInternalFrame frame = frames[(i*cols)+j];
        if ((frame.isClosed() == false) && (frame.isIcon() == true))
        {
          try
          {
            frame.setIcon(false);
          }
          catch (PropertyVetoException pvX)
          {
            log.throwing("Client_TileWindow", "actionPerformed()", pvX);
            System.err.println("Property has been vetoed.");
          }
        }
        Client.getJNWindow().getDesktopPane().getDesktopManager().resizeFrame(frame, x, y, w, h);
        x += w;
      }
      y += h;
      x = 0;
    }
  }
}