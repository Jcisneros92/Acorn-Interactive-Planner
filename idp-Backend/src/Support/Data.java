/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Support;

import static Support.Logic.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.print.PrinterException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.html.HTMLEditorKit;

/**
 * Data Subsystems.
 * Contains all data related methods
 */
public final class Data {
    private static final String DEFAULT_USER_DATA_FILE = "user_schedules.dat";  // Default user data file    
    private static final String COURSE_DATA_FILE = Data.class.getResource("files/courses.csv.").getPath();               // Database in csv format    
        
    /**
     * Load courses database.
     */
    public static void loadCourseData(){        
        try {                    
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(COURSE_DATA_FILE)));
            String strLine;
        
            while ((strLine = br.readLine())!=null) {
                String[] items = strLine.split(",", -1);                
                Course course = new Course(items[0],items[1],Integer.valueOf(items[2]), items[3], items[4],items[5]);
                courses.put(course.name(), course);
            }
            br.close();
                   
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }        
        
    /**
     * Save user's schedules
     * note: The User's scheduling data to be saved consists of
     *      course database, user taken courses, schedule list data structure, schedule static variables
     *      , all are packed and saved as one object (DataPacker).
     * @return True: success; False: failed
     */
    public static boolean saveUserSchedules(){                
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File(DEFAULT_USER_DATA_FILE));
        int result = fileChooser.showSaveDialog(new JPanel());
        
        if(result == JFileChooser.APPROVE_OPTION){
            String path = fileChooser.getSelectedFile().getPath();
            return saveUserSchedules(path);
        }else{
            return false;
        }                
    }          
    
    /**
     * Load user's schedules     
     * @return True: success; False: failed
     */
    public static boolean loadUserSchedules(){                
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("User Schedule", "dat"));
        int result = fileChooser.showOpenDialog(new JPanel());
        
        if(result == JFileChooser.APPROVE_OPTION){
            String path = fileChooser.getSelectedFile().getPath();
            return loadUserSchedules(path);
        }else{
            return false;
        }        
    }                   
    
    /**
     * Print Report.
     */
    public static void printReport(){        
        JEditorPane editorPane = new JEditorPane();
        editorPane.setEditorKit(new HTMLEditorKit());
        editorPane.setText(createReport());
        editorPane.setEditable(false);      

        JButton bttnPrint = new JButton("Print");
        bttnPrint.setPreferredSize(new Dimension(100, 50));        
        bttnPrint.addActionListener( e -> {
            try {
                editorPane.print();                
            } catch (PrinterException ex) {
                Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex);
            }
        });                            

        JScrollPane scrollPane = new JScrollPane(editorPane);            

        JPanel pane = new JPanel(new BorderLayout(5, 5));        
        pane.setBorder(new EmptyBorder(2, 2, 2, 2));
        pane.add(scrollPane,BorderLayout.CENTER);
        
        JPanel subPane = new JPanel();
        subPane.add(bttnPrint);
        pane.add(subPane,BorderLayout.PAGE_END);         

        JFrame frame = new JFrame("Report");
        frame.add(pane);          
        frame.setPreferredSize(new Dimension(750, 900));
        
        frame.pack();          
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);              
    }
    
    
    //Private methods ======================================================
    
    /**
     * Save user's schedules
     * @param filePath
     * @return True: success; False: otherwise
     */
    private static boolean saveUserSchedules(String filePath){
        // Pack data and save            
        DataPacker newDataPacker = new DataPacker(Logic.courses, ScheduleList.getInstance().getSchedules_Map(),
                Logic.userTakenCourses, Schedule.retrieveStaticConfig());
        try(ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(filePath))) {
                oos.writeObject(newDataPacker);                  
        } catch (IOException ex) {
            Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }
    
    /**
     * Load User's Schedule
     * @param filePath
     * @return True: success; False: otherwise
     */
    private static boolean loadUserSchedules(String filePath){
        boolean ret = true;
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(filePath))){
            DataPacker newDataPacker = (DataPacker)ois.readObject();

            Logic.courses.clear();                
            Logic.courses.putAll(newDataPacker.courses);                

            Logic.userTakenCourses.clear();
            Logic.userTakenCourses.addAll(newDataPacker.takenCourses);

            ScheduleList.getInstance().setSchedules_Map(newDataPacker.schedules);
            Schedule.restoreStaticConfig(newDataPacker.schedConfig);    //This should be the last

        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex);
            ret = false;
        }
        return ret;
    }
    
    /**
     * Generate report for printReport
     * @return HTML doc as string
     */
    private static String createReport(){
        //Collect statistical info
        final int totalRequired = 121;
        final int hoursTaken = Logic.userTakenCourses.parallelStream().mapToInt(course -> course.creditHour()).sum();
        final int hoursTakenAndPlanned = Logic.courses.values().parallelStream().filter(course -> course.taken()).mapToInt(course -> course.creditHour()).sum();                
        ArrayList<String> lines = new ArrayList<>();
        
        //Insert date and header      
        lines.add("<h2><em>Degree Plan Report</em></h2><p>Date: " + new SimpleDateFormat("MM/dd/YYYY").format(new Date()) + "</p><br>");        
        lines.add("<table style=\"width: 265px;\" cellpadding=\"0\"><thead><tr><td style=\"width: 257px;\" colspan=\"2\"><strong>Status Summary</strong></td></tr></thead><tbody>");
        
        // Insert Statistics Summary
        lines.add("<tr><td style=\"width: 161px;\">Credit hours required</td><td style=\"width: 96px; text-align: center;\">" + totalRequired +"</td></tr>");
        lines.add("<tr><td style=\"width: 161px;\">Credit hours taken</td><td style=\"width: 96px; text-align: center;\">" + hoursTaken + "</td></tr>");
        lines.add("<tr><td style=\"width: 161px;\">Credit hours planned</td><td style=\"width: 96px; text-align: center;\">" + (hoursTakenAndPlanned - hoursTaken) + "</td></tr>");
        lines.add("<tr><td style=\"width: 161px;\">Credit hours remain</td><td style=\"width: 96px; text-align: center;\">" + (totalRequired - hoursTakenAndPlanned) + "</td></tr>");
        //lines.add("<tr><td style=\"width: 161px;\">Graduation expected</td><td style=\"width: 96px; text-align: center;\">Some day</td></tr>");
        lines.add("</tbody></table><br>");
        
        //Insert "Taken courses" table
        lines.add("<table style=\"width: 530px;\"><tbody><tr><td style=\"width: 530px;\" colspan=\"3\"><strong>Taken Courses</strong></td>" +
                "</tr><tr><td style=\"width: 72px;\"><span style=\"text-decoration: underline;\">Course</span></td>" +
                "<td style=\"width: 97px; text-align: center;\"><span style=\"text-decoration: underline;\">Credit hour</span></td>" +
                "<td style=\"width: 361px;\"><span style=\"text-decoration: underline;\">Description</span></td></tr>");
                
        Logic.userTakenCourses.parallelStream().sorted().forEach(course -> 
                lines.add("<tr><td style=\"width: 72px;\">" + course.name() + "</td><td style=\"text-align: center; width: 97px;\">" + 
                        course.creditHour() + "</td><td style=\"width: 361px;\">" + course.Description() + "</td></tr>")
                    );
        lines.add("</tbody></table>");
        
        // Insert schedule header
        lines.add("<p><strong>Planned Schedules</strong></p>");
        
        // Insert schedule tables
        ScheduleList.getInstance().getSchedulesList().forEach(schedule -> {
                    lines.add("<br><table style=\"width: 527px;\"><tbody>" + "<tr><td style=\"width: 523px;\" colspan=\"3\"><em>&nbsp;&nbsp;" + schedule.toString() + "</em></td></tr><tr>" +
                        "<td style=\"width: 69px;\"><span style=\"text-decoration: underline;\">Course</span></td>" +
                        "<td style=\"width: 98px; text-align: center;\"><span style=\"text-decoration: underline;\">Credit hour</span></td>" +
                        "<td style=\"width: 356px;\"><span style=\"text-decoration: underline;\">Description</span></td></tr>");
                    schedule.coursesInSchedule().stream().sorted().forEach(course -> 
                            lines.add("<tr><td style=\"width: 69px;\">" + course.name() + 
                                    "</td><td style=\"text-align: center; width: 98px;\">3</td><td style=\"width: 356px;\">" + course.Description() + "</td></tr>"));
                    lines.add("</tbody></table>");
        });
                        
        StringBuilder SB = new StringBuilder();
        lines.forEach(line -> SB.append(line));
        return SB.toString();
    }                
    
    /**
     * Class for data packing.
     */
    @SuppressWarnings("serial")
	private static class DataPacker implements Serializable{
        Map<String, Course> courses;
        Map<String,Schedule> schedules;
        Set<Course> takenCourses;
        Schedule.staticConfig schedConfig;

        public DataPacker(Map<String, Course> courses, Map<String,Schedule> schedules, Set<Course> takenCourses,Schedule.staticConfig schedStaticSettings) {
            this.courses = new HashMap<>();
            this.courses.putAll(courses);
            this.takenCourses = new HashSet<>();
            this.takenCourses.addAll(takenCourses);
                    
            this.schedules = new HashMap<>();
            this.schedules.putAll(schedules);
            this.schedConfig = new Schedule.staticConfig(schedStaticSettings.cSemesterSetting,
                    schedStaticSettings.cYearSetting,schedStaticSettings.cNextSerialSetting);
        }        
    }
}
