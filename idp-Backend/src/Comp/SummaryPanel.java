/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Comp;

import Support.Course;
import Support.Degree;
import Support.Logic;
import Support.LogicController;
import java.awt.Color;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JEditorPane;
import javax.swing.text.html.HTMLEditorKit;

/**
 *
 * @author Test
 */
@SuppressWarnings("serial")
public final class SummaryPanel extends JEditorPane implements Observer{
    private volatile static SummaryPanel instance = null;     
    private static final int totalHoursRequired = Degree.totalHoursRequired;    
    private static int preHr;
    private static int genHr;
    private static int proHr;
    
    private SummaryPanel(){
        init();
    }
    
    public static SummaryPanel getInstance(){
        if(instance==null)
            instance = new SummaryPanel();
        return instance;
    }
    
    private void init(){        
        setEditorKit(new HTMLEditorKit());
        //setPreferredSize(new Dimension(200,50));
        setEditable(false);
        setBackground(new Color(0,0,0,0));        
        setText("<html><span style=\"color: #000000;\">"                
                + "<br><b>Pre-prof hours taken: </b>0"
                + "<br><b>GenEd hours taken: </b>0"
                + "<br><b>Prof hours taken: </b>0"
                + "<br><br><b>Degree hours required: </b>" + totalHoursRequired
                + "<br><b>Total hours taken: </b>0"
                + "<br><b>Total hours remain: </b>" + totalHoursRequired
                        + "</span></html>");
        LogicController.getInstance().addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        preHr = LogicController.getInstance().getCourses().parallelStream()
                .filter((Course course) -> (course.taken() &&  (Logic.isInCategory(course, Degree.Category.PRE_ENGLISH)
                        || Logic.isInCategory(course, Degree.Category.PRE_MATHEMATICS)
                        || Logic.isInCategory(course, Degree.Category.PRE_NATURAL_SCIENCE)
                        || Logic.isInCategory(course, Degree.Category.PRE_CSENGR))))
                .map(cr -> cr.creditHour())
                .mapToInt(Integer::intValue).sum();
        
        genHr = LogicController.getInstance().getCourses().parallelStream()
                .filter((Course course) -> (course.taken() && (Logic.isInCategory(course, Degree.Category.GEN_LANGUAGE_CULTURE)
                        || Logic.isInCategory(course, Degree.Category.GEN_COMMUNICATION)
                        ||Logic.isInCategory(course, Degree.Category.GEN_CREATIVE_ART)
                        || Logic.isInCategory(course, Degree.Category.GEN_AMERICAN_HISTORY)
                        ||Logic.isInCategory(course, Degree.Category.GEN_POLITICAL_SCIENCE)
                        ||Logic.isInCategory(course, Degree.Category.GEN_SOCIAL_SCIENCE))))
                .map(cr -> cr.creditHour())
                .mapToInt(Integer::intValue).sum();
        
        proHr = LogicController.getInstance().getCourses().parallelStream()
                .filter((Course course) -> (course.taken() && (Logic.isInCategory(course, Degree.Category.PRO_CSENGR)
                        || Logic.isInCategory(course, Degree.Category.PRO_INDUSTRIAL_ENGR)
                        || Logic.isInCategory(course, Degree.Category.PRO_MATHEMATICS)
                        || Logic.isInCategory(course, Degree.Category.PRO_SCIENCE)
                        || Logic.isInCategory(course, Degree.Category.PRO_TECHNICAL_ELECTIVE))))
                .map(cr -> cr.creditHour())
                .mapToInt(Integer::intValue).sum();
        
        setText("<html><span style=\"color: #000000;\">"                
                + "<br><b>Pre-prof hours taken: </b>" + preHr
                + "<br><b>GenEd hours taken: </b>" + genHr
                + "<br><b>Prof hours taken: </b>" + proHr
                + "<br><br><b>Degree hours required: </b>" + totalHoursRequired
                + "<br><b>Total hours taken: </b>" + (preHr+genHr+proHr)
                + "<br><b>Total hours remain: </b>" + (totalHoursRequired - (preHr+genHr+proHr))
                        + "</span></html>");        
    }        
}
