/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Observable;
import javax.swing.JOptionPane;


/**
 * Schedule class.
 */
@SuppressWarnings("serial")
public final class Schedule extends Observable implements Comparable<Schedule>,Serializable{
    private final Integer ID;
    private final Semester semester;            //Semester
    private final Integer year;                 //Year
    private final List<Course> courses;         //List of courses in this semester schedule    
    
    private static int currentYear = Calendar.getInstance().get(Calendar.YEAR);     // Default year
    private static Semester currentSemester = Semester.SPRING;                      // Default semester
    private static int nextSerialNumber = 0;
    private static final int MAX_CREDIT_HOURS = 18;                                 //Max credit hours allowed

    
    /**
     * Semester Enum with auto rollover
     */
    public static enum Semester{
        SPRING(0),SUMMER(1),FALL(2){
            @Override 
            public Semester next(){
                return values()[0]; }
        };
        
        private final int id;

        Semester(int id) {this.id = id;}
        public int value(){return id;}        
        
        public Semester next(){            
            return values()[(ordinal() + 1)];
        }        
        
        @Override
        public String toString(){
            switch(id){
                case 0:
                    return "Spring";                    
                case 1:
                    return "Summer";                    
                case 2:
                    return "Fall";                    
                default:
            }
            return "";
        }        
    }
    
    /**
     * Constructor with default value (DONOT use)
     */
    @SuppressWarnings("unused")
	private Schedule(){        
        this.year = currentYear = Calendar.getInstance().get(Calendar.YEAR);
        this.semester = currentSemester = Semester.SPRING;        
        this.ID = nextSerialNumber++;
        courses = new ArrayList<>();
    }
        
    /**
     * Constructor with parameters
     * @param semester
     * @param year 
     */
    protected Schedule(Semester semester, int year) {        
        this.year = currentYear = year;                
        this.semester = currentSemester = semester;
        this.ID = nextSerialNumber++;
        courses = new ArrayList<>();
        addObserver(LogicController.getInstance());
    }
    
    /**
     * Add a course to schedule.
     * Note: If course has been added before or the addition of this course will
     *      cause this semester total hours to exceed maxCreditHours or this is 
     *      not the last schedule in the list, then this course will not be added.
     * @param course
     * @return True: success; False: failed, no course was added
     */
    public boolean addCourse(Course course){
        if(ScheduleList.getInstance().getLastSchedule()!=this) return false;    //disable ADD if this is not the last schedule
        //Check if hour limit exceeded
        if(courses.parallelStream().mapToInt(cr -> cr.creditHour())
                        .sum() + course.creditHour() > MAX_CREDIT_HOURS){
            JOptionPane.showMessageDialog(null, "The maximum hours allowed was exceeded.", "Credit hour limit exceeded", JOptionPane.WARNING_MESSAGE);
            return false;                                  
        }
                
        //Check if there are untaken co-requisites then give user a warning
        if (Logic.isPrereqMetButNotCoreqMet(course)) {
            StringBuilder sb = new StringBuilder("The below co-requisites are also needed in this schedule: (see course info)\n\n");
            Logic.findCorequisites(course).forEach(cr -> sb.append(cr + "\n"));
            JOptionPane.showMessageDialog(null, sb, "Co-requisites required", JOptionPane.WARNING_MESSAGE);
        }
        
        if(!courses.contains(course)){
            course.setTaken(true);
            courses.add(course);
            setChanged();
            notifyObservers(courses);
            return true;            
        }else{
            return false;
        }
    }
    
    /**
     * Delete a course from schedule
     * @param course
     * @return True: Success; False: failed, no course was deleted
     */
    public boolean deleteCourse(Course course){
        if(courses.contains(course)){
            course.setTaken(false);
            courses.remove(course);                    
            setChanged();
            notifyObservers(courses);
            
            ScheduleList.getInstance().refresh();   //request error checking
            return true;
        }else{
            return false;
        }
    }
    
    public void removeAllCourses(){
        courses.forEach(course -> course.setTaken(false));
        courses.clear();                
        setChanged();
        notifyObservers();
    }
    
    /**
     * Return a list of courses in schedule
     * @return List of Courses
     */
    public List<Course> coursesInSchedule(){
        return courses;
    }
    
    /**
     * Create next schedule instance
     * @return schedule instance
     */
    protected static Schedule next(){
        if(currentSemester == Schedule.Semester.FALL){
            Semester nextSemester = currentSemester.next();            
            int nextYear = currentYear + 1;
            currentSemester = nextSemester;
            return new Schedule(nextSemester, nextYear);
        }else{
            currentSemester = currentSemester.next();            
            return new Schedule(currentSemester, currentYear);
        }        
    }
    
    //Getters
    protected static Semester getCurrentSemester(){return currentSemester;}    
    protected static int getCurrentYear(){return currentYear;}
    
    //Setters
    protected static void setCurrentSemester(Semester restoreSemester){currentSemester = restoreSemester;}    
    protected static void setCurrentYear(int restoredYear){currentYear = restoredYear;}

    public String name(){
        return this.toString();
    }
    
    @Override
    public String toString(){
        return String.format("%s %d" , semester, year);
    }            
    
    @Override
    public int compareTo(Schedule o) {        
        return (ID.compareTo(o.ID));
    }
    
    /*
    * Below methods are used internally by Data class
    */
    
    /**
     * Restore static variables from user's saved data
     * @param lastSet 
     */
    protected static void restoreStaticConfig(staticConfig lastSet){
        currentSemester = lastSet.cSemesterSetting;
        currentYear = lastSet.cYearSetting;
        nextSerialNumber = lastSet.cNextSerialSetting;
    }
    
    /**
     * Retrieve static variable to be saved
     * @return 
     */
    protected static staticConfig retrieveStaticConfig(){        
        return new staticConfig(currentSemester, currentYear, nextSerialNumber);
    }
    
    /**
     * Combine all static variables into one
     */
    protected static class staticConfig implements Serializable{
        Semester cSemesterSetting;
        int cYearSetting;
        int cNextSerialSetting;

        public staticConfig(Semester cSemesterSetting, int cYearSetting, int cNextSerialSetting) {
            this.cSemesterSetting = cSemesterSetting;
            this.cYearSetting = cYearSetting;
            this.cNextSerialSetting = cNextSerialSetting;
        }
        
        @SuppressWarnings("unused")
		private staticConfig(){}        
    }
}
