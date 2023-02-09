/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package groupthinhquan.hospitalmanagement.interaction;

import java.util.HashMap;

/**
 *
 * @author Thinh
 */

public final class ScheduleSystem implements Cloneable {
    private HashMap<Integer, IndividualSchedule> mData;
    
    public ScheduleSystem() {
        mData = new HashMap<>();
    }
    
    public boolean createSchedule(int ID) {
        if(!mData.containsKey(ID)) {
            mData.put(ID, new IndividualSchedule());
            return true;
        }
        else {
            return false;
        }
    }
    
    public boolean removeSchedule(int ID) {
        if(mData.containsKey(ID)) {
            mData.remove(ID);
            return true;
        }
        else {
            return false;
        }
    }
    
    public boolean hasSchedule(int ID) {
        return mData.containsKey(ID);
    }
    
    public IndividualSchedule accessSchedule(int ID) {
        return mData.get(ID);
    }
    
    public Integer[] getIDs() {
        return mData.keySet().toArray(Integer[]::new);
    }
    
    public IndividualSchedule[] getSchedules() {
        return mData.values().toArray(IndividualSchedule[]::new);
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        ScheduleSystem newClone = (ScheduleSystem) super.clone();
        newClone.mData = new HashMap<>(mData.size());
        mData.entrySet().forEach(entry -> {
            newClone.mData.put(entry.getKey(), new IndividualSchedule(entry.getValue()));
        });
        
        return newClone;
    }
}
