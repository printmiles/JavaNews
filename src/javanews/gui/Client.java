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

/* Class name: Client
 * File name:  Client.java
 * Project:    JavaNews - PSE Monitoring
 * Copyright:  © 2007-2009 Alexander J. Harris, all rights reserved
 * Created:    17-Nov-2008 22:25:48
 * Modified:   17-Jun-2009
 *
 * Version History:
 * ~ ~ ~ ~ ~ ~ ~ ~ ~
 * 1.000  17-Jun-2009 Code finalised and released.
 * 0.008  13-Mar-2009 Changed class to use the independent class COMPortThreadManager
 *        to keep track of running threads. Removed the hashtable within this class to provide this function.
 * 0.007  16-Feb-2009 Changed getJDesktopPane method to addToDesktop
 * 0.006  13-Feb-2009 Added extra configuration menu option for changing the destination database.
 * 0.005  01-Feb-2009 Removed the status bar, progress bar and bottom frame from the window.
 * 0.004  19-Jan-2009 Added methods to work with the generic hashtable used to handle open threads.
 *                    Added code to the startApp method to open the threads for saved and configured COM ports.
 * 0.003  14-Jan-2009 Added events under the configuration menu
 * 0.002  12-Jan-2009 Added Help > Using and Help > Licensing and events to the Help menu.
 * 0.001  17-Nov-2008 Initial build
 */

package javanews.gui;
import javanews.events.gui.*;
import javanews.object.*;
import javanews.database.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.logging.*;
import java.util.prefs.*;
import javax.swing.*;

/**
 * This class is responsible for the general working environment of the JavaNews application.
 * Processing of messages from the PSEs is not performed here but in the COMPortClient class.
 * This class is used to manage instances of the COMPortClient class and handle the threads
 * contained within them.
 * @version 1.000
 * @author Alex Harris (email: Alexander.J.Harris(at)btinternet.com)
 */

public class Client extends JFrame
{
  /** The application preferences */
  private Preferences pGui;
  /** The logger to be used for writing messages to the application log */
  private Logger log;
  /** The desktop pane containing all of the child windows */
  private JDesktopPane jdMain;
  /** The menubar for the window */
  private JMenuBar jmbMenu;
  /** The unique name of the logger within the application */
  private static final String className = "javanews.gui.Client";
  /** The centralised instance of the class to be returned once instantiated */
  private static Client jnClient;
  /** The instance managing all threads within the application that process data */
  private COMPortThreadManager cptmPorts;
  
  /**
   * Initiates the application and internal logger. Requires the current splash
   * screen to be passed as an argument so that updates can be made.
   * @param ssSplash The splash screen being used to provide feedback to the user during the application load
   */
  public Client(SplashScreen ssSplash)
  {
    // For more information see the constructor above
    log = LoggerFactory.getLogger(className);
    log.finest("Setting ResourceBundle.");
    pGui = JNPreferences.getJNPrefs();
    jnClient = this;
    cptmPorts = new COMPortThreadManager();
    ssSplash.increaseProgress("Building Menus...");
    log.entering(className, "buildMenu()");
    buildMenu();
    ssSplash.increaseProgress("Finishing GUI...");
    log.entering(className, "buildGUI()");
    buildGUI();
    ssSplash.increaseProgress("Starting Application...");
    log.entering(className, "startApp()");
    startApp();
    ssSplash.increaseProgress("Ready");
  }

