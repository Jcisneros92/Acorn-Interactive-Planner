/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Comp;

import Support.Schedule;
import Support.ScheduleList;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;
import javax.swing.border.Border;

/**
 *
 * @author Test
 */
@SuppressWarnings("serial")
public final class MainScreen extends JFrame implements Observer{
    private volatile static MainScreen instance = null;
    
    private JLabel appNameLabel;
    private JPanel infoPanel;
    private JPanel prePanel;
    private JPanel genPanel;
    private JPanel proPanel;
    private JPanel schedPanel;
    private JPanel summaryPanel;
    
    private JMenuBar menuBar;
    private JMenu menu;
    private JMenuItem menuItem;
    private JMenuItem createInitialSMenuItem;
    
    private final InfoPanel infoText;
    private final SummaryPanel sumText;;
    private final OptionPanel prePane;
    private final OptionPanel genPane;
    private final OptionPanel proPane;    
    private final SchedTabbedPanel tabbedPane;
    private final ScheduleList masterPlanRef = ScheduleList.getInstance();
    
    @SuppressWarnings("unused")
	private static final Color SEMI_TRANS_B = new Color(0,0,0,0.5f);
    private static final Color SEMI_TRANS_W = new Color(1,1,1,0.7f);        

    /**
     * Creates new
     */
    private MainScreen() {
        infoText = InfoPanel.getInstance(); //INITIALIZES THE COURSE DESCRIPTIONS AND LEFT PANEL
        sumText = SummaryPanel.getInstance();
        prePane = new OptionPanel(OptionPanel.TopCat.PRE);
        genPane = new OptionPanel(OptionPanel.TopCat.GEN);
        proPane = new OptionPanel(OptionPanel.TopCat.PRO);
        tabbedPane = SchedTabbedPanel.getInstance();
        initComponents();
        masterPlanRef.addObserver(this);
        setVisible(true);
    }
    
    public static MainScreen getInstance(){
        if(instance==null)
            instance = new MainScreen();
        return instance;
    }                
    
    private void initComponents() {
        appNameLabel = new javax.swing.JLabel();
        infoPanel = new javax.swing.JPanel();
        prePanel = new javax.swing.JPanel();
        genPanel = new javax.swing.JPanel();
        proPanel = new javax.swing.JPanel();
        schedPanel = new javax.swing.JPanel();
        summaryPanel = new javax.swing.JPanel();                                

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);        
        
	// Create menu system =====================================================
        menuBar = new JMenuBar();
        
        menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        
        menuItem = new JMenuItem("Open saved data");
        menuItem.addActionListener(e -> Support.Data.loadUserSchedules());
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Save current data");
        menuItem.addActionListener(e -> Support.Data.saveUserSchedules());
        menu.add(menuItem);
        
        menu.addSeparator();
        menuItem = new JMenuItem("Print report");
        menuItem.addActionListener(e -> Support.Data.printReport());
        menu.add(menuItem);
        
        menu.addSeparator();
        menuItem = new JMenuItem("Exit");
        menuItem.addActionListener(e -> System.exit(0));
        menu.add(menuItem);
        
        menuBar.add(menu);
        
        menu = new JMenu("Schedule");
        menu.setMnemonic(KeyEvent.VK_S);
        
        createInitialSMenuItem = new JMenuItem("Create initial schedule");
        createInitialSMenuItem.addActionListener(e -> {new InitialSchedDialog();});
        menu.add(createInitialSMenuItem);
        
        menuItem = new JMenuItem("Add new schedule");
        menuItem.addActionListener(e -> masterPlanRef.createNextSchedule());
        menu.add(menuItem);        

        menu.addSeparator();
        menuItem = new JMenuItem("Clear current schedule");
        menuItem.addActionListener(e -> {
            Schedule current = masterPlanRef.getCurrentSchedule();
            masterPlanRef.clearSchedule(current);
        });        
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Reset");
        menuItem.addActionListener(e -> {
            masterPlanRef.reset();
        });        
        menu.add(menuItem);                
        menuBar.add(menu);
        
        menu = new JMenu("Help");
        menu.setMnemonic(KeyEvent.VK_H);        
                
        menuItem = new JMenuItem("General instruction");
        menuItem.addActionListener(e -> InfoPanel.getInstance().setText("HELP"));
        menu.add(menuItem);
        menuBar.add(menu);
        
        setJMenuBar(menuBar);                
        //End create menu system =======================================================                   

