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
import java.awt.Component;
import java.awt.Point;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Thinh
 */

public class SchedulePanel extends javax.swing.JPanel implements SaveDiscardSupportPanel {
    private static final String INPUT_ERROR_PROMPT = "Invalid Input ID!";
    private static final String EMPTY_PANEL_TITLE = "emptyPanel";
    private static final String TABLE_PANEL_TITLE = "tablePanel";
    private static final int FIRST_ROW_HEIGHT = 100;
    private static final Color FIRST_ROW_COLOR = new Color(255, 238, 92);
    private static final Color COLOR_ODD = Color.WHITE;
    private static final Color COLOR_EVEN = new Color(230, 230, 230);
    private static final int UNINITIALIZED_ID = -1;
    
    // Custom variables
    private MainFrame frame;
    private int panelIndex;
    private DataManager dataManager;
    
    private int currentScheduleID;
    
    /**
     * Creates new form SchedulePanel
     * @param frame: The main frame
     * @param index: the index of this panel in the tabbed pane
     */
    public SchedulePanel(MainFrame frame, int index) {
        initComponents();
        
        initCustomVar(frame, index);
        preset();
    }
    
    private void initCustomVar(MainFrame frame, int index) {
        this.frame = frame;
        panelIndex = index;
        dataManager = frame.getDataManager();
        currentScheduleID = UNINITIALIZED_ID;
    }
    