  /**
   * Part of the main constructor. This method is responsible for initialising
   * the menu along with its associated events.
   */
  private void buildMenu()
  {
    jmbMenu = new JMenuBar();
/* ~ ~ ~ ~ ~ ~ FILE MENU ~ ~ ~ ~ ~ ~ */
    JMenu jmFile;
    JMenuItem jmiFile_Exit;
    jmFile = new JMenu("File");
    jmFile.setMnemonic(KeyEvent.VK_F);
    jmiFile_Exit = new JMenuItem("Exit");
    jmiFile_Exit.setMnemonic(KeyEvent.VK_X);
    jmiFile_Exit.addActionListener(new CloseJavaNews());
    jmFile.add(jmiFile_Exit);
    jmbMenu.add(jmFile);
/* ~ ~ ~ ~ ~ ~ CONFIGURATION MENU ~ ~ ~ ~ ~ ~ */
    JMenu jmConfig;
    JMenu jmConfig_LNF;
    JMenu jmConfig_Log;
    JMenuItem jmiConfig_COMPorts;
    JMenuItem jmiConfig_SetLogDir;
    JMenuItem jmiConfig_SetDB;
    JMenuItem jmiConfig_ShowThreads;
    jmConfig = new JMenu("Configuration");
    jmConfig.setMnemonic (KeyEvent.VK_C);
    jmiConfig_SetLogDir = new JMenuItem("Set Log Directory");
    jmiConfig_SetLogDir.setMnemonic (KeyEvent.VK_V);
    jmiConfig_SetLogDir.addActionListener(new Client_LogDir(this));
    jmiConfig_COMPorts = new JMenuItem("COM Port Config");
    jmiConfig_COMPorts.setMnemonic (KeyEvent.VK_O);
    jmiConfig_COMPorts.addActionListener(new Client_ShowComConfig());
    jmiConfig_SetDB = new JMenuItem("Set Database");
    jmiConfig_SetDB.setMnemonic (KeyEvent.VK_D);
    jmiConfig_SetDB.addActionListener(new Client_SetDatabase());
    jmiConfig_ShowThreads = new JMenuItem("Show Threads");
    jmiConfig_ShowThreads.setMnemonic(KeyEvent.VK_T);
    jmiConfig_ShowThreads.addActionListener(new Client_ShowThreads());
    jmConfig_LNF = new JMenu("Look & Feel");
    jmConfig_LNF.setMnemonic (KeyEvent.VK_F);
    // Adding the Look and Feel section
    // Get the installed LookAndFeel (LnF) installed on the system
    UIManager.LookAndFeelInfo[] lafThisSystem = UIManager.getInstalledLookAndFeels();
    // Create a ButtonGroup to ensure that the RadioButtons work correctly
    ButtonGroup bgLnF = new ButtonGroup();
    // Iterate through the LnF array
    for (int i = 0; i < lafThisSystem.length; i++)
    {
      // Add a radio button with the LnF name to the menu
      JRadioButtonMenuItem jrbMI = new JRadioButtonMenuItem(lafThisSystem[i].getName());
      /* Attach an ActionListener to the button that knows:
       * a) The LnF class name
       * b) The parent GUI to apply the LnF to
       * c) The ResourceBundle to use for any issues that need to be echoed to the user
       */
      jrbMI.addActionListener(new Client_LnF(lafThisSystem[i].getClassName(), this));
      // Check the current LnF being iterated over to see if it matches the one saved in the preferences
      if (lafThisSystem[i].getClassName().equals(pGui.get("javanews.lnf", "javax.swing.plaf.metal.MetalLookAndFeel")))
      {
        // If it matches then set the button as selected
        jrbMI.setSelected(true);
      }
      // Add the button to the ButtonGroup
      bgLnF.add(jrbMI);
      // Add the button to the Menu
      jmConfig_LNF.add(jrbMI);
    }
    // Building the Logging submenu
    jmConfig_Log = new JMenu("Log Level");
    jmConfig_Log.setMnemonic(KeyEvent.VK_L);
    String[] strLog = new String[] {"Severe","Warning","Info","Config","Fine","Finer","Finest","All","Off"};
    ButtonGroup bgLogs = new ButtonGroup();
    for (int i = 0; i < strLog.length; i++)
    {
      JRadioButtonMenuItem jrbMI = new JRadioButtonMenuItem(strLog[i]);
      jrbMI.addActionListener(new Client_Logging(strLog[i]));
      if (pGui.get("javanews.logLevel", "All").equals(strLog[i]))
      {
        jrbMI.setSelected(true);
      }
      bgLogs.add(jrbMI);
      jmConfig_Log.add(jrbMI);
    }
    jmConfig.add(jmConfig_LNF);
    jmConfig.add(jmConfig_Log);
    jmConfig.add(jmiConfig_SetLogDir);
    jmConfig.add(jmiConfig_SetDB);
    jmConfig.add(jmiConfig_ShowThreads);
    jmConfig.add(jmiConfig_COMPorts);
    jmbMenu.add(jmConfig);
/* ~ ~ ~ ~ ~ ~ WINDOW MENU ~ ~ ~ ~ ~ ~ */
    JMenu jmWindow;
    JMenuItem jmiWindow_Tile;
    JMenuItem jmiWindow_MinimiseAll;
    jmWindow = new JMenu("Window");
    jmWindow.setMnemonic (KeyEvent.VK_W);
    jmiWindow_Tile = new JMenuItem("Tile");
    jmiWindow_Tile.setMnemonic(KeyEvent.VK_T);
    jmiWindow_Tile.addActionListener(new Client_TileWindow());
    jmiWindow_MinimiseAll = new JMenuItem("Minimise All");
    jmiWindow_MinimiseAll.setMnemonic(KeyEvent.VK_M);
    jmiWindow_MinimiseAll.addActionListener(new Client_MinimiseAll());
    jmWindow.add(jmiWindow_Tile);
    jmWindow.add(jmiWindow_MinimiseAll);
    jmbMenu.add(jmWindow);
/* ~ ~ ~ ~ ~ ~ HELP MENU ~ ~ ~ ~ ~ ~ */
    JMenu jmHelp;
    JMenuItem jmiHelp_About;
    JMenuItem jmiHelp_Licensing;
    JMenuItem jmiHelp_Using;
    jmHelp = new JMenu("Help");
    jmHelp.setMnemonic (KeyEvent.VK_H);
    jmiHelp_Using = new JMenuItem("Using");
    jmiHelp_Using.setMnemonic (KeyEvent.VK_U);
    jmiHelp_Using.addActionListener(new HelpMenu("using"));
    jmiHelp_Licensing = new JMenuItem("Licensing");
    jmiHelp_Licensing.setMnemonic (KeyEvent.VK_L);
    jmiHelp_Licensing.addActionListener(new HelpMenu("license"));
    jmiHelp_About = new JMenuItem("About");
    jmiHelp_About.setMnemonic (KeyEvent.VK_O);
    jmiHelp_About.addActionListener(new HelpMenu("about"));
    jmHelp.add(jmiHelp_Using);
    jmHelp.add(jmiHelp_Licensing);
    jmHelp.add(jmiHelp_About);
    jmbMenu.add(jmHelp);
  }

