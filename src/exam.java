import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

/** Exam management — dark themed CRUD form */
public class exam extends JFrame {

    Connection con;
    PreparedStatement pst;
    ResultSet rs;
    DefaultTableModel d;

    private JTextField   txtename, txtdate;
    private JComboBox<String> txtterm, txtclass, txtsection;
    private JTable       examtable;
    private JButton      btnsave, btnupdate;

    public exam() {
        UITheme.applyGlobalDefaults();
        buildUI();
        Connect();
        Load_Class();
        Load_Section();
        Exam_Load();
    }

    public void Connect() {
        try { Class.forName("org.h2.Driver"); con = DriverManager.getConnection(UITheme.dbUrl(), "sa", ""); }
        catch (Exception ex) { Logger.getLogger(exam.class.getName()).log(Level.SEVERE, null, ex); }
    }

    public void Load_Class() {
        try { pst = con.prepareStatement("SELECT DISTINCT CLASSNAME FROM CLASS ORDER BY CLASSNAME"); rs = pst.executeQuery(); txtclass.removeAllItems(); while (rs.next()) txtclass.addItem(rs.getString("CLASSNAME")); }
        catch (SQLException ex) { Logger.getLogger(exam.class.getName()).log(Level.SEVERE, null, ex); }
    }

    public void Load_Section() {
        try { pst = con.prepareStatement("SELECT DISTINCT SECTION FROM CLASS ORDER BY SECTION"); rs = pst.executeQuery(); txtsection.removeAllItems(); while (rs.next()) txtsection.addItem(rs.getString("SECTION")); }
        catch (SQLException ex) { Logger.getLogger(exam.class.getName()).log(Level.SEVERE, null, ex); }
    }

    public void Exam_Load() {
        try {
            pst = con.prepareStatement("SELECT * FROM EXAM ORDER BY EID");
            rs = pst.executeQuery(); d = (DefaultTableModel) examtable.getModel(); d.setRowCount(0);
            while (rs.next()) {
                d.addRow(new Object[]{rs.getString("EID"), rs.getString("ENAME"), rs.getString("TERM"), rs.getString("CLASS"), rs.getString("SECTION"), rs.getString("EDATE")});
            }
        } catch (SQLException ex) { Logger.getLogger(exam.class.getName()).log(Level.SEVERE, null, ex); }
    }

    private void clearForm() {
        txtename.setText(""); txtdate.setText("");
        if(txtterm.getItemCount()>0) txtterm.setSelectedIndex(0);
        if(txtclass.getItemCount()>0) txtclass.setSelectedIndex(0);
        if(txtsection.getItemCount()>0) txtsection.setSelectedIndex(0);
        btnsave.setEnabled(true); txtename.requestFocus();
    }

    private void buildUI() {
        setTitle("Exams — EduManage"); setDefaultCloseOperation(DISPOSE_ON_CLOSE); setSize(1100, 600); setLocationRelativeTo(null);
        JPanel root = new JPanel(new BorderLayout()); root.setBackground(UITheme.BG); setContentPane(root);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.SURFACE);
        header.setBorder(BorderFactory.createCompoundBorder(new MatteBorder(0,0,1,0,UITheme.BORDER),new EmptyBorder(14,24,14,24)));
        header.setPreferredSize(new Dimension(0,64));
        header.add(UITheme.label("Exam Management",20f,true),BorderLayout.WEST);
        root.add(header,BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(20,0)); body.setBackground(UITheme.BG); body.setBorder(new EmptyBorder(24,24,24,24));

