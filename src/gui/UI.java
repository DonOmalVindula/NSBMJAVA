/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;
import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Session;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.PasswordAuthentication;
/**
 *
 * @author Omal
 */
public class UI extends javax.swing.JFrame {
    //variables required for the database operations are defined here
    Connection conn = null;
    PreparedStatement pst = null;
    PreparedStatement pst1 = null;
    ResultSet rs = null;
    ResultSet rs1 = null;

    public UI() {
        initComponents();
        try {
            //Establishes the connection to the database
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3307/course_enroll?","root","assasin");

        } catch (ClassNotFoundException | SQLException e) {
        }
        //These functions are initialized at the beginning of the program to make it available to the user
        updateComboBox_course();
        updateComboBox_course1();
        updateComboBox_lecturer();
        updateComboBox_lecturer2();
        updateComboBox_instructor();
        updateComboBox_subject();
        updateComboBox_subject1();
        updateComboBox_lab();
        updateComboBox_lab1();
        show_student();
        show_course();
        show_staff();
        show_subject();
        show_lab();
        show_mark_table();
        show_leaderboard();
        showDate();
        showTime();
        showUniInfo();

    }
    
    void showDate() //shows current date in the Admin Home
    {
        Date d = new Date();
        SimpleDateFormat sdt = new SimpleDateFormat("yyyy-MM-dd");
        currentDate.setText(sdt.format(d));
    }
    
    void showTime() //shows current time in the Admin Home
    {
        new Timer(0, new ActionListener(){
            
           @Override
           public void actionPerformed(ActionEvent e){
               Date d = new Date();
               SimpleDateFormat sdt1 = new SimpleDateFormat("hh:mm:ss a");
               currentTime.setText(sdt1.format(d));
           }
        }).start();
    }
    