        //Border infoBorder = BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), "<html><span style=\"color: #ffffff;\">Information</span></html>");
        Border infoBorder = BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), "Information");        
        infoPanel.setBorder(infoBorder);        
        //infoPanel.setBackground(new Color(204, 255, 153));
        infoPanel.setBackground(SEMI_TRANS_W);        

        GroupLayout infoPanelLayout = new GroupLayout(infoPanel);
        infoPanel.setLayout(infoPanelLayout);
        infoPanelLayout.setHorizontalGroup(infoPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(infoText)   //Add infoText
            .addGap(0, 230, Short.MAX_VALUE)
        );
        infoPanelLayout.setVerticalGroup(infoPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(infoText)   //Add infoText
            .addGap(0, 0, Short.MAX_VALUE)
        );
        
        appNameLabel.setFont(new Font("Tahoma", Font.BOLD + Font.ITALIC, 24));
        appNameLabel.setForeground(Color.WHITE);   //Set AppName color        
        appNameLabel.setText("App Name");

        Border preBorder = BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2), "Pre-professional");
        prePanel.setBorder(preBorder);
        //prePanel.setBackground(new Color(255, 204, 255));        
        prePanel.setBackground(SEMI_TRANS_W);

        GroupLayout prePanelLayout = new GroupLayout(prePanel);
        prePanel.setLayout(prePanelLayout);
        prePanelLayout.setHorizontalGroup(prePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(prePane)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        prePanelLayout.setVerticalGroup(prePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(prePane)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        Border genBorder = BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2), "General");
        genPanel.setBorder(genBorder);        
        //genPanel.setBackground(new Color(204, 204, 255));
        genPanel.setBackground(SEMI_TRANS_W);

        GroupLayout genPanelLayout = new GroupLayout(genPanel);
        genPanel.setLayout(genPanelLayout);
        genPanelLayout.setHorizontalGroup(genPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(genPane)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        genPanelLayout.setVerticalGroup(genPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(genPane)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        Border proBorder = BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2), "Professional");
        proPanel.setBorder(proBorder);
        //proPanel.setBackground(new Color(204, 255, 204));
        proPanel.setBackground(SEMI_TRANS_W);

        GroupLayout proPanelLayout = new GroupLayout(proPanel);
        proPanel.setLayout(proPanelLayout);
        proPanelLayout.setHorizontalGroup(proPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(proPane)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        proPanelLayout.setVerticalGroup(proPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(proPane)
            .addGap(0, 254, Short.MAX_VALUE)
        );

        //schedPanel.setBackground(new Color(255, 204, 204));
        Border schedBorder = BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), "Schedule");
        schedPanel.setBorder(schedBorder);        
        schedPanel.setBackground(SEMI_TRANS_W);

        GroupLayout schedPanelLayout = new GroupLayout(schedPanel);
        schedPanel.setLayout(schedPanelLayout);
        schedPanelLayout.setHorizontalGroup(schedPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPane)
            .addGap(0, 368, Short.MAX_VALUE)
        );
        schedPanelLayout.setVerticalGroup(schedPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPane)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        summaryPanel.setBackground(new Color(204, 204, 255));
        Border summaryBorder = BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), "Summary");
        summaryPanel.setBorder(summaryBorder);        
        summaryPanel.setBackground(SEMI_TRANS_W);

        GroupLayout summaryPanelLayout = new GroupLayout(summaryPanel);
        summaryPanel.setLayout(summaryPanelLayout);
        summaryPanelLayout.setHorizontalGroup(summaryPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(sumText)          //Add Summary Text
            .addGap(0, 300, Short.MAX_VALUE)    // set size here(W)
        );
        summaryPanelLayout.setVerticalGroup(summaryPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(sumText)          //Add Summary Text
            .addGap(0, 200, Short.MAX_VALUE)    // set size here(H)
        );
                
        URL imageURL = getClass().getResource("files/wallpaper.png");
        JLabel bg = new JLabel(new ImageIcon(imageURL));                            
        add(bg);
        setContentPane(bg);        
        
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(infoPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(prePanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(genPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(proPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(schedPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(summaryPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
                    .addComponent(appNameLabel))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(appNameLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(proPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(genPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(prePanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                            .addComponent(summaryPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(schedPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(infoPanel, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
    }

    @Override
    public void update(Observable o, Object arg) {
        if (masterPlanRef.getSchedulesList().isEmpty()) {
            createInitialSMenuItem.setEnabled(true);
        }else{
            createInitialSMenuItem.setEnabled(false);
        }
    }
    
}
