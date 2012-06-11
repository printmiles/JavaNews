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


/* Class name: PortReader
 * File name:  PortReader.java
 * Project:    JavaNews - PSE Monitoring
 * Copyright:  © 2007-2009 Alexander J. Harris, all rights reserved
 * Created:    26-Jan-2009 21:45:06
 * Modified:   17-Jun-2009
 *
 * Version History:
 * ~ ~ ~ ~ ~ ~ ~ ~ ~
 * 1.000  17-Jun-2009 Code finalised and released.
 * 0.007  15-Apr-2009 Changed getData, getParity and getStop methods to ensure
 *        that a legitmate value is returned even if one is not supplied. Also
 *        added getBaud method to ensure that the provided baudRate is a valid
 *        value when provided.
 * 0.006  18-Mar-2009 Removed code relating to stopping and restarting thread execution.
 * 0.005  17-Mar-2009 Added isReaderRunning method to support threading operations.
 * 0.004  30-Jan-2009 Moved code from run() into methods to clarify its contents.
 *        Added the ability to check whether the parent COMPortClient is closed
 *        or not and stop the thread based on that.
 * 0.003  29-Jan-2009 Added test thread (COM99)
 * 0.002  28-Jan-2009 Added thread coding
 * 0.001  26-Jan-2009 Initial build
 */

package javanews.object;
import javanews.gui.Client;
import javanews.gui.internalframe.*;
import gnu.io.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.*;

/**
 * Used to receive data from a named COM port. The application allows for any
 * named port to be accessed providing it is recognised as a Serial port by the
 * underlying RxTx libraries. Multiple instances of this class can be used to
 * access multiple COM ports with each class instance accessing a uniquely
 * named port.
 * <p>Each port must be freely accessible for the application to work
 * correctly with it and so there cannot any other application attempting to
 * access the same port as JavaNews. The COM port configuration window can be
 * used to identify whether a port is in use by another application. This window
 * should also be used to set the configuration values for each port as it
 * contains the supported values for each setting.
 * <p>The application expects COM port configuration values to be one of those
 * listed below. JavaNews uses restricted values for user entry (in the COM port
 * configuration window) and verification of values here to ensure that no
 * invalid values can be configured with a port and thus cause system instability
 * either for the application or for the platform on which it is operating.
 * <p><h2>Supported Argument Values:</h2>
 * The following lists show supported values for each of the arguments required
 * when configuring a COM port. While the constructor will accept any values
 * compatible with the given data type, only certain values are supported by the
 * RxTx configuration and the COM port itself. These, accepted, values are
 * listed below and should any value not be supported those shown as default
 * values will be used instead.
 * <h3>Baud Rate (bps)</h3>
 * <ul>
 *   <li>110</li>
 *   <li>300</li>
 *   <li>600</li>
 *   <li>1200</li>
 *   <li>2400</li>
 *   <li>4800</li>
 *   <li>9600 - <em>Default Value</em></li>
 *   <li>14400</li>
 *   <li>19200</li>
 *   <li>38400</li>
 *   <li>57600</li>
 *   <li>115200</li>
 * </ul>
 * <h3>Data Length (bits)</h3>
 * <ul>
 *   <li>5</li>
 *   <li>6</li>
 *   <li>7</li>
 *   <li>8 - <em>Default Value</em></li>
 * </ul>
 * <h3>Parity Type</h3>
 * <ul>
 *   <li>Even</li>
 *   <li>Odd</li>
 *   <li>Mark</li>
 *   <li>None - <em>Default Value</em></li>
 * </ul>
 * <h3>Stop Bit (size of, in bits)</h3>
 * <ul>
 *   <li>1 - <em>Default Value</em></li>
 *   <li>1.5</li>
 *   <li>2</li>
 * </ul>
 * @version 1.000
 * @author Alex Harris (email: Alexander.J.Harris(at)btinternet.com)
 */
public class PortReader implements Runnable
{
  /** The unique name of the logger within the application */
  private static final String className = "javanews.object.PortReader";
  /** The logger to be used for writing messages to the application log */
  private Logger log;
  /** The identifying name of the COM port that the class has been created for */
  private String comPortName;
  /** The type of parity to be used within the class */
  private String parityType;
  /** The baud rate to be used within the class */
  private int baudRate;
  /** The number of bits to be used for carrying data within the class */
  private int dataBits;
  /** The length of the stop bit within the class */
  private float stopBits;
  /** The parent instance of <code>COMPortClient</code> used to output the results to */
  private COMPortClient parent;
  /** Whether the port should continue to be monitored */
  private Boolean shouldRun;
  /** How long (in milliseconds) the test port should sleep before its next iteration */
  private final int TEST_PORT_THREAD_DELAY = 100;

