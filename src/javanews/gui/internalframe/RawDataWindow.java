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

/* Class name: RawDataWindow
 * File name:  RawDataWindow.java
 * Project:    JavaNews - PSE Monitoring
 * Copyright:  © 2007-2009 Alexander J. Harris, all rights reserved
 * Created:    25-Jan-2009 11:55:54
 * Modified:   17-Jun-2009
 *
 * Version History:
 * ~ ~ ~ ~ ~ ~ ~ ~ ~
 * 1.000  17-Jun-2009 Code finalised and released.
 * 0.002  25-Feb-2009 Added Javadoc, getOutputText method and removed the toolbar
 *        to save output to the file system. Attached a listener for InternalFrameEvents
 *        to watch for the window closing and prompt the user whether to save the output.
 * 0.001  25-Jan-2009 Initial build
 */

package javanews.gui.internalframe;
import javanews.gui.*;
import javanews.events.gui.*;
import javanews.object.*;
import java.awt.*;
import java.util.logging.*;
import javax.swing.*;

/**
 * This class can be attached to a displayed instance of COMPortClient and is used to display messages received by the application.
 * Messages are seperated from another by use of the ASCII 10 character (new-line) and will be displayed here before they are passed
 * to other classes within the application for further processing.
 * @version 1.000
 * @author Alex Harris (email: Alexander.J.Harris(at)btinternet.com)
 */

public class RawDataWindow extends JInternalFrame
{
  /** The logger to be used for writing messages to the application log */
  private Logger log;
  /** The unique name of the logger within the application */
  private static final String className = "javanews.gui.internalframe.RawDataWindow";
  /** The text area to display all of the received text */
  private JTextArea jtaOutput;
  /** The identifying name of the owning port */
  private String owningPort;

  /**
   * Initialises a new window to display raw data that has been received by the application prior to processing by the PortReader class.
   * Any text seperated with a new line character (ASCII code 10) is displayed here even if the contents isn't added to the database.
   * @param name The name of the owning port to be displayed in the title bar.
   */
  public RawDataWindow(String name)
  {
    super("Raw Data for COM Port: " + name, true, true, true, true);
    super.setLayer(1);
    // Obtain an instance of Logger for the class
    log = LoggerFactory.getLogger(className);
    owningPort = name;
    log.fine("Raw Data is owned by: " + owningPort);
    jtaOutput = new JTextArea();
    jtaOutput.setEditable(false);
    jtaOutput.setLineWrap(true);
    JScrollPane jsp = new JScrollPane(jtaOutput);
    this.add(jsp, BorderLayout.CENTER);
    log.finest("Adding frame to the desktop");
    this.addInternalFrameListener(new CloseRawDataWindow(this));
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    Dimension dimMinSize = new Dimension();
    Dimension dimPrefSize = new Dimension();
    dimMinSize.setSize(400, 200);
    dimPrefSize.setSize(600,300);
    this.setMinimumSize(dimMinSize);
    this.setPreferredSize(dimPrefSize);
    this.setSize(600, 300);
    Client parent = Client.getJNWindow();
    parent.addToDesktop(this);
    this.setVisible(true);
  }

  /**
   * Adds a string to the end of the window and updates the cursor position to move to the end of the new string.
   * @param text The new message to be appended to the output window.
   */
  public void addData(String text)
  {
    jtaOutput.append("\n" + text);
    jtaOutput.setCaretPosition(jtaOutput.getText().length());
  }

  /**
   * Returns the text currently shown within the JTextArea window.
   * @return The contents of the text area.
   */
  public String getOutputText()
  {
    return jtaOutput.getText();
  }

  /**
   * Returns the identifier of the COM port that this is outputting text from.
   * @return The name of the owning COM port
   */
  public String getOwningPort()
  {
    return owningPort;
  }
}