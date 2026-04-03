import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

/** Teacher management — dark themed CRUD form */
public class teacher extends JFrame {

    Connection con;
    PreparedStatement pst;
    ResultSet rs;
    DefaultTableModel d;

    private JTextField   txttname, txtphone, txtaddress;
    private JTable       teachertable;
    private JButton      btnsave, btnupdate;

    public teacher() {
        UITheme.applyGlobalDefaults();
        buildUI();
        Connect();
        Teacher_Load();
    }

    public void Connect() {
        try { Class.forName("org.h2.Driver"); con = DriverManager.getConnection(UITheme.dbUrl(), "sa", ""); }
        catch (Exception ex) { Logger.getLogger(teacher.class.getName()).log(Level.SEVERE, null, ex); }
    }

    public void Teacher_Load() {
        try {
            pst = con.prepareStatement("SELECT * FROM TEACHER ORDER BY TID");
            rs = pst.executeQuery(); d = (DefaultTableModel) teachertable.getModel(); d.setRowCount(0);
            while (rs.next()) {
                d.addRow(new Object[]{rs.getString("TID"), rs.getString("TNAME"), rs.getString("PHONE"), rs.getString("ADDRESS")});
            }
        } catch (SQLException ex) { Logger.getLogger(teacher.class.getName()).log(Level.SEVERE, null, ex); }
    }

    private void clearForm() {
        txttname.setText(""); txtphone.setText(""); txtaddress.setText("");
        btnsave.setEnabled(true); txttname.requestFocus();
    }

    private void buildUI() {
        setTitle("Teachers — EduManage"); setDefaultCloseOperation(DISPOSE_ON_CLOSE); setSize(1000, 550); setLocationRelativeTo(null);
        JPanel root = new JPanel(new BorderLayout()); root.setBackground(UITheme.BG); setContentPane(root);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.SURFACE);
        header.setBorder(BorderFactory.createCompoundBorder(new MatteBorder(0,0,1,0,UITheme.BORDER),new EmptyBorder(14,24,14,24)));
        header.setPreferredSize(new Dimension(0,64));
        header.add(UITheme.label("Teacher Management",20f,true),BorderLayout.WEST);
        root.add(header,BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(20,0)); body.setBackground(UITheme.BG); body.setBorder(new EmptyBorder(24,24,24,24));

        JPanel form = new JPanel(); form.setBackground(UITheme.CARD); form.setLayout(new BoxLayout(form,BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createCompoundBorder(new LineBorder(UITheme.BORDER,1,true),new EmptyBorder(24,24,24,24)));
        form.setPreferredSize(new Dimension(300,0));

        JLabel st=UITheme.label("Teacher Registration",14f,true); st.setForeground(UITheme.ACCENT); st.setAlignmentX(LEFT_ALIGNMENT); form.add(st); form.add(Box.createVerticalStrut(16));

        txttname   = addF(form,"Teacher Name");
        txtphone   = addF(form,"Phone Number");
        txtaddress = addF(form,"Address");
        form.add(Box.createVerticalStrut(14));

        btnsave   = UITheme.button("Save Record", UITheme.ACCENT);
        btnupdate = UITheme.button("Update Info", UITheme.WARNING);
        JButton del = UITheme.button("Delete Teacher", UITheme.DANGER);
        JButton clr = UITheme.button("Clear Form",    UITheme.MUTED);

        for(JButton b:new JButton[]{btnsave,btnupdate,del,clr}){b.setAlignmentX(LEFT_ALIGNMENT);b.setMaximumSize(new Dimension(Integer.MAX_VALUE,38));form.add(b);form.add(Box.createVerticalStrut(10));}

        btnsave.addActionListener(e -> {
            try {
                pst = con.prepareStatement("INSERT INTO TEACHER(TNAME,PHONE,ADDRESS) VALUES(?,?,?)");
                pst.setString(1,txttname.getText()); pst.setString(2,txtphone.getText());
                pst.setString(3,txtaddress.getText());
                pst.executeUpdate(); JOptionPane.showMessageDialog(this, "Teacher added."); Teacher_Load(); clearForm();
            }catch(SQLException ex){Logger.getLogger(teacher.class.getName()).log(Level.SEVERE,null,ex);}
        });
        btnupdate.addActionListener(e -> {
            int row = teachertable.getSelectedRow(); if(row==-1)return;
            try {
                pst = con.prepareStatement("UPDATE TEACHER SET TNAME=?,PHONE=?,ADDRESS=? WHERE TID=?");
                pst.setString(1,txttname.getText()); pst.setString(2,txtphone.getText());
                pst.setString(3,txtaddress.getText()); pst.setString(4,d.getValueAt(row,0).toString());
                pst.executeUpdate(); JOptionPane.showMessageDialog(this, "Record updated."); Teacher_Load(); clearForm();
            }catch(SQLException ex){Logger.getLogger(teacher.class.getName()).log(Level.SEVERE,null,ex);}
        });
        del.addActionListener(e -> {
            int row = teachertable.getSelectedRow(); if(row==-1)return;
            try {
                pst = con.prepareStatement("DELETE FROM TEACHER WHERE TID=?");
                pst.setString(1, d.getValueAt(row,0).toString());
                pst.executeUpdate(); JOptionPane.showMessageDialog(this, "Record deleted."); Teacher_Load(); clearForm();
            }catch(SQLException ex){Logger.getLogger(teacher.class.getName()).log(Level.SEVERE,null,ex);}
        });
        clr.addActionListener(e -> clearForm());

        // Table
        teachertable = new JTable(new DefaultTableModel(new Object[][]{},new String[]{"ID","Name","Phone","Address"}){public boolean isCellEditable(int r,int c){return false;}});
        UITheme.styleTable(teachertable);
        teachertable.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e){ 
                int row = teachertable.getSelectedRow(); if(row==-1)return;
                txttname.setText(d.getValueAt(row,1).toString());
                txtphone.setText(d.getValueAt(row,2).toString()); txtaddress.setText(d.getValueAt(row,3).toString());
                btnsave.setEnabled(false);
            }
        });
        JScrollPane sp = UITheme.scrollPane(teachertable);

        body.add(form,BorderLayout.WEST); body.add(sp,BorderLayout.CENTER);
        root.add(body,BorderLayout.CENTER);
    }

    private JTextField addF(JPanel p,String label){JLabel l=UITheme.mutedLabel(label);l.setAlignmentX(LEFT_ALIGNMENT);JTextField f=UITheme.textField("");f.setMaximumSize(new Dimension(Integer.MAX_VALUE,34));f.setAlignmentX(LEFT_ALIGNMENT);p.add(l);p.add(Box.createVerticalStrut(4));p.add(f);p.add(Box.createVerticalStrut(10));return f;}

    public static void main(String[] args){SwingUtilities.invokeLater(()->new teacher().setVisible(true));}
}
