/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package groupthinhquan.hospitalmanagement.util;

import groupthinhquan.hospitalmanagement.core.Time;
import javax.swing.JSpinner;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.SpinnerNumberModel;

/**
 *
 * @author Thinh
 */

public final class TimeInputSpinnerSupporter {
    private TimeInputSpinnerSupporter() {}
    
    public static void setupTimeInputSpinners(JSpinner hourSpinner, JSpinner minSpinner) {
        hourSpinner.setModel(new SpinnerNumberModel(0, 0, 23, 1));
        hourSpinner.setEditor(new NumberEditor(hourSpinner, "00"));
        minSpinner.setModel(new SpinnerNumberModel(0, 0, 59, 1));
        minSpinner.setEditor(new NumberEditor(minSpinner, "00"));
    }
    
    public static void setTimeInputSpinners(JSpinner hourSpinner, JSpinner minSpinner, int hour, int min) {
        hourSpinner.setValue(hour);
        minSpinner.setValue(min);
    }
    
    public static void setTimeInputSpinners(JSpinner hourSpinner, JSpinner minSpinner, Time time) {
        hourSpinner.setValue(time.getHour());
        minSpinner.setValue(time.getMin());
    }
    
    public static void clearTimeInputSpinners(JSpinner hourSpinner, JSpinner minSpinner) {
        hourSpinner.setValue(0);
        minSpinner.setValue(0);
    }
}
