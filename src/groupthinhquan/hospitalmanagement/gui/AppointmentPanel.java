/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package groupthinhquan.hospitalmanagement.gui;

import groupthinhquan.hospitalmanagement.core.*;
import groupthinhquan.hospitalmanagement.util.*;
import groupthinhquan.hospitalmanagement.interaction.*;
import static groupthinhquan.hospitalmanagement.interaction.AppointmentSystem.AppointmentInfo;
import java.awt.event.KeyEvent;

import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Thinh
 */

public class AppointmentPanel extends javax.swing.JPanel implements SaveDiscardSupportPanel {
    private static enum ColumnHeader {
        APPOINTMENT_ID, DATE, TIME, DOCTOR_ID, PATIENT_ID, DETAIL
    }
    private static final int COLUMN_NUM = ColumnHeader.values().length;
    
    // Custom variables
    private MainFrame frame;
    private int panelIndex;
    private DataManager dataManager;
    
    private TableRowSorter appointmentTableSorter;
    private ArrayList<RowFilter<Object, Integer>> filters;
    
    /**
     * Creates new form AppointmentPanel
     * @param frame: the main frame
     * @param index: the index of this panel in the tabbed pane
     */
    public AppointmentPanel(MainFrame frame, int index) {
        initComponents();
        
        initCustomVar(frame, index);
        preset();
    }
    
    private void initCustomVar(MainFrame frame, int index) {
        this.frame = frame;
        panelIndex = index;
        dataManager = frame.getDataManager();
        
        filters = new ArrayList<>(COLUMN_NUM);
        for(int i = 0; i < COLUMN_NUM; i++)
            filters.add(RowFilter.regexFilter("", i));
            
        appointmentTableSorter = new TableRowSorter<>(appointmentTable.getModel());
        linkSorter(appointmentTableSorter, appointmentTable);
        
        loadAppointmentData();
    }
    
