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

/* Class name: GenericTable
 * File name:  GenericTable.java
 * Project:    JavaNews - PSE Monitoring
 * Copyright:  © 2007-2009 Alexander J. Harris, all rights reserved
 * Created:    19-Dec-2008 21:02:04
 * Modified:   17-Jun-2009
 *
 * Version History:
 * ~ ~ ~ ~ ~ ~ ~ ~ ~
 * 1.000  17-Jun-2009 Code finalised and released.
 * 0.006  07-Jun-2009 Changed logging within the class to prevent excessive amounts
 *        of logs from being generated. If all methods within the class contain a single
 *        line of code outputting to the logger then in a minute there will be over 250,000 entries in
 *        the log file!!!
 *        The following methods no longer have logging data:
 *          - getColumnClass
 *          - getColumnCount
 *          - getRowCount
 *          - getValueAt
 * 0.005  20-Jan-2009 Changed notification methods sent from this class to others. this.fire events have
 *                    been changed to better suit the nature of the event in the method.
 * 0.004  19-Jan-2009 Added getColumns(), getRows() and getRow(int) methods to the class
 * 0.003  13-Jan-2009 Added removeRow(), setEditableColumns() and isCellEditable() methods to the class
 * 0.002  12-Jan-2009 Added JavaDoc to the class and also amended messages to the log
 *        to show the values being returned rather than just indicating that
 *        execution has entered the method.
 * 0.001  19-Dec-2008 Initial build
 */

package javanews.object.table;
import javanews.object.LoggerFactory;
import java.util.Vector;
import java.util.logging.*;
import javax.swing.table.AbstractTableModel;

/**
 * This class is used to contain data for tables. All tables used in this application use this class
 * for data storage and retrieval.
 * @version 1.000
 * @author Alex Harris (email: Alexander.J.Harris(at)btinternet.com)
 */
public class GenericTable extends AbstractTableModel
{
  /** The list of column names for the table */
  private Vector columnNames = new Vector();
  /** The list of row entries for the table */
  private Vector data = new Vector();
  /** The unique name of the logger within the application */
  private static final String className = "javanews.object.table.GenericTable";
  /** The logger to be used for writing messages to the application log */
  private static Logger log;
  /** The list of user-editable columns within the table */
  private int[] editableCells;

  /**
   * Creates a new instance of the class and initialises the table with some default data values.
   * These default values are in a one column Vector with five rows (New Data To Be Displayed Here #n).
   * These can be used if needed for testing but should be overwritten within the application on first use.
   * Therefore the first method called on a new instance of this class must be <code>setData()</code>
   * rather than <code>addRow()</code>.
   * @see java.util.Vector
   * @see #addRow(java.lang.Object[])
   * @see #setData(java.util.Vector)
   * @see #setData(java.util.Vector, java.util.Vector)
   */
  public GenericTable()
  {
    log = LoggerFactory.getLogger(className);
    log.finer("Building default table");
    columnNames.add("Default Table");
    data.add(new Object[] {"New Data To Be Displayed Here #1"});
    data.add(new Object[] {"New Data To Be Displayed Here #2"});
    data.add(new Object[] {"New Data To Be Displayed Here #3"});
    data.add(new Object[] {"New Data To Be Displayed Here #4"});
    data.add(new Object[] {"New Data To Be Displayed Here #5"});
  }

  /**
   * Returns the number of columns stored within the table.
   * <p><i>Inherited method from the AbstractTableModel class.</i></p>
   * @return The number of columns in the table
   */
  public int getColumnCount()
  {
    return columnNames.size();
  }

  /**
   * Returns the number of rows stored within the table.
   * <p><i>Inherited method from the AbstractTableModel class.</i></p>
   * @return The number of rows in the table
   */
  public int getRowCount()
  {
    return data.size();
  }

  /**
   * Returns the name of a column given its index in the table.
   * <p><i>Inherited method from the AbstractTableModel class.</i></p>
   * @param col The column index
   * @return The column name
   */
  public String getColumnName(int col)
  {
    String name = (String) columnNames.get(col);
    log.finest("Returning value: " + name);
    return name;
  }

  /**
   * Returns the object stored within the table at the position denoted by the
   * row and column index.
   * <p><i>Inherited method from the AbstractTableModel class.</i></p>
   * @param row The row index of the object to be returned
   * @param col The column index of the object to be returned
   * @return The object requested
   */
  public Object getValueAt(int row, int col)
  {
    Object[] temp = (Object[]) data.get(row);
    if (col >= temp.length)
    {
      return null;
    }
    else
    {
      return temp[col];
    }
  }

