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

/* Class name: SaveData
 * File name:  SaveData.java
 * Project:    JavaNews - PSE Monitoring
 * Copyright:  © 2007-2009 Alexander J. Harris, all rights reserved
 * Created:    03-Mar-2009 10:34:17
 * Modified:   17-Jun-2009
 *
 * Version History:
 * ~ ~ ~ ~ ~ ~ ~ ~ ~
 * 1.000  17-Jun-2009 Code finalised and released.
 * 0.001  03-Mar-2009 Initial build
 */

package javanews.object.save;
import javanews.gui.Client;
import javanews.object.*;
import javanews.object.save.filters.CSVFilter;
import javanews.object.table.GenericTable;
import java.io.*;
import java.util.logging.*;
import javax.swing.*;

/**
 * This class is responsible for outputting data from a GenericTable to a CSV file.
 * @version 1.000
 * @author Alex Harris (email: Alexander.J.Harris(at)btinternet.com)
 */

public class SaveData {
  /** The unique name of the logger within the application */
  private static final String className = "javanews.object.save.SaveData";
  /** The logger to be used for writing messages to the application log */
  private Logger log;
  /** The table containing source data to be exported to the file */
  private GenericTable source;
  /** The identifying name of the owning COM port */
  private String owningPort;

  /**
   * Initialises the class and attaches an instance of GenericTable to it which will be later exported.
   * @param data The GenericTable that will be exported to a CSV file.
   */
  public SaveData(GenericTable data, String owner)
  {
    log = LoggerFactory.getLogger(className);
    source = data;
    owningPort = owner;
  }

  /**
   * Prompts the user for a location and name for the CSV file and then exports the contents of the GenericTable object to it.
   * @return Whether the method executed without encountering any errors.
   */
  public boolean exportData()
  {
    try
    {
      JFileChooser jfcSave = new JFileChooser();
      jfcSave.setDialogTitle("Save Data as CSV...");
      jfcSave.setFileFilter(new CSVFilter());
      jfcSave.setSelectedFile(new File("JN_Export_" + owningPort));
      int saveResult = jfcSave.showSaveDialog(Client.getJNWindow().getDesktopPane());
      if (saveResult == JFileChooser.APPROVE_OPTION)
      {
        File path = jfcSave.getSelectedFile();
        String file = path.toString();
        if (!file.endsWith(".csv"))
        {
          file = file + ".csv";
        }
        log.fine("Exporting data to file: " + file);
        PrintWriter pwFile = new PrintWriter(new BufferedWriter(new FileWriter(file)));
        JProgressBar jpbSaveProgress = new JProgressBar(0,source.getRowCount());
        JDialog jdProgress = new JDialog();
        jdProgress.add(jpbSaveProgress);
        jdProgress.setVisible(true);

        // Output the column names first
        String headers = new String();
        for (int a = 0; a < source.getColumnCount(); a++)
        {
          if (headers.equals(""))
          {
            headers = source.getColumnName(a);
          }
          else
          {
            headers = headers + "," + source.getColumnName(a);
          }
        }
        pwFile.println(headers); // Write the headers to the file
        pwFile.flush(); // Make certain that the headers are at the front of the file

        for (int b = 0; b < source.getRowCount(); b++)
        {
          jpbSaveProgress.setValue(b);
          String anEntry = "";
          for (int c = 0; c < source.getColumnCount(); c++)
          {
            if (anEntry.equals(""))
            {
              anEntry = source.getValueAt(b, c).toString();
            }
            else
            {
              anEntry = anEntry + "," + source.getValueAt(b, c).toString();
            }
          }
          pwFile.println(anEntry);
        }
        pwFile.flush();
        pwFile.close();
        log.fine("Finished exporting data");
        jdProgress.setVisible(false);
        jdProgress = null;
      }
      return true;
    }
    catch (IOException ioX)
    {
      System.err.println("An error has occurred while attempting to output data to a CSV file.");
      log.throwing(className, "exportData", ioX);
      return false;
    }
    catch (Exception x)
    {
      System.err.println("An error has occurred while attempting to output data to a CSV file.");
      log.throwing(className, "exportData", x);
      return false;
    }
  }
}