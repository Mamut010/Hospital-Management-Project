/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package groupthinhquan.hospitalmanagement.interaction;

import groupthinhquan.hospitalmanagement.core.Date;
import groupthinhquan.hospitalmanagement.core.Time;
import java.util.Objects;

/**
 *
 * @author Thinh
 */

public class Appointment {
    private final String mID;
    protected Date mDate;
    protected Time mTime;
    protected String mDetail;
    
    public Appointment(String ID) {
        mID = (ID != null) ? ID : "";
        mDate = new Date();
        mTime = new Time();
        mDetail = "";
    }
    
    public Appointment(String ID, Date date, Time time) {
        mID = (ID != null) ? ID : "";
        mDate = date;
        mTime = time;
        mDetail = "";
    }
    
    public Appointment(String ID, Date date, Time time, String detail) {
        mID = (ID != null) ? ID : "";
        mDate = date;
        mTime = time;
        mDetail = detail;
    }
    
    public Appointment(Appointment other) throws CloneNotSupportedException {
        mID = other.mID;
        mDate = (Date) other.mDate.clone();
        mTime = (Time) other.mTime.clone();
        mDetail = other.mDetail;
    }
    
    public void setDate(Date date) {
        mDate = date;
    }
    
    public void setTime(Time time) {
        mTime = time;
    }
    
    public void setDetail(String detail) {
        mDetail = detail;
    }
    
    public String getID() {
        return mID;
    }
    
    public Date getDate() {
        return mDate;
    }
    
    public Time getTime() {
        return mTime;
    }
    
    public String getDetail() {
        return mDetail;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;
        
        if(obj == null || this.getClass() != obj.getClass())
            return false;
        
        Appointment other = (Appointment) obj;
        return mID.equals(other.mID);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 11 * hash + Objects.hashCode(this.mID);
        return hash;
    }
}
