/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package groupthinhquan.hospitalmanagement.util;

import groupthinhquan.hospitalmanagement.core.Date;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.YearMonth;
import javax.swing.JComboBox;

/**
 *
 * @author Thinh
 */

public final class DateInputBoxSupporter {
    private DateInputBoxSupporter() {}
    
    public static void setupDateInputBoxes(JComboBox<String> dayBox, JComboBox<String> monthBox, JComboBox<String> yearBox) {
        /* CLEAR THE BOXES */
        dayBox.removeAllItems();
        monthBox.removeAllItems();
        yearBox.removeAllItems();
        
        /* SET BOXES EDITABLE */
        dayBox.setEditable(true);
        monthBox.setEditable(true);
        yearBox.setEditable(true);
        
        /* SETUP ITEMS */
        // Setup year box
        // Year items will be current system clock's year plus 4 years away (both in the past and in the future) 
        // from current system clock's year
        final int CURRENT_YEAR = YearMonth.now().getYear();
        final int START_YEAR = CURRENT_YEAR - 4;
        final int END_YEAR = CURRENT_YEAR + 4;
        for(int i = START_YEAR; i <= END_YEAR; i++) {
            yearBox.addItem(Integer.toString(i));
        }
        // Convert threshold years to strings for later use
        final String CURRENT_YEAR_STRING = Integer.toString(CURRENT_YEAR);
        final String START_YEAR_STRING = Integer.toString(START_YEAR);
        final String END_YEAR_STRING = Integer.toString(END_YEAR);
        // Set initially selected year to current system clock's year
        yearBox.setSelectedItem(CURRENT_YEAR_STRING);
      
        
        // Setup day and month box
        // Since the initialially selected month is January, add items into the box in accordance with it
        final int INITIAL_DAY = 1;
        final int INITIAL_MONTH = 1;
        final int MAX_MONTH = 12;
        final int INITIAL_MAX_DAY = Date.computeMaxDay(INITIAL_MONTH, CURRENT_YEAR);
        for(int i = INITIAL_DAY; i <= INITIAL_MAX_DAY; i++) {
            if(i <= MAX_MONTH) {
                monthBox.addItem(Integer.toString(i));
            }
            
            dayBox.addItem(Integer.toString(i));
        }
        // Convert threshold days and months to strings for later use
        final String INITIAL_DAY_STRING = Integer.toString(INITIAL_DAY);
        final String INITIAL_MONTH_STRING = Integer.toString(INITIAL_MONTH);
        final String MAX_MONTH_STRING = Integer.toString(MAX_MONTH);
        
        /* SETUP LISTENERS */
        final int LEAP_YEAR_FEB_DAY = 29;
        final String FEB_STRING = "2";
        final String LEAP_YEAR_FEB_DAY_STRING = Integer.toString(LEAP_YEAR_FEB_DAY);
        final String NONLEAP_YEAR_FEB_DAY_STRING = "28";
        
        yearBox.addActionListener(new ActionListener(){ 
            @Override
            public void actionPerformed(ActionEvent evt) {
                String input = (String) yearBox.getSelectedItem();
                try {
                    int inputYear = Integer.parseInt(input);
                    // If input is smaller than START_YEAR
                    if(inputYear < START_YEAR) {
                        // Change selected item to START_YEAR
                        yearBox.setSelectedItem(START_YEAR_STRING);
                    }
                    // Else if input is greater than END_YEAR
                    else if(inputYear > END_YEAR) {
                        // Change selected item to END_YEAR
                        yearBox.setSelectedItem(END_YEAR_STRING);
                    }
                    // Otherwise
                    else {
                        // Set the selected item corresponding to the input
                        yearBox.setSelectedItem(input);
                        // Since the previous year may be a leap year whereas the new year is not or vice versa
                        // we need to handle such situation
                        String selectedMonth = (String) monthBox.getSelectedItem();
                        int dayCount = dayBox.getItemCount();
                        
                        // This situation occurs only if the currently selected month is February
                        if(selectedMonth.equals(FEB_STRING)) {
                            // flag to determine whether the previous year is a leap year
                            boolean isPreviousLeapYear = dayCount == LEAP_YEAR_FEB_DAY;
                            // flag to determine whether the new year is a leap year
                            boolean isNewLeapYear = Date.isLeapYear(inputYear);
                            // If both years are leap years OR both are neither leap years, the case is trivial
                            // Otherwise, this is a special case
                            boolean isSpecialCase = isPreviousLeapYear ^ isNewLeapYear;
                            
                            // Only special case need to be handled
                            if(isSpecialCase) {
                                // In case of the previous year being a leap year whereas the new year is not
                                if(isPreviousLeapYear) {
                                    String selectedDay = (String) dayBox.getSelectedItem();
                                    // If the currently selected day is 29, roll it back to 28
                                    if(selectedDay.equals(LEAP_YEAR_FEB_DAY_STRING)) {
                                        dayBox.setSelectedItem(NONLEAP_YEAR_FEB_DAY_STRING);
                                    }
                                    
                                    // Remove the selection of 29 for day (since the new year is not a leap year)
                                    dayBox.removeItemAt(LEAP_YEAR_FEB_DAY - 1);
                                }
                                // Otherwise
                                else {
                                    // Add the selection of 29 for day (since the new year is a leap year)
                                    dayBox.addItem(LEAP_YEAR_FEB_DAY_STRING);
                                }
                            }
                        }
                    }
                }
                // If the input is not an integer
                catch(NumberFormatException e) {
                    // Change selected input to CURRENT_YEAR
                    yearBox.setSelectedItem(CURRENT_YEAR_STRING);
                }
            }
        });
    
        monthBox.addActionListener(new ActionListener(){ 
            @Override
            public void actionPerformed(ActionEvent evt) {
                String input = (String) monthBox.getSelectedItem();
                try {
                    int inputMonth = Integer.parseInt(input);
                    // If input is smaller than INITIAL_MONTH
                    if(inputMonth < INITIAL_MONTH) {
                        // Change selected item to INITIAL_MONTH
                        monthBox.setSelectedItem(INITIAL_MONTH_STRING);
                    }
                    // Else if input is greater than MAX_MONTH
                    else if(inputMonth > MAX_MONTH) {
                        // Change selected item to MAX_MONTH
                        monthBox.setSelectedItem(MAX_MONTH_STRING);
                    }
                    // Otherwise
                    else {
                        // Set the selected item corresponding to the input
                        monthBox.setSelectedItem(input);
                        
                        // Adjust the day selection list so that its maximum day fittes the selected month and year
                        int selectedYear = Integer.parseInt((String)yearBox.getSelectedItem());
                        int newMaxDay = Date.computeMaxDay(inputMonth, selectedYear);
                        int previousMaxDay = dayBox.getItemCount();
                        
                        // Adjust only if the new maximum day is different from the previous maximum day
                        // If new maximum day is greater than the previous maximum day
                        if(newMaxDay > previousMaxDay) {
                            // Append new selections to the list until they are equal
                            for(int i = previousMaxDay + 1; i <= newMaxDay; i++) {
                                dayBox.addItem(Integer.toString(i));
                            }
                        }
                        // If new maximum day is less than the previous maximum day
                        else if(newMaxDay < previousMaxDay) {
                            // Truncate the list instead, also change the selected day if needed
                            int selectedDay = Integer.parseInt((String)dayBox.getSelectedItem());
                            if(selectedDay > newMaxDay) {
                                dayBox.setSelectedItem(Integer.toString(newMaxDay));
                            }
                            
                            for(int i = previousMaxDay; i > newMaxDay; i--) {
                                dayBox.removeItem(Integer.toString(i));
                            }
                        }
                    }
                }
                // If the input is not an integer
                catch(NumberFormatException e) {
                    // Change selected input to INITIAL_MONTH
                    monthBox.setSelectedItem(INITIAL_MONTH_STRING);
                }
            }
        });
    
        dayBox.addActionListener(new ActionListener(){ 
            @Override
            public void actionPerformed(ActionEvent evt) {
                String input = (String) dayBox.getSelectedItem();
                try {
                    int inputDay = Integer.parseInt(input);
                    int biggestValue = Integer.parseInt(
                                    (String)dayBox.getItemAt(dayBox.getItemCount() - 1));
                    // If input is smaller than INITIAL_DAY
                    if(inputDay < INITIAL_DAY) {
                        // Change selected item to INITIAL_DAY
                        dayBox.setSelectedItem(INITIAL_DAY_STRING);
                    }
                    // Else if input is greater than the biggest available value
                    else if(inputDay > biggestValue) {
                        // Change selected item to the biggest value
                        dayBox.setSelectedIndex(dayBox.getItemCount() - 1);
                    }
                    // Otherwise
                    else {
                        // Set the selected item corresponding to the input
                        dayBox.setSelectedItem(input);
                    }
                }
                // If the input is not an integer
                catch(NumberFormatException e) {
                    // Change selected input to INITIAL_DAY
                    dayBox.setSelectedItem(INITIAL_DAY_STRING);
                }
            }
        });
    }
    
    public static void setDateInputBoxes(JComboBox<String> dayBox, JComboBox<String> monthBox, JComboBox<String> yearBox,
                                         int day, int month, int year) {
        dayBox.setSelectedItem(Integer.toString(day));
        monthBox.setSelectedItem(Integer.toString(month));
        yearBox.setSelectedItem(Integer.toString(year));
    }
    
    public static void setDateInputBoxes(JComboBox<String> dayBox, JComboBox<String> monthBox, JComboBox<String> yearBox,
                                         Date date) {
        dayBox.setSelectedItem(Integer.toString(date.getDay()));
        monthBox.setSelectedItem(Integer.toString(date.getMonth()));
        yearBox.setSelectedItem(Integer.toString(date.getYear()));
    }
    
    public static void clearDateInputBoxes(JComboBox<String> dayBox, JComboBox<String> monthBox, JComboBox<String> yearBox) {
        dayBox.setSelectedIndex(0);
        monthBox.setSelectedIndex(0);
        yearBox.setSelectedItem(Integer.toString(YearMonth.now().getYear()));
    }
}
