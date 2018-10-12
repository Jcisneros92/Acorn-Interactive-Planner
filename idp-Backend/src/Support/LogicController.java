/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Support;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class provides all methods used by GUI layer
 * @author Test
 */
public final class LogicController extends Observable implements Observer{
    private volatile static LogicController instance = null;    
    private static Set<Course> startingCurrentOptions;          //Options before courses added to schedule
    private static Set<Course> workingCurrentOptions;           //Options after courses added to schedule
    
    private LogicController(){
        startingCurrentOptions = Collections.synchronizedSet(new HashSet<>());
        workingCurrentOptions = Collections.synchronizedSet(new HashSet<>());        
        ScheduleList.getInstance().addObserver(this);        
    }
    
    /**
     * Return all courses in database as list
     * @return List
     */
    public List<Course> getCourses(){
        return Logic.courses.values().parallelStream().collect(Collectors.toList());
    }
    
    /**
     * Return a list of courses user can select
     * @return List
     */
    public List<Course> getUserOptions(){
        return workingCurrentOptions.parallelStream().collect(Collectors.toList());
    }
    
    /**
     * Return List of courses user can select group by category
     * @param category
     * @return 
     */
    public List<Course> getUserOptionsByClass(Degree.Category category){
        return workingCurrentOptions.parallelStream()
                .filter(course -> Logic.isInCategory(course, category))
                .collect(Collectors.toList());
    }

    /**
     * Combine user taken courses set with main database set
     * @param takenCourses 
     */
    public void setUserTakenCourses(List<Course> takenCourses){
        Logic.mergeCoursesUserHasTaken(takenCourses);
    }
    
    public static LogicController getInstance(){
        if (instance==null) {
            instance = new LogicController();
        }
        return instance;
    }

    @Override
    public void update(Observable o, Object arg) {        
        if(o instanceof ScheduleList){                                  //If notified by ScheduleList then update startingCurrentOptions
            startingCurrentOptions = Logic.coursesUserCanSelect();
            setChanged();
            notifyObservers(startingCurrentOptions);
        }else if (o instanceof Schedule) {                              //If notified by Schedule then update workingCurrentOptions
            workingCurrentOptions.clear();            
            workingCurrentOptions.addAll(Logic.coursesUserCanSelect());
            workingCurrentOptions.retainAll(startingCurrentOptions);    //workingCurrentOptions is the intersection of 2 sets
            setChanged();
            notifyObservers(workingCurrentOptions);            
        }else{
            //never reach
        }
    }
}
