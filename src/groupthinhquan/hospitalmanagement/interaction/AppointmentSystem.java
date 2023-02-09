/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package groupthinhquan.hospitalmanagement.interaction;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

/**
 *
 * @author Thinh
 */

public final class AppointmentSystem implements Cloneable {
    public static class AppointmentInfo {
        private final Appointment appointment;
        private int doctorID;
        private int patientID;
        
        private AppointmentInfo(Appointment appointment, int doctor, int patient) {
            this.appointment = appointment;
            doctorID = doctor;
            patientID = patient;
        }
        
        public void setDoctorID(int doctorID) {
            this.doctorID = doctorID;
        }
        
        public void setPatientID(int doctorID) {
            this.patientID = doctorID;
        }
        
        public Appointment getAppointment() {
            return appointment;
        }
        
        public int getDoctorID() {
            return doctorID;
        }
        
        public int getPatientID() {
            return patientID;
        }
    }
    
    // Map to store all apppointments with their info, using appointment ID as key
    private LinkedHashMap<String, AppointmentInfo> mAppointmentMap;
    // Map to store all appointments exclusively for doctors, using doctor ID as key
    private HashMap<Integer, TreeSet<AppointmentInfo>> mDoctorAppointmentMap;
    // Map to store all appointments exclusively for patients, using patient ID as key
    private HashMap<Integer, TreeSet<AppointmentInfo>> mPatientAppointmentMap;
    
    public AppointmentSystem() {
        mAppointmentMap = new LinkedHashMap<>();
        mDoctorAppointmentMap = new HashMap<>();
        mPatientAppointmentMap = new HashMap<>();
    }
    
    // When adding an appointment, this appointment will be put into mApppointmentMap 
    //                              with both the doctorID and patientID
    // AND put into mDoctorAppointmentMap with the patient info
    // AND put into mPatientAppointmentMap with the doctor info
    public boolean addAppointment(Appointment appointment, int doctorID, int patientID) {
        if(!mAppointmentMap.containsKey(appointment.getID())) {
            AppointmentInfo newEntry = new AppointmentInfo(appointment, doctorID, patientID);
            
            mAppointmentMap.put(appointment.getID(), newEntry);
            
            TreeSet<AppointmentInfo> DoctorAppointmentSet = mDoctorAppointmentMap.get(doctorID);
            if(DoctorAppointmentSet == null) {
                DoctorAppointmentSet = new TreeSet<>(new AppointmentInfoComparator());
                DoctorAppointmentSet.add(newEntry);
                mDoctorAppointmentMap.put(doctorID, DoctorAppointmentSet);
            }
            else {
                DoctorAppointmentSet.add(newEntry);
            }
            
            TreeSet<AppointmentInfo> PatientAppointmentSet = mPatientAppointmentMap.get(patientID);
            if(PatientAppointmentSet == null) {
                PatientAppointmentSet = new TreeSet<>(new AppointmentInfoComparator());
                PatientAppointmentSet.add(newEntry);
                mPatientAppointmentMap.put(patientID, PatientAppointmentSet);
            }
            else {
                PatientAppointmentSet.add(newEntry);
            }

            return true;
        }
        else {
            return false;
        }
    }
    
    // When removing an appointment, remove the corresponding entries in all 3 maps
    public boolean removeAppointment(String appointmentID) {
        AppointmentInfo info = mAppointmentMap.get(appointmentID);
        if(info != null) {
            int doctorID = info.getDoctorID();
            int patientID = info.getPatientID();
            mAppointmentMap.remove(appointmentID);
            
            TreeSet DoctorAppointmentSet = mDoctorAppointmentMap.get(doctorID);
            DoctorAppointmentSet.remove(info);
            if(DoctorAppointmentSet.isEmpty())
                mDoctorAppointmentMap.remove(doctorID);
            
            TreeSet PatientAppointmentSet = mPatientAppointmentMap.get(patientID);
            PatientAppointmentSet.remove(info);
            if(PatientAppointmentSet.isEmpty())
                mPatientAppointmentMap.remove(patientID);
                
            return true;
        }
        else {
            return false;
        }
    }
    
    public boolean removeAppointment(int doctorID, int patientID) {
        Iterator<Map.Entry<String, AppointmentInfo>> iter = mAppointmentMap.entrySet().iterator();
        while(iter.hasNext()) {
            AppointmentInfo info = iter.next().getValue();
            if(info.getDoctorID() == doctorID && info.getPatientID() == patientID) {
                TreeSet DoctorAppointmentSet = mDoctorAppointmentMap.get(doctorID);
                DoctorAppointmentSet.remove(info);
                if(DoctorAppointmentSet.isEmpty())
                    mDoctorAppointmentMap.remove(doctorID);

                TreeSet PatientAppointmentSet = mPatientAppointmentMap.get(patientID);
                PatientAppointmentSet.remove(info);
                if(PatientAppointmentSet.isEmpty())
                    mPatientAppointmentMap.remove(patientID);
                
                iter.remove();

                return true;
            }
        }
        
        return false;
    }
    
