/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package groupthinhquan.hospitalmanagement.gui;

import java.awt.CardLayout;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

/**
 *
 * @author Thinh
 */

public class AboutUsPanel extends javax.swing.JPanel {
    //Custom variables
    private MainFrame frame;
    private String title;
    
    /**
     * Creates new form AboutUsPanel
     */
    public AboutUsPanel(MainFrame frame, String mainTitle) {
        initComponents();
        
        initCustomVar(frame, mainTitle);
        preset();
    }
    
    private void initCustomVar(MainFrame frame, String mainTitle) {
        this.frame = frame;
        title = mainTitle;
    }
    
    private void preset() {
        // Set Shortcut Key for Back button: ESC
        String backActionName = "back";
        Action backAction = new AbstractAction(){
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutUsBackButtonActionPerformed(evt);
            }
        };
        aboutUsBackButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), backActionName);
        aboutUsBackButton.getActionMap().put(backActionName, backAction);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        aboutUsPanel = new javax.swing.JPanel();
        aboutUsBodyPanel = new javax.swing.JPanel();
        aboutUsIntroduceLabel = new javax.swing.JLabel();
        aboutUsIntermediateLabel = new javax.swing.JLabel();
        aboutUsNameLabel1 = new javax.swing.JLabel();
        aboutUsNameLabel2 = new javax.swing.JLabel();
        aboutUsBackButton = new javax.swing.JButton();

        aboutUsPanel.setPreferredSize(new java.awt.Dimension(782, 573));

        aboutUsIntroduceLabel.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        aboutUsIntroduceLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        aboutUsIntroduceLabel.setText("This is an application belonging to a project in Java course");

        aboutUsIntermediateLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        aboutUsIntermediateLabel.setText("Group Member:");

        aboutUsNameLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        aboutUsNameLabel1.setText("TRUONG PHUC THINH");
        aboutUsNameLabel1.setToolTipText("");

        aboutUsNameLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        aboutUsNameLabel2.setText("TRAN TRUNG QUAN");

        javax.swing.GroupLayout aboutUsBodyPanelLayout = new javax.swing.GroupLayout(aboutUsBodyPanel);
        aboutUsBodyPanel.setLayout(aboutUsBodyPanelLayout);
        aboutUsBodyPanelLayout.setHorizontalGroup(
            aboutUsBodyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, aboutUsBodyPanelLayout.createSequentialGroup()
                .addContainerGap(151, Short.MAX_VALUE)
                .addGroup(aboutUsBodyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(aboutUsNameLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(aboutUsIntroduceLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(aboutUsIntermediateLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(aboutUsNameLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 703, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(141, 141, 141))
        );
        aboutUsBodyPanelLayout.setVerticalGroup(
            aboutUsBodyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(aboutUsBodyPanelLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(aboutUsIntroduceLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(aboutUsIntermediateLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(aboutUsNameLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(aboutUsNameLabel2)
                .addContainerGap(47, Short.MAX_VALUE))
        );

        aboutUsBackButton.setText("Back");
        aboutUsBackButton.setToolTipText("Click to go back to main menu <Esc>");
        aboutUsBackButton.setFocusPainted(false);
        aboutUsBackButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutUsBackButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout aboutUsPanelLayout = new javax.swing.GroupLayout(aboutUsPanel);
        aboutUsPanel.setLayout(aboutUsPanelLayout);
        aboutUsPanelLayout.setHorizontalGroup(
            aboutUsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(aboutUsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(aboutUsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(aboutUsPanelLayout.createSequentialGroup()
                        .addComponent(aboutUsBackButton, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(aboutUsBodyPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        aboutUsPanelLayout.setVerticalGroup(
            aboutUsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(aboutUsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(aboutUsBackButton, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(167, 167, 167)
                .addComponent(aboutUsBodyPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(246, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(aboutUsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 1007, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(aboutUsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 656, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void aboutUsBackButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutUsBackButtonActionPerformed
        javax.swing.JPanel backgroundPanel = frame.getBackgroundPanel();
        CardLayout layout = (CardLayout) backgroundPanel.getLayout();
        layout.show(backgroundPanel, title);
    }//GEN-LAST:event_aboutUsBackButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton aboutUsBackButton;
    private javax.swing.JPanel aboutUsBodyPanel;
    private javax.swing.JLabel aboutUsIntermediateLabel;
    private javax.swing.JLabel aboutUsIntroduceLabel;
    private javax.swing.JLabel aboutUsNameLabel1;
    private javax.swing.JLabel aboutUsNameLabel2;
    private javax.swing.JPanel aboutUsPanel;
    // End of variables declaration//GEN-END:variables
}
