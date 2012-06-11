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

/* Class name: CloseJavaNews
 * File name:  CloseJavaNews.java
 * Project:    JavaNews - PSE Monitoring
 * Copyright:  © 2007-2009 Alexander J. Harris, all rights reserved
 * Created:    19-Nov-2008 12:55:10
 * Modified:   17-Jun-2009
 *
 * Version History:
 * ~ ~ ~ ~ ~ ~ ~ ~ ~
 * 1.000  17-Jun-2009 Code finalised and released.
 * 0.003  17-Mar-2009 Added code to close any threads executing within the
 *        COMPortThreadManager class.
 * 0.002  25-Jan-2009 Amended class to implement ActionListener so that it acts
 *        as a central point when the application closes. This remvoes the need
 *        for a second class.
 * 0.001  19-Nov-2008 Initial build
 */

package javanews.events.gui;
import javanews.database.*;
import javanews.object.*;
import java.awt.event.*;

/**
 * This class handles any request to close the JavaNews application. It does this
 * by closing all log files gracefully and performs garbage collection prior to
 * the application closing.
 * <p>This class extends the <code>WindowAdapter</code> class and implements
 * the <code>ActionListener</code> interface in order to handle both types of
 * events generate either by the user closing the window, or requesting the
 * application exit through the file menu.
 * @version 1.000
 * @author Alex Harris (email: Alexander.J.Harris(at)btinternet.com)
 */
public class CloseJavaNews extends WindowAdapter implements ActionListener
{ 
  /**
   * The standard constructor for the class.
   */
  public CloseJavaNews()
  {}

  /**
   * This method is called when the application window is requested to shut.
   * The program then closes the logs gracefully and performs garbage collection
   * before ordering the system to exit with a shutdown code of 1 (successful
   * shutdown).
   * @param e The triggered WindowEvent
   */
  public void windowClosing(WindowEvent e)
  {
    closeApp();
  }
  
  /**
   * This method is called when the user clicks on File > Exit from the JavaNews
   * menu bar. The program then closes the logs gracefully and performs garbage
   * collection before ordering the system to exit with a shutdown code of 1
   * (successful shutdown).
   * @param ae The generated ActionEvent
   */
  public void actionPerformed(ActionEvent ae)
  {
    closeApp();
  }

  /**
   * This method is called by both the actionPerformed() and windowClosing()
   * methods. The actual functions are stored in this method to further centralise
   * the code being executed. Any changes to the execution of the program during
   * shutdown should be made here.
   */
  private void closeApp()
  {
    COMPortThreadManager cptm = COMPortThreadManager.getInstance();
    cptm.haltThreads();
    cptm = null;
    DbInterface dbI = new DbInterface("System");
    dbI.closeConnection();
    LoggerFactory.closeLogs();
    System.gc();
    System.out.println("Closing application at: " + java.util.Calendar.getInstance().getTime().toString());
    System.exit(1);
  }
}