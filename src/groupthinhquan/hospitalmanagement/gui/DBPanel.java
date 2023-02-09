/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package groupthinhquan.hospitalmanagement.gui;

import groupthinhquan.hospitalmanagement.core.*;
import groupthinhquan.hospitalmanagement.interaction.*;
import groupthinhquan.hospitalmanagement.util.*;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
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

public class DBPanel extends javax.swing.JPanel implements SaveDiscardSupportPanel {
    private static final int EMPLOYEE_INDEX = 1;
    private static final int PATIENT_INDEX = 2;
    private static final String SALARY_FORMAT = "#,##0.###";
    private static final int MAXIMUM_FRACTION_DIGITS = 3;
    private static final char GROUPING_CHARACTER = ',';
    private static final String DELIMITER = ",";
    private static final Color UNEDITABLE_COLOR = Color.GRAY;
    
    // Custom variables
    private MainFrame frame;
    private int panelIndex;
    private DataManager dataManager;
    
    private TableRowSorter doctorTableSorter;
    private TableRowSorter nurseTableSorter;
    private TableRowSorter patientTableSorter;
    
    private JTable currentTable;
    private DecimalFormat salaryFormatter;
    
    /**
     * Creates new form DBPanel
     * @param frame: The main frame
     * @param index: the index of this panel in the tabbed pane
     */
    public DBPanel(MainFrame frame, int index) {
        initComponents();
        
        initCustomVar(frame, index);
        preset();
    }
    
    private void initCustomVar(MainFrame frame, int index) {
        this.frame = frame;
        panelIndex = index;
        dataManager = frame.getDataManager();
        salaryFormatter = new DecimalFormat(SALARY_FORMAT);
        DecimalFormatSymbols symbols = salaryFormatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator(GROUPING_CHARACTER);
        salaryFormatter.setDecimalFormatSymbols(symbols);
        salaryFormatter.setMaximumFractionDigits(MAXIMUM_FRACTION_DIGITS);
        
        doctorTableSorter = new TableRowSorter<>(doctorTable.getModel());
        nurseTableSorter = new TableRowSorter<>(nurseTable.getModel());
        patientTableSorter = new TableRowSorter<>(patientTable.getModel());
        linkSorter(doctorTableSorter, doctorTable);
        linkSorter(nurseTableSorter, nurseTable);
        linkSorter(patientTableSorter, patientTable);
        
        loadDoctorData();
        loadNurseData();
        loadPatientData();
    }
    
