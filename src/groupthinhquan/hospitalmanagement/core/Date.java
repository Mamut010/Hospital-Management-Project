/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package groupthinhquan.hospitalmanagement.core;

import java.util.UnknownFormatConversionException;

/**
 *
 * @author Thinh
 */

public final class Date implements Comparable<Date>, Cloneable {
    static private int msDefaultDay = 1;
    static private int msDefaultMonth = 1;
    static private int msDefaultYear = 1970;
    static private char mSeparator = '/';
    private int mDay;
    private int mMonth;
    private int mYear;
    
    // Construct a date object with default day, month and year
    public Date() {
        toDefault();
    }
    
    // Construct a date object with given day, month and year
    public Date(int day, int month, int year) {
        boolean valid = setDate(day, month, year);
        if(!valid) {
            toDefault();
        }
    }
    
    // toDefault changes day, month, year to their default values
    public void toDefault() {
        mDay = msDefaultDay;
        mMonth = msDefaultMonth;
        mYear = msDefaultYear;
    }
    
    // isLeapYear checks if a given year is a leap year
    public static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }  
    
    // parseDate parse a string to a date object
    // this function throws an UnknownFormatConversionException error if failed
    public static Date parseDate(String dateString) throws UnknownFormatConversionException {
        String[] dateTokens = dateString.trim().split(Character.toString(mSeparator));
        if(dateTokens.length != 3)
            throw new UnknownFormatConversionException(dateString);
        
        int day, month, year;
        
        try {
            day = Integer.parseInt(dateTokens[0]);
            month = Integer.parseInt(dateTokens[1]);
            year = Integer.parseInt(dateTokens[2]);
        }
        catch(NumberFormatException e) {
            throw new UnknownFormatConversionException(dateString);
        }

        return new Date(day, month, year);
    }
    
    // computeMaxDay returns the max day of a given month in a given year
    public static int computeMaxDay(int month, int year) {
        if(month > 0 && year > 0) {
            return switch(month) {
            case 1, 3, 5, 7, 8, 10, 12 -> {
                    yield 31;
                }
            case 4, 6, 9, 11 -> {
                    yield 30;
                }
            case 2 -> {
                if(isLeapYear(year))
                    yield 29;
                else
                    yield 28;
                }
            default -> {
                    yield -1;
                }
            };
        }
        else {
            return -1;
        }
    }
    
    /* 
    *   computeDayOfWeek returns the day in a week, given day, month and year
    *   Possible returned values are:
    *   0 --> Sunday
    *   1 --> Monday
    *   2 --> Tuesday
    *   3 --> Wednesday
    *   4 --> Thursday
    *   5 --> Friday
    *   6 --> Saturday
    */
    public static int computeDayOfWeek(int day, int month, int year) {
        int k = day;
        int m = (month > 2) ? (month - 2) : (month + 10);
        int C = year/100;
        int Y = (month > 2) ? (year % 100) : (year % 100 - 1);

        int W = (k + (int)(2.6*m - 0.2) - 2*C + Y + (Y/4) + (C/4)) % 7;
        if(W < 0)
            W += 7;

        return W;
    }
    
    // setDefaultDate sets default date for all dates
    public static boolean setDefaultDate(int defaultDay, int defaultMonth, int defaultYear) {
        int maxDay = computeMaxDay(defaultMonth, defaultYear);
        if(maxDay != -1 && defaultDay <= maxDay) {
            msDefaultDay = defaultDay;
            msDefaultMonth = defaultMonth;
            msDefaultYear = defaultYear;
            return true;
        } 
        else {
            return false; 
        }
    }
    
    // setDate sets day, month and year of a date object to given day, month, year
    public boolean setDate(int day, int month, int year) {
        int maxDay = computeMaxDay(month, year);
        if(maxDay != -1 && day <= maxDay) {
            mDay = day;
            mMonth = month;
            mYear = year;
            return true;
        } 
        else {
            return false; 
        }
    }
    
    // getDefaultDay returns default day for all date objects
    public static int getDefaultDay() {
        return msDefaultDay;
    }
    
    // getDefaultMonth returns default month for all date objects
    public static int getDefaultMonth() {
        return msDefaultMonth;
    }
    
    // getDefaultYear returns default year for all date objects
    public static int getDefaultYear() {
        return msDefaultYear;
    }
    
    // getSeparator returns separator for date format (default to '/')
    public static char getSeparator() {
        return mSeparator;
    }
    
    // getDay returns the day of a date object
    public int getDay() {
        return mDay;
    }
    
    // getDay returns the month of a date object
    public int getMonth() {
        return mMonth;
    }
    
    // getDay returns the year of a date object
    public int getYear() {
        return mYear;
    }
    
    // getDayOfWeek returns the day in a week of a date object
    public int getDayOfWeek() {
        return computeDayOfWeek(mDay, mMonth, mYear);
    }
    
    /*
    *   toString returns the string representation of the date object:
    *   Default to: DD/MM/YYYY
    *   Note: only separator '/' can be changed
    */
    @Override
    public String toString() {
        String pattern =  "%02d" + mSeparator + "%02d" + mSeparator + "%d";
        return String.format(pattern, mDay, mMonth, mYear);
    }
    
    /*
    *   compareTo compares this date object with another date object
    *   if this date object is before the other date object, -1 is returned
    *   if this date object is after the other date object, 1 is returned
    *   Otherwise, 0 is returned
    */
    @Override
    public int compareTo(Date other) {
        if(mYear != other.mYear) {
            return (mYear > other.mYear) ? 1 :
                   (mYear < other.mYear) ? -1 : 0;
        }
        else if(mMonth != other.mMonth) {
            return (mMonth > other.mMonth) ? 1 :
                   (mMonth < other.mMonth) ? -1 : 0;
        }
        else {
            return (mDay > other.mDay) ? 1 :
                   (mDay < other.mDay) ? -1 : 0;
        }
    }
    
    /*
    *   equals check whether this date object is equal to another object
    *   If the other object is not a date object, false is returned
    *   If the other object is a date object, this function checks for equality on each field of both objects
    */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) 
            return true;

        if (obj == null || this.getClass() != obj.getClass()) 
            return false;

        Date other = (Date) obj;
        return mDay == other.mDay && mMonth == other.mMonth && mYear == other.mYear;
    }

    // hashCode returns hash code of the date object
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + this.mDay;
        hash = 67 * hash + this.mMonth;
        hash = 67 * hash + this.mYear;
        return hash;
    }
    
    // clone efficiently returns a copy of the date object
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
