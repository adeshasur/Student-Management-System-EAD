import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

/** Student management — dark themed CRUD form */
public class student extends JFrame {

    Connection con;
    PreparedStatement pst;
    ResultSet rs;
    DefaultTableModel d;

    private JTextField   txtstname, txtpname, txtdob_str, txtphone, txtaddress;
    private JComboBox<String> txtclass, txtsection, txtgender;
    private JTable       studenttable;
    private JButton      btnsave, btnupdate;

    public student() {
        UITheme.applyGlobalDefaults();
        buildUI();
        Connect();
        Load_Class();
        Load_Section();
        Student_Load();
    }

    public void Connect() {
        try { Class.forName("org.h2.Driver"); con = DriverManager.getConnection(UITheme.dbUrl(), "sa", ""); }
        catch (Exception ex) { Logger.getLogger(student.class.getName()).log(Level.SEVERE, null, ex); }
    }

    public void Load_Class() {
        try { pst = con.prepareStatement("SELECT DISTINCT CLASSNAME FROM CLASS ORDER BY CLASSNAME"); rs = pst.executeQuery(); txtclass.removeAllItems(); while (rs.next()) txtclass.addItem(rs.getString("CLASSNAME")); }
        catch (SQLException ex) { Logger.getLogger(student.class.getName()).log(Level.SEVERE, null, ex); }
    }

    public void Load_Section() {
        try { pst = con.prepareStatement("SELECT DISTINCT SECTION FROM CLASS ORDER BY SECTION"); rs = pst.executeQuery(); txtsection.removeAllItems(); while (rs.next()) txtsection.addItem(rs.getString("SECTION")); }
        catch (SQLException ex) { Logger.getLogger(student.class.getName()).log(Level.SEVERE, null, ex); }
    }

    public void Student_Load() {
        try {
            pst = con.prepareStatement("SELECT * FROM STUDENT ORDER BY STUDENTID");
            rs = pst.executeQuery(); d = (DefaultTableModel) studenttable.getModel(); d.setRowCount(0);
            while (rs.next()) {
                d.addRow(new Object[]{rs.getString("STUDENTID"), rs.getString("STNAME"), rs.getString("PNAME"), rs.getString("DOB"), rs.getString("GENDER"), rs.getString("PHONE"), rs.getString("ADDRESS"), rs.getString("CLASS"), rs.getString("SECTION")});
            }
        } catch (SQLException ex) { Logger.getLogger(student.class.getName()).log(Level.SEVERE, null, ex); }
    }

    private void clearForm() {
        txtstname.setText(""); txtpname.setText(""); txtdob_str.setText(""); txtphone.setText(""); txtaddress.setText("");
        if(txtclass.getItemCount()>0) txtclass.setSelectedIndex(0);
        if(txtsection.getItemCount()>0) txtsection.setSelectedIndex(0);
        if(txtgender.getItemCount()>0) txtgender.setSelectedIndex(0);
        btnsave.setEnabled(true); txtstname.requestFocus();
    }

    private void buildUI() {
        setTitle("Students — EduManage"); setDefaultCloseOperation(DISPOSE_ON_CLOSE); setSize(1150, 650); setLocationRelativeTo(null);
        JPanel root = new JPanel(new BorderLayout()); root.setBackground(UITheme.BG); setContentPane(root);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.SURFACE);
        header.setBorder(BorderFactory.createCompoundBorder(new MatteBorder(0,0,1,0,UITheme.BORDER),new EmptyBorder(14,24,14,24)));
        header.setPreferredSize(new Dimension(0,64));
        header.add(UITheme.label("Student Management",20f,true),BorderLayout.WEST);
        root.add(header,BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(20,0)); body.setBackground(UITheme.BG); body.setBorder(new EmptyBorder(24,24,24,24));

