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

public class Nurse extends Employee {
    private String mPosition;
    
    public Nurse(int ID) {
        super(ID);
        mPosition = "";
    }
    
    public Nurse(int ID, String name, int age, Sex sex, String address, String phoneNum, 
                double salary, String position)  {
        super(ID, name, age, sex, address, phoneNum, salary);
        mPosition = position;
    }
    
    public Nurse(int ID, String name, int age, Sex sex,  String address, String phoneNum, 
                int workingYear, double salary, String position)  {
        super(ID, name, age, sex, address, phoneNum, workingYear, salary);
        mPosition = position;
    }
    
    public Nurse(Nurse other) {
        super(other);
        mPosition = other.mPosition;
    }
    
    public void setPosition(String position) {
        mPosition = position;
    }
    
    public String getPosition() {
        return mPosition;
    }
    
    @Override
    public Job getJob() {
        return Job.NURSE;
    }
}
