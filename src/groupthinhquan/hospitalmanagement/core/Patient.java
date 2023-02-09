/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package groupthinhquan.hospitalmanagement.core;

/**
 *
 * @author Thinh
 */

public class Patient extends Person {
    private String mStatus;
    
    public Patient(int ID) {
        super(ID);
        mStatus = "";
    }
    
    public Patient (int ID, String name, int age, Sex sex, String address, String phoneNum) {
        super(ID, name, age, sex, address, phoneNum);
        mStatus = "";
    }
    
    public Patient (int ID, String name, int age, Sex sex, String address, String phoneNum, String status) {
        super(ID, name, age, sex, address, phoneNum);
        mStatus = status;
    }
    
    public Patient(Patient other) {
        super(other);
        mStatus = other.mStatus;
    }
    
    public void setStatus(String status) {
        mStatus = status;
    }
    
    public String getStatus() {
        return mStatus;
    }
    
    @Override
    public Job getJob() {
        return Job.PATIENT;
    }
}
