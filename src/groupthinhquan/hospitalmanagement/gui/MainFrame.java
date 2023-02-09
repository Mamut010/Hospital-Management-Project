/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package groupthinhquan.hospitalmanagement.gui;

import groupthinhquan.hospitalmanagement.util.DataManager;
import java.awt.CardLayout;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

/**
 *
 * @author Thinh
 */

public class MainFrame extends javax.swing.JFrame {
    private static final long serialVersionUID = 2L;
    private static final String DOCTOR_FILE_PATH = "data" + File.separatorChar + "Doctor List.csv";
    private static final String NURSE_FILE_PATH = "data" + File.separatorChar + "Nurse List.csv";
    private static final String PATIENT_FILE_PATH = "data" + File.separatorChar + "Patient List.csv";
    private static final String SCHEDULE_FILE_PATH = "data" + File.separatorChar + "Schedule List.csv";
    private static final String APPOINTMENT_FILE_PATH = "data" + File.separatorChar + "Appointment List.csv";
    private static final String DELIMITER = ",";
        
    public final static String TITLE = "Hospital Management System";
    
    private static enum PanelIndex {
        DB_INDEX, SCHEDULE_INDEX, APPOINTMENT_INDEX;
    }
    private final static int PANEL_INDEX_COUNT = PanelIndex.values().length;
    
    // Custom variables
    private boolean dataUpdated;
    private boolean[] saves;
    private DataManager dataManager;
    
    private final String mainPanelTitle = "mainPanel";
    private final String workingPanelTitle = "workingPanel";
    private final String aboutUsPanelTitle = "aboutUsPanel";
    
    private AboutUsPanel aboutUsPanel;
    private DBPanel DBPanel;
    private SchedulePanel schedulePanel;
    private AppointmentPanel appointmentPanel;

    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        initComponents();
        
