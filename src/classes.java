import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

/** Class management form — dark themed CRUD */
public class classes extends JFrame {

    Connection con;
    PreparedStatement pst;
    ResultSet rs;
    DefaultTableModel d;

    private JComboBox<String> txtclass, txtsection;
    private JTable jTable1;
    private JButton btnsave;

    public classes() {
        UITheme.applyGlobalDefaults();
        buildUI();
        Connect();
        Class_Load();
    }

    public void Connect() {
        try { Class.forName("org.h2.Driver"); con = DriverManager.getConnection(UITheme.dbUrl(), "sa", ""); }
        catch (Exception ex) { Logger.getLogger(classes.class.getName()).log(Level.SEVERE, null, ex); }
    }

    private void Class_Load() {
        try {
            pst = con.prepareStatement("SELECT * FROM CLASS ORDER BY CID");
            rs = pst.executeQuery(); d = (DefaultTableModel) jTable1.getModel(); d.setRowCount(0);
            while (rs.next()) {
                d.addRow(new Object[]{rs.getString("CID"), rs.getString("CLASSNAME"), rs.getString("SECTION")});
            }
        } catch (SQLException ex) { Logger.getLogger(classes.class.getName()).log(Level.SEVERE, null, ex); }
    }

    private void clear() {
        if(txtclass.getItemCount()>0) txtclass.setSelectedIndex(0);
        if(txtsection.getItemCount()>0) txtsection.setSelectedIndex(0);
        btnsave.setEnabled(true);
    }

    private void buildUI() {
        setTitle("Classes — EduManage"); setDefaultCloseOperation(DISPOSE_ON_CLOSE); setSize(800, 500); setLocationRelativeTo(null);
        JPanel root = new JPanel(new BorderLayout()); root.setBackground(UITheme.BG); setContentPane(root);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.SURFACE);
        header.setBorder(BorderFactory.createCompoundBorder(new MatteBorder(0,0,1,0,UITheme.BORDER),new EmptyBorder(14,24,14,24)));
        header.setPreferredSize(new Dimension(0,60));
        header.add(UITheme.label("Class Management",18f,true),BorderLayout.WEST);
        root.add(header,BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(20,0)); body.setBackground(UITheme.BG); body.setBorder(new EmptyBorder(20,20,20,20));

        JPanel form = new JPanel(); form.setBackground(UITheme.CARD); form.setLayout(new BoxLayout(form,BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createCompoundBorder(new LineBorder(UITheme.BORDER,1,true),new EmptyBorder(20,20,20,20)));
        form.setPreferredSize(new Dimension(280,0));

        JLabel st=UITheme.label("Manage Classes",12f,true); st.setForeground(UITheme.ACCENT); st.setAlignmentX(LEFT_ALIGNMENT); form.add(st); form.add(Box.createVerticalStrut(12));

        JLabel lc=UITheme.mutedLabel("Class Name"); lc.setAlignmentX(LEFT_ALIGNMENT); form.add(lc); form.add(Box.createVerticalStrut(3));
        txtclass = UITheme.comboBox(new String[]{"Grade 1","Grade 2","Grade 3","Grade 4","Grade 5","Grade 6","Grade 7","Grade 8","Grade 9","Grade 10","Grade 11","Grade 12","Grade 13"});
        txtclass.setMaximumSize(new Dimension(Integer.MAX_VALUE,34)); txtclass.setAlignmentX(LEFT_ALIGNMENT);
        form.add(txtclass); form.add(Box.createVerticalStrut(10));

        JLabel ls=UITheme.mutedLabel("Section"); ls.setAlignmentX(LEFT_ALIGNMENT); form.add(ls); form.add(Box.createVerticalStrut(3));
        txtsection = UITheme.comboBox(new String[]{"A","B","C","D","E"});
        txtsection.setMaximumSize(new Dimension(Integer.MAX_VALUE,34)); txtsection.setAlignmentX(LEFT_ALIGNMENT);
        form.add(txtsection); form.add(Box.createVerticalStrut(20));

        btnsave  = UITheme.button("Save",   UITheme.ACCENT);
        JButton del = UITheme.button("Delete", UITheme.DANGER);
        JButton clr = UITheme.button("Clear",  UITheme.MUTED);
        for(JButton b:new JButton[]{btnsave,del,clr}){b.setAlignmentX(LEFT_ALIGNMENT);b.setMaximumSize(new Dimension(Integer.MAX_VALUE,36));form.add(b);form.add(Box.createVerticalStrut(8));}

        btnsave.addActionListener(e -> {
            try {
                pst = con.prepareStatement("INSERT INTO CLASS(CLASSNAME,SECTION) VALUES(?,?)");
                pst.setString(1, txtclass.getSelectedItem().toString());
                pst.setString(2, txtsection.getSelectedItem().toString());
                pst.executeUpdate(); JOptionPane.showMessageDialog(this, "Class added."); Class_Load(); clear();
            } catch (SQLException ex) { Logger.getLogger(classes.class.getName()).log(Level.SEVERE, null, ex); }
        });
        del.addActionListener(e -> {
            int row = jTable1.getSelectedRow(); if (row == -1) return;
            try {
                pst = con.prepareStatement("DELETE FROM CLASS WHERE CID=?");
                pst.setString(1, d.getValueAt(row, 0).toString());
                pst.executeUpdate(); JOptionPane.showMessageDialog(this, "Class deleted."); Class_Load(); clear();
            } catch (SQLException ex) { Logger.getLogger(classes.class.getName()).log(Level.SEVERE, null, ex); }
        });
        clr.addActionListener(e -> clear());

        // Table
        jTable1 = new JTable(new DefaultTableModel(new Object[][]{},new String[]{"ID","Class","Section"}){public boolean isCellEditable(int r,int c){return false;}});
        UITheme.styleTable(jTable1);
        jTable1.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e){ 
                int row = jTable1.getSelectedRow(); if(row==-1)return;
                txtclass.setSelectedItem(d.getValueAt(row,1).toString());
                txtsection.setSelectedItem(d.getValueAt(row,2).toString());
                btnsave.setEnabled(false);
            }
        });
        JScrollPane sp = UITheme.scrollPane(jTable1);

        body.add(form,BorderLayout.WEST); body.add(sp,BorderLayout.CENTER);
        root.add(body,BorderLayout.CENTER);
    }

    public static void main(String[] args){SwingUtilities.invokeLater(()->new classes().setVisible(true));}
}