    private void preset() {
        disableUtilityButtons();
        TableRenderingSupporter.centerTableHeader(scheduleTable);
        
        scheduleAddDialog.pack();
        scheduleAddDialog.setLocationRelativeTo(null);
        DateInputBoxSupporter.setupDateInputBoxes(scheduleAddDayBox, scheduleAddMonthBox, scheduleAddYearBox);
        TimeInputSpinnerSupporter.setupTimeInputSpinners(scheduleAddStartHourSpinner, scheduleAddStartMinSpinner);
        TimeInputSpinnerSupporter.setupTimeInputSpinners(scheduleAddEndHourSpinner, scheduleAddEndMinSpinner);
        
        scheduleEditTimeDialog.pack();
        scheduleEditTimeDialog.setLocationRelativeTo(null);
        TimeInputSpinnerSupporter.setupTimeInputSpinners(scheduleEditTimeStartHourSpinner, scheduleEditTimeStartMinSpinner);
        TimeInputSpinnerSupporter.setupTimeInputSpinners(scheduleEditTimeEndHourSpinner, scheduleEditTimeEndMinSpinner);
        
        scheduleEditDateDialog.pack();
        scheduleEditDateDialog.setLocationRelativeTo(null);
        DateInputBoxSupporter.setupDateInputBoxes(scheduleEditDateDayBox, scheduleEditDateMonthBox, scheduleEditDateYearBox);
        
        scheduleFineTimeDialog.pack();
        scheduleFineTimeDialog.setLocationRelativeTo(null);
        DateInputBoxSupporter.setupDateInputBoxes(scheduleFindTimeDayBox, scheduleFindTimeMonthBox, scheduleFindTimeYearBox);
        
        // Set Shortcut Key for delete date button: SHIFT + DEL
        String deleteDateActionName = "delete date";
        Action deleteDateAction = new AbstractAction(){
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scheduleDeleteDateButtonActionPerformed(evt);
            }
        };
        scheduleDeleteDateButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK), 
            deleteDateActionName);
        scheduleDeleteDateButton.getActionMap().put(deleteDateActionName, deleteDateAction);
        
        // Set Shortcut Key for delete time button: DEL
        String deleteTimeActionName = "delete time";
        Action deleteTimeAction = new AbstractAction(){
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scheduleDeleteTimeButtonActionPerformed(evt);
            }
        };
        scheduleDeleteTimeButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), deleteTimeActionName);
        scheduleDeleteTimeButton.getActionMap().put(deleteTimeActionName, deleteTimeAction);
        
        // Set Shortcut Key for find time button: CTRL + F
        String findActionName = "find";
        Action findAction = new AbstractAction(){
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scheduleFindTimeButtonActionPerformed(evt);
            }
        };
        scheduleFindTimeButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK), findActionName);
        scheduleFindTimeButton.getActionMap().put(findActionName, findAction);
    }
    
    private void showTablePanel() {
        CardLayout layout = (CardLayout) scheduleTableField.getLayout();
        layout.show(scheduleTableField, TABLE_PANEL_TITLE);
    }
    
    private void hideTablePanel() {
        CardLayout layout = (CardLayout) scheduleTableField.getLayout();
        layout.show(scheduleTableField, EMPTY_PANEL_TITLE);
    }
    
    private void disableUtilityButtons() {
        scheduleAddButton.setEnabled(false);
        scheduleDeleteTimeButton.setEnabled(false);
        scheduleDeleteDateButton.setEnabled(false);
        scheduleEditTimeButton.setEnabled(false);
        scheduleEditDateButton.setEnabled(false);
        scheduleFindTimeButton.setEnabled(false);
    }
    
    private void renewUtilityButtons() {
        scheduleAddButton.setEnabled(true);
        scheduleDeleteTimeButton.setEnabled(false);
        scheduleDeleteDateButton.setEnabled(false);
        scheduleEditTimeButton.setEnabled(false);
        scheduleEditDateButton.setEnabled(false);
        scheduleFindTimeButton.setEnabled(true);
    }
    
    private void clearTableColumnData(int columnIndex) {
        DefaultTableModel model = (DefaultTableModel) scheduleTable.getModel();
        ScheduleSystem scheduleSystem = dataManager.getScheduleSystem();
        IndividualSchedule schedule = scheduleSystem.accessSchedule(currentScheduleID);
        schedule.removeEntry((Date) model.getValueAt(0, columnIndex));

        for(int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(null, i, columnIndex);
        }
        
        clearEmptyTrailingRows();
    }
    
    private void clearEmptyTrailingRows() {
        DefaultTableModel model = (DefaultTableModel) scheduleTable.getModel();
        
        int i = model.getRowCount() - 1;
        for(; i >= 0; i--) {
            boolean isEmptyRow = true;

            for(int j = 0; j < model.getColumnCount(); j++) {
                if(model.getValueAt(i, j) != null) {
                    isEmptyRow = false;
                    break;
                }
            }

            if(isEmptyRow) {
                model.setRowCount(model.getRowCount() - 1);
            }
            else {
                break;
            }
        }
    }
    
    private void loadScheduleData(IndividualSchedule schedule) {
        // Get the model of the table
        DefaultTableModel model = (DefaultTableModel) scheduleTable.getModel();
        // Discard all old data stored in the model
        model.setRowCount(0);
        
        if(schedule == null) {
            return;
        }
        
        Date[] dates = schedule.getDates();
        // If there is nothing to be inserted, just return
        if(dates.length == 0) {
            return;
        }
        // Otherwise, create a new empty row to stored dates
        else {
            model.addRow(createEmptyRow());
        }
        
        // For every date to be inserted
        for(Date date : dates) {
            // Compute the column to be inserted based on the date
            int columnIndex = dayOfWeekToIndex(date.getDayOfWeek());
            // Get the object at the first row of the to be inserted column
            Object headingObject = model.getValueAt(0, columnIndex);
            // If the column has already been inserted, the object will be not null
            // in this case, don't insert data into this column anymore
            // otherwise, process normally
            if(headingObject == null) {
                // Insert the date into the first row
                model.setValueAt(date, 0, columnIndex);
                // Then insert all WHs into the following rows
                WorkingHour[] WHs = schedule.getWorkingHours(date);
                // If model's row count is not bigger than WHs' length, the model is not large enough 
                // to insert WHs into it; in this case, add empty rows to enlarge the model
                if(!(model.getRowCount() > WHs.length))
                    model.setRowCount(WHs.length + 1);
                
                int currentRow = 1;
                for(WorkingHour WH : WHs) {
                    model.setValueAt(WH, currentRow++, columnIndex);
                }
            }
        }
        
        scheduleTable.setRowHeight(0, FIRST_ROW_HEIGHT);
    }
     
    private void loadTableColumnData(IndividualSchedule schedule, Date date, int columnIndex) {
        DefaultTableModel model = (DefaultTableModel) scheduleTable.getModel();
        int rowCount = model.getRowCount();
        
        // Clean up the column data
        for(int i = 0; i < rowCount; i++) {
            if(model.getValueAt(i, columnIndex) != null) {
                model.setValueAt(null, i, columnIndex);
            }
            else {
                break;
            }
        }
        
        WorkingHour[] WHs = (date != null) ? schedule.getWorkingHours(date) : new WorkingHour[0];
        
        while(rowCount - 1 < WHs.length) {
            model.addRow(createEmptyRow());
            rowCount++;
        }
        
        if(WHs.length != 0) {
            model.setValueAt(date, 0, columnIndex);
        }
        
        int i = 1;
        for(WorkingHour WH : WHs) {
            model.setValueAt(WH, i++, columnIndex);
        }
        
        scheduleTable.setRowHeight(0, FIRST_ROW_HEIGHT);
        clearEmptyTrailingRows();
    }
   
    private void clearAndHideAddDialog() {
        DateInputBoxSupporter.clearDateInputBoxes(scheduleAddDayBox, scheduleAddMonthBox, scheduleAddYearBox);
        TimeInputSpinnerSupporter.clearTimeInputSpinners(scheduleAddStartHourSpinner, scheduleAddStartMinSpinner);
        TimeInputSpinnerSupporter.clearTimeInputSpinners(scheduleAddEndHourSpinner, scheduleAddEndMinSpinner);
        
        scheduleAddDialog.setVisible(false);
    }
    
    private void clearAndHideEditTimeDialog() {
        TimeInputSpinnerSupporter.clearTimeInputSpinners(scheduleEditTimeStartHourSpinner, scheduleEditTimeStartMinSpinner);
        TimeInputSpinnerSupporter.clearTimeInputSpinners(scheduleEditTimeEndHourSpinner, scheduleEditTimeEndMinSpinner);
        
        scheduleEditTimeDialog.setVisible(false);
    }
    
    private void clearAndHideEditDateDialog() {
        DateInputBoxSupporter.clearDateInputBoxes(scheduleEditDateDayBox, scheduleEditDateMonthBox, scheduleEditDateYearBox);
        
        scheduleEditDateDialog.setVisible(false);
    }
    
    private void clearAndHideLocateTimeDialog() {
        DateInputBoxSupporter.clearDateInputBoxes(scheduleFindTimeDayBox, scheduleFindTimeMonthBox, scheduleFindTimeYearBox);
        TimeInputSpinnerSupporter.clearTimeInputSpinners(scheduleFindTimeHourSpinner, scheduleFindTimeMinSpinner);
        
        scheduleFineTimeDialog.setVisible(false);
    }
    
    private void setEditTimeSpinners(WorkingHour WH) {
        Time startTime = WH.getStartTime();
        Time endTime = WH.getEndTime();
            
        TimeInputSpinnerSupporter.setTimeInputSpinners(scheduleEditTimeStartHourSpinner, scheduleEditTimeStartMinSpinner, startTime);
        TimeInputSpinnerSupporter.setTimeInputSpinners(scheduleEditTimeEndHourSpinner, scheduleEditTimeEndMinSpinner, endTime);
    }
    
    private void setEditDateBoxes(Date date) {
        DateInputBoxSupporter.setDateInputBoxes(scheduleEditDateDayBox, scheduleEditDateMonthBox, scheduleEditDateYearBox, 
                                                date);
    }

    private void setFindTimeDateBoxes(Date date) {
        DateInputBoxSupporter.setDateInputBoxes(scheduleFindTimeDayBox, scheduleFindTimeMonthBox, scheduleFindTimeYearBox, 
                                                date);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scheduleAddPanel = new javax.swing.JPanel();
        scheduleAddDayBox = new javax.swing.JComboBox<>();
        scheduleAddMonthBox = new javax.swing.JComboBox<>();
        scheduleAddYearBox = new javax.swing.JComboBox<>();
        scheduleAddDateLabel = new javax.swing.JLabel();
        scheduleAddDayLabel = new javax.swing.JLabel();
        scheduleAddMonthLabel = new javax.swing.JLabel();
        scheduleAddYearLabel = new javax.swing.JLabel();
        scheduleAddStartTimeLabel = new javax.swing.JLabel();
        scheduleAddStartHourLabel = new javax.swing.JLabel();
        scheduleAddStartMinLabel = new javax.swing.JLabel();
        scheduleAddEndTimeLabel = new javax.swing.JLabel();
        scheduleAddEndHourLabel = new javax.swing.JLabel();
        scheduleAddEndMinLabel = new javax.swing.JLabel();
        scheduleAddStartHourSpinner = new javax.swing.JSpinner();
        scheduleAddStartMinSpinner = new javax.swing.JSpinner();
        scheduleAddEndHourSpinner = new javax.swing.JSpinner();
        scheduleAddEndMinSpinner = new javax.swing.JSpinner();
        scheduleEditTimePanel = new javax.swing.JPanel();
        scheduleEditTimeStartTimeLabel = new javax.swing.JLabel();
        scheduleEditTimeEndTimeLabel = new javax.swing.JLabel();
        scheduleEditTimeStartHourLabel = new javax.swing.JLabel();
        scheduleEditTimeEndHourLabel = new javax.swing.JLabel();
        scheduleEditTimeStartMinLabel = new javax.swing.JLabel();
        scheduleEditTimeEndMinLabel = new javax.swing.JLabel();
        scheduleEditTimeStartHourSpinner = new javax.swing.JSpinner();
        scheduleEditTimeEndHourSpinner = new javax.swing.JSpinner();
        scheduleEditTimeStartMinSpinner = new javax.swing.JSpinner();
        scheduleEditTimeEndMinSpinner = new javax.swing.JSpinner();
        scheduleEditDatePanel = new javax.swing.JPanel();
        scheduleEditDateDateLabel = new javax.swing.JLabel();
        scheduleEditDateDayLabel = new javax.swing.JLabel();
        scheduleEditDateMonthLabel = new javax.swing.JLabel();
        scheduleEditDateYearLabel = new javax.swing.JLabel();
        scheduleEditDateDayBox = new javax.swing.JComboBox<>();
        scheduleEditDateMonthBox = new javax.swing.JComboBox<>();
        scheduleEditDateYearBox = new javax.swing.JComboBox<>();
        scheduleFindTimePanel = new javax.swing.JPanel();
        scheduleFindTimeDayBox = new javax.swing.JComboBox<>();
        scheduleFindTimeMonthBox = new javax.swing.JComboBox<>();
        scheduleFindTimeYearBox = new javax.swing.JComboBox<>();
        scheduleFindTimeDateLabel = new javax.swing.JLabel();
        scheduleFindTimeDayLabel = new javax.swing.JLabel();
        scheduleFindTimeLabel = new javax.swing.JLabel();
        scheduleFindTimeYearLabel = new javax.swing.JLabel();
        scheduleFindTimeTimeLabel = new javax.swing.JLabel();
        scheduleFindTimeHourLabel = new javax.swing.JLabel();
        scheduleFindTimeMinLabel = new javax.swing.JLabel();
        scheduleFindTimeHourSpinner = new javax.swing.JSpinner();
        scheduleFindTimeMinSpinner = new javax.swing.JSpinner();
        scheduleAddDialog = new javax.swing.JDialog();
        scheduleAddOptionPane = new JOptionPane(null, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null);
        scheduleEditTimeDialog = new javax.swing.JDialog();
        scheduleEditTimeOptionPane = new JOptionPane(null, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null);
        scheduleEditDateDialog = new javax.swing.JDialog();
        scheduleEditDateOptionPane = new JOptionPane(null, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null);
        scheduleFineTimeDialog = new javax.swing.JDialog();
        scheduleFindTimeOptionPane = new JOptionPane(null, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null);
        schedulePanel = new javax.swing.JPanel();
        scheduleEntryField = new javax.swing.JPanel();
        scheduleIDPromptLabel = new javax.swing.JLabel();
        scheduleAccessScheduleButton = new javax.swing.JButton();
        scheduleCreateScheduleButton = new javax.swing.JButton();
        scheduleRemoveScheduleButton = new javax.swing.JButton();
        schedulePromptLabel = new javax.swing.JLabel();
        scheduleInvalidInputLabel = new javax.swing.JLabel();
        scheduleIDInputTextField = new javax.swing.JTextField();
        scheduleUtilityField = new javax.swing.JPanel();
        scheduleAddButton = new javax.swing.JButton();
        scheduleFindTimeButton = new javax.swing.JButton();
        scheduleEditDateButton = new javax.swing.JButton();
        scheduleEditTimeButton = new javax.swing.JButton();
        scheduleDeleteDateButton = new javax.swing.JButton();
        scheduleDeleteTimeButton = new javax.swing.JButton();
        scheduleTableField = new javax.swing.JPanel();
        scheduleTableEmptyPanel = new javax.swing.JPanel();
        scheduleTableScrollPane = new javax.swing.JScrollPane();
        scheduleTable = new javax.swing.JTable(){
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                if(!comp.getBackground().equals(getSelectionBackground())) {
                    Color c;            
                    if(row == 0) {
                        c = FIRST_ROW_COLOR;
                    }
                    else {
                        c = (row % 2 == 1) ? COLOR_ODD : COLOR_EVEN;
                    }

                    comp.setBackground(c);
                    c = null;
                }
                return comp;
            }
        };

        scheduleAddDayBox.setEditable(true);

        scheduleAddMonthBox.setEditable(true);

        scheduleAddYearBox.setEditable(true);

        scheduleAddDateLabel.setText("Date:");

        scheduleAddDayLabel.setText("Day");

        scheduleAddMonthLabel.setText("Month");

        scheduleAddYearLabel.setText("Year");

        scheduleAddStartTimeLabel.setText("Start Time:");

        scheduleAddStartHourLabel.setText("Hour");

        scheduleAddStartMinLabel.setText("Minute");

        scheduleAddEndTimeLabel.setText("End Time:");

        scheduleAddEndHourLabel.setText("Hour");

        scheduleAddEndMinLabel.setText("Minute");

        javax.swing.GroupLayout scheduleAddPanelLayout = new javax.swing.GroupLayout(scheduleAddPanel);
        scheduleAddPanel.setLayout(scheduleAddPanelLayout);
        scheduleAddPanelLayout.setHorizontalGroup(
            scheduleAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scheduleAddPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(scheduleAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scheduleAddDateLabel)
                    .addComponent(scheduleAddStartTimeLabel)
                    .addComponent(scheduleAddEndTimeLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(scheduleAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scheduleAddDayLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scheduleAddStartHourLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scheduleAddEndHourLabel, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(scheduleAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scheduleAddDayBox, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scheduleAddStartHourSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scheduleAddEndHourSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(scheduleAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scheduleAddMonthLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scheduleAddStartMinLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scheduleAddEndMinLabel, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(scheduleAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(scheduleAddMonthBox, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scheduleAddStartMinSpinner)
                    .addComponent(scheduleAddEndMinSpinner))
                .addGap(15, 15, 15)
                .addComponent(scheduleAddYearLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scheduleAddYearBox, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(10, Short.MAX_VALUE))
        );

        scheduleAddPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {scheduleAddDayBox, scheduleAddEndHourSpinner, scheduleAddEndMinSpinner, scheduleAddMonthBox, scheduleAddStartHourSpinner, scheduleAddStartMinSpinner});

        scheduleAddPanelLayout.setVerticalGroup(
            scheduleAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scheduleAddPanelLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(scheduleAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(scheduleAddDateLabel)
                    .addComponent(scheduleAddDayLabel)
                    .addComponent(scheduleAddDayBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scheduleAddMonthBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scheduleAddMonthLabel)
                    .addComponent(scheduleAddYearBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scheduleAddYearLabel))
                .addGap(74, 74, 74)
                .addGroup(scheduleAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(scheduleAddStartHourLabel)
                    .addComponent(scheduleAddStartTimeLabel)
                    .addComponent(scheduleAddStartMinLabel)
                    .addComponent(scheduleAddStartHourSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scheduleAddStartMinSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(74, 74, 74)
                .addGroup(scheduleAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(scheduleAddEndHourLabel)
                    .addComponent(scheduleAddEndTimeLabel)
                    .addComponent(scheduleAddEndMinLabel)
                    .addComponent(scheduleAddEndHourSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scheduleAddEndMinSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        scheduleAddPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {scheduleAddDayBox, scheduleAddEndHourSpinner, scheduleAddEndMinSpinner, scheduleAddMonthBox, scheduleAddStartHourSpinner, scheduleAddStartMinSpinner, scheduleAddYearBox});

        scheduleEditTimeStartTimeLabel.setText("Start Time:");

        scheduleEditTimeEndTimeLabel.setText("End Time:");

        scheduleEditTimeStartHourLabel.setText("Hour");

        scheduleEditTimeEndHourLabel.setText("Hour");

        scheduleEditTimeStartMinLabel.setText("Minute");

        scheduleEditTimeEndMinLabel.setText("Minute");

        javax.swing.GroupLayout scheduleEditTimePanelLayout = new javax.swing.GroupLayout(scheduleEditTimePanel);
        scheduleEditTimePanel.setLayout(scheduleEditTimePanelLayout);
        scheduleEditTimePanelLayout.setHorizontalGroup(
            scheduleEditTimePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scheduleEditTimePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scheduleEditTimeStartTimeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(scheduleEditTimeStartHourLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scheduleEditTimeStartHourSpinner, javax.swing.GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(scheduleEditTimeStartMinLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scheduleEditTimeStartMinSpinner, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, scheduleEditTimePanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(scheduleEditTimeEndTimeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(scheduleEditTimeEndHourLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scheduleEditTimeEndHourSpinner)
                .addGap(18, 18, 18)
                .addComponent(scheduleEditTimeEndMinLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scheduleEditTimeEndMinSpinner)
                .addContainerGap())
        );

        scheduleEditTimePanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {scheduleEditTimeEndHourSpinner, scheduleEditTimeEndMinSpinner, scheduleEditTimeStartHourSpinner, scheduleEditTimeStartMinSpinner});

        scheduleEditTimePanelLayout.setVerticalGroup(
            scheduleEditTimePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scheduleEditTimePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(scheduleEditTimePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(scheduleEditTimeStartTimeLabel)
                    .addComponent(scheduleEditTimeStartHourLabel)
                    .addComponent(scheduleEditTimeStartMinLabel)
                    .addComponent(scheduleEditTimeStartHourSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scheduleEditTimeStartMinSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(40, 40, 40)
                .addGroup(scheduleEditTimePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(scheduleEditTimeEndTimeLabel)
                    .addComponent(scheduleEditTimeEndHourLabel)
                    .addComponent(scheduleEditTimeEndMinLabel)
                    .addComponent(scheduleEditTimeEndHourSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scheduleEditTimeEndMinSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(19, 19, 19))
        );

        scheduleEditTimePanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {scheduleEditTimeEndHourSpinner, scheduleEditTimeEndMinSpinner});

        scheduleEditTimePanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {scheduleEditTimeStartHourSpinner, scheduleEditTimeStartMinSpinner});

        scheduleEditDateDateLabel.setText("Date:");

        scheduleEditDateDayLabel.setText("Day");

        scheduleEditDateMonthLabel.setText("Month");

        scheduleEditDateYearLabel.setText("Year");

        scheduleEditDateDayBox.setEditable(true);

        scheduleEditDateMonthBox.setEditable(true);

        scheduleEditDateYearBox.setEditable(true);

        javax.swing.GroupLayout scheduleEditDatePanelLayout = new javax.swing.GroupLayout(scheduleEditDatePanel);
        scheduleEditDatePanel.setLayout(scheduleEditDatePanelLayout);
        scheduleEditDatePanelLayout.setHorizontalGroup(
            scheduleEditDatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scheduleEditDatePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scheduleEditDateDateLabel)
                .addGap(12, 12, 12)
                .addComponent(scheduleEditDateDayLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scheduleEditDateDayBox, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21)
                .addComponent(scheduleEditDateMonthLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scheduleEditDateMonthBox, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(scheduleEditDateYearLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scheduleEditDateYearBox, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        scheduleEditDatePanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {scheduleEditDateDayBox, scheduleEditDateMonthBox});

        scheduleEditDatePanelLayout.setVerticalGroup(
            scheduleEditDatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scheduleEditDatePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(scheduleEditDatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(scheduleEditDateDateLabel)
                    .addComponent(scheduleEditDateDayLabel)
                    .addComponent(scheduleEditDateMonthLabel)
                    .addComponent(scheduleEditDateYearLabel)
                    .addComponent(scheduleEditDateDayBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scheduleEditDateMonthBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scheduleEditDateYearBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(39, Short.MAX_VALUE))
        );

        scheduleEditDatePanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {scheduleEditDateDayBox, scheduleEditDateMonthBox, scheduleEditDateYearBox});

        scheduleFindTimeDayBox.setEditable(true);

        scheduleFindTimeMonthBox.setEditable(true);

        scheduleFindTimeYearBox.setEditable(true);

        scheduleFindTimeDateLabel.setText("Date:");

        scheduleFindTimeDayLabel.setText("Day");

        scheduleFindTimeLabel.setText("Month");

        scheduleFindTimeYearLabel.setText("Year");

        scheduleFindTimeTimeLabel.setText("Time:");

        scheduleFindTimeHourLabel.setText("Hour");

        scheduleFindTimeMinLabel.setText("Minute");

        scheduleFindTimeHourSpinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, 23, 1));
        scheduleFindTimeHourSpinner.setEditor(new javax.swing.JSpinner.NumberEditor(scheduleFindTimeHourSpinner, "00"));

        scheduleFindTimeMinSpinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));
        scheduleFindTimeMinSpinner.setEditor(new javax.swing.JSpinner.NumberEditor(scheduleFindTimeMinSpinner, "00"));

        javax.swing.GroupLayout scheduleFindTimePanelLayout = new javax.swing.GroupLayout(scheduleFindTimePanel);
        scheduleFindTimePanel.setLayout(scheduleFindTimePanelLayout);
        scheduleFindTimePanelLayout.setHorizontalGroup(
            scheduleFindTimePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scheduleFindTimePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(scheduleFindTimePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scheduleFindTimeTimeLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scheduleFindTimeDateLabel, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(scheduleFindTimePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scheduleFindTimeDayLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scheduleFindTimeHourLabel, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(scheduleFindTimePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scheduleFindTimeDayBox, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scheduleFindTimeHourSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(scheduleFindTimePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scheduleFindTimeLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scheduleFindTimeMinLabel, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(scheduleFindTimePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(scheduleFindTimeMonthBox, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scheduleFindTimeMinSpinner))
                .addGap(15, 15, 15)
                .addComponent(scheduleFindTimeYearLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scheduleFindTimeYearBox, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(25, Short.MAX_VALUE))
        );
        scheduleFindTimePanelLayout.setVerticalGroup(
            scheduleFindTimePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scheduleFindTimePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(scheduleFindTimePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(scheduleFindTimeDateLabel)
                    .addComponent(scheduleFindTimeDayLabel)
                    .addComponent(scheduleFindTimeDayBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scheduleFindTimeMonthBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scheduleFindTimeLabel)
                    .addComponent(scheduleFindTimeYearBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scheduleFindTimeYearLabel))
                .addGap(57, 57, 57)
                .addGroup(scheduleFindTimePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(scheduleFindTimeHourLabel)
                    .addComponent(scheduleFindTimeTimeLabel)
                    .addComponent(scheduleFindTimeMinLabel)
                    .addComponent(scheduleFindTimeHourSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scheduleFindTimeMinSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        scheduleAddDialog.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        scheduleAddDialog.setTitle("Add");
        scheduleAddDialog.setModal(true);
        scheduleAddDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                scheduleAddDialogWindowClosing(evt);
            }
        });

        scheduleAddOptionPane.setMessage(scheduleAddPanel);
        scheduleAddOptionPane.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                scheduleAddOptionPanePropertyChange(evt);
            }
        });

        javax.swing.GroupLayout scheduleAddDialogLayout = new javax.swing.GroupLayout(scheduleAddDialog.getContentPane());
        scheduleAddDialog.getContentPane().setLayout(scheduleAddDialogLayout);
        scheduleAddDialogLayout.setHorizontalGroup(
            scheduleAddDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scheduleAddOptionPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        scheduleAddDialogLayout.setVerticalGroup(
            scheduleAddDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scheduleAddOptionPane, javax.swing.GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE)
        );

        scheduleEditTimeDialog.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        scheduleEditTimeDialog.setTitle("Edit Time");
        scheduleEditTimeDialog.setModal(true);
        scheduleEditTimeDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                scheduleEditTimeDialogWindowClosing(evt);
            }
        });

        scheduleEditTimeOptionPane.setMessage(scheduleEditTimePanel);
        scheduleEditTimeOptionPane.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                scheduleEditTimeOptionPanePropertyChange(evt);
            }
        });

        javax.swing.GroupLayout scheduleEditTimeDialogLayout = new javax.swing.GroupLayout(scheduleEditTimeDialog.getContentPane());
        scheduleEditTimeDialog.getContentPane().setLayout(scheduleEditTimeDialogLayout);
        scheduleEditTimeDialogLayout.setHorizontalGroup(
            scheduleEditTimeDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 262, Short.MAX_VALUE)
            .addGroup(scheduleEditTimeDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(scheduleEditTimeOptionPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        scheduleEditTimeDialogLayout.setVerticalGroup(
            scheduleEditTimeDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 119, Short.MAX_VALUE)
            .addGroup(scheduleEditTimeDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(scheduleEditTimeOptionPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE))
        );

        scheduleEditDateDialog.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        scheduleEditDateDialog.setTitle("Edit Date");
        scheduleEditDateDialog.setModal(true);
        scheduleEditDateDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                scheduleEditDateDialogWindowClosing(evt);
            }
        });

        scheduleEditDateOptionPane.setMessage(scheduleEditDatePanel);
        scheduleEditDateOptionPane.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                scheduleEditDateOptionPanePropertyChange(evt);
            }
        });

        javax.swing.GroupLayout scheduleEditDateDialogLayout = new javax.swing.GroupLayout(scheduleEditDateDialog.getContentPane());
        scheduleEditDateDialog.getContentPane().setLayout(scheduleEditDateDialogLayout);
        scheduleEditDateDialogLayout.setHorizontalGroup(
            scheduleEditDateDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 276, Short.MAX_VALUE)
            .addGroup(scheduleEditDateDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(scheduleEditDateOptionPane, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE))
        );
        scheduleEditDateDialogLayout.setVerticalGroup(
            scheduleEditDateDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 143, Short.MAX_VALUE)
            .addGroup(scheduleEditDateDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(scheduleEditDateOptionPane, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE))
        );

        scheduleFineTimeDialog.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        scheduleFineTimeDialog.setTitle("Find Time");
        scheduleFineTimeDialog.setModal(true);
        scheduleFineTimeDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                scheduleFineTimeDialogWindowClosing(evt);
            }
        });

        scheduleFindTimeOptionPane.setMessage(scheduleFindTimePanel);
        scheduleFindTimeOptionPane.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                scheduleFindTimeOptionPanePropertyChange(evt);
            }
        });

        javax.swing.GroupLayout scheduleFineTimeDialogLayout = new javax.swing.GroupLayout(scheduleFineTimeDialog.getContentPane());
        scheduleFineTimeDialog.getContentPane().setLayout(scheduleFineTimeDialogLayout);
        scheduleFineTimeDialogLayout.setHorizontalGroup(
            scheduleFineTimeDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 361, Short.MAX_VALUE)
            .addGroup(scheduleFineTimeDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(scheduleFindTimeOptionPane, javax.swing.GroupLayout.DEFAULT_SIZE, 361, Short.MAX_VALUE))
        );
        scheduleFineTimeDialogLayout.setVerticalGroup(
            scheduleFineTimeDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 119, Short.MAX_VALUE)
            .addGroup(scheduleFineTimeDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(scheduleFindTimeOptionPane, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE))
        );

        scheduleEntryField.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        scheduleIDPromptLabel.setText("ID:");

        scheduleAccessScheduleButton.setText("Access Schedule");
        scheduleAccessScheduleButton.setFocusPainted(false);
        scheduleAccessScheduleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scheduleAccessScheduleButtonActionPerformed(evt);
            }
        });

        scheduleCreateScheduleButton.setText("Create Schedule");
        scheduleCreateScheduleButton.setFocusPainted(false);
        scheduleCreateScheduleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scheduleCreateScheduleButtonActionPerformed(evt);
            }
        });

        scheduleRemoveScheduleButton.setText("Remove Schedule");
        scheduleRemoveScheduleButton.setFocusPainted(false);
        scheduleRemoveScheduleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scheduleRemoveScheduleButtonActionPerformed(evt);
            }
        });

        schedulePromptLabel.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        schedulePromptLabel.setText("Enter an employee's ID");

        scheduleInvalidInputLabel.setFont(new java.awt.Font("Dialog", 2, 12)); // NOI18N
        scheduleInvalidInputLabel.setForeground(new java.awt.Color(255, 0, 51));

        javax.swing.GroupLayout scheduleEntryFieldLayout = new javax.swing.GroupLayout(scheduleEntryField);
        scheduleEntryField.setLayout(scheduleEntryFieldLayout);
        scheduleEntryFieldLayout.setHorizontalGroup(
            scheduleEntryFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, scheduleEntryFieldLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(scheduleEntryFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(scheduleEntryFieldLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(scheduleInvalidInputLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, scheduleEntryFieldLayout.createSequentialGroup()
                        .addComponent(scheduleIDPromptLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scheduleIDInputTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(schedulePromptLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(scheduleEntryFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scheduleCreateScheduleButton)
                    .addComponent(scheduleRemoveScheduleButton)
                    .addComponent(scheduleAccessScheduleButton))
                .addContainerGap())
        );

        scheduleEntryFieldLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {scheduleAccessScheduleButton, scheduleCreateScheduleButton, scheduleRemoveScheduleButton});

        scheduleEntryFieldLayout.setVerticalGroup(
            scheduleEntryFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scheduleEntryFieldLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(scheduleEntryFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(scheduleEntryFieldLayout.createSequentialGroup()
                        .addComponent(schedulePromptLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(scheduleEntryFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(scheduleIDInputTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(scheduleIDPromptLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(scheduleInvalidInputLabel)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(scheduleEntryFieldLayout.createSequentialGroup()
                        .addComponent(scheduleAccessScheduleButton, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                        .addComponent(scheduleCreateScheduleButton, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(scheduleRemoveScheduleButton, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );

        scheduleEntryFieldLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {scheduleAccessScheduleButton, scheduleCreateScheduleButton, scheduleRemoveScheduleButton});

        scheduleUtilityField.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        scheduleAddButton.setText("Add");
        scheduleAddButton.setToolTipText("Add a new record to the schedule");
        scheduleAddButton.setFocusPainted(false);
        scheduleAddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scheduleAddButtonActionPerformed(evt);
            }
        });

        scheduleFindTimeButton.setText("Find Time");
        scheduleFindTimeButton.setToolTipText("Find a record that covers a given time <Ctrl + F>");
        scheduleFindTimeButton.setFocusPainted(false);
        scheduleFindTimeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scheduleFindTimeButtonActionPerformed(evt);
            }
        });

        scheduleEditDateButton.setText("Edit Date");
        scheduleEditDateButton.setToolTipText("Edit a date, then move all associated records to the new corresponding column");
        scheduleEditDateButton.setFocusPainted(false);
        scheduleEditDateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scheduleEditDateButtonActionPerformed(evt);
            }
        });

        scheduleEditTimeButton.setText("Edit Time");
        scheduleEditTimeButton.setToolTipText("Edit a record");
        scheduleEditTimeButton.setFocusPainted(false);
        scheduleEditTimeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scheduleEditTimeButtonActionPerformed(evt);
            }
        });

        scheduleDeleteDateButton.setText("Delete Date");
        scheduleDeleteDateButton.setToolTipText("Delete all records in a column, including the date <Ctrl + Shift + Del>");
        scheduleDeleteDateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scheduleDeleteDateButtonActionPerformed(evt);
            }
        });

        scheduleDeleteTimeButton.setText("Delete Time");
        scheduleDeleteTimeButton.setToolTipText("Delete a record <Del>");
        scheduleDeleteTimeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scheduleDeleteTimeButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout scheduleUtilityFieldLayout = new javax.swing.GroupLayout(scheduleUtilityField);
        scheduleUtilityField.setLayout(scheduleUtilityFieldLayout);
        scheduleUtilityFieldLayout.setHorizontalGroup(
            scheduleUtilityFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, scheduleUtilityFieldLayout.createSequentialGroup()
                .addContainerGap(8, Short.MAX_VALUE)
                .addComponent(scheduleAddButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scheduleDeleteTimeButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scheduleDeleteDateButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scheduleEditTimeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scheduleEditDateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scheduleFindTimeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        scheduleUtilityFieldLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {scheduleAddButton, scheduleDeleteDateButton, scheduleDeleteTimeButton, scheduleEditDateButton, scheduleEditTimeButton, scheduleFindTimeButton});

        scheduleUtilityFieldLayout.setVerticalGroup(
            scheduleUtilityFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scheduleUtilityFieldLayout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(scheduleUtilityFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(scheduleAddButton)
                    .addComponent(scheduleEditDateButton)
                    .addComponent(scheduleFindTimeButton)
                    .addComponent(scheduleDeleteTimeButton)
                    .addComponent(scheduleDeleteDateButton)
                    .addComponent(scheduleEditTimeButton))
                .addContainerGap(42, Short.MAX_VALUE))
        );

        scheduleUtilityFieldLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {scheduleAddButton, scheduleDeleteDateButton, scheduleDeleteTimeButton, scheduleEditDateButton, scheduleEditTimeButton, scheduleFindTimeButton});

        scheduleTableField.setLayout(new java.awt.CardLayout());

        javax.swing.GroupLayout scheduleTableEmptyPanelLayout = new javax.swing.GroupLayout(scheduleTableEmptyPanel);
        scheduleTableEmptyPanel.setLayout(scheduleTableEmptyPanelLayout);
        scheduleTableEmptyPanelLayout.setHorizontalGroup(
            scheduleTableEmptyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1055, Short.MAX_VALUE)
        );
        scheduleTableEmptyPanelLayout.setVerticalGroup(
            scheduleTableEmptyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 520, Short.MAX_VALUE)
        );

        scheduleTableField.add(scheduleTableEmptyPanel, "emptyPanel");

        scheduleTable.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        scheduleTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        scheduleTable.setColumnSelectionAllowed(true);
        scheduleTable.setRowHeight(60);
        scheduleTable.getTableHeader().setReorderingAllowed(false);
        scheduleTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                scheduleTableMousePressed(evt);
            }
        });
        scheduleTableScrollPane.setViewportView(scheduleTable);
        scheduleTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        scheduleTableField.add(scheduleTableScrollPane, "tablePanel");

        javax.swing.GroupLayout schedulePanelLayout = new javax.swing.GroupLayout(schedulePanel);
        schedulePanel.setLayout(schedulePanelLayout);
        schedulePanelLayout.setHorizontalGroup(
            schedulePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(schedulePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scheduleEntryField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scheduleUtilityField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(scheduleTableField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        schedulePanelLayout.setVerticalGroup(
            schedulePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(schedulePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(schedulePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scheduleUtilityField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scheduleEntryField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scheduleTableField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1055, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(schedulePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 639, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(schedulePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void scheduleAccessScheduleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scheduleAccessScheduleButtonActionPerformed
        try {
            int inputID = Integer.parseInt(scheduleIDInputTextField.getText());
            if(inputID < 0) {
                throw new IllegalArgumentException();
            }
            
            if(!dataManager.getEmployeeDB().has(inputID)) {
                String message = "Employee ID not found in the database!";
                MainFrame.makeBeepSound();
                JOptionPane.showMessageDialog(null, message, MainFrame.TITLE, JOptionPane.ERROR_MESSAGE);
            }
            else {
                ScheduleSystem scheduleSystem = dataManager.getScheduleSystem();
                IndividualSchedule schedule = scheduleSystem.accessSchedule(inputID);
                if(schedule != null) {
                    if(inputID != currentScheduleID){
                        loadScheduleData(schedule);
                        currentScheduleID = inputID;
                        showTablePanel();
                        renewUtilityButtons();
                    }
                }
                else {
                    String message = """
                                     The input ID has not been assigned to a schedule!
                                     Do you want to create a new schedule for this ID?""";
                    int choice = JOptionPane.showConfirmDialog(null, message, MainFrame.TITLE, 
                                                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                    if(choice == JOptionPane.YES_OPTION) {
                        scheduleSystem.createSchedule(inputID);
                        message = "New schedule created successfully!";
                        JOptionPane.showMessageDialog(null, message, MainFrame.TITLE, JOptionPane.INFORMATION_MESSAGE);

                        schedule = scheduleSystem.accessSchedule(inputID);
                        loadScheduleData(schedule);
                        currentScheduleID = inputID;
                        showTablePanel();
                        renewUtilityButtons();
                        frame.setSaved(panelIndex, false);
                    }
                }
            }
        }
        catch(IllegalArgumentException e) {
            scheduleInvalidInputLabel.setText(INPUT_ERROR_PROMPT);
        }
    }//GEN-LAST:event_scheduleAccessScheduleButtonActionPerformed

    private void scheduleCreateScheduleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scheduleCreateScheduleButtonActionPerformed
        try {
            int inputID = Integer.parseInt(scheduleIDInputTextField.getText());
            if(inputID < 0) {
                throw new IllegalArgumentException();
            }
            
            ScheduleSystem scheduleSystem = dataManager.getScheduleSystem();

            if(scheduleSystem.hasSchedule(inputID)) {
                String message = "The input ID has already been assigned with a schedule!";
                MainFrame.makeBeepSound();
                JOptionPane.showMessageDialog(null, message, MainFrame.TITLE, JOptionPane.ERROR_MESSAGE);
            }
            else if(!dataManager.getEmployeeDB().has(inputID)) {
                String message = "Employee ID not found in the database!";
                MainFrame.makeBeepSound();
                JOptionPane.showMessageDialog(null, message, MainFrame.TITLE, JOptionPane.ERROR_MESSAGE);
            }
            else {
                String message = "Do you want to create a new schedule for this ID?";
                int choice = JOptionPane.showConfirmDialog(null, message, MainFrame.TITLE, 
                                                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if(choice == JOptionPane.YES_OPTION) {
                    scheduleSystem.createSchedule(inputID);
                    IndividualSchedule schedule = scheduleSystem.accessSchedule(inputID);
                    loadScheduleData(schedule);
                    
                    message = "Schedule created successfully!";
                    JOptionPane.showMessageDialog(null, message, MainFrame.TITLE, JOptionPane.INFORMATION_MESSAGE);
                    
                    currentScheduleID = inputID;
                    showTablePanel();
                    renewUtilityButtons();
                    frame.setSaved(panelIndex, false);
                }
            }
        }
        catch(IllegalArgumentException e) {
            scheduleInvalidInputLabel.setText(INPUT_ERROR_PROMPT);
        }
    }//GEN-LAST:event_scheduleCreateScheduleButtonActionPerformed

    private void scheduleRemoveScheduleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scheduleRemoveScheduleButtonActionPerformed
        try {
            int inputID = Integer.parseInt(scheduleIDInputTextField.getText());
            if(inputID < 0) {
                throw new IllegalArgumentException();
            }
            
            if(!dataManager.getEmployeeDB().has(inputID)) {
                String message = "Employee ID not found in the database!";
                MainFrame.makeBeepSound();
                JOptionPane.showMessageDialog(null, message, MainFrame.TITLE, JOptionPane.ERROR_MESSAGE);
            }
            else {
                ScheduleSystem scheduleSystem = dataManager.getScheduleSystem();
                if(scheduleSystem.hasSchedule(inputID)) {
                    String message = "Do you want to remove the schedule assigned to this ID?";
                    int choice = JOptionPane.showConfirmDialog(null, message, MainFrame.TITLE, 
                                                                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(choice == JOptionPane.YES_OPTION) {
                        if(scheduleSystem.removeSchedule(inputID)) {
                            message = "Schedule removed successfully!";
                            JOptionPane.showMessageDialog(null, message, MainFrame.TITLE, JOptionPane.INFORMATION_MESSAGE);

                            if(inputID == currentScheduleID) {
                                loadScheduleData(null);
                                hideTablePanel();
                                disableUtilityButtons();
                                currentScheduleID = UNINITIALIZED_ID;
                            }

                            frame.setSaved(panelIndex, false); 
                        }
                        else {
                            message = "Error! Cannot remove schedule!";
                            MainFrame.makeBeepSound();
                            JOptionPane.showMessageDialog(null, message, MainFrame.TITLE, JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
                else {
                    String message = "The input ID has not been assigned to any schedules!";
                    MainFrame.makeBeepSound();
                    JOptionPane.showMessageDialog(null, message, MainFrame.TITLE, JOptionPane.WARNING_MESSAGE);
                }
            }
        }
        catch(IllegalArgumentException e) {
            scheduleInvalidInputLabel.setText(INPUT_ERROR_PROMPT);
        }
    }//GEN-LAST:event_scheduleRemoveScheduleButtonActionPerformed

    private void scheduleAddButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scheduleAddButtonActionPerformed
        scheduleAddDialog.setVisible(true);
    }//GEN-LAST:event_scheduleAddButtonActionPerformed

    private void scheduleDeleteDateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scheduleDeleteDateButtonActionPerformed
        int columnIndex = scheduleTable.getSelectedColumn();
        scheduleTable.setRowSelectionInterval(0, scheduleTable.getRowCount() - 1);
        
        String message = "Do you want to delete all records in the selected column?";
        int choice = JOptionPane.showConfirmDialog(null, message, MainFrame.TITLE, 
                                                  JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if(choice == JOptionPane.YES_OPTION) {
            clearTableColumnData(columnIndex);

            message = "Records deleted successfully!";
            JOptionPane.showMessageDialog(null, message, MainFrame.TITLE, JOptionPane.INFORMATION_MESSAGE);
            
            renewUtilityButtons();

            frame.setSaved(panelIndex, false);
        }
        else {
            scheduleTable.setRowSelectionInterval(0, 0);
        }
    }//GEN-LAST:event_scheduleDeleteDateButtonActionPerformed

    private void scheduleTableMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_scheduleTableMousePressed
        // Double click event
        if(evt.getClickCount() == 2 && !evt.isConsumed()) {
            evt.consume();
            JTable table = (JTable) evt.getSource();

            if(table.getSelectedColumn() != -1 && table.getSelectedRow() != -1) {
                Point point = evt.getPoint();
                int row = table.rowAtPoint(point);
                int column = table.columnAtPoint(point);

                // Open edit date dialog if a date is double clicked
                if(row == 0) {
                    Date date = (Date) table.getValueAt(row, column);
                    setEditDateBoxes(date);
                    scheduleEditDateDialog.setVisible(true);
                }
                // Otherwise, open edit time dialog
                else {
                    WorkingHour WH = (WorkingHour) table.getValueAt(row, column);
                    setEditTimeSpinners(WH);
                    scheduleEditTimeDialog.setVisible(true);
                }
            }
        }
        else {
            int selectedRow = scheduleTable.getSelectedRow();
            int selectedColumn = scheduleTable.getSelectedColumn();
            Object selectedObject = scheduleTable.getValueAt(selectedRow, selectedColumn);
            // If a record is selected
            if(selectedObject != null) {
                // If a date is selected
                if(selectedRow == 0) {
                    scheduleDeleteTimeButton.setEnabled(false);
                    scheduleEditDateButton.setEnabled(true);
                    scheduleEditTimeButton.setEnabled(false);
                }
                // Otherwise, a working hour is selected
                else {
                    scheduleDeleteTimeButton.setEnabled(true);
                    scheduleEditDateButton.setEnabled(false);
                    scheduleEditTimeButton.setEnabled(true);
                }
            }
            // Otherwise, no record is selected
            else {
                // Disable the according buttons
                scheduleDeleteTimeButton.setEnabled(false);
                scheduleEditTimeButton.setEnabled(false);
            }
            
            // If there is a record in this column
            if(scheduleTable.getValueAt(0, selectedColumn) != null) {
                scheduleDeleteDateButton.setEnabled(true);
            }
            else {
                scheduleDeleteDateButton.setEnabled(false);
            }
        }
    }//GEN-LAST:event_scheduleTableMousePressed

    private void scheduleAddDialogWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_scheduleAddDialogWindowClosing
        scheduleAddOptionPane.setValue(JOptionPane.CLOSED_OPTION);
    }//GEN-LAST:event_scheduleAddDialogWindowClosing

    private void scheduleAddOptionPanePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_scheduleAddOptionPanePropertyChange
        String prop = evt.getPropertyName();
        if(scheduleAddDialog.isVisible() && (evt.getSource() == scheduleAddOptionPane) 
            && (prop.equals(JOptionPane.VALUE_PROPERTY))) {
            
            Object value = scheduleAddOptionPane.getValue();
            
            if(value == JOptionPane.UNINITIALIZED_VALUE) {
                // Ignore reset
                return;
            }
            
            // Reset value so that later on, if the same button is pressed,
            // a property change event will be fired again
            scheduleAddOptionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
            
            if(value.equals(JOptionPane.OK_OPTION)) {
                int startHour = (Integer) scheduleAddStartHourSpinner.getValue();
                int startMin = (Integer) scheduleAddStartMinSpinner.getValue();
                int endHour = (Integer) scheduleAddEndHourSpinner.getValue();
                int endMin = (Integer) scheduleAddEndMinSpinner.getValue();
                Time startTime = new Time(startHour, startMin);
                Time endTime = new Time(endHour, endMin);
                if(startTime.compareTo(endTime) > 0) {
                    String message = """
                                     Invalid input!
                                     Start time cannot be after end time!""";
                    MainFrame.makeBeepSound();
                    JOptionPane.showMessageDialog(null, message, MainFrame.TITLE, JOptionPane.ERROR_MESSAGE);
                }
                else {
                    WorkingHour newWH = new WorkingHour(startTime, endTime);
                    
                    int day = Integer.parseInt((String) scheduleAddDayBox.getSelectedItem());
                    int month = Integer.parseInt((String) scheduleAddMonthBox.getSelectedItem());
                    int year = Integer.parseInt((String) scheduleAddYearBox.getSelectedItem());
                    Date date = new Date(day, month, year);
                    int columnIndex = dayOfWeekToIndex(date.getDayOfWeek());
                    
                    IndividualSchedule schedule = dataManager.getScheduleSystem().accessSchedule(currentScheduleID);
                    if(schedule.containsWorkingHour(date, newWH)) {
                        String message = "A record with the same data has already existed!";
                        MainFrame.makeBeepSound();
                        JOptionPane.showMessageDialog(null, message, MainFrame.TITLE, JOptionPane.ERROR_MESSAGE);
                    }
                    else {
                        String message = "Do you want to add a record with the following data" + '\n' +
                                         "Date: " + date + '\n' +
                                         "Working Hour: " + newWH + '\n' +
                                         "to the schedule?";
                    
                        int choice  = JOptionPane.showConfirmDialog(null, message, MainFrame.TITLE,
                                                                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                        if(choice == JOptionPane.YES_OPTION) {
                            
                            if(scheduleTable.getRowCount() != 0) {
                                Object previousObject = scheduleTable.getValueAt(0, columnIndex);
                                if(previousObject != null) {
                                    Date previousDate = (Date) previousObject;
                                    if(!previousDate.equals(date)) {
                                        schedule.removeEntry(previousDate);
                                    }
                                }
                            }
                            
                            schedule.addEntry(date, newWH);
                            loadTableColumnData(schedule, date, columnIndex);
                            
                            int row;
                            for(int i = 1; ; i++) {
                                WorkingHour currentWH = (WorkingHour) scheduleTable.getValueAt(i, columnIndex);
                                if(currentWH.equals(newWH)) {
                                    row = i;
                                    break;
                                }
                            }

                            message = "Record added successfully!";
                            JOptionPane.showMessageDialog(null, message, MainFrame.TITLE, JOptionPane.INFORMATION_MESSAGE);
                            
                            TableRenderingSupporter.selectCellAndScrollTableToCell(scheduleTable, row, columnIndex);
                            clearAndHideAddDialog();
                            
                            scheduleEditTimeButton.setEnabled(true);
                            scheduleDeleteTimeButton.setEnabled(true);
                            
                            frame.setSaved(panelIndex, false);
                        }
                    }
                }
            }
            else {
                clearAndHideAddDialog();
            }
        }
    }//GEN-LAST:event_scheduleAddOptionPanePropertyChange

    private void scheduleEditTimeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scheduleEditTimeButtonActionPerformed
        WorkingHour selectedWH = (WorkingHour) scheduleTable.getValueAt(scheduleTable.getSelectedRow(), scheduleTable.getSelectedColumn());
        setEditTimeSpinners(selectedWH);
        
        scheduleEditTimeDialog.setVisible(true);
    }//GEN-LAST:event_scheduleEditTimeButtonActionPerformed

    private void scheduleEditDateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scheduleEditDateButtonActionPerformed
        Date selectedDate = (Date) scheduleTable.getValueAt(scheduleTable.getSelectedRow(), scheduleTable.getSelectedColumn());
        setEditDateBoxes(selectedDate);
        
        scheduleEditDateDialog.setVisible(true);
    }//GEN-LAST:event_scheduleEditDateButtonActionPerformed

    private void scheduleEditTimeDialogWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_scheduleEditTimeDialogWindowClosing
        scheduleEditTimeOptionPane.setValue(JOptionPane.CLOSED_OPTION);
    }//GEN-LAST:event_scheduleEditTimeDialogWindowClosing

    private void scheduleEditTimeOptionPanePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_scheduleEditTimeOptionPanePropertyChange
        String prop = evt.getPropertyName();
        if(scheduleEditTimeDialog.isVisible() && (evt.getSource() == scheduleEditTimeOptionPane) 
            && (prop.equals(JOptionPane.VALUE_PROPERTY))) {
            
            Object value = scheduleEditTimeOptionPane.getValue();
            
            if(value == JOptionPane.UNINITIALIZED_VALUE) {
                // Ignore reset
                return;
            }
            
            // Reset value so that later on, if the same button is pressed,
            // a property change event will be fired again
            scheduleEditTimeOptionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
            
            if(value.equals(JOptionPane.OK_OPTION)) {
                int startHour = (Integer) scheduleEditTimeStartHourSpinner.getValue();
                int startMin = (Integer) scheduleEditTimeStartMinSpinner.getValue();
                int endHour = (Integer) scheduleEditTimeEndHourSpinner.getValue();
                int endMin = (Integer) scheduleEditTimeEndMinSpinner.getValue();
                Time startTime = new Time(startHour, startMin);
                Time endTime = new Time(endHour, endMin);
                if(startTime.compareTo(endTime) > 0) {
                    String message = """
                                     Invalid input!
                                     Start time cannot be after end time!""";
                    MainFrame.makeBeepSound();
                    JOptionPane.showMessageDialog(null, message, MainFrame.TITLE, JOptionPane.ERROR_MESSAGE);
                }
                else {
                    int columnIndex = scheduleTable.getSelectedColumn();
                    Date date = (Date) scheduleTable.getValueAt(0, scheduleTable.getSelectedColumn());
                    WorkingHour oldWH = (WorkingHour) scheduleTable.getValueAt(scheduleTable.getSelectedRow(), scheduleTable.getSelectedColumn());
                    WorkingHour newWH = new WorkingHour(startTime, endTime);
                    IndividualSchedule schedule = dataManager.getScheduleSystem().accessSchedule(currentScheduleID);
                    if(schedule.containsWorkingHour(date, newWH)) {
                        String message = "A record with the same data has already existed!";
                        MainFrame.makeBeepSound();
                        JOptionPane.showMessageDialog(null, message, MainFrame.TITLE, JOptionPane.ERROR_MESSAGE);
                    }
                    else {
                        String message = "Confirm to change: " + oldWH + '\n' +
                                         "               to: " + newWH + '?';
                    
                        int choice  = JOptionPane.showConfirmDialog(null, message, MainFrame.TITLE,
                                                                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                        if(choice == JOptionPane.YES_OPTION) {
                            schedule.modifyWorkingHour(date, oldWH, newWH);
                            loadTableColumnData(schedule, date, scheduleTable.getSelectedColumn());
                            
                            int row;
                            for(int i = 1; ; i++) {
                                WorkingHour currentWH = (WorkingHour) scheduleTable.getValueAt(i, columnIndex);
                                if(currentWH.equals(newWH)) {
                                    row = i;
                                    break;
                                }
                            }
                            
                            message = "Time edited successfully!";
                            JOptionPane.showMessageDialog(null, message, MainFrame.TITLE, JOptionPane.INFORMATION_MESSAGE);
                            
                            TableRenderingSupporter.selectCellAndScrollTableToCell(scheduleTable, row, columnIndex);
                            
                            clearAndHideEditTimeDialog();
                            frame.setSaved(panelIndex, false);
                        }
                    }
                }
            }
            else {
                clearAndHideEditTimeDialog();
            }
        }
    }//GEN-LAST:event_scheduleEditTimeOptionPanePropertyChange

    private void scheduleEditDateDialogWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_scheduleEditDateDialogWindowClosing
        scheduleEditDateOptionPane.setValue(JOptionPane.CLOSED_OPTION);
    }//GEN-LAST:event_scheduleEditDateDialogWindowClosing

    private void scheduleEditDateOptionPanePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_scheduleEditDateOptionPanePropertyChange
        String prop = evt.getPropertyName();
        if(scheduleEditDateDialog.isVisible() && (evt.getSource() == scheduleEditDateOptionPane) 
            && (prop.equals(JOptionPane.VALUE_PROPERTY))) {
            
            Object value = scheduleEditDateOptionPane.getValue();
            
            if(value == JOptionPane.UNINITIALIZED_VALUE) {
                // Ignore reset
                return;
            }
            
            // Reset value so that later on, if the same button is pressed,
            // a property change event will be fired again
            scheduleEditDateOptionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
            
            if(value.equals(JOptionPane.OK_OPTION)) {
                int oldColumnIndex = scheduleTable.getSelectedColumn();
                Date oldDate = (Date) scheduleTable.getValueAt(scheduleTable.getSelectedRow(), oldColumnIndex);
                
                int newDay = Integer.parseInt((String) scheduleEditDateDayBox.getSelectedItem());
                int newMonth = Integer.parseInt((String) scheduleEditDateMonthBox.getSelectedItem());
                int newYear = Integer.parseInt((String) scheduleEditDateYearBox.getSelectedItem());
                Date newDate = new Date(newDay, newMonth, newYear);
                int newColumnIndex = dayOfWeekToIndex(newDate.getDayOfWeek());
                
                IndividualSchedule schedule = dataManager.getScheduleSystem().accessSchedule(currentScheduleID);
                
                Object targetObject = scheduleTable.getValueAt(0, newColumnIndex);
                if(targetObject != null) {
                    Date targetDate = (Date) targetObject;
                    
                    if(newDate.equals(oldDate)) {
                        clearAndHideEditDateDialog();
                    }
                    else {
                        if(oldColumnIndex == newColumnIndex) {
                            String message = "Confirm to edit" + '\n' +
                                             "Date: " + oldDate + '\n' +
                                             "to" + '\n' +
                                             "Date: " + newDate + '?';
                            
                            int choice  = JOptionPane.showConfirmDialog(null, message, MainFrame.TITLE,
                                                                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                            if(choice == JOptionPane.YES_OPTION) {
                                for(var WH : schedule.getWorkingHours(oldDate)) {
                                    schedule.modifyDate(WH, oldDate, newDate);
                                }
                                
                                DefaultTableModel model = (DefaultTableModel) scheduleTable.getModel();
                                model.setValueAt(newDate, 0, newColumnIndex);
                                
                                message = "Date edited successfully";
                                JOptionPane.showMessageDialog(null, message, MainFrame.TITLE, JOptionPane.INFORMATION_MESSAGE);

                                clearAndHideEditDateDialog();
                                frame.setSaved(panelIndex, false);
                            }
                        }
                        else {
                            String message = """
                                            There are records already existed at the destination column!
                                            Do you want to swap or replace records?""";

                            String[] choices = {"Swap", "Replace", "Cancel"};
                            int choice = JOptionPane.showOptionDialog(null, message, MainFrame.TITLE, JOptionPane.YES_NO_CANCEL_OPTION, 
                                                                    JOptionPane.WARNING_MESSAGE, null, choices, null);

                            if(choice == JOptionPane.YES_OPTION || choice == JOptionPane.NO_OPTION) {
                                if(choice == JOptionPane.YES_OPTION) {
                                    WorkingHour[] targetWHs = schedule.getWorkingHours(targetDate);
                                    schedule.removeEntry(targetDate);

                                    for(var WH : schedule.getWorkingHours(oldDate)) {
                                        schedule.modifyDate(WH, oldDate, newDate);
                                    }

                                    for(var WH : targetWHs) {
                                        schedule.addEntry(oldDate, WH);
                                    }

                                    loadTableColumnData(schedule, newDate, newColumnIndex);
                                    loadTableColumnData(schedule, oldDate, oldColumnIndex);
                                }
                                else {       
                                    schedule.removeEntry(targetDate);
                                    for(var WH : schedule.getWorkingHours(oldDate)) {
                                        schedule.modifyDate(WH, oldDate, newDate);
                                    }

                                    loadTableColumnData(schedule, newDate, newColumnIndex);
                                    loadTableColumnData(schedule, null, oldColumnIndex);
                                }

                                scheduleTable.setColumnSelectionInterval(newColumnIndex, newColumnIndex);
                                scheduleTable.setRowSelectionInterval(0, 0);

                                message = "Date edited successfully";
                                JOptionPane.showMessageDialog(null, message, MainFrame.TITLE, JOptionPane.INFORMATION_MESSAGE);

                                clearAndHideEditDateDialog();
                                frame.setSaved(panelIndex, false);
                            }
                        }
                    }
                }
                else {
                    String message = "Confirm to edit" + '\n' +
                                     "Date: " + oldDate + '\n' +
                                     "to" + '\n' +
                                     "Date: " + newDate + '?';
                    int choice  = JOptionPane.showConfirmDialog(null, message, MainFrame.TITLE,
                                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(choice == JOptionPane.YES_OPTION) {
                        for(var WH : schedule.getWorkingHours(oldDate)) {
                            schedule.modifyDate(WH, oldDate, newDate);
                        }
                        loadTableColumnData(schedule, null, oldColumnIndex);
                        loadTableColumnData(schedule, newDate, newColumnIndex);
                        
                        TableRenderingSupporter.selectCellAndScrollTableToCell(scheduleTable, 0, newColumnIndex);
                        
                        message = "Date edited successfully";
                        JOptionPane.showMessageDialog(null, message, MainFrame.TITLE, JOptionPane.INFORMATION_MESSAGE);

                        frame.setSaved(panelIndex, false);
                        clearAndHideEditDateDialog();
                    }
                }
            }
            else {
                clearAndHideEditDateDialog();
            }
        }
    }//GEN-LAST:event_scheduleEditDateOptionPanePropertyChange

    private void scheduleFindTimeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scheduleFindTimeButtonActionPerformed
        int selectedColumn = scheduleTable.getSelectedColumn();
        if(selectedColumn != -1) {
            Object headingObject = scheduleTable.getValueAt(0, selectedColumn);
            if(headingObject != null) {
                Date date = (Date) headingObject;
                setFindTimeDateBoxes(date);
            }
        }
        
        scheduleFineTimeDialog.setVisible(true);
    }//GEN-LAST:event_scheduleFindTimeButtonActionPerformed

    private void scheduleFineTimeDialogWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_scheduleFineTimeDialogWindowClosing
        scheduleFindTimeOptionPane.setValue(JOptionPane.CLOSED_OPTION);
    }//GEN-LAST:event_scheduleFineTimeDialogWindowClosing

    private void scheduleFindTimeOptionPanePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_scheduleFindTimeOptionPanePropertyChange
        String prop = evt.getPropertyName();
        if(scheduleFineTimeDialog.isVisible() && (evt.getSource() == scheduleFindTimeOptionPane) 
            && (prop.equals(JOptionPane.VALUE_PROPERTY))) {
            
            Object value = scheduleFindTimeOptionPane.getValue();
            
            if(value == JOptionPane.UNINITIALIZED_VALUE) {
                // Ignore reset
                return;
            }
            
            // Reset value so that later on, if the same button is pressed,
            // a property change event will be fired again
            scheduleFindTimeOptionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
            
            if(value.equals(JOptionPane.OK_OPTION)) {
                IndividualSchedule schedule = dataManager.getScheduleSystem().accessSchedule(currentScheduleID);
                int day = Integer.parseInt((String) scheduleFindTimeDayBox.getSelectedItem());
                int month = Integer.parseInt((String) scheduleFindTimeMonthBox.getSelectedItem());
                int year = Integer.parseInt((String) scheduleFindTimeYearBox.getSelectedItem());
                Date date = new Date(day, month, year);
                
                int hour = (Integer) scheduleFindTimeHourSpinner.getValue();
                int min = (Integer) scheduleFindTimeMinSpinner.getValue();
                Time time = new Time(hour, min);
                
                WorkingHour[] targetWHs = schedule.locateTimePoint(date, time);
                
                if(targetWHs.length > 0) {
                    // We find only the first target
                    WorkingHour targetWH = targetWHs[0];
                    int columnIndex = dayOfWeekToIndex(date.getDayOfWeek());
                    for(int i = 1; ; i++) {
                        WorkingHour currentWH = (WorkingHour) scheduleTable.getValueAt(i, columnIndex);
                        if(currentWH.equals(targetWH)) {
                            TableRenderingSupporter.selectCellAndScrollTableToCell(scheduleTable, i, columnIndex);
                            break;
                        }
                    }

                    clearAndHideLocateTimeDialog();
                }
                else {
                    String message = "Time not found!";
                    MainFrame.makeBeepSound();
                    JOptionPane.showMessageDialog(null, message, MainFrame.TITLE, JOptionPane.WARNING_MESSAGE);
                    clearAndHideLocateTimeDialog();
                }
            }
            else {
                clearAndHideLocateTimeDialog();
            }
        }
    }//GEN-LAST:event_scheduleFindTimeOptionPanePropertyChange

    private void scheduleDeleteTimeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scheduleDeleteTimeButtonActionPerformed
        String message = "Do you want to delete the selected record?";
        int choice = JOptionPane.showConfirmDialog(null, message, MainFrame.TITLE, 
                                                  JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if(choice == JOptionPane.YES_OPTION) {
            int selectedRow = scheduleTable.getSelectedRow();
            int selectedColumn = scheduleTable.getSelectedColumn();
            WorkingHour selectedWH = (WorkingHour) scheduleTable.getValueAt(selectedRow, selectedColumn);
            Date date = (Date) scheduleTable.getValueAt(0, selectedColumn);
            
            IndividualSchedule schedule = dataManager.getScheduleSystem().accessSchedule(currentScheduleID);
            schedule.removeEntry(date, selectedWH);
            
            loadTableColumnData(schedule, date, selectedColumn);
            
            boolean isColumnEmpty = true;
            for(int i = 0; i < scheduleTable.getRowCount(); i++) {
                if(scheduleTable.getValueAt(i, selectedColumn) != null) {
                    isColumnEmpty = false;
                    break;
                }
            }
            
            if(isColumnEmpty) {
                scheduleDeleteTimeButton.setEnabled(false);
                scheduleEditTimeButton.setEnabled(false);
                scheduleDeleteDateButton.setEnabled(false);
                scheduleEditDateButton.setEnabled(false);
            }
            else {
                if(scheduleTable.getRowCount() == selectedRow) {
                    scheduleTable.setRowSelectionInterval(selectedRow - 1, selectedRow - 1);
                }
            }
            
            message = "Record deleted successfully!";
            JOptionPane.showMessageDialog(null, message, MainFrame.TITLE, JOptionPane.INFORMATION_MESSAGE);

            frame.setSaved(panelIndex, false);
        }
        
    }//GEN-LAST:event_scheduleDeleteTimeButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton scheduleAccessScheduleButton;
    private javax.swing.JButton scheduleAddButton;
    private javax.swing.JLabel scheduleAddDateLabel;
    private javax.swing.JComboBox<String> scheduleAddDayBox;
    private javax.swing.JLabel scheduleAddDayLabel;
    private javax.swing.JDialog scheduleAddDialog;
    private javax.swing.JLabel scheduleAddEndHourLabel;
    private javax.swing.JSpinner scheduleAddEndHourSpinner;
    private javax.swing.JLabel scheduleAddEndMinLabel;
    private javax.swing.JSpinner scheduleAddEndMinSpinner;
    private javax.swing.JLabel scheduleAddEndTimeLabel;
    private javax.swing.JComboBox<String> scheduleAddMonthBox;
    private javax.swing.JLabel scheduleAddMonthLabel;
    private javax.swing.JOptionPane scheduleAddOptionPane;
    private javax.swing.JPanel scheduleAddPanel;
    private javax.swing.JLabel scheduleAddStartHourLabel;
    private javax.swing.JSpinner scheduleAddStartHourSpinner;
    private javax.swing.JLabel scheduleAddStartMinLabel;
    private javax.swing.JSpinner scheduleAddStartMinSpinner;
    private javax.swing.JLabel scheduleAddStartTimeLabel;
    private javax.swing.JComboBox<String> scheduleAddYearBox;
    private javax.swing.JLabel scheduleAddYearLabel;
    private javax.swing.JButton scheduleCreateScheduleButton;
    private javax.swing.JButton scheduleDeleteDateButton;
    private javax.swing.JButton scheduleDeleteTimeButton;
    private javax.swing.JButton scheduleEditDateButton;
    private javax.swing.JLabel scheduleEditDateDateLabel;
    private javax.swing.JComboBox<String> scheduleEditDateDayBox;
    private javax.swing.JLabel scheduleEditDateDayLabel;
    private javax.swing.JDialog scheduleEditDateDialog;
    private javax.swing.JComboBox<String> scheduleEditDateMonthBox;
    private javax.swing.JLabel scheduleEditDateMonthLabel;
    private javax.swing.JOptionPane scheduleEditDateOptionPane;
    private javax.swing.JPanel scheduleEditDatePanel;
    private javax.swing.JComboBox<String> scheduleEditDateYearBox;
    private javax.swing.JLabel scheduleEditDateYearLabel;
    private javax.swing.JButton scheduleEditTimeButton;
    private javax.swing.JDialog scheduleEditTimeDialog;
    private javax.swing.JLabel scheduleEditTimeEndHourLabel;
    private javax.swing.JSpinner scheduleEditTimeEndHourSpinner;
    private javax.swing.JLabel scheduleEditTimeEndMinLabel;
    private javax.swing.JSpinner scheduleEditTimeEndMinSpinner;
    private javax.swing.JLabel scheduleEditTimeEndTimeLabel;
    private javax.swing.JOptionPane scheduleEditTimeOptionPane;
    private javax.swing.JPanel scheduleEditTimePanel;
    private javax.swing.JLabel scheduleEditTimeStartHourLabel;
    private javax.swing.JSpinner scheduleEditTimeStartHourSpinner;
    private javax.swing.JLabel scheduleEditTimeStartMinLabel;
    private javax.swing.JSpinner scheduleEditTimeStartMinSpinner;
    private javax.swing.JLabel scheduleEditTimeStartTimeLabel;
    private javax.swing.JPanel scheduleEntryField;
    private javax.swing.JButton scheduleFindTimeButton;
    private javax.swing.JLabel scheduleFindTimeDateLabel;
    private javax.swing.JComboBox<String> scheduleFindTimeDayBox;
    private javax.swing.JLabel scheduleFindTimeDayLabel;
    private javax.swing.JLabel scheduleFindTimeHourLabel;
    private javax.swing.JSpinner scheduleFindTimeHourSpinner;
    private javax.swing.JLabel scheduleFindTimeLabel;
    private javax.swing.JLabel scheduleFindTimeMinLabel;
    private javax.swing.JSpinner scheduleFindTimeMinSpinner;
    private javax.swing.JComboBox<String> scheduleFindTimeMonthBox;
    private javax.swing.JOptionPane scheduleFindTimeOptionPane;
    private javax.swing.JPanel scheduleFindTimePanel;
    private javax.swing.JLabel scheduleFindTimeTimeLabel;
    private javax.swing.JComboBox<String> scheduleFindTimeYearBox;
    private javax.swing.JLabel scheduleFindTimeYearLabel;
    private javax.swing.JDialog scheduleFineTimeDialog;
    private javax.swing.JTextField scheduleIDInputTextField;
    private javax.swing.JLabel scheduleIDPromptLabel;
    private javax.swing.JLabel scheduleInvalidInputLabel;
    private javax.swing.JPanel schedulePanel;
    private javax.swing.JLabel schedulePromptLabel;
    private javax.swing.JButton scheduleRemoveScheduleButton;
    private javax.swing.JTable scheduleTable;
    private javax.swing.JPanel scheduleTableEmptyPanel;
    private javax.swing.JPanel scheduleTableField;
    private javax.swing.JScrollPane scheduleTableScrollPane;
    private javax.swing.JPanel scheduleUtilityField;
    // End of variables declaration//GEN-END:variables
   
    private static int dayOfWeekToIndex(int dateOfWeek) {
        int result = dateOfWeek - 1;
        return (result >= 0) ? result : (result + 7);
    }
    
    private static Object[] createEmptyRow() {
        return new Object[]{null, null, null, null, null, null, null};
    }
    
    @Override
    public void handleSaveAction() {
        // DO NOTHING
    }
    
    @Override
    public void handleDiscardAction() {
        // If there is a schedule currently being showed, update the table data. Otherwise, do nothing
        if(currentScheduleID != UNINITIALIZED_ID) {
            ScheduleSystem scheduleSystem = dataManager.getScheduleSystem();
            IndividualSchedule schedule = scheduleSystem.accessSchedule(currentScheduleID);
            if(schedule == null) {
                hideTablePanel();
                disableUtilityButtons();
                currentScheduleID = UNINITIALIZED_ID;
            }
            else {
                loadScheduleData(schedule);
                scheduleTable.clearSelection();
                renewUtilityButtons();
            }
        }
    }
}
