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

/* Class name: HelpMenu
 * File name:  HelpMenu.java
 * Project:    JavaNews - PSE Monitoring
 * Copyright:  © 2007-2009 Alexander J. Harris, all rights reserved
 * Created:    11-Jan-2009 17:27:32
 * Modified:   17-Jun-2009
 *
 * Version History:
 * ~ ~ ~ ~ ~ ~ ~ ~ ~
 * 1.000  17-Jun-2009 Code finalised and released.
 * 0.001  11-Jan-2009 Initial build
 */

package javanews.events.gui;
import javanews.gui.internalframe.*;
import javanews.object.LoggerFactory;
import java.awt.event.*;
import java.util.logging.*;

/**
 * Listens for events corresponding to any of the three items in the Help menu.
 * An identifier is used to recognise exactly which type of event is being generated.
 * @version 1.000
 * @author Alex Harris (email: Alexander.J.Harris(at)btinternet.com)
 */

public class HelpMenu implements ActionListener
{
  /** The unique name of the logger within the application */
  private static final String className = "javanews.events.gui.HelpMenu";
  /** The name of the button which the class has been bound to */
  private String id;
  /** The logger to be used for writing messages to the application log */
  private static Logger log;
  
  /**
   * Creates a new instance of the class. As this class can be used to respond to any one
   * of 3 distinct events the supplied identifier allows us to recognise which event should be triggered.
   * <p> The events supported by this class are:
   * <ul>
   * <li>Help &gt; About</li>
   * <li>Help &gt; Using</li>
   * <li>Help &gt; Licensing</li>
   * </ul>
   * @param identifier The string identifier of the event to be attached to. This should be in lower case.
   */
  public HelpMenu(String identifier)
  {
    log = LoggerFactory.getLogger(className);
    id = identifier;
  }
  
  /**
   * The event generate is compared against the button to which it is attached and the
   * appropriate Help window is shown in the GUI.
   * @param e The event generated by the user.
   */
  public void actionPerformed(ActionEvent e)
  {
    if (id.equals("about"))
    {
      Help_About haNew = new Help_About();
    }
    if (id.equals("license"))
    {
      Help_Licensing hlNew = new Help_Licensing();
    }
    if (id.equals("using"))
    {
      Help_Using huNew = new Help_Using();
    }
  }
}