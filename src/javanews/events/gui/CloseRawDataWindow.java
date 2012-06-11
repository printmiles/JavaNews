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


/* Class name: CloseRawDataWindow
 * File name:  CloseRawDataWindow.java
 * Project:    JavaNews - PSE Monitoring
 * Copyright:  © 2007-2009 Alexander J. Harris, all rights reserved
 * Created:    24-Feb-2009 20:43:02
 * Modified:   17-Jun-2009
 *
 * Version History:
 * ~ ~ ~ ~ ~ ~ ~ ~ ~
 * 1.000  17-Jun-2009 Code finalised and released.
 * 0.002  03-Mar-2009 Added support for a FileFilter to the JFileChooser
 * 0.001  24-Feb-2009 Initial build
 */

package javanews.events.gui;
import javanews.gui.internalframe.RawDataWindow;
import javanews.object.LoggerFactory;
import javanews.object.save.filters.TXTFilter;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * This class is responsible for reacting to the user closing instances of the RawDataWindow class.
 * It queries the user as to whether they want to save the contents of the window to a text file.
 * @version 1.000
 * @author Alex Harris (email: Alexander.J.Harris(at)btinternet.com)
 */
public class CloseRawDataWindow implements InternalFrameListener
{
  /** The window to be closed */
  private RawDataWindow parent;
  /** The unique name of the logger within the application */
  private static final String className = "javanews.events.gui.CloseRawDataWindow";
  /** The logger to be used for writing messages to the application log */
  private Logger log;

  /**
   * Used to attach the listener to a particular instance of the RawDataWindow class
   * @param rdwWindow The window the listener should be attached to
   */
  public CloseRawDataWindow(RawDataWindow rdwWindow)
  {
    parent = rdwWindow;
    log = LoggerFactory.getLogger(className);
    log.fine("RawDataWindow listener has been attached");
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
   * This method is called by both the actionPerformed() and windowClosing()
   * methods. The actual functions are stored in this method to further centralise
   * the code being executed. Any changes to the execution of the program during
   * shutdown should be made here.
   * <p>The method questions the user as to whether they want to store the contents
   * of the raw data window. If they do then they are prompted for a file name and
   * location and the contents of the window is saved in a TXT format.
   */
  private void closeWindow()
  {
    String filePath = "Unknown";
    try
    {
      int choice = JOptionPane.showOptionDialog(parent.getDesktopPane(),
              "Do you want to save the contents of this window to a text file?",
              "Save contents to text file?",
              JOptionPane.YES_NO_OPTION,
              JOptionPane.QUESTION_MESSAGE, null, null, null);
      if (choice == JOptionPane.YES_OPTION)
      {
        log.fine("User wants to save contents of the raw data window to the file system");
        JFileChooser jfcSave = new JFileChooser();
        jfcSave.setDialogTitle("Save Output As...");
        jfcSave.setFileFilter(new TXTFilter());
        jfcSave.setSelectedFile(new File("JN_Raw Data_" + parent.getOwningPort() + "_" + java.util.Calendar.getInstance().getTime().toString()));
        int saveNow = jfcSave.showSaveDialog(parent.getDesktopPane());
        if (saveNow == JFileChooser.APPROVE_OPTION)
        {
          File savePath = jfcSave.getSelectedFile();
          filePath = savePath.toString();
          // Correct the file path if the user missed .txt from the end
          if (!filePath.endsWith(".txt"))
          {
            filePath = filePath + ".txt";
          }
          PrintWriter pwOut = new PrintWriter(new BufferedWriter(new FileWriter(filePath)));
          String output = parent.getOutputText();
          StringTokenizer st = new StringTokenizer(output,"\n");
          JProgressBar jpbSaveProgress = new JProgressBar(0,st.countTokens());
          JDialog jdProgress = new JDialog();
          jdProgress.setContentPane(jpbSaveProgress);
          jdProgress.setVisible(true);
          int i = 0;
          while (st.hasMoreTokens())
          {
            pwOut.println(st.nextToken());
            jpbSaveProgress.setValue(i);
            i++;
          }
          pwOut.flush();
          pwOut.close();
          jdProgress.setVisible(false);
        }
      }
      parent = null;
      System.gc();
      log.finest("Window has been closed");
    }
    catch (IOException ioX)
    {
      log.warning("Problem encountered while writing the output file to " + filePath);
      log.throwing("CloseRawDataWindow","closeWindow()", ioX);
      System.err.println("Problem creating the output file. Please refer to the logs for more details.");
      System.err.println(ioX.getMessage());
      System.err.println(ioX);
    }
    catch (Exception x)
    {
      log.severe("Could not close the raw data window");
      log.throwing("CloseRawDataWindow","closeWindow()", x);
      System.err.println("Problem closing raw data window. Please refer to the logs for more details.");
      System.err.println(x.getMessage());
      System.err.println(x);
    }
  }
}