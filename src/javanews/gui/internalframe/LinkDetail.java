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

/* Class name: LinkDetail
 * File name:  LinkDetail.java
 * Project:    JavaNews - PSE Monitoring
 * Copyright:  © 2007-2009 Alexander J. Harris, all rights reserved
 * Created:    09-Mar-2009 22:05:13
 * Modified:   17-Jun-2009
 *
 * Version History:
 * ~ ~ ~ ~ ~ ~ ~ ~ ~
 * 1.000  17-Jun-2009 Code finalised and released.
 * 0.003  14-Jun-2009 Added support for the user to add and edit links, both
 *        discovered and recognised.
 * 0.002  25-May-2009 Changed buttons on the form and moved editing capacities to
 *        another form making this class read-only.
 * 0.001  09-Mar-2009 Initial build
 */

package javanews.gui.internalframe;
import java.awt.event.ActionListener;
import javanews.database.DbInterface;
import javanews.gui.*;
import javanews.object.*;
import javanews.object.table.*;
import java.awt.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.*;

/**
 * This class is responsible for providing detailed information on a specific link.
 * <p>This class was created using the GUI builder in NetBeans 6.5.
 * @version 1.000
 * @author Alex Harris (email: Alexander.J.Harris(at)btinternet.com)
 */
public class LinkDetail extends JInternalFrame implements ActionListener
{
  /** The unique name of the logger within the application */
  private static final String className = "javanews.gui.internalframe.LinkDetail";
  /** The logger to be used for writing messages to the application log */
  private Logger log;
  /** The name of the owning COM port */
  private String port;
  /** The name of the PSE */
  private String pse;
  /** The id of the link */
  private String link;
  /** The connection to the back-end database */
  private DbInterface dbI;
  /** The parent window */
  private COMPortClient parent;
  /** Constant value for a link state of up */
  private static final int LINK_STATE_UP = 1;
  /** Constant value for a link state of down */
  private static final int LINK_STATE_DOWN = 0;
  /** The type of link being used with the displayed window */
  private String type;
  /** A constant value used to represent the successful execution of a command or subroutine */
  private final int WORKED_OK = 1;
  /** A constant value used to represent the event that an error has been encountered during the execution of a method */
  private final int ERROR_ENCOUNTERED = 4;

  /**
   * Creates new form LinkDetail
   * @param comPort The com port the form is related to
   * @param pseName The pse name the form is related to
   * @param linkID The link id on the pse that the form is related to
   * @param linkType The type of link that this is attached to. Valid values are: known (default value) or discovered
   * @param cpcWindow The instance of COMPortClient that "owns" the window
   */
  public LinkDetail(String comPort, String pseName, String linkID, String linkType, COMPortClient cpcWindow)
  {
    super("Licensing Information", true, true, true, true);
    super.setLayer(1);
    log = LoggerFactory.getLogger(className);
    log.fine("LinkDetail form displayed for " + comPort + " : " + pseName + " : " + linkID);
    dbI = new DbInterface(comPort);
    port = comPort;
    pse = pseName;
    link = linkID;
    parent = cpcWindow;
    initComponents();
    setTitle(port + ": " + pse + " - " + link);
    jtComPort.setText(port);
    type = linkType;
    if (type.equals("discovered"))
    {
      jtPSE.setText(pse);
      jtLink.setText(link);
      jtDetails.setText("Not set: discovered link.");
      jtDescription.setText("Not set: discovered link.");
      Integer intState = ((LinkTable) parent.getNewLinkTable().getModel()).getLinkState(pse, link);
      Calendar updated = ((LinkTable) parent.getNewLinkTable().getModel()).getUpdatedDate(pse, link);
      switch (intState)
      {
        case LINK_STATE_DOWN: // Link down
        {
          jlStatus.setText("Link Down");
          jlStatus.setForeground(new Color(204,0,0));
          break;
        }
        case LINK_STATE_UP: // Link up
        {
          jlStatus.setText("Link Up");
          jlStatus.setForeground(new Color(51,204,0));
          break;
        }
        default: // Link status unknown
        {
          jlStatus.setText("Unknown");
          jlStatus.setForeground(Color.DARK_GRAY);
          break;
        }
      }
      jlTime.setText(updated.getTime().toString());
    }
    else
    {
      String sLinkID = dbI.getCodexLinkID(pseName, linkID);
      Vector vecDetails = dbI.lookupCodexLink(sLinkID);
      Object[] objInfo = (Object[]) vecDetails.get(0);
      jtPSE.setText((String) objInfo[0]);
      jtLink.setText((String) objInfo[1]);
      jtDetails.setText((String) objInfo[2]);
      jtDescription.setText((String) objInfo[3]);
      Integer intState = ((LinkTable) parent.getLinkTable().getModel()).getLinkState(pse, link);
      Calendar updated = ((LinkTable) parent.getLinkTable().getModel()).getUpdatedDate(pse, link);
      switch (intState)
      {
        case LINK_STATE_DOWN: // Link down
        {
          jlStatus.setText("Link Down");
          jlStatus.setForeground(new Color(204,0,0));
          break;
        }
        case LINK_STATE_UP: // Link up
        {
          jlStatus.setText("Link Up");
          jlStatus.setForeground(new Color(51,204,0));
          break;
        }
        default: // Link status unknown
        {
          jlStatus.setText("Unknown");
          jlStatus.setForeground(Color.DARK_GRAY);
          break;
        }
      }
      jlTime.setText(updated.getTime().toString());
    }
    Dimension dimMinSize = new Dimension();
    Dimension dimPrefSize = new Dimension();
    dimMinSize.setSize(400, 200);
    dimPrefSize.setSize(600,300);
    this.setMinimumSize(dimMinSize);
    this.setPreferredSize(dimPrefSize);
    this.setSize(600, 300);
    Client cParent = Client.getJNWindow();
    cParent.addToDesktop(this);
    this.setVisible(true);
  }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jbAmend = new javax.swing.JButton();
    jtComPort = new javax.swing.JTextField();
    jtPSE = new javax.swing.JTextField();
    jtLink = new javax.swing.JTextField();
    jtDetails = new javax.swing.JTextField();
    jtDescription = new javax.swing.JTextField();
    javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
    javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
    javax.swing.JLabel jLabel3 = new javax.swing.JLabel();
    javax.swing.JLabel jLabel4 = new javax.swing.JLabel();
    javax.swing.JLabel jLabel5 = new javax.swing.JLabel();
    javax.swing.JPanel jPanel1 = new javax.swing.JPanel();
    jlStatus = new javax.swing.JLabel();
    jlTime = new javax.swing.JLabel();

