import java.awt.*;
import java.awt.event.*;
import java.io.InputStream;
import java.sql.*;
import java.util.HashMap;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;

/** Marks management — dark themed form with search and report */
public class marks extends JFrame {

    Connection con;
    PreparedStatement pst;
    ResultSet rs;
    DefaultTableModel d;

    private JTextField   txtno, txtstname, txtmarks;
    private JComboBox<String> txtclass, txtsubject, txtterm;
    private JTable       markstable;
    private JScrollPane  jScrollPane1;
    private JButton      jButton1, jButton2, jButton3, jButton4, jButton5;

    public marks() {
        UITheme.applyGlobalDefaults();
        buildUI();
        Connect();
        Load_Subject();
        Marks_Load();
    }

    public void Connect() {
        try { Class.forName("org.h2.Driver"); con = DriverManager.getConnection(UITheme.dbUrl(), "sa", ""); }
        catch (Exception ex) { Logger.getLogger(marks.class.getName()).log(Level.SEVERE, null, ex); }
    }

    public void Load_Subject() {
        try { pst = con.prepareStatement("SELECT DISTINCT SUBJECTNAME FROM SUBJECT ORDER BY SUBJECTNAME"); rs = pst.executeQuery(); txtsubject.removeAllItems(); while (rs.next()) txtsubject.addItem(rs.getString("SUBJECTNAME")); }
        catch (SQLException ex) { Logger.getLogger(marks.class.getName()).log(Level.SEVERE, null, ex); }
    }

    public void Marks_Load() {
        try {
            pst = con.prepareStatement("SELECT * FROM MARKS ORDER BY MARKID");
            rs = pst.executeQuery(); d = (DefaultTableModel) markstable.getModel(); d.setRowCount(0);
            while (rs.next()) {
                d.addRow(new Object[]{rs.getString("MARKID"), rs.getString("STID"), rs.getString("STNAME"), rs.getString("CLASS"), rs.getString("SUBJECT"), rs.getString("MARKS")});
            }
        } catch (SQLException ex) { Logger.getLogger(marks.class.getName()).log(Level.SEVERE, null, ex); }
    }

    private void printReport() {
        try {
            Class.forName("org.h2.Driver");
            Connection rc = DriverManager.getConnection(UITheme.dbUrl(), "sa", "");
            InputStream rs2 = getClass().getResourceAsStream("\\marks.jrxml");
            if (rs2 == null) { System.out.println("Report file not found"); return; }
            JasperReport jr = JasperCompileManager.compileReport(rs2);
            JasperPrint jp = JasperFillManager.fillReport(jr, null, rc);
            JasperViewer.viewReport(jp);
        } catch (Exception e) { System.err.println("Report error: " + e.getMessage()); }
    }

    private void buildUI() {
        setTitle("Marks — EduManage"); setDefaultCloseOperation(DISPOSE_ON_CLOSE); setSize(1050, 600); setLocationRelativeTo(null);
        JPanel root = new JPanel(new BorderLayout()); root.setBackground(UITheme.BG); setContentPane(root);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.SURFACE);
        header.setBorder(BorderFactory.createCompoundBorder(new MatteBorder(0,0,1,0,UITheme.BORDER),new EmptyBorder(14,24,14,24)));
        header.setPreferredSize(new Dimension(0,60));
        header.add(UITheme.label("📊  Marks Entry",18f,true),BorderLayout.WEST);
        jButton5 = UITheme.button("✕  Close",UITheme.MUTED); jButton5.addActionListener(e -> dispose());
        header.add(jButton5,BorderLayout.EAST); root.add(header,BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(20,0)); body.setBackground(UITheme.BG); body.setBorder(new EmptyBorder(20,20,20,20));

