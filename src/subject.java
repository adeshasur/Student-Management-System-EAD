import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

/** Subject management form — dark themed CRUD */
public class subject extends JFrame {

    Connection con;
    PreparedStatement pst;
    ResultSet rs;
    DefaultTableModel d;

    private JTextField txtsubject;
    private JTable jTable1;
    private JButton btnsave;

    public subject() {
        UITheme.applyGlobalDefaults();
        buildUI();
        Connect();
        Subject_Load();
    }

    public void Connect() {
        try { Class.forName("org.h2.Driver"); con = DriverManager.getConnection(UITheme.dbUrl(), "sa", ""); }
        catch (Exception ex) { Logger.getLogger(subject.class.getName()).log(Level.SEVERE, null, ex); }
    }

    private void Subject_Load() {
        try {
            pst = con.prepareStatement("SELECT * FROM SUBJECT ORDER BY SID");
            rs = pst.executeQuery(); d = (DefaultTableModel) jTable1.getModel(); d.setRowCount(0);
            while (rs.next()) {
                d.addRow(new Object[]{rs.getString("SID"), rs.getString("SUBJECTNAME")});
            }
        } catch (SQLException ex) { Logger.getLogger(subject.class.getName()).log(Level.SEVERE, null, ex); }
    }

    private void clear() { txtsubject.setText(""); btnsave.setEnabled(true); txtsubject.requestFocus(); }

    private void buildUI() {
        setTitle("Subjects — EduManage"); setDefaultCloseOperation(DISPOSE_ON_CLOSE); setSize(800, 500); setLocationRelativeTo(null);
        JPanel root = new JPanel(new BorderLayout()); root.setBackground(UITheme.BG); setContentPane(root);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.SURFACE);
        header.setBorder(BorderFactory.createCompoundBorder(new MatteBorder(0,0,1,0,UITheme.BORDER),new EmptyBorder(14,24,14,24)));
        header.setPreferredSize(new Dimension(0,60));
        header.add(UITheme.label("Subject Management",18f,true),BorderLayout.WEST);
        root.add(header,BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(20,0)); body.setBackground(UITheme.BG); body.setBorder(new EmptyBorder(20,20,20,20));

        JPanel form = new JPanel(); form.setBackground(UITheme.CARD); form.setLayout(new BoxLayout(form,BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createCompoundBorder(new LineBorder(UITheme.BORDER,1,true),new EmptyBorder(20,20,20,20)));
        form.setPreferredSize(new Dimension(280,0));

        JLabel st=UITheme.label("Manage Subjects",12f,true); st.setForeground(UITheme.ACCENT); st.setAlignmentX(LEFT_ALIGNMENT); form.add(st); form.add(Box.createVerticalStrut(12));

        JLabel ls=UITheme.mutedLabel("Subject Name"); ls.setAlignmentX(LEFT_ALIGNMENT); form.add(ls); form.add(Box.createVerticalStrut(3));
        txtsubject = UITheme.textField(""); txtsubject.setMaximumSize(new Dimension(Integer.MAX_VALUE,34)); txtsubject.setAlignmentX(LEFT_ALIGNMENT);
        form.add(txtsubject); form.add(Box.createVerticalStrut(20));

        btnsave  = UITheme.button("Save",   UITheme.ACCENT);
        JButton del = UITheme.button("Delete", UITheme.DANGER);
        JButton clr = UITheme.button("Clear",  UITheme.MUTED);
        for(JButton b:new JButton[]{btnsave,del,clr}){b.setAlignmentX(LEFT_ALIGNMENT);b.setMaximumSize(new Dimension(Integer.MAX_VALUE,36));form.add(b);form.add(Box.createVerticalStrut(8));}

        btnsave.addActionListener(e -> {
            try {
                pst = con.prepareStatement("INSERT INTO SUBJECT(SUBJECTNAME) VALUES(?)");
                pst.setString(1, txtsubject.getText()); pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Subject added."); Subject_Load(); clear();
            } catch (SQLException ex) { Logger.getLogger(subject.class.getName()).log(Level.SEVERE, null, ex); }
        });
        del.addActionListener(e -> {
            int row = jTable1.getSelectedRow(); if (row == -1) return;
            try {
                pst = con.prepareStatement("DELETE FROM SUBJECT WHERE SID=?");
                pst.setString(1, d.getValueAt(row, 0).toString());
                pst.executeUpdate(); JOptionPane.showMessageDialog(this, "Subject deleted."); Subject_Load(); clear();
            } catch (SQLException ex) { Logger.getLogger(subject.class.getName()).log(Level.SEVERE, null, ex); }
        });
        clr.addActionListener(e -> clear());

        // Table
        jTable1 = new JTable(new DefaultTableModel(new Object[][]{},new String[]{"ID","Subject Name"}){public boolean isCellEditable(int r,int c){return false;}});
        UITheme.styleTable(jTable1);
        jTable1.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e){ 
                int row = jTable1.getSelectedRow(); if(row==-1)return;
                txtsubject.setText(d.getValueAt(row,1).toString());
                btnsave.setEnabled(false);
            }
        });
        JScrollPane sp = UITheme.scrollPane(jTable1);

        body.add(form,BorderLayout.WEST); body.add(sp,BorderLayout.CENTER);
        root.add(body,BorderLayout.CENTER);
    }

    public static void main(String[] args){SwingUtilities.invokeLater(()->new subject().setVisible(true));}
}