    private void preset() {
        renewUtilityButtons();
        TableRenderingSupporter.centerTableHeader(appointmentTable);
        
        appointmentAddDialog.pack();
        appointmentAddDialog.setLocationRelativeTo(null);
        setupAddPanelComponents();
        
        appointmentEditDialog.pack();
        appointmentEditDialog.setLocationRelativeTo(null);
        setupEditPanelComponents();
        
        // Set Shortcut Key for delete button: DEL
        String deleteActionName = "delete";
        Action deleteAction = new AbstractAction(){
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                appointmentDeleteButtonActionPerformed(evt);
            }
        };
        appointmentDeleteButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), deleteActionName);
        appointmentDeleteButton.getActionMap().put(deleteActionName, deleteAction);
    }
    
    private void linkSorter(final TableRowSorter sorter, JTable table) {
        table.setRowSorter(sorter);
        setupFilter(sorter, appointmentAppointmentIDTextField, ColumnHeader.APPOINTMENT_ID.ordinal());
        setupFilter(sorter, appointmentDoctorIDTextField, ColumnHeader.DOCTOR_ID.ordinal());
        setupFilter(sorter, appointmentPatientIDTextField, ColumnHeader.PATIENT_ID.ordinal());
        setupFilter(sorter, appointmentDateTextField, ColumnHeader.DATE.ordinal());
        setupFilter(sorter, appointmentTimeTextField, ColumnHeader.TIME.ordinal());
        setupFilter(sorter, appointmentDetailTextField, ColumnHeader.DETAIL.ordinal());
    }
    
    private void setupFilter(final TableRowSorter sorter, JTextField filterTextField, int columnIndice) {
        filterTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent evt) {
               search(filterTextField.getText());
            }
            
            @Override
            public void removeUpdate(DocumentEvent evt) {
               search(filterTextField.getText());
            }
            
            @Override
            public void changedUpdate(DocumentEvent evt) {
               search(filterTextField.getText());
            }
            
            public void search(String input) {
                try {
                    filters.set(columnIndice, RowFilter.regexFilter(input, columnIndice));
                    sorter.setRowFilter(RowFilter.andFilter(filters));
                }
                catch(Exception e) {
                    // Do nothing
                }
                finally {
                   if(appointmentTable.getSelectedRow() == -1) {
                        renewUtilityButtons();
                    }
                    else {
                        appointmentEditButton.setEnabled(true);
                        appointmentDeleteButton.setEnabled(true);
                    }
                }
            }
        });
    }
    
    private void loadAppointmentData() {
        AppointmentSystem appointmentSystem = dataManager.getAppointmentSystem();
        AppointmentSystem.AppointmentInfo[] infos = appointmentSystem.getAppointments();
        DefaultTableModel model = (DefaultTableModel) appointmentTable.getModel();
        model.setRowCount(0);
        for(var info : infos) {
            Appointment appointment = info.getAppointment();
            String appointmentID = appointment.getID();
            String date = appointment.getDate().toString();
            String time = appointment.getTime().toString();
            String doctorID = Integer.toString(info.getDoctorID());
            String patientID = Integer.toString(info.getPatientID());
            String detail = appointment.getDetail();
            
            model.addRow(new String[]{appointmentID, date, time, doctorID, patientID, detail});
        }
    }
    
    private void setupAddPanelComponents() {
        DateInputBoxSupporter.setupDateInputBoxes(appointmentAddDayBox, appointmentAddMonthBox, appointmentAddYearBox);
        TimeInputSpinnerSupporter.setupTimeInputSpinners(appointmentAddHourSpinner, appointmentAddMinSpinner);
        TextFieldFilteringSupporter.setPositiveIntegerFilter(appointmentAddDoctorIDTextField);
        TextFieldFilteringSupporter.setPositiveIntegerFilter(appointmentAddPatientIDTextField);
    }
    
    private void setupEditPanelComponents() {
        DateInputBoxSupporter.setupDateInputBoxes(appointmentEditDayBox, appointmentEditMonthBox, appointmentEditYearBox);
        TimeInputSpinnerSupporter.setupTimeInputSpinners(appointmentEditHourSpinner, appointmentEditMinSpinner);
        TextFieldFilteringSupporter.setPositiveIntegerFilter(appointmentEditDoctorIDTextField);
        TextFieldFilteringSupporter.setPositiveIntegerFilter(appointmentEditPatientIDTextField);
    }
    
    private void renewUtilityButtons() {
        appointmentAddButton.setEnabled(true);
        appointmentEditButton.setEnabled(false);
        appointmentDeleteButton.setEnabled(false);
    }

    private void clearFilterTextFields() {
        appointmentAppointmentIDTextField.setText("");
        appointmentDoctorIDTextField.setText("");
        appointmentPatientIDTextField.setText("");
        appointmentDateTextField.setText("");
        appointmentTimeTextField.setText("");
        appointmentDetailTextField.setText("");
    }
    
    private void resetView() {
        clearFilterTextFields();
        appointmentTable.clearSelection();
        renewUtilityButtons();
    }
    
    private void handleTableDoubleClick() {
        int selectedRow = appointmentTable.getSelectedRow();
        if(selectedRow != -1) {
            prepareAndOpenEditDialog((String) appointmentTable.getValueAt(selectedRow, 0));
        }
    }
    
    private void handleTableSingleClick() {
        if(appointmentTable.getSelectedRow() != -1) {
            appointmentEditButton.setEnabled(true);
            appointmentDeleteButton.setEnabled(true);
        }
        else {
            appointmentEditButton.setEnabled(false);
            appointmentDeleteButton.setEnabled(false);
        }
    }
    
    private void clearAddDialog() {
        appointmentAddAppointmentIDTextField.setText("");
        DateInputBoxSupporter.clearDateInputBoxes(appointmentAddDayBox, appointmentAddMonthBox, appointmentAddYearBox);
        TimeInputSpinnerSupporter.clearTimeInputSpinners(appointmentAddHourSpinner, appointmentAddMinSpinner);
        appointmentAddDoctorIDTextField.setText("");
        appointmentAddPatientIDTextField.setText("");
        appointmentAddDetailTextField.setText("");
    }
    
    private void clearAndHideAddDialog() {
        clearAddDialog();
        appointmentAddDialog.setVisible(false);
    }
    
    private void resetEditDialog(AppointmentInfo info) {
        Appointment appointment = info.getAppointment();
        String appointmentID = appointment.getID();
        Date date = appointment.getDate();
        Time time = appointment.getTime();
        int doctorID = info.getDoctorID();
        int patientID = info.getPatientID();
        String detail = appointment.getDetail();
        
        appointmentEditAppointmentIDTextField.setText(appointmentID);
        DateInputBoxSupporter.setDateInputBoxes(appointmentEditDayBox, appointmentEditMonthBox, appointmentEditYearBox, date);
        TimeInputSpinnerSupporter.setTimeInputSpinners(appointmentEditHourSpinner, appointmentEditMinSpinner, time);
        appointmentEditDoctorIDTextField.setText(Integer.toString(doctorID));
        appointmentEditPatientIDTextField.setText(Integer.toString(patientID));
        appointmentEditDetailTextField.setText(detail); 
    }
    
    private void prepareAndOpenEditDialog(String appointmentID) {
        AppointmentSystem appointmentSystem = dataManager.getAppointmentSystem();
        resetEditDialog(appointmentSystem.getAppointment(appointmentID));
        appointmentEditDialog.setVisible(true);
    }
    
    private void clearEditDialog() {
        appointmentEditAppointmentIDTextField.setText("");
        DateInputBoxSupporter.clearDateInputBoxes(appointmentEditDayBox, appointmentEditMonthBox, appointmentEditYearBox);
        TimeInputSpinnerSupporter.clearTimeInputSpinners(appointmentEditHourSpinner, appointmentEditMinSpinner);
        appointmentEditDoctorIDTextField.setText("");
        appointmentEditPatientIDTextField.setText("");
        appointmentEditDetailTextField.setText("");
    }
    
    private void clearAndHideEditDialog() {
        clearEditDialog();
        appointmentEditDialog.setVisible(false);
    }
    
    private void addRow(Object[] data) {
        int oldRowCount = appointmentTable.getRowCount();

        DefaultTableModel model = (DefaultTableModel) appointmentTable.getModel();
        model.addRow(data);
        
        int newRowCount = appointmentTable.getRowCount();

        if(newRowCount != oldRowCount) {
            TableRenderingSupporter.selectRowAndScrollTableToCell(appointmentTable, newRowCount - 1, 0);
        }

        String message = "Record added successfully!";
        JOptionPane.showMessageDialog(null, message, MainFrame.TITLE, JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        appointmentAddDialog = new javax.swing.JDialog();
        appointmentAddOptionPane = new JOptionPane(null, JOptionPane.PLAIN_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, new String[]{"OK", "Reset", "Cancel"});
        appointmentEditDialog = new javax.swing.JDialog();
        appointmentEditOptionPane = new JOptionPane(null, JOptionPane.PLAIN_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, new String[]{"OK", "Reset", "Cancel"});
        appointmentAddPanel = new javax.swing.JPanel();
        appointmentAddAppointmentIDLabel = new javax.swing.JLabel();
        appointmentAddDateLabel = new javax.swing.JLabel();
        appointmentAddTimeLabel = new javax.swing.JLabel();
        appointmentAddDoctorIDLabel = new javax.swing.JLabel();
        appointmentAddPatientIDLabel = new javax.swing.JLabel();
        appointmentAddDetailLabel = new javax.swing.JLabel();
        appointmentAddDayLabel = new javax.swing.JLabel();
        appointmentAddMonthLabel = new javax.swing.JLabel();
        appointmentAddYearLabel = new javax.swing.JLabel();
        appointmentAddHourLabel = new javax.swing.JLabel();
        appointmentAddMinLabel = new javax.swing.JLabel();
        appointmentAddAppointmentIDTextField = new javax.swing.JTextField();
        appointmentAddDayBox = new javax.swing.JComboBox<>();
        appointmentAddMonthBox = new javax.swing.JComboBox<>();
        appointmentAddYearBox = new javax.swing.JComboBox<>();
        appointmentAddHourSpinner = new javax.swing.JSpinner();
        appointmentAddMinSpinner = new javax.swing.JSpinner();
        appointmentAddDoctorIDTextField = new javax.swing.JTextField();
        appointmentAddPatientIDTextField = new javax.swing.JTextField();
        appointmentAddDetailTextField = new javax.swing.JTextField();
        appointmentAddRequiredLabel = new javax.swing.JLabel();
        appointmentAddAppointmentIDRequiredLabel = new javax.swing.JLabel();
        appointmentAddDateRequiredLabel = new javax.swing.JLabel();
        appointmentAddTimeRequiredLabel = new javax.swing.JLabel();
        appointmentAddDoctorIDRequiredLabel = new javax.swing.JLabel();
        appointmentAddPatientIDRequiredLabel = new javax.swing.JLabel();
        appointmentEditPanel = new javax.swing.JPanel();
        appointmentEditAppointmentIDLabel = new javax.swing.JLabel();
        appointmentEditDateLabel = new javax.swing.JLabel();
        appointmenEditTimeLabel = new javax.swing.JLabel();
        appointmentEditDoctorIDLabel = new javax.swing.JLabel();
        appointmentEditPatientIDLabel = new javax.swing.JLabel();
        appointmenEditDetailLabel = new javax.swing.JLabel();
        appointmentEditDayLabel = new javax.swing.JLabel();
        appointmentEditMonthLabel = new javax.swing.JLabel();
        appointmentEditYearLabel = new javax.swing.JLabel();
        appointmentEditHourLabel = new javax.swing.JLabel();
        appointmentEditMinLabel = new javax.swing.JLabel();
        appointmentEditAppointmentIDTextField = new javax.swing.JTextField();
        appointmentEditDayBox = new javax.swing.JComboBox<>();
        appointmentEditMonthBox = new javax.swing.JComboBox<>();
        appointmentEditYearBox = new javax.swing.JComboBox<>();
        appointmentEditHourSpinner = new javax.swing.JSpinner();
        appointmentEditMinSpinner = new javax.swing.JSpinner();
        appointmentEditDoctorIDTextField = new javax.swing.JTextField();
        appointmentEditPatientIDTextField = new javax.swing.JTextField();
        appointmentEditDetailTextField = new javax.swing.JTextField();
        appointmentEditRequiredLabel = new javax.swing.JLabel();
        appointmentEditDateRequiredLabel = new javax.swing.JLabel();
        appointmentEditTimeRequiredLabel = new javax.swing.JLabel();
        appointmentEditDoctorIDRequiredLabel = new javax.swing.JLabel();
        appointmentEditPatientIDRequiredLabel = new javax.swing.JLabel();
        appointmentPanel = new javax.swing.JPanel();
        appointmentFilterField = new javax.swing.JPanel();
        appointmentSearchingPromptButton = new javax.swing.JLabel();
        appointmentAppointmentIDLabel = new javax.swing.JLabel();
        appointmentDoctorIDLabel = new javax.swing.JLabel();
        appointmentPatientIDLabel = new javax.swing.JLabel();
        appointmentDateLabel = new javax.swing.JLabel();
        appointmentTimeLabel = new javax.swing.JLabel();
        appointmentDetailLabel = new javax.swing.JLabel();
        appointmentAppointmentIDTextField = new javax.swing.JTextField();
        appointmentDoctorIDTextField = new javax.swing.JTextField();
        appointmentPatientIDTextField = new javax.swing.JTextField();
        appointmentDateTextField = new javax.swing.JTextField();
        appointmentTimeTextField = new javax.swing.JTextField();
        appointmentDetailTextField = new javax.swing.JTextField();
        appointmentUtilityField = new javax.swing.JPanel();
        appointmentAddButton = new javax.swing.JButton();
        appointmentEditButton = new javax.swing.JButton();
        appointmentDeleteButton = new javax.swing.JButton();
        appointmentTableField = new javax.swing.JPanel();
        appointmentTableScrollPanel = new javax.swing.JScrollPane();
        appointmentTable = new javax.swing.JTable();

        appointmentAddDialog.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        appointmentAddDialog.setTitle("Add");
        appointmentAddDialog.setModal(true);
        appointmentAddDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                appointmentAddDialogWindowClosing(evt);
            }
        });

        appointmentAddOptionPane.setMessage(appointmentAddPanel);
        appointmentAddOptionPane.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                appointmentAddOptionPanePropertyChange(evt);
            }
        });

        javax.swing.GroupLayout appointmentAddDialogLayout = new javax.swing.GroupLayout(appointmentAddDialog.getContentPane());
        appointmentAddDialog.getContentPane().setLayout(appointmentAddDialogLayout);
        appointmentAddDialogLayout.setHorizontalGroup(
            appointmentAddDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
            .addGroup(appointmentAddDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(appointmentAddOptionPane, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE))
        );
        appointmentAddDialogLayout.setVerticalGroup(
            appointmentAddDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
            .addGroup(appointmentAddDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(appointmentAddOptionPane, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE))
        );

        appointmentEditDialog.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        appointmentEditDialog.setTitle("Edit");
        appointmentEditDialog.setModal(true);
        appointmentEditDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                appointmentEditDialogWindowClosing(evt);
            }
        });

        appointmentEditOptionPane.setMessage(appointmentEditPanel);
        appointmentEditOptionPane.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                appointmentEditOptionPanePropertyChange(evt);
            }
        });

        javax.swing.GroupLayout appointmentEditDialogLayout = new javax.swing.GroupLayout(appointmentEditDialog.getContentPane());
        appointmentEditDialog.getContentPane().setLayout(appointmentEditDialogLayout);
        appointmentEditDialogLayout.setHorizontalGroup(
            appointmentEditDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
            .addGroup(appointmentEditDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(appointmentEditOptionPane, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE))
        );
        appointmentEditDialogLayout.setVerticalGroup(
            appointmentEditDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
            .addGroup(appointmentEditDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(appointmentEditOptionPane, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE))
        );

        appointmentAddAppointmentIDLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        appointmentAddAppointmentIDLabel.setText("Appointment ID:");

        appointmentAddDateLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        appointmentAddDateLabel.setText("Date:");

        appointmentAddTimeLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        appointmentAddTimeLabel.setText("Time:");

        appointmentAddDoctorIDLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        appointmentAddDoctorIDLabel.setText("Doctor ID:");

        appointmentAddPatientIDLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        appointmentAddPatientIDLabel.setText("Patient ID:");

        appointmentAddDetailLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        appointmentAddDetailLabel.setText("Detail:");

        appointmentAddDayLabel.setText("Day");

        appointmentAddMonthLabel.setText("Month");

        appointmentAddYearLabel.setText("Year");

        appointmentAddHourLabel.setText("Hour");

        appointmentAddMinLabel.setText("Minute");

        appointmentAddDayBox.setEditable(true);

        appointmentAddMonthBox.setEditable(true);

        appointmentAddYearBox.setEditable(true);

        appointmentAddRequiredLabel.setForeground(new java.awt.Color(255, 0, 0));
        appointmentAddRequiredLabel.setText("*: Required information");

        appointmentAddAppointmentIDRequiredLabel.setForeground(new java.awt.Color(255, 0, 0));
        appointmentAddAppointmentIDRequiredLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        appointmentAddAppointmentIDRequiredLabel.setText("*");

        appointmentAddDateRequiredLabel.setForeground(new java.awt.Color(255, 0, 0));
        appointmentAddDateRequiredLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        appointmentAddDateRequiredLabel.setText("*");

        appointmentAddTimeRequiredLabel.setForeground(new java.awt.Color(255, 0, 0));
        appointmentAddTimeRequiredLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        appointmentAddTimeRequiredLabel.setText("*");

        appointmentAddDoctorIDRequiredLabel.setForeground(new java.awt.Color(255, 0, 0));
        appointmentAddDoctorIDRequiredLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        appointmentAddDoctorIDRequiredLabel.setText("*");

        appointmentAddPatientIDRequiredLabel.setForeground(new java.awt.Color(255, 0, 0));
        appointmentAddPatientIDRequiredLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        appointmentAddPatientIDRequiredLabel.setText("*");

        javax.swing.GroupLayout appointmentAddPanelLayout = new javax.swing.GroupLayout(appointmentAddPanel);
        appointmentAddPanel.setLayout(appointmentAddPanelLayout);
        appointmentAddPanelLayout.setHorizontalGroup(
            appointmentAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(appointmentAddPanelLayout.createSequentialGroup()
                .addGroup(appointmentAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, appointmentAddPanelLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(appointmentAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, appointmentAddPanelLayout.createSequentialGroup()
                                .addComponent(appointmentAddDoctorIDRequiredLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(appointmentAddDoctorIDLabel))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, appointmentAddPanelLayout.createSequentialGroup()
                                .addComponent(appointmentAddPatientIDRequiredLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(appointmentAddPatientIDLabel))
                            .addComponent(appointmentAddDetailLabel, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(appointmentAddPanelLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(appointmentAddAppointmentIDRequiredLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 8, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(appointmentAddAppointmentIDLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(appointmentAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(appointmentAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(appointmentAddDoctorIDTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(appointmentAddPatientIDTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(appointmentAddDetailTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(appointmentAddAppointmentIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(80, Short.MAX_VALUE))
            .addGroup(appointmentAddPanelLayout.createSequentialGroup()
                .addGroup(appointmentAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(appointmentAddPanelLayout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addGroup(appointmentAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(appointmentAddPanelLayout.createSequentialGroup()
                                .addComponent(appointmentAddTimeRequiredLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(appointmentAddTimeLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(appointmentAddHourLabel))
                            .addGroup(appointmentAddPanelLayout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(appointmentAddDateRequiredLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(appointmentAddDateLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(appointmentAddDayLabel)))
                        .addGap(8, 8, 8)
                        .addGroup(appointmentAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(appointmentAddDayBox, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(appointmentAddHourSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(appointmentAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(appointmentAddMinLabel)
                            .addComponent(appointmentAddMonthLabel))
                        .addGap(2, 2, 2)
                        .addGroup(appointmentAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(appointmentAddMonthBox, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(appointmentAddMinSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(appointmentAddYearLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(appointmentAddYearBox, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(appointmentAddRequiredLabel))
                .addContainerGap())
        );

        appointmentAddPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {appointmentAddAppointmentIDTextField, appointmentAddDetailTextField, appointmentAddDoctorIDTextField, appointmentAddPatientIDTextField});

        appointmentAddPanelLayout.setVerticalGroup(
            appointmentAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(appointmentAddPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(appointmentAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(appointmentAddAppointmentIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(appointmentAddAppointmentIDLabel)
                    .addComponent(appointmentAddAppointmentIDRequiredLabel))
                .addGap(24, 24, 24)
                .addGroup(appointmentAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(appointmentAddDayLabel)
                    .addComponent(appointmentAddDayBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(appointmentAddMonthLabel)
                    .addComponent(appointmentAddMonthBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(appointmentAddYearLabel)
                    .addComponent(appointmentAddYearBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(appointmentAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(appointmentAddDateLabel)
                        .addComponent(appointmentAddDateRequiredLabel)))
                .addGap(28, 28, 28)
                .addGroup(appointmentAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(appointmentAddTimeLabel)
                    .addComponent(appointmentAddTimeRequiredLabel)
                    .addComponent(appointmentAddHourLabel)
                    .addComponent(appointmentAddHourSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(appointmentAddMinLabel)
                    .addComponent(appointmentAddMinSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(appointmentAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(appointmentAddDoctorIDLabel)
                    .addComponent(appointmentAddDoctorIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(appointmentAddDoctorIDRequiredLabel))
                .addGap(26, 26, 26)
                .addGroup(appointmentAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(appointmentAddPatientIDLabel)
                    .addComponent(appointmentAddPatientIDRequiredLabel)
                    .addComponent(appointmentAddPatientIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addGroup(appointmentAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(appointmentAddDetailTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(appointmentAddDetailLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 17, Short.MAX_VALUE)
                .addComponent(appointmentAddRequiredLabel))
        );

        appointmentEditAppointmentIDLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        appointmentEditAppointmentIDLabel.setText("Appointment ID:");

        appointmentEditDateLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        appointmentEditDateLabel.setText("Date:");

        appointmenEditTimeLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        appointmenEditTimeLabel.setText("Time:");

        appointmentEditDoctorIDLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        appointmentEditDoctorIDLabel.setText("Doctor ID:");

        appointmentEditPatientIDLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        appointmentEditPatientIDLabel.setText("Patient ID:");

        appointmenEditDetailLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        appointmenEditDetailLabel.setText("Detail:");

        appointmentEditDayLabel.setText("Day");

        appointmentEditMonthLabel.setText("Month");

        appointmentEditYearLabel.setText("Year");

        appointmentEditHourLabel.setText("Hour");

        appointmentEditMinLabel.setText("Minute");

        appointmentEditAppointmentIDTextField.setEditable(false);

        appointmentEditDayBox.setEditable(true);

        appointmentEditMonthBox.setEditable(true);

        appointmentEditYearBox.setEditable(true);

        appointmentEditRequiredLabel.setForeground(new java.awt.Color(255, 0, 0));
        appointmentEditRequiredLabel.setText("*: Required information");

        appointmentEditDateRequiredLabel.setForeground(new java.awt.Color(255, 0, 0));
        appointmentEditDateRequiredLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        appointmentEditDateRequiredLabel.setText("*");

        appointmentEditTimeRequiredLabel.setForeground(new java.awt.Color(255, 0, 0));
        appointmentEditTimeRequiredLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        appointmentEditTimeRequiredLabel.setText("*");

        appointmentEditDoctorIDRequiredLabel.setForeground(new java.awt.Color(255, 0, 0));
        appointmentEditDoctorIDRequiredLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        appointmentEditDoctorIDRequiredLabel.setText("*");

        appointmentEditPatientIDRequiredLabel.setForeground(new java.awt.Color(255, 0, 0));
        appointmentEditPatientIDRequiredLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        appointmentEditPatientIDRequiredLabel.setText("*");

        javax.swing.GroupLayout appointmentEditPanelLayout = new javax.swing.GroupLayout(appointmentEditPanel);
        appointmentEditPanel.setLayout(appointmentEditPanelLayout);
        appointmentEditPanelLayout.setHorizontalGroup(
            appointmentEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(appointmentEditPanelLayout.createSequentialGroup()
                .addGroup(appointmentEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, appointmentEditPanelLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(appointmentEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, appointmentEditPanelLayout.createSequentialGroup()
                                .addComponent(appointmentEditDoctorIDRequiredLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(appointmentEditDoctorIDLabel))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, appointmentEditPanelLayout.createSequentialGroup()
                                .addComponent(appointmentEditPatientIDRequiredLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(appointmentEditPatientIDLabel))
                            .addComponent(appointmenEditDetailLabel, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(appointmentEditPanelLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(appointmentEditAppointmentIDLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(appointmentEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(appointmentEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(appointmentEditDoctorIDTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(appointmentEditPatientIDTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(appointmentEditDetailTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(appointmentEditAppointmentIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(80, Short.MAX_VALUE))
            .addGroup(appointmentEditPanelLayout.createSequentialGroup()
                .addGroup(appointmentEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(appointmentEditPanelLayout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addGroup(appointmentEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(appointmentEditPanelLayout.createSequentialGroup()
                                .addComponent(appointmentEditTimeRequiredLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(appointmenEditTimeLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(appointmentEditHourLabel))
                            .addGroup(appointmentEditPanelLayout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(appointmentEditDateRequiredLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(appointmentEditDateLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(appointmentEditDayLabel)))
                        .addGap(8, 8, 8)
                        .addGroup(appointmentEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(appointmentEditDayBox, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(appointmentEditHourSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(appointmentEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(appointmentEditMinLabel)
                            .addComponent(appointmentEditMonthLabel))
                        .addGap(2, 2, 2)
                        .addGroup(appointmentEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(appointmentEditMonthBox, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(appointmentEditMinSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(appointmentEditYearLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(appointmentEditYearBox, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(appointmentEditRequiredLabel))
                .addContainerGap())
        );

        appointmentEditPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {appointmentEditAppointmentIDTextField, appointmentEditDetailTextField, appointmentEditDoctorIDTextField, appointmentEditPatientIDTextField});

        appointmentEditPanelLayout.setVerticalGroup(
            appointmentEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(appointmentEditPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(appointmentEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(appointmentEditAppointmentIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(appointmentEditAppointmentIDLabel))
                .addGap(24, 24, 24)
                .addGroup(appointmentEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(appointmentEditDayLabel)
                    .addComponent(appointmentEditDayBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(appointmentEditMonthLabel)
                    .addComponent(appointmentEditMonthBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(appointmentEditYearLabel)
                    .addComponent(appointmentEditYearBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(appointmentEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(appointmentEditDateLabel)
                        .addComponent(appointmentEditDateRequiredLabel)))
                .addGap(28, 28, 28)
                .addGroup(appointmentEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(appointmenEditTimeLabel)
                    .addComponent(appointmentEditTimeRequiredLabel)
                    .addComponent(appointmentEditHourLabel)
                    .addComponent(appointmentEditHourSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(appointmentEditMinLabel)
                    .addComponent(appointmentEditMinSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(appointmentEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(appointmentEditDoctorIDLabel)
                    .addComponent(appointmentEditDoctorIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(appointmentEditDoctorIDRequiredLabel))
                .addGap(26, 26, 26)
                .addGroup(appointmentEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(appointmentEditPatientIDLabel)
                    .addComponent(appointmentEditPatientIDRequiredLabel)
                    .addComponent(appointmentEditPatientIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addGroup(appointmentEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(appointmentEditDetailTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(appointmenEditDetailLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 17, Short.MAX_VALUE)
                .addComponent(appointmentEditRequiredLabel))
        );

        appointmentFilterField.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        appointmentSearchingPromptButton.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        appointmentSearchingPromptButton.setText("Filter keywords:");

        appointmentAppointmentIDLabel.setText("Appointment ID:");

        appointmentDoctorIDLabel.setText("Doctor ID:");

        appointmentPatientIDLabel.setText("Patient ID:");

        appointmentDateLabel.setText("Date:");

        appointmentTimeLabel.setText("Time:");

        appointmentDetailLabel.setText("Detail:");

        javax.swing.GroupLayout appointmentFilterFieldLayout = new javax.swing.GroupLayout(appointmentFilterField);
        appointmentFilterField.setLayout(appointmentFilterFieldLayout);
        appointmentFilterFieldLayout.setHorizontalGroup(
            appointmentFilterFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, appointmentFilterFieldLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(appointmentSearchingPromptButton)
                .addGap(610, 610, 610))
            .addGroup(appointmentFilterFieldLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(appointmentFilterFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(appointmentDoctorIDLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(appointmentAppointmentIDLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(appointmentPatientIDLabel, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(appointmentFilterFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(appointmentDoctorIDTextField)
                    .addComponent(appointmentPatientIDTextField)
                    .addComponent(appointmentAppointmentIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(appointmentFilterFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(appointmentDateLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(appointmentTimeLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(appointmentDetailLabel, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(appointmentFilterFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(appointmentTimeTextField)
                    .addComponent(appointmentDateTextField)
                    .addComponent(appointmentDetailTextField))
                .addContainerGap())
        );
        appointmentFilterFieldLayout.setVerticalGroup(
            appointmentFilterFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(appointmentFilterFieldLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(appointmentSearchingPromptButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(appointmentFilterFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(appointmentAppointmentIDLabel)
                    .addComponent(appointmentDateLabel)
                    .addComponent(appointmentAppointmentIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(appointmentDateTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(appointmentFilterFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(appointmentDoctorIDLabel)
                    .addComponent(appointmentDoctorIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(appointmentTimeLabel)
                    .addComponent(appointmentTimeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(appointmentFilterFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(appointmentPatientIDLabel)
                    .addComponent(appointmentPatientIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(appointmentDetailLabel)
                    .addComponent(appointmentDetailTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        appointmentUtilityField.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        appointmentAddButton.setText("Add");
        appointmentAddButton.setToolTipText("Add a record");
        appointmentAddButton.setFocusPainted(false);
        appointmentAddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                appointmentAddButtonActionPerformed(evt);
            }
        });

        appointmentEditButton.setText("Edit");
        appointmentEditButton.setToolTipText("Edit the selected record");
        appointmentEditButton.setFocusPainted(false);
        appointmentEditButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                appointmentEditButtonActionPerformed(evt);
            }
        });

        appointmentDeleteButton.setText("Delete");
        appointmentDeleteButton.setToolTipText("Delete the selected record <Del>");
        appointmentDeleteButton.setFocusPainted(false);
        appointmentDeleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                appointmentDeleteButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout appointmentUtilityFieldLayout = new javax.swing.GroupLayout(appointmentUtilityField);
        appointmentUtilityField.setLayout(appointmentUtilityFieldLayout);
        appointmentUtilityFieldLayout.setHorizontalGroup(
            appointmentUtilityFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, appointmentUtilityFieldLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(appointmentAddButton, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(appointmentEditButton)
                .addGap(18, 18, 18)
                .addComponent(appointmentDeleteButton)
                .addGap(25, 25, 25))
        );

        appointmentUtilityFieldLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {appointmentAddButton, appointmentDeleteButton, appointmentEditButton});

        appointmentUtilityFieldLayout.setVerticalGroup(
            appointmentUtilityFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(appointmentUtilityFieldLayout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addGroup(appointmentUtilityFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(appointmentDeleteButton)
                    .addComponent(appointmentEditButton)
                    .addComponent(appointmentAddButton, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        appointmentUtilityFieldLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {appointmentAddButton, appointmentDeleteButton, appointmentEditButton});

        appointmentTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Appointment ID", "Date", "Time", "Doctor ID", "Patient ID", "Detail"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        appointmentTable.setRowHeight(25);
        appointmentTable.getTableHeader().setReorderingAllowed(false);
        appointmentTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                appointmentTableMousePressed(evt);
            }
        });
        appointmentTableScrollPanel.setViewportView(appointmentTable);

        javax.swing.GroupLayout appointmentTableFieldLayout = new javax.swing.GroupLayout(appointmentTableField);
        appointmentTableField.setLayout(appointmentTableFieldLayout);
        appointmentTableFieldLayout.setHorizontalGroup(
            appointmentTableFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(appointmentTableScrollPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 1032, Short.MAX_VALUE)
        );
        appointmentTableFieldLayout.setVerticalGroup(
            appointmentTableFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(appointmentTableFieldLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(appointmentTableScrollPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 488, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout appointmentPanelLayout = new javax.swing.GroupLayout(appointmentPanel);
        appointmentPanel.setLayout(appointmentPanelLayout);
        appointmentPanelLayout.setHorizontalGroup(
            appointmentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, appointmentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(appointmentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(appointmentTableField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(appointmentPanelLayout.createSequentialGroup()
                        .addComponent(appointmentFilterField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(appointmentUtilityField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        appointmentPanelLayout.setVerticalGroup(
            appointmentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(appointmentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(appointmentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(appointmentFilterField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(appointmentUtilityField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(appointmentTableField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1044, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(appointmentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 634, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(appointmentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void appointmentTableMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_appointmentTableMousePressed
        // Double click event
        if(evt.getClickCount() == 2 && !evt.isConsumed()) {
            evt.consume();
            handleTableDoubleClick();
        }
        // Single click event
        else {
            handleTableSingleClick();
        }
    }//GEN-LAST:event_appointmentTableMousePressed

    private void appointmentAddButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_appointmentAddButtonActionPerformed
        appointmentAddDialog.setVisible(true);
    }//GEN-LAST:event_appointmentAddButtonActionPerformed

    private void appointmentAddDialogWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_appointmentAddDialogWindowClosing
        appointmentAddOptionPane.setValue(JOptionPane.CLOSED_OPTION);
    }//GEN-LAST:event_appointmentAddDialogWindowClosing

    private void appointmentAddOptionPanePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_appointmentAddOptionPanePropertyChange
        String prop = evt.getPropertyName();
        if(appointmentAddDialog.isVisible() && (evt.getSource() == appointmentAddOptionPane) 
            && (prop.equals(JOptionPane.VALUE_PROPERTY))) {
            
            Object value = appointmentAddOptionPane.getValue();
            
            if(value == JOptionPane.UNINITIALIZED_VALUE) {
                // Ignore reset
                return;
            }
            
            // Reset value so that later on, if the same button is pressed,
            // a property change event will be fired again
            appointmentAddOptionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
            if(value.equals("OK")) {
                String inputAppointmentID = appointmentAddAppointmentIDTextField.getText();
                String inputDoctorID = appointmentAddDoctorIDTextField.getText();
                String inputPatientID = appointmentAddPatientIDTextField.getText();
                String inputDetail = appointmentAddDetailTextField.getText();
                
                if(inputAppointmentID.isEmpty() || inputDoctorID.isEmpty() || inputPatientID.isEmpty()) {
                    String message = "Required information not filled!";
                    MainFrame.makeBeepSound();
                    JOptionPane.showMessageDialog(null, message, MainFrame.TITLE, JOptionPane.ERROR_MESSAGE);
                }
                else {
                    AppointmentSystem appointmentSystem = dataManager.getAppointmentSystem();
                    EmployeeDatabase employeeDB = dataManager.getEmployeeDB();
                    PatientDatabase patientDB = dataManager.getPatientDB();
                    String appointmentID = inputAppointmentID;
                    int doctorID = Integer.parseInt(inputDoctorID);
                    int patientID = Integer.parseInt(inputPatientID);
                    
                    if(appointmentSystem.hasAppointment(appointmentID)) {
                        String message = "Appointment ID has already been assigned to a record!";
                        MainFrame.makeBeepSound();
                        JOptionPane.showMessageDialog(null, message, MainFrame.TITLE, JOptionPane.ERROR_MESSAGE);
                    }
                    else if(!employeeDB.hasDoctor(doctorID)) {
                        String message = "Doctor ID not found in the database!";
                        MainFrame.makeBeepSound();
                        JOptionPane.showMessageDialog(null, message, MainFrame.TITLE, JOptionPane.ERROR_MESSAGE);
                    }
                    else if(!patientDB.has(patientID)) {
                        String message = "Patient ID not found in the database!";
                        MainFrame.makeBeepSound();
                        JOptionPane.showMessageDialog(null, message, MainFrame.TITLE, JOptionPane.ERROR_MESSAGE);
                    }
                    else {
                        int day = Integer.parseInt((String) appointmentAddDayBox.getSelectedItem());
                        int month = Integer.parseInt((String) appointmentAddMonthBox.getSelectedItem());
                        int year = Integer.parseInt((String) appointmentAddYearBox.getSelectedItem());
                        int hour = (Integer) appointmentAddHourSpinner.getValue();
                        int min = (Integer) appointmentAddMinSpinner.getValue();
                        Date date = new Date(day, month, year);
                        Time time = new Time(hour, min);
                        String detail = inputDetail;
                        
                        String message = "Confirm to add an appointment record with the following information" + '\n'
                                       + "Appointment ID: " + appointmentID + '\n'
                                       + "Date: " + date + '\n'
                                       + "Time: " + time + '\n'
                                       + "Doctor ID: " + doctorID + '\n'
                                       + "Patient ID: " + patientID + '\n'
                                       + "Detail: " + detail + '\n'
                                       + "to the system?";
                        
                        int choice = JOptionPane.showConfirmDialog(null, message, MainFrame.TITLE, 
                                                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                        if(choice == JOptionPane.YES_OPTION) {
                            Appointment appointment = new Appointment(appointmentID, date, time, detail);
                            appointmentSystem.addAppointment(appointment, doctorID, patientID);
                            
                            addRow(new String[]{inputAppointmentID, date.toString(), time.toString(),
                                                inputDoctorID, inputPatientID, inputDetail});
                            
                            clearAndHideAddDialog();
                            frame.setSaved(panelIndex, false);
                        }
                    }
                }
            }
            else if(value.equals("Reset")) {
                clearAddDialog();
            }
            else {
                clearAndHideAddDialog();
            }
        }
    }//GEN-LAST:event_appointmentAddOptionPanePropertyChange

    private void appointmentEditOptionPanePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_appointmentEditOptionPanePropertyChange
        String prop = evt.getPropertyName();
        if(appointmentEditDialog.isVisible() && (evt.getSource() == appointmentEditOptionPane) 
            && (prop.equals(JOptionPane.VALUE_PROPERTY))) {
            
            Object value = appointmentEditOptionPane.getValue();
            
            if(value == JOptionPane.UNINITIALIZED_VALUE) {
                // Ignore reset
                return;
            }
            
            // Reset value so that later on, if the same button is pressed,
            // a property change event will be fired again
            appointmentEditOptionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
            if(value.equals("OK")) {
                String inputDoctorID = appointmentEditDoctorIDTextField.getText();
                String inputPatientID = appointmentEditPatientIDTextField.getText();
                
                if(inputDoctorID.isEmpty() || inputPatientID.isEmpty()) {
                    String message = "Required information not filled!";
                    MainFrame.makeBeepSound();
                    JOptionPane.showMessageDialog(null, message, MainFrame.TITLE, JOptionPane.ERROR_MESSAGE);
                }
                else {
                    EmployeeDatabase employeeDB = dataManager.getEmployeeDB();
                    PatientDatabase patientDB = dataManager.getPatientDB();
                    int doctorID = Integer.parseInt(inputDoctorID);
                    int patientID = Integer.parseInt(inputPatientID);
                    
                    if(!employeeDB.hasDoctor(doctorID)) {
                        String message = "Doctor ID not found in the database!";
                        MainFrame.makeBeepSound();
                        JOptionPane.showMessageDialog(null, message, MainFrame.TITLE, JOptionPane.ERROR_MESSAGE);
                    }
                    else if(!patientDB.has(patientID)) {
                        String message = "Patient ID not found in the database!";
                        MainFrame.makeBeepSound();
                        JOptionPane.showMessageDialog(null, message, MainFrame.TITLE, JOptionPane.ERROR_MESSAGE);
                    }
                    else {
                        String appointmentID = appointmentEditAppointmentIDTextField.getText();
                        int day = Integer.parseInt((String) appointmentEditDayBox.getSelectedItem());
                        int month = Integer.parseInt((String) appointmentEditMonthBox.getSelectedItem());
                        int year = Integer.parseInt((String) appointmentEditYearBox.getSelectedItem());
                        int hour = (Integer) appointmentEditHourSpinner.getValue();
                        int min = (Integer) appointmentEditMinSpinner.getValue();
                        Date date = new Date(day, month, year);
                        Time time = new Time(hour, min);
                        String detail = appointmentEditDetailTextField.getText();
                        
                        String[] updatedData = {appointmentID, date.toString(),  time.toString(), 
                                                inputDoctorID, inputPatientID, detail};
                        int selectedRow = appointmentTable.getSelectedRow();
                        int modelRow = appointmentTable.convertRowIndexToModel(selectedRow);
                        if(TableRenderingSupporter.isRowIdentical(appointmentTable, modelRow, updatedData)) {
                            clearAndHideEditDialog();
                        }
                        else {
                            String message = "Confirm to edit the selected record with the given information?";

                            int choice = JOptionPane.showConfirmDialog(null, message, MainFrame.TITLE, 
                                                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                            if(choice == JOptionPane.YES_OPTION) {
                                AppointmentSystem appointmentSystem = dataManager.getAppointmentSystem();
                                AppointmentInfo info = appointmentSystem.getAppointment(appointmentID);
                                Appointment appointment = info.getAppointment();
                                appointment.setDate(date);
                                appointment.setTime(time);
                                info.setDoctorID(doctorID);
                                info.setPatientID(patientID);
                                appointment.setDetail(detail);

                                TableRenderingSupporter.updateRow(appointmentTable, modelRow, updatedData);

                                message = "Record edited successfully!";
                                JOptionPane.showMessageDialog(null, message, MainFrame.TITLE, JOptionPane.INFORMATION_MESSAGE);

                                clearAndHideEditDialog();
                                frame.setSaved(panelIndex, false);
                            }
                        }
                    }
                }
            }
            else if(value.equals("Reset")) {
                int selectedRow = appointmentTable.getSelectedRow();
                String currentAppointmentID = (String) appointmentTable.getValueAt(selectedRow, 0);
                AppointmentSystem appointmentSystem = dataManager.getAppointmentSystem();
                resetEditDialog(appointmentSystem.getAppointment(currentAppointmentID));
            }
            else {
                clearAndHideEditDialog();
            }
        }
    }//GEN-LAST:event_appointmentEditOptionPanePropertyChange

    private void appointmentEditDialogWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_appointmentEditDialogWindowClosing
        appointmentEditOptionPane.setValue(JOptionPane.CLOSED_OPTION);
    }//GEN-LAST:event_appointmentEditDialogWindowClosing

    private void appointmentEditButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_appointmentEditButtonActionPerformed
        int selectedRow = appointmentTable.getSelectedRow();
        prepareAndOpenEditDialog((String) appointmentTable.getValueAt(selectedRow, 0));
    }//GEN-LAST:event_appointmentEditButtonActionPerformed

    private void appointmentDeleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_appointmentDeleteButtonActionPerformed
        String message = "Do you want to delete the selected record?";
        int choice = JOptionPane.showConfirmDialog(null, message, MainFrame.TITLE, 
                                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if(choice == JOptionPane.YES_OPTION) {
            int selectedRow = appointmentTable.getSelectedRow();
            String currentAppointmentID = (String) appointmentTable.getValueAt(selectedRow, 0);
            
            AppointmentSystem appointmentSystem = dataManager.getAppointmentSystem();
            appointmentSystem.removeAppointment(currentAppointmentID);
            
            DefaultTableModel model = (DefaultTableModel) appointmentTable.getModel();
            int modelRow = appointmentTable.convertRowIndexToModel(selectedRow);
            model.removeRow(modelRow);
            
            if(appointmentTable.getRowCount() == 0) {
                appointmentTable.clearSelection();
                renewUtilityButtons();
            }
            else if(selectedRow == appointmentTable.getRowCount()) {
                TableRenderingSupporter.selectRowAndScrollTableToCell(appointmentTable, selectedRow - 1, 0);
            }
            else {
                TableRenderingSupporter.selectRowAndScrollTableToCell(appointmentTable, selectedRow, 0);
            }
            
            message = "Record deleted successfully!";
            JOptionPane.showMessageDialog(null, message, MainFrame.TITLE, JOptionPane.INFORMATION_MESSAGE);
            
            frame.setSaved(panelIndex, false);
        }
    }//GEN-LAST:event_appointmentDeleteButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel appointmenEditDetailLabel;
    private javax.swing.JLabel appointmenEditTimeLabel;
    private javax.swing.JLabel appointmentAddAppointmentIDLabel;
    private javax.swing.JLabel appointmentAddAppointmentIDRequiredLabel;
    private javax.swing.JTextField appointmentAddAppointmentIDTextField;
    private javax.swing.JButton appointmentAddButton;
    private javax.swing.JLabel appointmentAddDateLabel;
    private javax.swing.JLabel appointmentAddDateRequiredLabel;
    private javax.swing.JComboBox<String> appointmentAddDayBox;
    private javax.swing.JLabel appointmentAddDayLabel;
    private javax.swing.JLabel appointmentAddDetailLabel;
    private javax.swing.JTextField appointmentAddDetailTextField;
    private javax.swing.JDialog appointmentAddDialog;
    private javax.swing.JLabel appointmentAddDoctorIDLabel;
    private javax.swing.JLabel appointmentAddDoctorIDRequiredLabel;
    private javax.swing.JTextField appointmentAddDoctorIDTextField;
    private javax.swing.JLabel appointmentAddHourLabel;
    private javax.swing.JSpinner appointmentAddHourSpinner;
    private javax.swing.JLabel appointmentAddMinLabel;
    private javax.swing.JSpinner appointmentAddMinSpinner;
    private javax.swing.JComboBox<String> appointmentAddMonthBox;
    private javax.swing.JLabel appointmentAddMonthLabel;
    private javax.swing.JOptionPane appointmentAddOptionPane;
    private javax.swing.JPanel appointmentAddPanel;
    private javax.swing.JLabel appointmentAddPatientIDLabel;
    private javax.swing.JLabel appointmentAddPatientIDRequiredLabel;
    private javax.swing.JTextField appointmentAddPatientIDTextField;
    private javax.swing.JLabel appointmentAddRequiredLabel;
    private javax.swing.JLabel appointmentAddTimeLabel;
    private javax.swing.JLabel appointmentAddTimeRequiredLabel;
    private javax.swing.JComboBox<String> appointmentAddYearBox;
    private javax.swing.JLabel appointmentAddYearLabel;
    private javax.swing.JLabel appointmentAppointmentIDLabel;
    private javax.swing.JTextField appointmentAppointmentIDTextField;
    private javax.swing.JLabel appointmentDateLabel;
    private javax.swing.JTextField appointmentDateTextField;
    private javax.swing.JButton appointmentDeleteButton;
    private javax.swing.JLabel appointmentDetailLabel;
    private javax.swing.JTextField appointmentDetailTextField;
    private javax.swing.JLabel appointmentDoctorIDLabel;
    private javax.swing.JTextField appointmentDoctorIDTextField;
    private javax.swing.JLabel appointmentEditAppointmentIDLabel;
    private javax.swing.JTextField appointmentEditAppointmentIDTextField;
    private javax.swing.JButton appointmentEditButton;
    private javax.swing.JLabel appointmentEditDateLabel;
    private javax.swing.JLabel appointmentEditDateRequiredLabel;
    private javax.swing.JComboBox<String> appointmentEditDayBox;
    private javax.swing.JLabel appointmentEditDayLabel;
    private javax.swing.JTextField appointmentEditDetailTextField;
    private javax.swing.JDialog appointmentEditDialog;
    private javax.swing.JLabel appointmentEditDoctorIDLabel;
    private javax.swing.JLabel appointmentEditDoctorIDRequiredLabel;
    private javax.swing.JTextField appointmentEditDoctorIDTextField;
    private javax.swing.JLabel appointmentEditHourLabel;
    private javax.swing.JSpinner appointmentEditHourSpinner;
    private javax.swing.JLabel appointmentEditMinLabel;
    private javax.swing.JSpinner appointmentEditMinSpinner;
    private javax.swing.JComboBox<String> appointmentEditMonthBox;
    private javax.swing.JLabel appointmentEditMonthLabel;
    private javax.swing.JOptionPane appointmentEditOptionPane;
    private javax.swing.JPanel appointmentEditPanel;
    private javax.swing.JLabel appointmentEditPatientIDLabel;
    private javax.swing.JLabel appointmentEditPatientIDRequiredLabel;
    private javax.swing.JTextField appointmentEditPatientIDTextField;
    private javax.swing.JLabel appointmentEditRequiredLabel;
    private javax.swing.JLabel appointmentEditTimeRequiredLabel;
    private javax.swing.JComboBox<String> appointmentEditYearBox;
    private javax.swing.JLabel appointmentEditYearLabel;
    private javax.swing.JPanel appointmentFilterField;
    private javax.swing.JPanel appointmentPanel;
    private javax.swing.JLabel appointmentPatientIDLabel;
    private javax.swing.JTextField appointmentPatientIDTextField;
    private javax.swing.JLabel appointmentSearchingPromptButton;
    private javax.swing.JTable appointmentTable;
    private javax.swing.JPanel appointmentTableField;
    private javax.swing.JScrollPane appointmentTableScrollPanel;
    private javax.swing.JLabel appointmentTimeLabel;
    private javax.swing.JTextField appointmentTimeTextField;
    private javax.swing.JPanel appointmentUtilityField;
    // End of variables declaration//GEN-END:variables

    @Override
    public void handleSaveAction() {
        // DO NOTHING
    }
    
    @Override
    public void handleDiscardAction() {
        loadAppointmentData();
        resetView();
    }
}
