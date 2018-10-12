/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Comp;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JEditorPane;
import javax.swing.text.html.HTMLEditorKit;

/**
 *
 * @author Test
 */
@SuppressWarnings("serial")
public final class InfoPanel extends JEditorPane{
    private volatile static InfoPanel instance = null;
    private static CourseInfo courseInfo = null;
    
    private InfoPanel(){        
        init();
        courseInfo = CourseInfo.getInstance();
        super.setText(courseInfo.getDetailDesc("HELP")); //GETS THE DESCRIPTION OF HELP
    }
    
    public static InfoPanel getInstance(){
        if(instance==null)
            instance = new InfoPanel();
        return instance;
    }

    private void init(){
        setEditorKit(new HTMLEditorKit());
        setPreferredSize(new Dimension(200,500)); //SETS SIZE OF LEFT PANEL
        setEditable(false);
        setBackground(new Color(0,0,0,0));        
    }
    
    @Override
    public void setText(String indexString){
        super.setText(courseInfo.getDetailDesc(indexString));        
        getRootPane().repaint();
    }
}