    setMaximumSize(new java.awt.Dimension(423, 212));
    setMinimumSize(new java.awt.Dimension(423, 212));

    jbAmend.setLabel("Add / Edit");
    jbAmend.addActionListener(this);

    jtComPort.setEditable(false);

    jtPSE.setEditable(false);

    jtLink.setEditable(false);

    jtDetails.setEditable(false);

    jtDescription.setEditable(false);

    jLabel1.setText("COM Port:");

    jLabel2.setText("PSE Node:");

    jLabel3.setText("Link ID:");

    jLabel4.setText("Detail:");

    jLabel5.setText("Description:");

    jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Current Status"));

    jlStatus.setFont(jlStatus.getFont().deriveFont(jlStatus.getFont().getStyle() | java.awt.Font.BOLD, jlStatus.getFont().getSize()+2));
    jlStatus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    jlStatus.setText("Status");

    jlTime.setFont(jlTime.getFont().deriveFont((jlTime.getFont().getStyle() | java.awt.Font.ITALIC), jlTime.getFont().getSize()-2));
    jlTime.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    jlTime.setText("Time");

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(jlTime, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
      .addComponent(jlStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
        .addComponent(jlStatus)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
        .addComponent(jlTime)
        .addContainerGap())
    );

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(jLabel3)
              .addComponent(jLabel2)
              .addComponent(jLabel1))
            .addGap(11, 11, 11)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
              .addComponent(jtLink, javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(jtPSE, javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(jtComPort, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
          .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(jLabel5)
              .addComponent(jLabel4))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(jtDetails, javax.swing.GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE)
              .addComponent(jtDescription, javax.swing.GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE)))
          .addComponent(jbAmend, javax.swing.GroupLayout.Alignment.TRAILING))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
          .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(jtComPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(jLabel1))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(jtPSE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(jLabel2))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(jtLink, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(jLabel3)))
          .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jtDetails, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel4))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jtDescription, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel5))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jbAmend)
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
    setBounds((screenSize.width-423)/2, (screenSize.height-208)/2, 423, 208);
  }

  // Code for dispatching events from components to event handlers.

  public void actionPerformed(java.awt.event.ActionEvent evt) {
    if (evt.getSource() == jbAmend) {
      LinkDetail.this.clickEdit(evt);
    }
  }// </editor-fold>//GEN-END:initComponents

    /**
     * Used to handle events from the Add/Edit button on the screen and either
     * accept details and a description from the user before moving it into the
     * recognised link table and cascading changes through the tblReceivedMessages
     * table. It will also add the newly recognised link to the reports tree.
     * <p>The other option for already recognised links is to present the user
     * with a choice of adding a new link or editing the details of the existing
     * record.
     * @param evt The event generated by user-interaction with the GUI.
     */
  private void clickEdit(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clickEdit
      String newLinkID = UUID.randomUUID().toString();
      if (type.equals("discovered"))
      {
        String currentID = dbI.findDiscoveredLinkID(pse, link);
        /*
         * We need to:
         *   - Get the missing information from the user
         *   - Write the information to the recognised links table
         *   - Update received messages with the new link ID and remove the old one
         *   - Remove the old link from the discovered table
         *   - Update the GUI
         *   - Update the link stats
         */
        String details = JOptionPane.showInputDialog(parent, "Please enter any details about this link.\nLimit 255 characters including spaces.", port + ": " + pse + "-" + link, JOptionPane.PLAIN_MESSAGE);
        if ((details.equals("")) || details == null)
        {
          return;
        }
        String description = JOptionPane.showInputDialog(parent, "Please enter a description about this link.\nLimit 255 characters including spaces.", port + ": " + pse + "-" + link, JOptionPane.PLAIN_MESSAGE);
        if (description == null)
        {
          description = "";
        }
        int saveLinkResult = dbI.storeNewRecognisedLink(newLinkID, port, pse, link, details, description);
        if (saveLinkResult == WORKED_OK)
        {
          parent.migrateLink(currentID, newLinkID);
          Integer state = parent.removeDiscoveredLink(currentID);
          parent.addRecognisedLink(pse, link, state);
          log.finest("Migrated link: " + pse + "-" + link);
          this.setVisible(false);
        }
        else
        {
          log.finest("An error was detected while trying to store the details of a new link in the recognised links table.");
        }
      }
      else
      {
        String currentID = dbI.getCodexLinkID(pse, link);
        Object[] options = {"Add a new link", "Edit the current link"};
        int n = JOptionPane.showOptionDialog(parent, "What would you like to do?",
        port + ": " + pse + "-" + link, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
        null, options, options[0]);
        switch (n)
        {
          case 0: // Add a new link
          {
            String newPseName = JOptionPane.showInputDialog(parent, "Please enter the 8 character PSE name the link is attached to.", "New link on: " + port, JOptionPane.PLAIN_MESSAGE);
            String newLinkId = JOptionPane.showInputDialog(parent, "Please enter the link ID and protocol type (ie. X25-10).", port + ": " + newPseName, JOptionPane.PLAIN_MESSAGE);
            String details = JOptionPane.showInputDialog(parent, "Please enter any details about this link.\nLimit 255 characters including spaces.", port + ": " + newPseName + "-" + newLinkId, JOptionPane.PLAIN_MESSAGE);
            String description = JOptionPane.showInputDialog(parent, "Please enter a description about this link.\nLimit 255 characters including spaces.", port + ": " + newPseName + "-" + newLinkId, JOptionPane.PLAIN_MESSAGE);
            int saveLinkResult = dbI.storeNewRecognisedLink(newLinkID, port, newPseName, newLinkId, details, description);
            if (saveLinkResult == WORKED_OK)
            {
              parent.addRecognisedLink(newPseName, newLinkId, 2);
              jtPSE.setText(newPseName);
              jtLink.setText(newLinkId);
              jtDetails.setText(details);
              jtDescription.setText(description);
            }
            else
            {
              log.finest("An error was detected while trying to store the new link in the recognised links table.");
            }
            break;
          }
          case 1: // Edit the current link
          {
            String details = JOptionPane.showInputDialog(parent, "Please enter any details about this link.\nLimit 255 characters including spaces.", port + ": " + pse + "-" + link, JOptionPane.PLAIN_MESSAGE);
            String description = JOptionPane.showInputDialog(parent, "Please enter a description about this link.\nLimit 255 characters including spaces.", port + ": " + pse + "-" + link, JOptionPane.PLAIN_MESSAGE);
            int result = dbI.updateDetailsAndDescription(currentID, details, description);
            if (result == WORKED_OK)
            {
              jtDetails.setText(details);
              jtDescription.setText(description);
            }
            break;
          }
          default:
          {
            // Computer says WTF!
            log.warning("Code has entered a trap because the value of n is unexpected: " + n);
          }
        }
      }
}//GEN-LAST:event_clickEdit


  // Variables declaration - do not modify//GEN-BEGIN:variables
  javax.swing.JButton jbAmend;
  javax.swing.JLabel jlStatus;
  javax.swing.JLabel jlTime;
  javax.swing.JTextField jtComPort;
  javax.swing.JTextField jtDescription;
  javax.swing.JTextField jtDetails;
  javax.swing.JTextField jtLink;
  javax.swing.JTextField jtPSE;
  // End of variables declaration//GEN-END:variables

}