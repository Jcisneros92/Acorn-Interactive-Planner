/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Comp;

import Support.Schedule;
import Support.ScheduleList;
import java.awt.Font;
import java.util.Calendar;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;

/**
 *
 * @author Test
 */
@SuppressWarnings("serial")
public final class InitialSchedDialog extends JFrame {

    /**
     * Creates new form IniSched
     */
    public InitialSchedDialog() {
        initComponents();
    }


    private void initComponents() {

        jPanel1 = new JPanel();
        SemLabel = new JLabel();
        yearLabel = new JLabel();
        semComBox = new JComboBox<>();
        yearTextField = new JTextField();
        ProceedButton = new JButton();
        CancelButton = new JButton();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Starting Semester");
        setAlwaysOnTop(true);        
        setResizable(false);
        setLocationRelativeTo(null);

        SemLabel.setFont(new Font("Tahoma", 0, 14)); 
        SemLabel.setText("Semester");

        yearLabel.setFont(new Font("Tahoma", 0, 14)); 
        yearLabel.setText("Year");

        semComBox.setFont(new Font("Tahoma", 0, 14)); 
        semComBox.setModel(new DefaultComboBoxModel<>(new String[] { "Spring", "Summer", "Fall"}));

        yearTextField.setFont(new Font("Tahoma", 0, 14)); 
        yearTextField.setText(Integer.toString(Calendar.getInstance().get(Calendar.YEAR)));

        ProceedButton.setFont(new Font("Tahoma", 0, 14)); 
        ProceedButton.setText("Proceed");
        ProceedButton.addActionListener(e -> {
            int yr = Integer.parseInt(yearTextField.getText());
            String semString = semComBox.getSelectedItem().toString();                        
            if ((yr<2018)||(yr>2050)) {
                yearTextField.setText(Integer.toString(Calendar.getInstance().get(Calendar.YEAR)));
            }else{
                switch (semString){
                    case "Spring":
                        ScheduleList.getInstance().createInitialSchedule(Schedule.Semester.SPRING, yr);
                        break;
                    case "Summer":
                        ScheduleList.getInstance().createInitialSchedule(Schedule.Semester.SUMMER, yr);
                        break;
                    default:
                        ScheduleList.getInstance().createInitialSchedule(Schedule.Semester.FALL, yr);
                        break;                                
                }
                dispose();
            }
        });

        CancelButton.setFont(new Font("Tahoma", 0, 14)); 
        CancelButton.setText("Cancel");
        CancelButton.addActionListener(e -> dispose());

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(SemLabel)
                    .addComponent(yearLabel))
                .addGap(44, 44, 44)
                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                    .addComponent(semComBox, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(yearTextField, GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(ProceedButton)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(CancelButton)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(SemLabel)
                    .addComponent(semComBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(yearLabel)
                    .addComponent(yearTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, 22, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(ProceedButton)
                    .addComponent(CancelButton)))
        );

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        setVisible(true);
    }

    // Variables declaration
    private JButton ProceedButton;
    private JButton CancelButton;
    private JComboBox<String> semComBox;
    private JLabel SemLabel;
    private JLabel yearLabel;
    private JPanel jPanel1;
    private JTextField yearTextField;    
    // End of variables declaration
}
