/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package groupthinhquan.hospitalmanagement.core;

import java.util.Objects;
import java.util.UnknownFormatConversionException;

/**
 *
 * @author Thinh
 */

public final class WorkingHour implements Cloneable {
    private Time mStartTime;
    private Time mEndTime;
    
    public WorkingHour() {
        mStartTime = new Time();
        mEndTime = new Time();
    }

    public WorkingHour(Time startTime, Time endTime) {
        if(startTime.compareTo(endTime) <= 0) {
            mStartTime = startTime;
            mEndTime = endTime;
        }
        else {
            mStartTime = new Time();
            mEndTime = new Time();
        }
    }
    
    public boolean setStartTime(Time startTime) {
        if(startTime.compareTo(mEndTime) <= 0) {
            mStartTime = startTime;
            return true;
        }
        else {
            return false;
        }
    }
    
    public boolean setEndTime(Time endTime) {
        if(endTime.compareTo(mStartTime) >= 0) {
            mEndTime = endTime;
            return true;
        }
        else {
            return false;
        }
    }
    
    public boolean resetTime() {
        mStartTime.reset();
        mEndTime.reset();
        return true;
    }
    
    public boolean resetTime(Time startTime, Time endTime) {
        if(startTime.compareTo(endTime) <= 0) {
            mStartTime = startTime;
            mEndTime = endTime;
            return true;
        }
        else {
            return false;
        }
    }
    
    public Time getStartTime() {
        return mStartTime;
    }
    
    public Time getEndTime() {
        return mEndTime;
    }
    
    // utility method to check if a time point is in between mStartTime and mEndTime
    public boolean includesTimePoint(Time timePoint) {
        return (mStartTime.compareTo(timePoint) <= 0) && (mEndTime.compareTo(timePoint) >= 0);
    }
    
    // utility method to check if two working hours overlap
    public boolean overlaps(WorkingHour other) {
        if(other == null)
            return false;
        
        if(mStartTime.compareTo(other.mStartTime) < 0) {
            return mEndTime.compareTo(other.mStartTime) > 0;
        }
        else {
            return other.mEndTime.compareTo(mStartTime) > 0;
        }
    }
    
    public static WorkingHour parseWorkingHour(String workingHourString) throws UnknownFormatConversionException {
        String[] workingHourTokens = workingHourString.trim().split("-");
        if(workingHourTokens.length != 2)
            throw new UnknownFormatConversionException(workingHourString);
        
        Time startTime, endTime;
                
        try {
            startTime = Time.parseTime(workingHourTokens[0]);
            endTime = Time.parseTime(workingHourTokens[1]);
        }
        catch(NumberFormatException e) {
            throw new UnknownFormatConversionException(workingHourString);
        }

        return new WorkingHour(startTime, endTime);
    }
    
    @Override
    public String toString() {
        return mStartTime.toString() + " - " + mEndTime.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) 
            return true;

        if (obj == null || this.getClass() != obj.getClass()) 
            return false;

        WorkingHour other = (WorkingHour) obj;
        return mStartTime.compareTo(other.mStartTime) == 0 &&
               mEndTime.compareTo(other.mEndTime) == 0;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.mStartTime);
        hash = 97 * hash + Objects.hashCode(this.mEndTime);
        return hash;
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        WorkingHour newClone = (WorkingHour) super.clone();
        newClone.mStartTime = (Time) mStartTime.clone();
        newClone.mEndTime = (Time) mEndTime.clone();
        return newClone;
    }
}