  /**
   * Part of the constructor, it builds and places the core components of the GUI
   * on-screen.
   */
  private void buildGUI()
  {
    
    // Settting the JMenuBar
    setJMenuBar(jmbMenu);
    // Applying the preferred Look and Feel to the application. If it can't be found then revert to the Metal LnF
    String strLnF = pGui.get("javanews.lnf", "javax.swing.plaf.metal.MetalLookAndFeel");
    try
    {
      log.finest("Trying to change Look and Feel for Mars to " + strLnF);
      // Apply the new LnF
      UIManager.setLookAndFeel(strLnF);
      // Refresh all of the components in the JFrame
      SwingUtilities.updateComponentTreeUI(this);
    }
    catch (Exception x)
    {
      // If there's a problem applying the LnF then log the error and notify the user in the status pane
      log.throwing(className, "buildGUI()", x);
      System.err.println("Look and Feel could not be applied to the GUI.");
    }
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setTitle("JavaNews: PSE Monitoring");
    // Add the icons for the application. In Windows the 16px icon is used on the titlebar and
    // the 32px icon is used when Alt-Tabbing and the 64px icon for Windows Vista.
    Vector vecIcons = new Vector();
    vecIcons.add(Toolkit.getDefaultToolkit().createImage("images/jnIcon16.png")); // Adds a 16x16 icon
    vecIcons.add(Toolkit.getDefaultToolkit().createImage("images/jnIcon32.png")); // Adds a 32x32 icon
    vecIcons.add(Toolkit.getDefaultToolkit().createImage("images/jnIcon64.png")); // Adds a 64x64 icon
    setIconImages(vecIcons);
    // Set the dimensions of the window to be the same as the current screen size but with a 10px border around the edges
    Dimension dimScreenSize = new Dimension();
    dimScreenSize = Toolkit.getDefaultToolkit().getScreenSize();
    setBounds(20, 20, dimScreenSize.width - 40,dimScreenSize.height - 100);
    // Set the background image of the JDesktopPane
    ImageIcon iiB = new ImageIcon("images/jnBackground.png");
    JLabel lblBackground = new JLabel(iiB);
    lblBackground.setSize(iiB.getIconWidth() + 20, dimScreenSize.height-iiB.getIconHeight());
    lblBackground.setVerticalAlignment(SwingConstants.BOTTOM);
    jdMain = new JDesktopPane();
    jdMain.setDoubleBuffered(true);
    JScrollPane jspDesktop = new JScrollPane(jdMain);
    getContentPane().add(jspDesktop,BorderLayout.CENTER);
    // Add the customised Window listener
    addWindowListener(new CloseJavaNews());
    // Set the GUI to visible
    setVisible(true);
  }

