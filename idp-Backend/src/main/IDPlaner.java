/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import Comp.MainScreen;
import Support.Course;
import Support.Data;
import Support.Logic;
import Support.LogicController;
import java.util.ArrayList;
import java.util.List;

/**
 * Main Program.
 */
public class IDPlaner {        

    /**
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {                
        // Initialization        
        Data.loadCourseData();

        // Testing =========================================  
        //loadSampleUserTakenCourses();
        // End Testing =====================================
        
        MainScreen.getInstance();                    
        // TODO code application logic here
    }
    
    public static void loadSampleUserTakenCourses(){
        LogicController ctrl = LogicController.getInstance();
        List<Course> taken = new ArrayList<>();
        
        taken.add(Logic.getCourses().get("CSE1310"));
        taken.add(Logic.getCourses().get("UNIV1131"));
        taken.add(Logic.getCourses().get("CSE1105"));        
        
        ctrl.setUserTakenCourses(taken);
    }
}
