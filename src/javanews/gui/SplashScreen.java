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

/* Class name: SplashScreen
 * File name:  SplashScreen.java
 * Project:    JavaNews - PSE Monitoring
 * Copyright:  © 2007-2009 Alexander J. Harris, all rights reserved
 * Created:    13-Oct-2008 13:47:01
 * Modified:   17-Jun-2009
 *
 * Version History:
 * ~ ~ ~ ~ ~ ~ ~ ~ ~
 * 1.000  17-Jun-2009 Code finalised and released.
 * 0.001  13-Oct-2008 Initial build
 */

package javanews.gui;
import java.awt.*;
import javax.swing.*;

/**
 * Used to provide information to the user while the main components of the
 * application are initialised.
 * @version 1.000
 * @author Alex Harris (email: Alexander.J.Harris(at)btinternet.com)
 */
public class SplashScreen extends JWindow
{
  /** The width of the splash screen */
  private static final int WIDTH = 490;
  /** The height of the splash screen */
  private static final int HEIGHT = 200;
  /** The colour of the progress bar contents */
  private static final Color ProgressBar_COLOUR = new Color(206,123,0);
  /** The colour for text displayed within the splash screen */
  private static final Color Text_COLOUR = Color.BLACK;
  /** The status text displayed above the progress bar */
  private static JLabel textVal;
  /** The progress bar displayed on the splash screen */
  private static JProgressBar progress;

  /**
   * Initialises the class and sets the maximum value of steps taken to initialise
   * the application. Once the application has passed through these stages, and
   * called <code>increaseProgress()</code> accordingly the splash-screen is
   * updated and eventually closed to allow for interaction with the main program.
   * @param maximumValue The total number of stages of initialisation for the program.
   */
  public SplashScreen(int maximumValue)
  {
    JPanel content = (JPanel) getContentPane();
    content.setBackground(Color.WHITE);

    // Set the window's bounds, centering the window.
    Dimension screen = Toolkit.getDefaultToolkit( ).getScreenSize( );
    int x = (screen.width-WIDTH)/2;
    int y = (screen.height-HEIGHT)/2;
    setBounds(x,y,WIDTH,HEIGHT);

    // Build the splash screen.
    JLabel label = new JLabel(new ImageIcon("images/jnsplash.png"));
    JPanel jPBottom = new JPanel(new GridLayout(2,1));
    textVal = new JLabel("Loading...", JLabel.CENTER);
    textVal.setFont(new Font("Sans-Serif", Font.BOLD, 12));
    textVal.setForeground(Text_COLOUR);
    progress = new JProgressBar();
    progress.setMaximum(maximumValue);
    progress.setForeground(ProgressBar_COLOUR);
    jPBottom.add(textVal);
    jPBottom.add(progress);
    content.add(label, BorderLayout.CENTER);
    content.add(jPBottom, BorderLayout.SOUTH);

    // Display it.
    setVisible(true);
  }

  /**
   * Increases the value of the progress bar without changing the displayed text.
   * The value of the progress bar is incremented by one. If the new value of the
   * progress bar is greater than the value the splash-screen was initialised with
   * then it is closed.
   */
  public void increaseProgress()
  {
    int newVal = progress.getValue() + 1;
    if (newVal <= progress.getMaximum())
    {
      progress.setValue(newVal);
    }
    else
    {
      this.setVisible(false);
      this.dispose();
    }
  }

  /**
   * Increases the value of the progress bar and updates the displayed text to
   * that of the supplied argument. The <code>increaseProgress()</code> method
   * is called to update the value and so the same conditions apply.
   * @param newText The new text to be displayed above the progress bar.
   * @see #increaseProgress()
   */
  public void increaseProgress(String newText)
  {
    textVal.setText(newText);
    increaseProgress();
  }
}