        JPanel form = new JPanel(); form.setBackground(UITheme.CARD); form.setLayout(new BoxLayout(form,BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createCompoundBorder(new LineBorder(UITheme.BORDER,1,true),new EmptyBorder(20,20,20,20)));
        form.setPreferredSize(new Dimension(280,0));

        JLabel st=UITheme.label("Add Marks",12f,true); st.setForeground(UITheme.ACCENT); st.setAlignmentX(LEFT_ALIGNMENT); form.add(st); form.add(Box.createVerticalStrut(12));

        JLabel lno=UITheme.mutedLabel("Student ID"); lno.setAlignmentX(LEFT_ALIGNMENT); form.add(lno); form.add(Box.createVerticalStrut(3));
        JPanel searchRow = new JPanel(new BorderLayout(6,0)); searchRow.setOpaque(false); searchRow.setMaximumSize(new Dimension(Integer.MAX_VALUE,34)); searchRow.setAlignmentX(LEFT_ALIGNMENT);
        txtno = UITheme.textField(""); searchRow.add(txtno,BorderLayout.CENTER);
        jButton1 = UITheme.button("🔍",UITheme.ACCENT); jButton1.setPreferredSize(new Dimension(44,34)); searchRow.add(jButton1,BorderLayout.EAST);
        form.add(searchRow); form.add(Box.createVerticalStrut(10));

        JLabel lname=UITheme.mutedLabel("Student Name"); lname.setAlignmentX(LEFT_ALIGNMENT); form.add(lname); form.add(Box.createVerticalStrut(3));
        txtstname=UITheme.textField(""); txtstname.setMaximumSize(new Dimension(Integer.MAX_VALUE,34)); txtstname.setAlignmentX(LEFT_ALIGNMENT); txtstname.setEditable(false);
        form.add(txtstname); form.add(Box.createVerticalStrut(10));

        txtclass   = addC(form,"Class",new String[]{});
        txtsubject = addC(form,"Subject",new String[]{});

        JLabel lm=UITheme.mutedLabel("Marks"); lm.setAlignmentX(LEFT_ALIGNMENT); form.add(lm); form.add(Box.createVerticalStrut(3));
        txtmarks=UITheme.textField("0-100"); txtmarks.setMaximumSize(new Dimension(Integer.MAX_VALUE,34)); txtmarks.setAlignmentX(LEFT_ALIGNMENT);
        form.add(txtmarks); form.add(Box.createVerticalStrut(10));

        txtterm = addC(form,"Term",new String[]{"Term 1","Term 2","Mid Year","Annual"});
        form.add(Box.createVerticalStrut(16));

        jButton3 = UITheme.button("💾 Save Marks",UITheme.ACCENT);
        jButton4 = UITheme.button("📄 Print Report",UITheme.PURPLE);
        jButton2 = UITheme.button("✕ Clear",UITheme.MUTED);
        for(JButton b:new JButton[]{jButton3,jButton4,jButton2}){b.setAlignmentX(LEFT_ALIGNMENT);b.setMaximumSize(new Dimension(Integer.MAX_VALUE,36));form.add(b);form.add(Box.createVerticalStrut(8));}

        jButton1.addActionListener(e -> {
            try {
                pst = con.prepareStatement("SELECT * FROM STUDENT WHERE STUDENTID=?");
                pst.setString(1,txtno.getText()); rs=pst.executeQuery();
                if(rs.next()){txtstname.setText(rs.getString("STNAME")); txtclass.addItem(rs.getString("CLASS"));}
                else{JOptionPane.showMessageDialog(this,"Student not found."); txtstname.setText("");}
            }catch(SQLException ex){Logger.getLogger(marks.class.getName()).log(Level.SEVERE,null,ex);}
        });
        jButton3.addActionListener(e -> {
            try {
                pst = con.prepareStatement("INSERT INTO MARKS(STID,STNAME,CLASS,SUBJECT,MARKS) VALUES(?,?,?,?,?)");
                pst.setString(1,txtno.getText()); pst.setString(2,txtstname.getText());
                pst.setString(3,txtclass.getSelectedItem()!=null?txtclass.getSelectedItem().toString():"");
                pst.setString(4,txtsubject.getSelectedItem()!=null?txtsubject.getSelectedItem().toString():"");
                pst.setString(5,txtmarks.getText());
                pst.executeUpdate(); JOptionPane.showMessageDialog(this,"✅ Marks saved."); Marks_Load();
            }catch(SQLException ex){Logger.getLogger(marks.class.getName()).log(Level.SEVERE,null,ex);}
        });
        jButton4.addActionListener(e -> printReport());
        jButton2.addActionListener(e -> { txtno.setText(""); txtstname.setText(""); txtmarks.setText(""); txtno.requestFocus(); });

        // Table
        markstable = new JTable(new DefaultTableModel(new Object[][]{},new String[]{"ID","Stu.ID","Name","Class","Subject","Marks"}){public boolean isCellEditable(int r,int c){return false;}});
        UITheme.styleTable(markstable);
        jScrollPane1 = UITheme.scrollPane(markstable);

        body.add(form,BorderLayout.WEST); body.add(jScrollPane1,BorderLayout.CENTER);
        root.add(body,BorderLayout.CENTER);
    }

    private JComboBox<String> addC(JPanel p,String label,String[]items){JLabel l=UITheme.mutedLabel(label);l.setAlignmentX(LEFT_ALIGNMENT);JComboBox<String>c=UITheme.comboBox(items);c.setMaximumSize(new Dimension(Integer.MAX_VALUE,34));c.setAlignmentX(LEFT_ALIGNMENT);p.add(l);p.add(Box.createVerticalStrut(3));p.add(c);p.add(Box.createVerticalStrut(10));return c;}

    public static void main(String[] args){SwingUtilities.invokeLater(()->new marks().setVisible(true));}
}
