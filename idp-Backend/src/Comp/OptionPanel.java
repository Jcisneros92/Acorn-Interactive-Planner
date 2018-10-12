/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Comp;

import Support.LogicController;
import Support.Course;
import Support.Degree.Category;
import Support.Logic;
import Support.ScheduleList;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

/**
 *
 * @author Test
 */
@SuppressWarnings("serial")
public class OptionPanel extends JPanel implements Observer{
    public static enum TopCat {PRE,GEN,PRO};                            //Top categories
    private final TopCat topCategory;                                   //the top category this JPanel is assigned to
    private final List<SubList> subLists = new ArrayList<>();           //The sub list belongs to this JPanel
    private final Map<SubList,JScrollPane> scrollList = new HashMap<>();//The associated scrolllist references
    private Set<Course> currentOptions;
    
    public OptionPanel(TopCat topCat){
        topCategory = topCat;
        currentOptions = new HashSet<>();
        LogicController.getInstance().addObserver(this);
        initComp();
    }
    
    private void initComp(){
        setLayout(new GridLayout(0, 1));
        setBackground(new Color(0,0,0,0));
        switch(topCategory){
            case PRE:
                subLists.add(new SubList(Category.NOT_REQUIRED));
                subLists.add(new SubList(Category.PRE_ENGLISH));
                subLists.add(new SubList(Category.PRE_MATHEMATICS));
                subLists.add(new SubList(Category.PRE_NATURAL_SCIENCE));
                subLists.add(new SubList(Category.PRE_CSENGR));                
                break;
            case GEN:
                subLists.add(new SubList(Category.GEN_LANGUAGE_CULTURE));
                subLists.add(new SubList(Category.GEN_COMMUNICATION));
                subLists.add(new SubList(Category.GEN_CREATIVE_ART));
                subLists.add(new SubList(Category.GEN_AMERICAN_HISTORY));
                subLists.add(new SubList(Category.GEN_POLITICAL_SCIENCE));
                subLists.add(new SubList(Category.GEN_SOCIAL_SCIENCE));                
                break;
            default:
                subLists.add(new SubList(Category.PRO_CSENGR));
                subLists.add(new SubList(Category.PRO_INDUSTRIAL_ENGR));
                subLists.add(new SubList(Category.PRO_MATHEMATICS));
                subLists.add(new SubList(Category.PRO_SCIENCE));
                subLists.add(new SubList(Category.PRO_TECHNICAL_ELECTIVE));                
                break;
        }
        subLists.forEach(sList -> {            
            JScrollPane scrollPane = new JScrollPane(sList);
            scrollPane.setBackground(new Color(0,0,0,0));
            scrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLUE, 1, true),sList.getSubCategory().toString().split("-",-1)[1]));
            scrollPane.getHorizontalScrollBar().addAdjustmentListener(new MyAdjustmentListener());
            scrollPane.getVerticalScrollBar().addAdjustmentListener(new MyAdjustmentListener());
            
            add(scrollPane);
            scrollList.put(sList, scrollPane);
        });        
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public void update(Observable o, Object arg) {
        removeAll();                                                //Remove all from this panel
        currentOptions = (Set<Course>)arg;
        subLists.forEach(subList -> {                               //Recreate all lists
            subList.update();            
            SubListModel model = (SubListModel)subList.getModel();
            if(!model.isEmpty()){
                add(scrollList.get(subList));
            }
        });
        
        getRootPane().repaint();
        getRootPane().validate();
    }
    
    /**
     * Sub list for sub category
     */
    class SubList extends JList<Course> implements MouseListener{
        private final Category subCategory;
        private SubListModel subListModel;
        public SubList(Category subCat){
            subCategory = subCat;
            init();
        }

        public Category getSubCategory() {
            return subCategory;
        }
                        
        public void init(){
            subListModel = new SubListModel(subCategory);            
            setModel(subListModel);            
            setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            setLayoutOrientation(JList.VERTICAL_WRAP);                                    
            setBackground(new Color(0,0,0,0));            
            
            addMouseListener(this);
        }
        
        public void update(){
            subListModel.refresh();
        }

        @Override
        public void mouseClicked(MouseEvent e) {            
            if(e.getClickCount()==2){
                //Add this course to current schedule
                ScheduleList.getInstance().getCurrentSchedule().addCourse(getSelectedValue());                                                
            }else{
                getRootPane().repaint();
                getRootPane().validate();
                
                if (getSelectedValue()!=null) {
                    InfoPanel.getInstance().setText(getSelectedValue().toString());                    
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {}
        @Override
        public void mouseReleased(MouseEvent e) {}
        @Override
        public void mouseEntered(MouseEvent e) {}
        @Override
        public void mouseExited(MouseEvent e) {}
    }
    
    class SubListModel extends AbstractListModel<Course>{
        private final Category category;
        private List<Course> data = new ArrayList<>();
        public SubListModel(Category category){
            this.category = category;
            refresh();
        }

        public boolean isEmpty(){
            return data.isEmpty();
        }
        
        public void refresh(){
            data.clear();            
            data = currentOptions.parallelStream()
                    .filter(course -> Logic.isInCategory(course, category))
                    .collect(Collectors.toList());                        
            fireContentsChanged(this, 0, getSize()-1);            
        }
        
        @Override
        public int getSize() {            
            return data.size();
        }

        @Override
        public Course getElementAt(int index) {
            if(getSize()>0){
                return data.get(index);
            }else{
                return null;
            }
        }        
    }    
    
    /**
     * Implementation to fix repainting issue in list
     */
    class MyAdjustmentListener implements AdjustmentListener{

        @Override
        public void adjustmentValueChanged(AdjustmentEvent e) {            
            Component comp = (Component)e.getSource();            
            comp.getParent().getParent().getParent().repaint();
            comp.getParent().getParent().getParent().revalidate();
        }        
    }
}