        JPanel form = new JPanel(); form.setBackground(UITheme.CARD); form.setLayout(new BoxLayout(form,BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createCompoundBorder(new LineBorder(UITheme.BORDER,1,true),new EmptyBorder(24,24,24,24)));
        form.setPreferredSize(new Dimension(300,0));

        JLabel st=UITheme.label("Exam Schedule",14f,true); st.setForeground(UITheme.ACCENT); st.setAlignmentX(LEFT_ALIGNMENT); form.add(st); form.add(Box.createVerticalStrut(16));

        txtename = addF(form,"Exam Name");
        
        JLabel lt=UITheme.mutedLabel("Term"); lt.setAlignmentX(LEFT_ALIGNMENT); form.add(lt); form.add(Box.createVerticalStrut(4));
        txtterm=UITheme.comboBox(new String[]{"Term 1","Term 2","Term 3","Mid Year","Annual"}); txtterm.setMaximumSize(new Dimension(Integer.MAX_VALUE,34)); txtterm.setAlignmentX(LEFT_ALIGNMENT);
        form.add(txtterm); form.add(Box.createVerticalStrut(12));

        JLabel lc=UITheme.mutedLabel("Class"); lc.setAlignmentX(LEFT_ALIGNMENT); form.add(lc); form.add(Box.createVerticalStrut(4));
        txtclass=UITheme.comboBox(new String[]{}); txtclass.setMaximumSize(new Dimension(Integer.MAX_VALUE,34)); txtclass.setAlignmentX(LEFT_ALIGNMENT);
        form.add(txtclass); form.add(Box.createVerticalStrut(12));

        JLabel ls=UITheme.mutedLabel("Section"); ls.setAlignmentX(LEFT_ALIGNMENT); form.add(ls); form.add(Box.createVerticalStrut(4));
        txtsection=UITheme.comboBox(new String[]{}); txtsection.setMaximumSize(new Dimension(Integer.MAX_VALUE,34)); txtsection.setAlignmentX(LEFT_ALIGNMENT);
        form.add(txtsection); form.add(Box.createVerticalStrut(12));

        txtdate = addF(form,"Date (YYYY-MM-DD)");
        form.add(Box.createVerticalStrut(14));

        btnsave   = UITheme.button("Schedule Exam", UITheme.ACCENT);
        btnupdate = UITheme.button("Update Info",   UITheme.WARNING);
        JButton del = UITheme.button("Delete Exam",   UITheme.DANGER);
        JButton clr = UITheme.button("Clear Form",    UITheme.MUTED);

        for(JButton b:new JButton[]{btnsave,btnupdate,del,clr}){b.setAlignmentX(LEFT_ALIGNMENT);b.setMaximumSize(new Dimension(Integer.MAX_VALUE,38));form.add(b);form.add(Box.createVerticalStrut(10));}

        btnsave.addActionListener(e -> {
            try {
                pst = con.prepareStatement("INSERT INTO EXAM(ENAME,TERM,CLASS,SECTION,EDATE) VALUES(?,?,?,?,?)");
                pst.setString(1,txtename.getText()); pst.setString(2,txtterm.getSelectedItem().toString());
                pst.setString(3,txtclass.getSelectedItem().toString()); pst.setString(4,txtsection.getSelectedItem().toString());
                pst.setString(5,txtdate.getText());
                pst.executeUpdate(); JOptionPane.showMessageDialog(this, "Exam scheduled."); Exam_Load(); clearForm();
            }catch(SQLException ex){Logger.getLogger(exam.class.getName()).log(Level.SEVERE,null,ex);}
        });
        btnupdate.addActionListener(e -> {
            int row = examtable.getSelectedRow(); if(row==-1)return;
            try {
                pst = con.prepareStatement("UPDATE EXAM SET ENAME=?,TERM=?,CLASS=?,SECTION=?,EDATE=? WHERE EID=?");
                pst.setString(1,txtename.getText()); pst.setString(2,txtterm.getSelectedItem().toString());
                pst.setString(3,txtclass.getSelectedItem().toString()); pst.setString(4,txtsection.getSelectedItem().toString());
                pst.setString(5,txtdate.getText()); pst.setString(6,d.getValueAt(row,0).toString());
                pst.executeUpdate(); JOptionPane.showMessageDialog(this, "Record updated."); Exam_Load(); clearForm();
            }catch(SQLException ex){Logger.getLogger(exam.class.getName()).log(Level.SEVERE,null,ex);}
        });
        del.addActionListener(e -> {
            int row = examtable.getSelectedRow(); if(row==-1)return;
            try {
                pst = con.prepareStatement("DELETE FROM EXAM WHERE EID=?");
                pst.setString(1, d.getValueAt(row,0).toString());
                pst.executeUpdate(); JOptionPane.showMessageDialog(this, "Record deleted."); Exam_Load(); clearForm();
            }catch(SQLException ex){Logger.getLogger(exam.class.getName()).log(Level.SEVERE,null,ex);}
        });
        clr.addActionListener(e -> clearForm());

        // Table
        examtable = new JTable(new DefaultTableModel(new Object[][]{},new String[]{"ID","Exam Name","Term","Class","Sec","Date"}){public boolean isCellEditable(int r,int c){return false;}});
        UITheme.styleTable(examtable);
        examtable.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e){ 
                int row = examtable.getSelectedRow(); if(row==-1)return;
                txtename.setText(d.getValueAt(row,1).toString()); txtterm.setSelectedItem(d.getValueAt(row,2).toString());
                txtclass.setSelectedItem(d.getValueAt(row,3).toString()); txtsection.setSelectedItem(d.getValueAt(row,4).toString());
                txtdate.setText(d.getValueAt(row,5).toString());
                btnsave.setEnabled(false);
            }
        });
        JScrollPane sp = UITheme.scrollPane(examtable);

        body.add(form,BorderLayout.WEST); body.add(sp,BorderLayout.CENTER);
        root.add(body,BorderLayout.CENTER);
    }

    private JTextField addF(JPanel p,String label){JLabel l=UITheme.mutedLabel(label);l.setAlignmentX(LEFT_ALIGNMENT);JTextField f=UITheme.textField("");f.setMaximumSize(new Dimension(Integer.MAX_VALUE,34));f.setAlignmentX(LEFT_ALIGNMENT);p.add(l);p.add(Box.createVerticalStrut(4));p.add(f);p.add(Box.createVerticalStrut(10));return f;}

    public static void main(String[] args){SwingUtilities.invokeLater(()->new exam().setVisible(true));}
}