    void showUniInfo() //initialize the data shown in the admin home
    {
        String sql = "SELECT COUNT(student_id) FROM student";
        String sql1 = "SELECT COUNT(staff_id) FROM staff";
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            if (rs.next())
            {
                stu_count.setText(rs.getString("COUNT(student_id)"));
            }
            pst = conn.prepareStatement(sql1);
            rs = pst.executeQuery();
            if (rs.next())
            {
                staff_count.setText(rs.getString("COUNT(staff_id)"));
            }
            uni_rank.setText("3450");
        } catch (SQLException ex) {
            Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    //method to send mails to students
    public String sendMail(String ToEmail,String Subject,String Text){

	String Msg;
    
        final String username = "NSBMJAVA@outlook.com";
        final String password = "Javaproject1";

        Properties props = new Properties();
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.starttls.enable", true);
        props.put("mail.smtp.host", "smtp-mail.outlook.com");
        props.put("mail.smtp.port", "587");

        Session session;
        session = Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));//ur email
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(ToEmail));//u will send to
            message.setSubject(Subject);
            message.setText(Text);
            Transport.send(message);
            Msg="true";
    	    return Msg;

        } catch (MessagingException e) {
            System.out.println(e.toString());
            return e.toString();
        }    
    }
    //method to create the email body of mail
    public void createMail(String stu_id)
    {
        try
        {
            String output = "";
            String sql = "SELECT fname,lname,email FROM student WHERE student_id=?";
            pst = conn.prepareStatement(sql);
            pst.setString(1, stu_id);
            rs = pst.executeQuery();
            if(rs.next())
            {
                stu_report_name.setText(rs.getString("fname")+" "+rs.getString("lname"));
                stu_report_email.setText(rs.getString("email"));
            }
            output += "Hello! "+stu_report_name.getText()+"\n\nYour Results for this semester is as follows,\n\n";
            String sql2 = "SELECT * FROM student_mark WHERE student_id=?";
            pst = conn.prepareStatement(sql2);
            pst.setString(1, stu_id);
            rs = pst.executeQuery();
            while(rs.next())
            {
                if (java.sql.Types.NULL != rs.getInt("mark"))
                {
                    String sql3 = "SELECT sname FROM subject WHERE subject_id=?";
                    pst1 = conn.prepareStatement(sql3);
                    pst1.setString(1, rs.getString("subject_id"));
                    rs1 = pst1.executeQuery();
                    if (rs1.next())
                    {
                        output += rs1.getString("sname")+" : "+rs.getString("mark")+" marks\n";
                    }
                }
            }
            stu_report_body.setText(output);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    
 
    public void initiate_stu_dash() //Initialize student information in the student dashboard
    {
        try {
            String sql = "SELECT * FROM student WHERE student_id=?";
            pst = conn.prepareStatement(sql);
            pst.setInt(1, Integer.parseInt(stu_id_field.getText()));
            rs = pst.executeQuery();
            if (rs.next())
            {
                stu_nic_field.setText(rs.getString("student_id"));
                stu_name_field.setText(rs.getString("fname")+" "+rs.getString("lname"));
                stu_address_field.setText(rs.getString("address"));
                stu_course_field.setText(rs.getString("course_id"));
                stu_join_date_field.setText(rs.getString("date_joined"));
                updateComboBox_stu_subjectlists();
                show_mark_table();
            }

            
        } catch (SQLException ex) {
            Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public ArrayList<Student> studentList() //creating an array list of student objects to populate the student info table
    {
        ArrayList<Student> studentData;
        studentData = new ArrayList<>();
        String sql = "SELECT * FROM student";
        try
        {
        pst = conn.prepareStatement(sql);
        rs = pst.executeQuery();
        Student input;
        while (rs.next())
        {
            input = new Student();
            input.setStudent_id(rs.getInt("student_id"));
            input.setFname(rs.getString("fname"));
            input.setLname(rs.getString("lname"));
            input.setGender(rs.getString("gender"));
            input.setType(rs.getString("type"));
            input.setEnroll_sem(rs.getString("enroll_semester"));
            input.setCourse_id(rs.getInt("course_id"));
            studentData.add(input);
        }
        }
        catch(SQLException e)
        {
            JOptionPane.showMessageDialog(null, e);
        }
        return studentData;
    }
    
    
    public void show_student() //populating the student info table in the admin > student panel
    {
        ArrayList<Student> list = studentList();
        DefaultTableModel model = (DefaultTableModel)stu_jTable.getModel();
        model.setRowCount(0);
        Object[] row = new Object[7];
        for (int i = 0; i < list.size(); i++)
        {
            row[0] = list.get(i).getStudent_id();
            row[1] = list.get(i).getFname();
            row[2] = list.get(i).getLname();
            row[3] = list.get(i).getType();
            row[4] = list.get(i).getCourse_id();
            row[5] = list.get(i).getGender();
            row[6] = list.get(i).getEnroll_sem();
            model.addRow(row);
        }
    }
    
    public ArrayList<Student> undergradMarkList() //getting undergrad leaderboard
    {
        ArrayList<Student> studentData;
        studentData = new ArrayList<>();
        String sql = "SELECT * FROM student WHERE type='Undergrad' ORDER BY gpa DESC";
        try
        {
        pst = conn.prepareStatement(sql);
        rs = pst.executeQuery();
        Student input;
        while (rs.next())
        {
            input = new Student();
            input.setStudent_id(rs.getInt("student_id"));
            input.setFname(rs.getString("fname"));
            input.setLname(rs.getString("lname"));
            input.setGpa(rs.getDouble("gpa"));
            studentData.add(input);
        }
        }
        catch(SQLException e)
        {
            JOptionPane.showMessageDialog(null, e);
        }
        return studentData;
    }
    
    public ArrayList<Student> postgradMarkList() //getting post grad leaderboard
    {
        ArrayList<Student> studentData;
        studentData = new ArrayList<>();
        String sql = "SELECT * FROM student WHERE type='Postgrad' ORDER BY gpa DESC";
        try
        {
        pst = conn.prepareStatement(sql);
        rs = pst.executeQuery();
        Student input;
        while (rs.next())
        {
            input = new Student();
            input.setStudent_id(rs.getInt("student_id"));
            input.setFname(rs.getString("fname"));
            input.setLname(rs.getString("lname"));
            input.setGpa(rs.getDouble("gpa"));
            studentData.add(input);
        }
        }
        catch(SQLException e)
        {
            JOptionPane.showMessageDialog(null, e);
        }
        return studentData;
    }
    
    
    public void show_leaderboard() //populates the 2 leaderboard tables in the admin home panel
    {
        ArrayList<Student> list = undergradMarkList();
        DefaultTableModel model = (DefaultTableModel)under_leaderboard.getModel();
        model.setRowCount(0);
        Object[] row = new Object[5];
        int j = 1;
        for (int i = 0; i < list.size(); i++)
        {
            row[0] = j;
            row[1] = list.get(i).getStudent_id();
            row[2] = list.get(i).getFname();
            row[3] = list.get(i).getLname();
            row[4] = list.get(i).getGpa();
            model.addRow(row);
            j++;
        }
        
        ArrayList<Student> list1 = postgradMarkList();
        DefaultTableModel model1 = (DefaultTableModel)post_leaderboard.getModel();
        model1.setRowCount(0);
        Object[] row1 = new Object[5];
        int k = 1;
        for (int i = 0; i < list1.size(); i++)
        {
            row1[0] = k;
            row1[1] = list1.get(i).getStudent_id();
            row1[2] = list1.get(i).getFname();
            row1[3] = list1.get(i).getLname();
            row1[4] = list1.get(i).getGpa();
            model1.addRow(row1);
            k++;
        }
    }
    
    public ArrayList<Task> task_student_subject_List() //helper function to show task marks of a student
    {
        ArrayList<Task> taskData;
        taskData = new ArrayList<>();
        String sql = "SELECT * FROM subject_task_marks WHERE student_id=? AND subject_id=?";
        try
        {
        pst = conn.prepareStatement(sql);
        pst.setInt(1, Integer.parseInt(mark_stu_id2.getText()));
        String subject_name = (String)task_subject_list.getSelectedItem();
        String[] subject_array = subject_name.split(" ");       
        pst.setInt(2, Integer.parseInt(subject_array[0]));
        rs = pst.executeQuery();
        Task input;
        while (rs.next())
        {
            input = new Task();
            input.setStudent_id(rs.getInt("student_id"));
            input.setSubject_id(rs.getInt("subject_id"));
            input.setTask_id(rs.getString("task_id"));
            input.setTask_type(rs.getString("task_type"));
            input.setTotal_mark(rs.getInt("total_mark"));
            input.setAwarded_mark(rs.getInt("mark_awarded"));
            taskData.add(input);
        }
        }
        catch(SQLException e)
        {
            JOptionPane.showMessageDialog(null, e);
        }
        return taskData;
    }
    
    
    public void show_task_mark_table_subject() //showing task marks on a table
    {
        ArrayList<Task> list = task_student_subject_List();
        DefaultTableModel model = (DefaultTableModel)task_mark_jTable.getModel();
        model.setRowCount(0);
        Object[] row = new Object[5];
        for (int i = 0; i < list.size(); i++)
        {
            row[0] = list.get(i).getTask_id();
            row[1] = list.get(i).getSubject_id();
            row[2] = list.get(i).getTask_type();
            row[3] = list.get(i).getTotal_mark();
            row[4] = list.get(i).getAwarded_mark();
            model.addRow(row);
        }
    }
    
    public ArrayList<Task> task_student_subject_List2() //showing subjects helper function
    {
        ArrayList<Task> taskData;
        taskData = new ArrayList<>();
        String sql = "SELECT * FROM subject_task_marks WHERE student_id=?";
        try
        {
        pst = conn.prepareStatement(sql);
        pst.setInt(1, Integer.parseInt(mark_stu_id2.getText()));
        rs = pst.executeQuery();
        Task input;
        while (rs.next())
        {
            input = new Task();
            input.setStudent_id(rs.getInt("student_id"));
            input.setSubject_id(rs.getInt("subject_id"));
            input.setTask_id(rs.getString("task_id"));
            input.setTask_type(rs.getString("task_type"));
            input.setTotal_mark(rs.getInt("total_mark"));
            input.setAwarded_mark(rs.getInt("mark_awarded"));
            taskData.add(input);
        }
        }
        catch(SQLException e)
        {
            JOptionPane.showMessageDialog(null, e);
        }
        return taskData;
    }
    
    
    public void show_task_mark_table_subject2() //showing subjects helper function
    {
        ArrayList<Task> list = task_student_subject_List2();
        DefaultTableModel model = (DefaultTableModel)task_mark_jTable.getModel();
        model.setRowCount(0);
        Object[] row = new Object[5];
        for (int i = 0; i < list.size(); i++)
        {
            row[0] = list.get(i).getTask_id();
            row[1] = list.get(i).getSubject_id();
            row[2] = list.get(i).getTask_type();
            row[3] = list.get(i).getTotal_mark();
            row[4] = list.get(i).getAwarded_mark();
            model.addRow(row);
        }
    }
    
    public void show_instructor_result() //getting information of a given instructor
    {
        String sql = "SELECT staff.staff_fname, staff.staff_lname, subject_instructor.instructor_id FROM subject_instructor INNER JOIN staff ON staff.staff_id=subject_instructor.instructor_id WHERE subject_instructor.subject_id=?";
        try
        {
            pst = conn.prepareStatement(sql);
            pst.setInt(1, Integer.parseInt(search_subject_id.getText()));
            rs = pst.executeQuery();
            DefaultTableModel model = (DefaultTableModel)inst_res_jTable.getModel();
            model.setRowCount(0);
            String[] row;
            row = new String[1];
            while(rs.next())
            {
                row[0] = rs.getString("staff_fname")+" "+rs.getString("staff_lname");
                model.addRow(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }
    
    //This will update the lab schedule table given a day of week as parameters (Eg: "Sunday" will give Sunday's schedule)
    public void show_lab_schedule(String day)
    {
        String sql = "SELECT * FROM lab_assign WHERE day_of_week=? ORDER BY time ASC";
        try
        {
            pst = conn.prepareStatement(sql);
            pst.setString(1, day);
            rs = pst.executeQuery();
            DefaultTableModel model = (DefaultTableModel)lab_schedule_jTable.getModel();
            model.setRowCount(0);
            String[] row;
            row = new String[3];
            while(rs.next())
            {
                row[0] = rs.getTime("time").toString();
                row[1] = rs.getString("lab_id");
                int sub_id = rs.getInt("subject_id");
                String sql1 = "SELECT * FROM subject WHERE subject_id=?";
                pst1 = conn.prepareStatement(sql1);
                pst1.setInt(1, sub_id);
                rs1 = pst1.executeQuery();
                if(rs1.next())
                {
                row[2] = rs1.getString("sname");
                model.addRow(row);
                }
            }
        }
        catch(SQLException ex)
        {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }
    
    public void show_mark_table() //display marks of a student
    {
        String sql = "SELECT * FROM student_mark WHERE student_id=?";
        try
        {
            pst = conn.prepareStatement(sql);
            pst.setString(1, stu_nic_field.getText());
            rs = pst.executeQuery();
            DefaultTableModel model = (DefaultTableModel)stu_marks_jTable.getModel();
            model.setRowCount(0);
            String[] row;
            row = new String[5];
            while(rs.next())
            {
                String sql1 = "SELECT * FROM subject WHERE subject_id=?";
                pst1 = conn.prepareStatement(sql1);
                pst1.setString(1, rs.getString("subject_id"));
                rs1 = pst1.executeQuery();
                if(rs1.next())
                {  
                    row[0] = rs1.getString("sname");
                }
                row[1] = rs.getString("mark");
                row[2] = rs.getString("year");
                row[3] = rs.getString("semester");
                row[4] = rs.getString("sub_type");
                model.addRow(row);
            }
        }
        catch(SQLException ex)
        {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }
    
    public void show_mark_table2() //
    {
        String sql = "SELECT * FROM student_mark WHERE student_id=?";
        try
        {
            pst = conn.prepareStatement(sql);
            pst.setString(1, mark_stu_id.getText());
            rs = pst.executeQuery();
            DefaultTableModel model = (DefaultTableModel)marks_jTable.getModel();
            model.setRowCount(0);
            String[] row;
            row = new String[5];
            while(rs.next())
            {
                String sql1 = "SELECT * FROM subject WHERE subject_id=?";
                pst1 = conn.prepareStatement(sql1);
                pst1.setString(1, rs.getString("subject_id"));
                rs1 = pst1.executeQuery();
                if(rs1.next())
                {  
                    row[1] = rs1.getString("sname");
                }
                row[0] = rs.getString("subject_id");
                row[2] = rs.getString("mark");
                row[4] = rs.getString("year");
                row[3] = rs.getString("semester");
                model.addRow(row);
            }
        }
        catch(SQLException ex)
        {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }
    

    
    public ArrayList<Course> courseList()
    {
        ArrayList<Course> courseData;
        courseData = new ArrayList<>();
        String sql = "SELECT * FROM course";
        try
        {
        pst = conn.prepareStatement(sql);
        rs = pst.executeQuery();
        Course input;
        while (rs.next())
        {
            input = new Course();
            input.setCourse_id(rs.getInt("course_id"));
            input.setCourse_name(rs.getString("co_name"));
            input.setCourse_duration(rs.getInt("duration"));
            input.setCourse_credits(rs.getInt("credits_to_complete"));
            courseData.add(input);
        }
        }
        catch(SQLException e)
        {
            JOptionPane.showMessageDialog(null, e);
        }
        return courseData;
    }
    
    public void show_course() //showing all the courses available
    {
        ArrayList<Course> list = courseList();
        DefaultTableModel model = (DefaultTableModel)course_jTable.getModel();
        model.setRowCount(0);
        Object[] row = new Object[4];
        for (int i = 0; i < list.size(); i++)
        {
            row[0] = list.get(i).getCourse_id();
            row[1] = list.get(i).getCourse_name();
            row[2] = list.get(i).getCourse_duration();
            row[3] = list.get(i).getCourse_credits();
            model.addRow(row);
        }
    }
    
    public ArrayList<Lab> labList()
    {
        ArrayList<Lab> labData;
        labData = new ArrayList<>();
        String sql = "SELECT * FROM lab";
        try
        {
        pst = conn.prepareStatement(sql);
        rs = pst.executeQuery();
        Lab input;
        while (rs.next())
        {
            input = new Lab();
            input.setLab_id(rs.getString("lab_id"));
            input.setLab_floor(rs.getString("lab_floor"));
            input.setLab_build(rs.getString("lab_building"));
            labData.add(input);
        }
        }
        catch(SQLException e)
        {
            JOptionPane.showMessageDialog(null, e);
        }
        return labData;
    }
    
    public void show_lab() //showing all the labs available
    {
        ArrayList<Lab> list = labList();
        DefaultTableModel model = (DefaultTableModel)lab_jTable.getModel();
        model.setRowCount(0);
        Object[] row = new Object[3];
        for (int i = 0; i < list.size(); i++)
        {
            row[0] = list.get(i).getLab_id();
            row[1] = list.get(i).getLab_build();
            row[2] = list.get(i).getLab_floor();
            model.addRow(row);
        }
    }
    
    public ArrayList<Staff> staffList()
    {
        ArrayList<Staff> staffData;
        staffData = new ArrayList<>();
        String sql = "SELECT * FROM staff";
        try
        {
        pst = conn.prepareStatement(sql);
        rs = pst.executeQuery();
        Staff input;
        while (rs.next())
        {
            input = new Staff();
            input.setStaff_id(rs.getInt("staff_id"));
            input.setFname(rs.getString("staff_fname"));
            input.setLname(rs.getString("staff_lname"));
            input.setDesignation(rs.getString("staff_designation"));
            input.setQualification(rs.getString("staff_qualification"));
            input.setGender(rs.getString("staff_gender"));  
            staffData.add(input);
        }
        }
        catch(SQLException e)
        {
            JOptionPane.showMessageDialog(null, e);
        }
        return staffData;
    }
    
    public void show_staff() //showing staff details
    {
        ArrayList<Staff> list = staffList();
        DefaultTableModel model = (DefaultTableModel)staff_jTable.getModel();
        model.setRowCount(0);
        Object[] row = new Object[6];
        for (int i = 0; i < list.size(); i++)
        {
            row[0] = list.get(i).getStaff_id();
            row[1] = list.get(i).getFname();
            row[2] = list.get(i).getLname();
            row[3] = list.get(i).getDesignation();
            row[4] = list.get(i).getQualification();
            row[5] = list.get(i).getGender();
            model.addRow(row);
        }
    }
    
    public ArrayList<Subject> subjectList()
    {
        ArrayList<Subject> subjectData;
        subjectData = new ArrayList<>();
        String sql = "SELECT * FROM subject";
        try
        {
        pst = conn.prepareStatement(sql);
        rs = pst.executeQuery();
        Subject input;
        while (rs.next())
        {
            input = new Subject();
            input.setSubject_code(rs.getInt("subject_id"));
            input.setSubject_name(rs.getString("sname"));
            input.setDuration(rs.getInt("sduration"));
            input.setSubject_credits(rs.getInt("credits_awarded"));
            input.setSubject_fee(rs.getDouble("sfee"));
            input.setSubject_course(rs.getInt("course_id"));
            subjectData.add(input);
        }
        }
        catch(SQLException e)
        {
            JOptionPane.showMessageDialog(null, e);
        }
        return subjectData;
    }
    
    public void show_subject() //showing subject details
    {
        ArrayList<Subject> list = subjectList();
        DefaultTableModel model = (DefaultTableModel)subject_jTable.getModel();
        model.setRowCount(0);
        Object[] row = new Object[6];
        for (int i = 0; i < list.size(); i++)
        {
            row[0] = list.get(i).getSubject_code();
            row[1] = list.get(i).getSubject_name();
            row[2] = list.get(i).getDuration();
            row[3] = list.get(i).getSubject_credits();
            row[4] = list.get(i).getSubject_fee();
            row[5] = list.get(i).getSubject_course();
            model.addRow(row);
        }
    }
    
    /*this method will calculate and return the GPA of a student given there student ID
    it will get all the marks from all subjects and their credits and will calculate something similar
    to a weighted average mark by using credits for each and every student which help in ranking them 
    accoring to their performance
    */    
    public double calcGPA(String stu_id)
    {
        double GPA = 0;
        int weightedCredit = 0;
        int totalCredit = 0;
        try
        {
            String sql = "SELECT * FROM student_mark WHERE student_id=?";
            String sql1 = "SELECT credits_awarded FROM subject WHERE subject_id=?";
            pst = conn.prepareStatement(sql);
            pst1 = conn.prepareStatement(sql1);
            pst.setString(1, stu_id);
            rs = pst.executeQuery();
            while (rs.next())
            {
                pst1.setString(1, rs.getString("subject_id"));
                rs1 = pst1.executeQuery();
                if (rs1.next())
                {
                    totalCredit += rs1.getInt("credits_awarded");
                    weightedCredit += (rs1.getInt("credits_awarded"))*(rs.getInt("mark"));
                }
            }
            GPA = weightedCredit/totalCredit;            
        }
        catch (SQLException e)
        {
            e.getMessage();
        }
        return GPA;
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        entityManager = java.beans.Beans.isDesignTime() ? null : javax.persistence.Persistence.createEntityManagerFactory("course_enroll?zeroDateTimeBehavior=convertToNullPU").createEntityManager();
        main_back_pane = new javax.swing.JPanel();
        login_pane = new javax.swing.JPanel();
        jLabel85 = new javax.swing.JLabel();
        jLabel86 = new javax.swing.JLabel();
        login_details_init = new javax.swing.JPanel();
        login_details_select = new javax.swing.JPanel();
        jLabel102 = new javax.swing.JLabel();
        student_login_btn = new javax.swing.JLabel();
        admin_login_btn = new javax.swing.JLabel();
        app_exit_btn = new javax.swing.JLabel();
        admin_login = new javax.swing.JPanel();
        jLabel100 = new javax.swing.JLabel();
        admin_field_line = new javax.swing.JSeparator();
        admin_pass_line = new javax.swing.JSeparator();
        admin_id_field = new javax.swing.JTextField();
        admin_pass_field = new javax.swing.JPasswordField();
        jLabel103 = new javax.swing.JLabel();
        jLabel104 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        login_btn = new javax.swing.JLabel();
        login_cancel_btn = new javax.swing.JLabel();
        student_login = new javax.swing.JPanel();
        jLabel105 = new javax.swing.JLabel();
        stu_id_field = new javax.swing.JTextField();
        admin_field_line1 = new javax.swing.JSeparator();
        stu_login_btn = new javax.swing.JLabel();
        stu_login_cancel_btn = new javax.swing.JLabel();
        jLabel106 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        admin_pane = new javax.swing.JPanel();
        top_menu = new javax.swing.JPanel();
        stud_btn = new javax.swing.JLabel();
        sub_btn = new javax.swing.JLabel();
        cour_btn = new javax.swing.JLabel();
        staff_btn = new javax.swing.JLabel();
        home_btn = new javax.swing.JLabel();
        user = new javax.swing.JLabel();
        lab_btn = new javax.swing.JLabel();
        mark_btn = new javax.swing.JLabel();
        bottom_panel = new javax.swing.JPanel();
        home = new javax.swing.JPanel();
        jPanel20 = new javax.swing.JPanel();
        currentDate = new javax.swing.JLabel();
        under_leader_panel = new javax.swing.JPanel();
        jScrollPane11 = new javax.swing.JScrollPane();
        under_leaderboard = new javax.swing.JTable();
        jLabel151 = new javax.swing.JLabel();
        post_leader_panel = new javax.swing.JPanel();
        jScrollPane12 = new javax.swing.JScrollPane();
        post_leaderboard = new javax.swing.JTable();
        jLabel152 = new javax.swing.JLabel();
        currentTime = new javax.swing.JLabel();
        jLabel150 = new javax.swing.JLabel();
        jLabel153 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        currentUser = new javax.swing.JLabel();
        jLabel155 = new javax.swing.JLabel();
        jLabel154 = new javax.swing.JLabel();
        jLabel156 = new javax.swing.JLabel();
        jLabel157 = new javax.swing.JLabel();
        stu_count = new javax.swing.JLabel();
        staff_count = new javax.swing.JLabel();
        uni_rank = new javax.swing.JLabel();
        student = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        fname = new javax.swing.JTextField();
        lname = new javax.swing.JTextField();
        telephone = new javax.swing.JTextField();
        email = new javax.swing.JTextField();
        address = new javax.swing.JTextField();
        gender = new javax.swing.JComboBox<>();
        semester = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        grad_select = new javax.swing.JPanel();
        initpanel = new javax.swing.JPanel();
        postgrad = new javax.swing.JPanel();
        qualification = new javax.swing.JTextField();
        institute = new javax.swing.JTextField();
        completed_year = new javax.swing.JTextField();
        undergrad = new javax.swing.JPanel();
        gk_result = new javax.swing.JTextField();
        result_1 = new javax.swing.JTextField();
        result_2 = new javax.swing.JTextField();
        result_3 = new javax.swing.JTextField();
        eng_result = new javax.swing.JTextField();
        alvl_island_rank = new javax.swing.JTextField();
        student_reg_btn = new javax.swing.JButton();
        student_reg_cancel_btn = new javax.swing.JButton();
        course_id = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        Undergraduate = new javax.swing.JToggleButton();
        Postgraduate = new javax.swing.JToggleButton();
        jLabel46 = new javax.swing.JLabel();
        student_id = new javax.swing.JTextField();
        birthday_selector = new com.toedter.calendar.JDateChooser();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        search_id = new javax.swing.JTextField();
        search_student_btn = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        stu_jTable = new javax.swing.JTable();
        mail_report_btn = new javax.swing.JButton();
        stu_search_panel = new javax.swing.JPanel();
        student_init_panel = new javax.swing.JPanel();
        under_result_panel = new javax.swing.JPanel();
        address_field1 = new javax.swing.JTextField();
        fname_field1 = new javax.swing.JTextField();
        eng_res1 = new javax.swing.JTextField();
        sub3_res1 = new javax.swing.JTextField();
        sub2_res1 = new javax.swing.JTextField();
        sub1_res1 = new javax.swing.JTextField();
        gender_field1 = new javax.swing.JTextField();
        lname_field1 = new javax.swing.JTextField();
        gk_res1 = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        semester_field1 = new javax.swing.JTextField();
        email_field1 = new javax.swing.JTextField();
        tele_field1 = new javax.swing.JTextField();
        jLabel38 = new javax.swing.JLabel();
        course_field1 = new javax.swing.JTextField();
        jLabel39 = new javax.swing.JLabel();
        type_field1 = new javax.swing.JTextField();
        jLabel40 = new javax.swing.JLabel();
        dob_field1 = new javax.swing.JTextField();
        jLabel41 = new javax.swing.JLabel();
        rank_res1 = new javax.swing.JTextField();
        jLabel42 = new javax.swing.JLabel();
        update_undergrad_btn = new javax.swing.JButton();
        delete_undergrad_btn = new javax.swing.JButton();
        make_pay_field1 = new javax.swing.JTextField();
        out_pay_field1 = new javax.swing.JTextField();
        jLabel169 = new javax.swing.JLabel();
        jLabel170 = new javax.swing.JLabel();
        under_payment_btn = new javax.swing.JButton();
        post_result_panel = new javax.swing.JPanel();
        address_field2 = new javax.swing.JTextField();
        fname_field2 = new javax.swing.JTextField();
        institute_res = new javax.swing.JTextField();
        qualification_res = new javax.swing.JTextField();
        complete_year_res = new javax.swing.JTextField();
        gender_field2 = new javax.swing.JTextField();
        lname_field2 = new javax.swing.JTextField();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        semester_field2 = new javax.swing.JTextField();
        email_field2 = new javax.swing.JTextField();
        tele_field2 = new javax.swing.JTextField();
        jLabel54 = new javax.swing.JLabel();
        course_field2 = new javax.swing.JTextField();
        jLabel55 = new javax.swing.JLabel();
        type_field2 = new javax.swing.JTextField();
        jLabel56 = new javax.swing.JLabel();
        dob_field2 = new javax.swing.JTextField();
        jLabel57 = new javax.swing.JLabel();
        update_postgrad_btn = new javax.swing.JButton();
        delete_postgrad_btn = new javax.swing.JButton();
        out_pay_field = new javax.swing.JTextField();
        make_pay_field = new javax.swing.JTextField();
        jLabel167 = new javax.swing.JLabel();
        jLabel168 = new javax.swing.JLabel();
        post_payment_btn = new javax.swing.JButton();
        mail_report_panel = new javax.swing.JPanel();
        stu_report_name = new javax.swing.JTextField();
        jLabel164 = new javax.swing.JLabel();
        stu_report_email = new javax.swing.JTextField();
        jLabel165 = new javax.swing.JLabel();
        stu_report_subject = new javax.swing.JTextField();
        jLabel166 = new javax.swing.JLabel();
        jScrollPane13 = new javax.swing.JScrollPane();
        stu_report_body = new javax.swing.JTextArea();
        send_mail_btn = new javax.swing.JButton();
        cancel_mail_btn = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        subject = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        subject_name = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        instructor_select = new javax.swing.JPanel();
        instructor_init_panel = new javax.swing.JPanel();
        add_instructor_panel = new javax.swing.JPanel();
        instructor_list1 = new javax.swing.JComboBox<>();
        add_instructor_btn = new javax.swing.JButton();
        subject_list1 = new javax.swing.JComboBox<>();
        jLabel77 = new javax.swing.JLabel();
        jLabel78 = new javax.swing.JLabel();
        subject_reg_btn = new javax.swing.JButton();
        subject_reg_cancel_btn = new javax.swing.JButton();
        jLabel88 = new javax.swing.JLabel();
        jLabel93 = new javax.swing.JLabel();
        jLabel94 = new javax.swing.JLabel();
        jLabel95 = new javax.swing.JLabel();
        subject_id_enter = new javax.swing.JTextField();
        lecturer_list = new javax.swing.JComboBox<>();
        duration_list = new javax.swing.JComboBox<>();
        jLabel96 = new javax.swing.JLabel();
        subject_fee = new javax.swing.JTextField();
        jLabel97 = new javax.swing.JLabel();
        subject_credits = new javax.swing.JTextField();
        add_instructor_show_btn = new javax.swing.JToggleButton();
        course_list1 = new javax.swing.JComboBox<>();
        jLabel89 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel70 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        update_subject_table = new javax.swing.JButton();
        search_subject_id = new javax.swing.JTextField();
        search_subject_btn = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        subject_jTable = new javax.swing.JTable();
        subject_search_panel = new javax.swing.JPanel();
        subject_init_panel = new javax.swing.JPanel();
        subject_result_panel = new javax.swing.JPanel();
        subject_name_field = new javax.swing.JTextField();
        subject_lecturer_field = new javax.swing.JTextField();
        jLabel72 = new javax.swing.JLabel();
        jLabel73 = new javax.swing.JLabel();
        update_subject_btn = new javax.swing.JButton();
        delete_subject_btn = new javax.swing.JButton();
        subject_duration_field = new javax.swing.JTextField();
        subject_fee_field = new javax.swing.JTextField();
        subject_credit_field = new javax.swing.JTextField();
        jLabel74 = new javax.swing.JLabel();
        jLabel75 = new javax.swing.JLabel();
        jLabel76 = new javax.swing.JLabel();
        duration_list2 = new javax.swing.JComboBox<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        inst_res_jTable = new javax.swing.JTable();
        lecturer_list2 = new javax.swing.JComboBox<>();
        course = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        search_course_id = new javax.swing.JTextField();
        search_course_btn = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        course_jTable = new javax.swing.JTable();
        course_search_panel = new javax.swing.JPanel();
        course_init_panel = new javax.swing.JPanel();
        course_result_panel = new javax.swing.JPanel();
        course_name_field = new javax.swing.JTextField();
        course_credit_field = new javax.swing.JTextField();
        course_duration_field = new javax.swing.JTextField();
        jLabel62 = new javax.swing.JLabel();
        jLabel63 = new javax.swing.JLabel();
        jLabel65 = new javax.swing.JLabel();
        update_course_btn = new javax.swing.JButton();
        delete_course_btn = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        course_name = new javax.swing.JTextField();
        course_duration = new javax.swing.JTextField();
        course_credits = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        grad_select1 = new javax.swing.JPanel();
        course_reg_btn = new javax.swing.JButton();
        course_reg_cancel_btn = new javax.swing.JButton();
        jLabel87 = new javax.swing.JLabel();
        jLabel90 = new javax.swing.JLabel();
        jLabel91 = new javax.swing.JLabel();
        jLabel92 = new javax.swing.JLabel();
        course_id_enter = new javax.swing.JTextField();
        staff = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        search_staff_id = new javax.swing.JTextField();
        search_staff_btn = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        staff_jTable = new javax.swing.JTable();
        staff_search_panel = new javax.swing.JPanel();
        staff_init_panel = new javax.swing.JPanel();
        staff_result_panel = new javax.swing.JPanel();
        staff_fname_field = new javax.swing.JTextField();
        staff_lname_field = new javax.swing.JTextField();
        staff_designation_field = new javax.swing.JTextField();
        jLabel64 = new javax.swing.JLabel();
        jLabel66 = new javax.swing.JLabel();
        update_staff_btn = new javax.swing.JButton();
        delete_staff_btn = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        staff_address_field = new javax.swing.JTextField();
        staff_dob_field = new javax.swing.JTextField();
        staff_tele_field = new javax.swing.JTextField();
        staff_gender_field = new javax.swing.JTextField();
        staff_email_field = new javax.swing.JTextField();
        staff_qualification_field = new javax.swing.JTextField();
        staff_joindate_field = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jLabel67 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        fname_staff = new javax.swing.JTextField();
        lname_staff = new javax.swing.JTextField();
        telephone_staff = new javax.swing.JTextField();
        email_staff = new javax.swing.JTextField();
        address_staff = new javax.swing.JTextField();
        gender_staff = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        designation_staff = new javax.swing.JComboBox<>();
        staff_reg_btn = new javax.swing.JButton();
        staff_reg_cancel_btn = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jLabel58 = new javax.swing.JLabel();
        jLabel59 = new javax.swing.JLabel();
        jLabel60 = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        jLabel68 = new javax.swing.JLabel();
        jLabel69 = new javax.swing.JLabel();
        staff_id = new javax.swing.JTextField();
        birthday_selector_staff = new com.toedter.calendar.JDateChooser();
        qualification_staff = new javax.swing.JTextField();
        lab = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jLabel71 = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();
        assign_lab_show_btn = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        lab_schedule_jTable = new javax.swing.JTable();
        select_date = new javax.swing.JComboBox<>();
        get_schedule_btn = new javax.swing.JButton();
        schedule_day = new javax.swing.JLabel();
        get_today_schedule_btn = new javax.swing.JButton();
        lab_main_panel = new javax.swing.JPanel();
        lab_init_panel = new javax.swing.JPanel();
        lab_assign_panel = new javax.swing.JPanel();
        jLabel79 = new javax.swing.JLabel();
        jLabel80 = new javax.swing.JLabel();
        jLabel81 = new javax.swing.JLabel();
        jLabel83 = new javax.swing.JLabel();
        lab_id_list = new javax.swing.JComboBox<>();
        lab_subject_list = new javax.swing.JComboBox<>();
        lab_date_list = new javax.swing.JComboBox<>();
        lab_time = new com.github.lgooddatepicker.components.TimePicker();
        assign_session_btn = new javax.swing.JButton();
        delete_session_btn = new javax.swing.JButton();
        cancel_session_btn = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        lab_building = new javax.swing.JTextField();
        lab_floor = new javax.swing.JTextField();
        jLabel82 = new javax.swing.JLabel();
        add_lab_btn = new javax.swing.JButton();
        lab_cancel_btn = new javax.swing.JButton();
        jLabel98 = new javax.swing.JLabel();
        jLabel99 = new javax.swing.JLabel();
        jLabel101 = new javax.swing.JLabel();
        lab_id_enter = new javax.swing.JTextField();
        jPanel16 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        lab_jTable = new javax.swing.JTable();
        jLabel84 = new javax.swing.JLabel();
        lab_delete_main = new javax.swing.JPanel();
        lab_delete_init = new javax.swing.JPanel();
        lab_delete_result = new javax.swing.JPanel();
        delete_lab_btn = new javax.swing.JButton();
        delete_lab_cancel_btn = new javax.swing.JButton();
        delete_lab_list = new javax.swing.JComboBox<>();
        delete_lab_panel_btn = new javax.swing.JButton();
        mark = new javax.swing.JPanel();
        add_mark_panel = new javax.swing.JPanel();
        jLabel112 = new javax.swing.JLabel();
        mark_options_pane = new javax.swing.JPanel();
        mark_options_init = new javax.swing.JPanel();
        mark_options_select = new javax.swing.JPanel();
        jLabel113 = new javax.swing.JLabel();
        task_type_list = new javax.swing.JComboBox<>();
        task_id_field = new javax.swing.JTextField();
        total_mark_field = new javax.swing.JTextField();
        awarded_mark_field = new javax.swing.JTextField();
        jLabel116 = new javax.swing.JLabel();
        jLabel117 = new javax.swing.JLabel();
        add_task_mark_btn = new javax.swing.JButton();
        jLabel135 = new javax.swing.JLabel();
        jLabel138 = new javax.swing.JLabel();
        add_task_mark_btn1 = new javax.swing.JButton();
        mark_stu_id2 = new javax.swing.JTextField();
        student_mark_option = new javax.swing.JButton();
        stu_sub_mark_panel = new javax.swing.JPanel();
        stu_sub_mark_init = new javax.swing.JPanel();
        stu_sub_mark_select = new javax.swing.JPanel();
        jLabel137 = new javax.swing.JLabel();
        task_subject_list = new javax.swing.JComboBox<>();
        show_mark_pane_btn = new javax.swing.JButton();
        show_mark_table_btn = new javax.swing.JButton();
        delete_task_panel = new javax.swing.JPanel();
        delete_task_init = new javax.swing.JPanel();
        delete_task_show = new javax.swing.JPanel();
        jLabel142 = new javax.swing.JLabel();
        delete_task_field = new javax.swing.JTextField();
        delete_task_btn = new javax.swing.JButton();
        jLabel143 = new javax.swing.JLabel();
        delete_cancel_btn = new javax.swing.JButton();
        task_mark_panel = new javax.swing.JPanel();
        stu_task_init = new javax.swing.JPanel();
        stu_task_mark = new javax.swing.JPanel();
        jScrollPane9 = new javax.swing.JScrollPane();
        task_mark_jTable = new javax.swing.JTable();
        jLabel141 = new javax.swing.JLabel();
        show_all_task_marks = new javax.swing.JButton();
        show_delete_task_pane = new javax.swing.JButton();
        view_mark_panel = new javax.swing.JPanel();
        jLabel136 = new javax.swing.JLabel();
        get_mark_btn = new javax.swing.JButton();
        cancel_mark_btn = new javax.swing.JButton();
        jLabel139 = new javax.swing.JLabel();
        mark_stu_id = new javax.swing.JTextField();
        marks_pane = new javax.swing.JPanel();
        jScrollPane10 = new javax.swing.JScrollPane();
        marks_jTable = new javax.swing.JTable();
        jLabel140 = new javax.swing.JLabel();
        logout = new javax.swing.JPanel();
        logout_display = new javax.swing.JPanel();
        logout_init_pane = new javax.swing.JPanel();
        change_pw_pane = new javax.swing.JPanel();
        current_pw_field = new javax.swing.JTextField();
        new_pw_field = new javax.swing.JTextField();
        confirm_pw_field = new javax.swing.JTextField();
        jLabel158 = new javax.swing.JLabel();
        jLabel159 = new javax.swing.JLabel();
        jLabel160 = new javax.swing.JLabel();
        pw_change_btn = new javax.swing.JButton();
        cancel_pw_btn = new javax.swing.JButton();
        new_admin_pane = new javax.swing.JPanel();
        current_pw_field1 = new javax.swing.JTextField();
        new_admin_field = new javax.swing.JTextField();
        new_admin_pw_field = new javax.swing.JTextField();
        jLabel161 = new javax.swing.JLabel();
        jLabel162 = new javax.swing.JLabel();
        jLabel163 = new javax.swing.JLabel();
        add_admin_btn = new javax.swing.JButton();
        cancel_admin_btn = new javax.swing.JButton();
        logout_pane = new javax.swing.JPanel();
        logout_btn = new javax.swing.JButton();
        new_admin_btn = new javax.swing.JButton();
        change_pw_btn = new javax.swing.JButton();
        student_pane = new javax.swing.JPanel();
        stu_detail_pane = new javax.swing.JPanel();
        jLabel107 = new javax.swing.JLabel();
        stu_fname_field = new javax.swing.JLabel();
        jLabel127 = new javax.swing.JLabel();
        jLabel128 = new javax.swing.JLabel();
        jLabel129 = new javax.swing.JLabel();
        jLabel130 = new javax.swing.JLabel();
        jLabel131 = new javax.swing.JLabel();
        jLabel132 = new javax.swing.JLabel();
        jLabel133 = new javax.swing.JLabel();
        jLabel134 = new javax.swing.JLabel();
        stu_nic_field = new javax.swing.JTextField();
        stu_name_field = new javax.swing.JTextField();
        stu_address_field = new javax.swing.JTextField();
        stu_course_field = new javax.swing.JTextField();
        stu_join_date_field = new javax.swing.JTextField();
        stu_gpa_field = new javax.swing.JTextField();
        stu_rank_field = new javax.swing.JTextField();
        jPanel18 = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        stu_marks_jTable = new javax.swing.JTable();
        stu_join_date_field1 = new javax.swing.JTextField();
        stu_logout_btn = new javax.swing.JButton();
        stu_subject_select_pane = new javax.swing.JPanel();
        jLabel114 = new javax.swing.JLabel();
        sub_select_pane = new javax.swing.JPanel();
        sub_select_init_pane = new javax.swing.JPanel();
        jLabel111 = new javax.swing.JLabel();
        sub_select_res_pane = new javax.swing.JPanel();
        opt_sub_pane = new javax.swing.JPanel();
        jLabel118 = new javax.swing.JLabel();
        jLabel123 = new javax.swing.JLabel();
        opt_sub1_list = new javax.swing.JComboBox<>();
        opt_sub2_list = new javax.swing.JComboBox<>();
        jLabel124 = new javax.swing.JLabel();
        jLabel125 = new javax.swing.JLabel();
        opt_sub3_list = new javax.swing.JComboBox<>();
        opt_sub4_list = new javax.swing.JComboBox<>();
        jLabel126 = new javax.swing.JLabel();
        comp_sub_pane = new javax.swing.JPanel();
        jLabel115 = new javax.swing.JLabel();
        jLabel119 = new javax.swing.JLabel();
        jLabel120 = new javax.swing.JLabel();
        jLabel121 = new javax.swing.JLabel();
        jLabel122 = new javax.swing.JLabel();
        sub1_list = new javax.swing.JComboBox<>();
        sub2_list = new javax.swing.JComboBox<>();
        sub3_list = new javax.swing.JComboBox<>();
        sub4_list = new javax.swing.JComboBox<>();
        jLabel108 = new javax.swing.JLabel();
        jLabel109 = new javax.swing.JLabel();
        jLabel110 = new javax.swing.JLabel();
        change_student_subject = new javax.swing.JButton();
        register_student_subject = new javax.swing.JButton();
        chk_credits_btn = new javax.swing.JButton();
        feb_sem_btn = new javax.swing.JToggleButton();
        jul_sem_btn = new javax.swing.JToggleButton();
        credit_panel = new javax.swing.JPanel();
        credit_pane_show = new javax.swing.JPanel();
        jLabel144 = new javax.swing.JLabel();
        credits_per_year_field = new javax.swing.JTextField();
        jLabel145 = new javax.swing.JLabel();
        credit_gained_field = new javax.swing.JTextField();
        jLabel146 = new javax.swing.JLabel();
        credit_needed_field = new javax.swing.JTextField();
        jLabel147 = new javax.swing.JLabel();
        jLabel148 = new javax.swing.JLabel();
        jLabel149 = new javax.swing.JLabel();
        total_selection_fee = new javax.swing.JTextField();
        outstanding_fee = new javax.swing.JTextField();
        fee_payable = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        main_back_pane.setPreferredSize(new java.awt.Dimension(1280, 720));
        main_back_pane.setLayout(new java.awt.CardLayout());

        login_pane.setBackground(new java.awt.Color(51, 51, 51));

        jLabel85.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel85.setText("NSBM Student Enrollment System");
        jLabel85.setFont(new java.awt.Font("Raleway Black", 0, 48)); // NOI18N
        jLabel85.setForeground(new java.awt.Color(255, 255, 255));

        jLabel86.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel86.setText("Welcome");
        jLabel86.setFont(new java.awt.Font("Raleway", 0, 36)); // NOI18N
        jLabel86.setForeground(new java.awt.Color(204, 204, 204));

        login_details_init.setBackground(new java.awt.Color(51, 51, 51));
        login_details_init.setForeground(new java.awt.Color(102, 102, 102));
        login_details_init.setLayout(new java.awt.CardLayout());

        login_details_select.setBackground(new java.awt.Color(51, 51, 51));
        login_details_select.setForeground(new java.awt.Color(102, 102, 102));

        jLabel102.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel102.setText("Please Select an Option");
        jLabel102.setFont(new java.awt.Font("Raleway", 0, 24)); // NOI18N
        jLabel102.setForeground(new java.awt.Color(153, 153, 153));

        student_login_btn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        student_login_btn.setText("Student Login");
        student_login_btn.setBackground(new java.awt.Color(204, 204, 204));
        student_login_btn.setFont(new java.awt.Font("Raleway", 0, 18)); // NOI18N
        student_login_btn.setOpaque(true);
        student_login_btn.setPreferredSize(new java.awt.Dimension(108, 22));
        student_login_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                student_login_btnMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                student_login_btnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                student_login_btnMouseExited(evt);
            }
        });

        admin_login_btn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        admin_login_btn.setText("Admin Login");
        admin_login_btn.setBackground(new java.awt.Color(204, 204, 204));
        admin_login_btn.setFont(new java.awt.Font("Raleway", 0, 18)); // NOI18N
        admin_login_btn.setOpaque(true);
        admin_login_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                admin_login_btnMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                admin_login_btnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                admin_login_btnMouseExited(evt);
            }
        });

        app_exit_btn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        app_exit_btn.setText("Exit");
        app_exit_btn.setBackground(new java.awt.Color(204, 204, 204));
        app_exit_btn.setFont(new java.awt.Font("Raleway", 0, 18)); // NOI18N
        app_exit_btn.setOpaque(true);
        app_exit_btn.setPreferredSize(new java.awt.Dimension(108, 22));
        app_exit_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                app_exit_btnMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                app_exit_btnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                app_exit_btnMouseExited(evt);
            }
        });

        javax.swing.GroupLayout login_details_selectLayout = new javax.swing.GroupLayout(login_details_select);
        login_details_select.setLayout(login_details_selectLayout);
        login_details_selectLayout.setHorizontalGroup(
            login_details_selectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel102, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(login_details_selectLayout.createSequentialGroup()
                .addGap(513, 513, 513)
                .addGroup(login_details_selectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(app_exit_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(student_login_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(admin_login_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(495, Short.MAX_VALUE))
        );
        login_details_selectLayout.setVerticalGroup(
            login_details_selectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(login_details_selectLayout.createSequentialGroup()
                .addGap(76, 76, 76)
                .addComponent(jLabel102)
                .addGap(38, 38, 38)
                .addComponent(admin_login_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addComponent(student_login_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 113, Short.MAX_VALUE)
                .addComponent(app_exit_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(45, 45, 45))
        );

        login_details_init.add(login_details_select, "card2");

        admin_login.setBackground(new java.awt.Color(51, 51, 51));

        jLabel100.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel100.setText("Administrator Login");
        jLabel100.setFont(new java.awt.Font("Raleway Medium", 0, 36)); // NOI18N
        jLabel100.setForeground(new java.awt.Color(255, 255, 255));

        admin_id_field.setText("Enter Admin ID");
        admin_id_field.setBackground(new java.awt.Color(51, 51, 51));
        admin_id_field.setBorder(null);
        admin_id_field.setForeground(new java.awt.Color(153, 153, 153));
        admin_id_field.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                admin_id_fieldMouseClicked(evt);
            }
        });

        admin_pass_field.setText("jPasswordField1");
        admin_pass_field.setBackground(new java.awt.Color(51, 51, 51));
        admin_pass_field.setBorder(null);
        admin_pass_field.setForeground(new java.awt.Color(204, 204, 204));
        admin_pass_field.setPreferredSize(new java.awt.Dimension(86, 16));
        admin_pass_field.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                admin_pass_fieldMouseClicked(evt);
            }
        });

        jLabel103.setText("Password");
        jLabel103.setFont(new java.awt.Font("Raleway Medium", 0, 18)); // NOI18N
        jLabel103.setForeground(new java.awt.Color(255, 255, 255));

        jLabel104.setText("Administrator ID");
        jLabel104.setFont(new java.awt.Font("Raleway Medium", 0, 18)); // NOI18N
        jLabel104.setForeground(new java.awt.Color(255, 255, 255));

        login_btn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        login_btn.setText("Log In");
        login_btn.setBackground(new java.awt.Color(153, 153, 153));
        login_btn.setFont(new java.awt.Font("Raleway Black", 0, 24)); // NOI18N
        login_btn.setForeground(new java.awt.Color(51, 51, 51));
        login_btn.setOpaque(true);
        login_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                login_btnMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                login_btnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                login_btnMouseExited(evt);
            }
        });

        login_cancel_btn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        login_cancel_btn.setText("Cancel");
        login_cancel_btn.setBackground(new java.awt.Color(153, 153, 153));
        login_cancel_btn.setFont(new java.awt.Font("Raleway Black", 0, 24)); // NOI18N
        login_cancel_btn.setForeground(new java.awt.Color(51, 51, 51));
        login_cancel_btn.setOpaque(true);
        login_cancel_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                login_cancel_btnMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                login_cancel_btnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                login_cancel_btnMouseExited(evt);
            }
        });

        javax.swing.GroupLayout admin_loginLayout = new javax.swing.GroupLayout(admin_login);
        admin_login.setLayout(admin_loginLayout);
        admin_loginLayout.setHorizontalGroup(
            admin_loginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel100, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(admin_loginLayout.createSequentialGroup()
                .addGap(510, 510, 510)
                .addGroup(admin_loginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(admin_id_field, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel104, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(admin_loginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel103, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(admin_pass_line, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(admin_pass_field, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(admin_field_line, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(admin_loginLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(admin_loginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(login_btn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(login_cancel_btn, javax.swing.GroupLayout.DEFAULT_SIZE, 363, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(admin_loginLayout.createSequentialGroup()
                .addGap(386, 386, 386)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 499, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(395, Short.MAX_VALUE))
        );
        admin_loginLayout.setVerticalGroup(
            admin_loginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(admin_loginLayout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addComponent(jLabel100)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(56, 56, 56)
                .addComponent(jLabel104)
                .addGap(0, 0, 0)
                .addComponent(admin_id_field, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(admin_field_line, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel103)
                .addGap(0, 0, 0)
                .addComponent(admin_pass_field, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(admin_pass_line, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 47, Short.MAX_VALUE)
                .addComponent(login_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(login_cancel_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );

        login_details_init.add(admin_login, "card3");

        student_login.setBackground(new java.awt.Color(51, 51, 51));

        jLabel105.setText("Student ID");
        jLabel105.setFont(new java.awt.Font("Raleway Medium", 0, 18)); // NOI18N
        jLabel105.setForeground(new java.awt.Color(255, 255, 255));

        stu_id_field.setText("Enter Student ID");
        stu_id_field.setBackground(new java.awt.Color(51, 51, 51));
        stu_id_field.setBorder(null);
        stu_id_field.setForeground(new java.awt.Color(153, 153, 153));
        stu_id_field.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                stu_id_fieldMouseClicked(evt);
            }
        });

        stu_login_btn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        stu_login_btn.setText("Log In");
        stu_login_btn.setBackground(new java.awt.Color(153, 153, 153));
        stu_login_btn.setFont(new java.awt.Font("Raleway Black", 0, 24)); // NOI18N
        stu_login_btn.setForeground(new java.awt.Color(51, 51, 51));
        stu_login_btn.setOpaque(true);
        stu_login_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                stu_login_btnMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                stu_login_btnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                stu_login_btnMouseExited(evt);
            }
        });

        stu_login_cancel_btn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        stu_login_cancel_btn.setText("Cancel");
        stu_login_cancel_btn.setBackground(new java.awt.Color(153, 153, 153));
        stu_login_cancel_btn.setFont(new java.awt.Font("Raleway Black", 0, 24)); // NOI18N
        stu_login_cancel_btn.setForeground(new java.awt.Color(51, 51, 51));
        stu_login_cancel_btn.setOpaque(true);
        stu_login_cancel_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                stu_login_cancel_btnMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                stu_login_cancel_btnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                stu_login_cancel_btnMouseExited(evt);
            }
        });

        jLabel106.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel106.setText("Student Login");
        jLabel106.setFont(new java.awt.Font("Raleway Medium", 0, 36)); // NOI18N
        jLabel106.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout student_loginLayout = new javax.swing.GroupLayout(student_login);
        student_login.setLayout(student_loginLayout);
        student_loginLayout.setHorizontalGroup(
            student_loginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel106, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(student_loginLayout.createSequentialGroup()
                .addGap(456, 456, 456)
                .addGroup(student_loginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(student_loginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(stu_login_cancel_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 363, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(stu_login_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 363, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 366, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(458, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, student_loginLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(student_loginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(stu_id_field, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel105, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(admin_field_line1, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(491, 491, 491))
        );
        student_loginLayout.setVerticalGroup(
            student_loginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(student_loginLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel106)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(65, 65, 65)
                .addComponent(jLabel105)
                .addGap(0, 0, 0)
                .addComponent(stu_id_field, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(admin_field_line1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(stu_login_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(stu_login_cancel_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(147, Short.MAX_VALUE))
        );

        login_details_init.add(student_login, "card4");

        javax.swing.GroupLayout login_paneLayout = new javax.swing.GroupLayout(login_pane);
        login_pane.setLayout(login_paneLayout);
        login_paneLayout.setHorizontalGroup(
            login_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel85, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel86, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(login_details_init, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        login_paneLayout.setVerticalGroup(
            login_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(login_paneLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(jLabel85)
                .addGap(18, 18, 18)
                .addComponent(jLabel86)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(login_details_init, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(79, Short.MAX_VALUE))
        );

        main_back_pane.add(login_pane, "card4");

        admin_pane.setPreferredSize(new java.awt.Dimension(1280, 720));

        stud_btn.setIcon(new javax.swing.ImageIcon("C:\\Users\\Omal\\Documents\\NetBeansProjects\\course\\icons\\Student Male_50px.png")); // NOI18N
        stud_btn.setText("Student");
        stud_btn.setFont(new java.awt.Font("Raleway", 0, 13)); // NOI18N
        stud_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                stud_btnMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                stud_btnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                stud_btnMouseExited(evt);
            }
        });

        sub_btn.setIcon(new javax.swing.ImageIcon("C:\\Users\\Omal\\Documents\\NetBeansProjects\\course\\icons\\Book_50px.png")); // NOI18N
        sub_btn.setText("Subject");
        sub_btn.setFont(new java.awt.Font("Raleway", 0, 13)); // NOI18N
        sub_btn.setToolTipText("");
        sub_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                sub_btnMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                sub_btnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                sub_btnMouseExited(evt);
            }
        });

        cour_btn.setIcon(new javax.swing.ImageIcon("C:\\Users\\Omal\\Documents\\NetBeansProjects\\course\\icons\\Course_50px.png")); // NOI18N
        cour_btn.setText("Course");
        cour_btn.setFont(new java.awt.Font("Raleway", 0, 13)); // NOI18N
        cour_btn.setToolTipText("");
        cour_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cour_btnMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                cour_btnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                cour_btnMouseExited(evt);
            }
        });

        staff_btn.setIcon(new javax.swing.ImageIcon("C:\\Users\\Omal\\Documents\\NetBeansProjects\\course\\icons\\Lecturer_50px.png")); // NOI18N
        staff_btn.setText("Staff");
        staff_btn.setFont(new java.awt.Font("Raleway", 0, 13)); // NOI18N
        staff_btn.setToolTipText("");
        staff_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                staff_btnMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                staff_btnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                staff_btnMouseExited(evt);
            }
        });

        home_btn.setIcon(new javax.swing.ImageIcon("C:\\Users\\Omal\\Documents\\NetBeansProjects\\course\\icons\\Home Page_50px.png")); // NOI18N
        home_btn.setFont(new java.awt.Font("Raleway", 0, 13)); // NOI18N
        home_btn.setToolTipText("");
        home_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                home_btnMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                home_btnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                home_btnMouseExited(evt);
            }
        });

        user.setIcon(new javax.swing.ImageIcon("C:\\Users\\Omal\\Documents\\NetBeansProjects\\course\\icons\\Male User_50px.png")); // NOI18N
        user.setFont(new java.awt.Font("Raleway", 0, 13)); // NOI18N
        user.setToolTipText("");
        user.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                userMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                userMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                userMouseExited(evt);
            }
        });

        lab_btn.setIcon(new javax.swing.ImageIcon("C:\\Users\\Omal\\Documents\\NetBeansProjects\\course\\src\\buttons\\Test Tube_50px.png")); // NOI18N
        lab_btn.setText("Labs");
        lab_btn.setFont(new java.awt.Font("Raleway", 0, 13)); // NOI18N
        lab_btn.setToolTipText("");
        lab_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lab_btnMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lab_btnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lab_btnMouseExited(evt);
            }
        });

        mark_btn.setIcon(new javax.swing.ImageIcon("C:\\Users\\Omal\\Documents\\NetBeansProjects\\course\\src\\buttons\\Scorecard_50px.png")); // NOI18N
        mark_btn.setText("Student Marks");
        mark_btn.setFont(new java.awt.Font("Raleway", 0, 13)); // NOI18N
        mark_btn.setToolTipText("");
        mark_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mark_btnMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                mark_btnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                mark_btnMouseExited(evt);
            }
        });

        javax.swing.GroupLayout top_menuLayout = new javax.swing.GroupLayout(top_menu);
        top_menu.setLayout(top_menuLayout);
        top_menuLayout.setHorizontalGroup(
            top_menuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, top_menuLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(home_btn)
                .addGap(81, 81, 81)
                .addComponent(stud_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(cour_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(sub_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(staff_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lab_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(mark_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 64, Short.MAX_VALUE)
                .addComponent(user)
                .addGap(23, 23, 23))
        );
        top_menuLayout.setVerticalGroup(
            top_menuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(top_menuLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(top_menuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(user, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE)
                    .addGroup(top_menuLayout.createSequentialGroup()
                        .addComponent(home_btn)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(top_menuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(sub_btn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(staff_btn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cour_btn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(stud_btn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lab_btn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(mark_btn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        bottom_panel.setBackground(new java.awt.Color(0, 54, 97));
        bottom_panel.setLayout(new java.awt.CardLayout());

        home.setBackground(new java.awt.Color(153, 153, 153));

        currentDate.setText("Date");
        currentDate.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        currentDate.setForeground(new java.awt.Color(102, 102, 102));

        under_leaderboard.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Rank", "Student ID", "Fname", "Lname", "GPA"
            }
        ));
        jScrollPane11.setViewportView(under_leaderboard);

        jLabel151.setText("Undergraduate Leaderboard");
        jLabel151.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        javax.swing.GroupLayout under_leader_panelLayout = new javax.swing.GroupLayout(under_leader_panel);
        under_leader_panel.setLayout(under_leader_panelLayout);
        under_leader_panelLayout.setHorizontalGroup(
            under_leader_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, under_leader_panelLayout.createSequentialGroup()
                .addContainerGap(104, Short.MAX_VALUE)
                .addComponent(jLabel151)
                .addGap(100, 100, 100))
            .addGroup(under_leader_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane11, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 419, Short.MAX_VALUE))
        );
        under_leader_panelLayout.setVerticalGroup(
            under_leader_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(under_leader_panelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel151)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(under_leader_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, under_leader_panelLayout.createSequentialGroup()
                    .addGap(0, 50, Short.MAX_VALUE)
                    .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 528, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        post_leaderboard.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Rank", "Student ID", "Fname", "Lname", "GPA"
            }
        ));
        jScrollPane12.setViewportView(post_leaderboard);

        jLabel152.setText("Postgraduate Leaderboard");
        jLabel152.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        javax.swing.GroupLayout post_leader_panelLayout = new javax.swing.GroupLayout(post_leader_panel);
        post_leader_panel.setLayout(post_leader_panelLayout);
        post_leader_panelLayout.setHorizontalGroup(
            post_leader_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(post_leader_panelLayout.createSequentialGroup()
                .addGap(114, 114, 114)
                .addComponent(jLabel152)
                .addContainerGap(114, Short.MAX_VALUE))
            .addGroup(post_leader_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane12, javax.swing.GroupLayout.DEFAULT_SIZE, 415, Short.MAX_VALUE))
        );
        post_leader_panelLayout.setVerticalGroup(
            post_leader_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(post_leader_panelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel152)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(post_leader_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, post_leader_panelLayout.createSequentialGroup()
                    .addGap(0, 50, Short.MAX_VALUE)
                    .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, 528, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        currentTime.setText("Time");
        currentTime.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        currentTime.setForeground(new java.awt.Color(102, 102, 102));

        jLabel150.setText("Current Time :");
        jLabel150.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel153.setText("Current Date :");
        jLabel153.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jSeparator3.setForeground(new java.awt.Color(51, 51, 51));

        currentUser.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel155.setText("           User :");
        jLabel155.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel154.setText("Number of Students :");
        jLabel154.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel156.setText("Number of Staff :");
        jLabel156.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel157.setText("University Ranking :");
        jLabel157.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        stu_count.setText("0");
        stu_count.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        stu_count.setForeground(new java.awt.Color(102, 102, 102));

        staff_count.setText("0");
        staff_count.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        staff_count.setForeground(new java.awt.Color(102, 102, 102));

        uni_rank.setText("0");
        uni_rank.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        uni_rank.setForeground(new java.awt.Color(102, 102, 102));

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel20Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel20Layout.createSequentialGroup()
                                .addComponent(jLabel150, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(currentTime, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel20Layout.createSequentialGroup()
                                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel153, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel155, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel20Layout.createSequentialGroup()
                                        .addComponent(currentUser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGap(3, 3, 3))
                                    .addComponent(currentDate, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(31, 31, 31))
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 331, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel20Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel154)
                                    .addComponent(jLabel156)
                                    .addComponent(jLabel157))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(stu_count, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(staff_count, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(uni_rank, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(6, 6, 6)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)))
                .addComponent(under_leader_panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(post_leader_panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(post_leader_panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(under_leader_panel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addGap(66, 66, 66)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(currentUser, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel155, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(18, 18, 18)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel153, javax.swing.GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE)
                    .addComponent(currentDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(currentTime, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel150))
                .addGap(21, 21, 21)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel154)
                    .addComponent(stu_count))
                .addGap(18, 18, 18)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel156)
                    .addComponent(staff_count))
                .addGap(18, 18, 18)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel157)
                    .addComponent(uni_rank))
                .addContainerGap(268, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout homeLayout = new javax.swing.GroupLayout(home);
        home.setLayout(homeLayout);
        homeLayout.setHorizontalGroup(
            homeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(homeLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        homeLayout.setVerticalGroup(
            homeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(homeLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        bottom_panel.add(home, "card6");

        student.setBackground(new java.awt.Color(153, 153, 153));

        fname.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fnameMouseClicked(evt);
            }
        });

        lname.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lnameMouseClicked(evt);
            }
        });

        telephone.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                telephoneMouseClicked(evt);
            }
        });

        email.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                emailMouseClicked(evt);
            }
        });

        address.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addressMouseClicked(evt);
            }
        });

        gender.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Male", "Female", "Other"}));

        semester.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "February", "July"}));
        semester.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                semesterActionPerformed(evt);
            }
        });

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Register New Student");
        jLabel1.setFont(new java.awt.Font("Raleway SemiBold", 0, 18)); // NOI18N

        grad_select.setLayout(new java.awt.CardLayout());

        javax.swing.GroupLayout initpanelLayout = new javax.swing.GroupLayout(initpanel);
        initpanel.setLayout(initpanelLayout);
        initpanelLayout.setHorizontalGroup(
            initpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 398, Short.MAX_VALUE)
        );
        initpanelLayout.setVerticalGroup(
            initpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 99, Short.MAX_VALUE)
        );

        grad_select.add(initpanel, "card3");

        qualification.setText("Qualification");
        qualification.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                qualificationMouseClicked(evt);
            }
        });

        institute.setText("Institute");
        institute.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                instituteMouseClicked(evt);
            }
        });

        completed_year.setText("Year Completed");
        completed_year.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                completed_yearMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout postgradLayout = new javax.swing.GroupLayout(postgrad);
        postgrad.setLayout(postgradLayout);
        postgradLayout.setHorizontalGroup(
            postgradLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(postgradLayout.createSequentialGroup()
                .addComponent(completed_year, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(postgradLayout.createSequentialGroup()
                .addComponent(institute, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(qualification, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        postgradLayout.setVerticalGroup(
            postgradLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, postgradLayout.createSequentialGroup()
                .addGroup(postgradLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(institute, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(qualification, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(completed_year, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(42, Short.MAX_VALUE))
        );

        grad_select.add(postgrad, "card5");

        gk_result.setText("General Knowledge Result");
        gk_result.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                gk_resultMouseClicked(evt);
            }
        });

        result_1.setText("Subject 1 Result");
        result_1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                result_1MouseClicked(evt);
            }
        });
        result_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                result_1ActionPerformed(evt);
            }
        });

        result_2.setText("Subject 2 Result");
        result_2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                result_2MouseClicked(evt);
            }
        });

        result_3.setText("Subject 3 Result");
        result_3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                result_3MouseClicked(evt);
            }
        });
        result_3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                result_3ActionPerformed(evt);
            }
        });

        eng_result.setText("English Result");
        eng_result.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                eng_resultMouseClicked(evt);
            }
        });
        eng_result.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eng_resultActionPerformed(evt);
            }
        });

        alvl_island_rank.setText("A/L Island Rank");
        alvl_island_rank.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                alvl_island_rankMouseClicked(evt);
            }
        });
        alvl_island_rank.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                alvl_island_rankActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout undergradLayout = new javax.swing.GroupLayout(undergrad);
        undergrad.setLayout(undergradLayout);
        undergradLayout.setHorizontalGroup(
            undergradLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(undergradLayout.createSequentialGroup()
                .addGroup(undergradLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(undergradLayout.createSequentialGroup()
                        .addComponent(result_3, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(eng_result, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(undergradLayout.createSequentialGroup()
                        .addComponent(result_1, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(result_2, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(undergradLayout.createSequentialGroup()
                        .addComponent(alvl_island_rank, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(gk_result, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        undergradLayout.setVerticalGroup(
            undergradLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(undergradLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(undergradLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(gk_result, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(alvl_island_rank, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(undergradLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(result_2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(result_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(undergradLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(result_3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(eng_result, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        grad_select.add(undergrad, "card4");

        student_reg_btn.setText("Register");
        student_reg_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                student_reg_btnActionPerformed(evt);
            }
        });

        student_reg_cancel_btn.setText("Cancel");
        student_reg_cancel_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                student_reg_cancel_btnMouseClicked(evt);
            }
        });
        student_reg_cancel_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                student_reg_cancel_btnActionPerformed(evt);
            }
        });

        course_id.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] {}));
        course_id.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                course_idActionPerformed(evt);
            }
        });

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel4.setText("Semester Joined");

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel5.setText("Course Following");

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel6.setText("Gender");

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel21.setText("Date of Birth");

        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel22.setText("First Name");

        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel23.setText("Telephone");

        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel24.setText("Email");

        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel25.setText("Last Name");

        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel26.setText("Address");

        Undergraduate.setText("Undergraduate");
        Undergraduate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UndergraduateActionPerformed(evt);
            }
        });

        Postgraduate.setText("Postgraduate");
        Postgraduate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PostgraduateActionPerformed(evt);
            }
        });

        jLabel46.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel46.setText("NIC");

        student_id.setText("NIC (without 'V')");
        student_id.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                student_idMouseClicked(evt);
            }
        });
        student_id.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                student_idActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE))
                            .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel24, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel23, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel25, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel22, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel26, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel46, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(semester, 0, 265, Short.MAX_VALUE)
                            .addComponent(gender, 0, 265, Short.MAX_VALUE)
                            .addComponent(course_id, 0, 265, Short.MAX_VALUE)
                            .addComponent(email, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                            .addComponent(telephone, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                            .addComponent(address, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                            .addComponent(fname, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                            .addComponent(lname, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                            .addComponent(student_id, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                            .addComponent(birthday_selector, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(Undergraduate, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Postgraduate, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(grad_select, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(student_reg_btn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 398, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(student_reg_cancel_btn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 398, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(56, Short.MAX_VALUE))
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(student_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(address, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(birthday_selector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(telephone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(email, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(gender, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(semester, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(course_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Undergraduate)
                    .addComponent(Postgraduate))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(grad_select, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(student_reg_btn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(student_reg_cancel_btn)
                .addGap(384, 384, 384))
        );

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Student Details");
        jLabel2.setFont(new java.awt.Font("Raleway SemiBold", 0, 18)); // NOI18N

        jButton1.setText("Update Table");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        search_id.setText("Search by ID");
        search_id.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                search_idMouseClicked(evt);
            }
        });

        search_student_btn.setText("Search");
        search_student_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                search_student_btnActionPerformed(evt);
            }
        });

        stu_jTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Student ID", "First Name", "Last Name", "Type", "Course", "Gender", "Semester"
            }
        ));
        jScrollPane1.setViewportView(stu_jTable);

        mail_report_btn.setText("Send Mail Report");
        mail_report_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mail_report_btnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(search_id, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(search_student_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(mail_report_btn, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(search_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(search_student_btn)
                    .addComponent(mail_report_btn))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        stu_search_panel.setLayout(new java.awt.CardLayout());

        javax.swing.GroupLayout student_init_panelLayout = new javax.swing.GroupLayout(student_init_panel);
        student_init_panel.setLayout(student_init_panelLayout);
        student_init_panelLayout.setHorizontalGroup(
            student_init_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 743, Short.MAX_VALUE)
        );
        student_init_panelLayout.setVerticalGroup(
            student_init_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 311, Short.MAX_VALUE)
        );

        stu_search_panel.add(student_init_panel, "card2");

        address_field1.setText("-----------");

        fname_field1.setText("-----------");

        eng_res1.setText("----");
        eng_res1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eng_res1ActionPerformed(evt);
            }
        });

        sub3_res1.setText("----");

        sub2_res1.setText("----");
        sub2_res1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sub2_res1ActionPerformed(evt);
            }
        });

        sub1_res1.setText("----");
        sub1_res1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sub1_res1ActionPerformed(evt);
            }
        });

        gender_field1.setText("-----------");

        lname_field1.setText("-----------");
        lname_field1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lname_field1ActionPerformed(evt);
            }
        });

        gk_res1.setText("----");
        gk_res1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gk_res1ActionPerformed(evt);
            }
        });

        jLabel27.setText("English");

        jLabel28.setText("Subject 1");

        jLabel29.setText("Subject 2");

        jLabel30.setText("Subject 3");

        jLabel31.setText("Gen.Knw");

        jLabel32.setText("First Name");

        jLabel33.setText("Gender");

        jLabel34.setText("Email");

        jLabel35.setText("Last Name");

        jLabel36.setText("Address");

        jLabel37.setText("Semester");

        semester_field1.setText("-----------");

        email_field1.setText("-----------");

        tele_field1.setText("-----------");
        tele_field1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tele_field1ActionPerformed(evt);
            }
        });

        jLabel38.setText("Telephone");

        course_field1.setText("-----------");

        jLabel39.setText("Course");

        type_field1.setText("-----------");

        jLabel40.setText("Type");

        dob_field1.setText("-----------");
        dob_field1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dob_field1ActionPerformed(evt);
            }
        });

        jLabel41.setText("DoB");

        rank_res1.setText("----");
        rank_res1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rank_res1ActionPerformed(evt);
            }
        });

        jLabel42.setText("Rank");

        update_undergrad_btn.setText("Update");
        update_undergrad_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                update_undergrad_btnActionPerformed(evt);
            }
        });

        delete_undergrad_btn.setText("Delete");
        delete_undergrad_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delete_undergrad_btnActionPerformed(evt);
            }
        });

        make_pay_field1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                make_pay_field1ActionPerformed(evt);
            }
        });

        out_pay_field1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                out_pay_field1ActionPerformed(evt);
            }
        });

        jLabel169.setText("Payment");

        jLabel170.setText("Paid Amt");

        under_payment_btn.setText("Make Payment");
        under_payment_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                under_payment_btnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout under_result_panelLayout = new javax.swing.GroupLayout(under_result_panel);
        under_result_panel.setLayout(under_result_panelLayout);
        under_result_panelLayout.setHorizontalGroup(
            under_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(under_result_panelLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(under_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(under_result_panelLayout.createSequentialGroup()
                        .addComponent(jLabel34)
                        .addGap(18, 18, 18)
                        .addComponent(email_field1, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(under_result_panelLayout.createSequentialGroup()
                        .addComponent(jLabel39)
                        .addGap(18, 18, 18)
                        .addComponent(course_field1, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(under_result_panelLayout.createSequentialGroup()
                        .addGroup(under_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(under_result_panelLayout.createSequentialGroup()
                                .addGroup(under_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel33)
                                    .addComponent(jLabel32))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                            .addGroup(under_result_panelLayout.createSequentialGroup()
                                .addGroup(under_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel169)
                                    .addComponent(jLabel41))
                                .addGap(18, 18, 18)))
                        .addGroup(under_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(gender_field1)
                            .addComponent(dob_field1)
                            .addComponent(fname_field1)
                            .addComponent(update_undergrad_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(out_pay_field1))))
                .addGroup(under_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(under_result_panelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                        .addGroup(under_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel37, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel36, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel35, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel40, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel38, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(under_result_panelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel170)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(under_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(under_result_panelLayout.createSequentialGroup()
                        .addGroup(under_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(under_result_panelLayout.createSequentialGroup()
                                .addGroup(under_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(address_field1, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(semester_field1, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(type_field1, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tele_field1, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lname_field1, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(60, 60, 60)
                                .addGroup(under_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel28)
                                    .addComponent(jLabel29)
                                    .addComponent(jLabel30)
                                    .addComponent(jLabel31)
                                    .addComponent(jLabel27)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, under_result_panelLayout.createSequentialGroup()
                                .addComponent(make_pay_field1, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel42)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(under_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(sub3_res1, javax.swing.GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE)
                            .addComponent(sub2_res1)
                            .addComponent(sub1_res1, javax.swing.GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE)
                            .addComponent(rank_res1)
                            .addComponent(eng_res1, javax.swing.GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE)
                            .addComponent(gk_res1)))
                    .addGroup(under_result_panelLayout.createSequentialGroup()
                        .addComponent(under_payment_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(delete_undergrad_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(13, Short.MAX_VALUE))
        );
        under_result_panelLayout.setVerticalGroup(
            under_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(under_result_panelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(under_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(under_result_panelLayout.createSequentialGroup()
                        .addGroup(under_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(fname_field1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lname_field1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel35)
                            .addComponent(jLabel27)
                            .addComponent(jLabel32)
                            .addComponent(eng_res1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(under_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(under_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(gender_field1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel33))
                            .addGroup(under_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(address_field1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel28))
                            .addComponent(jLabel36))
                        .addGap(18, 18, 18)
                        .addGroup(under_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(under_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(semester_field1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel37)
                                .addComponent(jLabel29))
                            .addGroup(under_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(email_field1)
                                .addComponent(jLabel34)))
                        .addGap(18, 18, 18)
                        .addGroup(under_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(under_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(course_field1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel39))
                            .addGroup(under_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(type_field1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel40)
                                .addComponent(jLabel30))))
                    .addGroup(under_result_panelLayout.createSequentialGroup()
                        .addComponent(sub1_res1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(sub2_res1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(sub3_res1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(under_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(under_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tele_field1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel38)
                        .addComponent(jLabel31)
                        .addComponent(gk_res1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel41)
                    .addComponent(dob_field1))
                .addGap(18, 18, 18)
                .addGroup(under_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rank_res1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel42)
                    .addComponent(make_pay_field1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(out_pay_field1)
                    .addComponent(jLabel169)
                    .addComponent(jLabel170))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(under_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(update_undergrad_btn)
                    .addComponent(delete_undergrad_btn)
                    .addComponent(under_payment_btn))
                .addGap(27, 27, 27))
        );

        stu_search_panel.add(under_result_panel, "card3");

        address_field2.setText("-----------");

        fname_field2.setText("-----------");

        institute_res.setText("----");
        institute_res.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                institute_resActionPerformed(evt);
            }
        });

        qualification_res.setText("----");
        qualification_res.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                qualification_resActionPerformed(evt);
            }
        });

        complete_year_res.setText("----");
        complete_year_res.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                complete_year_resActionPerformed(evt);
            }
        });

        gender_field2.setText("-----------");

        lname_field2.setText("-----------");
        lname_field2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lname_field2ActionPerformed(evt);
            }
        });

        jLabel43.setText("Institute");

        jLabel44.setText("Completed Year");

        jLabel45.setText("Qualification");

        jLabel48.setText("First Name");

        jLabel49.setText("Gender");

        jLabel50.setText("Email");

        jLabel51.setText("Last Name");

        jLabel52.setText("Address");

        jLabel53.setText("Semester");

        semester_field2.setText("-----------");

        email_field2.setText("-----------");

        tele_field2.setText("-----------");
        tele_field2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tele_field2ActionPerformed(evt);
            }
        });

        jLabel54.setText("Telephone");

        course_field2.setText("-----------");

        jLabel55.setText("Course");

        type_field2.setText("-----------");

        jLabel56.setText("Type");

        dob_field2.setText("-----------");

        jLabel57.setText("DoB");

        update_postgrad_btn.setText("Update");
        update_postgrad_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                update_postgrad_btnActionPerformed(evt);
            }
        });

        delete_postgrad_btn.setText("Delete");
        delete_postgrad_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delete_postgrad_btnActionPerformed(evt);
            }
        });

        jLabel167.setText("Payments");

        jLabel168.setText("Paid Amt");

        post_payment_btn.setText("Make Payment");
        post_payment_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                post_payment_btnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout post_result_panelLayout = new javax.swing.GroupLayout(post_result_panel);
        post_result_panel.setLayout(post_result_panelLayout);
        post_result_panelLayout.setHorizontalGroup(
            post_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(post_result_panelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(post_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel49, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel48, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel57, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel56, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel50, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel167, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(18, 18, 18)
                .addGroup(post_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(update_postgrad_btn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(gender_field2, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
                    .addComponent(dob_field2, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
                    .addComponent(type_field2, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
                    .addComponent(email_field2, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
                    .addComponent(fname_field2)
                    .addComponent(out_pay_field, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(post_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(post_result_panelLayout.createSequentialGroup()
                        .addGroup(post_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel51)
                            .addComponent(jLabel52, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(18, 18, 18)
                        .addGroup(post_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lname_field2, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(address_field2, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(post_result_panelLayout.createSequentialGroup()
                        .addGroup(post_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel54)
                            .addComponent(jLabel53, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel55, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel168, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(18, 18, 18)
                        .addGroup(post_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(make_pay_field)
                            .addGroup(post_result_panelLayout.createSequentialGroup()
                                .addGroup(post_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(course_field2, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(semester_field2, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tele_field2, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(post_payment_btn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(post_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(post_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(post_result_panelLayout.createSequentialGroup()
                            .addGap(21, 21, 21)
                            .addComponent(jLabel45)
                            .addGap(18, 18, 18)
                            .addComponent(qualification_res, javax.swing.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE))
                        .addGroup(post_result_panelLayout.createSequentialGroup()
                            .addGroup(post_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(post_result_panelLayout.createSequentialGroup()
                                    .addGap(45, 45, 45)
                                    .addComponent(jLabel43))
                                .addComponent(jLabel44))
                            .addGap(18, 18, 18)
                            .addGroup(post_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(institute_res, javax.swing.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)
                                .addComponent(complete_year_res))))
                    .addComponent(delete_postgrad_btn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        post_result_panelLayout.setVerticalGroup(
            post_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(post_result_panelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(post_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fname_field2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lname_field2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel51)
                    .addComponent(jLabel48)
                    .addComponent(institute_res, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel43))
                .addGap(18, 18, 18)
                .addGroup(post_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(gender_field2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(address_field2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(post_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel49)
                        .addComponent(jLabel52)
                        .addComponent(complete_year_res, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel44)))
                .addGap(18, 18, 18)
                .addGroup(post_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tele_field2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(post_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel54)
                        .addComponent(dob_field2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel57)
                        .addComponent(qualification_res, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel45)))
                .addGap(18, 18, 18)
                .addGroup(post_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel55)
                    .addComponent(course_field2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(email_field2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel50))
                .addGap(18, 18, 18)
                .addGroup(post_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(semester_field2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(type_field2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel56)
                    .addComponent(jLabel53))
                .addGap(18, 18, 18)
                .addGroup(post_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(post_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(make_pay_field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(delete_postgrad_btn))
                    .addGroup(post_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(out_pay_field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel168)
                        .addComponent(jLabel167)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(post_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(update_postgrad_btn)
                    .addComponent(post_payment_btn))
                .addContainerGap(41, Short.MAX_VALUE))
        );

        stu_search_panel.add(post_result_panel, "card3");

        stu_report_name.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stu_report_nameActionPerformed(evt);
            }
        });

        jLabel164.setText("Student Name");

        stu_report_email.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stu_report_emailActionPerformed(evt);
            }
        });

        jLabel165.setText("Receiving Email");

        stu_report_subject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stu_report_subjectActionPerformed(evt);
            }
        });

        jLabel166.setText("Email Subject");

        stu_report_body.setColumns(20);
        stu_report_body.setRows(5);
        jScrollPane13.setViewportView(stu_report_body);

        send_mail_btn.setText("Send Mail");
        send_mail_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                send_mail_btnActionPerformed(evt);
            }
        });

        cancel_mail_btn.setText("Cancel");
        cancel_mail_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancel_mail_btnActionPerformed(evt);
            }
        });

        jButton6.setText("Send Mail to All");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout mail_report_panelLayout = new javax.swing.GroupLayout(mail_report_panel);
        mail_report_panel.setLayout(mail_report_panelLayout);
        mail_report_panelLayout.setHorizontalGroup(
            mail_report_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mail_report_panelLayout.createSequentialGroup()
                .addGroup(mail_report_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, mail_report_panelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(send_mail_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cancel_mail_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE))
                    .addGroup(mail_report_panelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane13))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, mail_report_panelLayout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addGroup(mail_report_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel164)
                            .addComponent(jLabel166))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(mail_report_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(mail_report_panelLayout.createSequentialGroup()
                                .addComponent(stu_report_name, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel165)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(stu_report_email, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(stu_report_subject))))
                .addGap(34, 34, 34))
        );
        mail_report_panelLayout.setVerticalGroup(
            mail_report_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mail_report_panelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mail_report_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(stu_report_name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel164)
                    .addComponent(stu_report_email, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel165))
                .addGap(18, 18, 18)
                .addGroup(mail_report_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(stu_report_subject, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel166))
                .addGap(27, 27, 27)
                .addComponent(jScrollPane13, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(mail_report_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(send_mail_btn)
                    .addComponent(cancel_mail_btn)
                    .addComponent(jButton6))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        stu_search_panel.add(mail_report_panel, "card5");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(stu_search_panel, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 11, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 756, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(stu_search_panel, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout studentLayout = new javax.swing.GroupLayout(student);
        student.setLayout(studentLayout);
        studentLayout.setHorizontalGroup(
            studentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(studentLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        studentLayout.setVerticalGroup(
            studentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, studentLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(studentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 619, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        bottom_panel.add(student, "card2");

        subject.setBackground(new java.awt.Color(153, 153, 153));

        subject_name.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                subject_nameMouseClicked(evt);
            }
        });

        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel20.setText("Add New Subject");
        jLabel20.setFont(new java.awt.Font("Raleway SemiBold", 0, 18)); // NOI18N

        instructor_select.setLayout(new java.awt.CardLayout());

        javax.swing.GroupLayout instructor_init_panelLayout = new javax.swing.GroupLayout(instructor_init_panel);
        instructor_init_panel.setLayout(instructor_init_panelLayout);
        instructor_init_panelLayout.setHorizontalGroup(
            instructor_init_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 398, Short.MAX_VALUE)
        );
        instructor_init_panelLayout.setVerticalGroup(
            instructor_init_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 144, Short.MAX_VALUE)
        );

        instructor_select.add(instructor_init_panel, "card2");

        instructor_list1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] {}));

        add_instructor_btn.setText("Add Instructor");
        add_instructor_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                add_instructor_btnActionPerformed(evt);
            }
        });

        subject_list1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] {}));

        jLabel77.setText("Select Subject");

        jLabel78.setText("Select Instructor");

        javax.swing.GroupLayout add_instructor_panelLayout = new javax.swing.GroupLayout(add_instructor_panel);
        add_instructor_panel.setLayout(add_instructor_panelLayout);
        add_instructor_panelLayout.setHorizontalGroup(
            add_instructor_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, add_instructor_panelLayout.createSequentialGroup()
                .addContainerGap(26, Short.MAX_VALUE)
                .addGroup(add_instructor_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(add_instructor_panelLayout.createSequentialGroup()
                        .addGroup(add_instructor_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel77)
                            .addComponent(jLabel78))
                        .addGap(18, 18, 18)
                        .addGroup(add_instructor_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(instructor_list1, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(subject_list1, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(add_instructor_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 348, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(24, 24, 24))
        );
        add_instructor_panelLayout.setVerticalGroup(
            add_instructor_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(add_instructor_panelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(add_instructor_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel77)
                    .addComponent(subject_list1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(add_instructor_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(instructor_list1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel78))
                .addGap(18, 18, 18)
                .addComponent(add_instructor_btn)
                .addGap(45, 45, 45))
        );

        instructor_select.add(add_instructor_panel, "card3");

        subject_reg_btn.setText("Add Subject");
        subject_reg_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subject_reg_btnActionPerformed(evt);
            }
        });

        subject_reg_cancel_btn.setText("Cancel");
        subject_reg_cancel_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                subject_reg_cancel_btnMouseClicked(evt);
            }
        });
        subject_reg_cancel_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subject_reg_cancel_btnActionPerformed(evt);
            }
        });

        jLabel88.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel88.setText("Subject Name");

        jLabel93.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel93.setText("Assigned Lecturer");

        jLabel94.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel94.setText("Duration (Hours)");

        jLabel95.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel95.setText("Subject Code");

        subject_id_enter.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                subject_id_enterMouseClicked(evt);
            }
        });
        subject_id_enter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subject_id_enterActionPerformed(evt);
            }
        });

        lecturer_list.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] {}));

        duration_list.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2"}));
        duration_list.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                duration_listActionPerformed(evt);
            }
        });

        jLabel96.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel96.setText("Subject Fee");

        subject_fee.setText("In LKR");
        subject_fee.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                subject_feeMouseClicked(evt);
            }
        });
        subject_fee.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subject_feeActionPerformed(evt);
            }
        });

        jLabel97.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel97.setText("Credits Awarded");

        add_instructor_show_btn.setText("Add Instructors to Subjects");
        add_instructor_show_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                add_instructor_show_btnActionPerformed(evt);
            }
        });

        course_list1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] {}));

        jLabel89.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel89.setText("Related Course");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(subject_reg_cancel_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 398, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(subject_reg_btn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 398, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(instructor_select, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(add_instructor_show_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 398, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel93, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel88, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel94, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel95, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel96, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel97, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel89, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(subject_name)
                            .addComponent(subject_id_enter)
                            .addComponent(duration_list, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(subject_fee, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                            .addComponent(subject_credits)
                            .addComponent(lecturer_list, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(course_list1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(55, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel20)
                .addGap(20, 20, 20)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel95, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(subject_id_enter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(subject_name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel88, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(course_list1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel89, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel93, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lecturer_list, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel94, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(duration_list, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel96, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(subject_fee, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel97, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(subject_credits, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(subject_reg_btn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(subject_reg_cancel_btn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(add_instructor_show_btn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(instructor_select, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(114, 114, 114))
        );

        jLabel70.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel70.setText("Subject Details");
        jLabel70.setFont(new java.awt.Font("Raleway SemiBold", 0, 18)); // NOI18N

        update_subject_table.setText("Update Table");
        update_subject_table.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                update_subject_tableActionPerformed(evt);
            }
        });

        search_subject_id.setText("Search by ID");
        search_subject_id.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                search_subject_idMouseClicked(evt);
            }
        });
        search_subject_id.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                search_subject_idActionPerformed(evt);
            }
        });

        search_subject_btn.setText("Search");
        search_subject_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                search_subject_btnActionPerformed(evt);
            }
        });

        subject_jTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Subject Code", "Subject Name", "Subject Duration", "Credits", "Subject Fee", "Course Code"
            }
        ));
        jScrollPane5.setViewportView(subject_jTable);

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addComponent(search_subject_id, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(search_subject_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(update_subject_table, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 756, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(update_subject_table)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(search_subject_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(search_subject_btn))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        subject_search_panel.setLayout(new java.awt.CardLayout());

        javax.swing.GroupLayout subject_init_panelLayout = new javax.swing.GroupLayout(subject_init_panel);
        subject_init_panel.setLayout(subject_init_panelLayout);
        subject_init_panelLayout.setHorizontalGroup(
            subject_init_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 743, Short.MAX_VALUE)
        );
        subject_init_panelLayout.setVerticalGroup(
            subject_init_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 311, Short.MAX_VALUE)
        );

        subject_search_panel.add(subject_init_panel, "card2");

        subject_name_field.setText("-----------");

        subject_lecturer_field.setEditable(false);
        subject_lecturer_field.setText("-----------");
        subject_lecturer_field.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subject_lecturer_fieldActionPerformed(evt);
            }
        });

        jLabel72.setText("Subject Name");

        jLabel73.setText("Assigned Lecturer");

        update_subject_btn.setText("Update");
        update_subject_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                update_subject_btnActionPerformed(evt);
            }
        });

        delete_subject_btn.setText("Delete");
        delete_subject_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delete_subject_btnActionPerformed(evt);
            }
        });

        subject_duration_field.setEditable(false);
        subject_duration_field.setText("-----------");
        subject_duration_field.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subject_duration_fieldActionPerformed(evt);
            }
        });

        subject_fee_field.setText("-----------");
        subject_fee_field.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subject_fee_fieldActionPerformed(evt);
            }
        });

        subject_credit_field.setText("-----------");
        subject_credit_field.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subject_credit_fieldActionPerformed(evt);
            }
        });

        jLabel74.setText("Subject Fee");

        jLabel75.setText("Credits");

        jLabel76.setText("Duration (hours)");

        duration_list2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1","2"}));

        inst_res_jTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "List of Instructors for the Subject"
            }
        ));
        jScrollPane2.setViewportView(inst_res_jTable);

        lecturer_list2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1","2"}));

        javax.swing.GroupLayout subject_result_panelLayout = new javax.swing.GroupLayout(subject_result_panel);
        subject_result_panel.setLayout(subject_result_panelLayout);
        subject_result_panelLayout.setHorizontalGroup(
            subject_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, subject_result_panelLayout.createSequentialGroup()
                .addGroup(subject_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(subject_result_panelLayout.createSequentialGroup()
                        .addGap(58, 58, 58)
                        .addGroup(subject_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(subject_result_panelLayout.createSequentialGroup()
                                .addComponent(jLabel72)
                                .addGap(26, 26, 26)
                                .addComponent(subject_name_field, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(subject_result_panelLayout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(subject_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel74)
                                    .addComponent(jLabel75))
                                .addGap(29, 29, 29)
                                .addGroup(subject_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(subject_fee_field, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(subject_credit_field, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(subject_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(subject_result_panelLayout.createSequentialGroup()
                            .addGap(162, 162, 162)
                            .addComponent(delete_subject_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, subject_result_panelLayout.createSequentialGroup()
                            .addGap(34, 34, 34)
                            .addGroup(subject_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel73)
                                .addComponent(jLabel76))
                            .addGap(26, 26, 26)
                            .addGroup(subject_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(subject_lecturer_field, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(subject_duration_field, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(50, 50, 50)
                .addGroup(subject_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                    .addComponent(duration_list2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(update_subject_btn, javax.swing.GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE)
                    .addComponent(lecturer_list2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(46, 46, 46))
        );
        subject_result_panelLayout.setVerticalGroup(
            subject_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(subject_result_panelLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(subject_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(subject_result_panelLayout.createSequentialGroup()
                        .addGroup(subject_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(subject_name_field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel72))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(subject_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(subject_fee_field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel74))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(subject_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(subject_credit_field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel75)))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                .addGroup(subject_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(subject_lecturer_field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel73)
                    .addComponent(lecturer_list2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(subject_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(duration_list2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(subject_duration_field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel76))
                .addGap(18, 18, 18)
                .addGroup(subject_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(delete_subject_btn)
                    .addComponent(update_subject_btn))
                .addGap(62, 62, 62))
        );

        subject_search_panel.add(subject_result_panel, "card3");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(subject_search_panel, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
            .addComponent(jLabel70, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel70, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(subject_search_panel, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout subjectLayout = new javax.swing.GroupLayout(subject);
        subject.setLayout(subjectLayout);
        subjectLayout.setHorizontalGroup(
            subjectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(subjectLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        subjectLayout.setVerticalGroup(
            subjectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(subjectLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(subjectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(subjectLayout.createSequentialGroup()
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, 619, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        bottom_panel.add(subject, "card4");

        course.setBackground(new java.awt.Color(153, 153, 153));

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Course Details");
        jLabel7.setFont(new java.awt.Font("Raleway SemiBold", 0, 18)); // NOI18N

        jButton2.setText("Update Table");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        search_course_id.setText("Search by ID");
        search_course_id.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                search_course_idMouseClicked(evt);
            }
        });
        search_course_id.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                search_course_idActionPerformed(evt);
            }
        });

        search_course_btn.setText("Search");
        search_course_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                search_course_btnActionPerformed(evt);
            }
        });

        course_jTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Course ID", "Course Name", "Course Duration", "Course Credits Per Year"
            }
        ));
        jScrollPane3.setViewportView(course_jTable);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(search_course_id, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(search_course_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jButton2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(search_course_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(search_course_btn))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        course_search_panel.setLayout(new java.awt.CardLayout());

        javax.swing.GroupLayout course_init_panelLayout = new javax.swing.GroupLayout(course_init_panel);
        course_init_panel.setLayout(course_init_panelLayout);
        course_init_panelLayout.setHorizontalGroup(
            course_init_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 719, Short.MAX_VALUE)
        );
        course_init_panelLayout.setVerticalGroup(
            course_init_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 311, Short.MAX_VALUE)
        );

        course_search_panel.add(course_init_panel, "card2");

        course_name_field.setText("-----------");

        course_credit_field.setText("-----------");

        course_duration_field.setText("-----------");
        course_duration_field.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                course_duration_fieldActionPerformed(evt);
            }
        });

        jLabel62.setText("Course Name");

        jLabel63.setText("Course Credits");

        jLabel65.setText("Course Duration (Years)");

        update_course_btn.setText("Update");
        update_course_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                update_course_btnActionPerformed(evt);
            }
        });

        delete_course_btn.setText("Delete");
        delete_course_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delete_course_btnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout course_result_panelLayout = new javax.swing.GroupLayout(course_result_panel);
        course_result_panel.setLayout(course_result_panelLayout);
        course_result_panelLayout.setHorizontalGroup(
            course_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(course_result_panelLayout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addGroup(course_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel63)
                    .addComponent(jLabel65)
                    .addComponent(jLabel62))
                .addGap(26, 26, 26)
                .addGroup(course_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(delete_course_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(update_course_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(course_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(course_duration_field, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(course_credit_field)
                        .addComponent(course_name_field, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(319, Short.MAX_VALUE))
        );
        course_result_panelLayout.setVerticalGroup(
            course_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(course_result_panelLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(course_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(course_name_field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel62))
                .addGap(18, 18, 18)
                .addGroup(course_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(course_credit_field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel63))
                .addGap(18, 18, 18)
                .addGroup(course_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(course_duration_field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel65))
                .addGap(18, 18, 18)
                .addComponent(update_course_btn)
                .addGap(18, 18, 18)
                .addComponent(delete_course_btn)
                .addContainerGap(91, Short.MAX_VALUE))
        );

        course_search_panel.add(course_result_panel, "card3");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(course_search_panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(course_search_panel, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        course_name.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                course_nameMouseClicked(evt);
            }
        });

        course_duration.setText("In Years");
        course_duration.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                course_durationMouseClicked(evt);
            }
        });

        course_credits.setText("Credits Needed to Complete");
        course_credits.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                course_creditsMouseClicked(evt);
            }
        });

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Add New Course");
        jLabel8.setFont(new java.awt.Font("Raleway SemiBold", 0, 18)); // NOI18N

        grad_select1.setLayout(new java.awt.CardLayout());

        course_reg_btn.setText("Add Course");
        course_reg_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                course_reg_btnActionPerformed(evt);
            }
        });

        course_reg_cancel_btn.setText("Cancel");
        course_reg_cancel_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                course_reg_cancel_btnMouseClicked(evt);
            }
        });
        course_reg_cancel_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                course_reg_cancel_btnActionPerformed(evt);
            }
        });

        jLabel87.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel87.setText("Course Name");

        jLabel90.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel90.setText("Duration (Years)");

        jLabel91.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel91.setText("Annual Credits Needed");

        jLabel92.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel92.setText("Course ID");

        course_id_enter.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                course_id_enterMouseClicked(evt);
            }
        });
        course_id_enter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                course_id_enterActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel90, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel87, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel92, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel91))
                            .addGap(18, 18, 18)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(course_credits, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(course_name, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(course_duration, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(course_id_enter, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addComponent(grad_select1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(course_reg_btn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(course_reg_cancel_btn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(51, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addGap(20, 20, 20)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel92, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(course_id_enter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(course_name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel87, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(course_duration, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel90, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(course_credits, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel91, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(course_reg_btn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(course_reg_cancel_btn)
                .addGap(144, 144, 144)
                .addComponent(grad_select1, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(448, 448, 448))
        );

        javax.swing.GroupLayout courseLayout = new javax.swing.GroupLayout(course);
        course.setLayout(courseLayout);
        courseLayout.setHorizontalGroup(
            courseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(courseLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        courseLayout.setVerticalGroup(
            courseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, courseLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(courseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        bottom_panel.add(course, "card3");

        staff.setBackground(new java.awt.Color(153, 153, 153));

        jPanel11.setPreferredSize(new java.awt.Dimension(519, 627));

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("Staff Details");
        jLabel11.setFont(new java.awt.Font("Raleway SemiBold", 0, 18)); // NOI18N

        jButton3.setText("Update Table");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        search_staff_id.setText("Search by ID");
        search_staff_id.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                search_staff_idMouseClicked(evt);
            }
        });
        search_staff_id.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                search_staff_idActionPerformed(evt);
            }
        });

        search_staff_btn.setText("Search");
        search_staff_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                search_staff_btnActionPerformed(evt);
            }
        });

        staff_jTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Staff ID", "First Name", "Last Name", "Designation", "Qualification", "Gender"
            }
        ));
        jScrollPane4.setViewportView(staff_jTable);

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(search_staff_id, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(search_staff_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jScrollPane4)
            .addComponent(jButton3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(search_staff_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(search_staff_btn))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        staff_search_panel.setLayout(new java.awt.CardLayout());

        javax.swing.GroupLayout staff_init_panelLayout = new javax.swing.GroupLayout(staff_init_panel);
        staff_init_panel.setLayout(staff_init_panelLayout);
        staff_init_panelLayout.setHorizontalGroup(
            staff_init_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 723, Short.MAX_VALUE)
        );
        staff_init_panelLayout.setVerticalGroup(
            staff_init_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 311, Short.MAX_VALUE)
        );

        staff_search_panel.add(staff_init_panel, "card2");

        staff_fname_field.setText("-----------");

        staff_lname_field.setText("-----------");
        staff_lname_field.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                staff_lname_fieldActionPerformed(evt);
            }
        });

        staff_designation_field.setText("-----------");
        staff_designation_field.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                staff_designation_fieldActionPerformed(evt);
            }
        });

        jLabel64.setText("First Name");

        jLabel66.setText("Designation");

        update_staff_btn.setText("Update");
        update_staff_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                update_staff_btnActionPerformed(evt);
            }
        });

        delete_staff_btn.setText("Delete");
        delete_staff_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delete_staff_btnActionPerformed(evt);
            }
        });

        jLabel10.setText("Gender");

        jLabel14.setText("Address");

        jLabel15.setText("Email");

        jLabel16.setText("Telephone");

        jLabel17.setText("Date of Birth");

        jLabel18.setText("Qualification");

        staff_address_field.setText("-----------");
        staff_address_field.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                staff_address_fieldActionPerformed(evt);
            }
        });

        staff_dob_field.setText("-----------");
        staff_dob_field.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                staff_dob_fieldActionPerformed(evt);
            }
        });

        staff_tele_field.setText("-----------");
        staff_tele_field.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                staff_tele_fieldActionPerformed(evt);
            }
        });

        staff_gender_field.setText("-----------");
        staff_gender_field.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                staff_gender_fieldActionPerformed(evt);
            }
        });

        staff_email_field.setText("-----------");
        staff_email_field.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                staff_email_fieldActionPerformed(evt);
            }
        });

        staff_qualification_field.setText("-----------");
        staff_qualification_field.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                staff_qualification_fieldActionPerformed(evt);
            }
        });

        staff_joindate_field.setText("-----------");
        staff_joindate_field.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                staff_joindate_fieldActionPerformed(evt);
            }
        });

        jLabel19.setText("Date Joined");

        jLabel67.setText("Last Name");

        javax.swing.GroupLayout staff_result_panelLayout = new javax.swing.GroupLayout(staff_result_panel);
        staff_result_panel.setLayout(staff_result_panelLayout);
        staff_result_panelLayout.setHorizontalGroup(
            staff_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(staff_result_panelLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(staff_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel66)
                    .addComponent(jLabel64)
                    .addComponent(jLabel14)
                    .addComponent(jLabel16)
                    .addComponent(jLabel67))
                .addGap(18, 18, 18)
                .addGroup(staff_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(staff_result_panelLayout.createSequentialGroup()
                        .addGroup(staff_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(staff_lname_field, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(staff_tele_field, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(staff_designation_field, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(staff_address_field, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(staff_fname_field, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(66, 66, 66)
                        .addGroup(staff_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(staff_result_panelLayout.createSequentialGroup()
                                .addComponent(jLabel18)
                                .addGap(18, 18, 18)
                                .addComponent(staff_qualification_field, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(staff_result_panelLayout.createSequentialGroup()
                                .addComponent(jLabel19)
                                .addGap(18, 18, 18)
                                .addComponent(staff_joindate_field, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(staff_result_panelLayout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addGap(18, 18, 18)
                                .addComponent(staff_email_field, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(staff_result_panelLayout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addGap(18, 18, 18)
                                .addComponent(staff_gender_field, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(staff_result_panelLayout.createSequentialGroup()
                                .addComponent(jLabel17)
                                .addGap(18, 18, 18)
                                .addComponent(staff_dob_field, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(staff_result_panelLayout.createSequentialGroup()
                        .addComponent(update_staff_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(delete_staff_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(79, Short.MAX_VALUE))
        );
        staff_result_panelLayout.setVerticalGroup(
            staff_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, staff_result_panelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(staff_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel64)
                    .addComponent(staff_fname_field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(staff_email_field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(staff_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(staff_lname_field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel67)
                    .addComponent(staff_gender_field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addGap(10, 10, 10)
                .addGroup(staff_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(staff_designation_field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel66)
                    .addComponent(staff_dob_field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(staff_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(staff_address_field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14)
                    .addComponent(staff_qualification_field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(staff_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(staff_joindate_field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(staff_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(staff_tele_field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel16)
                        .addComponent(jLabel19)))
                .addGap(56, 56, 56)
                .addGroup(staff_result_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(update_staff_btn)
                    .addComponent(delete_staff_btn))
                .addContainerGap(58, Short.MAX_VALUE))
        );

        staff_search_panel.add(staff_result_panel, "card3");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(staff_search_panel, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(staff_search_panel, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel2.setPreferredSize(new java.awt.Dimension(502, 627));

        fname_staff.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fname_staffMouseClicked(evt);
            }
        });

        lname_staff.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lname_staffMouseClicked(evt);
            }
        });

        telephone_staff.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                telephone_staffMouseClicked(evt);
            }
        });

        email_staff.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                email_staffMouseClicked(evt);
            }
        });

        address_staff.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                address_staffMouseClicked(evt);
            }
        });

        gender_staff.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Male", "Female", "Other"}));

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Register New Staff Member");
        jLabel3.setFont(new java.awt.Font("Raleway SemiBold", 0, 18)); // NOI18N

        designation_staff.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] {"Lecturer", "Instructor", "Other"}));
        designation_staff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                designation_staffActionPerformed(evt);
            }
        });

        staff_reg_btn.setText("Register");
        staff_reg_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                staff_reg_btnActionPerformed(evt);
            }
        });

        staff_reg_cancel_btn.setText("Cancel");
        staff_reg_cancel_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                staff_reg_cancel_btnMouseClicked(evt);
            }
        });
        staff_reg_cancel_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                staff_reg_cancel_btnActionPerformed(evt);
            }
        });

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel9.setText("Qualification");

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel12.setText("Designation");

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel13.setText("Gender");

        jLabel47.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel47.setText("Date of Birth");

        jLabel58.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel58.setText("First Name");

        jLabel59.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel59.setText("Telephone");

        jLabel60.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel60.setText("Email");

        jLabel61.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel61.setText("Last Name");

        jLabel68.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel68.setText("Address");

        jLabel69.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel69.setText("Staff ID");

        staff_id.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                staff_idMouseClicked(evt);
            }
        });
        staff_id.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                staff_idActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE))
                            .addComponent(jLabel47, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel60, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel59, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel61, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel58, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel68, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel69, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(gender_staff, 0, 265, Short.MAX_VALUE)
                            .addComponent(designation_staff, 0, 265, Short.MAX_VALUE)
                            .addComponent(email_staff, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                            .addComponent(telephone_staff, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                            .addComponent(address_staff, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                            .addComponent(fname_staff, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                            .addComponent(lname_staff, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                            .addComponent(staff_id, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                            .addComponent(birthday_selector_staff, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(qualification_staff)))
                    .addComponent(staff_reg_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 398, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(staff_reg_cancel_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 398, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(56, Short.MAX_VALUE))
            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel69, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(staff_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fname_staff, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel58, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lname_staff, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel61, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(address_staff, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel68, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(birthday_selector_staff, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(telephone_staff, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel59, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(email_staff, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel60, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(gender_staff, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(qualification_staff, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(designation_staff, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(staff_reg_btn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(staff_reg_cancel_btn)
                .addGap(205, 205, 205))
        );

        javax.swing.GroupLayout staffLayout = new javax.swing.GroupLayout(staff);
        staff.setLayout(staffLayout);
        staffLayout.setHorizontalGroup(
            staffLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(staffLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, 747, Short.MAX_VALUE)
                .addContainerGap())
        );
        staffLayout.setVerticalGroup(
            staffLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(staffLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(staffLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 616, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        bottom_panel.add(staff, "card5");

        lab.setBackground(new java.awt.Color(153, 153, 153));

        jLabel71.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel71.setText("Lab Schedule");
        jLabel71.setFont(new java.awt.Font("Raleway SemiBold", 0, 18)); // NOI18N

        assign_lab_show_btn.setText("Assign a New Lab");
        assign_lab_show_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                assign_lab_show_btnActionPerformed(evt);
            }
        });

        lab_schedule_jTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Time", "Lab", "Subject"
            }
        ));
        jScrollPane6.setViewportView(lab_schedule_jTable);

        select_date.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" }));

        get_schedule_btn.setText("Get Schedule");
        get_schedule_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                get_schedule_btnActionPerformed(evt);
            }
        });

        schedule_day.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        schedule_day.setFont(new java.awt.Font("Raleway SemiBold", 0, 18)); // NOI18N

        get_today_schedule_btn.setText("Get Today's Schedule");
        get_today_schedule_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                get_today_schedule_btnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane6)
            .addComponent(assign_lab_show_btn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addComponent(select_date, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(get_schedule_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(get_today_schedule_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(schedule_day, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                .addComponent(schedule_day, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(select_date, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(get_schedule_btn)
                    .addComponent(get_today_schedule_btn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(assign_lab_show_btn)
                .addContainerGap())
        );

        lab_main_panel.setLayout(new java.awt.CardLayout());

        javax.swing.GroupLayout lab_init_panelLayout = new javax.swing.GroupLayout(lab_init_panel);
        lab_init_panel.setLayout(lab_init_panelLayout);
        lab_init_panelLayout.setHorizontalGroup(
            lab_init_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 723, Short.MAX_VALUE)
        );
        lab_init_panelLayout.setVerticalGroup(
            lab_init_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 215, Short.MAX_VALUE)
        );

        lab_main_panel.add(lab_init_panel, "card2");

        jLabel79.setText("Lab ID");

        jLabel80.setText("Subject");

        jLabel81.setText("Time");

        jLabel83.setText("Date");

        lab_id_list.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { }));
        lab_id_list.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lab_id_listActionPerformed(evt);
            }
        });

        lab_subject_list.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { }));

        lab_date_list.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" }));

        assign_session_btn.setText("Assign Lab");
        assign_session_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                assign_session_btnActionPerformed(evt);
            }
        });

        delete_session_btn.setText("Delete");
        delete_session_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delete_session_btnActionPerformed(evt);
            }
        });

        cancel_session_btn.setText("Cancel");
        cancel_session_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancel_session_btnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout lab_assign_panelLayout = new javax.swing.GroupLayout(lab_assign_panel);
        lab_assign_panel.setLayout(lab_assign_panelLayout);
        lab_assign_panelLayout.setHorizontalGroup(
            lab_assign_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lab_assign_panelLayout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addGroup(lab_assign_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(lab_assign_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel83)
                        .addComponent(jLabel80)
                        .addComponent(jLabel79))
                    .addGroup(lab_assign_panelLayout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(jLabel81)))
                .addGap(24, 24, 24)
                .addGroup(lab_assign_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lab_id_list, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lab_subject_list, 0, 186, Short.MAX_VALUE)
                    .addComponent(lab_date_list, 0, 186, Short.MAX_VALUE)
                    .addComponent(lab_time, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(68, 68, 68)
                .addGroup(lab_assign_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(assign_session_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(delete_session_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cancel_session_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(178, Short.MAX_VALUE))
        );
        lab_assign_panelLayout.setVerticalGroup(
            lab_assign_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lab_assign_panelLayout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(lab_assign_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel79)
                    .addComponent(lab_id_list, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(assign_session_btn))
                .addGap(18, 18, 18)
                .addGroup(lab_assign_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel80)
                    .addComponent(lab_subject_list, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(delete_session_btn))
                .addGap(18, 18, 18)
                .addGroup(lab_assign_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab_date_list, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel83)
                    .addComponent(cancel_session_btn))
                .addGap(18, 18, 18)
                .addGroup(lab_assign_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel81)
                    .addComponent(lab_time, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(32, Short.MAX_VALUE))
        );

        lab_main_panel.add(lab_assign_panel, "card3");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lab_main_panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addComponent(jLabel71, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel71, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lab_main_panel, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel6.setPreferredSize(new java.awt.Dimension(502, 870));

        lab_building.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lab_buildingMouseClicked(evt);
            }
        });

        lab_floor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lab_floorMouseClicked(evt);
            }
        });

        jLabel82.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel82.setText("Add New Lab");
        jLabel82.setFont(new java.awt.Font("Raleway SemiBold", 0, 18)); // NOI18N

        add_lab_btn.setText("Add Lab");
        add_lab_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                add_lab_btnActionPerformed(evt);
            }
        });

        lab_cancel_btn.setText("Cancel");
        lab_cancel_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lab_cancel_btnMouseClicked(evt);
            }
        });
        lab_cancel_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lab_cancel_btnActionPerformed(evt);
            }
        });

        jLabel98.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel98.setText("Building");

        jLabel99.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel99.setText("Floor");

        jLabel101.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel101.setText("Lab ID");

        lab_id_enter.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lab_id_enterMouseClicked(evt);
            }
        });
        lab_id_enter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lab_id_enterActionPerformed(evt);
            }
        });

        lab_jTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Lab ID", "Building", "Floor"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                true, true, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane7.setViewportView(lab_jTable);

        jLabel84.setText("Lab Details");
        jLabel84.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGap(136, 136, 136)
                .addComponent(jLabel84)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel84)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2))
        );

        lab_delete_main.setLayout(new java.awt.CardLayout());

        javax.swing.GroupLayout lab_delete_initLayout = new javax.swing.GroupLayout(lab_delete_init);
        lab_delete_init.setLayout(lab_delete_initLayout);
        lab_delete_initLayout.setHorizontalGroup(
            lab_delete_initLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 342, Short.MAX_VALUE)
        );
        lab_delete_initLayout.setVerticalGroup(
            lab_delete_initLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 51, Short.MAX_VALUE)
        );

        lab_delete_main.add(lab_delete_init, "card2");

        delete_lab_btn.setText("Delete");
        delete_lab_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delete_lab_btnActionPerformed(evt);
            }
        });

        delete_lab_cancel_btn.setText("Cancel");
        delete_lab_cancel_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delete_lab_cancel_btnActionPerformed(evt);
            }
        });

        delete_lab_list.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] {  }));

        javax.swing.GroupLayout lab_delete_resultLayout = new javax.swing.GroupLayout(lab_delete_result);
        lab_delete_result.setLayout(lab_delete_resultLayout);
        lab_delete_resultLayout.setHorizontalGroup(
            lab_delete_resultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lab_delete_resultLayout.createSequentialGroup()
                .addComponent(delete_lab_list, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(delete_lab_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(delete_lab_cancel_btn, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE))
        );
        lab_delete_resultLayout.setVerticalGroup(
            lab_delete_resultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lab_delete_resultLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(lab_delete_resultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(delete_lab_btn)
                    .addComponent(delete_lab_cancel_btn)
                    .addComponent(delete_lab_list, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lab_delete_main.add(lab_delete_result, "card3");

        delete_lab_panel_btn.setText("Delete Lab");
        delete_lab_panel_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delete_lab_panel_btnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel82, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel99, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel98, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel101, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lab_building, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lab_floor, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(lab_id_enter, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(86, 86, 86)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(add_lab_btn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lab_cancel_btn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(delete_lab_panel_btn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lab_delete_main, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(74, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel82)
                .addGap(20, 20, 20)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel101, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lab_id_enter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab_building, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel98, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab_floor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel99, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(add_lab_btn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lab_cancel_btn)
                .addGap(38, 38, 38)
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(delete_lab_panel_btn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lab_delete_main, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(49, 49, 49))
        );

        javax.swing.GroupLayout labLayout = new javax.swing.GroupLayout(lab);
        lab.setLayout(labLayout);
        labLayout.setHorizontalGroup(
            labLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(labLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        labLayout.setVerticalGroup(
            labLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(labLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(labLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(labLayout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 619, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        bottom_panel.add(lab, "card7");

        mark.setBackground(new java.awt.Color(153, 153, 153));

        jLabel112.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel112.setText("Add Student Mark");
        jLabel112.setFont(new java.awt.Font("Raleway SemiBold", 0, 18)); // NOI18N

        mark_options_pane.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        mark_options_pane.setLayout(new java.awt.CardLayout());

        mark_options_init.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout mark_options_initLayout = new javax.swing.GroupLayout(mark_options_init);
        mark_options_init.setLayout(mark_options_initLayout);
        mark_options_initLayout.setHorizontalGroup(
            mark_options_initLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 502, Short.MAX_VALUE)
        );
        mark_options_initLayout.setVerticalGroup(
            mark_options_initLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 171, Short.MAX_VALUE)
        );

        mark_options_pane.add(mark_options_init, "card2");

        mark_options_select.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel113.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel113.setText("Add Marks to a Task");
        jLabel113.setFont(new java.awt.Font("Raleway", 1, 14)); // NOI18N

        task_type_list.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "In-class Assignment", "Take Home Assignment", "Semester Exam", "Project", "Report", "Practical" }));

        task_id_field.setText("Enter Task Name");
        task_id_field.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                task_id_fieldMouseClicked(evt);
            }
        });

        jLabel116.setText("Mark Awarded");

        jLabel117.setText("Total Mark");

        add_task_mark_btn.setText("Add Task Mark");
        add_task_mark_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                add_task_mark_btnActionPerformed(evt);
            }
        });

        jLabel135.setText("Task Type");

        jLabel138.setText("Task Name");

        add_task_mark_btn1.setText("Cancel");
        add_task_mark_btn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                add_task_mark_btn1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout mark_options_selectLayout = new javax.swing.GroupLayout(mark_options_select);
        mark_options_select.setLayout(mark_options_selectLayout);
        mark_options_selectLayout.setHorizontalGroup(
            mark_options_selectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mark_options_selectLayout.createSequentialGroup()
                .addGroup(mark_options_selectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(mark_options_selectLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(add_task_mark_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, mark_options_selectLayout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addGroup(mark_options_selectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel135)
                            .addComponent(jLabel138))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(mark_options_selectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(task_type_list, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(task_id_field, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(mark_options_selectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mark_options_selectLayout.createSequentialGroup()
                        .addGap(0, 31, Short.MAX_VALUE)
                        .addGroup(mark_options_selectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mark_options_selectLayout.createSequentialGroup()
                                .addComponent(jLabel117)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(total_mark_field, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mark_options_selectLayout.createSequentialGroup()
                                .addComponent(jLabel116)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(awarded_mark_field, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(add_task_mark_btn1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(25, 25, 25))
            .addComponent(jLabel113, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        mark_options_selectLayout.setVerticalGroup(
            mark_options_selectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mark_options_selectLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel113)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(mark_options_selectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(task_id_field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel138)
                    .addComponent(jLabel117)
                    .addComponent(total_mark_field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mark_options_selectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(task_type_list, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel135)
                    .addComponent(awarded_mark_field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel116))
                .addGap(18, 18, 18)
                .addGroup(mark_options_selectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(add_task_mark_btn1)
                    .addComponent(add_task_mark_btn))
                .addContainerGap(33, Short.MAX_VALUE))
        );

        mark_options_pane.add(mark_options_select, "card3");

        mark_stu_id2.setText("Enter Student ID");
        mark_stu_id2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mark_stu_id2MouseClicked(evt);
            }
        });

        student_mark_option.setText("Select Student");
        student_mark_option.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                student_mark_optionActionPerformed(evt);
            }
        });

        stu_sub_mark_panel.setLayout(new java.awt.CardLayout());

        javax.swing.GroupLayout stu_sub_mark_initLayout = new javax.swing.GroupLayout(stu_sub_mark_init);
        stu_sub_mark_init.setLayout(stu_sub_mark_initLayout);
        stu_sub_mark_initLayout.setHorizontalGroup(
            stu_sub_mark_initLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 237, Short.MAX_VALUE)
        );
        stu_sub_mark_initLayout.setVerticalGroup(
            stu_sub_mark_initLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 118, Short.MAX_VALUE)
        );

        stu_sub_mark_panel.add(stu_sub_mark_init, "card2");

        jLabel137.setText("Select Subject ");

        task_subject_list.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] {}));
        task_subject_list.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                task_subject_listActionPerformed(evt);
            }
        });

        show_mark_pane_btn.setText("Add Marks");
        show_mark_pane_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                show_mark_pane_btnActionPerformed(evt);
            }
        });

        show_mark_table_btn.setText("Show Subject-Wise Task Marks");
        show_mark_table_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                show_mark_table_btnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout stu_sub_mark_selectLayout = new javax.swing.GroupLayout(stu_sub_mark_select);
        stu_sub_mark_select.setLayout(stu_sub_mark_selectLayout);
        stu_sub_mark_selectLayout.setHorizontalGroup(
            stu_sub_mark_selectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(stu_sub_mark_selectLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(stu_sub_mark_selectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(task_subject_list, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(stu_sub_mark_selectLayout.createSequentialGroup()
                        .addComponent(jLabel137)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(show_mark_pane_btn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(show_mark_table_btn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        stu_sub_mark_selectLayout.setVerticalGroup(
            stu_sub_mark_selectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(stu_sub_mark_selectLayout.createSequentialGroup()
                .addComponent(jLabel137)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(task_subject_list, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(show_mark_pane_btn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                .addComponent(show_mark_table_btn))
        );

        stu_sub_mark_panel.add(stu_sub_mark_select, "card3");

        delete_task_panel.setLayout(new java.awt.CardLayout());

        javax.swing.GroupLayout delete_task_initLayout = new javax.swing.GroupLayout(delete_task_init);
        delete_task_init.setLayout(delete_task_initLayout);
        delete_task_initLayout.setHorizontalGroup(
            delete_task_initLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 758, Short.MAX_VALUE)
        );
        delete_task_initLayout.setVerticalGroup(
            delete_task_initLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 63, Short.MAX_VALUE)
        );

        delete_task_panel.add(delete_task_init, "card2");

        jLabel142.setText("**Select Student ID and the Subject of the Task from options above.");
        jLabel142.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N

        delete_task_btn.setText("Delete Task");
        delete_task_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delete_task_btnActionPerformed(evt);
            }
        });

        jLabel143.setText("Enter Task ID ");

        delete_cancel_btn.setText("Cancel");

        javax.swing.GroupLayout delete_task_showLayout = new javax.swing.GroupLayout(delete_task_show);
        delete_task_show.setLayout(delete_task_showLayout);
        delete_task_showLayout.setHorizontalGroup(
            delete_task_showLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel142, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(delete_task_showLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel143)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(delete_task_field, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(delete_task_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(delete_cancel_btn, javax.swing.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                .addContainerGap())
        );
        delete_task_showLayout.setVerticalGroup(
            delete_task_showLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(delete_task_showLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel142)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(delete_task_showLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(delete_task_field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(delete_task_btn)
                    .addComponent(jLabel143)
                    .addComponent(delete_cancel_btn))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        delete_task_panel.add(delete_task_show, "card3");

        task_mark_panel.setLayout(new java.awt.CardLayout());

        javax.swing.GroupLayout stu_task_initLayout = new javax.swing.GroupLayout(stu_task_init);
        stu_task_init.setLayout(stu_task_initLayout);
        stu_task_initLayout.setHorizontalGroup(
            stu_task_initLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 758, Short.MAX_VALUE)
        );
        stu_task_initLayout.setVerticalGroup(
            stu_task_initLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 303, Short.MAX_VALUE)
        );

        task_mark_panel.add(stu_task_init, "card3");

        task_mark_jTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Task Name", "Subject ID", "Task Type", "Total Mark", "Marks Awarded"
            }
        ));
        jScrollPane9.setViewportView(task_mark_jTable);

        jLabel141.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel141.setText("Student Marks by Subject Tasks");
        jLabel141.setFont(new java.awt.Font("Raleway", 0, 18)); // NOI18N

        show_all_task_marks.setText("Show Marks from All Tasks");
        show_all_task_marks.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                show_all_task_marksActionPerformed(evt);
            }
        });

        show_delete_task_pane.setText("Delete Task Mark");
        show_delete_task_pane.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                show_delete_task_paneActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout stu_task_markLayout = new javax.swing.GroupLayout(stu_task_mark);
        stu_task_mark.setLayout(stu_task_markLayout);
        stu_task_markLayout.setHorizontalGroup(
            stu_task_markLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel141, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane9)
            .addGroup(stu_task_markLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(show_all_task_marks, javax.swing.GroupLayout.PREFERRED_SIZE, 364, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(show_delete_task_pane, javax.swing.GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE))
        );
        stu_task_markLayout.setVerticalGroup(
            stu_task_markLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, stu_task_markLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel141)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(stu_task_markLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(show_all_task_marks)
                    .addComponent(show_delete_task_pane))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        task_mark_panel.add(stu_task_mark, "card2");

        javax.swing.GroupLayout add_mark_panelLayout = new javax.swing.GroupLayout(add_mark_panel);
        add_mark_panel.setLayout(add_mark_panelLayout);
        add_mark_panelLayout.setHorizontalGroup(
            add_mark_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel112, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(add_mark_panelLayout.createSequentialGroup()
                .addGroup(add_mark_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, add_mark_panelLayout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(add_mark_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(mark_stu_id2)
                            .addComponent(student_mark_option, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(stu_sub_mark_panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mark_options_pane, javax.swing.GroupLayout.PREFERRED_SIZE, 510, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(add_mark_panelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(delete_task_panel, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addGroup(add_mark_panelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(task_mark_panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        add_mark_panelLayout.setVerticalGroup(
            add_mark_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(add_mark_panelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel112, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(add_mark_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(add_mark_panelLayout.createSequentialGroup()
                        .addComponent(mark_stu_id2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(student_mark_option)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(stu_sub_mark_panel, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addComponent(mark_options_pane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(task_mark_panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(delete_task_panel, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1))
        );

        view_mark_panel.setPreferredSize(new java.awt.Dimension(502, 870));

        jLabel136.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel136.setText("View Student Mark");
        jLabel136.setFont(new java.awt.Font("Raleway SemiBold", 0, 18)); // NOI18N

        get_mark_btn.setText("Get Marks");
        get_mark_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                get_mark_btnActionPerformed(evt);
            }
        });

        cancel_mark_btn.setText("Cancel");
        cancel_mark_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cancel_mark_btnMouseClicked(evt);
            }
        });
        cancel_mark_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancel_mark_btnActionPerformed(evt);
            }
        });

        jLabel139.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel139.setText("Student ID");

        mark_stu_id.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mark_stu_idMouseClicked(evt);
            }
        });
        mark_stu_id.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mark_stu_idActionPerformed(evt);
            }
        });

        marks_jTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Subject ID", "Subject", "Marks", "Semester", "Year"
            }
        ));
        jScrollPane10.setViewportView(marks_jTable);

        jLabel140.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel140.setText("Student Marks by Subject");
        jLabel140.setFont(new java.awt.Font("Raleway", 0, 18)); // NOI18N

        javax.swing.GroupLayout marks_paneLayout = new javax.swing.GroupLayout(marks_pane);
        marks_pane.setLayout(marks_paneLayout);
        marks_paneLayout.setHorizontalGroup(
            marks_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 490, Short.MAX_VALUE)
            .addComponent(jLabel140, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        marks_paneLayout.setVerticalGroup(
            marks_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, marks_paneLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel140)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 286, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout view_mark_panelLayout = new javax.swing.GroupLayout(view_mark_panel);
        view_mark_panel.setLayout(view_mark_panelLayout);
        view_mark_panelLayout.setHorizontalGroup(
            view_mark_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel136, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(view_mark_panelLayout.createSequentialGroup()
                .addGroup(view_mark_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(view_mark_panelLayout.createSequentialGroup()
                        .addGroup(view_mark_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(view_mark_panelLayout.createSequentialGroup()
                                .addGap(30, 30, 30)
                                .addComponent(jLabel139, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(mark_stu_id, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(view_mark_panelLayout.createSequentialGroup()
                                .addGap(86, 86, 86)
                                .addGroup(view_mark_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(get_mark_btn, javax.swing.GroupLayout.DEFAULT_SIZE, 342, Short.MAX_VALUE)
                                    .addComponent(cancel_mark_btn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addGap(0, 62, Short.MAX_VALUE))
                    .addGroup(view_mark_panelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(marks_pane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        view_mark_panelLayout.setVerticalGroup(
            view_mark_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(view_mark_panelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel136)
                .addGap(20, 20, 20)
                .addGroup(view_mark_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel139, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mark_stu_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(get_mark_btn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancel_mark_btn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(marks_pane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout markLayout = new javax.swing.GroupLayout(mark);
        mark.setLayout(markLayout);
        markLayout.setHorizontalGroup(
            markLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(markLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(view_mark_panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(add_mark_panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        markLayout.setVerticalGroup(
            markLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(markLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(markLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(view_mark_panel, javax.swing.GroupLayout.DEFAULT_SIZE, 619, Short.MAX_VALUE)
                    .addComponent(add_mark_panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        bottom_panel.add(mark, "card8");

        logout.setBackground(new java.awt.Color(153, 153, 153));

        logout_display.setLayout(new java.awt.CardLayout());

        javax.swing.GroupLayout logout_init_paneLayout = new javax.swing.GroupLayout(logout_init_pane);
        logout_init_pane.setLayout(logout_init_paneLayout);
        logout_init_paneLayout.setHorizontalGroup(
            logout_init_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 687, Short.MAX_VALUE)
        );
        logout_init_paneLayout.setVerticalGroup(
            logout_init_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 604, Short.MAX_VALUE)
        );

        logout_display.add(logout_init_pane, "card2");

        current_pw_field.setText("Current Password");
        current_pw_field.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                current_pw_fieldMouseClicked(evt);
            }
        });
        current_pw_field.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                current_pw_fieldActionPerformed(evt);
            }
        });

        new_pw_field.setText("Enter New Password");
        new_pw_field.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new_pw_fieldMouseClicked(evt);
            }
        });
        new_pw_field.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                new_pw_fieldActionPerformed(evt);
            }
        });

        confirm_pw_field.setText("Confirm Password");
        confirm_pw_field.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                confirm_pw_fieldMouseClicked(evt);
            }
        });
        confirm_pw_field.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                confirm_pw_fieldActionPerformed(evt);
            }
        });

        jLabel158.setText("Current Password");

        jLabel159.setText("New Password");

        jLabel160.setText("Confirm New Password");

        pw_change_btn.setText("Change Password");
        pw_change_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pw_change_btnActionPerformed(evt);
            }
        });

        cancel_pw_btn.setText("Cancel");
        cancel_pw_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancel_pw_btnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout change_pw_paneLayout = new javax.swing.GroupLayout(change_pw_pane);
        change_pw_pane.setLayout(change_pw_paneLayout);
        change_pw_paneLayout.setHorizontalGroup(
            change_pw_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, change_pw_paneLayout.createSequentialGroup()
                .addContainerGap(143, Short.MAX_VALUE)
                .addGroup(change_pw_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cancel_pw_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 406, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pw_change_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 406, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(change_pw_paneLayout.createSequentialGroup()
                        .addGroup(change_pw_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel158, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel159, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel160, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(18, 18, 18)
                        .addGroup(change_pw_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(new_pw_field, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(confirm_pw_field, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(current_pw_field, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(138, 138, 138))
        );
        change_pw_paneLayout.setVerticalGroup(
            change_pw_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(change_pw_paneLayout.createSequentialGroup()
                .addGap(69, 69, 69)
                .addGroup(change_pw_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(current_pw_field, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel158))
                .addGap(36, 36, 36)
                .addGroup(change_pw_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(new_pw_field, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel159))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(change_pw_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(confirm_pw_field, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel160))
                .addGap(18, 18, 18)
                .addComponent(pw_change_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancel_pw_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(293, Short.MAX_VALUE))
        );

        logout_display.add(change_pw_pane, "card3");

        current_pw_field1.setText("Current Password");
        current_pw_field1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                current_pw_field1MouseClicked(evt);
            }
        });
        current_pw_field1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                current_pw_field1ActionPerformed(evt);
            }
        });

        new_admin_field.setText("Enter New Admin ID");
        new_admin_field.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new_admin_fieldMouseClicked(evt);
            }
        });
        new_admin_field.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                new_admin_fieldActionPerformed(evt);
            }
        });

        new_admin_pw_field.setText("Enter New Password");
        new_admin_pw_field.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new_admin_pw_fieldMouseClicked(evt);
            }
        });
        new_admin_pw_field.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                new_admin_pw_fieldActionPerformed(evt);
            }
        });

        jLabel161.setText("New Password");

        jLabel162.setText("New Admin ID");

        jLabel163.setText("Current Password");

        add_admin_btn.setText("Add New Admin");
        add_admin_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                add_admin_btnActionPerformed(evt);
            }
        });

        cancel_admin_btn.setText("Cancel");
        cancel_admin_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancel_admin_btnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout new_admin_paneLayout = new javax.swing.GroupLayout(new_admin_pane);
        new_admin_pane.setLayout(new_admin_paneLayout);
        new_admin_paneLayout.setHorizontalGroup(
            new_admin_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, new_admin_paneLayout.createSequentialGroup()
                .addContainerGap(143, Short.MAX_VALUE)
                .addGroup(new_admin_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cancel_admin_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 406, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(add_admin_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 406, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(new_admin_paneLayout.createSequentialGroup()
                        .addGroup(new_admin_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel163, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel162, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel161, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(18, 18, 18)
                        .addGroup(new_admin_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(new_admin_field, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(new_admin_pw_field, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(current_pw_field1, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(138, 138, 138))
        );
        new_admin_paneLayout.setVerticalGroup(
            new_admin_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(new_admin_paneLayout.createSequentialGroup()
                .addGap(69, 69, 69)
                .addGroup(new_admin_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(current_pw_field1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel163))
                .addGap(36, 36, 36)
                .addGroup(new_admin_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(new_admin_field, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel162))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(new_admin_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(new_admin_pw_field, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel161))
                .addGap(18, 18, 18)
                .addComponent(add_admin_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancel_admin_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(293, Short.MAX_VALUE))
        );

        logout_display.add(new_admin_pane, "card4");

        logout_btn.setText("Logout");
        logout_btn.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        logout_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logout_btnActionPerformed(evt);
            }
        });

        new_admin_btn.setText("New Admin");
        new_admin_btn.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        new_admin_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                new_admin_btnActionPerformed(evt);
            }
        });

        change_pw_btn.setText("Change Password");
        change_pw_btn.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        change_pw_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                change_pw_btnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout logout_paneLayout = new javax.swing.GroupLayout(logout_pane);
        logout_pane.setLayout(logout_paneLayout);
        logout_paneLayout.setHorizontalGroup(
            logout_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(logout_paneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(logout_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(logout_btn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(new_admin_btn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 538, Short.MAX_VALUE)
                    .addComponent(change_pw_btn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 538, Short.MAX_VALUE))
                .addContainerGap())
        );
        logout_paneLayout.setVerticalGroup(
            logout_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(logout_paneLayout.createSequentialGroup()
                .addGap(61, 61, 61)
                .addComponent(logout_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41)
                .addComponent(change_pw_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(new_admin_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout logoutLayout = new javax.swing.GroupLayout(logout);
        logout.setLayout(logoutLayout);
        logoutLayout.setHorizontalGroup(
            logoutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(logoutLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(logout_display, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(logout_pane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        logoutLayout.setVerticalGroup(
            logoutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(logoutLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(logoutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(logout_pane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(logout_display, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        bottom_panel.add(logout, "card9");

        javax.swing.GroupLayout admin_paneLayout = new javax.swing.GroupLayout(admin_pane);
        admin_pane.setLayout(admin_paneLayout);
        admin_paneLayout.setHorizontalGroup(
            admin_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(admin_paneLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(admin_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(top_menu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bottom_panel, javax.swing.GroupLayout.PREFERRED_SIZE, 1280, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        admin_paneLayout.setVerticalGroup(
            admin_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(admin_paneLayout.createSequentialGroup()
                .addGap(0, 5, Short.MAX_VALUE)
                .addComponent(top_menu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(bottom_panel, javax.swing.GroupLayout.PREFERRED_SIZE, 630, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 5, Short.MAX_VALUE))
        );

        main_back_pane.add(admin_pane, "card2");

        student_pane.setBackground(new java.awt.Color(153, 153, 153));

        jLabel107.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel107.setText("Student Details");
        jLabel107.setFont(new java.awt.Font("Raleway SemiBold", 0, 18)); // NOI18N

        jLabel127.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel127.setText("NIC");
        jLabel127.setFont(new java.awt.Font("Raleway", 0, 14)); // NOI18N

        jLabel128.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel128.setText("Name");
        jLabel128.setFont(new java.awt.Font("Raleway", 0, 14)); // NOI18N

        jLabel129.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel129.setFont(new java.awt.Font("Raleway", 0, 14)); // NOI18N

        jLabel130.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel130.setText("Date Joined");
        jLabel130.setFont(new java.awt.Font("Raleway", 0, 14)); // NOI18N

        jLabel131.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel131.setText("Course");
        jLabel131.setFont(new java.awt.Font("Raleway", 0, 14)); // NOI18N

        jLabel132.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel132.setText("Address");
        jLabel132.setFont(new java.awt.Font("Raleway", 0, 14)); // NOI18N

        jLabel133.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel133.setText("GPA");
        jLabel133.setFont(new java.awt.Font("Raleway", 0, 14)); // NOI18N

        jLabel134.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel134.setText("Rank");
        jLabel134.setFont(new java.awt.Font("Raleway", 0, 14)); // NOI18N

        stu_marks_jTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Subject", "Mark", "Year", "Semester", "Type"
            }
        ));
        jScrollPane8.setViewportView(stu_marks_jTable);

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane8)
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE)
        );

        stu_logout_btn.setText("Logout");
        stu_logout_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stu_logout_btnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout stu_detail_paneLayout = new javax.swing.GroupLayout(stu_detail_pane);
        stu_detail_pane.setLayout(stu_detail_paneLayout);
        stu_detail_paneLayout.setHorizontalGroup(
            stu_detail_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(stu_detail_paneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(stu_logout_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel107, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(stu_detail_paneLayout.createSequentialGroup()
                .addGap(101, 101, 101)
                .addGroup(stu_detail_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(stu_detail_paneLayout.createSequentialGroup()
                        .addGroup(stu_detail_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel133, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel134, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(stu_detail_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(stu_detail_paneLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel129, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(stu_detail_paneLayout.createSequentialGroup()
                                .addGap(39, 39, 39)
                                .addGroup(stu_detail_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(stu_rank_field, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(stu_gpa_field, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(stu_detail_paneLayout.createSequentialGroup()
                        .addGroup(stu_detail_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel128, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel132, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel127, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel131, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel130, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(39, 39, 39)
                        .addGroup(stu_detail_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(stu_detail_paneLayout.createSequentialGroup()
                                .addComponent(stu_address_field, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(stu_fname_field, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(stu_nic_field, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(stu_name_field, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(stu_detail_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, stu_detail_paneLayout.createSequentialGroup()
                                    .addComponent(stu_join_date_field, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(stu_join_date_field1))
                                .addComponent(stu_course_field, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(stu_detail_paneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        stu_detail_paneLayout.setVerticalGroup(
            stu_detail_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(stu_detail_paneLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(stu_detail_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel107, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(stu_detail_paneLayout.createSequentialGroup()
                        .addComponent(stu_logout_btn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(3, 3, 3)))
                .addGap(27, 27, 27)
                .addGroup(stu_detail_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel127)
                    .addComponent(stu_nic_field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(stu_detail_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel128)
                    .addComponent(stu_name_field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(stu_detail_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(stu_fname_field)
                    .addGroup(stu_detail_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel132)
                        .addComponent(stu_address_field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(stu_detail_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel131)
                    .addComponent(stu_course_field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(stu_detail_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel130)
                    .addComponent(stu_join_date_field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(stu_join_date_field1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(stu_detail_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel133)
                    .addComponent(stu_gpa_field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(stu_detail_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel134)
                    .addComponent(stu_rank_field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(64, 64, 64)
                .addComponent(jLabel129))
        );

        jLabel114.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel114.setText("Subject Selection");
        jLabel114.setFont(new java.awt.Font("Raleway SemiBold", 0, 18)); // NOI18N

        sub_select_pane.setLayout(new java.awt.CardLayout());

        jLabel111.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel111.setText("Select a Semester from 2 buttons above");
        jLabel111.setFont(new java.awt.Font("Raleway", 0, 18)); // NOI18N

        javax.swing.GroupLayout sub_select_init_paneLayout = new javax.swing.GroupLayout(sub_select_init_pane);
        sub_select_init_pane.setLayout(sub_select_init_paneLayout);
        sub_select_init_paneLayout.setHorizontalGroup(
            sub_select_init_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel111, javax.swing.GroupLayout.DEFAULT_SIZE, 688, Short.MAX_VALUE)
        );
        sub_select_init_paneLayout.setVerticalGroup(
            sub_select_init_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sub_select_init_paneLayout.createSequentialGroup()
                .addGap(94, 94, 94)
                .addComponent(jLabel111)
                .addContainerGap(258, Short.MAX_VALUE))
        );

        sub_select_pane.add(sub_select_init_pane, "card2");

        opt_sub_pane.setBorder(new javax.swing.border.MatteBorder(null));

        jLabel118.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel118.setText("Optional Subjects");

        jLabel123.setText("Subject 1");

        opt_sub1_list.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "None" }));

        opt_sub2_list.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "None" }));

        jLabel124.setText("Subject 2");

        jLabel125.setText("Subject 3");

        opt_sub3_list.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "None" }));

        opt_sub4_list.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "None" }));

        jLabel126.setText("Subject 4");

        javax.swing.GroupLayout opt_sub_paneLayout = new javax.swing.GroupLayout(opt_sub_pane);
        opt_sub_pane.setLayout(opt_sub_paneLayout);
        opt_sub_paneLayout.setHorizontalGroup(
            opt_sub_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel118, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(opt_sub_paneLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(opt_sub_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(opt_sub_paneLayout.createSequentialGroup()
                        .addGroup(opt_sub_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel125)
                            .addComponent(jLabel126))
                        .addGap(18, 18, 18)
                        .addGroup(opt_sub_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(opt_sub3_list, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(opt_sub4_list, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(opt_sub_paneLayout.createSequentialGroup()
                        .addGroup(opt_sub_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel123)
                            .addComponent(jLabel124))
                        .addGap(18, 18, 18)
                        .addGroup(opt_sub_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(opt_sub1_list, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(opt_sub2_list, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        opt_sub_paneLayout.setVerticalGroup(
            opt_sub_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(opt_sub_paneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel118)
                .addGap(20, 20, 20)
                .addGroup(opt_sub_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel123)
                    .addComponent(opt_sub1_list, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(opt_sub_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel124)
                    .addComponent(opt_sub2_list, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(opt_sub_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel125)
                    .addComponent(opt_sub3_list, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(opt_sub_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel126)
                    .addComponent(opt_sub4_list, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        comp_sub_pane.setBorder(new javax.swing.border.MatteBorder(null));

        jLabel115.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel115.setText("Compulsory Subjects");

        jLabel119.setText("Subject 1");

        jLabel120.setText("Subject 2");

        jLabel121.setText("Subject 3");

        jLabel122.setText("Subject 4");

        sub1_list.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] {  }));

        sub2_list.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] {  }));

        sub3_list.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { }));

        sub4_list.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] {  }));

        javax.swing.GroupLayout comp_sub_paneLayout = new javax.swing.GroupLayout(comp_sub_pane);
        comp_sub_pane.setLayout(comp_sub_paneLayout);
        comp_sub_paneLayout.setHorizontalGroup(
            comp_sub_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel115, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(comp_sub_paneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(comp_sub_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(comp_sub_paneLayout.createSequentialGroup()
                        .addGroup(comp_sub_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel121)
                            .addComponent(jLabel122))
                        .addGap(18, 18, 18)
                        .addGroup(comp_sub_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(sub3_list, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(sub4_list, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(comp_sub_paneLayout.createSequentialGroup()
                        .addComponent(jLabel119)
                        .addGap(18, 18, 18)
                        .addComponent(sub1_list, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(comp_sub_paneLayout.createSequentialGroup()
                        .addComponent(jLabel120)
                        .addGap(18, 18, 18)
                        .addComponent(sub2_list, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        comp_sub_paneLayout.setVerticalGroup(
            comp_sub_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(comp_sub_paneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel115)
                .addGap(20, 20, 20)
                .addGroup(comp_sub_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel119)
                    .addComponent(sub1_list, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(comp_sub_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel120)
                    .addComponent(sub2_list, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(comp_sub_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel121)
                    .addComponent(sub3_list, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(comp_sub_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel122)
                    .addComponent(sub4_list, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel108.setText("*You may select a maximum of 4 optional subjects per semester.");
        jLabel108.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel109.setText("**Please select \"None\" option for the subjects you do not wish to choose.");
        jLabel109.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel110.setText(" ***If you are enrolled in February, you have to select subjects for 2 semesters.");
        jLabel110.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        change_student_subject.setText("Change Selection");
        change_student_subject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                change_student_subjectActionPerformed(evt);
            }
        });

        register_student_subject.setText("Confirm Selection");
        register_student_subject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                register_student_subjectActionPerformed(evt);
            }
        });

        chk_credits_btn.setText("Check Credits");
        chk_credits_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chk_credits_btnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout sub_select_res_paneLayout = new javax.swing.GroupLayout(sub_select_res_pane);
        sub_select_res_pane.setLayout(sub_select_res_paneLayout);
        sub_select_res_paneLayout.setHorizontalGroup(
            sub_select_res_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sub_select_res_paneLayout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addComponent(comp_sub_pane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 65, Short.MAX_VALUE)
                .addComponent(opt_sub_pane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(42, 42, 42))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, sub_select_res_paneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sub_select_res_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel108, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel109, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel110, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(sub_select_res_paneLayout.createSequentialGroup()
                        .addComponent(change_student_subject, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(register_student_subject, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(chk_credits_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        sub_select_res_paneLayout.setVerticalGroup(
            sub_select_res_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, sub_select_res_paneLayout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addGroup(sub_select_res_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(comp_sub_pane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(opt_sub_pane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel108)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel109)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel110)
                .addGap(25, 25, 25)
                .addGroup(sub_select_res_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(register_student_subject, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(change_student_subject, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chk_credits_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10))
        );

        sub_select_pane.add(sub_select_res_pane, "card3");

        feb_sem_btn.setText("February Semester");
        feb_sem_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                feb_sem_btnActionPerformed(evt);
            }
        });

        jul_sem_btn.setText("July Semester");
        jul_sem_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jul_sem_btnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout stu_subject_select_paneLayout = new javax.swing.GroupLayout(stu_subject_select_pane);
        stu_subject_select_pane.setLayout(stu_subject_select_paneLayout);
        stu_subject_select_paneLayout.setHorizontalGroup(
            stu_subject_select_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel114, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(stu_subject_select_paneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(stu_subject_select_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(stu_subject_select_paneLayout.createSequentialGroup()
                        .addComponent(feb_sem_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27)
                        .addComponent(jul_sem_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 331, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(sub_select_pane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        stu_subject_select_paneLayout.setVerticalGroup(
            stu_subject_select_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(stu_subject_select_paneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel114, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21)
                .addGroup(stu_subject_select_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(feb_sem_btn)
                    .addComponent(jul_sem_btn))
                .addGap(6, 6, 6)
                .addComponent(sub_select_pane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        credit_panel.setLayout(new java.awt.CardLayout());

        jLabel144.setText("Credits Needed Per Year");
        jLabel144.setFont(new java.awt.Font("Raleway", 0, 14)); // NOI18N

        jLabel145.setText("Total credit gain from subjects");
        jLabel145.setFont(new java.awt.Font("Raleway", 0, 14)); // NOI18N

        jLabel146.setText("Credits remaining to complete");
        jLabel146.setFont(new java.awt.Font("Raleway", 0, 14)); // NOI18N

        jLabel147.setText("Outstanding fees");
        jLabel147.setFont(new java.awt.Font("Raleway", 0, 14)); // NOI18N

        jLabel148.setText("Total fee for selected subjects");
        jLabel148.setFont(new java.awt.Font("Raleway", 0, 14)); // NOI18N

        jLabel149.setText("Total Fees Payable");
        jLabel149.setFont(new java.awt.Font("Raleway", 0, 14)); // NOI18N

        javax.swing.GroupLayout credit_pane_showLayout = new javax.swing.GroupLayout(credit_pane_show);
        credit_pane_show.setLayout(credit_pane_showLayout);
        credit_pane_showLayout.setHorizontalGroup(
            credit_pane_showLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(credit_pane_showLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(credit_pane_showLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel145)
                    .addComponent(jLabel146)
                    .addComponent(jLabel144, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(19, 19, 19)
                .addGroup(credit_pane_showLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(credit_gained_field, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(credits_per_year_field, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(credit_needed_field, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(44, 44, 44)
                .addGroup(credit_pane_showLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel148)
                    .addComponent(jLabel147, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel149, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(18, 18, 18)
                .addGroup(credit_pane_showLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(total_selection_fee, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fee_payable, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(outstanding_fee, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(38, Short.MAX_VALUE))
        );
        credit_pane_showLayout.setVerticalGroup(
            credit_pane_showLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(credit_pane_showLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(credit_pane_showLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel144)
                    .addComponent(credits_per_year_field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel148)
                    .addComponent(total_selection_fee, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(credit_pane_showLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel145)
                    .addComponent(credit_gained_field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel147)
                    .addComponent(outstanding_fee, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(credit_pane_showLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel146)
                    .addComponent(credit_needed_field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel149)
                    .addComponent(fee_payable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(72, Short.MAX_VALUE))
        );

        credit_panel.add(credit_pane_show, "card3");

        javax.swing.GroupLayout student_paneLayout = new javax.swing.GroupLayout(student_pane);
        student_pane.setLayout(student_paneLayout);
        student_paneLayout.setHorizontalGroup(
            student_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(student_paneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(stu_detail_pane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(student_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(credit_panel, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(stu_subject_select_pane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        student_paneLayout.setVerticalGroup(
            student_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(student_paneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(student_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(stu_detail_pane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(student_paneLayout.createSequentialGroup()
                        .addComponent(stu_subject_select_pane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(credit_panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        main_back_pane.add(student_pane, "card3");

        getContentPane().add(main_back_pane, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1280, 720));

        setSize(new java.awt.Dimension(1288, 757));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    //following codes are used to update data in combobox as they get changed from user input
    private void updateComboBox_course() 
    {
        String sql = "SELECT * from course";
        try
        {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while(rs.next())
            {
                course_id.addItem(rs.getString("co_name"));
            }
        }
        catch(SQLException e)
        {

        }
    }
    
    private void updateComboBox_course1()
    {
        String sql = "SELECT * from course";
        try
        {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while(rs.next())
            {
                course_list1.addItem(rs.getString("co_name"));
            }
        }
        catch(SQLException e)
        {

        }
    }
    
    private void updateComboBox_lecturer()
    {
        lecturer_list.removeAllItems();
        String sql = "SELECT * from staff WHERE staff_designation='Lecturer'";
        try
        {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while(rs.next())
            {
                lecturer_list.addItem(rs.getString("staff_fname")+" "+rs.getString("staff_lname"));
            }
        }
        catch(SQLException e)
        {

        }
    }
    
    private void updateComboBox_lecturer2()
    {
        lecturer_list2.removeAllItems();
        String sql = "SELECT * from staff WHERE staff_designation='Lecturer'";
        try
        {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while(rs.next())
            {
                lecturer_list2.addItem(rs.getString("staff_fname")+" "+rs.getString("staff_lname"));
            }
        }
        catch(SQLException e)
        {

        }
    }
    
    private void updateComboBox_instructor()
    {
        instructor_list1.removeAllItems();
        
        String sql = "SELECT * from staff WHERE staff_designation='Instructor'";
        try
        {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while(rs.next())
            {
                instructor_list1.addItem(rs.getString("staff_fname")+" "+rs.getString("staff_lname"));
            }
        }
        catch(SQLException e)
        {

        }
    }
    
    private void updateComboBox_subject()
    {
        subject_list1.removeAllItems();
        
        String sql = "SELECT * from subject";
        try
        {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while(rs.next())
            {
                subject_list1.addItem(rs.getString("sname"));
            }
        }
        catch(SQLException e)
        {

        }
    }
    
    private void updateComboBox_subject1()
    {
        lab_subject_list.removeAllItems();
        
        String sql = "SELECT * from subject";
        try
        {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while(rs.next())
            {
                lab_subject_list.addItem(rs.getString("sname"));
            }
        }
        catch(SQLException e)
        {

        }
    }
    
    
    private void updateComboBox_stu_subjectlists()
    {
        lab_subject_list.removeAllItems();
        sub1_list.removeAllItems();
        sub2_list.removeAllItems();
        sub3_list.removeAllItems();
        sub4_list.removeAllItems();
        opt_sub1_list.removeAllItems();
        opt_sub2_list.removeAllItems();
        opt_sub3_list.removeAllItems();
        opt_sub4_list.removeAllItems();
        opt_sub1_list.addItem("None");
        opt_sub2_list.addItem("None");
        opt_sub3_list.addItem("None");
        opt_sub4_list.addItem("None");

        
        
        
        String sql = "SELECT * from subject WHERE course_id=?";
        try
        {
            pst = conn.prepareStatement(sql);
            pst.setInt(1,Integer.parseInt(stu_course_field.getText()));
            rs = pst.executeQuery();
            
            while(rs.next())
            {
                sub1_list.addItem(rs.getString("sname"));
                sub2_list.addItem(rs.getString("sname"));
                sub3_list.addItem(rs.getString("sname"));
                sub4_list.addItem(rs.getString("sname"));
                opt_sub1_list.addItem(rs.getString("sname"));
                opt_sub2_list.addItem(rs.getString("sname"));
                opt_sub3_list.addItem(rs.getString("sname"));
                opt_sub4_list.addItem(rs.getString("sname"));
                
            }
        }
        catch(SQLException e)
        {

        }
    }
    
     private void updateComboBox_task_subject_list()
    {
        task_subject_list.removeAllItems();
        
        String sql = "SELECT * from student_mark WHERE student_id=?";
        try
        {
            pst = conn.prepareStatement(sql);
            pst.setInt(1,Integer.parseInt(mark_stu_id2.getText()));
            rs = pst.executeQuery();
            
            while(rs.next())
            {
                String sql2 = "SELECT * FROM subject WHERE subject_id=?";
                pst1 = conn.prepareStatement(sql2);
                pst1.setInt(1, Integer.parseInt(rs.getString("subject_id")));
                rs1 = pst1.executeQuery();
                if(rs1.next())
                {
                    task_subject_list.addItem(rs.getString("subject_id") + " - " + rs1.getString("sname"));
                }   
            }
        }
        catch(SQLException e)
        {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }
    
    
    
    private void updateComboBox_lab()
    {
        lab_id_list.removeAllItems();
        
        String sql = "SELECT * from lab";
        try
        {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while(rs.next())
            {
                lab_id_list.addItem(rs.getString("lab_id"));
            }
        }
        catch(SQLException e)
        {

        }
    }
    
    private void updateComboBox_lab1()
    {
        delete_lab_list.removeAllItems();
        
        String sql = "SELECT * from lab";
        try
        {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while(rs.next())
            {
                delete_lab_list.addItem(rs.getString("lab_id"));
            }
        }
        catch(SQLException e)
        {

        }
    }
    
    private void cour_btnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cour_btnMouseClicked

        bottom_panel.removeAll();
        bottom_panel.repaint();
        bottom_panel.revalidate();

        bottom_panel.add(course);
        bottom_panel.repaint();
        bottom_panel.revalidate();
        ImageIcon hover = new ImageIcon(getClass().getResource("/buttons/Course_50px_pressed.png"));
        cour_btn.setIcon(hover);
    }//GEN-LAST:event_cour_btnMouseClicked

    private void sub_btnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_sub_btnMouseClicked

        bottom_panel.removeAll();
        bottom_panel.repaint();
        bottom_panel.revalidate();

        bottom_panel.add(subject);
        bottom_panel.repaint();
        bottom_panel.revalidate();
        ImageIcon hover = new ImageIcon(getClass().getResource("/buttons/Book_50px_pressed.png"));
        sub_btn.setIcon(hover);
    }//GEN-LAST:event_sub_btnMouseClicked

    private void staff_btnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_staff_btnMouseClicked

        bottom_panel.removeAll();
        bottom_panel.repaint();
        bottom_panel.revalidate();

        bottom_panel.add(staff);
        bottom_panel.repaint();
        bottom_panel.revalidate();
        ImageIcon hover = new ImageIcon(getClass().getResource("/buttons/Lecturer_50px_pressed.png"));
        staff_btn.setIcon(hover);
    }//GEN-LAST:event_staff_btnMouseClicked

    private void home_btnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_home_btnMouseClicked

        bottom_panel.removeAll();
        bottom_panel.repaint();
        bottom_panel.revalidate();

        bottom_panel.add(home);
        bottom_panel.repaint();
        bottom_panel.revalidate();
        show_leaderboard();
        showTime();
        showDate();
        currentUser.setText(admin_id_field.getText());
        ImageIcon hover = new ImageIcon(getClass().getResource("/buttons/Home Page_50px_pressed.png"));
        home_btn.setIcon(hover);                
    }//GEN-LAST:event_home_btnMouseClicked

    private void semesterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_semesterActionPerformed


    }//GEN-LAST:event_semesterActionPerformed

    //function to register students based on student type
    private void student_reg_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_student_reg_btnActionPerformed
        if (Undergraduate.isSelected()==true)
        {
            Undergraduate input = new Undergraduate();
            input.setStudent_id(Integer.parseInt(student_id.getText()));
            input.setFname(fname.getText());
            input.setLname(lname.getText());
            input.setAddress(address.getText());
            input.setEmail(email.getText());
            input.setTelephone(Integer.parseInt(telephone.getText()));
            input.setCourse_id(course_id.getSelectedIndex()+1);
            input.setEnroll_sem((String)semester.getSelectedItem());
            input.setGender((String)gender.getSelectedItem());
            input.setType("Undergrad");
            input.setDob(birthday_selector.getDate());
            input.setDate_joined(new java.util.Date());
            try
            {
            input.setAlvl_rank(Integer.parseInt(alvl_island_rank.getText()));
            }
            catch(NumberFormatException e)
            {
            }
            input.setEng_result(eng_result.getText().charAt(0));
            input.setGk_result(gk_result.getText().charAt(0));
            input.setSubject1_result(result_1.getText().charAt(0));
            input.setSubject2_result(result_2.getText().charAt(0));
            input.setSubject3_result(result_3.getText().charAt(0));

            String sql1= "INSERT INTO student (student_id,fname,lname,telephone,email,address,course_id,dob,type,gender,enroll_semester,date_joined)" + " values(?,?,?,?,?,?,?,?,?,?,?,?)";
            String sql2= "INSERT INTO undergrad (grad_id,eng_result,result_1,result_2,result_3,gk_result,alvl_island_rank)" + " values(?,?,?,?,?,?,?)";
            try {
                pst = conn.prepareStatement(sql1);
                pst.setInt(1, input.getStudent_id());
                pst.setString(2, input.getFname());
                pst.setString(3, input.getLname());
                pst.setInt(4, input.getTelephone());
                pst.setString(5, input.getEmail());
                pst.setString(6, input.getAddress());
                pst.setInt(7, input.getCourse_id());
                java.sql.Date sqlDate = new java.sql.Date(input.getDob().getTime());
                pst.setDate(8,sqlDate);
                pst.setString(9, input.getType());
                pst.setString(10, input.getGender());
                pst.setString(11, input.getEnroll_sem());
                long mill = System.currentTimeMillis();
                pst.setDate(12, new java.sql.Date(mill));
                pst.execute();

                pst = conn.prepareStatement(sql2);
                pst.setInt(1,input.getStudent_id());
                pst.setString(2, Character.toString(input.getEng_result()));
                pst.setString(3, Character.toString(input.getSubject1_result()));
                pst.setString(4, Character.toString(input.getSubject2_result()));
                pst.setString(5, Character.toString(input.getSubject3_result()));
                pst.setString(6, Character.toString(input.getGk_result()));
                pst.setInt(7,input.getAlvl_rank());
                pst.execute();
                DefaultTableModel model = (DefaultTableModel)stu_jTable.getModel();
                model.setRowCount(0);
                show_student();
                JOptionPane.showMessageDialog(null, "Undergraduate Registered in the System!");               
            } catch (SQLException ex) {
                Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (Postgraduate.isSelected()==true)
        {
            Postgraduate input = new Postgraduate();
            input.setStudent_id(Integer.parseInt(student_id.getText()));
            input.setFname(fname.getText());
            input.setLname(lname.getText());
            input.setAddress(address.getText());
            input.setEmail(email.getText());
            input.setTelephone(Integer.parseInt(telephone.getText()));
            input.setCourse_id(course_id.getSelectedIndex()+1);
            input.setEnroll_sem((String)semester.getSelectedItem());
            input.setGender((String)gender.getSelectedItem());
            input.setType("Postgrad");
            input.setDob(birthday_selector.getDate());

            input.setInstitute(institute.getText());
            input.setQualification(qualification.getText());
            input.setYear_completed(Integer.parseInt(completed_year.getText()));


            String sql1= "INSERT INTO student (student_id,fname,lname,telephone,email,address,course_id,dob,type,gender,enroll_semester,date_joined)" + " values(?,?,?,?,?,?,?,?,?,?,?,?)";
            String sql2= "INSERT INTO postgrad (grad_id,qualification,institute,completed_year)" + " values(?,?,?,?)";
            try {
                pst = conn.prepareStatement(sql1);
                pst.setInt(1, input.getStudent_id());
                pst.setString(2, input.getFname());
                pst.setString(3, input.getLname());
                pst.setInt(4, input.getTelephone());
                pst.setString(5, input.getEmail());
                pst.setString(6, input.getAddress());
                pst.setInt(7, input.getCourse_id());
                java.sql.Date sqlDate = new java.sql.Date(input.getDob().getTime());
                pst.setDate(8,sqlDate);
                pst.setString(9, input.getType());
                pst.setString(10, input.getGender());
                pst.setString(11, input.getEnroll_sem());
                long mill = System.currentTimeMillis();
                pst.setDate(12, new java.sql.Date(mill));
                pst.execute();

                pst = conn.prepareStatement(sql2);
                pst.setInt(1,input.getStudent_id());
                pst.setString(2, input.getQualification());
                pst.setString(3, input.getInstitute());
                pst.setInt(4, input.getYear_completed());
                pst.execute();
                DefaultTableModel model = (DefaultTableModel)stu_jTable.getModel();
                model.setRowCount(0);
                show_student();
                JOptionPane.showMessageDialog(null, "Postgraduate Registered in the System!");

            } catch (SQLException ex) {
                Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_student_reg_btnActionPerformed

    private void course_idActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_course_idActionPerformed

    }//GEN-LAST:event_course_idActionPerformed

    private void fnameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fnameMouseClicked

        fname.setText("");
    }//GEN-LAST:event_fnameMouseClicked

    private void lnameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lnameMouseClicked

        lname.setText("");
    }//GEN-LAST:event_lnameMouseClicked

    private void telephoneMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_telephoneMouseClicked

        telephone.setText("");
    }//GEN-LAST:event_telephoneMouseClicked

    private void addressMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addressMouseClicked

        address.setText("");
    }//GEN-LAST:event_addressMouseClicked

    private void emailMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emailMouseClicked

        email.setText("");
    }//GEN-LAST:event_emailMouseClicked

    private void instituteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_instituteMouseClicked
        // TODO add your handling code here:
        institute.setText("");
    }//GEN-LAST:event_instituteMouseClicked

    private void qualificationMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_qualificationMouseClicked

        qualification.setText("");
    }//GEN-LAST:event_qualificationMouseClicked

    private void completed_yearMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_completed_yearMouseClicked

        completed_year.setText("");
    }//GEN-LAST:event_completed_yearMouseClicked

    private void gk_resultMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gk_resultMouseClicked

        gk_result.setText("");
    }//GEN-LAST:event_gk_resultMouseClicked

    private void result_1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_result_1MouseClicked

        result_1.setText("");
    }//GEN-LAST:event_result_1MouseClicked

    private void result_2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_result_2MouseClicked

        result_2.setText("");
    }//GEN-LAST:event_result_2MouseClicked

    private void result_3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_result_3MouseClicked

        result_3.setText("");
    }//GEN-LAST:event_result_3MouseClicked

    private void eng_resultMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_eng_resultMouseClicked

        eng_result.setText("");
    }//GEN-LAST:event_eng_resultMouseClicked

    private void student_reg_cancel_btnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_student_reg_cancel_btnMouseClicked




    }//GEN-LAST:event_student_reg_cancel_btnMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
         show_student();
    }//GEN-LAST:event_jButton1ActionPerformed

    public boolean isNumeric(String s) //check whether a given string is a number and can be converted to int
    {
        boolean result;
        try
        {
           Integer.parseInt(s);
           result = true;
        }
        catch(NumberFormatException e)
        {
            result = false;
        }
        return result;
    }
    //searching student from ID
    private void search_student_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_search_student_btnActionPerformed
        if (isNumeric(search_id.getText())== true)
        {

            try
            {
                String sql = "SELECT * FROM student WHERE student_id =?";
                pst = conn.prepareStatement(sql);
                pst.setString(1, search_id.getText());
                rs = pst.executeQuery();
                if(rs.next())
                {
                    if ("Undergrad".equals(rs.getString("type")))
                    {
                        stu_search_panel.removeAll();
                        stu_search_panel.repaint();
                        stu_search_panel.revalidate();
                        stu_search_panel.add(under_result_panel);
                        stu_search_panel.repaint();
                        stu_search_panel.revalidate();

                        fname_field1.setText(rs.getString("fname"));
                        lname_field1.setText(rs.getString("lname"));
                        gender_field1.setText(rs.getString("gender"));
                        address_field1.setText(rs.getString("address"));
                        email_field1.setText(rs.getString("email"));
                        semester_field1.setText(rs.getString("enroll_semester"));
                        type_field1.setText(rs.getString("type"));
                        dob_field1.setText(rs.getString("dob"));
                        tele_field1.setText(rs.getString("telephone"));
                        out_pay_field1.setText(rs.getString("fee_payable"));

                        String cid = rs.getString("course_id");
                        String sql2 = "SELECT co_name FROM course WHERE course_id="+cid;
                        pst = conn.prepareStatement(sql2);
                        rs = pst.executeQuery();
                        if(rs.next())
                        {
                            course_field1.setText(rs.getString("co_name"));
                        }

                        String sql3 = "SELECT * FROM undergrad WHERE grad_id=?";
                        pst = conn.prepareStatement(sql3);
                        pst.setString(1, search_id.getText());
                        rs = pst.executeQuery();
                        if(rs.next())
                        {
                            eng_res1.setText(rs.getString("eng_result"));
                            sub1_res1.setText(rs.getString("result_1"));
                            sub2_res1.setText(rs.getString("result_2"));
                            sub3_res1.setText(rs.getString("result_3"));
                            gk_res1.setText(rs.getString("gk_result"));
                            rank_res1.setText(rs.getString("alvl_island_rank"));
                        }

                    }

                    else if ("Postgrad".equals(rs.getString("type")))
                    {
                        stu_search_panel.removeAll();
                        stu_search_panel.repaint();
                        stu_search_panel.revalidate();
                        stu_search_panel.add(post_result_panel);
                        stu_search_panel.repaint();
                        stu_search_panel.revalidate();

                        fname_field2.setText(rs.getString("fname"));
                        lname_field2.setText(rs.getString("lname"));
                        gender_field2.setText(rs.getString("gender"));
                        address_field2.setText(rs.getString("address"));
                        email_field2.setText(rs.getString("email"));
                        semester_field2.setText(rs.getString("enroll_semester"));
                        type_field2.setText(rs.getString("type"));
                        dob_field2.setText(rs.getString("dob"));
                        tele_field2.setText(rs.getString("telephone"));
                        out_pay_field.setText(rs.getString("fee_payable"));

                        String cid = rs.getString("course_id");
                        String sql2 = "SELECT co_name FROM course WHERE course_id="+cid;
                        pst = conn.prepareStatement(sql2);
                        rs = pst.executeQuery();
                        if(rs.next())
                        {
                            course_field2.setText(rs.getString("co_name"));
                        }

                        String sql3 = "SELECT * FROM postgrad WHERE grad_id=?";
                        pst = conn.prepareStatement(sql3);
                        pst.setString(1, search_id.getText());
                        rs = pst.executeQuery();
                        if(rs.next())
                        {
                            institute_res.setText(rs.getString("institute"));
                            qualification_res.setText(rs.getString("qualification"));
                            complete_year_res.setText(rs.getString("completed_year"));
                        }
                    }

                }
                else
                {
                    JOptionPane.showMessageDialog(null, "ID not found! Please try again.");
                }
            }
            catch(SQLException e)
            {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        }
        else
        {
            JOptionPane.showMessageDialog(null, "Please Enter a Numeric Value for the Search Field.");
        }
    }//GEN-LAST:event_search_student_btnActionPerformed

    private void eng_res1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eng_res1ActionPerformed

    }//GEN-LAST:event_eng_res1ActionPerformed

    private void sub2_res1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sub2_res1ActionPerformed

    }//GEN-LAST:event_sub2_res1ActionPerformed

    private void sub1_res1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sub1_res1ActionPerformed

    }//GEN-LAST:event_sub1_res1ActionPerformed

    private void lname_field1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lname_field1ActionPerformed

    }//GEN-LAST:event_lname_field1ActionPerformed

    private void gk_res1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gk_res1ActionPerformed

    }//GEN-LAST:event_gk_res1ActionPerformed

    private void tele_field1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tele_field1ActionPerformed

    }//GEN-LAST:event_tele_field1ActionPerformed

    private void rank_res1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rank_res1ActionPerformed

    }//GEN-LAST:event_rank_res1ActionPerformed

    private void UndergraduateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UndergraduateActionPerformed

        grad_select.removeAll();
        grad_select.repaint();
        grad_select.revalidate();

        grad_select.add(undergrad);
        grad_select.repaint();
        grad_select.revalidate();
        if (Postgraduate.isSelected()==true)
        {
            Postgraduate.doClick();
        }
    }//GEN-LAST:event_UndergraduateActionPerformed

    private void PostgraduateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PostgraduateActionPerformed

        grad_select.removeAll();
        grad_select.repaint();
        grad_select.revalidate();

        grad_select.add(postgrad);
        grad_select.repaint();
        grad_select.revalidate();
        if (Undergraduate.isSelected()==true)
        {
            Undergraduate.doClick();
        }
    }//GEN-LAST:event_PostgraduateActionPerformed

    private void institute_resActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_institute_resActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_institute_resActionPerformed

    private void qualification_resActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_qualification_resActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_qualification_resActionPerformed

    private void complete_year_resActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_complete_year_resActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_complete_year_resActionPerformed

    private void lname_field2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lname_field2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_lname_field2ActionPerformed

    private void tele_field2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tele_field2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tele_field2ActionPerformed

    private void result_1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_result_1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_result_1ActionPerformed

    private void result_3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_result_3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_result_3ActionPerformed

    private void eng_resultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eng_resultActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_eng_resultActionPerformed

    private void student_idMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_student_idMouseClicked
        // TODO add your handling code here:
        student_id.setText("");
    }//GEN-LAST:event_student_idMouseClicked

    private void alvl_island_rankMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_alvl_island_rankMouseClicked
        alvl_island_rank.setText("");
    }//GEN-LAST:event_alvl_island_rankMouseClicked

    private void alvl_island_rankActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_alvl_island_rankActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_alvl_island_rankActionPerformed

    private void dob_field1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dob_field1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dob_field1ActionPerformed

    private void search_idMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_search_idMouseClicked
        // TODO add your handling code here:
        search_id.setText("");
    }//GEN-LAST:event_search_idMouseClicked

    private void update_postgrad_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_update_postgrad_btnActionPerformed
        int confirm = JOptionPane.showConfirmDialog(null, "Do you wish to update details?","Confirm Update Record",JOptionPane.YES_NO_OPTION);

        if (confirm == 0)
        {
            try
            {
            String sql1 = "UPDATE student SET fname=?, lname=?, telephone=?, email=?, address=?, dob=?, type=?, gender=? WHERE student_id=?";
            pst = conn.prepareStatement(sql1);
            pst.setString(1,fname_field2.getText());
            pst.setString(2,lname_field2.getText());
            pst.setInt(3,Integer.parseInt(tele_field2.getText()));
            pst.setString(4,email_field2.getText());
            pst.setString(5,address_field2.getText());
            //converting String input to Date object
            String dateString = dob_field2.getText();
            Date birth = new Date();
            try {
                birth = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
            } catch (ParseException ex) {
                Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
            }
            java.sql.Date sqldate = new java.sql.Date(birth.getTime());
            pst.setDate(6,sqldate);
            pst.setString(7,type_field2.getText());
            pst.setString(8,gender_field2.getText());
            pst.setString(9, search_id.getText());
            pst.executeUpdate();

            String sql2 = "UPDATE postgrad SET qualification?, institute=?, completed_year=? WHERE grad_id=?";
            pst = conn.prepareStatement(sql2);
            pst.setString(1, qualification_res.getText());
            pst.setString(2, institute_res.getText());
            pst.setInt(3, Integer.parseInt(complete_year_res.getText()));
            pst.executeUpdate();
            }
            catch(SQLException e)
            {

            }
            DefaultTableModel model = (DefaultTableModel)stu_jTable.getModel();
            model.setRowCount(0);
            show_student();
        }
    }//GEN-LAST:event_update_postgrad_btnActionPerformed

    private void delete_postgrad_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delete_postgrad_btnActionPerformed
        int confirm = JOptionPane.showConfirmDialog(null, "Do you wish to update details?","Confirm Update Record",JOptionPane.YES_NO_OPTION);

        if (confirm == 0)
        {
            try
            {
            String sql = "DELETE from student WHERE student_id=?";
            pst = conn.prepareStatement(sql);
            pst.setString(1, search_id.getText());
            pst.execute();
            }
            catch(SQLException e)
            {
                System.out.println(e.getMessage());
            }
            fname_field2.setText("");
            lname_field2.setText("");
            tele_field2.setText("");
            email_field2.setText("");
            address_field2.setText("");
            dob_field2.setText("");
            type_field2.setText("");
            gender_field2.setText("");
            qualification_res.setText("");
            institute_res.setText("");
            complete_year_res.setText("");
            semester_field2.setText("");
            course_field2.setText("");
            DefaultTableModel model = (DefaultTableModel)stu_jTable.getModel();
            model.setRowCount(0);
            show_student();
        }
    }//GEN-LAST:event_delete_postgrad_btnActionPerformed

    private void update_undergrad_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_update_undergrad_btnActionPerformed
        int confirm = JOptionPane.showConfirmDialog(null, "Do you wish to update details?","Confirm Update Record",JOptionPane.YES_NO_OPTION);

        if (confirm == 0)
        {
            try
            {
            String sql1 = "UPDATE student SET fname=?, lname=?, telephone=?, email=?, address=?, dob=?, type=?, gender=? WHERE student_id=?";
            pst = conn.prepareStatement(sql1);
            pst.setString(1,fname_field1.getText());
            pst.setString(2,lname_field1.getText());
            pst.setInt(3,Integer.parseInt(tele_field1.getText()));
            pst.setString(4,email_field1.getText());
            pst.setString(5,address_field1.getText());
            //converting String input to Date object
            String dateString = dob_field1.getText();
            Date birth = new Date();
            try {
                birth = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
            } catch (ParseException ex) {
                Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
            }
            java.sql.Date sqldate = new java.sql.Date(birth.getTime());
            pst.setDate(6,sqldate);
            pst.setString(7,type_field1.getText());
            pst.setString(8,gender_field1.getText());
            pst.setString(9, search_id.getText());
            pst.executeUpdate();

            String sql2 = "UPDATE undergrad SET eng_result=?, result_1=?, result_2=?, result_3=?, alvl_island_rank=? WHERE grad_id=?";
            pst = conn.prepareStatement(sql2);
            pst.setString(1, eng_res1.getText());
            pst.setString(2, sub1_res1.getText());
            pst.setString(3, sub2_res1.getText());
            pst.setString(4, sub3_res1.getText());
            pst.setString(3, gk_res1.getText());
            pst.setInt(3, Integer.parseInt(rank_res1.getText()));
            pst.executeUpdate();
            }
            catch(SQLException e)
            {
            }
            DefaultTableModel model = (DefaultTableModel)stu_jTable.getModel();
            model.setRowCount(0);
            show_student();
        }
    }//GEN-LAST:event_update_undergrad_btnActionPerformed

    private void delete_undergrad_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delete_undergrad_btnActionPerformed
        int confirm = JOptionPane.showConfirmDialog(null, "Do you wish to update details?","Confirm Update Record",JOptionPane.YES_NO_OPTION);

        if (confirm == 0)
        {
            try
            {
            String sql = "DELETE from student WHERE student_id=?";
            pst = conn.prepareStatement(sql);
            pst.setString(1, search_id.getText());
            pst.execute();
            }
            catch(SQLException e)
            {
            }
            fname_field1.setText("");
            lname_field1.setText("");
            tele_field1.setText("");
            email_field1.setText("");
            address_field1.setText("");
            dob_field1.setText("");
            type_field1.setText("");
            gender_field1.setText("");
            eng_res1.setText("");
            sub1_res1.setText("");
            sub2_res1.setText("");
            sub3_res1.setText("");
            gk_res1.setText("");
            rank_res1.setText("");
            course_field1.setText("");
            semester_field1.setText("");
            DefaultTableModel model = (DefaultTableModel)stu_jTable.getModel();
            model.setRowCount(0);
            show_student();
        }
    }//GEN-LAST:event_delete_undergrad_btnActionPerformed

    private void student_reg_cancel_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_student_reg_cancel_btnActionPerformed
        student_id.setText("NIC (without 'V')");
        fname.setText("");
        lname.setText("");
        telephone.setText("");
        address.setText("");
        email.setText("");
        eng_result.setText("English Result");
        result_1.setText("Subject 1 Result");
        result_2.setText("Subject 2 Result");
        result_3.setText("Subject 3 Result");
        gk_result.setText("General Knowledge Result");
        alvl_island_rank.setText("A/L Island Rank");
        institute.setText("Institute");
        qualification.setText("Qualification");
        completed_year.setText("Year of Completion");
        
        grad_select.removeAll();
        grad_select.repaint();
        grad_select.revalidate();
        grad_select.add(initpanel);
        grad_select.repaint();
        grad_select.revalidate();
        if(Undergraduate.isSelected())
        {
            Undergraduate.doClick();
        }
        if(Postgraduate.isSelected())
        {
            Postgraduate.doClick();
        }
    }//GEN-LAST:event_student_reg_cancel_btnActionPerformed

    private void student_idActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_student_idActionPerformed

    }//GEN-LAST:event_student_idActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        DefaultTableModel model = (DefaultTableModel)course_jTable.getModel();
        model.setRowCount(0);
        show_course();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void search_course_idMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_search_course_idMouseClicked
        // TODO add your handling code here:
        search_course_id.setText("");
    }//GEN-LAST:event_search_course_idMouseClicked

    private void search_course_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_search_course_btnActionPerformed
        if (isNumeric(search_course_id.getText())== true)
        {
            try
            {
                String sql = "SELECT * FROM course WHERE course_id =?";
                pst = conn.prepareStatement(sql);
                pst.setString(1, search_course_id.getText());
                rs = pst.executeQuery();
                if(rs.next())
                {
                    course_search_panel.removeAll();
                    course_search_panel.repaint();
                    course_search_panel.revalidate();
                    course_search_panel.add(course_result_panel);
                    course_search_panel.repaint();
                    course_search_panel.revalidate();

                    course_name_field.setText(rs.getString("co_name"));
                    course_duration_field.setText(rs.getString("duration"));
                    course_credit_field.setText(rs.getString("credits_to_complete"));
                    }

                }
            catch(SQLException e)
            {
                JOptionPane.showMessageDialog(email, e.getMessage());
            }
        }  
    }//GEN-LAST:event_search_course_btnActionPerformed

    private void course_nameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_course_nameMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_course_nameMouseClicked

    private void course_durationMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_course_durationMouseClicked
        course_duration.setText("");
    }//GEN-LAST:event_course_durationMouseClicked

    private void course_creditsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_course_creditsMouseClicked
        course_credits.setText("");
    }//GEN-LAST:event_course_creditsMouseClicked

    private void course_reg_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_course_reg_btnActionPerformed
        Course input = new Course();
        int err = 0;
        if("".equals(course_name.getText())||"".equals(course_id_enter.getText())||"".equals(course_credits.getText())||"".equals(course_duration.getText()))
        {
            JOptionPane.showMessageDialog(null, "Please Enter Valid Details for All Fields!");
        }
        else
        {
            input.setCourse_name(course_name.getText());

            try{
            input.setCourse_id(Integer.parseInt(course_id_enter.getText()));
            input.setCourse_credits(Integer.parseInt(course_credits.getText()));
            input.setCourse_duration(Integer.parseInt(course_duration.getText()));

            }
            catch(NumberFormatException e)
            {
                err = 1;
            }

            if (err == 0)
            {
                String sql = "INSERT INTO course (course_id,co_name,duration,credits_to_complete)" + " VALUES(?,?,?,?)";
                try
                {
                    pst = conn.prepareStatement(sql);
                    pst.setInt(1, input.getCourse_id());
                    pst.setString(2, input.getCourse_name());
                    pst.setInt(3, input.getCourse_duration());
                    pst.setInt(4, input.getCourse_credits());
                    pst.execute();
                    DefaultTableModel model = (DefaultTableModel)course_jTable.getModel();
                    model.setRowCount(0);
                    show_course();
                    JOptionPane.showMessageDialog(null, "The new course is added to the System!");
                }
                catch(SQLException e)
                {
                    System.out.println(e.getMessage());
                }
            }
            else
            {
                JOptionPane.showMessageDialog(null, "Error! Course NOT added to the system. Please enter valid details.");
            }
        }
        updateComboBox_course();
        updateComboBox_course1();
        updateComboBox_lecturer();
        updateComboBox_lecturer2();
        updateComboBox_instructor();
        updateComboBox_subject();
        updateComboBox_subject1();
        updateComboBox_lab();
        updateComboBox_lab1();
    }//GEN-LAST:event_course_reg_btnActionPerformed

    private void course_reg_cancel_btnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_course_reg_cancel_btnMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_course_reg_cancel_btnMouseClicked

    private void course_reg_cancel_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_course_reg_cancel_btnActionPerformed
        course_name.setText("");
        course_id_enter.setText("");
        course_duration.setText("In Years");
        course_credits.setText("Credits Needed to Complete");
    }//GEN-LAST:event_course_reg_cancel_btnActionPerformed

    private void course_id_enterMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_course_id_enterMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_course_id_enterMouseClicked

    private void course_id_enterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_course_id_enterActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_course_id_enterActionPerformed

    private void delete_course_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delete_course_btnActionPerformed
        int confirm = JOptionPane.showConfirmDialog(null, "Do you wish to delete details?","Confirm Delete Record",JOptionPane.YES_NO_OPTION);

        if (confirm == 0)
        {
            try
            {
            String sql = "DELETE from course WHERE course_id=?";
            pst = conn.prepareStatement(sql);
            pst.setString(1, search_course_id.getText());
            pst.execute();
            DefaultTableModel model = (DefaultTableModel)course_jTable.getModel();
            model.setRowCount(0);
            show_course();
            }
            catch(SQLException e)
            {
            }
            course_name_field.setText("");
            course_credit_field.setText("");
            course_duration_field.setText("");
        }
    }//GEN-LAST:event_delete_course_btnActionPerformed

    private void update_course_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_update_course_btnActionPerformed
        int confirm = JOptionPane.showConfirmDialog(null, "Do you wish to update details?","Confirm Update Record",JOptionPane.YES_NO_OPTION);
        int err = 0;
        if (confirm == 0)
        {
            try
            {
            String sql1 = "UPDATE course SET co_name=?, duration=?, credits_to_complete=? WHERE course_id=?";
            pst = conn.prepareStatement(sql1);
            pst.setString(1,course_name_field.getText());
            try
            {
            pst.setInt(2,Integer.parseInt(course_duration_field.getText()));
            pst.setInt(3,Integer.parseInt(course_credit_field.getText()));
            }
            catch(NumberFormatException | SQLException e)
            {
                JOptionPane.showMessageDialog(null, "Enter numeric values for Duration and Credits fields!");
                err = 1;
            }
            pst.setString(4, search_course_id.getText());
            if (err == 0)
            {
                pst.executeUpdate();
                DefaultTableModel model = (DefaultTableModel)course_jTable.getModel();
                model.setRowCount(0);
                show_course();
            }
            }
            catch(SQLException e)
            {
                JOptionPane.showMessageDialog(email, e.getMessage());
            }
        }
        updateComboBox_course();
        updateComboBox_course1();
        updateComboBox_lecturer();
        updateComboBox_lecturer2();
        updateComboBox_instructor();
        updateComboBox_subject();
        updateComboBox_subject1();
        updateComboBox_lab();
        updateComboBox_lab1();
    }//GEN-LAST:event_update_course_btnActionPerformed

    private void course_duration_fieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_course_duration_fieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_course_duration_fieldActionPerformed

    private void search_course_idActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_search_course_idActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_search_course_idActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        DefaultTableModel model = (DefaultTableModel)staff_jTable.getModel();
        model.setRowCount(0);
        show_staff();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void search_staff_idMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_search_staff_idMouseClicked
        search_staff_id.setText("");
    }//GEN-LAST:event_search_staff_idMouseClicked

    private void search_staff_idActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_search_staff_idActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_search_staff_idActionPerformed

    private void search_staff_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_search_staff_btnActionPerformed
        try
        {
            String sql = "SELECT * FROM staff WHERE staff_id =?";
            pst = conn.prepareStatement(sql);
            pst.setString(1, search_staff_id.getText());
            rs = pst.executeQuery();
            if(rs.next())
            {
                staff_search_panel.removeAll();
                staff_search_panel.repaint();
                staff_search_panel.revalidate();       
                staff_search_panel.add(staff_result_panel);
                staff_search_panel.repaint();
                staff_search_panel.revalidate();

                staff_fname_field.setText(rs.getString("staff_fname"));
                staff_lname_field.setText(rs.getString("staff_lname"));
                staff_designation_field.setText(rs.getString("staff_designation"));
                staff_address_field.setText(rs.getString("staff_address"));
                staff_tele_field.setText(rs.getString("staff_tele"));
                staff_email_field.setText(rs.getString("staff_email"));
                staff_qualification_field.setText(rs.getString("staff_qualification"));
                staff_dob_field.setText(rs.getString("staff_dob"));
                staff_joindate_field.setText(rs.getString("date_joined"));
                staff_gender_field.setText(rs.getString("staff_gender"));
                }

            }
        catch(SQLException e)
        {
            JOptionPane.showMessageDialog(email, e.getMessage());
        }

        
        
    }//GEN-LAST:event_search_staff_btnActionPerformed

    private void staff_designation_fieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_staff_designation_fieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_staff_designation_fieldActionPerformed

    private void update_staff_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_update_staff_btnActionPerformed
        int confirm = JOptionPane.showConfirmDialog(null, "Do you wish to update details?","Confirm Update Record",JOptionPane.YES_NO_OPTION);
        int err = 0;
        if (confirm == 0)
        {
            try
            {
            String sql1 = "UPDATE staff SET staff_fname=?, staff_lname=?, staff_address=?, staff_dob=?, staff_tele=?, staff_email=?, staff_gender=?, staff_qualification=?, staff_designation=? WHERE staff_id=?";
            pst = conn.prepareStatement(sql1);
            try
            {
            pst.setString(1,staff_fname_field.getText());                
            pst.setString(2,staff_lname_field.getText());
            pst.setString(3,staff_address_field.getText());
            String dateString = staff_dob_field.getText();
            Date birth = new Date();
            try {
                birth = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
            } catch (ParseException ex) {
                Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
            }
            java.sql.Date sqldate = new java.sql.Date(birth.getTime());
            pst.setDate(4,sqldate);
            try
            {
            pst.setInt(5,Integer.parseInt(staff_tele_field.getText()));
            pst.setInt(10,Integer.parseInt(search_staff_id.getText()));
            }
            catch(NumberFormatException | SQLException e)
            {
                
            }
            pst.setString(6,staff_email_field.getText());
            pst.setString(7,staff_gender_field.getText());
            pst.setString(8,staff_qualification_field.getText());
            pst.setString(9,staff_designation_field.getText());
            }
            catch(NumberFormatException | SQLException e)
            {
                JOptionPane.showMessageDialog(null, "Enter numeric values for Duration and Credits fields!");
                err = 1;
            }
            if (err == 0)
            {
                pst.executeUpdate();
                DefaultTableModel model = (DefaultTableModel)staff_jTable.getModel();
                model.setRowCount(0);
                show_staff();
            }
            }
            catch(SQLException e)
            {
                JOptionPane.showMessageDialog(email, e.getMessage());
            }
        }
        updateComboBox_course();
        updateComboBox_course1();
        updateComboBox_lecturer();
        updateComboBox_lecturer2();
        updateComboBox_instructor();
        updateComboBox_subject();
        updateComboBox_subject1();
        updateComboBox_lab();
        updateComboBox_lab1();        
    }//GEN-LAST:event_update_staff_btnActionPerformed

    private void delete_staff_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delete_staff_btnActionPerformed
        int confirm = JOptionPane.showConfirmDialog(null, "Do you wish to delete details?","Confirm Delete Record",JOptionPane.YES_NO_OPTION);

        if (confirm == 0)
        {
            try
            {
            String sql = "DELETE from staff WHERE staff_id=?";
            pst = conn.prepareStatement(sql);
            pst.setString(1, search_staff_id.getText());
            pst.execute();
            DefaultTableModel model = (DefaultTableModel)staff_jTable.getModel();
            model.setRowCount(0);
            show_staff();
            }
            catch(SQLException e)
            {
                JOptionPane.showMessageDialog(null, "Cannot delete! Staff member is assigned to a subject. To prevent this error, remove the staff member from subject assignment.");
            }
            search_staff_id.setText("");
            staff_fname_field.setText("");
            staff_lname_field.setText("");
            staff_designation_field.setText("");
            staff_address_field.setText("");
            staff_email_field.setText("");
            staff_dob_field.setText("");
            staff_tele_field.setText("");
            staff_gender_field.setText("");
            staff_qualification_field.setText("");
            staff_joindate_field.setText("");      
            updateComboBox_course();
            updateComboBox_course1();
            updateComboBox_lecturer();
            updateComboBox_lecturer2();
            updateComboBox_instructor();
            updateComboBox_subject();
            updateComboBox_subject1();
            updateComboBox_lab();
            updateComboBox_lab1();
        }
    }//GEN-LAST:event_delete_staff_btnActionPerformed

    private void fname_staffMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fname_staffMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_fname_staffMouseClicked

    private void lname_staffMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lname_staffMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_lname_staffMouseClicked

    private void telephone_staffMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_telephone_staffMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_telephone_staffMouseClicked

    private void email_staffMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_email_staffMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_email_staffMouseClicked

    private void address_staffMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_address_staffMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_address_staffMouseClicked

    private void staff_reg_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_staff_reg_btnActionPerformed
        int err = 0;
        Staff input = new Staff();
        try
        {
        input.setStaff_id(Integer.parseInt(staff_id.getText()));
        input.setTelephone(Integer.parseInt(telephone_staff.getText()));
        }
        catch(NumberFormatException e)
        {
            err = 1;
            JOptionPane.showMessageDialog(null, "Error! Please enter numerics to Staff ID and Telephone fields");
            JOptionPane.showMessageDialog(null, e.getMessage());           
        }
        
        input.setFname(fname_staff.getText());
        input.setLname(lname_staff.getText());
        input.setAddress(address_staff.getText());
        input.setEmail(email_staff.getText());
        input.setGender((String)gender_staff.getSelectedItem());
        input.setDesignation((String)designation_staff.getSelectedItem());
        input.setQualification(qualification_staff.getText());
        input.setDob(birthday_selector_staff.getDate());
        Date curDate = new java.util.Date();
        input.setDate_joined(curDate);
        
        String sql = "INSERT INTO staff(staff_id,staff_fname,staff_lname,staff_address,staff_dob,staff_tele,staff_email,staff_gender,staff_qualification,staff_designation,date_joined)" + " VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try {
            pst = conn.prepareStatement(sql);
            pst.setInt(1, input.getStaff_id());
            pst.setString(2, input.getFname());
            pst.setString(3, input.getLname());
            pst.setString(4, input.getAddress());
            java.sql.Date sqlDate = new java.sql.Date(input.getDob().getTime());
            pst.setDate(5, sqlDate);
            pst.setInt(6, input.getTelephone());
            pst.setString(7, input.getEmail());
            pst.setString(8, input.getGender());
            pst.setString(9, input.getQualification());
            pst.setString(10, input.getDesignation());
            java.sql.Date sqlDateJoined = new java.sql.Date(input.getDate_joined().getTime());
            pst.setDate(11, sqlDateJoined);
            if (err==0)
            {
                pst.execute();
                JOptionPane.showMessageDialog(null, "Staff Member Registered in the System!");
                DefaultTableModel model = (DefaultTableModel)staff_jTable.getModel();
                model.setRowCount(0);
                show_staff();
                updateComboBox_lecturer();
            }           
        } catch (SQLException e) {     
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        updateComboBox_course();
        updateComboBox_course1();
        updateComboBox_lecturer();
        updateComboBox_lecturer2();
        updateComboBox_instructor();
        updateComboBox_subject();
        updateComboBox_subject1();
        updateComboBox_lab();
        updateComboBox_lab1();  
    }//GEN-LAST:event_staff_reg_btnActionPerformed

    private void staff_reg_cancel_btnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_staff_reg_cancel_btnMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_staff_reg_cancel_btnMouseClicked

    private void staff_reg_cancel_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_staff_reg_cancel_btnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_staff_reg_cancel_btnActionPerformed

    private void designation_staffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_designation_staffActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_designation_staffActionPerformed

    private void staff_idMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_staff_idMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_staff_idMouseClicked

    private void staff_idActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_staff_idActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_staff_idActionPerformed

    private void staff_address_fieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_staff_address_fieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_staff_address_fieldActionPerformed

    private void staff_dob_fieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_staff_dob_fieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_staff_dob_fieldActionPerformed

    private void staff_tele_fieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_staff_tele_fieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_staff_tele_fieldActionPerformed

    private void staff_gender_fieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_staff_gender_fieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_staff_gender_fieldActionPerformed

    private void staff_email_fieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_staff_email_fieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_staff_email_fieldActionPerformed

    private void staff_qualification_fieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_staff_qualification_fieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_staff_qualification_fieldActionPerformed

    private void staff_joindate_fieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_staff_joindate_fieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_staff_joindate_fieldActionPerformed

    private void staff_lname_fieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_staff_lname_fieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_staff_lname_fieldActionPerformed

    private void stud_btnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_stud_btnMouseExited
        // TODO add your handling code here:
        ImageIcon revert = new ImageIcon(getClass().getResource("/buttons/Student Male_50px.png"));
        stud_btn.setIcon(revert);
    }//GEN-LAST:event_stud_btnMouseExited

    private void stud_btnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_stud_btnMouseEntered
        // TODO add your handling code here:
        ImageIcon hover = new ImageIcon(getClass().getResource("/buttons/Student Male_50px_hover.png"));
        stud_btn.setIcon(hover);
    }//GEN-LAST:event_stud_btnMouseEntered

    private void stud_btnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_stud_btnMouseClicked
        // TODO add your handling code here:
        ImageIcon press = new ImageIcon(getClass().getResource("/buttons/Student Male_50px_pressed.png"));
        stud_btn.setIcon(press);

        bottom_panel.removeAll();
        bottom_panel.repaint();
        bottom_panel.revalidate();

        bottom_panel.add(student);
        bottom_panel.repaint();
        bottom_panel.revalidate();
    }//GEN-LAST:event_stud_btnMouseClicked

    private void subject_nameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_subject_nameMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_subject_nameMouseClicked

    private void subject_reg_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subject_reg_btnActionPerformed
        //Adding subject details
        Subject input = new Subject();
        int lec_id = 0;
        try
        {
        input.setSubject_code(Integer.parseInt(subject_id_enter.getText()));
        input.setDuration(Integer.parseInt((String)duration_list.getSelectedItem()));
        input.setSubject_credits(Integer.parseInt(subject_credits.getText()));
        input.setSubject_fee(Double.parseDouble(subject_fee.getText()));        
        }
        catch(NumberFormatException e)
        {
            
        }
        input.setSubject_name(subject_name.getText());
        
        String fullname = (String)lecturer_list.getSelectedItem();
        String firstname = fullname.substring(0, fullname.indexOf(" "));        
        String sql = "SELECT * FROM staff WHERE staff_fname=?";
        try
        {
        pst = conn.prepareStatement(sql);
        pst.setString(1, firstname);
        rs = pst.executeQuery();
        while(rs.next())
        {
            lec_id = rs.getInt("staff_id");
        }
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
        if (lec_id != 0)
        {
            input.setSubject_lecturer(lec_id);
        }
        else
        {
            JOptionPane.showMessageDialog(null, "ERROR!");
        }
        
        String course_name = (String)course_list1.getSelectedItem();
        String sql2 = "SELECT * FROM course WHERE co_name=?";
        try {
            pst = conn.prepareStatement(sql2);
            pst.setString(1, course_name);
            rs = pst.executeQuery();
            if (rs.next())
            {
                input.setSubject_course(rs.getInt("course_id"));
            }
                    
        } catch (SQLException ex) {
            Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String sql1 = "INSERT INTO subject(subject_id,sname,sduration,credits_awarded,sfee,lecturer_id,course_id) VALUES (?,?,?,?,?,?,?)";
        try
        {
        pst = conn.prepareStatement(sql1);
        pst.setInt(1, input.getSubject_code());
        pst.setString(2, input.getSubject_name());
        pst.setInt(3, input.getDuration());
        pst.setInt(4, input.getSubject_credits());
        pst.setDouble(5, input.getSubject_fee());
        pst.setInt(6, input.getSubject_lecturer());
        pst.setInt(7, input.getSubject_course());
        pst.execute();
        JOptionPane.showMessageDialog(null, "Subject Added to the System!");
        updateComboBox_subject();
        show_subject();
        
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(null, "Duplicate Entry! Please Check!");
        }
        updateComboBox_course();
        updateComboBox_course1();
        updateComboBox_lecturer();
        updateComboBox_lecturer2();
        updateComboBox_instructor();
        updateComboBox_subject();
        updateComboBox_subject1();
        updateComboBox_lab();
        updateComboBox_lab1();
    }//GEN-LAST:event_subject_reg_btnActionPerformed

    private void subject_reg_cancel_btnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_subject_reg_cancel_btnMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_subject_reg_cancel_btnMouseClicked

    private void subject_reg_cancel_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subject_reg_cancel_btnActionPerformed
        subject_id_enter.setText("");
        subject_name.setText("");
        subject_fee.setText("In LKR");
        subject_credits.setText("");
        
        instructor_select.removeAll();
        instructor_select.repaint();
        instructor_select.revalidate();
        instructor_select.add(instructor_init_panel);
        instructor_select.repaint();
        instructor_select.revalidate();
        if(add_instructor_show_btn.isSelected())
        {
            add_instructor_show_btn.doClick();
        }
        
    }//GEN-LAST:event_subject_reg_cancel_btnActionPerformed

    private void subject_id_enterMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_subject_id_enterMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_subject_id_enterMouseClicked

    private void subject_id_enterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subject_id_enterActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_subject_id_enterActionPerformed

    private void update_subject_tableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_update_subject_tableActionPerformed
        show_subject();
        show_instructor_result();
    }//GEN-LAST:event_update_subject_tableActionPerformed

    private void search_subject_idMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_search_subject_idMouseClicked
        search_subject_id.setText("");
    }//GEN-LAST:event_search_subject_idMouseClicked

    private void search_subject_idActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_search_subject_idActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_search_subject_idActionPerformed

    private void search_subject_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_search_subject_btnActionPerformed
        //extract data from DB to text fields
        String sql = "SELECT * FROM subject WHERE subject_id=?";
        try
        {
            pst = conn.prepareStatement(sql);
            pst.setString(1, search_subject_id.getText());
            rs = pst.executeQuery();
            if(rs.next())
            {
                subject_search_panel.removeAll();
                subject_search_panel.repaint();
                subject_search_panel.revalidate();

                subject_search_panel.add(subject_result_panel);
                subject_search_panel.repaint();
                subject_search_panel.revalidate();

                subject_name_field.setText(rs.getString("sname"));
                subject_duration_field.setText(rs.getString("sduration"));
                subject_fee_field.setText(rs.getString("sfee"));
                subject_credit_field.setText(rs.getString("credits_awarded"));
                String sql2 = "SELECT * FROM staff WHERE staff_id=?";
                pst1 = conn.prepareStatement(sql2);
                pst1.setInt(1, Integer.parseInt(rs.getString("lecturer_id")));
                rs1 = pst1.executeQuery();
                show_instructor_result();
                if (rs1.next())
                {
                    subject_lecturer_field.setText(rs1.getString("staff_fname")+" "+rs1.getString("staff_lname"));
                }
                else
                {
                    System.out.print("Err");
                }
            }
            else
            {
                subject_search_panel.removeAll();
                subject_search_panel.repaint();
                subject_search_panel.revalidate();

                subject_search_panel.add(subject_init_panel);
                subject_search_panel.repaint();
                subject_search_panel.revalidate();
                JOptionPane.showMessageDialog(null, "Invalid subject code! Please try again.");
            }
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
    }//GEN-LAST:event_search_subject_btnActionPerformed

    private void subject_lecturer_fieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subject_lecturer_fieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_subject_lecturer_fieldActionPerformed

    private void update_subject_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_update_subject_btnActionPerformed
        int confirm = JOptionPane.showConfirmDialog(null, "Do you wish to update details?","Confirm Update Record",JOptionPane.YES_NO_OPTION);
        int err = 0;
        if (confirm == 0)
        {
            try
            {
            String sql1 = "UPDATE subject SET sname=?, sduration=?, credits_awarded=?, sfee=? WHERE subject_id=?";
            pst = conn.prepareStatement(sql1);
            try
            {
            pst.setString(1,subject_name_field.getText());                
            try
            {
            pst.setInt(2,Integer.parseInt((String)duration_list2.getSelectedItem()));
            pst.setInt(3,Integer.parseInt(subject_credit_field.getText()));
            pst.setDouble(4,Double.parseDouble(subject_fee_field.getText()));
            pst.setInt(5, Integer.parseInt(search_subject_id.getText()));
            pst.executeUpdate();
            }
            catch(NumberFormatException | SQLException e)
            {
                System.out.println(e.getMessage());
                JOptionPane.showMessageDialog(null, "Enter numeric values for Duration, Credits and Subject Fee fields!");
                err = 1;
            }
            
            String lec_fullname = (String)lecturer_list2.getSelectedItem();
            String lec_names[] = lec_fullname.split(" ");
            String sql2 = "SELECT * FROM staff WHERE staff_fname=? AND staff_lname=?";
            int extracted_lec_id = 0;
            pst = conn.prepareStatement(sql2);
            pst.setString(1, lec_names[0]);
            pst.setString(2, lec_names[1]);
            if(err == 0)
            {
            rs = pst.executeQuery();
            }
            if(rs.next())
            {
                extracted_lec_id = rs.getInt("staff_id");
            }
            
            String sql3 = "UPDATE subject SET lecturer_id=? WHERE subject_id=?";
            pst = conn.prepareStatement(sql3);
            pst.setInt(1, extracted_lec_id);
            pst.setInt(2, Integer.parseInt(search_subject_id.getText()));
            
            }
            catch(NumberFormatException | SQLException e)
            {
                JOptionPane.showMessageDialog(null, e.getMessage());
                err = 1;
            }
            if (err == 0)
            {
                pst.executeUpdate();
                show_subject();
            }
            }
            catch(SQLException e)
            {
                JOptionPane.showMessageDialog(email, e.getMessage());
            }
        }
    }//GEN-LAST:event_update_subject_btnActionPerformed

    private void delete_subject_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delete_subject_btnActionPerformed
        try {
            String sql = "DELETE FROM subject where subject_id=?";
            pst = conn.prepareStatement(sql);
            pst.setInt(1, Integer.parseInt(search_subject_id.getText()));
            pst.execute();
            show_subject();
        } catch (SQLException ex) {
            Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_delete_subject_btnActionPerformed

    private void subject_feeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subject_feeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_subject_feeActionPerformed

    private void subject_duration_fieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subject_duration_fieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_subject_duration_fieldActionPerformed

    private void subject_fee_fieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subject_fee_fieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_subject_fee_fieldActionPerformed

    private void subject_credit_fieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subject_credit_fieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_subject_credit_fieldActionPerformed

    private void duration_listActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_duration_listActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_duration_listActionPerformed

    private void add_instructor_show_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_add_instructor_show_btnActionPerformed
        if (add_instructor_show_btn.isSelected())
        {
            instructor_select.removeAll();
            instructor_select.repaint();
            instructor_select.revalidate();
            instructor_select.add(add_instructor_panel);
            instructor_select.repaint();
            instructor_select.revalidate();
        }
        else
        {
            instructor_select.removeAll();
            instructor_select.repaint();
            instructor_select.revalidate();
            instructor_select.add(instructor_init_panel);
            instructor_select.repaint();
            instructor_select.revalidate();            
        }
    }//GEN-LAST:event_add_instructor_show_btnActionPerformed

    private void add_instructor_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_add_instructor_btnActionPerformed
        //extracting subject_id and staff_id from comboBox selections
        String sub_name = (String)subject_list1.getSelectedItem();
        String ins_fullname = (String)instructor_list1.getSelectedItem();
        String ins_fname = ins_fullname.substring(0, ins_fullname.indexOf(" "));
        int sub_id = 0;
        int ins_id = 0;
        String sql1 = "SELECT * FROM subject WHERE sname=?";
        String sql2 = "SELECT * FROM staff WHERE staff_fname=?";
        try
        {
        pst = conn.prepareStatement(sql1);
        pst.setString(1, sub_name);
        rs = pst.executeQuery();
        while(rs.next())
        {
            sub_id = rs.getInt("subject_id");
        }
        }
        catch(SQLException e)
        {
            JOptionPane.showMessageDialog(null, "Error in subject ID");
        }
        try
        {
            pst = conn.prepareStatement(sql2);
            pst.setString(1, ins_fname);
            rs = pst.executeQuery();
            while(rs.next())
            {
                ins_id = rs.getInt("staff_id");
            }
        }
        catch(SQLException e)
        {
            JOptionPane.showMessageDialog(null, "Error in staff ID");
        }
        
        //inserting extracted IDs into subject_instructor table
        String sql3 = "INSERT INTO subject_instructor(subject_id,instructor_id) VALUES(?,?)";
        try
        {
        pst = conn.prepareStatement(sql3);
        pst.setInt(1, sub_id);
        pst.setInt(2, ins_id);
        pst.execute();
        JOptionPane.showMessageDialog(null, "Instructor added to the Subject!");
        }
        catch(SQLException e)
        {
            JOptionPane.showMessageDialog(null, "The instructor is already added to the subject!");
        }
    }//GEN-LAST:event_add_instructor_btnActionPerformed

    private void subject_feeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_subject_feeMouseClicked
        subject_fee.setText("");
    }//GEN-LAST:event_subject_feeMouseClicked

    private void lab_btnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lab_btnMouseClicked
        bottom_panel.removeAll();
        bottom_panel.repaint();
        bottom_panel.revalidate();
        bottom_panel.add(lab);
        bottom_panel.repaint();
        bottom_panel.revalidate();
        ImageIcon hover = new ImageIcon(getClass().getResource("/buttons/Test Tube_50px_pressed.png"));
        lab_btn.setIcon(hover);
    }//GEN-LAST:event_lab_btnMouseClicked

    private void assign_lab_show_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_assign_lab_show_btnActionPerformed
        lab_main_panel.removeAll();
        lab_main_panel.repaint();
        lab_main_panel.revalidate();
        lab_main_panel.add(lab_assign_panel);
        lab_main_panel.repaint();
        lab_main_panel.revalidate();
    }//GEN-LAST:event_assign_lab_show_btnActionPerformed

    private void lab_buildingMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lab_buildingMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_lab_buildingMouseClicked

    private void lab_floorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lab_floorMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_lab_floorMouseClicked

    private void add_lab_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_add_lab_btnActionPerformed
        
        if (!"".equals(lab_id_enter.getText()) && !"".equals(lab_floor.getText()) && !"".equals(lab_building.getText()))
        {
            Lab input = new Lab();
            input.setLab_id(lab_id_enter.getText());
            input.setLab_build(lab_building.getText());
            input.setLab_floor(lab_floor.getText());
            
            try
            {
            String sql = "INSERT INTO lab(lab_id,lab_building,lab_floor) VALUES(?,?,?)";
            pst = conn.prepareStatement(sql);
            pst.setString(1, input.getLab_id());
            pst.setString(2, input.getLab_build());
            pst.setString(3, input.getLab_floor());
            pst.execute();
            JOptionPane.showMessageDialog(null, "Lab Registered in the System!");
            show_lab();
            updateComboBox_lab();
            updateComboBox_lab1();
            }
            catch(SQLException e)
            {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
            
        }
        else
        {
            JOptionPane.showMessageDialog(null, "Please fill the blank fields and try again!");
        }
    }//GEN-LAST:event_add_lab_btnActionPerformed

    private void lab_cancel_btnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lab_cancel_btnMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_lab_cancel_btnMouseClicked

    private void lab_cancel_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lab_cancel_btnActionPerformed
        lab_id_enter.setText("");
        lab_floor.setText("");
        lab_building.setText("");
    }//GEN-LAST:event_lab_cancel_btnActionPerformed

    private void lab_id_enterMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lab_id_enterMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_lab_id_enterMouseClicked

    private void lab_id_enterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lab_id_enterActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_lab_id_enterActionPerformed

    private void assign_session_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_assign_session_btnActionPerformed
        int confirm = JOptionPane.showConfirmDialog(null, "Do you wish to assign lab?","Assign Lab Record",JOptionPane.YES_NO_OPTION);
        
        if (confirm == 0)
        {
            try
            {
                String sql = "INSERT INTO lab_assign(lab_id, subject_id, time, day_of_week) VALUES(?,?,?,?)";
                pst = conn.prepareStatement(sql);
                pst.setString(1, (String)lab_id_list.getSelectedItem());
                pst.setString(4, (String)lab_date_list.getSelectedItem());
                LocalTime time;
                time = lab_time.getTime();
                pst.setTime(3, java.sql.Time.valueOf(time));
                String sub_name;
                sub_name = (String)lab_subject_list.getSelectedItem();
                String sql1 = "SELECT * FROM subject WHERE sname=?";
                pst1 = conn.prepareStatement(sql1);
                pst1.setString(1, sub_name);
                rs = pst1.executeQuery();
                if (rs.next())
                {
                    pst.setInt(2, rs.getInt("subject_id"));
                    pst.execute(); 
                } 
            }
            catch(SQLException e)
            {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        }
    }//GEN-LAST:event_assign_session_btnActionPerformed

    private void cancel_session_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancel_session_btnActionPerformed
        lab_main_panel.removeAll();
        lab_main_panel.repaint();
        lab_main_panel.revalidate();
        lab_main_panel.add(lab_init_panel);
        lab_main_panel.repaint();
        lab_main_panel.revalidate();
    }//GEN-LAST:event_cancel_session_btnActionPerformed

    private void get_schedule_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_get_schedule_btnActionPerformed
        show_lab_schedule((String)select_date.getSelectedItem());
        schedule_day.setText((String)select_date.getSelectedItem());
    }//GEN-LAST:event_get_schedule_btnActionPerformed

    private void delete_session_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delete_session_btnActionPerformed
        int confirm = JOptionPane.showConfirmDialog(null, "Do you wish to update lab details?","Update Lab Record",JOptionPane.YES_NO_OPTION);
        
        if (confirm == 0)
        {
            try
            {
                String sql = "DELETE FROM lab_assign WHERE lab_id=? AND subject_id=? AND time=? AND day_of_week=?";
                pst = conn.prepareStatement(sql);
                pst.setInt(1, Integer.parseInt((String)lab_id_list.getSelectedItem()));
                pst.setString(4, (String)lab_date_list.getSelectedItem());
                LocalTime time;
                time = lab_time.getTime();
                pst.setTime(3, java.sql.Time.valueOf(time));
                String sub_name;
                sub_name = (String)lab_subject_list.getSelectedItem();
                String sql1 = "SELECT * FROM subject WHERE sname=?";
                pst1 = conn.prepareStatement(sql1);
                pst1.setString(1, sub_name);
                rs = pst1.executeQuery();
                if (rs.next())
                {
                    pst.setInt(2, rs.getInt("subject_id"));
                    pst.execute(); 
                    JOptionPane.showMessageDialog(null, "Lab Session Deleted!");
                } 
                else
                {
                    JOptionPane.showMessageDialog(null, "Lab Session Deletion Failed! Check the entered details!");
                }
            }
            catch(SQLException | NullPointerException e)
            {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        }
    }//GEN-LAST:event_delete_session_btnActionPerformed

    private void delete_lab_panel_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delete_lab_panel_btnActionPerformed
        lab_delete_main.removeAll();
        lab_delete_main.repaint();
        lab_delete_main.revalidate();
        lab_delete_main.add(lab_delete_result);
        lab_delete_main.repaint();
        lab_delete_main.revalidate();
    }//GEN-LAST:event_delete_lab_panel_btnActionPerformed

    private void delete_lab_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delete_lab_btnActionPerformed
        try
        {
            String sql = "DELETE FROM lab WHERE lab_id=?";
            pst = conn.prepareStatement(sql);
            pst.setInt(1, Integer.parseInt((String)delete_lab_list.getSelectedItem()));
            pst.execute();
            JOptionPane.showMessageDialog(null, "Lab Removed!");
            show_lab();
            updateComboBox_lab1();
        }
        catch(SQLException | NumberFormatException e)
        {
            JOptionPane.showMessageDialog(null, "Cannot Remove Lab! There are sessions assigned to the Lab. Remove them and try again.");
        }
    }//GEN-LAST:event_delete_lab_btnActionPerformed

    private void delete_lab_cancel_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delete_lab_cancel_btnActionPerformed
        lab_delete_main.removeAll();
        lab_delete_main.repaint();
        lab_delete_main.revalidate();
        lab_delete_main.add(lab_delete_init);
        lab_delete_main.repaint();
        lab_delete_main.revalidate();
    }//GEN-LAST:event_delete_lab_cancel_btnActionPerformed

    private void get_today_schedule_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_get_today_schedule_btnActionPerformed
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        String day_name = null;
        switch(dayOfWeek)
        {
            case 1 : day_name = "Sunday";
                     break;
            case 2 : day_name = "Monday";
                     break;
            case 3 : day_name = "Tuesday";
                     break;
            case 4 : day_name = "Wednesday";
                     break;
            case 5 : day_name = "Thursday";
                     break;
            case 6 : day_name = "Friday";
                     break;
            case 7 : day_name = "Saturday";
                     break;
            default : break;
        }
        show_lab_schedule(day_name);
        schedule_day.setText("Today : "+day_name);
    }//GEN-LAST:event_get_today_schedule_btnActionPerformed

    private void mark_btnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mark_btnMouseClicked
        bottom_panel.removeAll();
        bottom_panel.repaint();
        bottom_panel.revalidate();
        bottom_panel.add(mark);
        bottom_panel.repaint();
        bottom_panel.revalidate();
        ImageIcon hover = new ImageIcon(getClass().getResource("/buttons/Scorecard_50px_pressed.png"));
        mark_btn.setIcon(hover);
    }//GEN-LAST:event_mark_btnMouseClicked

    private void admin_login_btnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_admin_login_btnMouseClicked
        login_details_init.removeAll();
        login_details_init.repaint();
        login_details_init.revalidate();
        login_details_init.add(admin_login);
        login_details_init.repaint();
        login_details_init.revalidate();
    }//GEN-LAST:event_admin_login_btnMouseClicked

    private void admin_id_fieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_admin_id_fieldMouseClicked
        admin_id_field.setText("");
    }//GEN-LAST:event_admin_id_fieldMouseClicked

    private void admin_pass_fieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_admin_pass_fieldMouseClicked
        admin_pass_field.setText("");
    }//GEN-LAST:event_admin_pass_fieldMouseClicked

    private void login_btnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_login_btnMouseClicked
        login_btn.setBackground(Color.DARK_GRAY);
        try
        {
            String sql = "SELECT * FROM admin WHERE admin_id=?";
            pst = conn.prepareStatement(sql);
            pst.setString(1, admin_id_field.getText());
            rs = pst.executeQuery();
            String given_pass = new String(this.admin_pass_field.getPassword());
            if(rs.next())
            {
                System.out.println(given_pass);
                System.out.println(rs.getString("admin_pass"));
                if (given_pass.equals(rs.getString("admin_pass")))
                {
                    JOptionPane.showMessageDialog(null, "Login Successful.");
                    main_back_pane.removeAll();
                    main_back_pane.repaint();
                    main_back_pane.revalidate();
                    main_back_pane.add(admin_pane);
                    main_back_pane.repaint();
                    main_back_pane.revalidate();
                    currentUser.setText(admin_id_field.getText());
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "Error! Username or Password Mismatch!");
                }
            }
            
        }
        catch(HeadlessException | SQLException e)
        {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }//GEN-LAST:event_login_btnMouseClicked

    private void stu_id_fieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_stu_id_fieldMouseClicked
        stu_id_field.setText("");
    }//GEN-LAST:event_stu_id_fieldMouseClicked

    private void stu_login_btnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_stu_login_btnMouseClicked
        stu_login_btn.setBackground(Color.DARK_GRAY);
        try
        {
            String sql = "SELECT * FROM student WHERE student_id=?";
            pst = conn.prepareStatement(sql);
            String stu_user = stu_id_field.getText();
            pst.setString(1, stu_user);
            rs = pst.executeQuery();
            if (rs.next())
            {
                String getuser = rs.getString("student_id");
                if (getuser.equals(stu_user))
                {
                    JOptionPane.showMessageDialog(null, "Student User Found. Logging In...");
                    
                    main_back_pane.removeAll();
                    main_back_pane.repaint();
                    main_back_pane.revalidate();
                    main_back_pane.add(student_pane);
                    main_back_pane.repaint();
                    main_back_pane.revalidate();
                    initiate_stu_dash();
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "Login Failed! Please check you Student ID.");
                }
            }
            else
            {
                JOptionPane.showMessageDialog(null, "Login Failed! Please check you Student ID.");
            }
            
        }
        catch(SQLException e)
        {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }//GEN-LAST:event_stu_login_btnMouseClicked

    private void login_cancel_btnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_login_cancel_btnMouseClicked
        login_cancel_btn.setBackground(Color.DARK_GRAY);
        login_details_init.removeAll();
        login_details_init.repaint();
        login_details_init.revalidate();
        login_details_init.add(login_details_select);
        login_details_init.repaint();
        login_details_init.revalidate();
    }//GEN-LAST:event_login_cancel_btnMouseClicked

    private void student_login_btnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_student_login_btnMouseClicked
        
        login_details_init.removeAll();
        login_details_init.repaint();
        login_details_init.revalidate();
        login_details_init.add(student_login);
        login_details_init.repaint();
        login_details_init.revalidate();
    }//GEN-LAST:event_student_login_btnMouseClicked

    private void stu_login_cancel_btnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_stu_login_cancel_btnMouseClicked
        stu_login_cancel_btn.setBackground(Color.DARK_GRAY);
        login_details_init.removeAll();
        login_details_init.repaint();
        login_details_init.revalidate();
        login_details_init.add(login_details_select);
        login_details_init.repaint();
        login_details_init.revalidate();
    }//GEN-LAST:event_stu_login_cancel_btnMouseClicked
    //selecting subjects by a student for a semester
    private void register_student_subjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_register_student_subjectActionPerformed
        int main_sub1 = 0;
        int main_sub2 = 0;
        int main_sub3 = 0;
        int main_sub4 = 0;
        int opt_sub1 = 0;
        int opt_sub2 = 0;
        int opt_sub3 = 0;
        int opt_sub4 = 0;
        try
        {          
            String sql = "SELECT subject_id FROM subject WHERE sname=?";
            String sql1 = "INSERT INTO student_mark(student_id,subject_id,semester,year,sub_type) VALUES(?,?,?,?,?)";
            pst = conn.prepareStatement(sql);
            pst.setString(1, (String)sub1_list.getSelectedItem());
            rs = pst.executeQuery();
            if (rs.next())
            {
                main_sub1 = rs.getInt("subject_id");
                pst1 = conn.prepareStatement(sql1);
                pst1.setInt(1, Integer.parseInt(stu_nic_field.getText()));
                pst1.setInt(2, main_sub1);
                pst1.setString(5, "Main");
                if (feb_sem_btn.isSelected())
                {
                    pst1.setString(3, "February");
                }
                if (jul_sem_btn.isSelected())
                {
                    pst1.setString(3, "July");
                }
                int year = Calendar.getInstance().get(Calendar.YEAR);
                pst1.setInt(4, year);
                pst1.execute();               
            }

            
            pst = conn.prepareStatement(sql);
            pst.setString(1, (String)sub2_list.getSelectedItem());
            rs = pst.executeQuery();
            if (rs.next())
            {
                main_sub2 = rs.getInt("subject_id");
                pst1 = conn.prepareStatement(sql1);
                pst1.setInt(1, Integer.parseInt(stu_nic_field.getText()));
                pst1.setInt(2, main_sub2);
                pst1.setString(5, "Main");
                if (feb_sem_btn.isSelected())
                {
                    pst1.setString(3, "February");
                }
                if (jul_sem_btn.isSelected())
                {
                    pst1.setString(3, "July");
                }
                int year = Calendar.getInstance().get(Calendar.YEAR);
                pst1.setInt(4, year);
                pst1.execute();
            }

            pst = conn.prepareStatement(sql);
            pst.setString(1, (String)sub3_list.getSelectedItem());
            rs = pst.executeQuery();
            if (rs.next())
            {
                main_sub3 = rs.getInt("subject_id");
                pst1 = conn.prepareStatement(sql1);
                pst1.setInt(1, Integer.parseInt(stu_nic_field.getText()));
                pst1.setInt(2, main_sub3);
                pst1.setString(5, "Main");
                if (feb_sem_btn.isSelected())
                {
                    pst1.setString(3, "February");
                }
                if (jul_sem_btn.isSelected())
                {
                    pst1.setString(3, "July");
                }
                int year = Calendar.getInstance().get(Calendar.YEAR);
                pst1.setInt(4, year);
                pst1.execute();
            }

            
            pst = conn.prepareStatement(sql);
            pst.setString(1, (String)sub4_list.getSelectedItem());
            rs = pst.executeQuery();
            if (rs.next())
            {
                main_sub4 = rs.getInt("subject_id");
                pst1 = conn.prepareStatement(sql1);
                pst1.setInt(1, Integer.parseInt(stu_nic_field.getText()));
                pst1.setInt(2, main_sub4);
                pst1.setString(5, "Main");
                if (feb_sem_btn.isSelected())
                {
                    pst1.setString(3, "February");
                }
                if (jul_sem_btn.isSelected())
                {
                    pst1.setString(3, "July");
                }
                int year = Calendar.getInstance().get(Calendar.YEAR);
                pst1.setInt(4, year);
                pst1.execute();
            }

            
            if (!"None".equals((String)opt_sub1_list.getSelectedItem()))
            {
                pst = conn.prepareStatement(sql);
                pst.setString(1, (String)opt_sub1_list.getSelectedItem());
                rs = pst.executeQuery();
                if (rs.next())
                {
                    opt_sub1 = rs.getInt("subject_id");
                    pst1 = conn.prepareStatement(sql1);
                    pst1.setInt(1, Integer.parseInt(stu_nic_field.getText()));
                    pst1.setInt(2, opt_sub1);
                    pst1.setString(5, "Optional");
                    if (feb_sem_btn.isSelected())
                    {
                        pst1.setString(3, "February");
                    }
                    if (jul_sem_btn.isSelected())
                    {
                        pst1.setString(3, "July");
                    }
                    int year = Calendar.getInstance().get(Calendar.YEAR);
                    pst1.setInt(4, year);
                    pst1.execute();
                }
            }
            
            if (!"None".equals((String)opt_sub2_list.getSelectedItem()))
            {
                pst = conn.prepareStatement(sql);
                pst.setString(1, (String)opt_sub2_list.getSelectedItem());
                rs = pst.executeQuery();
                if (rs.next())
                {
                    opt_sub2 = rs.getInt("subject_id");
                    pst1 = conn.prepareStatement(sql1);
                    pst1.setInt(1, Integer.parseInt(stu_nic_field.getText()));
                    pst1.setInt(2, opt_sub2);
                    pst1.setString(5, "Optional");
                    if (feb_sem_btn.isSelected())
                    {
                        pst1.setString(3, "February");
                    }
                    if (jul_sem_btn.isSelected())
                    {
                        pst1.setString(3, "July");
                    }
                    int year = Calendar.getInstance().get(Calendar.YEAR);
                    pst1.setInt(4, year);
                    pst1.execute();
                }
            }
            
            if (!"None".equals((String)opt_sub3_list.getSelectedItem()))
            {
                pst = conn.prepareStatement(sql);
                pst.setString(1, (String)opt_sub3_list.getSelectedItem());
                rs = pst.executeQuery();
                if (rs.next())
                {
                    opt_sub3 = rs.getInt("subject_id");
                    pst1 = conn.prepareStatement(sql1);
                    pst1.setInt(1, Integer.parseInt(stu_nic_field.getText()));
                    pst1.setInt(2, opt_sub3);
                    pst1.setString(5, "Optional");
                    if (feb_sem_btn.isSelected())
                    {
                        pst1.setString(3, "February");
                    }
                    if (jul_sem_btn.isSelected())
                    {
                        pst1.setString(3, "July");
                    }
                    int year = Calendar.getInstance().get(Calendar.YEAR);
                    pst1.setInt(4, year);
                    pst1.execute();
                }
            }
            
            if ("None".equals((String)opt_sub4_list.getSelectedItem())==false)
            {
                pst = conn.prepareStatement(sql);
                pst.setString(1, (String)opt_sub4_list.getSelectedItem());
                rs = pst.executeQuery();
                if (rs.next())
                {
                    opt_sub4 = rs.getInt("subject_id");
                    pst1 = conn.prepareStatement(sql1);
                    pst1.setInt(1, Integer.parseInt(stu_nic_field.getText()));
                    pst1.setInt(2, opt_sub4);
                    pst1.setString(5, "Optional");
                    if (feb_sem_btn.isSelected())
                    {
                        pst1.setString(3, "February");
                    }
                    if (jul_sem_btn.isSelected())
                    {
                        pst1.setString(3, "July");
                    }
                    int year = Calendar.getInstance().get(Calendar.YEAR);
                    pst1.setInt(4, year);
                    pst1.execute();
                    
                }           
            }
            
            double total_fee = 0;
            String sql4 = "SELECT * FROM student_mark WHERE student_id=?";
            String sql5 = "SELECT * FROM subject WHERE subject_id=?";
            pst = conn.prepareStatement(sql4);
            pst.setString(1, stu_nic_field.getText());
            rs = pst.executeQuery();
            
            while (rs.next())
            {
                pst1 = conn.prepareStatement(sql5);
                String sub_id = rs.getString("subject_id");
                pst1.setString(1, sub_id);
                rs1 = pst1.executeQuery();
                if (rs1.next())
                {
                    total_fee += rs1.getDouble("sfee");
                }
            }
            
            String sql6 = "UPDATE student SET fee_payable = fee_payable + ? WHERE student_id=?";
            pst = conn.prepareStatement(sql6);
            pst.setDouble(1, total_fee);
            pst.setString(2, stu_nic_field.getText());
            pst.executeUpdate();
            show_mark_table();
        }
        catch(SQLException e)
        {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }//GEN-LAST:event_register_student_subjectActionPerformed

    private void feb_sem_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_feb_sem_btnActionPerformed
        sub_select_pane.removeAll();
        sub_select_pane.repaint();
        sub_select_pane.revalidate();
        sub_select_pane.add(sub_select_res_pane);
        sub_select_pane.repaint();
        sub_select_pane.revalidate();
        if (jul_sem_btn.isSelected())
        {
            jul_sem_btn.doClick();
        }
    }//GEN-LAST:event_feb_sem_btnActionPerformed

    private void jul_sem_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jul_sem_btnActionPerformed
        sub_select_pane.removeAll();
        sub_select_pane.repaint();
        sub_select_pane.revalidate();
        sub_select_pane.add(sub_select_res_pane);
        sub_select_pane.repaint();
        sub_select_pane.revalidate();
        
        if (feb_sem_btn.isSelected())
        {
            feb_sem_btn.doClick();
        }
    }//GEN-LAST:event_jul_sem_btnActionPerformed

    private void change_student_subjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_change_student_subjectActionPerformed
        int confirm = JOptionPane.showConfirmDialog(null, "Do you wish to change details?","Confirm Update Record",JOptionPane.YES_NO_OPTION);
        if (confirm == 0)
        {
            try
            {
            String sql = "DELETE from student_mark WHERE student_id=? AND year=?";
            pst = conn.prepareStatement(sql);
            pst.setInt(1, Integer.parseInt(stu_nic_field.getText()));
            int year = Calendar.getInstance().get(Calendar.YEAR);
            pst.setInt(2, year);
            pst.execute();
            register_student_subject.doClick();
            JOptionPane.showMessageDialog(null, "Changes have been made");
            show_mark_table();
            }
            catch(NumberFormatException | SQLException e)
            {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        }
        
    }//GEN-LAST:event_change_student_subjectActionPerformed

    private void get_mark_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_get_mark_btnActionPerformed
        show_mark_table2();
    }//GEN-LAST:event_get_mark_btnActionPerformed

    private void cancel_mark_btnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cancel_mark_btnMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_cancel_mark_btnMouseClicked

    private void cancel_mark_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancel_mark_btnActionPerformed
        DefaultTableModel model = (DefaultTableModel)marks_jTable.getModel();
        model.setRowCount(0);
        mark_stu_id.setText("");
        
    }//GEN-LAST:event_cancel_mark_btnActionPerformed

    private void mark_stu_idMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mark_stu_idMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_mark_stu_idMouseClicked

    private void mark_stu_idActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mark_stu_idActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mark_stu_idActionPerformed
    //adding a new task with a mark for a particular student which contributes to their total marks
    private void add_task_mark_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_add_task_mark_btnActionPerformed
        int confirm = JOptionPane.showConfirmDialog(null, "Do you wish to add this mark?","Confirm Update Record",JOptionPane.YES_NO_OPTION);
        if (confirm == 0)
        {
            int total_mark;
            int award_mark;
            int err = 0;
            try
            {
               total_mark = Integer.parseInt(total_mark_field.getText());
               award_mark = Integer.parseInt(awarded_mark_field.getText());
               if (total_mark > 100 || total_mark < award_mark)
               {
                   err = 1;
                   JOptionPane.showMessageDialog(null, "Invalid marks input");
               }
            }
            catch (HeadlessException | NumberFormatException e)
            {
                JOptionPane.showMessageDialog(null, "Enter Numeric Value for Marks");
                err = 1;
            }
            if (err == 0)
            {
                Task input = new Task();
                input.setStudent_id(Integer.parseInt(mark_stu_id2.getText()));
                String subject_name = (String)task_subject_list.getSelectedItem();
                String[] subject_array = subject_name.split(" ");
                input.setSubject_id(Integer.parseInt(subject_array[0]));
                input.setTask_id(task_id_field.getText());
                input.setTask_type((String)task_type_list.getSelectedItem());
                input.setTotal_mark(Integer.parseInt(total_mark_field.getText()));
                input.setAwarded_mark(Integer.parseInt(awarded_mark_field.getText()));
                try
                {
                    String sql = "INSERT INTO subject_task_marks(subject_id, student_id, task_id, task_type, total_mark, mark_awarded) VALUES (?,?,?,?,?,?)";
                    pst = conn.prepareStatement(sql);
                    pst.setInt(1, input.getSubject_id());
                    pst.setInt(2, input.getStudent_id());
                    pst.setString(3, input.getTask_id());
                    pst.setString(4, input.getTask_type());
                    pst.setInt(5, input.getTotal_mark());
                    pst.setInt(6, input.getAwarded_mark());
                    pst.execute();
                    show_task_mark_table_subject();
                }
                catch(SQLException e)
                {
                    JOptionPane.showMessageDialog(null, "Error! Check your input.");
                } 
                //adding marks to the subject_mark table as a percentage of all the task marks
                int sum_total_mark = 0;
                int sum_award_mark = 0;
                double percentage_mark = 0;
                try
                {
                    String sql1 = "SELECT * FROM subject_task_marks WHERE student_id=? AND subject_id=?";
                    pst = conn.prepareStatement(sql1);
                    pst.setInt(1, Integer.parseInt(mark_stu_id2.getText()));
                    String subject_name1 = (String)task_subject_list.getSelectedItem();
                    String[] subject_array1 = subject_name1.split(" ");
                    pst.setInt(2, Integer.parseInt(subject_array1[0]));
                    rs = pst.executeQuery();
                    while(rs.next())
                    {
                        sum_total_mark += rs.getInt("total_mark");
                        sum_award_mark += rs.getInt("mark_awarded");

                    }
                    System.out.println(sum_total_mark);
                    System.out.println(sum_award_mark);
                    
                    percentage_mark = ((double)sum_award_mark/sum_total_mark)*100;
                    System.out.println(percentage_mark);
                    String sql2 = "UPDATE student_mark SET mark=? WHERE student_id=? AND subject_id=?";
                    pst = conn.prepareStatement(sql2);
                    pst.setDouble(1, percentage_mark);
                    pst.setInt(2, Integer.parseInt(mark_stu_id2.getText()));
                    pst.setInt(3, Integer.parseInt(subject_array1[0]));
                    pst.execute();
                    JOptionPane.showMessageDialog(null, "Marks have been added to student!");
                    mark_stu_id.setText(mark_stu_id2.getText());
                    show_mark_table2();
                    
                    double stu_GPA = calcGPA(mark_stu_id2.getText());
                    String sql3 = "UPDATE student SET gpa=? WHERE student_id=?";
                    pst = conn.prepareStatement(sql3);
                    pst.setDouble(1, stu_GPA);
                    pst.setString(2, mark_stu_id2.getText());
                    pst.executeUpdate();
                }
                catch(NumberFormatException | SQLException e)
                {
                    System.out.println(e.getMessage());
                }
            }
        }
    }//GEN-LAST:event_add_task_mark_btnActionPerformed

    private void student_mark_optionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_student_mark_optionActionPerformed
        int err = 0;
        try
        {
            Integer.parseInt(mark_stu_id2.getText());
        }
        catch (NumberFormatException e)
        {
            JOptionPane.showMessageDialog(null, "Please enter a numeric value");
            err = 1;
        }
        if (err == 0)
        {
            stu_sub_mark_panel.removeAll();
            stu_sub_mark_panel.repaint();
            stu_sub_mark_panel.revalidate();
            stu_sub_mark_panel.add(stu_sub_mark_select);
            stu_sub_mark_panel.repaint();
            stu_sub_mark_panel.revalidate();
            updateComboBox_task_subject_list();
            task_mark_panel.removeAll();
            task_mark_panel.repaint();
            task_mark_panel.revalidate();
            task_mark_panel.add(stu_task_mark);
            task_mark_panel.repaint();
            task_mark_panel.revalidate();
        }
    }//GEN-LAST:event_student_mark_optionActionPerformed

    private void add_task_mark_btn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_add_task_mark_btn1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_add_task_mark_btn1ActionPerformed

    private void show_mark_pane_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_show_mark_pane_btnActionPerformed
        mark_options_pane.removeAll();
        mark_options_pane.repaint();
        mark_options_pane.revalidate();
        mark_options_pane.add(mark_options_select);
        mark_options_pane.repaint();
        mark_options_pane.revalidate();
    }//GEN-LAST:event_show_mark_pane_btnActionPerformed

    private void task_subject_listActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_task_subject_listActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_task_subject_listActionPerformed

    private void show_mark_table_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_show_mark_table_btnActionPerformed
        show_task_mark_table_subject();
    }//GEN-LAST:event_show_mark_table_btnActionPerformed

    private void mark_stu_id2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mark_stu_id2MouseClicked
        mark_stu_id2.setText("");
    }//GEN-LAST:event_mark_stu_id2MouseClicked

    private void task_id_fieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_task_id_fieldMouseClicked
        task_id_field.setText("");
    }//GEN-LAST:event_task_id_fieldMouseClicked

    private void show_all_task_marksActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_show_all_task_marksActionPerformed
        show_task_mark_table_subject2();
    }//GEN-LAST:event_show_all_task_marksActionPerformed
    //delete an added task for a student
    private void delete_task_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delete_task_btnActionPerformed
        int confirm = JOptionPane.showConfirmDialog(null, "Do you wish to delete this mark?","Confirm Delete Mark",JOptionPane.YES_NO_OPTION);
        if (confirm == 0)
        {
            int err = 0;
            try
            {
                String sql = "DELETE FROM subject_task_marks WHERE subject_id=? AND student_id=? AND task_id=?";
                pst = conn.prepareStatement(sql);
                String subject_name = (String)task_subject_list.getSelectedItem();
                String[] subject_array = subject_name.split(" ");
                pst.setInt(1, Integer.parseInt(subject_array[0]));
                pst.setInt(2, Integer.parseInt(mark_stu_id2.getText()));
                pst.setString(3, delete_task_field.getText());
                if (err == 0)
                {
                    pst.execute();
                    JOptionPane.showMessageDialog(null, "Task Deleted");
                    //adding marks to the subject_mark table as a percentage of all the task marks
                    int sum_total_mark = 0;
                    int sum_award_mark = 0;
                    double percentage_mark = 0;

                    String sql1 = "SELECT * FROM subject_task_marks WHERE student_id=? AND subject_id=?";
                    pst = conn.prepareStatement(sql1);
                    pst.setInt(1, Integer.parseInt(mark_stu_id2.getText()));
                    String subject_name1 = (String)task_subject_list.getSelectedItem();
                    String[] subject_array1 = subject_name1.split(" ");
                    pst.setInt(2, Integer.parseInt(subject_array1[0]));
                    rs = pst.executeQuery();
                    while(rs.next())
                    {
                        sum_total_mark += rs.getInt("total_mark");
                        sum_award_mark += rs.getInt("mark_awarded");

                    }
                    System.out.println(sum_total_mark);
                    System.out.println(sum_award_mark);

                    percentage_mark = ((double)sum_award_mark/sum_total_mark)*100;
                    System.out.println(percentage_mark);
                    String sql2 = "UPDATE student_mark SET mark=? WHERE student_id=? AND subject_id=?";
                    pst = conn.prepareStatement(sql2);
                    pst.setDouble(1, percentage_mark);
                    pst.setInt(2, Integer.parseInt(mark_stu_id2.getText()));
                    pst.setInt(3, Integer.parseInt(subject_array1[0]));
                    pst.execute();
                    mark_stu_id.setText(mark_stu_id2.getText());
                    show_mark_table2();
                    show_task_mark_table_subject();
                }
            }
            catch(NumberFormatException | SQLException e)
            {
                JOptionPane.showMessageDialog(null, e.getMessage());
                err = 1;
            }
        }
    }//GEN-LAST:event_delete_task_btnActionPerformed

    private void show_delete_task_paneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_show_delete_task_paneActionPerformed
        delete_task_panel.removeAll();
        delete_task_panel.repaint();
        delete_task_panel.revalidate();
        delete_task_panel.add(delete_task_show);
        delete_task_panel.repaint();
        delete_task_panel.revalidate();
    }//GEN-LAST:event_show_delete_task_paneActionPerformed
    //checking the amount of credits for the subjects selected by the student to see if its sufficient for the course
    private void chk_credits_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chk_credits_btnActionPerformed
        try
        {
            int total_credits = 0;
            String course_name = stu_course_field.getText();
            String sql3 = "SELECT * FROM course WHERE course_id=?";
            pst = conn.prepareStatement(sql3);
            pst.setString(1, course_name);
            rs = pst.executeQuery();
            if (rs.next())
            {
                credits_per_year_field.setText(Integer.toString(rs.getInt("credits_to_complete")));
                total_credits = rs.getInt("credits_to_complete");
            }
            
            double total_fee = 0;
            int selected_credits = 0;
            int remaining_credits = 0;
            
            String sql4 = "SELECT * FROM student_mark WHERE student_id=?";
            String sql5 = "SELECT * FROM subject WHERE subject_id=?";
            pst = conn.prepareStatement(sql4);
            pst.setString(1, stu_nic_field.getText());
            rs = pst.executeQuery();
            
            while (rs.next())
            {
                pst1 = conn.prepareStatement(sql5);
                String sub_id = rs.getString("subject_id");
                pst1.setString(1, sub_id);
                rs1 = pst1.executeQuery();
                if (rs1.next())
                {
                    total_fee += rs1.getDouble("sfee");
                    selected_credits += rs1.getInt("credits_awarded");
                }
            }           
            total_selection_fee.setText(Double.toString(total_fee));
            credit_gained_field.setText(Integer.toString(selected_credits));
            
            if (total_credits > selected_credits)
            {
                credit_needed_field.setText(Integer.toString(total_credits - selected_credits));
            }
            else
            {
                credit_needed_field.setText("0");
            }
            
            String sql6 = "SELECT * FROM student WHERE student_id=?";
            pst = conn.prepareStatement(sql6);
            pst.setString(1, stu_nic_field.getText());
            rs = pst.executeQuery();
            double fee_payable = 0;
            if (rs.next())
            {
                fee_payable = rs.getDouble("fee_payable");
                outstanding_fee.setText(Double.toString(fee_payable));
            }
            double total = fee_payable + total_fee;
            this.fee_payable.setText(Double.toString(total));        
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
    }//GEN-LAST:event_chk_credits_btnActionPerformed

    private void userMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_userMouseClicked
        bottom_panel.removeAll();
        bottom_panel.repaint();
        bottom_panel.revalidate();
        
        bottom_panel.add(logout);
        bottom_panel.repaint();
        bottom_panel.revalidate();
        ImageIcon hover = new ImageIcon(getClass().getResource("/buttons/Male User_50px_pressed.png"));
        user.setIcon(hover);
    }//GEN-LAST:event_userMouseClicked

    private void logout_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logout_btnActionPerformed
        int confirm = JOptionPane.showConfirmDialog(null, "Do you wish to logout?","Confirm LogOut",JOptionPane.YES_NO_OPTION);
        if (confirm == 0)
        {
            main_back_pane.removeAll();
            main_back_pane.repaint();
            main_back_pane.revalidate();
            
            main_back_pane.add(login_pane);
            main_back_pane.repaint();
            main_back_pane.revalidate();
            
            login_details_init.removeAll();
            login_details_init.repaint();
            login_details_init.revalidate();
            
            login_details_init.add(login_details_select);
            login_details_init.repaint();
            login_details_init.revalidate();
            
            
            stu_id_field.setText("");
            admin_id_field.setText("");
            admin_pass_field.setText("");
        }
    }//GEN-LAST:event_logout_btnActionPerformed

    private void new_admin_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_new_admin_btnActionPerformed
        logout_display.removeAll();
        logout_display.repaint();
        logout_display.revalidate();
        
        logout_display.add(new_admin_pane);
        logout_display.repaint();
        logout_display.revalidate();
    }//GEN-LAST:event_new_admin_btnActionPerformed

    private void change_pw_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_change_pw_btnActionPerformed
        logout_display.removeAll();
        logout_display.repaint();
        logout_display.revalidate();
        
        logout_display.add(change_pw_pane);
        logout_display.repaint();
        logout_display.revalidate();
    }//GEN-LAST:event_change_pw_btnActionPerformed

    private void stu_logout_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stu_logout_btnActionPerformed
        int confirm = JOptionPane.showConfirmDialog(null, "Do you wish to logout?","Confirm LogOut",JOptionPane.YES_NO_OPTION);
        if (confirm == 0)
        {
            main_back_pane.removeAll();
            main_back_pane.repaint();
            main_back_pane.revalidate();
            
            main_back_pane.add(login_pane);
            main_back_pane.repaint();
            main_back_pane.revalidate();
            
            login_details_init.removeAll();
            login_details_init.repaint();
            login_details_init.revalidate();
            
            login_details_init.add(login_details_select);
            login_details_init.repaint();
            login_details_init.revalidate();
            
            
            stu_id_field.setText("");
            admin_id_field.setText("");
            admin_pass_field.setText("");
        }
    }//GEN-LAST:event_stu_logout_btnActionPerformed
    //clicking exit button and closing the app and closing DB connections
    private void app_exit_btnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_app_exit_btnMouseClicked
        int err = 0;
        try
        {
            if (conn != null)
            {
                conn.close();
            }
            if (pst != null)
            {
                pst.close();
            }if (pst1 != null)
            {
                pst1.close();
            }if (rs != null)
            {
                rs.close();
            }if (rs1 != null)
            {
                rs1.close();
            }
        }
        catch (SQLException e)
        {
            System.out.println("Error");
            System.out.println(e.getMessage());
            err = 1;
        }
        
        if (err == 0)
        {
            System.exit(err);
        }
    }//GEN-LAST:event_app_exit_btnMouseClicked
