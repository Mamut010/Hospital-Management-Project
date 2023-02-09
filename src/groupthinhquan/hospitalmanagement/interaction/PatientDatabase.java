/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package groupthinhquan.hospitalmanagement.interaction;

import groupthinhquan.hospitalmanagement.core.Patient;
import java.util.LinkedHashMap;

/**
 *
 * @author Thinh
 */

public class PatientDatabase implements MapDatabase<Integer, Patient>, Cloneable {
    private LinkedHashMap<Integer, Patient> mData;
    
    public PatientDatabase() {
        mData = new LinkedHashMap<>();
    }
    
    @Override
    public boolean add(Integer ID, Patient patient) {
        if(patient != null && patient.getID() == ID && !mData.containsKey(ID)) {
            mData.put(patient.getID(), patient);
            return true;
        }
        else {
            return false;
        }
    }
    
    @Override
    public int remove(Integer ID) {
        if(mData.containsKey(ID)) {
            mData.remove(ID);
            return 1;
        }
        else {
            return 0;
        }
    }
    
    @Override
    public boolean has(Integer ID) {
        return mData.get(ID) != null;
    }
    
    @Override
    public Patient get(Integer ID) {
        return mData.get(ID);
    }
    
    public Patient[] getPatients() {
        return mData.values().toArray(Patient[]::new);
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        PatientDatabase newClone = (PatientDatabase) super.clone();
        // Make a deep copy for the map
        newClone.mData = new LinkedHashMap<>(mData.size());
        mData.entrySet().forEach(entry -> {
            newClone.mData.put(entry.getKey(), new Patient(entry.getValue()));
        });
        return newClone;
    }
}
