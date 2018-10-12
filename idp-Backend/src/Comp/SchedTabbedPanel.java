/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Comp;

import Support.Course;
import Support.Schedule;
import Support.ScheduleList;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.stream.Collectors;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * TabbedPane component
 * Singleton
 * @author Ben
 */
@SuppressWarnings("serial")
public final class SchedTabbedPanel extends JTabbedPane implements Observer{    
    private volatile static SchedTabbedPanel instance = null;
    private ScheduleList masterPlanRef = null;    
    private Map<Schedule,JTable> tabbedPaneData = new HashMap<>();
    private Map<Schedule,Component> tabbedPaneTabLink = new HashMap<>();    
    private Schedule currentSchedule = null;
    
    /**
     * Private Constructor
     */
    private SchedTabbedPanel(){
        masterPlanRef = ScheduleList.getInstance();
        masterPlanRef.addObserver(this);        
        initComp();
    }
    
    /**
     * Get an instance of this class
     * @return instance of SchedTabbedPanel
     */
    public static SchedTabbedPanel getInstance(){
        if(instance==null)
            instance = new SchedTabbedPanel();
        return instance;
    }
    
    /**
     * Initialize Components
     */
    private void initComp(){
        UIDefaults defaults = UIManager.getLookAndFeelDefaults();
        if(defaults.get("Table.alternateRowColor")==null)
            defaults.put("Table.alternateRowColor", new Color(240,240,240));    //Change default table alternate row color here
        setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        List<Schedule> schedules = masterPlanRef.getSchedulesList();
        if(!schedules.isEmpty()){
            schedules.forEach(schedule -> createTab(schedule));                    
        }
        
        //On tab change, change current schedule in the master plan
        addChangeListener((ChangeEvent e) -> {            
            int idx = getSelectedIndex();
            if (idx >= 0) {
                currentSchedule = masterPlanRef.getSchedulesList().get(idx);
                masterPlanRef.setCurrentSchedule(currentSchedule);            
            }
        });
    }    
    
    /**
     * Remove all tabs and reset all data
     * This is needed when user loads saved data
     */
    public void reset(){
        removeAll();
        tabbedPaneData.clear();
        tabbedPaneTabLink.clear();
        currentSchedule = null;
    }
    
    @Override
    public void update(Observable o, Object arg) {        
        //Create lists for comparison
        List<Schedule> masterPlanSchedules = masterPlanRef.getSchedulesList();
        List<Schedule> tabbedPaneSchedules = tabbedPaneTabLink.keySet().stream().collect(Collectors.toList());
        
        //If schedule removal is allowed then enable this
        //if a schedule in tabbedPaneSchedules does not exist in master plan schedule then delete it (no need if schedule cannot be deleted)
//        tabbedPaneSchedules.forEach(sched -> {
//            if(!masterPlanSchedules.contains(sched)){
//                deleteTab(sched);
//            }
//        });
        
        //if a schedule in masterPlanSchedules does not exist in tabbedPaneSchedules then create a tab for it
        if (masterPlanSchedules.isEmpty()) {
            reset();
        }else{
            masterPlanSchedules.forEach(sched -> {
                if(!tabbedPaneSchedules.contains(sched)){
                    createTab(sched);
                }
            });                    
        }
    }    
    
    /**
     * Create a new tab
     * @param schedule 
     */
    public void createTab(Schedule schedule){
        String nameString[] = schedule.toString().split(" ", -1);
        String tabName = "<html><b>" + nameString[0] + "</b><br><i>" + nameString[1] + "</i></html>";
        JTable newTable = getTable(schedule);               //Create new table
        tabbedPaneData.put(schedule, newTable);             //Add new reference record
        JScrollPane scrollPane = new JScrollPane(newTable); //Put table in JScrollPane
        Component tab = add(tabName,scrollPane);            //Add new tab to this tabbedPane
        tabbedPaneTabLink.put(schedule, tab);               //Add a new reference record
        
        masterPlanRef.setCurrentSchedule(schedule);         //set this schedule as current
        setSelectedComponent(tab);                          //switch to this tab
    }
    
