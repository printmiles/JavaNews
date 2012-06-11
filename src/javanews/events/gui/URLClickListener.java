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

/* Class name: URLClickListener
 * File name:  URLClickListener.java
 * Project:    JavaNews - PSE Monitoring
 * Copyright:  © 2007-2009 Alexander J. Harris, all rights reserved
 * Created:    13-Jan-2009 15:18:59
 * Modified:   17-Jun-2009
 *
 * Version History:
 * ~ ~ ~ ~ ~ ~ ~ ~ ~
 * 1.000  17-Jun-2009 Code finalised and released.
 * 0.003  08-Apr-2009 Fixed issue with running a Windows specific command on other OSs.
 * 0.002  05-Mar-2009 Fixed bug with user clicking in the JEditorPane and launching
 *        unrequired Internet Explorer windows.
 * 0.001  13-Jan-2009 Initial build
 */

package javanews.events.gui;
import java.net.*;
import java.util.logging.*;
import javanews.object.LoggerFactory;
import javax.swing.event.*;

/**
 * Used to detect the user attempting to navigate through HTML links
 * embedded within any HTML content displayed within a JEditorPane.
 * @version 1.000
 * @author Alex Harris (email: Alexander.J.Harris(at)btinternet.com)
 */

public class URLClickListener implements HyperlinkListener
{
  /** The URL of the link the user has clicked on */
  private static String hoverURL = "localhost";
  /** The unique name of the logger within the application */
  private static final String className = "javanews.events.gui.URLClickListener";
  /** The logger to be used for writing messages to the application log */
  private Logger log;
  /** The identifier for the local file system */
  private static final String localFileSystem = "file://localhost";

  /**
   * Used to initilise the class and internal logger.
   */
  public URLClickListener()
  {
    log = LoggerFactory.getLogger(className);
  }

  /**
   * Listens for events generated by the user clicking on a HTML link to another
   * page.
   * @param httpE The {@link HyperlinkEvent} generated from the user's selection
   */
  public void hyperlinkUpdate(HyperlinkEvent httpE)
  {
    if (httpE.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
    {
      hoverURL = httpE.getURL().toString();
      if (hoverURL.startsWith(localFileSystem))
      {
        hoverURL = hoverURL.substring(localFileSystem.length());
        try
        {
          String fileDirectory = System.getProperty("user.dir");
          if (System.getProperty("file.separator").equals("\\"))
          {
            fileDirectory = fileDirectory.replace("\\","/");
            fileDirectory = fileDirectory.replace(" ", "%20");
          }
          URL localURL = new URL("file:///" + fileDirectory +"/"+ hoverURL);
          hoverURL = localURL.toString();
        }
        catch (MalformedURLException mURLX)
        {
          log.throwing(className, "hyperlinkUpdate()", mURLX);
        }
      }
      try
      {
        log.fine("User clicked on a hyperlink to: " + hoverURL);
        if (System.getProperty("os.name").contains("Windows"))
        {
          // This command was taken from: http://centerkey.com/java/browser/ [Accessed online, 13th Jan 2009]
          Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + hoverURL);
        }
      }
      catch (java.io.IOException ioX)
      {
        log.throwing("URLClickListener", "hyperlinkUpdate(HyperlinkEvent)", ioX);
      }
    }
  }
}