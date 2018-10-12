/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Support;

import java.io.Serializable;

/**
 * Course Class
 * 
 */
@SuppressWarnings("serial")
public class Course implements Comparable<Course>,Serializable{
    private final String courseID;            // Course courseID
    private final String courseAbbreviation;  //Course abbreviation
    private final String courseNumber;        //Course number as string
    private final int creditHour;             // Credit hour
    private final String category;            // Category
    private final String Description;         // Brief Description
    private final String prerequisites;       // prerequisites evaluation string    

    private boolean taken = false;      // True: Course is taken; false: not taken
 
    /**
     * Constructor
     * @param crAbbr
     * @param crNum
     * @param creditHour
     * @param category
     * @param Description
     * @param prerequisites 
     */
    public Course(String crAbbr, String crNum, int creditHour, String category, String Description, String prerequisites) {
        this.courseAbbreviation = crAbbr;
        this.courseNumber = crNum;
        this.courseID = crAbbr + crNum;
        this.creditHour = creditHour;
        this.category = category;        
        this.Description = Description;
        this.prerequisites = prerequisites;        
    }
    
    // Getters
    public String name(){return this.courseID;}
    public String Abbreviation(){return this.courseAbbreviation;}
    public String courseNumber(){return this.courseNumber;}
    public int creditHour(){return this.creditHour;}
    public String category(){return category;}    
    public String Description(){return Description;}    
    public String prerequisites(){return this.prerequisites;}    
    
    // Setters
    public void setTaken(boolean inBoolean){this.taken = inBoolean;}
    public boolean taken(){return this.taken;}
        
    @Override
    public boolean equals(Object o){
        if(!(o instanceof Course))
            return false;
        Course c = (Course)o;
        return (c.courseID.equals(courseID));
    }
    
    @Override
    public int hashCode(){
        return 18*courseID.hashCode();
    }
    
    @Override
    public String toString(){
        return courseID;
    }
    
    @Override
    public int compareTo(Course c) {        
        return (courseID.compareTo(c.courseID));
    }    
}
