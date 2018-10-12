/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Support;

import Support.Schedule.Semester;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.stream.Collectors;

/**
 * Name: ScheduleList 
 * Purpose: Maintains schedule data structure and provides schedule data retrieval and manipulation services to all subsystems
 * Note: First schedule should be created using createInitialSchedule(semester, year)
 *      subsequence schedule can be created using createNextSchedule()
 * Singleton class
 */
public final class ScheduleList extends Observable{
    private volatile static ScheduleList instance = null;
    private static Map<String,Schedule> schedules = new HashMap<>();        //Collection of semester schedules
    private static Map<String,Boolean> checkFlags_Map = new HashMap<>();    // Colllection of flags for schedule consistency check
    private static Schedule currentSchedule;                                //Current schedule
    private static Schedule lastSchedule;                                   //Last schedule added (since user only add course to this schedule for now)
    
    private ScheduleList(){
        Logic.courses.values().forEach(course -> checkFlags_Map.put(course.name(), Boolean.FALSE));  //Initialize CHECKFLAGS
    }
    
    public static ScheduleList getInstance(){
        if(instance==null)
            instance = new ScheduleList();
        return instance;
    }
    
    /**
     * Create initial schedule
     * @param semester
     * @param year
     * @return schedule instance
     */
    public Schedule createInitialSchedule(Semester semester, int year){
        if((year<2018)||(year>2050))        //Year validation
            year = Calendar.getInstance().get(Calendar.YEAR);
                    
        if(schedules.isEmpty()){
            Schedule newSchedule = new Schedule(semester, year);
            schedules.put(newSchedule.toString(),newSchedule);
            currentSchedule = newSchedule;
            lastSchedule = newSchedule;
                        
            setChanged();
            notifyObservers();            
            return newSchedule;
        }else{
            //if user calls to create initial schedule again, create next schedule instead
            return createNextSchedule();
        }
    }
    
    /**
     * Create next schedule
     * @return schedule instance
     */
    public Schedule createNextSchedule(){
        if(schedules.isEmpty())     //Check if it is the first schedule
            return null;
        
        scheduleConsistencyCheckAndFix();
        
        Schedule newSchedule = Schedule.next();
        schedules.put(newSchedule.toString(),newSchedule);
        currentSchedule = newSchedule; 
        lastSchedule = newSchedule;
                
        setChanged();
        notifyObservers();        
        return newSchedule;
    }
    
    /**
     * Clear schedule (clear courses if exist in this schedule)
     * @param schedule
     * @return True: Success; False otherwise
     */
    public boolean clearSchedule(Schedule schedule){
        if(schedules.containsValue(schedule)){
            if(!schedule.coursesInSchedule().isEmpty()){
                schedule.removeAllCourses();                                
                scheduleConsistencyCheckAndFix(); 
                return true;
            }
            return false;
        }else{
            return false;
        }
    }    

    /**
     * Get Schedule List
     * @return List of Schedule
     */
    public List<Schedule> getSchedulesList(){
        return schedules.values().parallelStream().sorted().collect(Collectors.toList());
    }
    
    /**
     * Get Schedule Map (Used by Data class)
     * @return 
     */
    protected Map<String,Schedule> getSchedules_Map(){
        if (schedules.isEmpty()) {
            createInitialSchedule(Semester.SPRING, Calendar.getInstance().get(Calendar.YEAR));
        }
        return schedules;
    }

    /**
     * Set schedule Map (Used by Data class)
     * @param newSchedulesMap 
     */
    protected void setSchedules_Map(Map<String,Schedule> newSchedulesMap){        
        schedules.values().forEach(this::clearSchedule);
        schedules.clear();        
        schedules.putAll(newSchedulesMap);
        
        //Find the last schedule in this data and set it as last and current
        int fIndex = schedules.size()-1;
        Schedule gSchedule = schedules.values().stream().sorted().collect(Collectors.toList()).get(fIndex);
        currentSchedule = gSchedule;
        lastSchedule = gSchedule;
        lastSchedule.addObserver(LogicController.getInstance());             
        
        //Reset TabbedPanel
        Comp.SchedTabbedPanel.getInstance().reset();
        setChanged();
        notifyObservers();                         
        
    }    
    
    /**
     * Return last schedule in the scheduleList
     * @return Schedule
     */
    public Schedule getLastSchedule(){
        return lastSchedule;
    }
    
    /**
     * Get current schedule
     * @return Schedule instance
     */
    public Schedule getCurrentSchedule(){
        return currentSchedule;
    }        
    
    /**
     * Set current schedule
     * @param current 
     */
    public void setCurrentSchedule(Schedule current){
        currentSchedule = current;
    }

    /**
     * Reset check flags (internal use)
     */
    private void resetCheckFlags(){
        checkFlags_Map.keySet().forEach(key -> checkFlags_Map.put(key, Boolean.FALSE));
    }
    
    /**
     * Check schedule for consistency and fix (internal use)
     */
    private void scheduleConsistencyCheckAndFix(){
        if(schedules.isEmpty()) return;                 //If scheduleList is empty then skip checking
        resetCheckFlags();
        Logic.userTakenCourses.forEach(course -> checkFlags_Map.put(course.name(), Boolean.TRUE));
        for(Schedule schedule:schedules.values().parallelStream().sorted().collect(Collectors.toList())){            
            if(schedule.coursesInSchedule().isEmpty())
                continue;                                  // if schedule is empty, skip this schedule
            
            schedule.coursesInSchedule().forEach(course -> checkFlags_Map.put(course.name(), Boolean.TRUE));    //presume all courses in this schedule is taken including corequisites

            //DONOT use function here or getting concurrent thread error
            for(Course course:schedule.coursesInSchedule().parallelStream().collect(Collectors.toList())){
                if(!Logic.consistencyCheck_isPrereqMet(course)){
                    schedule.deleteCourse(course);      //Delete course if prereq not met
                }
            }   

        }
    }
    
    /**
     * Evaluating Function, equivalence of isCourseTaken used by Logic.evaluateRule1
     * @param courseName as String
     * @return True if marked as taken; False otherwise
     */
    protected static boolean consistencyCheck_isCourseMarked(String courseName){
        return (checkFlags_Map.get(courseName.toUpperCase())==null) ? false : checkFlags_Map.get(courseName.toUpperCase());        
    }
    
    /**
     * For internal use by schedule class
     */
    protected void refresh(){
        scheduleConsistencyCheckAndFix();
        setChanged();
        notifyObservers();
    }
    
    public void reset(){
        schedules.values().forEach(sched -> clearSchedule(sched));
        schedules.clear();
        currentSchedule = null;
        lastSchedule = null;
        setChanged();
        notifyObservers();
    }
}