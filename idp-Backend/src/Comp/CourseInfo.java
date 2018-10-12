/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Comp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Providing course info in HTML text
 * @author Test
 * Singleton
 */
public final class CourseInfo{
    private volatile static CourseInfo instance = null;
    private static final Map<String,String> courseDescriptions = new HashMap<>();
    
    private CourseInfo(){
        readFile();
    }
    
    public static CourseInfo getInstance(){
        if(instance==null)
            instance = new CourseInfo();
        return instance;
    }
    
    /**
     * Get Course info
     * @param key
     * @return HTML text
     */    
    public String getDetailDesc(String key){
        if(courseDescriptions.containsKey(key))
            return courseDescriptions.get(key);
        else
            return courseDescriptions.get("HELP");
    }        
    
    /**
     * Read data from file
     */
    private void readFile(){
        InputStream is = getClass().getResourceAsStream("files/courseDescription.csv");
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String strLine;        
            while ((strLine = br.readLine())!=null) {
                String[] items = strLine.split("zz", -1);
                courseDescriptions.put(items[0], items[1]);
            }        
                   
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CourseInfo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CourseInfo.class.getName()).log(Level.SEVERE, null, ex);
        }                
    }
}