  /**
   * Initialises the class and its internal logger. It uses the supplied arguments
   * for the configuration of the COM port to read data accurately and read data
   * from it.
   * <p>As the arguments are of types that support any value, there are checks
   * in place to ensure that unsupported values are not passed to the COM port
   * which may crash the program. Please refer to the notes on the class for
   * more information about support values and default values.
   * @param name The identifying name of the COM port <strong>See the class notes</strong>.
   * @param baud The baud rate to be used <strong>See the class notes</strong>.
   * @param data The number of bits used to carry data <strong>See the class notes</strong>.
   * @param parity The type of parity to be used <strong>See the class notes</strong>.
   * @param stop The number of bits used for the stop bit <strong>See the class notes</strong>.
   * @param cpcParent The parent instance used to output data to
   */
  public PortReader(String name, int baud, int data, String parity, float stop, COMPortClient cpcParent)
  {
    log = LoggerFactory.getLogger(className);
    setShouldRun(true);
    comPortName = name;
    baudRate = baud;
    dataBits = data;
    parityType = parity;
    stopBits = stop;
    log.config("Port reader configured with the following settings: \n"+
            "Port name:\t" + name + "\n" +
            "Baud:\t" + baud + "\n" +
            "Data bits:\t" + data + "\n" +
            "Parity:\t" + parity + "\n" +
            "Stop bit:\t" + stop);
    parent = cpcParent;
  }

  /**
   * Starts the thread running using either a normal COM port (COM1 - COM98) or
   * to use the test port which reads data from a local file (COM99).
   * @see #accessCOMPort()
   * @see #runTestPort()
   */
  public void run()
  {
    log.info("Examining port " + comPortName);
    if (comPortName.equals("COM99"))
    {
      runTestPort();
    }
    else
    {
      accessCOMPort();
    }
  }

  /**
   * Receives input from a COM port and stores it within an instance of
   * <code>StringBuffer</code> for further processing. On receipt of a return
   * character from the COM port the contents of the <code>StringBuffer</code>
   * is passed to an instance of <code>MsgFormatter</code> to interpret the
   * contents of the message.
   * <p> This method uses the RxTx libraries to access the COM port, configure
   * it and read data from it.
   * <p>This method is solely responsible for receiving the data from the COM
   * port, not interpreting it. This should allow the application as a whole to
   * respond quickly to incoming data in the event of sudden influxes of data.
   * The MsgFormatter is responsible for handling messages received here and their
   * vetting, interpretation and storage in the database.
   * @see javanews.object.MsgFormatter
   *
   */
  private synchronized void accessCOMPort()
  {
    try
    {
      CommPortIdentifier cpiPort = CommPortIdentifier.getPortIdentifier(comPortName);
      if (cpiPort.isCurrentlyOwned())
      {
        // The port is being used by another application. Notify the user and stop further execution.
        log.warning("Cannot access port " + comPortName + " as it is in use with another application.");
        JOptionPane.showMessageDialog(Client.getJNWindow().getDesktopPane(),"Cannot read from port (" + comPortName + "), currently in use.","JavaNews:",JOptionPane.ERROR_MESSAGE);
        parent.setClosed(true);
      }
      else
      {
        // The port is free to be accessed by JavaNews.
        log.info("Trying to access port " + comPortName);
        String something = this.getClass().getName();
        CommPort comPort = cpiPort.open(something, 2000);
        // Check to see what type of COM port we're working with.
        if (comPort instanceof SerialPort)
        {
          // We're working with a serial port and so can continue with execution
          SerialPort spCOM = (SerialPort) comPort;
          spCOM.setSerialPortParams(getBaud(), getData(), getStop(), getParity());
          InputStream inS = spCOM.getInputStream();
          log.finest("Obtained the port input stream");
          StringBuffer sB = new StringBuffer();
          BufferedInputStream bis = new BufferedInputStream(inS);
          int in = 0;
          char inChar;
          while (getShouldRun())
          {
            // This is the loop which actually receives the incoming data and works with everything.
            in = bis.read();
            switch (in)
            {
              case -1:
              {
                // Do nothing, this is generated by the COM port when it's doing nothing
              }
              case 10: // \n
              {
                // Turn the little status light on
                parent.turnStatusOn();
                if (sB.length() != 0)
                {
                  log.fine("Message received on " + comPortName + ": " + sB.toString());
                  if (parent.isRawDataWindowOpen())
                  {
                    RawDataWindow rdw = parent.getRawDataWindow();
                    rdw.addData(sB.toString());
                    rdw = null;
                  }
                  MsgFormatter mf = new MsgFormatter(sB.toString(), parent);
                  mf = null;
                  sB.delete(0, sB.length());
                  System.gc();
                }
              }
              case 13:
              {
                // Ignore the \n character as we're looking for \r instead (above)
              }
              default:
              {
                // Turn the little status light on
                parent.turnStatusOn();
                if ((in > 0) && (in != 10) && (in != 13))
                {
                  inChar = (char) in;
                  sB.append(inChar);
                }
              }
            }
            // Turn the little status light off
            parent.turnStatusOff();
            // Quickly check to see if the COMPortClient window is still open
            boolean bClosed, bVisible;
            bClosed = parent.isClosed();
            bVisible = parent.isVisible();
            if (bClosed || !bVisible)
            {
              // If it is then we don't need to keep processing
              parent.stopThread();
            }
          }
        }
        else
        {
          // We've got some other form of port (Parallel or some legacy thing) and can't work with it.
          log.warning("Cannot read from COM port, only Serial ports can be accessed");
          JOptionPane.showMessageDialog(Client.getJNWindow().getDesktopPane(),"Cannot read from COM Port, only Serial ports can be accessed.","JavaNews:",JOptionPane.ERROR_MESSAGE);
        }
      }
    }
    catch (UnsatisfiedLinkError ulE)
    {
      System.err.println("UnsatisfiedLinkError: COM Port Not Found - " + comPortName);
      log.warning("UnsatisfiedLinkError: COM Port Not Found - " + comPortName);
      log.throwing("PortReader", "run()", ulE);
      JOptionPane.showMessageDialog(Client.getJNWindow().getDesktopPane(),"Cannot read from requested COM Port (" + comPortName + ")\n" +
              "There may be a problem with the port as it cannot be located or read from correctly","JavaNews:",JOptionPane.ERROR_MESSAGE);
    }
    catch (NoSuchPortException nspX)
    {
      System.err.println("NoSuchPortException: COM Port Not Found - " + comPortName);
      log.warning("NoSuchPortException: COM Port Not Found - " + comPortName);
      log.throwing("PortReader", "run()", nspX);
      JOptionPane.showMessageDialog(Client.getJNWindow().getDesktopPane(),"Cannot find the requested COM Port (" + comPortName + ") on this machine\n" +
              "Ensure that any adapters are connected correctly, and that the port has not been disabled in Device Manager\n" +
              "Close the application and try again or configure another port to be used","JavaNews:",JOptionPane.ERROR_MESSAGE);
    }
    catch (Exception x)
    {
      System.err.println("An error occurred in COMPortServer.run(), printing stack trace:");
      x.printStackTrace();
      log.warning("An error occurred in COMPortServer.run()");
      log.throwing("COMPortServer", "run()", x);
    }
  }

