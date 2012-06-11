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

/* Class name: CloseComPortClient
 * File name:  CloseComPortClient.java
 * Project:    JavaNews - PSE Monitoring
 * Copyright:  © 2007-2009 Alexander J. Harris, all rights reserved
 * Created:    27-Jan-2009 15:30:34
 * Modified:   17-Jun-2009
 *
 * Version History:
 * ~ ~ ~ ~ ~ ~ ~ ~ ~
 * 1.000  17-Jun-2009 Code finalised and released.
 * 0.001  27-Jan-2009 Initial build
 */

package javanews.events.gui;
import javanews.gui.internalframe.COMPortClient;
import javanews.object.LoggerFactory;
import java.awt.event.*;
import java.util.logging.*;
import javax.swing.event.*;

/**
 * This class handles any request to close a COM Port Client window. It does this
 * by closing all log files gracefully and performs garbage collection prior to
 * closing the window.
 * <p>This class extends the <code>WindowAdapter</code> class and implements
 * the <code>ActionListener</code> interface in order to handle both types of
 * events generate either by the user closing the window, or requesting the
 * exit through the file menu.
 * @version 1.000
 * @author Alex Harris (email: Alexander.J.Harris(at)btinternet.com)
 */
public class CloseComPortClient implements ActionListener, InternalFrameListener
{
  /** The window to be closed */
  private COMPortClient parent;
  /** The unique name of the logger within the application */
  private static final String className = "javanews.events.gui.CloseComPortClient";
  /** The logger to be used for writing messages to the application log */
  private Logger log;

  /**
   * Initialises the class and internal logger. It requires the instance of
   * <code>COMPortClient</code> to be passed as an argument to ensure that the
   * associated thread is closed before the window is shut and garbage collection
   * is performed.
   * @param cpcWindow
   */
  public CloseComPortClient(COMPortClient cpcWindow)
  {
    parent = cpcWindow;
    log = LoggerFactory.getLogger(className);
    log.fine("CloseComPortClient has been attached to the window for " + parent.getPortName());
  }

  /**
   * The event fired when the window is closing and used to trigger the required customised code.
   * @param ife The event being generated
   */
  public void internalFrameClosing(InternalFrameEvent ife)
  {
    closeWindow();
  }

  /**
   * This event is inherited from the interface but not implemented.
   * @param ife The event being generated
   */
  public void internalFrameClosed(InternalFrameEvent ife)
  {
    // We don't need anything special to happen here. Leave the code alone.
  }

  /**
   * This event is inherited from the interface but not implemented.
   * @param ife The event being generated
   */
  public void internalFrameOpened(InternalFrameEvent ife)
  {
    // We don't need anything special to happen here. Leave the code alone.
  }

  /**
   * This event is inherited from the interface but not implemented.
   * @param ife The event being generated
   */
  public void internalFrameIconified(InternalFrameEvent ife)
  {
    // We don't need anything special to happen here. Leave the code alone.
  }

  /**
   * This event is inherited from the interface but not implemented.
   * @param ife The event being generated
   */
  public void internalFrameDeiconified(InternalFrameEvent ife)
  {
    // We don't need anything special to happen here. Leave the code alone.
  }

  /**
   * This event is inherited from the interface but not implemented.
   * @param ife The event being generated
   */
  public void internalFrameActivated(InternalFrameEvent ife)
  {
    // We don't need anything special to happen here. Leave the code alone.
  }

  /**
   * This event is inherited from the interface but not implemented.
   * @param ife The event being generated
   */
  public void internalFrameDeactivated(InternalFrameEvent ife)
  {
    // We don't need anything special to happen here. Leave the code alone.
  }
  
  /**
   * This method is called when the user clicks on File > Exit from the menu bar.
   * It calls a central private method to perform the action so as to centralise
   * the code.
   * @param ae The generated ActionEvent
   */
  public void actionPerformed(ActionEvent ae)
  {
    closeWindow();
  }

  /**
   * This method is called by both the actionPerformed() and windowClosing()
   * methods. The actual functions are stored in this method to further centralise
   * the code being executed. Any changes to the execution of the program during
   * shutdown should be made here.
   * <p>The method stops the executing thread in the parent instance of
   * <code>COMPortClient</code> and performs garbage collection before the
   * window is closed.
   */
  private void closeWindow()
  {
    try
    {
      log.config("Closing window for port " + parent.getPortName());
      parent.stopThread();
      parent = null;
      System.gc();
      log.finest("Window has been closed");
    }
    catch (Exception x)
    {
      log.severe("Could not close window for port " + parent.getPortName());
      log.throwing("CloseComPortClient","closeWindow()", x);
      System.err.println("Problem closing COM port window. Please refer to the logs for more details.");
      System.err.println(x.getMessage());
      System.err.println(x);
    }
  }
}