        JPanel form = new JPanel(); form.setBackground(UITheme.CARD); form.setLayout(new BoxLayout(form,BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createCompoundBorder(new LineBorder(UITheme.BORDER,1,true),new EmptyBorder(24,24,24,24)));
        form.setPreferredSize(new Dimension(300,0));

        JLabel st=UITheme.label("Student Registration",14f,true); st.setForeground(UITheme.ACCENT); st.setAlignmentX(LEFT_ALIGNMENT); form.add(st); form.add(Box.createVerticalStrut(16));

        txtstname  = addF(form,"Student Name");
        txtpname   = addF(form,"Parent Name");
        txtdob_str = addF(form,"Date of Birth (YYYY-MM-DD)");
        
        JLabel lg=UITheme.mutedLabel("Gender"); lg.setAlignmentX(LEFT_ALIGNMENT); form.add(lg); form.add(Box.createVerticalStrut(4));
        txtgender=UITheme.comboBox(new String[]{"Male","Female","Other"}); txtgender.setMaximumSize(new Dimension(Integer.MAX_VALUE,34)); txtgender.setAlignmentX(LEFT_ALIGNMENT);
        form.add(txtgender); form.add(Box.createVerticalStrut(12));

        txtphone   = addF(form,"Phone Number");
        txtaddress = addF(form,"Address");
        
        JLabel lc=UITheme.mutedLabel("Class"); lc.setAlignmentX(LEFT_ALIGNMENT); form.add(lc); form.add(Box.createVerticalStrut(4));
        txtclass=UITheme.comboBox(new String[]{}); txtclass.setMaximumSize(new Dimension(Integer.MAX_VALUE,34)); txtclass.setAlignmentX(LEFT_ALIGNMENT);
        form.add(txtclass); form.add(Box.createVerticalStrut(12));

        JLabel ls=UITheme.mutedLabel("Section"); ls.setAlignmentX(LEFT_ALIGNMENT); form.add(ls); form.add(Box.createVerticalStrut(4));
        txtsection=UITheme.comboBox(new String[]{}); txtsection.setMaximumSize(new Dimension(Integer.MAX_VALUE,34)); txtsection.setAlignmentX(LEFT_ALIGNMENT);
        form.add(txtsection); form.add(Box.createVerticalStrut(24));

        btnsave   = UITheme.button("Save Record", UITheme.ACCENT);
        btnupdate = UITheme.button("Update Info", UITheme.WARNING);
        JButton del = UITheme.button("Delete Student", UITheme.DANGER);
        JButton clr = UITheme.button("Clear Form",    UITheme.MUTED);

        for(JButton b:new JButton[]{btnsave,btnupdate,del,clr}){b.setAlignmentX(LEFT_ALIGNMENT);b.setMaximumSize(new Dimension(Integer.MAX_VALUE,38));form.add(b);form.add(Box.createVerticalStrut(10));}

        btnsave.addActionListener(e -> {
            try {
                pst = con.prepareStatement("INSERT INTO STUDENT(STNAME,PNAME,DOB,GENDER,PHONE,ADDRESS,CLASS,SECTION) VALUES(?,?,?,?,?,?,?,?)");
                pst.setString(1,txtstname.getText()); pst.setString(2,txtpname.getText()); pst.setString(3,txtdob_str.getText());
                pst.setString(4,txtgender.getSelectedItem().toString()); pst.setString(5,txtphone.getText());
                pst.setString(6,txtaddress.getText()); pst.setString(7,txtclass.getSelectedItem().toString());
                pst.setString(8,txtsection.getSelectedItem().toString());
                pst.executeUpdate(); JOptionPane.showMessageDialog(this, "Student added."); Student_Load(); clearForm();
            }catch(SQLException ex){Logger.getLogger(student.class.getName()).log(Level.SEVERE,null,ex);}
        });
        btnupdate.addActionListener(e -> {
            int row = studenttable.getSelectedRow(); if(row==-1)return;
            try {
                pst = con.prepareStatement("UPDATE STUDENT SET STNAME=?,PNAME=?,DOB=?,GENDER=?,PHONE=?,ADDRESS=?,CLASS=?,SECTION=? WHERE STUDENTID=?");
                pst.setString(1,txtstname.getText()); pst.setString(2,txtpname.getText()); pst.setString(3,txtdob_str.getText());
                pst.setString(4,txtgender.getSelectedItem().toString()); pst.setString(5,txtphone.getText());
                pst.setString(6,txtaddress.getText()); pst.setString(7,txtclass.getSelectedItem().toString());
                pst.setString(8,txtsection.getSelectedItem().toString()); pst.setString(9,d.getValueAt(row,0).toString());
                pst.executeUpdate(); JOptionPane.showMessageDialog(this, "Record updated."); Student_Load(); clearForm();
            }catch(SQLException ex){Logger.getLogger(student.class.getName()).log(Level.SEVERE,null,ex);}
        });
        del.addActionListener(e -> {
            int row = studenttable.getSelectedRow(); if(row==-1)return;
            try {
                pst = con.prepareStatement("DELETE FROM STUDENT WHERE STUDENTID=?");
                pst.setString(1, d.getValueAt(row,0).toString());
                pst.executeUpdate(); JOptionPane.showMessageDialog(this, "Record deleted."); Student_Load(); clearForm();
            }catch(SQLException ex){Logger.getLogger(student.class.getName()).log(Level.SEVERE,null,ex);}
        });
        clr.addActionListener(e -> clearForm());

        // Table
        studenttable = new JTable(new DefaultTableModel(new Object[][]{},new String[]{"ID","Name","Parent","DOB","Gender","Phone","Address","Class","Sec"}){public boolean isCellEditable(int r,int c){return false;}});
        UITheme.styleTable(studenttable);
        studenttable.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e){ 
                int row = studenttable.getSelectedRow(); if(row==-1)return;
                txtstname.setText(d.getValueAt(row,1).toString()); txtpname.setText(d.getValueAt(row,2).toString());
                txtdob_str.setText(d.getValueAt(row,3).toString()); txtgender.setSelectedItem(d.getValueAt(row,4).toString());
                txtphone.setText(d.getValueAt(row,5).toString()); txtaddress.setText(d.getValueAt(row,6).toString());
                txtclass.setSelectedItem(d.getValueAt(row,7).toString()); txtsection.setSelectedItem(d.getValueAt(row,8).toString());
                btnsave.setEnabled(false);
            }
        });
        JScrollPane sp = UITheme.scrollPane(studenttable);

        body.add(form,BorderLayout.WEST); body.add(sp,BorderLayout.CENTER);
        root.add(body,BorderLayout.CENTER);
    }

    private JTextField addF(JPanel p,String label){JLabel l=UITheme.mutedLabel(label);l.setAlignmentX(LEFT_ALIGNMENT);JTextField f=UITheme.textField("");f.setMaximumSize(new Dimension(Integer.MAX_VALUE,34));f.setAlignmentX(LEFT_ALIGNMENT);p.add(l);p.add(Box.createVerticalStrut(4));p.add(f);p.add(Box.createVerticalStrut(10));return f;}

    public static void main(String[] args){SwingUtilities.invokeLater(()->new student().setVisible(true));}
}
