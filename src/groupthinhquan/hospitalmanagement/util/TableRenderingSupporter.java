/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package groupthinhquan.hospitalmanagement.util;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Thinh
 */

public final class TableRenderingSupporter {
    private TableRenderingSupporter() {}
    
    public static void centerTableHeader(JTable table) {
        ((DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
    }
    
    public static void selectCellAndScrollTableToCell(JTable table, int row, int column) {
        table.setRowSelectionInterval(row, row);
        table.setColumnSelectionInterval(column, column);
        table.scrollRectToVisible(table.getCellRect(row, column, true));
    }
    
    public static void selectRowAndScrollTableToCell(JTable table, int row, int column) {
        table.setRowSelectionInterval(row, row);
        table.scrollRectToVisible(table.getCellRect(row, column, true));
    }
        
    public static void selectColumnAndScrollTableToCell(JTable table, int row, int column) {
        table.setColumnSelectionInterval(column, column);
        table.scrollRectToVisible(table.getCellRect(row, column, true));
    }
    
    public static boolean isRowIdentical(JTable table, int modelRow, Object[] toCompareRowData) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        if(model.getColumnCount() == toCompareRowData.length) {
            for(int i = 0; i < toCompareRowData.length; i++) {
                if(!model.getValueAt(modelRow, i).equals(toCompareRowData[i])) {
                    return false;
                }
            }
            
            return true;
        }
        else {
            return false;
        }
    }
    
    public static void updateRow(JTable table, int modelRow, Object[] data) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        for(int i = 0; i < data.length; i++) {
            model.setValueAt(data[i], modelRow, i);
        }
    }
}
