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

/* Class name: Client_LnF
 * File name:  Client_LnF.java
 * Project:    JavaNews - PSE Monitoring
 * Copyright:  © 2007-2009 Alexander J. Harris, all rights reserved
 * Created:    13-Jan-2009 20:09:57
 * Modified:   17-Jun-2009
 *
 * Version History:
 * ~ ~ ~ ~ ~ ~ ~ ~ ~
 * 1.000  17-Jun-2009 Code finalised and released.
 * 0.001  13-Jan-2009 Initial build
 */

package javanews.events.gui;
import java.awt.event.*;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javanews.gui.Client;
import javanews.object.*;
import javax.swing.*;

/**
 * This class is responsible for updating the Look and Feel of the application according to the user's selection.
 * Within the application a menu will contain all of the supported Look and Feels by the installed Java Runtime.
 * An instance of this class is attached to each listed Look and Feel and the user selecting one from the list
 * will update the GUI accordingly.
 * @version 1.000
 * @author Alex Harris (email: Alexander.J.Harris(at)btinternet.com)
 */
public class Client_LnF implements ActionListener
{
  /** The unique name of the logger within the application */
  private static final String className = "javanews.events.gui.Client_LnF";
  /** The string representation of the Look and Feel to be used */
  private String lnfName;
  /** The parent window that the Look and Feel should be applied to */
  private Client client;
  /** The logger to be used for writing messages to the application log */
  private Logger log;

  /**
   * Initialises the class and internal logger. It requires the name of the Look
   * and Feel it is being attached to along with the instance of <code>Client</code> to work.
   * @param name The name of the Look and Feel which this particular class instance monitors
   * @param parentClient The instance of <code>Client</code> that will be updated
   */
  public Client_LnF(String name, Client parentClient)
  {
    client = parentClient;
    lnfName = name;
    log = LoggerFactory.getLogger(className);
    log.finest("Initialised the logger for the Look and Feel events");
  }

  /**
   * The user selection of the JMenuItem within the menu of the parent window.
   * @param ae The generated instance of <code>ActionEvent</code>.
   */
  public void actionPerformed(ActionEvent ae)
  {
    try
    {
      log.finest("Trying to change Look and Feel to " + lnfName);
      Preferences prefLnF = JNPreferences.getJNPrefs();
      prefLnF.put("javanews.lnf", lnfName);
      JNPreferences.updateJNPrefs();
      UIManager.setLookAndFeel(lnfName);
      SwingUtilities.updateComponentTreeUI(client);
    }
    catch (Exception x)
    {
      log.throwing(className, "actionPerformed(ActionEvent)", x);
      System.err.println("Look and Feel could not be applied to the GUI.");
    }
    log.finest("Successfully change look and feel");
  }
}