        initCustomVar();
        preset();
    }
    
    private void initCustomVar() {
        dataUpdated = true;
        saves = new boolean[PANEL_INDEX_COUNT];
        for(int i = 0; i < PANEL_INDEX_COUNT; i++)
            saves[i] = true;
        
        dataManager = new DataManager(DELIMITER);
        preloadEmployeeData();
        preloadPatientData();
        preloadScheduleData();
        preloadAppointmentData();
        
        aboutUsPanel = new AboutUsPanel(this, mainPanelTitle);
        backgroundPanel.add(aboutUsPanel, aboutUsPanelTitle);
        
        DBPanel = new DBPanel(this, PanelIndex.DB_INDEX.ordinal());
        bodyPane.addTab("Database", DBPanel);
        
        schedulePanel = new SchedulePanel(this, PanelIndex.SCHEDULE_INDEX.ordinal());
        bodyPane.addTab("Schedule", schedulePanel);
        
        appointmentPanel = new AppointmentPanel(this, PanelIndex.APPOINTMENT_INDEX.ordinal());
        bodyPane.addTab("Appointment", appointmentPanel);
    }
    
    private void preset() {
        // Center Frame
        setLocationRelativeTo(null);
        
        // Set Shortcut Key for save button: CTRL+S
        String saveActionName = "save";
        Action saveAction = new AbstractAction(){
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                footerPanelSaveButtonActionPerformed(evt);
            }
        };
        footerPanelSaveButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK), saveActionName);
        footerPanelSaveButton.getActionMap().put(saveActionName, saveAction);
        
        // Set Shortcut Key for discard button: CTRL+Z
        String discardActionName = "discard";
        Action discardAction = new AbstractAction(){
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                footerPanelDiscardButtonActionPerformed(evt);
            }
        };
        footerPanelDiscardButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK), discardActionName);
        footerPanelDiscardButton.getActionMap().put(discardActionName, discardAction);
        
        // Set Shortcut Key for Back button: ESC
        String backActionName = "back";
        Action backAction = new AbstractAction(){
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                footerPanelBackButtonActionPerformed(evt);
            }
        };
        footerPanelBackButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), backActionName);
        footerPanelBackButton.getActionMap().put(backActionName, backAction);
    }
    
    private void closing() {
        WindowEvent wev = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
    }
    
    private void close() {
        boolean DBSaved = saves[PanelIndex.DB_INDEX.ordinal()];
        boolean scheduleSaved = saves[PanelIndex.SCHEDULE_INDEX.ordinal()];
        boolean appointmentSaved = saves[PanelIndex.APPOINTMENT_INDEX.ordinal()];
        
        if(DBSaved && scheduleSaved && appointmentSaved) {
            closing();
            System.exit(0);
        }
        else {
            if(!dataUpdated) {
                String[] choices = {"Save and exit", "Exit without saving", "Cancel"};
                String exitMsg = "Your changes have not been saved. Do you want to exit?";

                int choice = JOptionPane.showOptionDialog(null, exitMsg, TITLE,
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, choices, null);

                if(choice == JOptionPane.YES_OPTION) {
                    closing();

                    boolean success = true;
                    if(!DBSaved) {
                        success &= saveDBToFile();
                    }
                    if(!scheduleSaved) {
                        success &= saveSchedulesToFile();
                    }
                    if(!appointmentSaved) {
                        success &= saveAppointmentsToFile();
                    }

                    if(success) {
                        String confirmPrompt = "Data saved successfully";
                        JOptionPane.showMessageDialog(null, confirmPrompt, TITLE, JOptionPane.INFORMATION_MESSAGE);
                        System.exit(0);
                    }
                }
                else if (choice == JOptionPane.NO_OPTION){
                    closing();
                    System.exit(0);
                }
            }
            else {
                closing();
                
                if(!DBSaved) {
                    saveDBToFile();
                }
                if(!scheduleSaved) {
                    saveSchedulesToFile();
                }
                if(!appointmentSaved) {
                    saveAppointmentsToFile();
                }
                
                System.exit(0);
            }
        }
    }
    
    private boolean saveDBToFile() {
        boolean success = true;
        
        try {
            dataManager.saveDoctorsToFile(DOCTOR_FILE_PATH);
        }
        catch(IOException e) {
            makeBeepSound();
            JOptionPane.showMessageDialog(null, e.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);
            success = false;
        }
        try {
            dataManager.saveNursesToFile(NURSE_FILE_PATH);
        }
        catch(IOException e) {
            makeBeepSound();
            JOptionPane.showMessageDialog(null, e.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);
            success = false;
        }
        try {
            dataManager.savePatientsToFile(PATIENT_FILE_PATH);
        }
        catch(IOException e) {
            makeBeepSound();
            JOptionPane.showMessageDialog(null, e.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);
            success = false;
        }
        
        return success;
    }
    
    private boolean saveSchedulesToFile() {
        try {
            dataManager.saveSchedulesToFile(SCHEDULE_FILE_PATH);
            return true;
        }
        catch(IOException e) {
            makeBeepSound();
            JOptionPane.showMessageDialog(null, e.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    private boolean saveAppointmentsToFile() {
        try {
            dataManager.saveAppointmentsToFile(APPOINTMENT_FILE_PATH);
            return true;
        }
        catch(IOException e) {
            makeBeepSound();
            JOptionPane.showMessageDialog(null, e.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    private void preloadEmployeeData() {
        try {
            dataManager.loadDoctorsFromFile(DOCTOR_FILE_PATH);
        }
        catch(IOException e) {
            // Do nothing
        }
        catch(Exception e) {
            // Show error message
            makeBeepSound();
            JOptionPane.showMessageDialog(null, e.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);
        }
        
        try{
            dataManager.loadNursesFromFile(NURSE_FILE_PATH);
        }
        catch(IOException e) {
            // Do nothing
        }
        catch(Exception e) {
            // Show error message
            makeBeepSound();
            JOptionPane.showMessageDialog(null, e.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);
        }
        finally {
            try {
                dataManager.applyEmployeeChanges();
            }
            catch(Exception e) {
                makeBeepSound();
                JOptionPane.showMessageDialog(null, backupErrorMsg("employee"), TITLE, JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void preloadPatientData() {
        try {
            dataManager.loadPatientsFromFile(PATIENT_FILE_PATH);
        }
        catch(IOException e) {
            // Do nothing
        }
        catch(Exception e) {
            // Show error message
            makeBeepSound();
            JOptionPane.showMessageDialog(null, e.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);
        }
        finally {
            try {
                dataManager.applyPatientChanges();
            }
            catch(Exception e) {
                makeBeepSound();
                JOptionPane.showMessageDialog(null, backupErrorMsg("patient"), TITLE, JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void preloadScheduleData() {
        try {
            dataManager.loadSchedulesFromFile(SCHEDULE_FILE_PATH);
        }
        catch(IOException e) {
            // Do nothing
        }
        catch(Exception e) {
            // Show error message
            makeBeepSound();
            JOptionPane.showMessageDialog(null, e.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);
        }
        finally {
            try {
                dataManager.applyScheduleChanges();
            }
            catch(Exception e) {
                makeBeepSound();
                JOptionPane.showMessageDialog(null, backupErrorMsg("schedule"), TITLE, JOptionPane.ERROR_MESSAGE);
            }
        }
    }
        
    private void preloadAppointmentData() {
        try {
            dataManager.loadAppointmentsFromFile(APPOINTMENT_FILE_PATH);
        }
        catch(IOException e) {
            // Do nothing
        }
        catch(Exception e) {
            // Show error message
            makeBeepSound();
            JOptionPane.showMessageDialog(null, e.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);
        }
        finally {
            try {
                dataManager.applyAppointmentChanges();
            }
            catch(Exception e) {
                makeBeepSound();
                JOptionPane.showMessageDialog(null, backupErrorMsg("appointment"), TITLE, JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void disableFooterButtons() {
        footerPanelSaveButton.setEnabled(false);
        footerPanelDiscardButton.setEnabled(false);
    }
    
    private void enableFooterButtons() {
        footerPanelSaveButton.setEnabled(true);
        footerPanelDiscardButton.setEnabled(true);
    }

    private void updateScreen() {
        if(isSaved()) {
            disableFooterButtons();
        }
        else {
            enableFooterButtons();
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        backgroundPanel = new javax.swing.JPanel();
        mainPanel = new javax.swing.JPanel();
        mainButtonPanel = new javax.swing.JPanel();
        aboutUsButton = new javax.swing.JButton();
        mainDBButton = new javax.swing.JButton();
        mainScheduleButton = new javax.swing.JButton();
        mainAppointmentButton = new javax.swing.JButton();
        mainExitButton = new javax.swing.JButton();
        mainLabelPanel = new javax.swing.JPanel();
        mainLabel = new javax.swing.JLabel();
        workingPanel = new javax.swing.JPanel();
        footerPanel = new javax.swing.JPanel();
        footerPanelSaveButton = new javax.swing.JButton();
        footerPanelDiscardButton = new javax.swing.JButton();
        footerPanelBackButton = new javax.swing.JButton();
        bodyPane = new javax.swing.JTabbedPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(TITLE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        backgroundPanel.setLayout(new java.awt.CardLayout());

        aboutUsButton.setText("About Us");
        aboutUsButton.setFocusPainted(false);
        aboutUsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutUsButtonActionPerformed(evt);
            }
        });

        mainDBButton.setText("Database");
        mainDBButton.setFocusPainted(false);
        mainDBButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mainDBButtonActionPerformed(evt);
            }
        });

        mainScheduleButton.setText("Schedule");
        mainScheduleButton.setFocusPainted(false);
        mainScheduleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mainScheduleButtonActionPerformed(evt);
            }
        });

        mainAppointmentButton.setText("Appointment");
        mainAppointmentButton.setFocusPainted(false);
        mainAppointmentButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mainAppointmentButtonActionPerformed(evt);
            }
        });

        mainExitButton.setText("Exit");
        mainExitButton.setFocusPainted(false);
        mainExitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mainExitButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout mainButtonPanelLayout = new javax.swing.GroupLayout(mainButtonPanel);
        mainButtonPanel.setLayout(mainButtonPanelLayout);
        mainButtonPanelLayout.setHorizontalGroup(
            mainButtonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainButtonPanelLayout.createSequentialGroup()
                .addGap(416, 416, 416)
                .addGroup(mainButtonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(mainDBButton, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mainScheduleButton, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mainAppointmentButton, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(aboutUsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mainExitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        mainButtonPanelLayout.setVerticalGroup(
            mainButtonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainButtonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mainDBButton, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(mainScheduleButton, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23)
                .addComponent(mainAppointmentButton, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addComponent(aboutUsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(mainExitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(49, Short.MAX_VALUE))
        );

        mainButtonPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {aboutUsButton, mainAppointmentButton, mainDBButton, mainExitButton, mainScheduleButton});

        mainLabel.setFont(new java.awt.Font("Dialog", 1, 36)); // NOI18N
        mainLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        mainLabel.setText("HOSPITAL MANAGEMENT SYSTEM");

        javax.swing.GroupLayout mainLabelPanelLayout = new javax.swing.GroupLayout(mainLabelPanel);
        mainLabelPanel.setLayout(mainLabelPanelLayout);
        mainLabelPanelLayout.setHorizontalGroup(
            mainLabelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1046, Short.MAX_VALUE)
        );
        mainLabelPanelLayout.setVerticalGroup(
            mainLabelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mainLabelPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(mainButtonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mainLabelPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(mainButtonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        backgroundPanel.add(mainPanel, "mainPanel");

        workingPanel.setPreferredSize(new java.awt.Dimension(1047, 682));

        footerPanelSaveButton.setText("Save");
        footerPanelSaveButton.setToolTipText("Click to save all changes <Ctrl+S>");
        footerPanelSaveButton.setEnabled(false);
        footerPanelSaveButton.setFocusPainted(false);
        footerPanelSaveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                footerPanelSaveButtonActionPerformed(evt);
            }
        });

        footerPanelDiscardButton.setText("Discard");
        footerPanelDiscardButton.setToolTipText("Click to discard all unsaved changes <Ctrl+Z>\n");
        footerPanelDiscardButton.setEnabled(false);
        footerPanelDiscardButton.setFocusPainted(false);
        footerPanelDiscardButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                footerPanelDiscardButtonActionPerformed(evt);
            }
        });

        footerPanelBackButton.setText("Back");
        footerPanelBackButton.setToolTipText("Click to go back to main menu <Esc>");
        footerPanelBackButton.setFocusPainted(false);
        footerPanelBackButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                footerPanelBackButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout footerPanelLayout = new javax.swing.GroupLayout(footerPanel);
        footerPanel.setLayout(footerPanelLayout);
        footerPanelLayout.setHorizontalGroup(
            footerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, footerPanelLayout.createSequentialGroup()
                .addComponent(footerPanelBackButton, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(footerPanelDiscardButton, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(footerPanelSaveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        footerPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {footerPanelDiscardButton, footerPanelSaveButton});

        footerPanelLayout.setVerticalGroup(
            footerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(footerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(footerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(footerPanelDiscardButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(footerPanelSaveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(footerPanelBackButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        footerPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {footerPanelDiscardButton, footerPanelSaveButton});

        javax.swing.GroupLayout workingPanelLayout = new javax.swing.GroupLayout(workingPanel);
        workingPanel.setLayout(workingPanelLayout);
        workingPanelLayout.setHorizontalGroup(
            workingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(workingPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(workingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(footerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(workingPanelLayout.createSequentialGroup()
                        .addComponent(bodyPane, javax.swing.GroupLayout.PREFERRED_SIZE, 1046, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        workingPanelLayout.setVerticalGroup(
            workingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, workingPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(bodyPane, javax.swing.GroupLayout.PREFERRED_SIZE, 662, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(footerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        backgroundPanel.add(workingPanel, "workingPanel");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(backgroundPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(backgroundPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void aboutUsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutUsButtonActionPerformed
        CardLayout layout = (CardLayout) backgroundPanel.getLayout();
        layout.show(backgroundPanel, aboutUsPanelTitle);
    }//GEN-LAST:event_aboutUsButtonActionPerformed

    private void mainDBButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mainDBButtonActionPerformed
        CardLayout layout = (CardLayout) backgroundPanel.getLayout();
        layout.show(backgroundPanel, workingPanelTitle);
        bodyPane.setSelectedIndex(PanelIndex.DB_INDEX.ordinal());
    }//GEN-LAST:event_mainDBButtonActionPerformed

    private void mainScheduleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mainScheduleButtonActionPerformed
        CardLayout layout = (CardLayout) backgroundPanel.getLayout();
        layout.show(backgroundPanel, workingPanelTitle);
        bodyPane.setSelectedIndex(PanelIndex.SCHEDULE_INDEX.ordinal());
    }//GEN-LAST:event_mainScheduleButtonActionPerformed

    private void mainAppointmentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mainAppointmentButtonActionPerformed
        CardLayout layout = (CardLayout) backgroundPanel.getLayout();
        layout.show(backgroundPanel, workingPanelTitle);
        bodyPane.setSelectedIndex(PanelIndex.APPOINTMENT_INDEX.ordinal());
    }//GEN-LAST:event_mainAppointmentButtonActionPerformed

    private void mainExitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mainExitButtonActionPerformed
        close();
    }//GEN-LAST:event_mainExitButtonActionPerformed

    private void footerPanelBackButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_footerPanelBackButtonActionPerformed
        CardLayout layout = (CardLayout) backgroundPanel.getLayout();
        layout.show(backgroundPanel, mainPanelTitle);
    }//GEN-LAST:event_footerPanelBackButtonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        close();
    }//GEN-LAST:event_formWindowClosing

    private void footerPanelSaveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_footerPanelSaveButtonActionPerformed
        boolean DBSaved = saves[PanelIndex.DB_INDEX.ordinal()];
        boolean scheduleSaved = saves[PanelIndex.SCHEDULE_INDEX.ordinal()];
        boolean appointmentSaved = saves[PanelIndex.APPOINTMENT_INDEX.ordinal()];
        if(DBSaved && scheduleSaved && appointmentSaved)
            return;
        
        String message = "Do you want to apply all changes?";
        int choice = JOptionPane.showConfirmDialog(null, message, TITLE, 
                                                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if(choice == JOptionPane.YES_OPTION) {
            boolean success = true;
            
            if(!DBSaved) {
                try {
                    boolean DBSuccess = true;
                    
                    dataManager.applyEmployeeChanges();
                    dataManager.applyPatientChanges();
                    
                    try {
                        dataManager.saveDoctorsToFile(DOCTOR_FILE_PATH);
                    }
                    catch(IOException e) {
                        success = false;
                        DBSuccess = false;
                        makeBeepSound();
                        JOptionPane.showMessageDialog(null, e.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);
                    }
                    
                    try {
                        dataManager.saveNursesToFile(NURSE_FILE_PATH);
                    }
                    catch(IOException e) {
                        success = false;
                        DBSuccess = false;
                        makeBeepSound();
                        JOptionPane.showMessageDialog(null, e.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);
                    }
                    
                    try {
                        dataManager.savePatientsToFile(PATIENT_FILE_PATH);
                    }
                    catch(IOException e ) {
                        success = false;
                        DBSuccess = false;
                        makeBeepSound();
                        JOptionPane.showMessageDialog(null, e.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);
                    }
                    
                    if(DBSuccess) {
                        saves[PanelIndex.DB_INDEX.ordinal()] = true;
                    }
                    
                    DBPanel.handleSaveAction();
                }
                catch(CloneNotSupportedException e) {
                    // This will never happen
                }
            }
            if(!scheduleSaved) {
                try {
                    boolean scheduleSuccess = true;
                    
                    dataManager.applyScheduleChanges();
                    
                    try {
                        dataManager.saveSchedulesToFile(SCHEDULE_FILE_PATH);
                        
                    }
                    catch(IOException e ) {
                        success = false;
                        scheduleSuccess = false;
                        makeBeepSound();
                        JOptionPane.showMessageDialog(null, e.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);
                    }
                    
                    if(scheduleSuccess) {
                        saves[PanelIndex.SCHEDULE_INDEX.ordinal()] = true;
                    }
                    
                    schedulePanel.handleSaveAction();
                }
                catch(CloneNotSupportedException e) {
                    // This will never happen
                }
            }
            if(!appointmentSaved) {
                try {
                    boolean appointmentSuccess = true;
                    
                    dataManager.applyAppointmentChanges();
                    
                    try {
                        dataManager.saveAppointmentsToFile(APPOINTMENT_FILE_PATH);
                    }
                    catch(IOException e ) {
                        success = false;
                        appointmentSuccess = false;
                        makeBeepSound();
                        JOptionPane.showMessageDialog(null, e.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);
                    }
                    
                    if(appointmentSuccess) {
                        saves[PanelIndex.APPOINTMENT_INDEX.ordinal()] = true;
                    }
                    
                    appointmentPanel.handleSaveAction();
                }
                catch(CloneNotSupportedException e) {
                    // This will never happen
                }
            }
            
            if(success) {
                message = "Data saved successfully!";
                JOptionPane.showMessageDialog(null, message, TITLE, JOptionPane.INFORMATION_MESSAGE);
                dataUpdated = true;
                disableFooterButtons();
            }
        }
    }//GEN-LAST:event_footerPanelSaveButtonActionPerformed

    private void footerPanelDiscardButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_footerPanelDiscardButtonActionPerformed
        boolean DBSaved = saves[PanelIndex.DB_INDEX.ordinal()];
        boolean scheduleSaved = saves[PanelIndex.SCHEDULE_INDEX.ordinal()];
        boolean appointmentSaved = saves[PanelIndex.APPOINTMENT_INDEX.ordinal()];
        if(DBSaved && scheduleSaved && appointmentSaved)
            return;
        
        String message = "Do you want to discard all changes?";
        int choice = JOptionPane.showConfirmDialog(null, message, TITLE, 
                                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if(choice == JOptionPane.YES_OPTION) {
            boolean success = true;
            
            if(!DBSaved) {
                try {
                    dataManager.discardEmployeeChanges();
                    dataManager.discardPatientChanges();
                    DBPanel.handleDiscardAction();
                    
                    saves[PanelIndex.DB_INDEX.ordinal()] = true;
                }
                catch(CloneNotSupportedException e) {
                    // This will never happen
                }
                catch(Exception e) {
                    success = false;
                    makeBeepSound();
                    JOptionPane.showMessageDialog(null, recoverErrorMsg("employee and patient"), TITLE, 
                            JOptionPane.ERROR_MESSAGE);
                }
            }
            if(!scheduleSaved) {
                try {
                    dataManager.discardScheduleChanges();
                    schedulePanel.handleDiscardAction();
                    
                    saves[PanelIndex.SCHEDULE_INDEX.ordinal()] = true;
                }
                catch(CloneNotSupportedException e) {
                    // This will never happen
                }
                catch(Exception e) {
                    success = false;
                    makeBeepSound();
                    JOptionPane.showMessageDialog(null, recoverErrorMsg("schedule"), TITLE, 
                            JOptionPane.ERROR_MESSAGE);
                }
            }
            if(!appointmentSaved) {
                try {
                    dataManager.discardAppointmentChanges();
                    appointmentPanel.handleDiscardAction();
                    
                    saves[PanelIndex.APPOINTMENT_INDEX.ordinal()] = true;
                }
                catch(CloneNotSupportedException e) {
                    // This will never happen
                }
                catch(Exception e) {
                    success = false;
                    makeBeepSound();
                    JOptionPane.showMessageDialog(null, recoverErrorMsg("appointment"), TITLE, 
                            JOptionPane.ERROR_MESSAGE);
                }
            }
            
            if(success) {
                message = "Data recovered successfully!";
                JOptionPane.showMessageDialog(null, message, TITLE, JOptionPane.INFORMATION_MESSAGE);
                dataUpdated = true;
                disableFooterButtons();
            }
        }
    }//GEN-LAST:event_footerPanelDiscardButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton aboutUsButton;
    private javax.swing.JPanel backgroundPanel;
    private javax.swing.JTabbedPane bodyPane;
    private javax.swing.JPanel footerPanel;
    private javax.swing.JButton footerPanelBackButton;
    private javax.swing.JButton footerPanelDiscardButton;
    private javax.swing.JButton footerPanelSaveButton;
    private javax.swing.JButton mainAppointmentButton;
    private javax.swing.JPanel mainButtonPanel;
    private javax.swing.JButton mainDBButton;
    private javax.swing.JButton mainExitButton;
    private javax.swing.JLabel mainLabel;
    private javax.swing.JPanel mainLabelPanel;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JButton mainScheduleButton;
    private javax.swing.JPanel workingPanel;
    // End of variables declaration//GEN-END:variables
    
    public JPanel getBackgroundPanel() {
        return backgroundPanel;
    }
    
    public DataManager getDataManager() {
        return dataManager;
    }

    public boolean isSaved() {
        for(boolean value : saves) {
            if(value == false) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isSaved(int index) throws IllegalArgumentException {
        if(index >= 0 && index < saves.length) {
            return saves[index];
        }
        else {
            throw new IllegalArgumentException();
        }
    }

    public void setSaved(int index, boolean state) throws IllegalArgumentException {
        if(index >= 0 && index < saves.length) {
            boolean oldState = saves[index];
            saves[index] = state;
            
            if(oldState != state) {
                dataUpdated = false;
                updateScreen();
            }
        }
        else {
            throw new IllegalArgumentException();
        }
    }
    
    private static String backupErrorMsg(String recordType) {
        return "An error occurred while trying to create backup data for " + recordType + " records!";
    }
    
    private static String recoverErrorMsg(String recordType) {
        return "An error occured while trying to recover " + recordType + " records!";
    }
    
    public static void makeBeepSound() {
        Toolkit.getDefaultToolkit().beep();
    }
}