  /**
   * Runs a simulated port which can be used for testing where a COM port or PSE
   * might not be available. The test port enumerates the OpenNewsSample.txt
   * file and stores the contents line-by-line in a Vector which is then sequentially
   * passed to an instance of <code>MsgFormatter</code> for further processing.
   * <p>This is repeated at a higher rate than would normally be expected on a
   * typical PSE network as messages through the test port are generated at 1 per
   * 10ms and is intended to duplicate prolonged periods of high volume.
   */
  private synchronized void runTestPort()
  {
    try
    {
      // This is the test port that we can use to read raw data from a text file
      log.fine("Opening file OpenNewsSample.txt");
      BufferedReader brSampleFile = new BufferedReader(new FileReader("OpenNewsSample.txt"));
      Vector vecFile = new Vector();
      String fileLine = brSampleFile.readLine();
      while (fileLine != null)
      {
        vecFile.add(fileLine);
        fileLine = brSampleFile.readLine();
      }
      while (getShouldRun())
      {
        for (int x = 0; x < vecFile.size(); x++)
        {
          if (!getShouldRun())
          {
            break;
          }
          String msg = (String) vecFile.get(x);
          // Turn the little status light on
          parent.turnStatusOn();
          // Check to see whether the raw data window is open or not
          if (parent.isRawDataWindowOpen())
          {
            RawDataWindow rdw = parent.getRawDataWindow();
            rdw.addData(msg);
            rdw = null;
          }
          // Send the string to the formatter
          MsgFormatter mf = new MsgFormatter(msg, parent);
          mf = null;
          msg = null;
          System.gc();
          // Turn the little status light off
          parent.turnStatusOff();
          // Pause a moment to allow the icon to be updated
          Thread.sleep(10);
          // Quickly check to see if the COMPortClient window is still open
          boolean bClosed, bVisible;
          bClosed = parent.isClosed();
          bVisible = parent.isVisible();
          if (bClosed || !bVisible)
          {
            // If it is then we don't need to keep processing
            parent.stopThread();
          }
          // Pause briefly to allow for the system to catch-up
          Thread.sleep(TEST_PORT_THREAD_DELAY);
        }
        if (!getShouldRun())
        {
          break;
        }
      }
      log.finest("PortReader closing");
    }
    catch (IOException ioX)
    {
      System.err.println("IOException while reading data from the test port. See logs for more information.");
      log.warning("I/O problem while accessing the COM port.");
      log.throwing("PortReader", "runTestPort()", ioX);
    }
    catch (InterruptedException iX)
    {
      System.err.println("InterruptedException while reading data from the test port. See logs for more information.");
      log.warning("The thread was interrupted while accessing the COM port.");
      log.throwing("PortReader", "runTestPort()", iX);
    }
    catch (Exception x)
    {
      System.err.println("A general exception occurred while accessing data from the test port. See logs for more information.");
      log.throwing("PortReader", "runTestPort()", x);
    }
  }