    private void preset() {
        DBDoctorButton.setSelected(true);
        DBButtonPanel.setVisible(false);
        DBUtilityField.setVisible(false);
        TableRenderingSupporter.centerTableHeader(doctorTable);
        TableRenderingSupporter.centerTableHeader(nurseTable);
        TableRenderingSupporter.centerTableHeader(patientTable);
        
        setupAddDoctorPanelFilters();
        setupEditDoctorPanelFilters();
        setupAddNursePanelFilters();
        setupEditNursePanelFilters();
        setupAddPatientPanelFilters();
        setupEditPatientPanelFilters();
        
        // Set Shortcut Key for delete button: DEL
        String deleteActionName = "delete";
        Action deleteAction = new AbstractAction(){
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DBDeleteButtonActionPerformed(evt);
            }
        };
        DBDeleteButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), deleteActionName);
        DBDeleteButton.getActionMap().put(deleteActionName, deleteAction);
    }
    
    private void linkSorter(final TableRowSorter sorter, JTable table) {
        table.setRowSorter(sorter);
        DBKeywordTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent evt) {
                search(DBKeywordTextField.getText());
            }
            
            @Override
            public void removeUpdate(DocumentEvent evt) {
                search(DBKeywordTextField.getText());
            }
            
            @Override            
            public void changedUpdate(DocumentEvent evt) {
                search(DBKeywordTextField.getText());
            }
            
            public void search(String input) {
                if(currentTable == table) {
                    if(input.isEmpty()) {
                        sorter.setRowFilter(null);
                    }
                    else {
                        sorter.setRowFilter(RowFilter.regexFilter(input));
                    }
                    
                    if(currentTable.getSelectedRow() == -1) {
                        renewUtilityButtons();
                    }
                    else {
                        DBEditButton.setEnabled(true);
                        DBDeleteButton.setEnabled(true);
                    }
                }
            }
        });
    }
    
    private void loadDoctorData() {
        EmployeeDatabase employeeDB = dataManager.getEmployeeDB();
        Doctor[] doctors = employeeDB.getDoctors();
        DefaultTableModel model = (DefaultTableModel) doctorTable.getModel();
        model.setRowCount(0);
        for(Doctor doctor : doctors) {
            String ID = Integer.toString(doctor.getID());
            String name = doctor.getName();
            String age = Integer.toString(doctor.getAge());
            String sex = doctor.getSex().toString();
            String address = doctor.getAddress();
            String phoneNumber = doctor.getPhoneNum();
            String WY = Integer.toString(doctor.getWorkingYear());
            String formattedSalary = salaryFormatter.format(doctor.getSalary());
            String majors = StringHandlingSupporter.combineStrings(doctor.getMajors(), DELIMITER);
            
            model.addRow(new String[]{ID, name, age, sex, address, phoneNumber, WY, formattedSalary, majors});
        }
    }
    
    private void loadNurseData() {
        EmployeeDatabase employeeDB = dataManager.getEmployeeDB();
        Nurse[] nurses = employeeDB.getNurses();
        DefaultTableModel model = (DefaultTableModel) nurseTable.getModel();
        model.setRowCount(0);
        for(Nurse nurse : nurses) {
            String ID = Integer.toString(nurse.getID());
            String name = nurse.getName();
            String age = Integer.toString(nurse.getAge());
            String sex = nurse.getSex().toString();
            String address = nurse.getAddress();
            String phoneNumber = nurse.getPhoneNum();
            String WY = Integer.toString(nurse.getWorkingYear());
            String formattedSalary = salaryFormatter.format(nurse.getSalary());
            String position = nurse.getPosition();
            
            model.addRow(new String[]{ID, name, age, sex, address, phoneNumber, WY, formattedSalary, position});
        }
    }
        
    private void loadPatientData() {
        PatientDatabase patientDB = dataManager.getPatientDB();
        Patient[] patients = patientDB.getPatients();
        DefaultTableModel model = (DefaultTableModel) patientTable.getModel();
        model.setRowCount(0);
        for(Patient patient : patients) {
            String ID = Integer.toString(patient.getID());
            String name = patient.getName();
            String age = Integer.toString(patient.getAge());
            String sex = patient.getSex().toString();
            String address = patient.getAddress();
            String phoneNumber = patient.getPhoneNum();
            String status = patient.getStatus();
            
            model.addRow(new String[]{ID, name, age, sex, address, phoneNumber, status});
        }
    }
    
    private void setupAddDoctorPanelFilters() {
        TextFieldFilteringSupporter.setPositiveIntegerFilter(DBAddDoctorIDTextField);
        TextFieldFilteringSupporter.setPositiveIntegerFilter(DBAddDoctorAgeTextField, 3, 200);
        TextFieldFilteringSupporter.setPositiveIntegerFilter(DBAddDoctorWYTextField, 3, 200);
        TextFieldFilteringSupporter.setPositiveFloatFilter(DBAddDoctorSalaryTextField, MAXIMUM_FRACTION_DIGITS);
    }
    
    private void setupEditDoctorPanelFilters() {
        DBEditDoctorIDTextField.setForeground(UNEDITABLE_COLOR);
        TextFieldFilteringSupporter.setPositiveIntegerFilter(DBEditDoctorAgeTextField, 3, 200);
        TextFieldFilteringSupporter.setPositiveIntegerFilter(DBEditDoctorWYTextField, 3, 200);
        TextFieldFilteringSupporter.setPositiveFloatFilter(DBEditDoctorSalaryTextField, MAXIMUM_FRACTION_DIGITS);
    }
    
    private void setupAddNursePanelFilters() {
        TextFieldFilteringSupporter.setPositiveIntegerFilter(DBAddNurseIDTextField);
        TextFieldFilteringSupporter.setPositiveIntegerFilter(DBAddNurseAgeTextField, 3, 200);
        TextFieldFilteringSupporter.setPositiveIntegerFilter(DBAddNurseWYTextField, 3, 200);
        TextFieldFilteringSupporter.setPositiveFloatFilter(DBAddNurseSalaryTextField, MAXIMUM_FRACTION_DIGITS);
    }
    
    private void setupEditNursePanelFilters() {
        DBEditNurseIDTextField.setForeground(UNEDITABLE_COLOR);
        TextFieldFilteringSupporter.setPositiveIntegerFilter(DBEditNurseAgeTextField, 3, 200);
        TextFieldFilteringSupporter.setPositiveIntegerFilter(DBEditNurseWYTextField, 3, 200);
        TextFieldFilteringSupporter.setPositiveFloatFilter(DBEditNurseSalaryTextField, MAXIMUM_FRACTION_DIGITS);
    }
    
    private void setupAddPatientPanelFilters() {
        TextFieldFilteringSupporter.setPositiveIntegerFilter(DBAddPatientIDTextField);
        TextFieldFilteringSupporter.setPositiveIntegerFilter(DBAddPatientAgeTextField, 3, 200);
    }
    
    private void setupEditPatientPanelFilters() {
        DBEditPatientIDTextField.setForeground(UNEDITABLE_COLOR);
        TextFieldFilteringSupporter.setPositiveIntegerFilter(DBEditPatientAgeTextField, 3, 100);
    }
    
    private void resetView() {
        doctorTable.clearSelection();
        nurseTable.clearSelection();
        patientTable.clearSelection();
        renewUtilityButtons();
        DBKeywordTextField.setText("");
    }
    
    private void renewUtilityButtons() {
        DBAddButton.setEnabled(true);
        DBEditButton.setEnabled(false);
        DBDeleteButton.setEnabled(false);
    }

    private void clearAddDialog() {
        if(currentTable == doctorTable) {
            DBAddDoctorIDTextField.setText("");
            DBAddDoctorNameTextField.setText("");
            DBAddDoctorAgeTextField.setText("");
            DBAddDoctorButtonGroup.clearSelection();
            DBAddDoctorAddressTextField.setText("");
            DBAddDoctorPhoneNumTextField.setText("");
            DBAddDoctorWYTextField.setText("");
            DBAddDoctorSalaryTextField.setText("");
            DBAddDoctorMajorsTextField.setText("");
        }
        else if(currentTable == nurseTable) {
            DBAddNurseIDTextField.setText("");
            DBAddNurseNameTextField.setText("");
            DBAddNurseAgeTextField.setText("");
            DBAddNurseButtonGroup.clearSelection();
            DBAddNurseAddressTextField.setText("");
            DBAddNursePhoneNumTextField.setText("");
            DBAddNurseWYTextField.setText("");
            DBAddNurseSalaryTextField.setText("");
            DBAddNursePositionTextField.setText("");
        }
        else if(currentTable == patientTable){
            DBAddPatientIDTextField.setText("");
            DBAddPatientNameTextField.setText("");
            DBAddPatientAgeTextField.setText("");
            DBAddPatientButtonGroup.clearSelection();
            DBAddPatientAddressTextField.setText("");
            DBAddPatientPhoneNumTextField.setText("");
            DBAddPatientStatusTextField.setText("");
        }
    }
    
    private void clearAndHideAddDialog() {
        clearAddDialog();
        DBAddDialog.setVisible(false);
    }
    
    private void resetEditDoctorPanel(Doctor doctor) {
        String ID = Integer.toString(doctor.getID());
        String name = doctor.getName();
        String age = Integer.toString(doctor.getAge());
        String sex = doctor.getSex().toString();
        String address = doctor.getAddress();
        String phoneNum = doctor.getPhoneNum();
        String WY = Integer.toString(doctor.getWorkingYear());
        String formattedSalary = salaryFormatter.format(doctor.getSalary());
        String salary = convertFormattedSalaryToNormal(formattedSalary);
        String majors = StringHandlingSupporter.combineStrings(doctor.getMajors(), DELIMITER);
        
        DBEditDoctorIDTextField.setText(ID);
        DBEditDoctorNameTextField.setText(name);
        DBEditDoctorAgeTextField.setText(age);
        ButtonGroupHandlingSupporter.setSelectedButton(DBEditDoctorButtonGroup, sex);
        DBEditDoctorAddressTextField.setText(address);
        DBEditDoctorPhoneNumTextField.setText(phoneNum);
        DBEditDoctorWYTextField.setText(WY);
        DBEditDoctorSalaryTextField.setText(salary);
        DBEditDoctorMajorsTextField.setText(majors);
    }
    
    private void resetEditNursePanel(Nurse nurse) {
        String ID = Integer.toString(nurse.getID());
        String name = nurse.getName();
        String age = Integer.toString(nurse.getAge());
        String sex = nurse.getSex().toString();
        String address = nurse.getAddress();
        String phoneNum = nurse.getPhoneNum();
        String WY = Integer.toString(nurse.getWorkingYear());
        String formattedSalary = salaryFormatter.format(nurse.getSalary());
        String salary = convertFormattedSalaryToNormal(formattedSalary);
        String position = nurse.getPosition();
        
        DBEditNurseIDTextField.setText(ID);
        DBEditNurseNameTextField.setText(name);
        DBEditNurseAgeTextField.setText(age);
        ButtonGroupHandlingSupporter.setSelectedButton(DBEditNurseButtonGroup, sex);
        DBEditNurseAddressTextField.setText(address);
        DBEditNursePhoneNumTextField.setText(phoneNum);
        DBEditNurseWYTextField.setText(WY);
        DBEditNurseSalaryTextField.setText(salary);
        DBEditNursePositionTextField.setText(position);
    }
    
    private void resetEditPatientPanel(Patient patient) {
        String ID = Integer.toString(patient.getID());
        String name = patient.getName();
        String age = Integer.toString(patient.getAge());
        String sex = patient.getSex().toString();
        String address = patient.getAddress();
        String phoneNum = patient.getPhoneNum();
        String status = patient.getStatus();
        
        DBEditPatientIDTextField.setText(ID);
        DBEditPatientNameTextField.setText(name);
        DBEditPatientAgeTextField.setText(age);
        ButtonGroupHandlingSupporter.setSelectedButton(DBEditPatientButtonGroup, sex);
        DBEditPatientAddressTextField.setText(address);
        DBEditPatientPhoneNumTextField.setText(phoneNum);
        DBEditPatientStatusTextField.setText(status);
    }
    
    private void resetEditDialog(Person person) {
        switch (person.getJob()) {
        case DOCTOR -> resetEditDoctorPanel((Doctor) person);
        case NURSE -> resetEditNursePanel((Nurse) person);
        case PATIENT -> resetEditPatientPanel((Patient) person);
        }
    }
    
    private void clearEditDialog() {
        if(currentTable == doctorTable) {
            DBEditDoctorIDTextField.setText("");
            DBEditDoctorNameTextField.setText("");
            DBEditDoctorAgeTextField.setText("");
            DBEditDoctorButtonGroup.clearSelection();
            DBEditDoctorAddressTextField.setText("");
            DBEditDoctorPhoneNumTextField.setText("");
            DBEditDoctorWYTextField.setText("");
            DBEditDoctorSalaryTextField.setText("");
            DBEditDoctorMajorsTextField.setText("");
        }
        else if(currentTable == nurseTable) {
            DBEditNurseIDTextField.setText("");
            DBEditNurseNameTextField.setText("");
            DBEditNurseAgeTextField.setText("");
            DBEditNurseButtonGroup.clearSelection();
            DBEditNurseAddressTextField.setText("");
            DBEditNursePhoneNumTextField.setText("");
            DBEditNurseWYTextField.setText("");
            DBEditNurseSalaryTextField.setText("");
            DBEditNursePositionTextField.setText("");
        }
        else if(currentTable == patientTable) {
            DBEditPatientIDTextField.setText("");
            DBEditPatientNameTextField.setText("");
            DBEditPatientAgeTextField.setText("");
            DBEditPatientButtonGroup.clearSelection();
            DBEditPatientAddressTextField.setText("");
            DBEditPatientPhoneNumTextField.setText("");
            DBEditPatientStatusTextField.setText("");
        }
    }
    
    private void clearAndHideEditDialog() {
        clearEditDialog();
        DBEditDialog.setVisible(false);
    }
    
    private void prepareAndOpenAddDialog() {
        if(currentTable == doctorTable) {
            DBAddOptionPane.setMessage(DBAddDoctorPanel);
        }
        else if(currentTable == nurseTable) {
            DBAddOptionPane.setMessage(DBAddNursePanel);
        }
        else if(currentTable == patientTable) {
            DBAddOptionPane.setMessage(DBAddPatientPanel);
        }
        
        DBAddDialog.pack();
        DBAddDialog.setLocationRelativeTo(null);
        DBAddDialog.setVisible(true);
    }
    
    private void prepareAndOpenEditDialog(int currentID) {
        if(currentTable == doctorTable) {
            resetEditDialog(dataManager.getEmployeeDB().getDoctor(currentID));
            DBEditOptionPane.setMessage(DBEditDoctorPanel);

        }
        else if(currentTable == nurseTable) {
            resetEditDialog(dataManager.getEmployeeDB().getNurse(currentID));
            DBEditOptionPane.setMessage(DBEditNursePanel);
        }
        else if(currentTable == patientTable) {
            resetEditDialog(dataManager.getPatientDB().get(currentID));
            DBEditOptionPane.setMessage(DBEditPatientPanel);
        }
        
        DBEditDialog.pack();
        DBEditDialog.setLocationRelativeTo(null);
        DBEditDialog.setVisible(true);
    }
    
    private void handleTableDoubleClick(JTable table) {
        int selectedRow = table.getSelectedRow();
        if(selectedRow != -1) {
            prepareAndOpenEditDialog(Integer.parseInt((String) table.getValueAt(selectedRow, 0)));
        }
    }
    
    private void handleTableSingleClick(JTable table) {
        if(table.getSelectedRow() != -1) {
            DBEditButton.setEnabled(true);
            DBDeleteButton.setEnabled(true);
        }
        else {
            DBEditButton.setEnabled(false);
            DBDeleteButton.setEnabled(false);
        }
    }
    
    private void handleAddDoctorAttempt() {
        String inputID = DBAddDoctorIDTextField.getText();
        String inputName = DBAddDoctorNameTextField.getText();
        String inputAge = DBAddDoctorAgeTextField.getText();
        String inputSex = ButtonGroupHandlingSupporter.getSelectedButtonText(DBAddDoctorButtonGroup);
        String inputAddress = DBAddDoctorAddressTextField.getText();
        String inputPhoneNum = DBAddDoctorPhoneNumTextField.getText();
        String inputWY = DBAddDoctorWYTextField.getText();
        String inputSalary = DBAddDoctorSalaryTextField.getText();
        String inputMajors = DBAddDoctorMajorsTextField.getText();

        if(inputID.isEmpty() || inputName.isEmpty() || inputAge.isEmpty() || inputSex.isEmpty()
            || inputWY.isEmpty() || inputSalary.isEmpty() || inputMajors.isEmpty()) {
            String message = "Required information not filled!";
            MainFrame.makeBeepSound();
            JOptionPane.showMessageDialog(null, message, MainFrame.TITLE, JOptionPane.ERROR_MESSAGE);
        }
        else {
            int ID = Integer.parseInt(inputID);

            EmployeeDatabase employeeDB = dataManager.getEmployeeDB();
            if(employeeDB.has(ID)) {
                String message = "This ID has already been assigned to an employee!";
                MainFrame.makeBeepSound();
                JOptionPane.showMessageDialog(null, message, MainFrame.TITLE, JOptionPane.ERROR_MESSAGE);
            }
            else {
                String name = inputName;
                int age = Integer.parseInt(inputAge);
                Sex sex = Sex.valueOf(inputSex);
                String address = inputAddress;
                String phoneNum = inputPhoneNum;
                int WY = Integer.parseInt(inputWY);
                double salary = Double.parseDouble(inputSalary);
                String[] majors = StringHandlingSupporter.separateString(inputMajors, DELIMITER);

                inputSalary = salaryFormatter.format(salary);

                String message = "Confirm to add a doctor record with the following information:" + '\n'
                               + "ID: " + inputID + '\n'
                               + "Name: " + inputName + '\n'
                               + "Age: " + inputAge + '\n' 
                               + "Sex: " + inputSex + '\n'
                               + "Address: " + inputAddress + '\n'
                               + "Phone number: " + inputPhoneNum + '\n'
                               + "Working Year: " + inputWY + '\n'
                               + "Salary: " + inputSalary + '\n'
                               + "Majors: " + inputMajors + '\n'
                               + "to the database?";

                int choice = JOptionPane.showConfirmDialog(null, message, MainFrame.TITLE, 
                                                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if(choice == JOptionPane.YES_OPTION) {
                    Doctor doctor = new Doctor(ID, name, age, sex, address, phoneNum, WY, salary, majors);
                    employeeDB.add(ID, doctor);

                    addRow(new String[]{inputID, inputName, inputAge, inputSex, inputAddress, 
                                            inputPhoneNum, inputWY, inputSalary, inputMajors});

                    clearAndHideAddDialog();
                    frame.setSaved(panelIndex, false);
                }
            }
        }
    }
    
    private void handleAddNurseAttempt() {
        String inputID = DBAddNurseIDTextField.getText();
        String inputName = DBAddNurseNameTextField.getText();
        String inputAge = DBAddNurseAgeTextField.getText();
        String inputSex = ButtonGroupHandlingSupporter.getSelectedButtonText(DBAddNurseButtonGroup);
        String inputAddress = DBAddNurseAddressTextField.getText();
        String inputPhoneNum = DBAddNursePhoneNumTextField.getText();
        String inputWY = DBAddNurseWYTextField.getText();
        String inputSalary = DBAddNurseSalaryTextField.getText();
        String inputPosition = DBAddNursePositionTextField.getText();

        if(inputID.isEmpty() || inputName.isEmpty() || inputAge.isEmpty() || inputSex.isEmpty()
            || inputWY.isEmpty() || inputSalary.isEmpty() || inputPosition.isEmpty()) {
            String message = "Required information not filled!";
            MainFrame.makeBeepSound();
            JOptionPane.showMessageDialog(null, message, MainFrame.TITLE, JOptionPane.ERROR_MESSAGE);
        }
        else {
            int ID = Integer.parseInt(inputID);

            EmployeeDatabase employeeDB = dataManager.getEmployeeDB();
            if(employeeDB.has(ID)) {
                String message = "This ID has already been assigned to an employee!";
                MainFrame.makeBeepSound();
                JOptionPane.showMessageDialog(null, message, MainFrame.TITLE, JOptionPane.ERROR_MESSAGE);
            }
            else {
                String name = inputName;
                int age = Integer.parseInt(inputAge);
                Sex sex = Sex.valueOf(inputSex);
                String address = inputAddress;
                String phoneNum = inputPhoneNum;
                int WY = Integer.parseInt(inputWY);
                double salary = Double.parseDouble(inputSalary);
                String position = inputPosition;

                inputSalary = salaryFormatter.format(salary);

                String message = "Confirm to add a nurse record with the following information:" + '\n'
                               + "ID: " + inputID + '\n'
                               + "Name: " + inputName + '\n'
                               + "Age: " + inputAge + '\n' 
                               + "Sex: " + inputSex + '\n'
                               + "Address: " + inputAddress + '\n'
                               + "Phone number: " + inputPhoneNum + '\n'
                               + "Working Year: " + inputWY + '\n'
                               + "Salary: " + inputSalary + '\n'
                               + "Position: " + inputPosition + '\n'
                               + "to the database?";

                int choice = JOptionPane.showConfirmDialog(null, message, MainFrame.TITLE, 
                                                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if(choice == JOptionPane.YES_OPTION) {
                    Nurse nurse = new Nurse(ID, name, age, sex, address, phoneNum, WY, salary, position);
                    employeeDB.add(ID, nurse);

                    addRow(new String[]{inputID, inputName, inputAge, inputSex, inputAddress, 
                                            inputPhoneNum, inputWY, inputSalary, inputPosition});

                    clearAndHideAddDialog();
                    frame.setSaved(panelIndex, false);
                }
            }
        }        
    }
    
    private void handleAddPatientAttempt() {
        String inputID = DBAddPatientIDTextField.getText();
        String inputName = DBAddPatientNameTextField.getText();
        String inputAge = DBAddPatientAgeTextField.getText();
        String inputSex = ButtonGroupHandlingSupporter.getSelectedButtonText(DBAddPatientButtonGroup);
        String inputAddress = DBAddPatientAddressTextField.getText();
        String inputPhoneNum = DBAddPatientPhoneNumTextField.getText();
        String inputStatus = DBAddPatientStatusTextField.getText();

        if(inputID.isEmpty() || inputName.isEmpty() || inputAge.isEmpty() || inputSex.isEmpty()
            || inputStatus.isEmpty()) {
            String message = "Required information not filled!";
            MainFrame.makeBeepSound();
            JOptionPane.showMessageDialog(null, message, MainFrame.TITLE, JOptionPane.ERROR_MESSAGE);
        }
        else {
            int ID = Integer.parseInt(inputID);

            PatientDatabase patientDB = dataManager.getPatientDB();
            if(patientDB.has(ID)) {
                String message = "This ID has already been assigned to a patient!";
                MainFrame.makeBeepSound();
                JOptionPane.showMessageDialog(null, message, MainFrame.TITLE, JOptionPane.ERROR_MESSAGE);
            }
            else {
                String name = inputName;
                int age = Integer.parseInt(inputAge);
                Sex sex = Sex.valueOf(inputSex);
                String address = inputAddress;
                String phoneNum = inputPhoneNum;
                String status = inputStatus;
                
                String[] updatedDate = {inputID, inputName, inputAge, inputSex, inputAddress, 
                                        inputPhoneNum, inputStatus};

                String message = "Confirm to add a patient record with the following information:" + '\n'
                               + "ID: " + inputID + '\n'
                               + "Name: " + inputName + '\n'
                               + "Age: " + inputAge + '\n' 
                               + "Sex: " + inputSex + '\n'
                               + "Address: " + inputAddress + '\n'
                               + "Phone number: " + inputPhoneNum + '\n'
                               + "Status: " + inputStatus + '\n'
                               + "to the database?";

                int choice = JOptionPane.showConfirmDialog(null, message, MainFrame.TITLE, 
                                                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if(choice == JOptionPane.YES_OPTION) {
                    Patient patient = new Patient(ID, name, age, sex, address, phoneNum, status);
                    patientDB.add(ID, patient);
                    
                    addRow(new String[]{inputID, inputName, inputAge, inputSex, inputAddress, 
                                             inputPhoneNum, inputStatus});

                    clearAndHideAddDialog();
                    frame.setSaved(panelIndex, false);
                }
            }
        }   
    }
    
    private void addRow(Object[] data) {
        int oldRowCount = currentTable.getRowCount();

        DefaultTableModel model = (DefaultTableModel) currentTable.getModel();
        model.addRow(data);
        
        int newRowCount = currentTable.getRowCount();

        if(newRowCount != oldRowCount) {
            TableRenderingSupporter.selectRowAndScrollTableToCell(currentTable, newRowCount - 1, 0);
        }

        String message = "Record added successfully!";
        JOptionPane.showMessageDialog(null, message, MainFrame.TITLE, JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void handleEditDoctorAttempt() {
        String inputName = DBEditDoctorNameTextField.getText();
        String inputAge = DBEditDoctorAgeTextField.getText();
        String inputSex = ButtonGroupHandlingSupporter.getSelectedButtonText(DBEditDoctorButtonGroup);
        String inputAddress = DBEditDoctorAddressTextField.getText();
        String inputPhoneNum = DBEditDoctorPhoneNumTextField.getText();
        String inputWY = DBEditDoctorWYTextField.getText();
        String inputSalary = DBEditDoctorSalaryTextField.getText();
        String inputMajors = DBEditDoctorMajorsTextField.getText();

        if(inputName.isEmpty() || inputAge.isEmpty() || inputSex.isEmpty()
            || inputWY.isEmpty() || inputSalary.isEmpty() || inputMajors.isEmpty()) {
            String message = "Required information not filled!";
            MainFrame.makeBeepSound();
            JOptionPane.showMessageDialog(null, message, MainFrame.TITLE, JOptionPane.ERROR_MESSAGE);
        }
        else {
            String inputID = DBEditDoctorIDTextField.getText();
            int ID = Integer.parseInt(inputID);
            String name = inputName;
            int age = Integer.parseInt(inputAge);
            Sex sex = Sex.valueOf(inputSex);
            String address = inputAddress;
            String phoneNum = inputPhoneNum;
            int WY = Integer.parseInt(inputWY);
            double salary = Double.parseDouble(inputSalary);
            String[] majors = StringHandlingSupporter.separateString(inputMajors, DELIMITER);
            
            inputSalary = salaryFormatter.format(salary);
            
            String[] updatedData = {inputID, inputName,  inputAge, inputSex, inputAddress, 
                                   inputPhoneNum, inputWY, inputSalary, inputMajors};
            int selectedRow = currentTable.getSelectedRow();
            int modelRow = currentTable.convertRowIndexToModel(selectedRow);
            if(TableRenderingSupporter.isRowIdentical(currentTable, modelRow, updatedData)) {
                clearAndHideEditDialog();
            }
            else {
                String message = "Confirm to edit the selected record with the given information?";

                int choice = JOptionPane.showConfirmDialog(null, message, MainFrame.TITLE, 
                                                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if(choice == JOptionPane.YES_OPTION) {
                    Doctor doctor = dataManager.getEmployeeDB().getDoctor(ID);
                    doctor.setName(name);
                    doctor.setAge(age);
                    doctor.setSex(sex);
                    doctor.setAddress(address);
                    doctor.setPhoneNum(phoneNum);
                    doctor.setWorkingYear(WY);
                    doctor.setSalary(salary);
                    doctor.removeAllMajors();
                    doctor.addMajor(majors);

                    updateSelectedRow(updatedData);

                    clearAndHideEditDialog();
                    frame.setSaved(panelIndex, false);
                }
            }
        }
    }
    
    private void handleEditNurseAttempt() {
        String inputName = DBEditNurseNameTextField.getText();
        String inputAge = DBEditNurseAgeTextField.getText();
        String inputSex = ButtonGroupHandlingSupporter.getSelectedButtonText(DBEditNurseButtonGroup);
        String inputAddress = DBEditNurseAddressTextField.getText();
        String inputPhoneNum = DBEditNursePhoneNumTextField.getText();
        String inputWY = DBEditNurseWYTextField.getText();
        String inputSalary = DBEditNurseSalaryTextField.getText();
        String inputPosition = DBEditNursePositionTextField.getText();

        if(inputName.isEmpty() || inputAge.isEmpty() || inputSex.isEmpty()
            || inputWY.isEmpty() || inputSalary.isEmpty() || inputPosition.isEmpty()) {
            String message = "Required information not filled!";
            MainFrame.makeBeepSound();
            JOptionPane.showMessageDialog(null, message, MainFrame.TITLE, JOptionPane.ERROR_MESSAGE);
        }
        else {
            String inputID = DBEditNurseIDTextField.getText();
            int ID = Integer.parseInt(inputID);
            String name = inputName;
            int age = Integer.parseInt(inputAge);
            Sex sex = Sex.valueOf(inputSex);
            String address = inputAddress;
            String phoneNum = inputPhoneNum;
            int WY = Integer.parseInt(inputWY);
            double salary = Double.parseDouble(inputSalary);
            String position = inputPosition;

            inputSalary = salaryFormatter.format(salary);
            
            String[] updatedData = {inputID, inputName,  inputAge, inputSex, inputAddress, 
                                   inputPhoneNum, inputWY, inputSalary, inputPosition};
            int selectedRow = currentTable.getSelectedRow();
            int modelRow = currentTable.convertRowIndexToModel(selectedRow);
            if(TableRenderingSupporter.isRowIdentical(currentTable, modelRow, updatedData)) {
                clearAndHideEditDialog();
            }
            else {
                String message = "Confirm to edit the selected record with the given information?";

                int choice = JOptionPane.showConfirmDialog(null, message, MainFrame.TITLE, 
                                                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if(choice == JOptionPane.YES_OPTION) {
                    Nurse nurse = dataManager.getEmployeeDB().getNurse(ID);
                    nurse.setName(name);
                    nurse.setAge(age);
                    nurse.setSex(sex);
                    nurse.setAddress(address);
                    nurse.setPhoneNum(phoneNum);
                    nurse.setWorkingYear(WY);
                    nurse.setSalary(salary);
                    nurse.setPosition(position);

                    updateSelectedRow(updatedData);

                    clearAndHideEditDialog();
                    frame.setSaved(panelIndex, false);
                }
            }
        }
    }
    
    private void handleEditPatientAttempt() {
        String inputName = DBEditPatientNameTextField.getText();
        String inputAge = DBEditPatientAgeTextField.getText();
        String inputSex = ButtonGroupHandlingSupporter.getSelectedButtonText(DBEditPatientButtonGroup);
        String inputAddress = DBEditPatientAddressTextField.getText();
        String inputPhoneNum = DBEditPatientPhoneNumTextField.getText();
        String inputStatus = DBEditPatientStatusTextField.getText();

        if(inputName.isEmpty() || inputAge.isEmpty() || inputSex.isEmpty() || inputStatus.isEmpty()) {
            String message = "Required information not filled!";
            MainFrame.makeBeepSound();
            JOptionPane.showMessageDialog(null, message, MainFrame.TITLE, JOptionPane.ERROR_MESSAGE);
        }
        else {
            String inputID = DBEditPatientIDTextField.getText();
            int ID = Integer.parseInt(inputID);
            String name = inputName;
            int age = Integer.parseInt(inputAge);
            Sex sex = Sex.valueOf(inputSex);
            String address = inputAddress;
            String phoneNum = inputPhoneNum;
            String status = inputStatus;
            
            String[] updatedData = {inputID, inputName,  inputAge, inputSex, inputAddress, 
                                   inputPhoneNum, inputStatus};
            int selectedRow = currentTable.getSelectedRow();
            int modelRow = currentTable.convertRowIndexToModel(selectedRow);
            if(TableRenderingSupporter.isRowIdentical(currentTable, modelRow, updatedData)) {
                clearAndHideEditDialog();
            }
            else {
                String message = "Confirm to edit the selected record with the given information?";

                int choice = JOptionPane.showConfirmDialog(null, message, MainFrame.TITLE, 
                                                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if(choice == JOptionPane.YES_OPTION) {
                    Patient patient = dataManager.getPatientDB().get(ID);
                    patient.setName(name);
                    patient.setAge(age);
                    patient.setSex(sex);
                    patient.setAddress(address);
                    patient.setPhoneNum(phoneNum);
                    patient.setStatus(status);

                    updateSelectedRow(updatedData);

                    clearAndHideEditDialog();
                    frame.setSaved(panelIndex, false);
                }
            }
        }
    }
    
    private void updateSelectedRow(Object[] data) {
        int selectedRow = currentTable.getSelectedRow();
        int modelRow = currentTable.convertRowIndexToModel(selectedRow);
        TableRenderingSupporter.updateRow(currentTable, modelRow, data);

        String message = "Record edited successfully!";
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

        DBDoctorNurseButtonGroup = new javax.swing.ButtonGroup();
        DBAddDialog = new javax.swing.JDialog();
        DBAddOptionPane = new JOptionPane(null, JOptionPane.PLAIN_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, new String[]{"OK", "Reset", "Cancel"});
        DBEditDialog = new javax.swing.JDialog();
        DBEditOptionPane = new JOptionPane(null, JOptionPane.PLAIN_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, new String[]{"OK", "Reset", "Cancel"});
        DBAddDoctorButtonGroup = new javax.swing.ButtonGroup();
        DBEditDoctorButtonGroup = new javax.swing.ButtonGroup();
        DBAddDoctorPanel = new javax.swing.JPanel();
        DBAddDoctorIDLabel = new javax.swing.JLabel();
        DBAddDoctorNameLabel = new javax.swing.JLabel();
        DBAddDoctorAgeLabel = new javax.swing.JLabel();
        DBAddDoctorSexLabel = new javax.swing.JLabel();
        DBAddDoctorAddressLabel = new javax.swing.JLabel();
        DBAddDoctorPhoneNumLabel = new javax.swing.JLabel();
        DBAddDoctorWYLabel = new javax.swing.JLabel();
        DBAddDoctorSalaryLabel = new javax.swing.JLabel();
        DBAddDoctorMajorsLabel = new javax.swing.JLabel();
        DBAddDoctorMaleCheckBox = new javax.swing.JCheckBox();
        DBAddDoctorFemaleCheckBox = new javax.swing.JCheckBox();
        DBAddDoctorOtherCheckBox = new javax.swing.JCheckBox();
        DBAddDoctorIDTextField = new javax.swing.JTextField();
        DBAddDoctorNameTextField = new javax.swing.JTextField();
        DBAddDoctorAgeTextField = new javax.swing.JTextField();
        DBAddDoctorAddressTextField = new javax.swing.JTextField();
        DBAddDoctorPhoneNumTextField = new javax.swing.JTextField();
        DBAddDoctorWYTextField = new javax.swing.JTextField();
        DBAddDoctorSalaryTextField = new javax.swing.JTextField();
        DBAddDoctorMajorsTextField = new javax.swing.JTextField();
        DBAddDoctorRequiredLabel = new javax.swing.JLabel();
        DBAddDoctorIDRequiredLabel = new javax.swing.JLabel();
        DBAddDoctorNameRequiredLabel = new javax.swing.JLabel();
        DBAddDoctorAgeRequiredLabel = new javax.swing.JLabel();
        DBAddDoctorSexRequiredLabel = new javax.swing.JLabel();
        DBAddDoctorWYRequiredLabel = new javax.swing.JLabel();
        DBAddDoctorSalaryRequiredLabel = new javax.swing.JLabel();
        DBAddDoctorMajorsRequiredLabel = new javax.swing.JLabel();
        DBEditDoctorPanel = new javax.swing.JPanel();
        DBEditDoctorIDLabel = new javax.swing.JLabel();
        DBEditDoctorNameLabel = new javax.swing.JLabel();
        DBEditDoctorAgeLabel = new javax.swing.JLabel();
        DBEditDoctorSexLabel = new javax.swing.JLabel();
        DBEditDoctorAddressLabel = new javax.swing.JLabel();
        DBEditDoctorPhoneNumLabel = new javax.swing.JLabel();
        DBEditDoctorWYLabel = new javax.swing.JLabel();
        DBEditDoctorSalaryLabel = new javax.swing.JLabel();
        DBEditDoctorMajorsLabel = new javax.swing.JLabel();
        DBEditDoctorMaleCheckBox = new javax.swing.JCheckBox();
        DBEditDoctorFemaleCheckBox = new javax.swing.JCheckBox();
        DBEditDoctorOtherCheckBox = new javax.swing.JCheckBox();
        DBEditDoctorIDTextField = new javax.swing.JTextField();
        DBEditDoctorNameTextField = new javax.swing.JTextField();
        DBEditDoctorAgeTextField = new javax.swing.JTextField();
        DBEditDoctorAddressTextField = new javax.swing.JTextField();
        DBEditDoctorPhoneNumTextField = new javax.swing.JTextField();
        DBEditDoctorWYTextField = new javax.swing.JTextField();
        DBEditDoctorSalaryTextField = new javax.swing.JTextField();
        DBEditDoctorMajorsTextField = new javax.swing.JTextField();
        DBEditDoctorRequiredLabel = new javax.swing.JLabel();
        DBEditDoctorNameRequiredLabel = new javax.swing.JLabel();
        DBEditDoctorAgeRequiredLabel = new javax.swing.JLabel();
        DBEditDoctorSexRequiredLabel = new javax.swing.JLabel();
        DBEditDoctorWYRequiredLabel = new javax.swing.JLabel();
        DBEditDoctorSalaryRequiredLabel = new javax.swing.JLabel();
        DBEditDoctorMajorsRequiredLabel = new javax.swing.JLabel();
        DBAddNurseButtonGroup = new javax.swing.ButtonGroup();
        DBEditNurseButtonGroup = new javax.swing.ButtonGroup();
        DBAddNursePanel = new javax.swing.JPanel();
        DBAddNurseIDLabel = new javax.swing.JLabel();
        DBAddNurseNameLabel = new javax.swing.JLabel();
        DBAddNurseAgeLabel = new javax.swing.JLabel();
        DBAddNurseSexLabel = new javax.swing.JLabel();
        DBAddNurseAddressLabel = new javax.swing.JLabel();
        DBAddNursePhoneNumLabel = new javax.swing.JLabel();
        DBAddNurseWYLabel = new javax.swing.JLabel();
        DBAddNurseSalaryLabel = new javax.swing.JLabel();
        DBAddNursePositionLabel = new javax.swing.JLabel();
        DBAddNurseMaleCheckBox = new javax.swing.JCheckBox();
        DBAddNurseFemaleCheckBox = new javax.swing.JCheckBox();
        DBAddNurseOtherCheckBox = new javax.swing.JCheckBox();
        DBAddNurseIDTextField = new javax.swing.JTextField();
        DBAddNurseNameTextField = new javax.swing.JTextField();
        DBAddNurseAgeTextField = new javax.swing.JTextField();
        DBAddNurseAddressTextField = new javax.swing.JTextField();
        DBAddNursePhoneNumTextField = new javax.swing.JTextField();
        DBAddNurseWYTextField = new javax.swing.JTextField();
        DBAddNurseSalaryTextField = new javax.swing.JTextField();
        DBAddNursePositionTextField = new javax.swing.JTextField();
        DBAddNurseRequiredLabel = new javax.swing.JLabel();
        DBAddNurseIDRequiredLabel = new javax.swing.JLabel();
        DBAddNurseNameRequiredLabel = new javax.swing.JLabel();
        DBAddNurseAgeRequiredLabel = new javax.swing.JLabel();
        DBAddNurseSexRequiredLabel = new javax.swing.JLabel();
        DBAddNurseWYRequiredLabel = new javax.swing.JLabel();
        DBAddNurseSalaryRequiredLabel = new javax.swing.JLabel();
        DBAddNursePositionRequiredLabel = new javax.swing.JLabel();
        DBEditNursePanel = new javax.swing.JPanel();
        DBEditNurseIDLabel = new javax.swing.JLabel();
        DBEditNurseNameLabel = new javax.swing.JLabel();
        DBEditNurseAgeLabel = new javax.swing.JLabel();
        DBEditNurseSexLabel = new javax.swing.JLabel();
        DBEditNurseAddressLabel = new javax.swing.JLabel();
        DBEditNursePhoneNumLabel = new javax.swing.JLabel();
        DBEditNurseWYLabel = new javax.swing.JLabel();
        DBEditNurseSalaryLabel = new javax.swing.JLabel();
        DBEditNurseMajorsLabel = new javax.swing.JLabel();
        DBEditNurseMaleCheckBox = new javax.swing.JCheckBox();
        DBEditNurseFemaleCheckBox = new javax.swing.JCheckBox();
        DBEditNurseOtherCheckBox = new javax.swing.JCheckBox();
        DBEditNurseIDTextField = new javax.swing.JTextField();
        DBEditNurseNameTextField = new javax.swing.JTextField();
        DBEditNurseAgeTextField = new javax.swing.JTextField();
        DBEditNurseAddressTextField = new javax.swing.JTextField();
        DBEditNursePhoneNumTextField = new javax.swing.JTextField();
        DBEditNurseWYTextField = new javax.swing.JTextField();
        DBEditNurseSalaryTextField = new javax.swing.JTextField();
        DBEditNursePositionTextField = new javax.swing.JTextField();
        DBEditNurseRequiredLabel = new javax.swing.JLabel();
        DBEditNurseNameRequiredLabel = new javax.swing.JLabel();
        DBEditNurseAgeRequiredLabel = new javax.swing.JLabel();
        DBEdiNurseSexRequiredLabel = new javax.swing.JLabel();
        DBEditNurseWYRequiredLabel = new javax.swing.JLabel();
        DBEditNurseSalaryRequiredLabel = new javax.swing.JLabel();
        DBEditNursePositionRequiredLabel = new javax.swing.JLabel();
        DBAddPatientButtonGroup = new javax.swing.ButtonGroup();
        DBEditPatientButtonGroup = new javax.swing.ButtonGroup();
        DBAddPatientPanel = new javax.swing.JPanel();
        DBAddPatientIDLabel = new javax.swing.JLabel();
        DBAddPatientNameLabel = new javax.swing.JLabel();
        DBAddPatientAgeLabel = new javax.swing.JLabel();
        DBAddPatientSexLabel = new javax.swing.JLabel();
        DBAddPatientAddressLabel = new javax.swing.JLabel();
        DBAddPatientPhoneNumLabel = new javax.swing.JLabel();
        DBAddPatientStatusLabel = new javax.swing.JLabel();
        DBAddPatientMaleCheckBox = new javax.swing.JCheckBox();
        DBAddPatientFemaleCheckBox = new javax.swing.JCheckBox();
        DBAddPatientOtherCheckBox = new javax.swing.JCheckBox();
        DBAddPatientIDTextField = new javax.swing.JTextField();
        DBAddPatientNameTextField = new javax.swing.JTextField();
        DBAddPatientAgeTextField = new javax.swing.JTextField();
        DBAddPatientAddressTextField = new javax.swing.JTextField();
        DBAddPatientPhoneNumTextField = new javax.swing.JTextField();
        DBAddPatientStatusTextField = new javax.swing.JTextField();
        DBAddPatientRequiredLabel = new javax.swing.JLabel();
        DBAddPatientIDRequiredLabel = new javax.swing.JLabel();
        DBAddPatientNameRequiredLabel = new javax.swing.JLabel();
        DBAddPatientAgeRequiredLabel = new javax.swing.JLabel();
        DBAddPatientSexRequiredLabel = new javax.swing.JLabel();
        DBAddPatientStatusRequiredLabel = new javax.swing.JLabel();
        DBEditPatientPanel = new javax.swing.JPanel();
        DBEditPatientIDLabel = new javax.swing.JLabel();
        DBEditPatientNameLabel = new javax.swing.JLabel();
        DBEditPatientAgeLabel = new javax.swing.JLabel();
        DBEditPatientSexLabel = new javax.swing.JLabel();
        DBEditPatientAddressLabel = new javax.swing.JLabel();
        DBEditPatientPhoneNumLabel = new javax.swing.JLabel();
        DBEditPatientStatusLabel = new javax.swing.JLabel();
        DBEditPatientMaleCheckBox = new javax.swing.JCheckBox();
        DBEditPatientFemaleCheckBox = new javax.swing.JCheckBox();
        DBEditPatientOtherCheckBox = new javax.swing.JCheckBox();
        DBEditPatientIDTextField = new javax.swing.JTextField();
        DBEditPatientNameTextField = new javax.swing.JTextField();
        DBEditPatientAgeTextField = new javax.swing.JTextField();
        DBEditPatientAddressTextField = new javax.swing.JTextField();
        DBEditPatientPhoneNumTextField = new javax.swing.JTextField();
        DBEditPatientStatusTextField = new javax.swing.JTextField();
        DBEditPatientRequiredLabel = new javax.swing.JLabel();
        DBEditPatientNameRequiredLabel = new javax.swing.JLabel();
        DBEditPatientAgeRequiredLabel = new javax.swing.JLabel();
        DBEditPatientSexRequiredLabel = new javax.swing.JLabel();
        DBEditPatientStatusRequiredLabel = new javax.swing.JLabel();
        DBPanel = new javax.swing.JPanel();
        DBUtilityField = new javax.swing.JPanel();
        DBAddButton = new javax.swing.JButton();
        DBEditButton = new javax.swing.JButton();
        DBDeleteButton = new javax.swing.JButton();
        DBKeywordTextField = new javax.swing.JTextField();
        DBSearchPromptLabel = new javax.swing.JLabel();
        DBSelectionField = new javax.swing.JPanel();
        DBSelectionBox = new javax.swing.JComboBox<>();
        DBButtonPanel = new javax.swing.JPanel();
        DBDoctorButton = new javax.swing.JRadioButton();
        DBNurseButton = new javax.swing.JRadioButton();
        DBTableField = new javax.swing.JPanel();
        DBNoSelectionPanel = new javax.swing.JPanel();
        DBDoctorScrollPane = new javax.swing.JScrollPane();
        doctorTable = new javax.swing.JTable();
        DBNurseScrollPane = new javax.swing.JScrollPane();
        nurseTable = new javax.swing.JTable();
        DBPatientScrollPane = new javax.swing.JScrollPane();
        patientTable = new javax.swing.JTable();

        DBAddDialog.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        DBAddDialog.setTitle("Add");
        DBAddDialog.setModal(true);
        DBAddDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                DBAddDialogWindowClosing(evt);
            }
        });

        DBAddOptionPane.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                DBAddOptionPanePropertyChange(evt);
            }
        });

        javax.swing.GroupLayout DBAddDialogLayout = new javax.swing.GroupLayout(DBAddDialog.getContentPane());
        DBAddDialog.getContentPane().setLayout(DBAddDialogLayout);
        DBAddDialogLayout.setHorizontalGroup(
            DBAddDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 414, Short.MAX_VALUE)
            .addGroup(DBAddDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(DBAddOptionPane, javax.swing.GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE))
        );
        DBAddDialogLayout.setVerticalGroup(
            DBAddDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 177, Short.MAX_VALUE)
            .addGroup(DBAddDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(DBAddOptionPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE))
        );

        DBEditDialog.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        DBEditDialog.setTitle("Edit");
        DBEditDialog.setModal(true);
        DBEditDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                DBEditDialogWindowClosing(evt);
            }
        });

        DBEditOptionPane.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                DBEditOptionPanePropertyChange(evt);
            }
        });

        javax.swing.GroupLayout DBEditDialogLayout = new javax.swing.GroupLayout(DBEditDialog.getContentPane());
        DBEditDialog.getContentPane().setLayout(DBEditDialogLayout);
        DBEditDialogLayout.setHorizontalGroup(
            DBEditDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(DBEditOptionPane, javax.swing.GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE)
        );
        DBEditDialogLayout.setVerticalGroup(
            DBEditDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(DBEditOptionPane, javax.swing.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE)
        );

        DBAddDoctorIDLabel.setText("ID:");

        DBAddDoctorNameLabel.setText("Name:");

        DBAddDoctorAgeLabel.setText("Age:");

        DBAddDoctorSexLabel.setText("Sex:");

        DBAddDoctorAddressLabel.setText("Address:");

        DBAddDoctorPhoneNumLabel.setText("Phone Number:");

        DBAddDoctorWYLabel.setText("Working Year:");

        DBAddDoctorSalaryLabel.setText("Salary:");

        DBAddDoctorMajorsLabel.setText("Majors:");

        DBAddDoctorButtonGroup.add(DBAddDoctorMaleCheckBox);
        DBAddDoctorMaleCheckBox.setText("MALE");

        DBAddDoctorButtonGroup.add(DBAddDoctorFemaleCheckBox);
        DBAddDoctorFemaleCheckBox.setText("FEMALE");

        DBAddDoctorButtonGroup.add(DBAddDoctorOtherCheckBox);
        DBAddDoctorOtherCheckBox.setText("OTHER");

        DBAddDoctorRequiredLabel.setForeground(new java.awt.Color(255, 0, 0));
        DBAddDoctorRequiredLabel.setText("*: Required information");

        DBAddDoctorIDRequiredLabel.setForeground(new java.awt.Color(255, 0, 0));
        DBAddDoctorIDRequiredLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        DBAddDoctorIDRequiredLabel.setText("*");

        DBAddDoctorNameRequiredLabel.setForeground(new java.awt.Color(255, 0, 0));
        DBAddDoctorNameRequiredLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        DBAddDoctorNameRequiredLabel.setText("*");

        DBAddDoctorAgeRequiredLabel.setForeground(new java.awt.Color(255, 0, 0));
        DBAddDoctorAgeRequiredLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        DBAddDoctorAgeRequiredLabel.setText("*");

        DBAddDoctorSexRequiredLabel.setForeground(new java.awt.Color(255, 0, 0));
        DBAddDoctorSexRequiredLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        DBAddDoctorSexRequiredLabel.setText("*");

        DBAddDoctorWYRequiredLabel.setForeground(new java.awt.Color(255, 0, 0));
        DBAddDoctorWYRequiredLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        DBAddDoctorWYRequiredLabel.setText("*");

        DBAddDoctorSalaryRequiredLabel.setForeground(new java.awt.Color(255, 0, 0));
        DBAddDoctorSalaryRequiredLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        DBAddDoctorSalaryRequiredLabel.setText("*");

        DBAddDoctorMajorsRequiredLabel.setForeground(new java.awt.Color(255, 0, 0));
        DBAddDoctorMajorsRequiredLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        DBAddDoctorMajorsRequiredLabel.setText("*");

        javax.swing.GroupLayout DBAddDoctorPanelLayout = new javax.swing.GroupLayout(DBAddDoctorPanel);
        DBAddDoctorPanel.setLayout(DBAddDoctorPanelLayout);
        DBAddDoctorPanelLayout.setHorizontalGroup(
            DBAddDoctorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DBAddDoctorPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(DBAddDoctorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DBAddDoctorPanelLayout.createSequentialGroup()
                        .addComponent(DBAddDoctorRequiredLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 118, Short.MAX_VALUE))
                    .addGroup(DBAddDoctorPanelLayout.createSequentialGroup()
                        .addGroup(DBAddDoctorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(DBAddDoctorPanelLayout.createSequentialGroup()
                                .addComponent(DBAddDoctorAddressLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(DBAddDoctorAddressTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE))
                            .addGroup(DBAddDoctorPanelLayout.createSequentialGroup()
                                .addGroup(DBAddDoctorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DBAddDoctorPanelLayout.createSequentialGroup()
                                        .addComponent(DBAddDoctorAgeRequiredLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(DBAddDoctorAgeLabel))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DBAddDoctorPanelLayout.createSequentialGroup()
                                        .addComponent(DBAddDoctorNameRequiredLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(DBAddDoctorNameLabel))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DBAddDoctorPanelLayout.createSequentialGroup()
                                        .addComponent(DBAddDoctorIDRequiredLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(DBAddDoctorIDLabel))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DBAddDoctorPanelLayout.createSequentialGroup()
                                        .addComponent(DBAddDoctorSexRequiredLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(DBAddDoctorSexLabel)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(DBAddDoctorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(DBAddDoctorIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(DBAddDoctorAgeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(DBAddDoctorNameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)
                                    .addGroup(DBAddDoctorPanelLayout.createSequentialGroup()
                                        .addComponent(DBAddDoctorMaleCheckBox)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(DBAddDoctorFemaleCheckBox)
                                        .addGap(18, 18, 18)
                                        .addComponent(DBAddDoctorOtherCheckBox))))
                            .addGroup(DBAddDoctorPanelLayout.createSequentialGroup()
                                .addComponent(DBAddDoctorPhoneNumLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(DBAddDoctorPhoneNumTextField))
                            .addGroup(DBAddDoctorPanelLayout.createSequentialGroup()
                                .addComponent(DBAddDoctorSalaryRequiredLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(DBAddDoctorSalaryLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(DBAddDoctorSalaryTextField))
                            .addGroup(DBAddDoctorPanelLayout.createSequentialGroup()
                                .addComponent(DBAddDoctorMajorsRequiredLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(DBAddDoctorMajorsLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(DBAddDoctorMajorsTextField))
                            .addGroup(DBAddDoctorPanelLayout.createSequentialGroup()
                                .addComponent(DBAddDoctorWYRequiredLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(DBAddDoctorWYLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(DBAddDoctorWYTextField)))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );

        DBAddDoctorPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {DBAddDoctorAddressTextField, DBAddDoctorAgeTextField, DBAddDoctorIDTextField, DBAddDoctorMajorsTextField, DBAddDoctorNameTextField, DBAddDoctorPhoneNumTextField, DBAddDoctorSalaryTextField, DBAddDoctorWYTextField});

        DBAddDoctorPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {DBAddDoctorFemaleCheckBox, DBAddDoctorMaleCheckBox, DBAddDoctorOtherCheckBox});

        DBAddDoctorPanelLayout.setVerticalGroup(
            DBAddDoctorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DBAddDoctorPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(DBAddDoctorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DBAddDoctorIDLabel)
                    .addComponent(DBAddDoctorIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DBAddDoctorIDRequiredLabel))
                .addGap(19, 19, 19)
                .addGroup(DBAddDoctorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DBAddDoctorNameLabel)
                    .addComponent(DBAddDoctorNameRequiredLabel)
                    .addComponent(DBAddDoctorNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(DBAddDoctorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DBAddDoctorAgeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DBAddDoctorAgeLabel)
                    .addComponent(DBAddDoctorAgeRequiredLabel))
                .addGap(18, 18, 18)
                .addGroup(DBAddDoctorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DBAddDoctorMaleCheckBox)
                    .addComponent(DBAddDoctorFemaleCheckBox)
                    .addComponent(DBAddDoctorOtherCheckBox)
                    .addComponent(DBAddDoctorSexLabel)
                    .addComponent(DBAddDoctorSexRequiredLabel))
                .addGap(18, 18, 18)
                .addGroup(DBAddDoctorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DBAddDoctorAddressLabel)
                    .addComponent(DBAddDoctorAddressTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(DBAddDoctorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DBAddDoctorPhoneNumLabel)
                    .addComponent(DBAddDoctorPhoneNumTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(DBAddDoctorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DBAddDoctorWYLabel)
                    .addComponent(DBAddDoctorWYTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DBAddDoctorWYRequiredLabel))
                .addGap(18, 18, 18)
                .addGroup(DBAddDoctorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DBAddDoctorSalaryLabel)
                    .addComponent(DBAddDoctorSalaryTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DBAddDoctorSalaryRequiredLabel))
                .addGap(18, 18, 18)
                .addGroup(DBAddDoctorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(DBAddDoctorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(DBAddDoctorMajorsLabel)
                        .addComponent(DBAddDoctorMajorsRequiredLabel))
                    .addComponent(DBAddDoctorMajorsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addComponent(DBAddDoctorRequiredLabel))
        );

        DBAddDoctorPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {DBAddDoctorAddressTextField, DBAddDoctorAgeTextField, DBAddDoctorIDTextField, DBAddDoctorMajorsTextField, DBAddDoctorNameTextField, DBAddDoctorPhoneNumTextField, DBAddDoctorSalaryTextField, DBAddDoctorWYTextField});

        DBEditDoctorIDLabel.setText("ID:");

        DBEditDoctorNameLabel.setText("Name:");

        DBEditDoctorAgeLabel.setText("Age:");

        DBEditDoctorSexLabel.setText("Sex:");

        DBEditDoctorAddressLabel.setText("Address:");

        DBEditDoctorPhoneNumLabel.setText("Phone Number:");

        DBEditDoctorWYLabel.setText("Working Year:");

        DBEditDoctorSalaryLabel.setText("Salary:");

        DBEditDoctorMajorsLabel.setText("Majors:");

        DBEditDoctorButtonGroup.add(DBEditDoctorMaleCheckBox);
        DBEditDoctorMaleCheckBox.setText("MALE");

        DBEditDoctorButtonGroup.add(DBEditDoctorFemaleCheckBox);
        DBEditDoctorFemaleCheckBox.setText("FEMALE");

        DBEditDoctorButtonGroup.add(DBEditDoctorOtherCheckBox);
        DBEditDoctorOtherCheckBox.setText("OTHER");

        DBEditDoctorIDTextField.setEditable(false);

        DBEditDoctorRequiredLabel.setForeground(new java.awt.Color(255, 0, 0));
        DBEditDoctorRequiredLabel.setText("*: Required information");

        DBEditDoctorNameRequiredLabel.setForeground(new java.awt.Color(255, 0, 0));
        DBEditDoctorNameRequiredLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        DBEditDoctorNameRequiredLabel.setText("*");

        DBEditDoctorAgeRequiredLabel.setForeground(new java.awt.Color(255, 0, 0));
        DBEditDoctorAgeRequiredLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        DBEditDoctorAgeRequiredLabel.setText("*");

        DBEditDoctorSexRequiredLabel.setForeground(new java.awt.Color(255, 0, 0));
        DBEditDoctorSexRequiredLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        DBEditDoctorSexRequiredLabel.setText("*");

        DBEditDoctorWYRequiredLabel.setForeground(new java.awt.Color(255, 0, 0));
        DBEditDoctorWYRequiredLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        DBEditDoctorWYRequiredLabel.setText("*");

        DBEditDoctorSalaryRequiredLabel.setForeground(new java.awt.Color(255, 0, 0));
        DBEditDoctorSalaryRequiredLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        DBEditDoctorSalaryRequiredLabel.setText("*");

        DBEditDoctorMajorsRequiredLabel.setForeground(new java.awt.Color(255, 0, 0));
        DBEditDoctorMajorsRequiredLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        DBEditDoctorMajorsRequiredLabel.setText("*");

        javax.swing.GroupLayout DBEditDoctorPanelLayout = new javax.swing.GroupLayout(DBEditDoctorPanel);
        DBEditDoctorPanel.setLayout(DBEditDoctorPanelLayout);
        DBEditDoctorPanelLayout.setHorizontalGroup(
            DBEditDoctorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DBEditDoctorPanelLayout.createSequentialGroup()
                .addComponent(DBEditDoctorRequiredLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DBEditDoctorPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(DBEditDoctorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(DBEditDoctorPanelLayout.createSequentialGroup()
                        .addComponent(DBEditDoctorMajorsRequiredLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DBEditDoctorMajorsLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DBEditDoctorMajorsTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, DBEditDoctorPanelLayout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addGroup(DBEditDoctorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(DBEditDoctorPanelLayout.createSequentialGroup()
                                .addComponent(DBEditDoctorPhoneNumLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(DBEditDoctorPhoneNumTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(DBEditDoctorPanelLayout.createSequentialGroup()
                                .addGroup(DBEditDoctorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DBEditDoctorPanelLayout.createSequentialGroup()
                                        .addComponent(DBEditDoctorNameRequiredLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(DBEditDoctorNameLabel))
                                    .addComponent(DBEditDoctorIDLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DBEditDoctorPanelLayout.createSequentialGroup()
                                        .addComponent(DBEditDoctorAgeRequiredLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(DBEditDoctorAgeLabel))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DBEditDoctorPanelLayout.createSequentialGroup()
                                        .addComponent(DBEditDoctorSexRequiredLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(DBEditDoctorSexLabel)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(DBEditDoctorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(DBEditDoctorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(DBEditDoctorAgeTextField, javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(DBEditDoctorNameTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(DBEditDoctorIDTextField, javax.swing.GroupLayout.Alignment.TRAILING))
                                    .addGroup(DBEditDoctorPanelLayout.createSequentialGroup()
                                        .addComponent(DBEditDoctorMaleCheckBox)
                                        .addGap(18, 18, 18)
                                        .addComponent(DBEditDoctorFemaleCheckBox)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(DBEditDoctorOtherCheckBox))))
                            .addGroup(DBEditDoctorPanelLayout.createSequentialGroup()
                                .addComponent(DBEditDoctorSalaryRequiredLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(DBEditDoctorSalaryLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(DBEditDoctorSalaryTextField))
                            .addGroup(DBEditDoctorPanelLayout.createSequentialGroup()
                                .addComponent(DBEditDoctorAddressLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(DBEditDoctorAddressTextField))))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, DBEditDoctorPanelLayout.createSequentialGroup()
                        .addComponent(DBEditDoctorWYRequiredLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DBEditDoctorWYLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DBEditDoctorWYTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(63, 63, 63))
        );

        DBEditDoctorPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {DBEditDoctorAddressTextField, DBEditDoctorAgeTextField, DBEditDoctorIDTextField, DBEditDoctorMajorsTextField, DBEditDoctorNameTextField, DBEditDoctorPhoneNumTextField, DBEditDoctorSalaryTextField, DBEditDoctorWYTextField});

        DBEditDoctorPanelLayout.setVerticalGroup(
            DBEditDoctorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DBEditDoctorPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(DBEditDoctorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DBEditDoctorIDLabel)
                    .addComponent(DBEditDoctorIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(19, 19, 19)
                .addGroup(DBEditDoctorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DBEditDoctorNameLabel)
                    .addComponent(DBEditDoctorNameRequiredLabel)
                    .addComponent(DBEditDoctorNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(DBEditDoctorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DBEditDoctorAgeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DBEditDoctorAgeLabel)
                    .addComponent(DBEditDoctorAgeRequiredLabel))
                .addGap(21, 21, 21)
                .addGroup(DBEditDoctorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DBEditDoctorMaleCheckBox)
                    .addComponent(DBEditDoctorFemaleCheckBox)
                    .addComponent(DBEditDoctorOtherCheckBox)
                    .addComponent(DBEditDoctorSexLabel)
                    .addComponent(DBEditDoctorSexRequiredLabel))
                .addGap(18, 18, 18)
                .addGroup(DBEditDoctorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DBEditDoctorAddressLabel)
                    .addComponent(DBEditDoctorAddressTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(DBEditDoctorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DBEditDoctorPhoneNumLabel)
                    .addComponent(DBEditDoctorPhoneNumTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(DBEditDoctorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DBEditDoctorWYLabel)
                    .addComponent(DBEditDoctorWYTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DBEditDoctorWYRequiredLabel))
                .addGap(18, 18, 18)
                .addGroup(DBEditDoctorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DBEditDoctorSalaryLabel)
                    .addComponent(DBEditDoctorSalaryTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DBEditDoctorSalaryRequiredLabel))
                .addGap(18, 18, 18)
                .addGroup(DBEditDoctorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DBEditDoctorMajorsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DBEditDoctorMajorsLabel)
                    .addComponent(DBEditDoctorMajorsRequiredLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(DBEditDoctorRequiredLabel))
        );

        DBEditDoctorPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {DBEditDoctorAddressTextField, DBEditDoctorMajorsTextField, DBEditDoctorPhoneNumTextField, DBEditDoctorSalaryTextField, DBEditDoctorWYTextField});

        DBAddNurseIDLabel.setText("ID:");

        DBAddNurseNameLabel.setText("Name:");

        DBAddNurseAgeLabel.setText("Age:");

        DBAddNurseSexLabel.setText("Sex:");

        DBAddNurseAddressLabel.setText("Address:");

        DBAddNursePhoneNumLabel.setText("Phone Number:");

        DBAddNurseWYLabel.setText("Working Year:");

        DBAddNurseSalaryLabel.setText("Salary:");

        DBAddNursePositionLabel.setText("Position:");

        DBAddNurseButtonGroup.add(DBAddNurseMaleCheckBox);
        DBAddNurseMaleCheckBox.setText("MALE");

        DBAddNurseButtonGroup.add(DBAddNurseFemaleCheckBox);
        DBAddNurseFemaleCheckBox.setText("FEMALE");

        DBAddNurseButtonGroup.add(DBAddNurseOtherCheckBox);
        DBAddNurseOtherCheckBox.setText("OTHER");

        DBAddNurseRequiredLabel.setForeground(new java.awt.Color(255, 0, 0));
        DBAddNurseRequiredLabel.setText("*: Required information");

        DBAddNurseIDRequiredLabel.setForeground(new java.awt.Color(255, 0, 0));
        DBAddNurseIDRequiredLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        DBAddNurseIDRequiredLabel.setText("*");

        DBAddNurseNameRequiredLabel.setForeground(new java.awt.Color(255, 0, 0));
        DBAddNurseNameRequiredLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        DBAddNurseNameRequiredLabel.setText("*");

        DBAddNurseAgeRequiredLabel.setForeground(new java.awt.Color(255, 0, 0));
        DBAddNurseAgeRequiredLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        DBAddNurseAgeRequiredLabel.setText("*");

        DBAddNurseSexRequiredLabel.setForeground(new java.awt.Color(255, 0, 0));
        DBAddNurseSexRequiredLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        DBAddNurseSexRequiredLabel.setText("*");

        DBAddNurseWYRequiredLabel.setForeground(new java.awt.Color(255, 0, 0));
        DBAddNurseWYRequiredLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        DBAddNurseWYRequiredLabel.setText("*");

        DBAddNurseSalaryRequiredLabel.setForeground(new java.awt.Color(255, 0, 0));
        DBAddNurseSalaryRequiredLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        DBAddNurseSalaryRequiredLabel.setText("*");

        DBAddNursePositionRequiredLabel.setForeground(new java.awt.Color(255, 0, 0));
        DBAddNursePositionRequiredLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        DBAddNursePositionRequiredLabel.setText("*");

        javax.swing.GroupLayout DBAddNursePanelLayout = new javax.swing.GroupLayout(DBAddNursePanel);
        DBAddNursePanel.setLayout(DBAddNursePanelLayout);
        DBAddNursePanelLayout.setHorizontalGroup(
            DBAddNursePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DBAddNursePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(DBAddNursePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DBAddNursePanelLayout.createSequentialGroup()
                        .addComponent(DBAddNursePositionRequiredLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DBAddNursePositionLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DBAddNursePositionTextField))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DBAddNursePanelLayout.createSequentialGroup()
                        .addComponent(DBAddNurseSalaryRequiredLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DBAddNurseSalaryLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DBAddNurseSalaryTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DBAddNursePanelLayout.createSequentialGroup()
                        .addComponent(DBAddNurseWYRequiredLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DBAddNurseWYLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DBAddNurseWYTextField))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DBAddNursePanelLayout.createSequentialGroup()
                        .addComponent(DBAddNursePhoneNumLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DBAddNursePhoneNumTextField))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DBAddNursePanelLayout.createSequentialGroup()
                        .addComponent(DBAddNurseAddressLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DBAddNurseAddressTextField))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DBAddNursePanelLayout.createSequentialGroup()
                        .addGroup(DBAddNursePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DBAddNursePanelLayout.createSequentialGroup()
                                .addComponent(DBAddNurseAgeRequiredLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(DBAddNurseAgeLabel))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DBAddNursePanelLayout.createSequentialGroup()
                                .addComponent(DBAddNurseNameRequiredLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(DBAddNurseNameLabel))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DBAddNursePanelLayout.createSequentialGroup()
                                .addComponent(DBAddNurseIDRequiredLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(DBAddNurseIDLabel))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DBAddNursePanelLayout.createSequentialGroup()
                                .addComponent(DBAddNurseSexRequiredLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(DBAddNurseSexLabel)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(DBAddNursePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(DBAddNurseIDTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)
                            .addComponent(DBAddNurseAgeTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)
                            .addGroup(DBAddNursePanelLayout.createSequentialGroup()
                                .addComponent(DBAddNurseMaleCheckBox)
                                .addGap(18, 18, 18)
                                .addComponent(DBAddNurseFemaleCheckBox)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(DBAddNurseOtherCheckBox))
                            .addComponent(DBAddNurseNameTextField))))
                .addContainerGap(63, Short.MAX_VALUE))
            .addGroup(DBAddNursePanelLayout.createSequentialGroup()
                .addComponent(DBAddNurseRequiredLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        DBAddNursePanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {DBAddNurseAddressTextField, DBAddNurseAgeTextField, DBAddNurseIDTextField, DBAddNurseNameTextField, DBAddNursePhoneNumTextField, DBAddNursePositionTextField, DBAddNurseSalaryTextField, DBAddNurseWYTextField});

        DBAddNursePanelLayout.setVerticalGroup(
            DBAddNursePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DBAddNursePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(DBAddNursePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DBAddNurseIDLabel)
                    .addComponent(DBAddNurseIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DBAddNurseIDRequiredLabel))
                .addGap(19, 19, 19)
                .addGroup(DBAddNursePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DBAddNurseNameLabel)
                    .addComponent(DBAddNurseNameRequiredLabel)
                    .addComponent(DBAddNurseNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(DBAddNursePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DBAddNurseAgeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DBAddNurseAgeLabel)
                    .addComponent(DBAddNurseAgeRequiredLabel))
                .addGap(18, 18, 18)
                .addGroup(DBAddNursePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DBAddNurseMaleCheckBox)
                    .addComponent(DBAddNurseFemaleCheckBox)
                    .addComponent(DBAddNurseOtherCheckBox)
                    .addComponent(DBAddNurseSexLabel)
                    .addComponent(DBAddNurseSexRequiredLabel))
                .addGap(18, 18, 18)
                .addGroup(DBAddNursePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DBAddNurseAddressLabel)
                    .addComponent(DBAddNurseAddressTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(DBAddNursePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DBAddNursePhoneNumLabel)
                    .addComponent(DBAddNursePhoneNumTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(DBAddNursePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DBAddNurseWYLabel)
                    .addComponent(DBAddNurseWYTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DBAddNurseWYRequiredLabel))
                .addGap(18, 18, 18)
                .addGroup(DBAddNursePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DBAddNurseSalaryLabel)
                    .addComponent(DBAddNurseSalaryTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DBAddNurseSalaryRequiredLabel))
                .addGap(18, 18, 18)
                .addGroup(DBAddNursePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(DBAddNursePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(DBAddNursePositionLabel)
                        .addComponent(DBAddNursePositionRequiredLabel))
                    .addComponent(DBAddNursePositionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addComponent(DBAddNurseRequiredLabel))
        );

        DBAddNursePanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {DBAddNurseAddressTextField, DBAddNurseAgeTextField, DBAddNurseIDTextField, DBAddNurseNameTextField, DBAddNursePhoneNumTextField, DBAddNursePositionTextField, DBAddNurseSalaryTextField, DBAddNurseWYTextField});

        DBEditNurseIDLabel.setText("ID:");

        DBEditNurseNameLabel.setText("Name:");

        DBEditNurseAgeLabel.setText("Age:");

        DBEditNurseSexLabel.setText("Sex:");

        DBEditNurseAddressLabel.setText("Address:");

        DBEditNursePhoneNumLabel.setText("Phone Number:");

        DBEditNurseWYLabel.setText("Working Year:");

        DBEditNurseSalaryLabel.setText("Salary:");

        DBEditNurseMajorsLabel.setText("Position:");

        DBEditNurseButtonGroup.add(DBEditNurseMaleCheckBox);
        DBEditNurseMaleCheckBox.setText("MALE");

        DBEditNurseButtonGroup.add(DBEditNurseFemaleCheckBox);
        DBEditNurseFemaleCheckBox.setText("FEMALE");

        DBEditNurseButtonGroup.add(DBEditNurseOtherCheckBox);
        DBEditNurseOtherCheckBox.setText("OTHER");

        DBEditNurseIDTextField.setEditable(false);

        DBEditNurseRequiredLabel.setForeground(new java.awt.Color(255, 0, 0));
        DBEditNurseRequiredLabel.setText("*: Required information");

        DBEditNurseNameRequiredLabel.setForeground(new java.awt.Color(255, 0, 0));
        DBEditNurseNameRequiredLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        DBEditNurseNameRequiredLabel.setText("*");

        DBEditNurseAgeRequiredLabel.setForeground(new java.awt.Color(255, 0, 0));
        DBEditNurseAgeRequiredLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        DBEditNurseAgeRequiredLabel.setText("*");

        DBEdiNurseSexRequiredLabel.setForeground(new java.awt.Color(255, 0, 0));
        DBEdiNurseSexRequiredLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        DBEdiNurseSexRequiredLabel.setText("*");

        DBEditNurseWYRequiredLabel.setForeground(new java.awt.Color(255, 0, 0));
        DBEditNurseWYRequiredLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        DBEditNurseWYRequiredLabel.setText("*");

        DBEditNurseSalaryRequiredLabel.setForeground(new java.awt.Color(255, 0, 0));
        DBEditNurseSalaryRequiredLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        DBEditNurseSalaryRequiredLabel.setText("*");

        DBEditNursePositionRequiredLabel.setForeground(new java.awt.Color(255, 0, 0));
        DBEditNursePositionRequiredLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        DBEditNursePositionRequiredLabel.setText("*");

        javax.swing.GroupLayout DBEditNursePanelLayout = new javax.swing.GroupLayout(DBEditNursePanel);
        DBEditNursePanel.setLayout(DBEditNursePanelLayout);
        DBEditNursePanelLayout.setHorizontalGroup(
            DBEditNursePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DBEditNursePanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(DBEditNursePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(DBEditNurseRequiredLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(DBEditNursePanelLayout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addGroup(DBEditNursePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, DBEditNursePanelLayout.createSequentialGroup()
                                .addGap(34, 34, 34)
                                .addComponent(DBEdiNurseSexRequiredLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(DBEditNurseSexLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(DBEditNurseMaleCheckBox)
                                .addGap(18, 18, 18)
                                .addComponent(DBEditNurseFemaleCheckBox)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(DBEditNurseOtherCheckBox))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, DBEditNursePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(DBEditNursePanelLayout.createSequentialGroup()
                                    .addGroup(DBEditNursePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(DBEditNursePanelLayout.createSequentialGroup()
                                            .addComponent(DBEditNurseAgeRequiredLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(DBEditNurseAgeLabel))
                                        .addComponent(DBEditNurseIDLabel)
                                        .addGroup(DBEditNursePanelLayout.createSequentialGroup()
                                            .addComponent(DBEditNurseNameRequiredLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(DBEditNurseNameLabel)))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(DBEditNursePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(DBEditNurseNameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)
                                        .addComponent(DBEditNurseIDTextField)
                                        .addComponent(DBEditNurseAgeTextField)))
                                .addGroup(DBEditNursePanelLayout.createSequentialGroup()
                                    .addGroup(DBEditNursePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(DBEditNursePhoneNumLabel)
                                        .addComponent(DBEditNurseAddressLabel))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(DBEditNursePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(DBEditNurseAddressTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE)
                                        .addComponent(DBEditNursePhoneNumTextField))))))
                    .addGroup(DBEditNursePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(DBEditNursePanelLayout.createSequentialGroup()
                            .addComponent(DBEditNurseSalaryRequiredLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(DBEditNurseSalaryLabel)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(DBEditNurseSalaryTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(DBEditNursePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(DBEditNursePanelLayout.createSequentialGroup()
                                .addComponent(DBEditNursePositionRequiredLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(DBEditNurseMajorsLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(DBEditNursePositionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, DBEditNursePanelLayout.createSequentialGroup()
                                .addComponent(DBEditNurseWYRequiredLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(DBEditNurseWYLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(DBEditNurseWYTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(63, 63, 63))
        );

        DBEditNursePanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {DBEditNurseAddressTextField, DBEditNurseAgeTextField, DBEditNurseIDTextField, DBEditNurseNameTextField, DBEditNursePhoneNumTextField, DBEditNursePositionTextField, DBEditNurseSalaryTextField, DBEditNurseWYTextField});

        DBEditNursePanelLayout.setVerticalGroup(
            DBEditNursePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DBEditNursePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(DBEditNursePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DBEditNurseIDLabel)
                    .addComponent(DBEditNurseIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(19, 19, 19)
                .addGroup(DBEditNursePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DBEditNurseNameLabel)
                    .addComponent(DBEditNurseNameRequiredLabel)
                    .addComponent(DBEditNurseNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(DBEditNursePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DBEditNurseAgeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(DBEditNursePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(DBEditNurseAgeLabel)
                        .addComponent(DBEditNurseAgeRequiredLabel)))
                .addGap(21, 21, 21)
                .addGroup(DBEditNursePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DBEditNurseMaleCheckBox)
                    .addComponent(DBEditNurseFemaleCheckBox)
                    .addComponent(DBEditNurseOtherCheckBox)
                    .addComponent(DBEditNurseSexLabel)
                    .addComponent(DBEdiNurseSexRequiredLabel))
                .addGap(18, 18, 18)
                .addGroup(DBEditNursePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DBEditNurseAddressLabel)
                    .addComponent(DBEditNurseAddressTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(DBEditNursePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DBEditNursePhoneNumLabel)
                    .addComponent(DBEditNursePhoneNumTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(DBEditNursePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DBEditNurseWYLabel)
                    .addComponent(DBEditNurseWYTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DBEditNurseWYRequiredLabel))
                .addGap(18, 18, 18)
                .addGroup(DBEditNursePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DBEditNurseSalaryLabel)
                    .addComponent(DBEditNurseSalaryTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DBEditNurseSalaryRequiredLabel))
                .addGap(18, 18, 18)
                .addGroup(DBEditNursePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DBEditNursePositionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DBEditNurseMajorsLabel)
                    .addComponent(DBEditNursePositionRequiredLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(DBEditNurseRequiredLabel))
        );

        DBEditNursePanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {DBEditNurseAddressTextField, DBEditNurseAgeTextField, DBEditNurseIDTextField, DBEditNurseNameTextField, DBEditNursePhoneNumTextField, DBEditNursePositionTextField, DBEditNurseSalaryTextField, DBEditNurseWYTextField});

        DBAddPatientIDLabel.setText("ID:");

        DBAddPatientNameLabel.setText("Name:");

        DBAddPatientAgeLabel.setText("Age:");

        DBAddPatientSexLabel.setText("Sex:");

        DBAddPatientAddressLabel.setText("Address:");

        DBAddPatientPhoneNumLabel.setText("Phone Number:");

        DBAddPatientStatusLabel.setText("Status:");

        DBAddPatientButtonGroup.add(DBAddPatientMaleCheckBox);
        DBAddPatientMaleCheckBox.setText("MALE");

        DBAddPatientButtonGroup.add(DBAddPatientFemaleCheckBox);
        DBAddPatientFemaleCheckBox.setText("FEMALE");

        DBAddPatientButtonGroup.add(DBAddPatientOtherCheckBox);
        DBAddPatientOtherCheckBox.setText("OTHER");

        DBAddPatientRequiredLabel.setForeground(new java.awt.Color(255, 0, 0));
        DBAddPatientRequiredLabel.setText("*: Required information");

        DBAddPatientIDRequiredLabel.setForeground(new java.awt.Color(255, 0, 0));
        DBAddPatientIDRequiredLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        DBAddPatientIDRequiredLabel.setText("*");

        DBAddPatientNameRequiredLabel.setForeground(new java.awt.Color(255, 0, 0));
        DBAddPatientNameRequiredLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        DBAddPatientNameRequiredLabel.setText("*");

        DBAddPatientAgeRequiredLabel.setForeground(new java.awt.Color(255, 0, 0));
        DBAddPatientAgeRequiredLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        DBAddPatientAgeRequiredLabel.setText("*");

        DBAddPatientSexRequiredLabel.setForeground(new java.awt.Color(255, 0, 0));
        DBAddPatientSexRequiredLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        DBAddPatientSexRequiredLabel.setText("*");

        DBAddPatientStatusRequiredLabel.setForeground(new java.awt.Color(255, 0, 0));
        DBAddPatientStatusRequiredLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        DBAddPatientStatusRequiredLabel.setText("*");

        javax.swing.GroupLayout DBAddPatientPanelLayout = new javax.swing.GroupLayout(DBAddPatientPanel);
        DBAddPatientPanel.setLayout(DBAddPatientPanelLayout);
        DBAddPatientPanelLayout.setHorizontalGroup(
            DBAddPatientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DBAddPatientPanelLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(DBAddPatientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DBAddPatientPanelLayout.createSequentialGroup()
                        .addComponent(DBAddPatientPhoneNumLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DBAddPatientPhoneNumTextField))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DBAddPatientPanelLayout.createSequentialGroup()
                        .addGroup(DBAddPatientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(DBAddPatientAddressLabel)
                            .addGroup(DBAddPatientPanelLayout.createSequentialGroup()
                                .addComponent(DBAddPatientAgeRequiredLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(DBAddPatientAgeLabel))
                            .addGroup(DBAddPatientPanelLayout.createSequentialGroup()
                                .addComponent(DBAddPatientNameRequiredLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(DBAddPatientNameLabel))
                            .addGroup(DBAddPatientPanelLayout.createSequentialGroup()
                                .addComponent(DBAddPatientIDRequiredLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(DBAddPatientIDLabel))
                            .addGroup(DBAddPatientPanelLayout.createSequentialGroup()
                                .addComponent(DBAddPatientSexRequiredLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(DBAddPatientSexLabel)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(DBAddPatientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(DBAddPatientAddressTextField)
                            .addComponent(DBAddPatientIDTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)
                            .addComponent(DBAddPatientAgeTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)
                            .addGroup(DBAddPatientPanelLayout.createSequentialGroup()
                                .addComponent(DBAddPatientMaleCheckBox)
                                .addGap(18, 18, 18)
                                .addComponent(DBAddPatientFemaleCheckBox)
                                .addGap(18, 18, 18)
                                .addComponent(DBAddPatientOtherCheckBox))
                            .addComponent(DBAddPatientNameTextField)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DBAddPatientPanelLayout.createSequentialGroup()
                        .addComponent(DBAddPatientStatusRequiredLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DBAddPatientStatusLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DBAddPatientStatusTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(63, Short.MAX_VALUE))
            .addGroup(DBAddPatientPanelLayout.createSequentialGroup()
                .addComponent(DBAddPatientRequiredLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        DBAddPatientPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {DBAddPatientAddressTextField, DBAddPatientAgeTextField, DBAddPatientIDTextField, DBAddPatientNameTextField, DBAddPatientPhoneNumTextField, DBAddPatientStatusTextField});

        DBAddPatientPanelLayout.setVerticalGroup(
            DBAddPatientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DBAddPatientPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(DBAddPatientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DBAddPatientIDLabel)
                    .addComponent(DBAddPatientIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DBAddPatientIDRequiredLabel))
                .addGap(19, 19, 19)
                .addGroup(DBAddPatientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DBAddPatientNameLabel)
                    .addComponent(DBAddPatientNameRequiredLabel)
                    .addComponent(DBAddPatientNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(DBAddPatientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DBAddPatientAgeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DBAddPatientAgeLabel)
                    .addComponent(DBAddPatientAgeRequiredLabel))
                .addGap(18, 18, 18)
                .addGroup(DBAddPatientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DBAddPatientMaleCheckBox)
                    .addComponent(DBAddPatientFemaleCheckBox)
                    .addComponent(DBAddPatientOtherCheckBox)
                    .addComponent(DBAddPatientSexLabel)
                    .addComponent(DBAddPatientSexRequiredLabel))
                .addGap(18, 18, 18)
                .addGroup(DBAddPatientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DBAddPatientAddressLabel)
                    .addComponent(DBAddPatientAddressTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(DBAddPatientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DBAddPatientPhoneNumLabel)
                    .addComponent(DBAddPatientPhoneNumTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(DBAddPatientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DBAddPatientStatusTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DBAddPatientStatusLabel)
                    .addComponent(DBAddPatientStatusRequiredLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                .addComponent(DBAddPatientRequiredLabel))
        );

        DBEditPatientIDLabel.setText("ID:");

        DBEditPatientNameLabel.setText("Name:");

        DBEditPatientAgeLabel.setText("Age:");

        DBEditPatientSexLabel.setText("Sex:");

        DBEditPatientAddressLabel.setText("Address:");

        DBEditPatientPhoneNumLabel.setText("Phone Number:");

        DBEditPatientStatusLabel.setText("Status:");

        DBEditPatientButtonGroup.add(DBEditPatientMaleCheckBox);
        DBEditPatientMaleCheckBox.setText("MALE");

        DBEditPatientButtonGroup.add(DBEditPatientFemaleCheckBox);
        DBEditPatientFemaleCheckBox.setText("FEMALE");

        DBEditPatientButtonGroup.add(DBEditPatientOtherCheckBox);
        DBEditPatientOtherCheckBox.setText("OTHER");

        DBEditPatientIDTextField.setEditable(false);

        DBEditPatientRequiredLabel.setForeground(new java.awt.Color(255, 0, 0));
        DBEditPatientRequiredLabel.setText("*: Required information");

        DBEditPatientNameRequiredLabel.setForeground(new java.awt.Color(255, 0, 0));
        DBEditPatientNameRequiredLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        DBEditPatientNameRequiredLabel.setText("*");

        DBEditPatientAgeRequiredLabel.setForeground(new java.awt.Color(255, 0, 0));
        DBEditPatientAgeRequiredLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        DBEditPatientAgeRequiredLabel.setText("*");

        DBEditPatientSexRequiredLabel.setForeground(new java.awt.Color(255, 0, 0));
        DBEditPatientSexRequiredLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        DBEditPatientSexRequiredLabel.setText("*");

        DBEditPatientStatusRequiredLabel.setForeground(new java.awt.Color(255, 0, 0));
        DBEditPatientStatusRequiredLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        DBEditPatientStatusRequiredLabel.setText("*");

        javax.swing.GroupLayout DBEditPatientPanelLayout = new javax.swing.GroupLayout(DBEditPatientPanel);
        DBEditPatientPanel.setLayout(DBEditPatientPanelLayout);
        DBEditPatientPanelLayout.setHorizontalGroup(
            DBEditPatientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DBEditPatientPanelLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(DBEditPatientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DBEditPatientPanelLayout.createSequentialGroup()
                        .addComponent(DBEditPatientPhoneNumLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(DBEditPatientPhoneNumTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DBEditPatientPanelLayout.createSequentialGroup()
                        .addGroup(DBEditPatientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(DBEditPatientAddressLabel)
                            .addGroup(DBEditPatientPanelLayout.createSequentialGroup()
                                .addComponent(DBEditPatientAgeRequiredLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(DBEditPatientAgeLabel))
                            .addGroup(DBEditPatientPanelLayout.createSequentialGroup()
                                .addComponent(DBEditPatientNameRequiredLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(DBEditPatientNameLabel))
                            .addComponent(DBEditPatientIDLabel)
                            .addGroup(DBEditPatientPanelLayout.createSequentialGroup()
                                .addComponent(DBEditPatientSexRequiredLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(DBEditPatientSexLabel)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(DBEditPatientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(DBEditPatientAddressTextField)
                            .addComponent(DBEditPatientIDTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)
                            .addComponent(DBEditPatientAgeTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)
                            .addGroup(DBEditPatientPanelLayout.createSequentialGroup()
                                .addComponent(DBEditPatientMaleCheckBox)
                                .addGap(18, 18, 18)
                                .addComponent(DBEditPatientFemaleCheckBox)
                                .addGap(18, 18, 18)
                                .addComponent(DBEditPatientOtherCheckBox))
                            .addComponent(DBEditPatientNameTextField)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DBEditPatientPanelLayout.createSequentialGroup()
                        .addComponent(DBEditPatientStatusRequiredLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DBEditPatientStatusLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DBEditPatientStatusTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(44, Short.MAX_VALUE))
            .addGroup(DBEditPatientPanelLayout.createSequentialGroup()
                .addComponent(DBEditPatientRequiredLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        DBEditPatientPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {DBEditPatientAddressTextField, DBEditPatientAgeTextField, DBEditPatientIDTextField, DBEditPatientNameTextField, DBEditPatientPhoneNumTextField, DBEditPatientStatusTextField});

        DBEditPatientPanelLayout.setVerticalGroup(
            DBEditPatientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DBEditPatientPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(DBEditPatientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DBEditPatientIDLabel)
                    .addComponent(DBEditPatientIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(19, 19, 19)
                .addGroup(DBEditPatientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DBEditPatientNameLabel)
                    .addComponent(DBEditPatientNameRequiredLabel)
                    .addComponent(DBEditPatientNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(DBEditPatientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DBEditPatientAgeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DBEditPatientAgeLabel)
                    .addComponent(DBEditPatientAgeRequiredLabel))
                .addGap(18, 18, 18)
                .addGroup(DBEditPatientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DBEditPatientMaleCheckBox)
                    .addComponent(DBEditPatientFemaleCheckBox)
                    .addComponent(DBEditPatientOtherCheckBox)
                    .addComponent(DBEditPatientSexLabel)
                    .addComponent(DBEditPatientSexRequiredLabel))
                .addGap(18, 18, 18)
                .addGroup(DBEditPatientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DBEditPatientAddressLabel)
                    .addComponent(DBEditPatientAddressTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(DBEditPatientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DBEditPatientPhoneNumLabel)
                    .addComponent(DBEditPatientPhoneNumTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(DBEditPatientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DBEditPatientStatusTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DBEditPatientStatusLabel)
                    .addComponent(DBEditPatientStatusRequiredLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                .addComponent(DBEditPatientRequiredLabel))
        );

        DBEditPatientPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {DBEditPatientAddressTextField, DBEditPatientAgeTextField, DBEditPatientIDTextField, DBEditPatientNameTextField, DBEditPatientPhoneNumTextField, DBEditPatientStatusTextField});

        DBUtilityField.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        DBAddButton.setText("Add");
        DBAddButton.setToolTipText("Add a record to the selected database");
        DBAddButton.setFocusPainted(false);
        DBAddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DBAddButtonActionPerformed(evt);
            }
        });

        DBEditButton.setText("Edit");
        DBEditButton.setToolTipText("Edit the selected record");
        DBEditButton.setFocusPainted(false);
        DBEditButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DBEditButtonActionPerformed(evt);
            }
        });

        DBDeleteButton.setText("Delete");
        DBDeleteButton.setToolTipText("Delete the selected record <Del>");
        DBDeleteButton.setFocusPainted(false);
        DBDeleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DBDeleteButtonActionPerformed(evt);
            }
        });

        DBSearchPromptLabel.setText("Searching keyword:");

        javax.swing.GroupLayout DBUtilityFieldLayout = new javax.swing.GroupLayout(DBUtilityField);
        DBUtilityField.setLayout(DBUtilityFieldLayout);
        DBUtilityFieldLayout.setHorizontalGroup(
            DBUtilityFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DBUtilityFieldLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(DBUtilityFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(DBUtilityFieldLayout.createSequentialGroup()
                        .addComponent(DBSearchPromptLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(DBKeywordTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 482, Short.MAX_VALUE))
                    .addGroup(DBUtilityFieldLayout.createSequentialGroup()
                        .addComponent(DBAddButton, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(DBEditButton, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(DBDeleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        DBUtilityFieldLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {DBAddButton, DBDeleteButton, DBEditButton});

        DBUtilityFieldLayout.setVerticalGroup(
            DBUtilityFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DBUtilityFieldLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(DBUtilityFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DBKeywordTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DBSearchPromptLabel))
                .addGap(12, 12, 12)
                .addGroup(DBUtilityFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DBAddButton)
                    .addComponent(DBEditButton)
                    .addComponent(DBDeleteButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        DBUtilityFieldLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {DBAddButton, DBDeleteButton, DBEditButton});

        DBSelectionBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select a database", "Employee Database", "Patient Database" }));
        DBSelectionBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DBSelectionBoxActionPerformed(evt);
            }
        });

        DBDoctorNurseButtonGroup.add(DBDoctorButton);
        DBDoctorButton.setText("Doctor");
        DBDoctorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DBDoctorButtonActionPerformed(evt);
            }
        });

        DBDoctorNurseButtonGroup.add(DBNurseButton);
        DBNurseButton.setText("Nurse");
        DBNurseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DBNurseButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout DBButtonPanelLayout = new javax.swing.GroupLayout(DBButtonPanel);
        DBButtonPanel.setLayout(DBButtonPanelLayout);
        DBButtonPanelLayout.setHorizontalGroup(
            DBButtonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DBButtonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(DBDoctorButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 41, Short.MAX_VALUE)
                .addComponent(DBNurseButton)
                .addGap(61, 61, 61))
        );
        DBButtonPanelLayout.setVerticalGroup(
            DBButtonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DBButtonPanelLayout.createSequentialGroup()
                .addGroup(DBButtonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DBNurseButton, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DBDoctorButton, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 4, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout DBSelectionFieldLayout = new javax.swing.GroupLayout(DBSelectionField);
        DBSelectionField.setLayout(DBSelectionFieldLayout);
        DBSelectionFieldLayout.setHorizontalGroup(
            DBSelectionFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DBSelectionFieldLayout.createSequentialGroup()
                .addGroup(DBSelectionFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(DBSelectionBox, javax.swing.GroupLayout.PREFERRED_SIZE, 371, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DBButtonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(45, Short.MAX_VALUE))
        );
        DBSelectionFieldLayout.setVerticalGroup(
            DBSelectionFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DBSelectionFieldLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(DBSelectionBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(DBButtonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(10, Short.MAX_VALUE))
        );

        DBTableField.setLayout(new java.awt.CardLayout());

        DBNoSelectionPanel.setPreferredSize(new java.awt.Dimension(1035, 520));

        javax.swing.GroupLayout DBNoSelectionPanelLayout = new javax.swing.GroupLayout(DBNoSelectionPanel);
        DBNoSelectionPanel.setLayout(DBNoSelectionPanelLayout);
        DBNoSelectionPanelLayout.setHorizontalGroup(
            DBNoSelectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1039, Short.MAX_VALUE)
        );
        DBNoSelectionPanelLayout.setVerticalGroup(
            DBNoSelectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 528, Short.MAX_VALUE)
        );

        DBTableField.add(DBNoSelectionPanel, "noSelection");

        DBDoctorScrollPane.setPreferredSize(new java.awt.Dimension(1052, 520));

        doctorTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Name", "Age", "Sex", "Address", "Phone Number", "WY", "Salary", "Majors"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        doctorTable.setRowHeight(20);
        doctorTable.getTableHeader().setReorderingAllowed(false);
        doctorTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                doctorTableMousePressed(evt);
            }
        });
        DBDoctorScrollPane.setViewportView(doctorTable);
        if (doctorTable.getColumnModel().getColumnCount() > 0) {
            doctorTable.getColumnModel().getColumn(0).setPreferredWidth(30);
            doctorTable.getColumnModel().getColumn(2).setPreferredWidth(30);
            doctorTable.getColumnModel().getColumn(3).setPreferredWidth(30);
            doctorTable.getColumnModel().getColumn(6).setPreferredWidth(30);
        }

        DBTableField.add(DBDoctorScrollPane, "doctorSelection");

        DBNurseScrollPane.setPreferredSize(new java.awt.Dimension(1052, 520));

        nurseTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Name", "Age", "Sex", "Address", "Phone Number", "WY", "Salary", "Position"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        nurseTable.setRowHeight(20);
        nurseTable.getTableHeader().setReorderingAllowed(false);
        nurseTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                nurseTableMousePressed(evt);
            }
        });
        DBNurseScrollPane.setViewportView(nurseTable);
        if (nurseTable.getColumnModel().getColumnCount() > 0) {
            nurseTable.getColumnModel().getColumn(0).setPreferredWidth(30);
            nurseTable.getColumnModel().getColumn(2).setPreferredWidth(30);
            nurseTable.getColumnModel().getColumn(3).setPreferredWidth(30);
            nurseTable.getColumnModel().getColumn(6).setPreferredWidth(30);
        }

        DBTableField.add(DBNurseScrollPane, "nurseSelection");

        DBPatientScrollPane.setPreferredSize(new java.awt.Dimension(1052, 520));

        patientTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Name", "Age", "Sex", "Address", "Phone Number", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        patientTable.setRowHeight(20);
        patientTable.getTableHeader().setReorderingAllowed(false);
        patientTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                patientTableMousePressed(evt);
            }
        });
        DBPatientScrollPane.setViewportView(patientTable);
        if (patientTable.getColumnModel().getColumnCount() > 0) {
            patientTable.getColumnModel().getColumn(0).setPreferredWidth(30);
            patientTable.getColumnModel().getColumn(2).setPreferredWidth(30);
            patientTable.getColumnModel().getColumn(3).setPreferredWidth(30);
        }

        DBTableField.add(DBPatientScrollPane, "patientSelection");

        javax.swing.GroupLayout DBPanelLayout = new javax.swing.GroupLayout(DBPanel);
        DBPanel.setLayout(DBPanelLayout);
        DBPanelLayout.setHorizontalGroup(
            DBPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DBPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(DBSelectionField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(DBUtilityField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(DBPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(DBPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(DBTableField, javax.swing.GroupLayout.DEFAULT_SIZE, 1035, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        DBPanelLayout.setVerticalGroup(
            DBPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DBPanelLayout.createSequentialGroup()
                .addGroup(DBPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(DBPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(DBSelectionField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(DBUtilityField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(543, Short.MAX_VALUE))
            .addGroup(DBPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DBPanelLayout.createSequentialGroup()
                    .addContainerGap(90, Short.MAX_VALUE)
                    .addComponent(DBTableField, javax.swing.GroupLayout.PREFERRED_SIZE, 528, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(DBPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(DBPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void DBSelectionBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DBSelectionBoxActionPerformed
        CardLayout layout = (CardLayout) DBTableField.getLayout();

        int index = DBSelectionBox.getSelectedIndex();
        switch (index) {
            case EMPLOYEE_INDEX -> {
                resetView();
                DBButtonPanel.setVisible(true);
                DBUtilityField.setVisible(true);
                if(DBDoctorButton.isSelected()) {
                    layout.show(DBTableField, "doctorSelection");
                    currentTable = doctorTable;
                }
                else {
                    layout.show(DBTableField, "nurseSelection");
                    currentTable = nurseTable;
                }
            }
            case PATIENT_INDEX -> {
                resetView();
                DBButtonPanel.setVisible(false);
                DBUtilityField.setVisible(true);
                layout.show(DBTableField, "patientSelection");
                currentTable = patientTable;
            }
            default -> {
                DBButtonPanel.setVisible(false);
                DBUtilityField.setVisible(false);
                layout.show(DBTableField, "noSelection");
                currentTable = null;
            }
        }
    }//GEN-LAST:event_DBSelectionBoxActionPerformed

    private void DBDoctorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DBDoctorButtonActionPerformed
        resetView();
        CardLayout layout = (CardLayout) DBTableField.getLayout();
        layout.show(DBTableField, "doctorSelection");
        currentTable = doctorTable;
    }//GEN-LAST:event_DBDoctorButtonActionPerformed

    private void DBNurseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DBNurseButtonActionPerformed
        resetView();
        CardLayout layout = (CardLayout) DBTableField.getLayout();
        layout.show(DBTableField, "nurseSelection");
        currentTable = nurseTable;
    }//GEN-LAST:event_DBNurseButtonActionPerformed

    private void DBAddButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DBAddButtonActionPerformed
        prepareAndOpenAddDialog();
    }//GEN-LAST:event_DBAddButtonActionPerformed

    private void DBAddDialogWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_DBAddDialogWindowClosing
        DBAddOptionPane.setValue(JOptionPane.CLOSED_OPTION);
    }//GEN-LAST:event_DBAddDialogWindowClosing

    private void DBAddOptionPanePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_DBAddOptionPanePropertyChange
        String prop = evt.getPropertyName();
        if(DBAddDialog.isVisible() && (evt.getSource() == DBAddOptionPane) 
            && (prop.equals(JOptionPane.VALUE_PROPERTY))) {
            
            Object value = DBAddOptionPane.getValue();
            
            if(value == JOptionPane.UNINITIALIZED_VALUE) {
                // Ignore reset
                return;
            }
            
            // Reset value so that later on, if the same button is pressed,
            // a property change event will be fired again
            DBAddOptionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
            if(value.equals("OK")) {
                if(currentTable == doctorTable) {
                    handleAddDoctorAttempt();
                }
                else if(currentTable == nurseTable) {
                    handleAddNurseAttempt();
                }
                else if(currentTable == patientTable) {
                    handleAddPatientAttempt();
                }
            }
            else if(value.equals("Reset")) {
                clearAddDialog();
            }
            else {
                clearAndHideAddDialog();
            }
        }
    }//GEN-LAST:event_DBAddOptionPanePropertyChange

    private void DBEditButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DBEditButtonActionPerformed
        int selectedRow = currentTable.getSelectedRow();
        prepareAndOpenEditDialog(Integer.parseInt((String) currentTable.getValueAt(selectedRow, 0)));
    }//GEN-LAST:event_DBEditButtonActionPerformed

    private void DBEditDialogWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_DBEditDialogWindowClosing
        DBEditOptionPane.setValue(JOptionPane.CLOSED_OPTION);
    }//GEN-LAST:event_DBEditDialogWindowClosing

    private void DBEditOptionPanePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_DBEditOptionPanePropertyChange
        String prop = evt.getPropertyName();
        if(DBEditDialog.isVisible() && (evt.getSource() == DBEditOptionPane) 
            && (prop.equals(JOptionPane.VALUE_PROPERTY))) {
            
            Object value = DBEditOptionPane.getValue();
            
            if(value == JOptionPane.UNINITIALIZED_VALUE) {
                // Ignore reset
                return;
            }
            
            // Reset value so that later on, if the same button is pressed,
            // a property change event will be fired again
            DBEditOptionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
            if(value.equals("OK")) {
                if(currentTable == doctorTable) {
                    handleEditDoctorAttempt();
                }
                else if(currentTable == nurseTable) {
                    handleEditNurseAttempt();
                }
                else if(currentTable == patientTable) {
                    handleEditPatientAttempt();
                }
            }
            else if(value.equals("Reset")) {
                int selectedRow = currentTable.getSelectedRow();
                int currentID = Integer.parseInt((String) currentTable.getValueAt(selectedRow, 0));
                if(currentTable == doctorTable) {
                    resetEditDialog(dataManager.getEmployeeDB().getDoctor(currentID));
                }
                else if(currentTable == nurseTable) {
                    resetEditDialog(dataManager.getEmployeeDB().getNurse(currentID));
                }
                else if(currentTable == patientTable) {
                    resetEditDialog(dataManager.getPatientDB().get(currentID));
                }
            }
            else {
                clearAndHideEditDialog();
            }
        }
    }//GEN-LAST:event_DBEditOptionPanePropertyChange

    private void DBDeleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DBDeleteButtonActionPerformed
        String message = "Do you want to delete the selected record?";
        int choice = JOptionPane.showConfirmDialog(null, message, MainFrame.TITLE, 
                                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if(choice == JOptionPane.YES_OPTION) {
            int selectedRow = currentTable.getSelectedRow();
            int currentID = Integer.parseInt((String) currentTable.getValueAt(selectedRow, 0));
            
            if(currentTable == doctorTable || currentTable == nurseTable ) {
                EmployeeDatabase employeeDB = dataManager.getEmployeeDB();
                employeeDB.remove(currentID);
            }
            else {
                PatientDatabase patientDB = dataManager.getPatientDB();
                patientDB.remove(currentID);
            }
            
            DefaultTableModel model = (DefaultTableModel) currentTable.getModel();
            int modelRow = currentTable.convertRowIndexToModel(selectedRow);
            model.removeRow(modelRow);
            
            if(currentTable.getRowCount() == 0) {
                currentTable.clearSelection();
                renewUtilityButtons();
            }
            else if(selectedRow == currentTable.getRowCount()) {
                TableRenderingSupporter.selectRowAndScrollTableToCell(currentTable, selectedRow - 1, 0);
            }
            else {
                TableRenderingSupporter.selectRowAndScrollTableToCell(currentTable, selectedRow, 0);
            }
            
            message = "Record deleted successfully!";
            JOptionPane.showMessageDialog(null, message, MainFrame.TITLE, JOptionPane.INFORMATION_MESSAGE);
            
            frame.setSaved(panelIndex, false);
        }
    }//GEN-LAST:event_DBDeleteButtonActionPerformed

    private void doctorTableMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_doctorTableMousePressed
        // Double click event
        if(evt.getClickCount() == 2 && !evt.isConsumed()) {
            evt.consume();
            handleTableDoubleClick(doctorTable);
        }
        // Single click event
        else {
            handleTableSingleClick(doctorTable);
        }
    }//GEN-LAST:event_doctorTableMousePressed

    private void nurseTableMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nurseTableMousePressed
        // Double click event
        if(evt.getClickCount() == 2 && !evt.isConsumed()) {
            evt.consume();
            handleTableDoubleClick(nurseTable);
        }
        // Single click event
        else {
            handleTableSingleClick(nurseTable);
        }
    }//GEN-LAST:event_nurseTableMousePressed

    private void patientTableMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_patientTableMousePressed
        // Double click event
        if(evt.getClickCount() == 2 && !evt.isConsumed()) {
            evt.consume();
            handleTableDoubleClick(patientTable);
        }
        // Single click event
        else {
            handleTableSingleClick(patientTable);
        }
    }//GEN-LAST:event_patientTableMousePressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton DBAddButton;
    private javax.swing.JDialog DBAddDialog;
    private javax.swing.JLabel DBAddDoctorAddressLabel;
    private javax.swing.JTextField DBAddDoctorAddressTextField;
    private javax.swing.JLabel DBAddDoctorAgeLabel;
    private javax.swing.JLabel DBAddDoctorAgeRequiredLabel;
    private javax.swing.JTextField DBAddDoctorAgeTextField;
    private javax.swing.ButtonGroup DBAddDoctorButtonGroup;
    private javax.swing.JCheckBox DBAddDoctorFemaleCheckBox;
    private javax.swing.JLabel DBAddDoctorIDLabel;
    private javax.swing.JLabel DBAddDoctorIDRequiredLabel;
    private javax.swing.JTextField DBAddDoctorIDTextField;
    private javax.swing.JLabel DBAddDoctorMajorsLabel;
    private javax.swing.JLabel DBAddDoctorMajorsRequiredLabel;
    private javax.swing.JTextField DBAddDoctorMajorsTextField;
    private javax.swing.JCheckBox DBAddDoctorMaleCheckBox;
    private javax.swing.JLabel DBAddDoctorNameLabel;
    private javax.swing.JLabel DBAddDoctorNameRequiredLabel;
    private javax.swing.JTextField DBAddDoctorNameTextField;
    private javax.swing.JCheckBox DBAddDoctorOtherCheckBox;
    private javax.swing.JPanel DBAddDoctorPanel;
    private javax.swing.JLabel DBAddDoctorPhoneNumLabel;
    private javax.swing.JTextField DBAddDoctorPhoneNumTextField;
    private javax.swing.JLabel DBAddDoctorRequiredLabel;
    private javax.swing.JLabel DBAddDoctorSalaryLabel;
    private javax.swing.JLabel DBAddDoctorSalaryRequiredLabel;
    private javax.swing.JTextField DBAddDoctorSalaryTextField;
    private javax.swing.JLabel DBAddDoctorSexLabel;
    private javax.swing.JLabel DBAddDoctorSexRequiredLabel;
    private javax.swing.JLabel DBAddDoctorWYLabel;
    private javax.swing.JLabel DBAddDoctorWYRequiredLabel;
    private javax.swing.JTextField DBAddDoctorWYTextField;
    private javax.swing.JLabel DBAddNurseAddressLabel;
    private javax.swing.JTextField DBAddNurseAddressTextField;
    private javax.swing.JLabel DBAddNurseAgeLabel;
    private javax.swing.JLabel DBAddNurseAgeRequiredLabel;
    private javax.swing.JTextField DBAddNurseAgeTextField;
    private javax.swing.ButtonGroup DBAddNurseButtonGroup;
    private javax.swing.JCheckBox DBAddNurseFemaleCheckBox;
    private javax.swing.JLabel DBAddNurseIDLabel;
    private javax.swing.JLabel DBAddNurseIDRequiredLabel;
    private javax.swing.JTextField DBAddNurseIDTextField;
    private javax.swing.JCheckBox DBAddNurseMaleCheckBox;
    private javax.swing.JLabel DBAddNurseNameLabel;
    private javax.swing.JLabel DBAddNurseNameRequiredLabel;
    private javax.swing.JTextField DBAddNurseNameTextField;
    private javax.swing.JCheckBox DBAddNurseOtherCheckBox;
    private javax.swing.JPanel DBAddNursePanel;
    private javax.swing.JLabel DBAddNursePhoneNumLabel;
    private javax.swing.JTextField DBAddNursePhoneNumTextField;
    private javax.swing.JLabel DBAddNursePositionLabel;
    private javax.swing.JLabel DBAddNursePositionRequiredLabel;
    private javax.swing.JTextField DBAddNursePositionTextField;
    private javax.swing.JLabel DBAddNurseRequiredLabel;
    private javax.swing.JLabel DBAddNurseSalaryLabel;
    private javax.swing.JLabel DBAddNurseSalaryRequiredLabel;
    private javax.swing.JTextField DBAddNurseSalaryTextField;
    private javax.swing.JLabel DBAddNurseSexLabel;
    private javax.swing.JLabel DBAddNurseSexRequiredLabel;
    private javax.swing.JLabel DBAddNurseWYLabel;
    private javax.swing.JLabel DBAddNurseWYRequiredLabel;
    private javax.swing.JTextField DBAddNurseWYTextField;
    private javax.swing.JOptionPane DBAddOptionPane;
    private javax.swing.JLabel DBAddPatientAddressLabel;
    private javax.swing.JTextField DBAddPatientAddressTextField;
    private javax.swing.JLabel DBAddPatientAgeLabel;
    private javax.swing.JLabel DBAddPatientAgeRequiredLabel;
    private javax.swing.JTextField DBAddPatientAgeTextField;
    private javax.swing.ButtonGroup DBAddPatientButtonGroup;
    private javax.swing.JCheckBox DBAddPatientFemaleCheckBox;
    private javax.swing.JLabel DBAddPatientIDLabel;
    private javax.swing.JLabel DBAddPatientIDRequiredLabel;
    private javax.swing.JTextField DBAddPatientIDTextField;
    private javax.swing.JCheckBox DBAddPatientMaleCheckBox;
    private javax.swing.JLabel DBAddPatientNameLabel;
    private javax.swing.JLabel DBAddPatientNameRequiredLabel;
    private javax.swing.JTextField DBAddPatientNameTextField;
    private javax.swing.JCheckBox DBAddPatientOtherCheckBox;
    private javax.swing.JPanel DBAddPatientPanel;
    private javax.swing.JLabel DBAddPatientPhoneNumLabel;
    private javax.swing.JTextField DBAddPatientPhoneNumTextField;
    private javax.swing.JLabel DBAddPatientRequiredLabel;
    private javax.swing.JLabel DBAddPatientSexLabel;
    private javax.swing.JLabel DBAddPatientSexRequiredLabel;
    private javax.swing.JLabel DBAddPatientStatusLabel;
    private javax.swing.JLabel DBAddPatientStatusRequiredLabel;
    private javax.swing.JTextField DBAddPatientStatusTextField;
    private javax.swing.JPanel DBButtonPanel;
    private javax.swing.JButton DBDeleteButton;
    private javax.swing.JRadioButton DBDoctorButton;
    private javax.swing.ButtonGroup DBDoctorNurseButtonGroup;
    private javax.swing.JScrollPane DBDoctorScrollPane;
    private javax.swing.JLabel DBEdiNurseSexRequiredLabel;
    private javax.swing.JButton DBEditButton;
    private javax.swing.JDialog DBEditDialog;
    private javax.swing.JLabel DBEditDoctorAddressLabel;
    private javax.swing.JTextField DBEditDoctorAddressTextField;
    private javax.swing.JLabel DBEditDoctorAgeLabel;
    private javax.swing.JLabel DBEditDoctorAgeRequiredLabel;
    private javax.swing.JTextField DBEditDoctorAgeTextField;
    private javax.swing.ButtonGroup DBEditDoctorButtonGroup;
    private javax.swing.JCheckBox DBEditDoctorFemaleCheckBox;
    private javax.swing.JLabel DBEditDoctorIDLabel;
    private javax.swing.JTextField DBEditDoctorIDTextField;
    private javax.swing.JLabel DBEditDoctorMajorsLabel;
    private javax.swing.JLabel DBEditDoctorMajorsRequiredLabel;
    private javax.swing.JTextField DBEditDoctorMajorsTextField;
    private javax.swing.JCheckBox DBEditDoctorMaleCheckBox;
    private javax.swing.JLabel DBEditDoctorNameLabel;
    private javax.swing.JLabel DBEditDoctorNameRequiredLabel;
    private javax.swing.JTextField DBEditDoctorNameTextField;
    private javax.swing.JCheckBox DBEditDoctorOtherCheckBox;
    private javax.swing.JPanel DBEditDoctorPanel;
    private javax.swing.JLabel DBEditDoctorPhoneNumLabel;
    private javax.swing.JTextField DBEditDoctorPhoneNumTextField;
    private javax.swing.JLabel DBEditDoctorRequiredLabel;
    private javax.swing.JLabel DBEditDoctorSalaryLabel;
    private javax.swing.JLabel DBEditDoctorSalaryRequiredLabel;
    private javax.swing.JTextField DBEditDoctorSalaryTextField;
    private javax.swing.JLabel DBEditDoctorSexLabel;
    private javax.swing.JLabel DBEditDoctorSexRequiredLabel;
    private javax.swing.JLabel DBEditDoctorWYLabel;
    private javax.swing.JLabel DBEditDoctorWYRequiredLabel;
    private javax.swing.JTextField DBEditDoctorWYTextField;
    private javax.swing.JLabel DBEditNurseAddressLabel;
    private javax.swing.JTextField DBEditNurseAddressTextField;
    private javax.swing.JLabel DBEditNurseAgeLabel;
    private javax.swing.JLabel DBEditNurseAgeRequiredLabel;
    private javax.swing.JTextField DBEditNurseAgeTextField;
    private javax.swing.ButtonGroup DBEditNurseButtonGroup;
    private javax.swing.JCheckBox DBEditNurseFemaleCheckBox;
    private javax.swing.JLabel DBEditNurseIDLabel;
    private javax.swing.JTextField DBEditNurseIDTextField;
    private javax.swing.JLabel DBEditNurseMajorsLabel;
    private javax.swing.JCheckBox DBEditNurseMaleCheckBox;
    private javax.swing.JLabel DBEditNurseNameLabel;
    private javax.swing.JLabel DBEditNurseNameRequiredLabel;
    private javax.swing.JTextField DBEditNurseNameTextField;
    private javax.swing.JCheckBox DBEditNurseOtherCheckBox;
    private javax.swing.JPanel DBEditNursePanel;
    private javax.swing.JLabel DBEditNursePhoneNumLabel;
    private javax.swing.JTextField DBEditNursePhoneNumTextField;
    private javax.swing.JLabel DBEditNursePositionRequiredLabel;
    private javax.swing.JTextField DBEditNursePositionTextField;
    private javax.swing.JLabel DBEditNurseRequiredLabel;
    private javax.swing.JLabel DBEditNurseSalaryLabel;
    private javax.swing.JLabel DBEditNurseSalaryRequiredLabel;
    private javax.swing.JTextField DBEditNurseSalaryTextField;
    private javax.swing.JLabel DBEditNurseSexLabel;
    private javax.swing.JLabel DBEditNurseWYLabel;
    private javax.swing.JLabel DBEditNurseWYRequiredLabel;
    private javax.swing.JTextField DBEditNurseWYTextField;
    private javax.swing.JOptionPane DBEditOptionPane;
    private javax.swing.JLabel DBEditPatientAddressLabel;
    private javax.swing.JTextField DBEditPatientAddressTextField;
    private javax.swing.JLabel DBEditPatientAgeLabel;
    private javax.swing.JLabel DBEditPatientAgeRequiredLabel;
    private javax.swing.JTextField DBEditPatientAgeTextField;
    private javax.swing.ButtonGroup DBEditPatientButtonGroup;
    private javax.swing.JCheckBox DBEditPatientFemaleCheckBox;
    private javax.swing.JLabel DBEditPatientIDLabel;
    private javax.swing.JTextField DBEditPatientIDTextField;
    private javax.swing.JCheckBox DBEditPatientMaleCheckBox;
    private javax.swing.JLabel DBEditPatientNameLabel;
    private javax.swing.JLabel DBEditPatientNameRequiredLabel;
    private javax.swing.JTextField DBEditPatientNameTextField;
    private javax.swing.JCheckBox DBEditPatientOtherCheckBox;
    private javax.swing.JPanel DBEditPatientPanel;
    private javax.swing.JLabel DBEditPatientPhoneNumLabel;
    private javax.swing.JTextField DBEditPatientPhoneNumTextField;
    private javax.swing.JLabel DBEditPatientRequiredLabel;
    private javax.swing.JLabel DBEditPatientSexLabel;
    private javax.swing.JLabel DBEditPatientSexRequiredLabel;
    private javax.swing.JLabel DBEditPatientStatusLabel;
    private javax.swing.JLabel DBEditPatientStatusRequiredLabel;
    private javax.swing.JTextField DBEditPatientStatusTextField;
    private javax.swing.JTextField DBKeywordTextField;
    private javax.swing.JPanel DBNoSelectionPanel;
    private javax.swing.JRadioButton DBNurseButton;
    private javax.swing.JScrollPane DBNurseScrollPane;
    private javax.swing.JPanel DBPanel;
    private javax.swing.JScrollPane DBPatientScrollPane;
    private javax.swing.JLabel DBSearchPromptLabel;
    private javax.swing.JComboBox<String> DBSelectionBox;
    private javax.swing.JPanel DBSelectionField;
    private javax.swing.JPanel DBTableField;
    private javax.swing.JPanel DBUtilityField;
    private javax.swing.JTable doctorTable;
    private javax.swing.JTable nurseTable;
    private javax.swing.JTable patientTable;
    // End of variables declaration//GEN-END:variables

    @Override
    public void handleSaveAction() {
        // DO NOTHING
    }
    
    @Override
    public void handleDiscardAction() {
        loadDoctorData();
        loadNurseData();
        loadPatientData();
        resetView();
    }
    
    private static String convertFormattedSalaryToNormal(String formattedSalary) {
        return formattedSalary.replace(Character.toString(GROUPING_CHARACTER), "");
    }
}
