/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package groupthinhquan.hospitalmanagement.core;

import java.util.ArrayList;

/**
 *
 * @author Thinh
 */

public class Doctor extends Employee {
    private final ArrayList<String> mMajorList;
    
    public Doctor(int ID){
        super(ID);
        mMajorList = new ArrayList<>();
    }
    
    public Doctor(int ID, String name, int age, Sex sex, String address, String phoneNum,
                double salary, String... majorList) {
        super(ID, name, age, sex, address, phoneNum, salary);
        mMajorList = new ArrayList<>();
        addMajor(majorList);
    }
    
    public Doctor(int ID, String name, int age, Sex sex, String address, String phoneNum,
                int workingYear, double salary, String... majorList) {
        super(ID, name, age, sex, address, phoneNum, workingYear, salary);
        mMajorList = new ArrayList<>();
        addMajor(majorList);
    }
    
    public Doctor(Doctor other) {
        super(other);
        mMajorList = (ArrayList<String>) other.mMajorList.clone();
    }
    
    public final void addMajor(String... majorList) {
        // For each major in the major list
        for(String major : majorList) {
            // If doctor's major list does not contain this major
            if(!mMajorList.contains(major))
                // Add it to doctor's major list
                mMajorList.add(major);
        }
    }
    
    public boolean removeMajor(String major) {
        return mMajorList.remove(major);
    }
    
    public void removeAllMajors() {
        mMajorList.clear();
    }
    
    public boolean hasMajor(String major) {
        return mMajorList.contains(major);
    }
    
    public String[] getMajors() {
        return mMajorList.toArray(String[]::new);
    }
    
    @Override
    public Job getJob() {
        return Job.DOCTOR;
    }
}
