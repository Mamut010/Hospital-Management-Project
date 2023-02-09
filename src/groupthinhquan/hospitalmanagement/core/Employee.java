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

public abstract class Employee extends Person {
    protected int mWorkingYear;
    protected double mSalary;
    
    public Employee(int ID) {
        super(ID);
        mWorkingYear = 0;
        mSalary = 0;
    }
    
    public Employee(int ID, String name, int age, Sex sex, String address, String phoneNum, 
                double salary) {
        super(ID, name, age, sex, address, phoneNum);
        mWorkingYear = 0;
        mSalary = (salary >= 0) ? salary : 0;
    }
    
    public Employee(int ID, String name, int age, Sex sex, String address, String phoneNum, 
                int workingYear, double salary) {
        super(ID, name, age, sex, address, phoneNum);
        mWorkingYear = (workingYear >= 0 && workingYear <= 60) ? workingYear : 0;
        mSalary = (salary >= 0) ? salary : 0;
    }
    
    public Employee(Employee other) {
        super(other);
        mWorkingYear = other.mWorkingYear;
        mSalary = other.mSalary;
    }
    
    public void setWorkingYear(int workingYear) {
        if(workingYear >= 0 && workingYear <= 60)
            mWorkingYear = workingYear;
    }
    
    public void setSalary(double salary) {
        if(salary >= 0)
            mSalary = salary;
    }
    
    public int getWorkingYear() {
        return mWorkingYear;
    }
    
    public double getSalary() {
        return mSalary;
    }
}
