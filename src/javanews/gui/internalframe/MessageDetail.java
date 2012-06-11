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

/* Class name: MessageDetail
 * File name:  MessageDetail.java
 * Project:    JavaNews - PSE Monitoring
 * Copyright:  © 2007-2009 Alexander J. Harris, all rights reserved
 * Created:    17-Apr-2009 12:44:02
 * Modified:   17-Jun-2009
 *
 * Version History:
 * ~ ~ ~ ~ ~ ~ ~ ~ ~
 * 1.000  17-Jun-2009 Code finalised and released.
 * 0.003  07-Jun-2009 Added support for comments and showing the link details.
 * 0.002  26-May-2009 Changed buttons on the form and moved editing capacities to
 *        another form making this class read-only.
 * 0.001  17-Apr-2009 Initial build
 */

package javanews.gui.internalframe;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.logging.*;
import javanews.database.*;
import javanews.gui.*;
import javanews.object.*;
import javanews.object.table.*;
import javax.swing.*;

/**
 * This class is responsible for providing detailed information on a specific link.
 * <p>This class was created using the GUI builder in NetBeans 6.5.
 * @version 1.000
 * @author Alex Harris (email: Alexander.J.Harris(at)btinternet.com)
 */
public class MessageDetail extends javax.swing.JInternalFrame implements ActionListener
{
  /** The unique name of the logger within the application */
  private static final String className = "javanews.gui.internalframe.MessageDetail";
  /** The logger to be used for writing messages to the application log */
  private Logger log;
  /** The name of the owning COM port */
  private String port;
  /** The identifier of the message we're displaying the detail of */
  private String message;
  /** The link to the back-end database */
  private DbInterface dbI;
  /** The parent window */
  private COMPortClient parent;
  /** The type of PSE we're working with, recognised or discovered */
  private String pseType;
  /** The table of comments shown on the form */
  private GenericTable gtComments;
  /** The name of the PSE to display */
  private String pseName;
  /** The id of the link to display */
  private String linkID;
  /** Whether the comments table is empty or not */
  private boolean isCommentTableEmpty;

  /**
   * Constructor for the class
   * @param comPort
   * @param messageID
   * @param cpcParent
   */
  public MessageDetail(String comPort, String messageID, COMPortClient cpcParent)
  {
    super("Message Details for: " + messageID, true, true, true, true);
    super.setLayer(1);
    log = LoggerFactory.getLogger(className);
    initComponents();
    port = comPort;
    parent = cpcParent;
    pseType = "unknown";
    message = messageID;
    isCommentTableEmpty = false;
    dbI = new DbInterface(port);
    log.finest("Fetching details for message: " + message);
    Hashtable htMsg = dbI.getMessageDetails(message);
    jtfContents.setText((String) htMsg.get("FullMessage"));
    jtfPSEDate.setText((String) htMsg.get("AlarmDate"));
    jtfReceivedDate.setText((String) htMsg.get("ReceivedDate"));
    jtfSeverity.setText((String) htMsg.get("Severity"));
    String definition = "";
    definition = dbI.getMessageDefinition((String) htMsg.get("MessageID"));
    if (definition.equals(""))
    {
      jtfDefinition.setText("There is no definition stored within the database for this message.");
    }
    else
    {
      jtfDefinition.setText(definition);
    }
    String pseID = (String) htMsg.get("PSEId");
    String tempID = (String) htMsg.get("TempID");
    pseName = "";
    linkID = "";
    if ((!pseID.equals("")) && (tempID.equals("")))
    {
      Vector details = dbI.lookupCodexLink(pseID);
      Object[] objTemp = (Object[]) details.get(0);
      pseName = (String) objTemp[0];
      linkID = (String) objTemp[1];
      pseType = "recognised";
    }
    else if ((pseID.equals("")) && (!tempID.equals("")))
    {
      Hashtable<String,String> htDetails = dbI.getDiscoveredLinkDetails(tempID);
      pseName = htDetails.get("PSEName");
      linkID = htDetails.get("LinkID");
      pseType = "discovered";
    }
    else
    {
      // For some reason the message doesn't have either a known or discovered PSE link
      log.warning("Caught a message which doesn't have either a known or a discovered PSE Id: " + message);
    }
    jtfPSEName.setText(pseName);
    jtfLinkID.setText(linkID);

    Vector vecComments = dbI.getComments(message);
    if (vecComments.isEmpty())
    {
      vecComments.add(new Object[]{"No comments available.","",""});
      isCommentTableEmpty = true;
    }
    Vector vecColumns = new Vector();
    vecColumns.add("Owner");
    vecColumns.add("Added");
    vecColumns.add("Comment");
    gtComments = new GenericTable();
    gtComments.setData(vecColumns, vecComments);
    jtComments.setModel(gtComments);
    jtComments.repaint();

    jtfContents.setEditable(false);
    jtfDefinition.setEditable(false);
    jtfLinkID.setEditable(false);
    jtfPSEDate.setEditable(false);
    jtfPSEName.setEditable(false);
    jtfReceivedDate.setEditable(false);
    jtfSeverity.setEditable(false);
    setLayer(1);
    Client cParent = Client.getJNWindow();
    cParent.addToDesktop(this);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setVisible(true);
  }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    javax.swing.JPanel jpMessage = new javax.swing.JPanel();
    jtfContents = new javax.swing.JTextField();
    jtfDefinition = new javax.swing.JTextField();
    javax.swing.JLabel jlContents = new javax.swing.JLabel();
    javax.swing.JLabel jlDefinition = new javax.swing.JLabel();
    javax.swing.JPanel jpSender = new javax.swing.JPanel();
    javax.swing.JLabel jlSeverity = new javax.swing.JLabel();
    javax.swing.JLabel jlPSEName = new javax.swing.JLabel();
    javax.swing.JLabel jlLinkID = new javax.swing.JLabel();
    jtfSeverity = new javax.swing.JTextField();
    jtfPSEName = new javax.swing.JTextField();
    jbShowLink = new javax.swing.JButton();
    jtfLinkID = new javax.swing.JTextField();
    jtfReceivedDate = new javax.swing.JTextField();
    javax.swing.JLabel jlPSEDate = new javax.swing.JLabel();
    javax.swing.JLabel jlReceivedDate = new javax.swing.JLabel();
    jtfPSEDate = new javax.swing.JTextField();
    javax.swing.JPanel jpComments = new javax.swing.JPanel();
    javax.swing.JScrollPane jspComments = new javax.swing.JScrollPane();
    jtComments = new javax.swing.JTable();
    jbAddComment = new javax.swing.JButton();
    jlStatus = new javax.swing.JLabel();