  /**
   * First of all the method checks whether the database can be connected to. If
   * it cannot then the user is warned and no windows opened to monitor COM ports.
   * Otherwise it looks through the application preferences for details of COM
   * ports to monitor and then starts threads accordingly.
   * @see #addPort(java.lang.String, int, int, java.lang.String, float)
   */
  private void startApp()
  {
    DbInterface dbI = new DbInterface("System");
    if (dbI.doesDbExist())
    {
      // Open the COM port information
      int numberOfComPorts = new Integer(pGui.get("javanews.comPort.numberToMonitor", "0")).intValue();
      // Iterate through the COM port information and create a new window for each
      for (int x = 0; x < numberOfComPorts; x++)
      {
        String name = pGui.get("javanews.comPort." + x + ".name", "COM0");
        int baud = new Integer(pGui.get("javanews.comPort." + x + ".baud", "9600")).intValue();
        int data = new Integer(pGui.get("javanews.comPort." + x + ".data", "8")).intValue();
        String parity = pGui.get("javanews.comPort." + x + ".parity", "even");
        float stop = new Float(pGui.get("javanews.comPort." + x + ".stop", "1.0")).floatValue();
        // Got the details, now create a window and add them to the hashtable to be monitored.
        addPort(name, baud, data, parity, stop);
      }
    }
    else
    {
      JOptionPane.showMessageDialog(jdMain, "The database could not be connected to.\n" +
              "You should check the name stored in JavaNews against the ODBC DSN.\n" +
              "Otherwise you should refer to the logs for more information.", "JavaNews", JOptionPane.WARNING_MESSAGE);
    }
  }

  /**
   * Requests that the <code>COMPortThreadManager</code> add a new thread, and
   * so add another window to the desktop, with the specified settings.
   * @param comPortName The identifier of the COM port to monitor
   * @param baudRate The baud rate of the COM port
   * @param dataBits The length of the data to be received in bits
   * @param parityType The type of parity to be used for error checking
   * @param stopBits The length of the stop bits to be used
   * @see javanews.object.COMPortThreadManager
   */
  private void addPort(String comPortName, int baudRate, int dataBits, String parityType, float stopBits)
  {
    cptmPorts.addNewWindow(comPortName, baudRate, dataBits, parityType, stopBits);
  }
  
  /**
   * Used for other classes to obtain a copy of the current JavaNews MDI parent application window
   * @return The main JavaNews window that is being displayed.
   */
  public static Client getJNWindow()
  {
    return jnClient;
  }

  /**
   * Adds the supplied instance of <code>JInternalFrame</code> to the desktop
   * for display to the user.
   * @param jIF An internal frame to be displayed to the user.
   * @see javax.swing.JInternalFrame
   */
  public void addToDesktop(JInternalFrame jIF)
  {
    jdMain.add(jIF);
  }

  /**
   * Returns the instance of <code>JDesktopPane</code> to the receiver.
   * @return The instance of <code>JDesktopPane</code> used by the current class instance.
   * @see javax.swing.JDesktopPane
   */
  public JDesktopPane getDesktopPane()
  {
    return jdMain;
  }
}