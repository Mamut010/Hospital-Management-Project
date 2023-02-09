/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package groupthinhquan.hospitalmanagement.interaction;

import groupthinhquan.hospitalmanagement.core.*;
import java.util.LinkedHashMap;

/**
 *
 * @author Thinh
 */

public class EmployeeDatabase implements MapDatabase<Integer, Employee>, Cloneable {
    private LinkedHashMap<Integer, Doctor> mDoctorMap;
    private LinkedHashMap<Integer, Nurse> mNurseMap;
    
    public EmployeeDatabase() {
        mDoctorMap = new LinkedHashMap<>();
        mNurseMap = new LinkedHashMap<>();
    }
    
    private static boolean isDoctor(Employee employee) {
        return employee != null && employee.getJob() == Job.DOCTOR;
    }
    
    private static boolean isNurse(Employee employee) {
        return employee != null && employee.getJob() == Job.NURSE;
    }
    
    @Override
    public boolean add(Integer ID, Employee employee) {
        if(employee != null && employee.getID() == ID) {
            if(isDoctor(employee) && !mDoctorMap.containsKey(ID)) {
                mDoctorMap.put(ID, (Doctor) employee);
                return true;
            }
            else if (isNurse(employee) && !mNurseMap.containsKey(ID)) {
                mNurseMap.put(ID, (Nurse) employee);
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }
    
    @Override
    public int remove(Integer ID) {
        if(mDoctorMap.containsKey(ID)) {
            mDoctorMap.remove(ID);
            return 1;
        }
        else if(mNurseMap.containsKey(ID)) {
            mNurseMap.remove(ID);
            return 1;
        }
        else {
            return 0;
        }
    }
    
    @Override
    public boolean has(Integer ID) {
        return mDoctorMap.containsKey(ID) || mNurseMap.containsKey(ID);
    }
    
    public boolean hasDoctor(Integer doctorID) {
        return mDoctorMap.containsKey(doctorID);
    }
    
    public boolean hasNurse(Integer nurseID) {
        return mNurseMap.containsKey(nurseID);
    }
    
    @Override
    public Employee get(Integer ID) {
        Doctor doctor = mDoctorMap.get(ID);
        Nurse nurse = mNurseMap.get(ID);
        if(doctor != null) {
            return doctor;
        }
        else {
            return nurse;
        }
    }
    
    public Doctor getDoctor(Integer doctorID) {
        return mDoctorMap.get(doctorID);
    }
    
    public Doctor[] getDoctors() {
        return mDoctorMap.values().toArray(Doctor[]::new);
    }
    
    public Nurse getNurse(Integer nurseID) {
        return mNurseMap.get(nurseID);
    }
    
    public Nurse[] getNurses() {
        return mNurseMap.values().toArray(Nurse[]::new);
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        EmployeeDatabase newClone = (EmployeeDatabase) super.clone();
        /* Make a deep copy for each map */
        // Doctor Map
        newClone.mDoctorMap = new LinkedHashMap<>(mDoctorMap.size());
        mDoctorMap.entrySet().forEach(entry -> {
            newClone.mDoctorMap.put(entry.getKey(), new Doctor(entry.getValue()));
        });
        
        // Nurse Map
        newClone.mNurseMap = new LinkedHashMap<>(mNurseMap.size());
        mNurseMap.entrySet().forEach(entry -> {
            newClone.mNurseMap.put(entry.getKey(), new Nurse(entry.getValue()));
        });
        
        return newClone;
    }
}
