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

    private JTextField   txtename;
    private JComboBox<String> txtterm, txtclass, txtsection, txtsubject;
    private JTextField   txtdate_str; // plain text date
    private JTable       examtable;
    private JScrollPane  jScrollPane1;
    private JButton      jButton1, jButton2, btncreate, jButton4;

    public exam() {
        UITheme.applyGlobalDefaults();
        buildUI();
        Connect();
        Load_Class(); Load_Section(); Load_Subject(); Exam_Load();
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
    public void Load_Subject() {
        try { pst = con.prepareStatement("SELECT DISTINCT SUBJECTNAME FROM SUBJECT ORDER BY SUBJECTNAME"); rs = pst.executeQuery(); txtsubject.removeAllItems(); while (rs.next()) txtsubject.addItem(rs.getString("SUBJECTNAME")); }
        catch (SQLException ex) { Logger.getLogger(exam.class.getName()).log(Level.SEVERE, null, ex); }
    }

    public void Exam_Load() {
        try {
            pst = con.prepareStatement("SELECT * FROM EXAM ORDER BY EXAMID");
            rs = pst.executeQuery();
            d = (DefaultTableModel) examtable.getModel(); d.setRowCount(0);
            while (rs.next()) {
                d.addRow(new Object[]{rs.getString("EXAMID"), rs.getString("EXAMNAME"), rs.getString("DATE"), rs.getString("CLASS"), rs.getString("SECTION"), rs.getString("SUBJECT")});
            }
        } catch (SQLException ex) { Logger.getLogger(exam.class.getName()).log(Level.SEVERE, null, ex); }
    }

    private void buildUI() {
        setTitle("Exams — EduManage"); setDefaultCloseOperation(DISPOSE_ON_CLOSE); setSize(1050, 580); setLocationRelativeTo(null);
        JPanel root = new JPanel(new BorderLayout()); root.setBackground(UITheme.BG); setContentPane(root);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.SURFACE);
        header.setBorder(BorderFactory.createCompoundBorder(new MatteBorder(0,0,1,0,UITheme.BORDER),new EmptyBorder(14,24,14,24)));
        header.setPreferredSize(new Dimension(0,60));
        header.add(UITheme.label("📝  Exam Management",18f,true),BorderLayout.WEST);
        jButton4 = UITheme.button("✕  Close",UITheme.MUTED); jButton4.addActionListener(e -> dispose());
        header.add(jButton4,BorderLayout.EAST); root.add(header,BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(20,0)); body.setBackground(UITheme.BG); body.setBorder(new EmptyBorder(20,20,20,20));

        JPanel form = new JPanel(); form.setBackground(UITheme.CARD); form.setLayout(new BoxLayout(form,BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createCompoundBorder(new LineBorder(UITheme.BORDER,1,true),new EmptyBorder(20,20,20,20)));
        form.setPreferredSize(new Dimension(270,0));

        JLabel st = UITheme.label("Add Exam",12f,true); st.setForeground(UITheme.ACCENT); st.setAlignmentX(LEFT_ALIGNMENT); form.add(st); form.add(Box.createVerticalStrut(12));

        txtename    = addF(form,"Exam Name");
        txtterm     = addC(form,"Term",new String[]{"Term 1","Term 2","Mid Year","Annual"});
        txtdate_str = addF(form,"Date (yyyy-MM-dd)");
        txtclass    = addC(form,"Class",new String[]{});
        txtsection  = addC(form,"Section",new String[]{});
        txtsubject  = addC(form,"Subject",new String[]{});
        form.add(Box.createVerticalStrut(16));

        jButton1 = UITheme.button("💾 Save",UITheme.ACCENT);
        jButton2 = UITheme.button("🗑 Delete",UITheme.DANGER);
        btncreate = UITheme.button("✕ Clear",UITheme.MUTED);
        for (JButton b:new JButton[]{jButton1,jButton2,btncreate}){b.setAlignmentX(LEFT_ALIGNMENT);b.setMaximumSize(new Dimension(Integer.MAX_VALUE,36));form.add(b);form.add(Box.createVerticalStrut(8));}

        jButton1.addActionListener(e -> {
            try {
                pst = con.prepareStatement("INSERT INTO EXAM(EXAMNAME,TERM,DATE,CLASS,SECTION,SUBJECT) VALUES(?,?,?,?,?,?)");
                pst.setString(1,txtename.getText()); pst.setString(2,txtterm.getSelectedItem().toString());
                pst.setString(3,txtdate_str.getText());
                pst.setString(4,txtclass.getSelectedItem()!=null?txtclass.getSelectedItem().toString():"");
                pst.setString(5,txtsection.getSelectedItem()!=null?txtsection.getSelectedItem().toString():"");
                pst.setString(6,txtsubject.getSelectedItem()!=null?txtsubject.getSelectedItem().toString():"");
                pst.executeUpdate(); JOptionPane.showMessageDialog(this,"✅ Exam added."); Exam_Load();
            } catch(SQLException ex){Logger.getLogger(exam.class.getName()).log(Level.SEVERE,null,ex);}
        });
        jButton2.addActionListener(e -> {
            int row=examtable.getSelectedRow(); if(row==-1){JOptionPane.showMessageDialog(this,"Select an exam.");return;}
            try{pst=con.prepareStatement("DELETE FROM EXAM WHERE EXAMID=?"); pst.setString(1,d.getValueAt(row,0).toString()); pst.executeUpdate(); JOptionPane.showMessageDialog(this,"🗑 Exam deleted."); Exam_Load();}
            catch(SQLException ex){Logger.getLogger(exam.class.getName()).log(Level.SEVERE,null,ex);}
        });
        btncreate.addActionListener(e -> { txtename.setText(""); txtdate_str.setText(""); jButton1.setEnabled(true); });

        examtable = new JTable(new DefaultTableModel(new Object[][]{},new String[]{"ID","Exam Name","Date","Class","Section","Subject"}){public boolean isCellEditable(int r,int c){return false;}});
        UITheme.styleTable(examtable);
        jScrollPane1 = UITheme.scrollPane(examtable);

        body.add(form,BorderLayout.WEST); body.add(jScrollPane1,BorderLayout.CENTER);
        root.add(body,BorderLayout.CENTER);
    }

    private JTextField addF(JPanel p,String label){JLabel l=UITheme.mutedLabel(label);l.setAlignmentX(LEFT_ALIGNMENT);JTextField f=UITheme.textField("");f.setMaximumSize(new Dimension(Integer.MAX_VALUE,34));f.setAlignmentX(LEFT_ALIGNMENT);p.add(l);p.add(Box.createVerticalStrut(3));p.add(f);p.add(Box.createVerticalStrut(10));return f;}
    private JComboBox<String> addC(JPanel p,String label,String[]items){JLabel l=UITheme.mutedLabel(label);l.setAlignmentX(LEFT_ALIGNMENT);JComboBox<String>c=UITheme.comboBox(items);c.setMaximumSize(new Dimension(Integer.MAX_VALUE,34));c.setAlignmentX(LEFT_ALIGNMENT);p.add(l);p.add(Box.createVerticalStrut(3));p.add(c);p.add(Box.createVerticalStrut(10));return c;}

    public static void main(String[] args){SwingUtilities.invokeLater(()->new exam().setVisible(true));}
}