  /**
   * Returns the class of the column denoted by the aupplied index.
   * <p><i>Inherited method from the AbstractTableModel class.</i></p>
   * @param c The column index
   * @return The class of the column
   */
  public Class getColumnClass(int c)
  {
    return getValueAt(0, c).getClass();
  }

  /**
   * Sets the value of a specific entry in the table given by a row and column index.
   * <p><i>Overrides method from the AbstractTableModel class.</i></p>
   * @param value The new value to store
   * @param row The row index in the table
   * @param col The column index in the table
   */
  public void setValueAt(Object value, int row, int col)
  {
    Object[] temp = (Object[]) data.get(row);
    temp[col] = value;
    fireTableCellUpdated(row, col);
    log.log(Level.FINER, "Value at row " + row + ", column " + col + " has been changed.", value);
  }

  /**
   * Replaces the currently stored values in the table with the new arguments.
   * <p>The columns Vector must contain a list of Strings while the rows can
   * contain any object providing that the entries in one column match the type of
   * those above and below it in the table.
   * @param columns A Vector contain the names of the columns stored as Strings
   * @param rows A Vector containing an array of entries to be stored in the table rows
   * @see java.util.Vector
   */
  public void setData(Vector columns, Vector rows)
  {
    log.finer("Storing new data values and column names");
    columnNames = columns;
    data = rows;
    fireTableStructureChanged();
    if (data == null)
    {
      fireTableStructureChanged();
    }
    else
    {
      fireTableStructureChanged();
    }
  }

  /**
   * Replaces the currently held rows with the contents of the supplied argument.
   * <p>If the table has already been defined then this must have an equal number of
   * columns to the number of column headers though the types may vary from those
   * previously stored in the table.
   * @param rows The new table rows to be stored
   * @see java.util.Vector
   */
  public void setData(Vector rows)
  {
    log.finer("Storing new data values");
    data = rows;
    fireTableDataChanged();
  }

  /**
   * Adds a new row to the table containing the table rows. The supplied Object array
   * must be in the same order as those already within the table otherwise the entry won't
   * display correctly.
   * @param aNewRow The new row to be added
   */
  public void addRow(Object[] aNewRow)
  {
    log.log(Level.FINER, "Adding new row to the table", aNewRow);
    data.add(aNewRow);
    fireTableRowsInserted(0, data.size()-1);
  }

  /**
   * Used to remove a particular row from the table model.
   * @param x The row number to be removed.
   */
  public void removeRow(int x)
  {
    data.remove(x);
    fireTableRowsDeleted(x, x);
  }

  /**
   * Defines the columns within the table which are editable to those contained within the array.
   * If the table is completely uneditable then this method shouldn't be used or the argument
   * should be set to null or an empty array. The columns whose index is included in the array
   * is then set to editable. This means that if a whole table is set to editable then all indices
   * should be contained within the array from 0 to size-1. The entries may be non-sequential and unordered.
   * @param editableColumns
   */
  public void setEditableColumns(int[] editableColumns)
  {
    editableCells = editableColumns;
  }

  /**
   * Returns a boolean value representing whether the column can be edited or not.
   * @param row This is used internally by Java and not of importance to the functioning of the method.
   * @param col This is the (zero-based) column index that is being queried.
   * @return Whether or not the column contents can be edited.
   */
  public boolean isCellEditable(int row, int col)
  {
    boolean result = false;

    if (editableCells == null)
    {
      return false;
    }

    for (int i = 0; i < editableCells.length; i++)
    {
      if (col == editableCells[i])
      {
        result = true;
      }
    }

    return result;
  }

  /**
   * Used to return the vector containing all of the row data
   * @return The vector containing the Object[] arrays making up the table data
   */
  public Vector getRows()
  {
    return data;
  }

  /**
   * Used to return the headers of a table
   * @return The vector containing the headers for the table
   */
  public Vector getColumns()
  {
    return columnNames;
  }

  /**
   * Used to return a particular row within the table
   * @param x The row at position x to be returned
   * @return The row contents as an instance of Object[]
   */
  public Object[] getRow(int x)
  {
    return (Object[]) data.get(x);
  }
}