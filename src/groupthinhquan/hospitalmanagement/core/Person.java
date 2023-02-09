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

public abstract class Person {
    private final int mID;
    protected String mName;
    protected int mAge;
    protected Sex mSex;
    protected String mAddress;
    protected String mPhoneNum;
    
    public Person(int ID) {
        mID = (ID >= 0) ? ID : 0;
        mName = "";
        mAge = 0;
        mSex = Sex.UNKNOWN;
        mAddress = "";
        mPhoneNum = "";
    }
    
    public Person(int ID, String name, int age, Sex sex, String address, String phoneNum) {
        mID = (ID >= 0) ? ID : 0;
        mName = name;
        mAge = (age >= 0 && age <= 200) ? age : 0;
        mSex = sex;
        mAddress = address;
        mPhoneNum = phoneNum;
    }
    
    public Person(Person other) {
        mID = other.mID;
        mName = other.mName;
        mAge = other.mAge;
        mSex = other.mSex;
        mAddress = other.mAddress;
        mPhoneNum = other.mPhoneNum;
    }
    
    public void setName(String name) {
        mName = name;
    }
    
    public void setAge(int age) {
        if(age >= 0 && age <= 200)
            mAge = age;
    }
    
    public void setSex(Sex sex) {
        mSex = sex;
    }
    
    public void setAddress(String address) {
        mAddress = address;
    }
    
    public void setPhoneNum(String phoneNum) {
        mPhoneNum = phoneNum;
    }
    
    public int getID() {
        return mID;
    }
    
    public String getName() {
        return mName;
    }
    
    public int getAge() {
        return mAge;
    }
    
    public Sex getSex() {
        return mSex;
    }
    
    public String getAddress() {
        return mAddress;
    }
    
    public String getPhoneNum() {
        return mPhoneNum;
    }
    
    public abstract Job getJob();
}