    public boolean hasAppointment(String appointmentID) {
        return mAppointmentMap.containsKey(appointmentID);
    }
    
    public boolean hasAppointment(int doctorID, int patientID) {
        return getAppointment(doctorID, patientID) != null;
    }
    
    public AppointmentInfo getAppointment(String appointmentID) {
        return mAppointmentMap.get(appointmentID);
    }
    
    public AppointmentInfo getAppointment(int doctorID, int patientID) {
        // Get appointment set of the given doctor and patient
        TreeSet<AppointmentInfo> DoctorAppointmentSet = mDoctorAppointmentMap.get(doctorID);
        TreeSet<AppointmentInfo> PatientAppointmentSet = mPatientAppointmentMap.get(patientID);
        
        // If both set are not empty
        if(DoctorAppointmentSet != null && PatientAppointmentSet != null) {
            TreeSet<AppointmentInfo> smallerSet;
            TreeSet<AppointmentInfo> biggerSet;
            
            // Find the smaller set and assign it to smallerSet, the leftover set to biggerSet
            if(DoctorAppointmentSet.size() < PatientAppointmentSet.size()) {
                smallerSet = DoctorAppointmentSet;
                biggerSet = PatientAppointmentSet;
            }
            else {
                smallerSet = PatientAppointmentSet;
                biggerSet = DoctorAppointmentSet;
            }
            
            for(var element : smallerSet) {
                if(biggerSet.contains(element))
                    return element;
            }
        }
        
        return null;
    }
    
    public AppointmentInfo[] getAppointments() {
        return mAppointmentMap.values().toArray(AppointmentInfo[]::new);
    }
    
    public AppointmentInfo[] getDoctorAppointments(int doctorID) {
        TreeSet DoctorAppointmentSet = mDoctorAppointmentMap.get(doctorID);
        if(DoctorAppointmentSet != null) {
            return (AppointmentInfo[]) DoctorAppointmentSet.toArray(AppointmentInfo[]::new);
        }
        else {
            return new AppointmentInfo[0];
        }
    }
    
    public AppointmentInfo[] getPatientAppointments(int patientID) {
        TreeSet PatientAppointmentSet = mPatientAppointmentMap.get(patientID);
        if(PatientAppointmentSet != null) {
            return (AppointmentInfo[]) PatientAppointmentSet.toArray(AppointmentInfo[]::new);
        }
        else {
            return new AppointmentInfo[0];
        }
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        AppointmentSystem newClone = (AppointmentSystem) super.clone();
        /* Make a deep copy for all map */
        newClone.mAppointmentMap = new LinkedHashMap<>(mAppointmentMap.size());
        newClone.mDoctorAppointmentMap = new HashMap<>(mDoctorAppointmentMap.size());
        newClone.mPatientAppointmentMap = new HashMap<>(mPatientAppointmentMap.size());
        
        mAppointmentMap.entrySet().stream()
                                  .map(entry -> entry.getValue())
                                  .forEach(e -> {
                                    try {
                                        Appointment appointment = new Appointment(e.getAppointment());
                                        int doctorID = e.getDoctorID();
                                        int patientID = e.getPatientID();
                                        newClone.addAppointment(appointment, doctorID, patientID);
                                    }
                                    catch(CloneNotSupportedException err) {
                                        System.err.println("An error occured when making a deep copy "
                                            + "of an Appointment object");
                                    }
                                  });
        
        return newClone;
    }
}

class AppointmentInfoComparator implements Comparator<AppointmentSystem.AppointmentInfo> {
    @Override
    public int compare(AppointmentSystem.AppointmentInfo obj1, AppointmentSystem.AppointmentInfo obj2) {
        Appointment a1 = obj1.getAppointment();
        Appointment a2 = obj2.getAppointment();
        
        // Apply equals() semantic of class Appointment
        if(a1.equals(a2))
            return 0;
        
        // Otherwise, compare the two appointments using their dates, or theirs time if the dates are the same
        int dateCompare = a1.getDate().compareTo(a2.getDate());
        if(dateCompare == 0) {
            return a1.getTime().compareTo(a2.getTime()) < 0 ? -1 : 1;
        }
        else {
            return dateCompare;
        }
    }
}