//following functions will chnage the color of the main icons on top when mouse is hovered on top
    private void sub_btnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_sub_btnMouseEntered
        ImageIcon hover = new ImageIcon(getClass().getResource("/buttons/Book_50px_hover.png"));
        sub_btn.setIcon(hover);
    }//GEN-LAST:event_sub_btnMouseEntered

    private void sub_btnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_sub_btnMouseExited
        ImageIcon hover = new ImageIcon(getClass().getResource("/buttons/Book_50px.png"));
        sub_btn.setIcon(hover);
    }//GEN-LAST:event_sub_btnMouseExited

    private void cour_btnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cour_btnMouseEntered
        ImageIcon hover = new ImageIcon(getClass().getResource("/buttons/Course_50px_hover.png"));
        cour_btn.setIcon(hover);
    }//GEN-LAST:event_cour_btnMouseEntered

    private void cour_btnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cour_btnMouseExited
        ImageIcon hover = new ImageIcon(getClass().getResource("/buttons/Course_50px.png"));
        cour_btn.setIcon(hover);
    }//GEN-LAST:event_cour_btnMouseExited

    private void staff_btnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_staff_btnMouseEntered
        ImageIcon hover = new ImageIcon(getClass().getResource("/buttons/Lecturer_50px_hover.png"));
        staff_btn.setIcon(hover);
    }//GEN-LAST:event_staff_btnMouseEntered

    private void staff_btnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_staff_btnMouseExited
        ImageIcon hover = new ImageIcon(getClass().getResource("/buttons/Lecturer_50px.png"));
        staff_btn.setIcon(hover);
    }//GEN-LAST:event_staff_btnMouseExited

    private void lab_btnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lab_btnMouseEntered
        ImageIcon hover = new ImageIcon(getClass().getResource("/buttons/Test Tube_50px_hover.png"));
        lab_btn.setIcon(hover);
    }//GEN-LAST:event_lab_btnMouseEntered

    private void lab_btnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lab_btnMouseExited
        ImageIcon hover = new ImageIcon(getClass().getResource("/buttons/Test Tube_50px.png"));
        lab_btn.setIcon(hover);
    }//GEN-LAST:event_lab_btnMouseExited

    private void mark_btnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mark_btnMouseEntered
        ImageIcon hover = new ImageIcon(getClass().getResource("/buttons/Scorecard_50px_hover.png"));
        mark_btn.setIcon(hover);
    }//GEN-LAST:event_mark_btnMouseEntered

    private void mark_btnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mark_btnMouseExited
        ImageIcon hover = new ImageIcon(getClass().getResource("/buttons/Scorecard_50px.png"));
        mark_btn.setIcon(hover);
    }//GEN-LAST:event_mark_btnMouseExited

    private void home_btnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_home_btnMouseEntered
        ImageIcon hover = new ImageIcon(getClass().getResource("/buttons/Home Page_50px_hover.png"));
        home_btn.setIcon(hover);
    }//GEN-LAST:event_home_btnMouseEntered

    private void home_btnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_home_btnMouseExited
        ImageIcon hover = new ImageIcon(getClass().getResource("/buttons/Home Page_50px.png"));
        home_btn.setIcon(hover);
    }//GEN-LAST:event_home_btnMouseExited

    private void userMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_userMouseEntered
        ImageIcon hover = new ImageIcon(getClass().getResource("/buttons/Male User_50px_hover.png"));
        user.setIcon(hover);
    }//GEN-LAST:event_userMouseEntered

    private void userMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_userMouseExited
        ImageIcon hover = new ImageIcon(getClass().getResource("/buttons/Male User_50px.png"));
        user.setIcon(hover);
    }//GEN-LAST:event_userMouseExited

    private void current_pw_fieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_current_pw_fieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_current_pw_fieldActionPerformed

    private void new_pw_fieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_new_pw_fieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_new_pw_fieldActionPerformed

    private void confirm_pw_fieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_confirm_pw_fieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_confirm_pw_fieldActionPerformed

    private void current_pw_fieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_current_pw_fieldMouseClicked
        current_pw_field.setText("");
    }//GEN-LAST:event_current_pw_fieldMouseClicked

    private void new_pw_fieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_new_pw_fieldMouseClicked
        new_pw_field.setText("");
    }//GEN-LAST:event_new_pw_fieldMouseClicked

    private void confirm_pw_fieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_confirm_pw_fieldMouseClicked
        confirm_pw_field.setText("");
    }//GEN-LAST:event_confirm_pw_fieldMouseClicked

    private void cancel_pw_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancel_pw_btnActionPerformed
        logout_display.removeAll();
        logout_display.repaint();
        logout_display.revalidate();
        
        logout_display.add(logout_init_pane);
        logout_display.repaint();
        logout_display.revalidate();
    }//GEN-LAST:event_cancel_pw_btnActionPerformed
    //changing the password of an admin
    private void pw_change_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pw_change_btnActionPerformed
        int err = 0;
        if (current_pw_field.getText().equals(admin_pass_field.getText()))
        {
            if (new_pw_field.getText().equals(confirm_pw_field.getText()))
            {
                try
                {
                String sql = "UPDATE admin SET admin_pass=? WHERE admin_id="+admin_id_field.getText();
                pst = conn.prepareStatement(sql);
                pst.setString(1, new_pw_field.getText());
                pst.executeUpdate();                
                }
                catch(SQLException e)
                {
                    err = 1;
                    System.out.println(e.getMessage());
                }
            }
            else
            {
                JOptionPane.showMessageDialog(null, "Error! New Passwords do not match!");
                err = 1;
            }
        }
        else
        {
            JOptionPane.showMessageDialog(null, "Current Password entered is not valid!");
            err = 1;
        }
        if (err == 0)
        {
            JOptionPane.showMessageDialog(null, "Password Changed Sucessfully!");
        }
    }//GEN-LAST:event_pw_change_btnActionPerformed

    private void current_pw_field1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_current_pw_field1MouseClicked
        current_pw_field1.setText("");
    }//GEN-LAST:event_current_pw_field1MouseClicked

    private void current_pw_field1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_current_pw_field1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_current_pw_field1ActionPerformed

    private void new_admin_fieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_new_admin_fieldMouseClicked
        new_admin_field.setText("");
    }//GEN-LAST:event_new_admin_fieldMouseClicked

    private void new_admin_fieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_new_admin_fieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_new_admin_fieldActionPerformed

    private void new_admin_pw_fieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_new_admin_pw_fieldMouseClicked
        new_admin_pw_field.setText("");
    }//GEN-LAST:event_new_admin_pw_fieldMouseClicked

    private void new_admin_pw_fieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_new_admin_pw_fieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_new_admin_pw_fieldActionPerformed
    //adding a new admin to the system
    private void add_admin_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_add_admin_btnActionPerformed
        int err = 1;
        if (current_pw_field1.getText().equals(admin_pass_field.getText()))
        {
            err = 0;
            String sql = "INSERT INTO admin (admin_id, admin_pass) VALUES (?,?)";
            try
            {
            pst = conn.prepareStatement(sql);
            pst.setString(1, new_admin_field.getText());
            pst.setString(2, new_admin_pw_field.getText());
            pst.execute();
            }
            catch(SQLException e)
            {
                err = 1;
                System.out.println(e.getMessage());
            }
        }
        if (err == 0)
        {
            JOptionPane.showMessageDialog(null, "Admin Added Successfully!");
        }
    }//GEN-LAST:event_add_admin_btnActionPerformed

    private void cancel_admin_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancel_admin_btnActionPerformed
        logout_display.removeAll();
        logout_display.repaint();
        logout_display.revalidate();
        
        logout_display.add(logout_init_pane);
        logout_display.repaint();
        logout_display.revalidate();
    }//GEN-LAST:event_cancel_admin_btnActionPerformed

    private void login_btnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_login_btnMouseEntered
        login_btn.setBackground(Color.WHITE);
    }//GEN-LAST:event_login_btnMouseEntered

    private void login_btnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_login_btnMouseExited
        login_btn.setBackground(Color.LIGHT_GRAY);
    }//GEN-LAST:event_login_btnMouseExited

    private void login_cancel_btnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_login_cancel_btnMouseEntered
        login_cancel_btn.setBackground(Color.WHITE);
    }//GEN-LAST:event_login_cancel_btnMouseEntered

    private void login_cancel_btnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_login_cancel_btnMouseExited
        login_cancel_btn.setBackground(Color.LIGHT_GRAY);
    }//GEN-LAST:event_login_cancel_btnMouseExited

    private void stu_login_btnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_stu_login_btnMouseEntered
        stu_login_btn.setBackground(Color.WHITE);
    }//GEN-LAST:event_stu_login_btnMouseEntered

    private void stu_login_btnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_stu_login_btnMouseExited
        stu_login_btn.setBackground(Color.LIGHT_GRAY);
    }//GEN-LAST:event_stu_login_btnMouseExited

    private void stu_login_cancel_btnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_stu_login_cancel_btnMouseEntered
        stu_login_cancel_btn.setBackground(Color.WHITE);
    }//GEN-LAST:event_stu_login_cancel_btnMouseEntered

    private void stu_login_cancel_btnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_stu_login_cancel_btnMouseExited
        stu_login_cancel_btn.setBackground(Color.LIGHT_GRAY);
    }//GEN-LAST:event_stu_login_cancel_btnMouseExited

    private void admin_login_btnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_admin_login_btnMouseEntered
        admin_login_btn.setBackground(Color.WHITE);
    }//GEN-LAST:event_admin_login_btnMouseEntered

    private void admin_login_btnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_admin_login_btnMouseExited
        admin_login_btn.setBackground(Color.LIGHT_GRAY);
    }//GEN-LAST:event_admin_login_btnMouseExited

    private void student_login_btnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_student_login_btnMouseEntered
        student_login_btn.setBackground(Color.WHITE);
    }//GEN-LAST:event_student_login_btnMouseEntered

    private void student_login_btnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_student_login_btnMouseExited
        student_login_btn.setBackground(Color.LIGHT_GRAY);
    }//GEN-LAST:event_student_login_btnMouseExited

    private void app_exit_btnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_app_exit_btnMouseEntered
        app_exit_btn.setBackground(Color.WHITE);
    }//GEN-LAST:event_app_exit_btnMouseEntered

    private void app_exit_btnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_app_exit_btnMouseExited
        app_exit_btn.setBackground(Color.LIGHT_GRAY);
    }//GEN-LAST:event_app_exit_btnMouseExited

    private void lab_id_listActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lab_id_listActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_lab_id_listActionPerformed

    private void mail_report_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mail_report_btnActionPerformed
        stu_search_panel.removeAll();
        stu_search_panel.repaint();
        stu_search_panel.revalidate();
        stu_search_panel.add(mail_report_panel);
        stu_search_panel.repaint();
        stu_search_panel.revalidate();
        createMail(search_id.getText());
    }//GEN-LAST:event_mail_report_btnActionPerformed

    private void stu_report_nameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stu_report_nameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_stu_report_nameActionPerformed

    private void stu_report_emailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stu_report_emailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_stu_report_emailActionPerformed

    private void stu_report_subjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stu_report_subjectActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_stu_report_subjectActionPerformed
    
    private void send_mail_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_send_mail_btnActionPerformed
        String receiver = stu_report_email.getText();
        String subject = stu_report_subject.getText();
        String email_body = stu_report_body.getText();
        
        String result = sendMail(receiver, subject, email_body);
        if (result.equals("true"))
        {
            JOptionPane.showMessageDialog(this, "Email Sent Successfully");
        }
        else
        {
            JOptionPane.showMessageDialog(this, "Error! Email NOT sent!");
        }
    }//GEN-LAST:event_send_mail_btnActionPerformed
    //cancel mail sent
    private void cancel_mail_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancel_mail_btnActionPerformed
        stu_search_panel.removeAll();
        stu_search_panel.repaint();
        stu_search_panel.revalidate();
        stu_search_panel.add(student_init_panel);
        stu_search_panel.repaint();
        stu_search_panel.revalidate();
    }//GEN-LAST:event_cancel_mail_btnActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton6ActionPerformed
    //recording payments made by postgrads
    private void post_payment_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_post_payment_btnActionPerformed
        double pay_amt;
        double due_amt;
        double total;
        try
        {
            pay_amt = Double.parseDouble(make_pay_field.getText());
            due_amt = Double.parseDouble(out_pay_field.getText());
            total = due_amt - pay_amt;
            String sql = "UPDATE student SET fee_payable=? WHERE student_id=?";
            pst = conn.prepareStatement(sql);
            pst.setDouble(1, total);
            pst.setString(2, search_id.getText());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Payment Registered!");
            search_student_btn.doClick();
            make_pay_field.setText("");
        }
        catch (NumberFormatException | SQLException e)
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error! Please enter proper payment amount!");
        }
    }//GEN-LAST:event_post_payment_btnActionPerformed

    private void make_pay_field1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_make_pay_field1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_make_pay_field1ActionPerformed

    private void out_pay_field1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_out_pay_field1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_out_pay_field1ActionPerformed
    //recording payments made by undergrads
    private void under_payment_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_under_payment_btnActionPerformed
        double pay_amt;
        double due_amt;
        double total;
        try
        {
            pay_amt = Double.parseDouble(make_pay_field1.getText());
            due_amt = Double.parseDouble(out_pay_field1.getText());
            total = due_amt - pay_amt;
            String sql = "UPDATE student SET fee_payable=? WHERE student_id=?";
            pst = conn.prepareStatement(sql);
            pst.setDouble(1, total);
            pst.setString(2, search_id.getText());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Payment Registered!");
            search_student_btn.doClick();
            make_pay_field1.setText("");
        }
        catch (NumberFormatException | SQLException e)
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error! Please enter proper payment amount!");
        }
    }//GEN-LAST:event_under_payment_btnActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(UI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        try {
            /* Create and display the form */
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3307/course_enroll?","root","assasin");

        } catch (ClassNotFoundException | SQLException e) {
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new UI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton Postgraduate;
    private javax.swing.JToggleButton Undergraduate;
    private javax.swing.JButton add_admin_btn;
    private javax.swing.JButton add_instructor_btn;
    private javax.swing.JPanel add_instructor_panel;
    private javax.swing.JToggleButton add_instructor_show_btn;
    public javax.swing.JButton add_lab_btn;
    private javax.swing.JPanel add_mark_panel;
    private javax.swing.JButton add_task_mark_btn;
    private javax.swing.JButton add_task_mark_btn1;
    private javax.swing.JTextField address;
    private javax.swing.JTextField address_field1;
    private javax.swing.JTextField address_field2;
    private javax.swing.JTextField address_staff;
    private javax.swing.JSeparator admin_field_line;
    private javax.swing.JSeparator admin_field_line1;
    private javax.swing.JTextField admin_id_field;
    private javax.swing.JPanel admin_login;
    private javax.swing.JLabel admin_login_btn;
    private javax.swing.JPanel admin_pane;
    private javax.swing.JPasswordField admin_pass_field;
    private javax.swing.JSeparator admin_pass_line;
    private javax.swing.JTextField alvl_island_rank;
    private javax.swing.JLabel app_exit_btn;
    private javax.swing.JButton assign_lab_show_btn;
    private javax.swing.JButton assign_session_btn;
    private javax.swing.JTextField awarded_mark_field;
    private com.toedter.calendar.JDateChooser birthday_selector;
    private com.toedter.calendar.JDateChooser birthday_selector_staff;
    private javax.swing.JPanel bottom_panel;
    private javax.swing.JButton cancel_admin_btn;
    private javax.swing.JButton cancel_mail_btn;
    private javax.swing.JButton cancel_mark_btn;
    private javax.swing.JButton cancel_pw_btn;
    private javax.swing.JButton cancel_session_btn;
    private javax.swing.JButton change_pw_btn;
    private javax.swing.JPanel change_pw_pane;
    private javax.swing.JButton change_student_subject;
    private javax.swing.JButton chk_credits_btn;
    private javax.swing.JPanel comp_sub_pane;
    private javax.swing.JTextField complete_year_res;
    private javax.swing.JTextField completed_year;
    private javax.swing.JTextField confirm_pw_field;
    private javax.swing.JLabel cour_btn;
    private javax.swing.JPanel course;
    private javax.swing.JTextField course_credit_field;
    private javax.swing.JTextField course_credits;
    private javax.swing.JTextField course_duration;
    private javax.swing.JTextField course_duration_field;
    private javax.swing.JTextField course_field1;
    private javax.swing.JTextField course_field2;
    private javax.swing.JComboBox<String> course_id;
    private javax.swing.JTextField course_id_enter;
    private javax.swing.JPanel course_init_panel;
    private javax.swing.JTable course_jTable;
    private javax.swing.JComboBox<String> course_list1;
    private javax.swing.JTextField course_name;
    private javax.swing.JTextField course_name_field;
    public javax.swing.JButton course_reg_btn;
    private javax.swing.JButton course_reg_cancel_btn;
    private javax.swing.JPanel course_result_panel;
    private javax.swing.JPanel course_search_panel;
    private javax.swing.JTextField credit_gained_field;
    private javax.swing.JTextField credit_needed_field;
    private javax.swing.JPanel credit_pane_show;
    private javax.swing.JPanel credit_panel;
    private javax.swing.JTextField credits_per_year_field;
    private javax.swing.JLabel currentDate;
    private javax.swing.JLabel currentTime;
    private javax.swing.JLabel currentUser;
    private javax.swing.JTextField current_pw_field;
    private javax.swing.JTextField current_pw_field1;
    private javax.swing.JButton delete_cancel_btn;
    private javax.swing.JButton delete_course_btn;
    private javax.swing.JButton delete_lab_btn;
    private javax.swing.JButton delete_lab_cancel_btn;
    private javax.swing.JComboBox<String> delete_lab_list;
    private javax.swing.JButton delete_lab_panel_btn;
    private javax.swing.JButton delete_postgrad_btn;
    private javax.swing.JButton delete_session_btn;
    private javax.swing.JButton delete_staff_btn;
    private javax.swing.JButton delete_subject_btn;
    private javax.swing.JButton delete_task_btn;
    private javax.swing.JTextField delete_task_field;
    private javax.swing.JPanel delete_task_init;
    private javax.swing.JPanel delete_task_panel;
    private javax.swing.JPanel delete_task_show;
    private javax.swing.JButton delete_undergrad_btn;
    private javax.swing.JComboBox<String> designation_staff;
    private javax.swing.JTextField dob_field1;
    private javax.swing.JTextField dob_field2;
    private javax.swing.JComboBox<String> duration_list;
    private javax.swing.JComboBox<String> duration_list2;
    private javax.swing.JTextField email;
    private javax.swing.JTextField email_field1;
    private javax.swing.JTextField email_field2;
    private javax.swing.JTextField email_staff;
    private javax.swing.JTextField eng_res1;
    private javax.swing.JTextField eng_result;
    private javax.persistence.EntityManager entityManager;
    private javax.swing.JToggleButton feb_sem_btn;
    private javax.swing.JTextField fee_payable;
    private javax.swing.JTextField fname;
    private javax.swing.JTextField fname_field1;
    private javax.swing.JTextField fname_field2;
    private javax.swing.JTextField fname_staff;
    private javax.swing.JComboBox<String> gender;
    private javax.swing.JTextField gender_field1;
    private javax.swing.JTextField gender_field2;
    private javax.swing.JComboBox<String> gender_staff;
    public javax.swing.JButton get_mark_btn;
    private javax.swing.JButton get_schedule_btn;
    private javax.swing.JButton get_today_schedule_btn;
    private javax.swing.JTextField gk_res1;
    private javax.swing.JTextField gk_result;
    private javax.swing.JPanel grad_select;
    private javax.swing.JPanel grad_select1;
    private javax.swing.JPanel home;
    private javax.swing.JLabel home_btn;
    private javax.swing.JPanel initpanel;
    private javax.swing.JTable inst_res_jTable;
    private javax.swing.JTextField institute;
    private javax.swing.JTextField institute_res;
    private javax.swing.JPanel instructor_init_panel;
    private javax.swing.JComboBox<String> instructor_list1;
    private javax.swing.JPanel instructor_select;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel100;
    private javax.swing.JLabel jLabel101;
    private javax.swing.JLabel jLabel102;
    private javax.swing.JLabel jLabel103;
    private javax.swing.JLabel jLabel104;
    private javax.swing.JLabel jLabel105;
    private javax.swing.JLabel jLabel106;
    private javax.swing.JLabel jLabel107;
    private javax.swing.JLabel jLabel108;
    private javax.swing.JLabel jLabel109;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel110;
    private javax.swing.JLabel jLabel111;
    private javax.swing.JLabel jLabel112;
    private javax.swing.JLabel jLabel113;
    private javax.swing.JLabel jLabel114;
    private javax.swing.JLabel jLabel115;
    private javax.swing.JLabel jLabel116;
    private javax.swing.JLabel jLabel117;
    private javax.swing.JLabel jLabel118;
    private javax.swing.JLabel jLabel119;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel120;
    private javax.swing.JLabel jLabel121;
    private javax.swing.JLabel jLabel122;
    private javax.swing.JLabel jLabel123;
    private javax.swing.JLabel jLabel124;
    private javax.swing.JLabel jLabel125;
    private javax.swing.JLabel jLabel126;
    private javax.swing.JLabel jLabel127;
    private javax.swing.JLabel jLabel128;
    private javax.swing.JLabel jLabel129;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel130;
    private javax.swing.JLabel jLabel131;
    private javax.swing.JLabel jLabel132;
    private javax.swing.JLabel jLabel133;
    private javax.swing.JLabel jLabel134;
    private javax.swing.JLabel jLabel135;
    private javax.swing.JLabel jLabel136;
    private javax.swing.JLabel jLabel137;
    private javax.swing.JLabel jLabel138;
    private javax.swing.JLabel jLabel139;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel140;
    private javax.swing.JLabel jLabel141;
    private javax.swing.JLabel jLabel142;
    private javax.swing.JLabel jLabel143;
    private javax.swing.JLabel jLabel144;
    private javax.swing.JLabel jLabel145;
    private javax.swing.JLabel jLabel146;
    private javax.swing.JLabel jLabel147;
    private javax.swing.JLabel jLabel148;
    private javax.swing.JLabel jLabel149;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel150;
    private javax.swing.JLabel jLabel151;
    private javax.swing.JLabel jLabel152;
    private javax.swing.JLabel jLabel153;
    private javax.swing.JLabel jLabel154;
    private javax.swing.JLabel jLabel155;
    private javax.swing.JLabel jLabel156;
    private javax.swing.JLabel jLabel157;
    private javax.swing.JLabel jLabel158;
    private javax.swing.JLabel jLabel159;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel160;
    private javax.swing.JLabel jLabel161;
    private javax.swing.JLabel jLabel162;
    private javax.swing.JLabel jLabel163;
    private javax.swing.JLabel jLabel164;
    private javax.swing.JLabel jLabel165;
    private javax.swing.JLabel jLabel166;
    private javax.swing.JLabel jLabel167;
    private javax.swing.JLabel jLabel168;
    private javax.swing.JLabel jLabel169;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel170;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel87;
    private javax.swing.JLabel jLabel88;
    private javax.swing.JLabel jLabel89;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel90;
    private javax.swing.JLabel jLabel91;
    private javax.swing.JLabel jLabel92;
    private javax.swing.JLabel jLabel93;
    private javax.swing.JLabel jLabel94;
    private javax.swing.JLabel jLabel95;
    private javax.swing.JLabel jLabel96;
    private javax.swing.JLabel jLabel97;
    private javax.swing.JLabel jLabel98;
    private javax.swing.JLabel jLabel99;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JToggleButton jul_sem_btn;
    private javax.swing.JPanel lab;
    private javax.swing.JPanel lab_assign_panel;
    private javax.swing.JLabel lab_btn;
    private javax.swing.JTextField lab_building;
    private javax.swing.JButton lab_cancel_btn;
    private javax.swing.JComboBox<String> lab_date_list;
    private javax.swing.JPanel lab_delete_init;
    private javax.swing.JPanel lab_delete_main;
    private javax.swing.JPanel lab_delete_result;
    private javax.swing.JTextField lab_floor;
    private javax.swing.JTextField lab_id_enter;
    private javax.swing.JComboBox<String> lab_id_list;
    private javax.swing.JPanel lab_init_panel;
    private javax.swing.JTable lab_jTable;
    private javax.swing.JPanel lab_main_panel;
    private javax.swing.JTable lab_schedule_jTable;
    private javax.swing.JComboBox<String> lab_subject_list;
    private com.github.lgooddatepicker.components.TimePicker lab_time;
    private javax.swing.JComboBox<String> lecturer_list;
    private javax.swing.JComboBox<String> lecturer_list2;
    private javax.swing.JTextField lname;
    private javax.swing.JTextField lname_field1;
    private javax.swing.JTextField lname_field2;
    private javax.swing.JTextField lname_staff;
    private javax.swing.JLabel login_btn;
    private javax.swing.JLabel login_cancel_btn;
    private javax.swing.JPanel login_details_init;
    private javax.swing.JPanel login_details_select;
    private javax.swing.JPanel login_pane;
    private javax.swing.JPanel logout;
    private javax.swing.JButton logout_btn;
    private javax.swing.JPanel logout_display;
    private javax.swing.JPanel logout_init_pane;
    private javax.swing.JPanel logout_pane;
    private javax.swing.JButton mail_report_btn;
    private javax.swing.JPanel mail_report_panel;
    private javax.swing.JPanel main_back_pane;
    private javax.swing.JTextField make_pay_field;
    private javax.swing.JTextField make_pay_field1;
    private javax.swing.JPanel mark;
    private javax.swing.JLabel mark_btn;
    private javax.swing.JPanel mark_options_init;
    private javax.swing.JPanel mark_options_pane;
    private javax.swing.JPanel mark_options_select;
    private javax.swing.JTextField mark_stu_id;
    private javax.swing.JTextField mark_stu_id2;
    private javax.swing.JTable marks_jTable;
    private javax.swing.JPanel marks_pane;
    private javax.swing.JButton new_admin_btn;
    private javax.swing.JTextField new_admin_field;
    private javax.swing.JPanel new_admin_pane;
    private javax.swing.JTextField new_admin_pw_field;
    private javax.swing.JTextField new_pw_field;
    private javax.swing.JComboBox<String> opt_sub1_list;
    private javax.swing.JComboBox<String> opt_sub2_list;
    private javax.swing.JComboBox<String> opt_sub3_list;
    private javax.swing.JComboBox<String> opt_sub4_list;
    private javax.swing.JPanel opt_sub_pane;
    private javax.swing.JTextField out_pay_field;
    private javax.swing.JTextField out_pay_field1;
    private javax.swing.JTextField outstanding_fee;
    private javax.swing.JPanel post_leader_panel;
    private javax.swing.JTable post_leaderboard;
    private javax.swing.JButton post_payment_btn;
    private javax.swing.JPanel post_result_panel;
    private javax.swing.JPanel postgrad;
    private javax.swing.JButton pw_change_btn;
    private javax.swing.JTextField qualification;
    private javax.swing.JTextField qualification_res;
    private javax.swing.JTextField qualification_staff;
    private javax.swing.JTextField rank_res1;
    private javax.swing.JButton register_student_subject;
    private javax.swing.JTextField result_1;
    private javax.swing.JTextField result_2;
    private javax.swing.JTextField result_3;
    private javax.swing.JLabel schedule_day;
    private javax.swing.JButton search_course_btn;
    private javax.swing.JTextField search_course_id;
    private javax.swing.JTextField search_id;
    private javax.swing.JButton search_staff_btn;
    private javax.swing.JTextField search_staff_id;
    private javax.swing.JButton search_student_btn;
    private javax.swing.JButton search_subject_btn;
    private javax.swing.JTextField search_subject_id;
    private javax.swing.JComboBox<String> select_date;
    private javax.swing.JComboBox<String> semester;
    private javax.swing.JTextField semester_field1;
    private javax.swing.JTextField semester_field2;
    private javax.swing.JButton send_mail_btn;
    private javax.swing.JButton show_all_task_marks;
    private javax.swing.JButton show_delete_task_pane;
    private javax.swing.JButton show_mark_pane_btn;
    private javax.swing.JButton show_mark_table_btn;
    private javax.swing.JPanel staff;
    private javax.swing.JTextField staff_address_field;
    private javax.swing.JLabel staff_btn;
    private javax.swing.JLabel staff_count;
    private javax.swing.JTextField staff_designation_field;
    private javax.swing.JTextField staff_dob_field;
    private javax.swing.JTextField staff_email_field;
    private javax.swing.JTextField staff_fname_field;
    private javax.swing.JTextField staff_gender_field;
    private javax.swing.JTextField staff_id;
    private javax.swing.JPanel staff_init_panel;
    private javax.swing.JTable staff_jTable;
    private javax.swing.JTextField staff_joindate_field;
    private javax.swing.JTextField staff_lname_field;
    private javax.swing.JTextField staff_qualification_field;
    public javax.swing.JButton staff_reg_btn;
    private javax.swing.JButton staff_reg_cancel_btn;
    private javax.swing.JPanel staff_result_panel;
    private javax.swing.JPanel staff_search_panel;
    private javax.swing.JTextField staff_tele_field;
    private javax.swing.JTextField stu_address_field;
    private javax.swing.JLabel stu_count;
    private javax.swing.JTextField stu_course_field;
    private javax.swing.JPanel stu_detail_pane;
    private javax.swing.JLabel stu_fname_field;
    private javax.swing.JTextField stu_gpa_field;
    private javax.swing.JTextField stu_id_field;
    private javax.swing.JTable stu_jTable;
    private javax.swing.JTextField stu_join_date_field;
    private javax.swing.JTextField stu_join_date_field1;
    private javax.swing.JLabel stu_login_btn;
    private javax.swing.JLabel stu_login_cancel_btn;
    private javax.swing.JButton stu_logout_btn;
    private javax.swing.JTable stu_marks_jTable;
    private javax.swing.JTextField stu_name_field;
    private javax.swing.JTextField stu_nic_field;
    private javax.swing.JTextField stu_rank_field;
    private javax.swing.JTextArea stu_report_body;
    private javax.swing.JTextField stu_report_email;
    private javax.swing.JTextField stu_report_name;
    private javax.swing.JTextField stu_report_subject;
    private javax.swing.JPanel stu_search_panel;
    private javax.swing.JPanel stu_sub_mark_init;
    private javax.swing.JPanel stu_sub_mark_panel;
    private javax.swing.JPanel stu_sub_mark_select;
    private javax.swing.JPanel stu_subject_select_pane;
    private javax.swing.JPanel stu_task_init;
    private javax.swing.JPanel stu_task_mark;
    private javax.swing.JLabel stud_btn;
    private javax.swing.JPanel student;
    private javax.swing.JTextField student_id;
    private javax.swing.JPanel student_init_panel;
    private javax.swing.JPanel student_login;
    private javax.swing.JLabel student_login_btn;
    private javax.swing.JButton student_mark_option;
    private javax.swing.JPanel student_pane;
    public javax.swing.JButton student_reg_btn;
    private javax.swing.JButton student_reg_cancel_btn;
    private javax.swing.JComboBox<String> sub1_list;
    private javax.swing.JTextField sub1_res1;
    private javax.swing.JComboBox<String> sub2_list;
    private javax.swing.JTextField sub2_res1;
    private javax.swing.JComboBox<String> sub3_list;
    private javax.swing.JTextField sub3_res1;
    private javax.swing.JComboBox<String> sub4_list;
    private javax.swing.JLabel sub_btn;
    private javax.swing.JPanel sub_select_init_pane;
    private javax.swing.JPanel sub_select_pane;
    private javax.swing.JPanel sub_select_res_pane;
    private javax.swing.JPanel subject;
    private javax.swing.JTextField subject_credit_field;
    private javax.swing.JTextField subject_credits;
    private javax.swing.JTextField subject_duration_field;
    private javax.swing.JTextField subject_fee;
    private javax.swing.JTextField subject_fee_field;
    private javax.swing.JTextField subject_id_enter;
    private javax.swing.JPanel subject_init_panel;
    private javax.swing.JTable subject_jTable;
    private javax.swing.JTextField subject_lecturer_field;
    private javax.swing.JComboBox<String> subject_list1;
    private javax.swing.JTextField subject_name;
    private javax.swing.JTextField subject_name_field;
    public javax.swing.JButton subject_reg_btn;
    private javax.swing.JButton subject_reg_cancel_btn;
    private javax.swing.JPanel subject_result_panel;
    private javax.swing.JPanel subject_search_panel;
    private javax.swing.JTextField task_id_field;
    private javax.swing.JTable task_mark_jTable;
    private javax.swing.JPanel task_mark_panel;
    private javax.swing.JComboBox<String> task_subject_list;
    private javax.swing.JComboBox<String> task_type_list;
    private javax.swing.JTextField tele_field1;
    private javax.swing.JTextField tele_field2;
    private javax.swing.JTextField telephone;
    private javax.swing.JTextField telephone_staff;
    private javax.swing.JPanel top_menu;
    private javax.swing.JTextField total_mark_field;
    private javax.swing.JTextField total_selection_fee;
    private javax.swing.JTextField type_field1;
    private javax.swing.JTextField type_field2;
    private javax.swing.JPanel under_leader_panel;
    private javax.swing.JTable under_leaderboard;
    private javax.swing.JButton under_payment_btn;
    private javax.swing.JPanel under_result_panel;
    private javax.swing.JPanel undergrad;
    private javax.swing.JLabel uni_rank;
    private javax.swing.JButton update_course_btn;
    private javax.swing.JButton update_postgrad_btn;
    private javax.swing.JButton update_staff_btn;
    private javax.swing.JButton update_subject_btn;
    private javax.swing.JButton update_subject_table;
    private javax.swing.JButton update_undergrad_btn;
    private javax.swing.JLabel user;
    private javax.swing.JPanel view_mark_panel;
    // End of variables declaration//GEN-END:variables
}
