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

public final class Time implements Comparable<Time>, Cloneable {
    public static final int MAX_HOUR = 23;
    public static final int MAX_MIN = 59;
    private int mHour;
    private int mMin;

    public Time() {
        reset();
    }

    public Time(int hour, int min) {
        reset(hour, min);
    }
    
    public Time(Time other) {
        mHour = other.mHour;
        mMin = other.mMin;
    }
    
    public void reset() {
        mHour = 0;
        mMin = 0;
    }
    
    public void reset(int hour, int min) {
        if(hour < 0 && min < 0) {
            mHour = 0;
            mMin = 0;
        }
        else {
            mHour = (hour + min/60) % 24;
            mMin = min % 60;
        }
    }

    public boolean setHour(int hour) {
        if(hour >= 1) {
            mHour = hour % 24;
            return true;
        }
        else {
            return false;
        }
    }
    
    public boolean setMin(int min) {
        if(min >= 1) {
            mMin = min % 60;
            mHour = (mHour + min/60) % 24;
            return true;
        }
        else {
            return false;
        }
    }
    
    public int getHour() {
        return mHour;
    }
    
    public int getMin() {
        return mMin;
    }
    
    public Time addTime(Time t) {
        int totalMin = mMin + t.mMin;
        int resultMin = totalMin % 60;
        int resultHour = (t.mHour + mHour + totalMin/60) % 24;
        return new Time(resultHour, resultMin);
    }
    
    public Time subtractTime(Time t) {
        int diffMin = mMin - t.mMin;
        int overflow = (diffMin < 0) ? 1 : 0;
        if(diffMin < 0)
            diffMin += 60;
        
        int resultHour = mHour - t.mHour - overflow;
        return new Time((resultHour < 0) ? resultHour + 24 : resultHour, diffMin);
    }
    
    public static Time parseTime(String timeString) throws UnknownFormatConversionException {
        String[] timeTokens = timeString.trim().split(":");
        if(timeTokens.length != 2)
            throw new UnknownFormatConversionException(timeString);
        
        int hour, min;
        
        try {
            hour = Integer.parseInt(timeTokens[0]);
            min = Integer.parseInt(timeTokens[1]);
        }
        catch(NumberFormatException e) {
            throw new UnknownFormatConversionException(timeString);
        }

        return new Time(hour, min);
    }
    
    @Override
    public String toString() {
        return String.format("%02d:%02d", mHour, mMin);
    }
    
    @Override
    public int compareTo(Time other) {
        if(mHour == other.mHour) {
            return (mMin > other.mMin) ? 1 :
                   (mMin < other.mMin) ? -1 : 0;
        }
        else {
            return (mHour > other.mHour) ? 1 : -1;
        }
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;
        
        if(obj == null || this.getClass() != obj.getClass())
            return false;
        
        Time other = (Time) obj;
        return mHour == other.mHour && mMin == other.mMin;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + this.mHour;
        hash = 53 * hash + this.mMin;
        return hash;
    }
}
