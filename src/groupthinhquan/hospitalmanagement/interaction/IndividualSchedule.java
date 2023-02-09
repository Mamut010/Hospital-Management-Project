/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package groupthinhquan.hospitalmanagement.interaction;

import groupthinhquan.hospitalmanagement.core.Date;
import groupthinhquan.hospitalmanagement.core.Time;
import groupthinhquan.hospitalmanagement.core.WorkingHour;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 *
 * @author Thinh
 */

public class IndividualSchedule {
    protected final TreeMap<Date, TreeSet<WorkingHour>> mData;
    
    public IndividualSchedule() {
        mData = new TreeMap<>((d1, d2) -> d1.compareTo(d2));
    }
    
    public IndividualSchedule(IndividualSchedule other) {
        mData = new TreeMap<>((d1, d2) -> d1.compareTo(d2));
        
        // Make a deep copy of the data
        other.mData.entrySet().forEach(entry -> {
            try {
                Date date = (Date) entry.getKey().clone();
                TreeSet<WorkingHour> copyWHSet = new TreeSet<>(new WorkingHourComparator());
                entry.getValue().forEach(WH -> {
                    try {
                        copyWHSet.add((WorkingHour) WH.clone());  
                    }
                    catch(CloneNotSupportedException e) {
                        System.err.println("An error occured when making a deep copy of "
                            + "an IndividualSchedule object");
                    }
                });
                mData.put(date, copyWHSet);
            }
            catch(CloneNotSupportedException e) {
                System.err.println("An error occured when making a deep copy of "
                    + "an IndividualSchedule object");
            }
        });
    }
    
    public boolean addEntry(Date date, WorkingHour workingHour) {
        // Get the set corresponding to the date in the map
        TreeSet<WorkingHour> workingHourSet = mData.get(date);
        
        // If there is no corresponding set of the date in the map
        if(workingHourSet == null) {
            // Create a new set
            workingHourSet = new TreeSet<>(new WorkingHourComparator());
            // Add the input working hour to the set
            workingHourSet.add(workingHour);
            // Put the set to the map and it corresponding date
            mData.put(date, workingHourSet);
            return true;
        }
        // Otherwise
        else {
            // Try to add the input working hour to the set and return the respective result
            return workingHourSet.add(workingHour);
        }
    }
    
    public boolean removeEntry(Date date) {
        return mData.remove(date) != null;
    }
    
    public boolean removeEntry(Date date, WorkingHour workingHour) {
        // Get the set corresponding to the date in the map
        TreeSet workingHourSet = mData.get(date);
        
        // If there exists such a set
        if(workingHourSet != null) {
            // If succeeded in removing the desired working hour
            if(workingHourSet.remove(workingHour)) {
                // If after removing, the set is empty
                if(workingHourSet.isEmpty()) {
                    // Remove the set
                    mData.remove(date);
                    return true;
                }
                else {
                    // Otherwise, just return true
                    return true;
                }
            }
        }
        
        // We reach here when nothing was deleted
        return false;
    }
    
    public boolean modifyDate(WorkingHour workingHour, Date oldDate, Date newDate) {
        // Get the set corresponding to the old date in the map
        TreeSet workingHourSet = mData.get(oldDate);
        
        // If there exists such a set
        if(workingHourSet != null) {
            // If this set contains the input working hour
            if(workingHourSet.contains(workingHour)) {
                // Remove the old entry
                workingHourSet.remove(workingHour);
                
                // If after removing the old entry, the set is empty
                if(workingHourSet.isEmpty())
                    // Remove the set from the map
                    mData.remove(oldDate);
                
                // Add a new entry to the map with the new date and input working hour
                addEntry(newDate, workingHour);
                return true;
            }
        }
        
        // We reach here if nothing was modified
        return false;
    }

    public boolean modifyWorkingHour(Date date, WorkingHour oldWH, WorkingHour newWH) {
        // Get the set corresponding to the date in the map
        TreeSet workingHourSet = mData.get(date);
        
        // If there exists such a set
        if(workingHourSet != null) {
            // If this set contains the working hour to be modified
            if(workingHourSet.contains(oldWH)) {
                // Remove this working hour from the set
                workingHourSet.remove(oldWH);
                // Add the new working hour to the set
                workingHourSet.add(newWH);
                return true;
            }
        }
        
        // We reach here if nothing was modified
        return false;
    }
    
    public boolean containsDate(Date date) {
        return mData.containsKey(date);
    }
    
    public boolean containsWorkingHour(Date date, WorkingHour workingHour) {
        TreeSet workingHourSet = mData.get(date);
        if(workingHourSet != null) {
            return workingHourSet.contains(workingHour);
        }
        else {
            return false;
        }
    }
    
    public Date[] getDates() {
        var dateSet = mData.keySet();
        return (!dateSet.isEmpty()) ? dateSet.toArray(Date[]::new) : new Date[0];
    }
    
    public WorkingHour[] getWorkingHours(Date date) {
        if(date != null) {
            var workingHourSet = mData.get(date);
            if(workingHourSet != null) {
                return workingHourSet.toArray(WorkingHour[]::new);
            }
        }
        return new WorkingHour[0];
    }
    
    public WorkingHour[] locateTimePoint(Date date, Time timePoint) {
        // Get the set corresponding to the date in the map
        if(date != null) {
            TreeSet<WorkingHour> workingHourSet = mData.get(date);
        
            // If there exists such a set
            if(workingHourSet != null) {
                                     // Turn the set into a parallel stream
                return workingHourSet.parallelStream() 
                                     // Filter the stream, keeping only WHs that include the given time point 
                                     .filter(WH -> WH.includesTimePoint(timePoint)) 
                                     // Return the array of elements after filtering
                                     .toArray(WorkingHour[]::new);
            }
        }
        
        // Otherwise
        // Return an empty array
        return new WorkingHour[0];
    }
    
    public boolean checkTimePoint(Date date, Time timePoint) {
        return locateTimePoint(date, timePoint).length != 0;
    }
}

class WorkingHourComparator implements Comparator<WorkingHour> {
    @Override
    public int compare(WorkingHour obj1, WorkingHour obj2) {
        int startTimeCompare = obj1.getStartTime().compareTo(obj2.getStartTime());
        return (startTimeCompare != 0) ? startTimeCompare : 
                                         obj1.getEndTime().compareTo(obj2.getEndTime());
    }
}