    setMinimumSize(new java.awt.Dimension(611, 417));

    jpMessage.setBorder(javax.swing.BorderFactory.createTitledBorder("Message"));

    jlContents.setText("Contents");

    jlDefinition.setText("Definition");

    javax.swing.GroupLayout jpMessageLayout = new javax.swing.GroupLayout(jpMessage);
    jpMessage.setLayout(jpMessageLayout);
    jpMessageLayout.setHorizontalGroup(
      jpMessageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jpMessageLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jpMessageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
          .addComponent(jlDefinition, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(jlContents, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jpMessageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jtfDefinition, javax.swing.GroupLayout.DEFAULT_SIZE, 494, Short.MAX_VALUE)
          .addComponent(jtfContents, javax.swing.GroupLayout.DEFAULT_SIZE, 494, Short.MAX_VALUE))
        .addContainerGap())
    );
    jpMessageLayout.setVerticalGroup(
      jpMessageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jpMessageLayout.createSequentialGroup()
        .addGroup(jpMessageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jlContents)
          .addComponent(jtfContents, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jpMessageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jtfDefinition, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jlDefinition)))
    );

    jpSender.setBorder(javax.swing.BorderFactory.createTitledBorder("Sender Details"));

    jlSeverity.setText("Severity");

    jlPSEName.setText("PSE Name");

    jlLinkID.setText("Link ID");

    jbShowLink.setText("Show Link");
    jbShowLink.addActionListener(this);

    jlPSEDate.setText("PSE Date");

    jlReceivedDate.setText("Received");

    javax.swing.GroupLayout jpSenderLayout = new javax.swing.GroupLayout(jpSender);
    jpSender.setLayout(jpSenderLayout);
    jpSenderLayout.setHorizontalGroup(
      jpSenderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jpSenderLayout.createSequentialGroup()
        .addGap(10, 10, 10)
        .addGroup(jpSenderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
          .addComponent(jlPSEName)
          .addComponent(jlLinkID))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jpSenderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
          .addComponent(jbShowLink, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(jtfLinkID)
          .addComponent(jtfPSEName, javax.swing.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE))
        .addGap(49, 49, 49)
        .addGroup(jpSenderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
          .addComponent(jlSeverity)
          .addComponent(jlPSEDate)
          .addComponent(jlReceivedDate))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jpSenderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jtfReceivedDate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)
          .addComponent(jtfPSEDate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)
          .addComponent(jtfSeverity, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE))
        .addContainerGap())
    );
    jpSenderLayout.setVerticalGroup(
      jpSenderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jpSenderLayout.createSequentialGroup()
        .addGroup(jpSenderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jtfPSEName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jlPSEName)
          .addComponent(jlSeverity)
          .addComponent(jtfSeverity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jpSenderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jtfLinkID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jlLinkID)
          .addComponent(jtfPSEDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jlPSEDate))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jpSenderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jtfReceivedDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jbShowLink)
          .addComponent(jlReceivedDate)))
    );

    jpComments.setBorder(javax.swing.BorderFactory.createTitledBorder("Comments"));

    jtComments.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {
        {null, null, null, null},
        {null, null, null, null},
        {null, null, null, null},
        {null, null, null, null}
      },
      new String [] {
        "Title 1", "Title 2", "Title 3", "Title 4"
      }
    ));
    jspComments.setViewportView(jtComments);

    jbAddComment.setText("Add Comment");
    jbAddComment.addActionListener(this);

    javax.swing.GroupLayout jpCommentsLayout = new javax.swing.GroupLayout(jpComments);
    jpComments.setLayout(jpCommentsLayout);
    jpCommentsLayout.setHorizontalGroup(
      jpCommentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpCommentsLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jpCommentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
          .addGroup(jpCommentsLayout.createSequentialGroup()
            .addComponent(jlStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jbAddComment))
          .addComponent(jspComments, javax.swing.GroupLayout.DEFAULT_SIZE, 543, Short.MAX_VALUE))
        .addContainerGap())
    );
    jpCommentsLayout.setVerticalGroup(
      jpCommentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jpCommentsLayout.createSequentialGroup()
        .addComponent(jspComments, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addGroup(jpCommentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jbAddComment)
          .addComponent(jlStatus)))
    );

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
          .addComponent(jpComments, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(jpMessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(jpSender, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jpMessage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jpSender, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jpComments, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap(15, Short.MAX_VALUE))
    );

    pack();
  }

  // Code for dispatching events from components to event handlers.

  public void actionPerformed(java.awt.event.ActionEvent evt) {
    if (evt.getSource() == jbShowLink) {
      MessageDetail.this.showLink(evt);
    }
    else if (evt.getSource() == jbAddComment) {
      MessageDetail.this.addComment(evt);
    }
  }// </editor-fold>//GEN-END:initComponents

    private void showLink(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showLink
      if (pseType.equals("recognised"))
      {
        LinkDetail lD = new LinkDetail(port, pseName, linkID, "known", parent);
      }
      if (pseType.equals("discovered"))
      {
        LinkDetail lD = new LinkDetail(port, pseName, linkID, pseType, parent);
      }
    }//GEN-LAST:event_showLink

    private void addComment(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addComment
      final int WORKED_OK = 1;
      final int ERROR_ENCOUNTERED = 4;
      String sComment = (String)JOptionPane.showInputDialog(parent,
              "Please enter your comment.\n(Each comment must be less than 255 characters including spaces).",
              "JavaNews", JOptionPane.QUESTION_MESSAGE);
      int result = dbI.storeComment(message, sComment);
      switch (result)
      {
        case WORKED_OK:
        {
          if (isCommentTableEmpty)
          {
            Object[] oRow = new Object[]{System.getProperty("user.name"),Calendar.getInstance().getTime().toString(),sComment};
            Vector vecRows = new Vector();
            vecRows.add(oRow);
            gtComments.setData(vecRows);
            isCommentTableEmpty = false;
          }
          else
          {
            gtComments.addRow(new Object[]{System.getProperty("user.name"),Calendar.getInstance().getTime().toString(),sComment});
          }
          jlStatus.setText("Comment added at: " + Calendar.getInstance().getTime().toString());
          break;
        }
        case ERROR_ENCOUNTERED:
        {
          jlStatus.setText("An error was encountered while saving your comment to the database.");
          break;
        }
        default:
        {
          log.warning("Encountered an unexpected result code from DbInterface.storeComment(String,String): " + result);
        }
      }
    }//GEN-LAST:event_addComment


  // Variables declaration - do not modify//GEN-BEGIN:variables
  javax.swing.JButton jbAddComment;
  javax.swing.JButton jbShowLink;
  private javax.swing.JLabel jlStatus;
  javax.swing.JTable jtComments;
  javax.swing.JTextField jtfContents;
  javax.swing.JTextField jtfDefinition;
  javax.swing.JTextField jtfLinkID;
  javax.swing.JTextField jtfPSEDate;
  javax.swing.JTextField jtfPSEName;
  javax.swing.JTextField jtfReceivedDate;
  javax.swing.JTextField jtfSeverity;
  // End of variables declaration//GEN-END:variables

}