    /**
     * Delete an existing tab
     * @param schedule 
     */
    public void deleteTab(Schedule schedule){
        if(tabbedPaneData.containsKey(schedule)){
            JTable theTable = tabbedPaneData.get(schedule);     //Get table reference
            tModel theModel = (tModel)theTable.getModel();      //Get the table data model reference
            theModel.unregister();                              //Disconnect this data model from schedule
            tabbedPaneData.remove(schedule, theTable);          //Remove reference record
            Component tab = tabbedPaneTabLink.get(schedule);
            int tabIdx = this.indexOfTabComponent(tab);
            remove(tab);                                        //remove tab from this tabbedPane
            tabbedPaneTabLink.remove(schedule, tab);            //remove reference record
            
            if(tabIdx == (getTabCount()-1)){                    //If this tab is the last tab
                setSelectedIndex(tabIdx-1);                     //move to the adjacent one
            }
        }
    }
    
    /**
     * Generate new table
     * @param schedule
     * @return reference to table
     */
    private JTable getTable (Schedule schedule){
        DefaultTableCellRenderer centerCellRenderer = new DefaultTableCellRenderer();
        centerCellRenderer.setHorizontalAlignment(JLabel.CENTER);
        JTable table = new JTable(3, 3);
        table.setAutoCreateColumnsFromModel(true);
        table.setAutoCreateRowSorter(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);                
        table.setGridColor(Color.BLUE);
        table.setPreferredScrollableViewportSize(new Dimension(100, 100));

        //table.setShowGrid(false);                 //Debug purpose
        //table.setShowHorizontalLines(false);      //Debug purpose
        //table.setShowVerticalLines(false);        //Debug purpose
        table.setToolTipText("Double click on row to remove item.");       
        
        table.setModel(new tModel(schedule));
        table.addMouseListener(new tMouseListener());                
        
        table.getColumnModel().getColumn(1).setCellRenderer(centerCellRenderer);        
        table.getColumnModel().getColumn(0).setPreferredWidth(5);
        table.getColumnModel().getColumn(1).setPreferredWidth(5);
        table.getColumnModel().getColumn(2).setPreferredWidth(200);        
        
        return table;
    }

    /**
     * Customized data model for table
     */
    class tModel extends AbstractTableModel implements Observer{
        private final String[] HEADER = {"<html><b>Course</b></html>","<html><b>Credit hour</b></html>","<html><b>Description</b></html>"};
        private Schedule scheduleInstance = null;       //Reference of schedule attached to this model 
        private List<Course> data;
        
        public tModel(Schedule schedule){
            scheduleInstance = schedule;
            scheduleInstance.addObserver(this);
            data = scheduleInstance.coursesInSchedule();
        }
        
        public void unregister(){
            scheduleInstance.deleteObserver(this);
        }

        @Override
        public int getRowCount() {           
            return data.size();
        }

        @Override
        public int getColumnCount() {            
            return HEADER.length;
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {            
            return HEADER[columnIndex].getClass();
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {            
            return false;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            switch(columnIndex){
                case 0:
                    return data.get(rowIndex);                    
                case 1:
                    return data.get(rowIndex).creditHour();
                case 2:
                    return data.get(rowIndex).Description();
                default:
                    return null;      // reserved for more info
            }            
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {            
            // not applicable
        }

        @Override
        public String getColumnName(int columnIndex) {            
            return HEADER[columnIndex];
        }

        @Override
        public void update(Observable o, Object arg) {            
            data = scheduleInstance.coursesInSchedule();            
            fireTableDataChanged();            
        }
        
        public void removeRecord(Course courseToRemove){
            scheduleInstance.deleteCourse(courseToRemove);
            data = scheduleInstance.coursesInSchedule();
            fireTableDataChanged();
        }
    }     
    
    /**
     * Customized MouseListener for table
     */
    class tMouseListener implements MouseListener{
        @Override
        public void mouseClicked(MouseEvent e) {            
            JTable table = (JTable)e.getSource();
            int row = table.getSelectedRow();
            tModel model = (tModel)table.getModel();
            Course course = (Course)model.getValueAt(row, 0);
            if(e.getClickCount()==2){
                model.removeRecord(course);
            }else{
                InfoPanel.getInstance().setText(course.name());
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {}
        @Override
        public void mouseReleased(MouseEvent e){}
        @Override
        public void mouseEntered(MouseEvent e) {}
        @Override
        public void mouseExited(MouseEvent e) {}
    }        
}