  /**
   * Returns the constant value for the specified field value.
   * @return The constant value to be used for interaction with RxTx
   */
  private int getData()
  {
    int r = 0;
    switch (dataBits)
    {
      case 5:
      {
        r = SerialPort.DATABITS_5;
        break;
      }
      case 6:
      {
        r = SerialPort.DATABITS_6;
        break;
      }
      case 7:
      {
        r = SerialPort.DATABITS_7;
        break;
      }
      default:
      {
        r = SerialPort.DATABITS_8;
        break;
      }
    }
    log.finest("Returning data bits size of " + dataBits);
    return r;
  }

  /**
   * Returns the constant value for the specified field value.
   * @return The constant value to be used for interaction with RxTx
   */
  private int getParity()
  {
    int r = SerialPort.PARITY_NONE;
    if (parityType.equals("Even"))
    {
      r = SerialPort.PARITY_EVEN;
    }
    if (parityType.equals("Odd"))
    {
      r = SerialPort.PARITY_ODD;
    }
    if (parityType.equals("Mark"))
    {
      r = SerialPort.PARITY_MARK;
    }
    // None is already set so we don't have to explicitly code it again
    log.finest("Parity is set to " + parityType);
    return r;
  }

  /**
   * Returns the constant value for the specified field value.
   * @return The constant value to be used for interaction with RxTx
   */
  private int getStop()
  {
    int r = SerialPort.STOPBITS_1;
    if (stopBits == 1.5)
    {
      r = SerialPort.STOPBITS_1_5;
    }
    if (stopBits == 2)
    {
      r = SerialPort.STOPBITS_2;
    }
    if (stopBits == 1)
    {
      r = SerialPort.STOPBITS_1;
    }
    log.finest("Returning stop bit length of " + stopBits);
    return r;
  }

  /**
   * Verifies and returns a supported value for the baud rate to be used for the
   * COM port. If the value originally supplied for use with the COM port cannot
   * be supported then the value is changed to a default on of 9600bps.
   * @return The baud rate supported on RxTx with the COM port
   */
  private int getBaud()
  {
    switch (baudRate)
    {
      case 110:
      {
        // This is a valid value, do nothing to amend it
        break;
      }
      case 300:
      {
        // This is a valid value, do nothing to amend it
        break;
      }
      case 600:
      {
        // This is a valid value, do nothing to amend it
        break;
      }
      case 1200:
      {
        // This is a valid value, do nothing to amend it
        break;
      }
      case 2400:
      {
        // This is a valid value, do nothing to amend it
        break;
      }
      case 4800:
      {
        // This is a valid value, do nothing to amend it
        break;
      }
      case 9600:
      {
        // This is a valid value, do nothing to amend it
        break;
      }
      case 14400:
      {
        // This is a valid value, do nothing to amend it
        break;
      }
      case 19200:
      {
        // This is a valid value, do nothing to amend it
        break;
      }
      case 38400:
      {
        // This is a valid value, do nothing to amend it
        break;
      }
      case 57600:
      {
        // This is a valid value, do nothing to amend it
        break;
      }
      case 115200:
      {
        // This is a valid value, do nothing to amend it
        break;
      }
      default:
      {
        // This isn't a valid value. We'll change it to a default value of 9600
        baudRate = 9600;
        break;
      }
    }
    return baudRate;
  }

  /**
   * Checks whether the monitoring loops should continue to execute.
   * @return The boolean value of whether iteration should continue.
   */
  private boolean getShouldRun()
  {
    return shouldRun;
  }

  /**
   * Sets the value affecting the monitoring loops.
   * @param continueRunning The new value for whether the threads should monitor
   * the designated COM port.
   */
  public void setShouldRun(boolean continueRunning)
  {
    shouldRun = continueRunning;
  }
}