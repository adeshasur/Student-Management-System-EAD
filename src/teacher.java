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
    int newid; String newutype;

    private JTextField txtname, txtqual, txtsal, txtphone, txtemail, txtaddress;
    private JTable   teachertable;
    private JScrollPane jScrollPane1;
    private JButton  btnsave, jButton3, edit, jButton2;

    public teacher() { this(0,""); }
    public teacher(int iid, String usertype) {
        UITheme.applyGlobalDefaults(); this.newid=iid; this.newutype=usertype;
        buildUI(); Connect(); Teacher_Load();
    }

    public void Connect() {
        try { Class.forName("org.h2.Driver"); con = DriverManager.getConnection(UITheme.dbUrl(), "sa", ""); }
        catch (Exception ex) { Logger.getLogger(teacher.class.getName()).log(Level.SEVERE, null, ex); }
    }

    private void Teacher_Load() {
        try {
            // Teachers are stored in USERS with UTYPE='Teacher'
            pst = con.prepareStatement("SELECT * FROM USERS WHERE UTYPE='Teacher' ORDER BY ID");
            rs = pst.executeQuery(); d = (DefaultTableModel) teachertable.getModel(); d.setRowCount(0);
            while (rs.next()) {
                d.addRow(new Object[]{rs.getString("ID"), rs.getString("NAME"), rs.getString("PHONE"), rs.getString("ADDRESS"), rs.getString("UNAME")});
            }
        } catch (SQLException ex) { Logger.getLogger(teacher.class.getName()).log(Level.SEVERE, null, ex); }
    }

    private void buildUI() {
        setTitle("Teachers — EduManage"); setDefaultCloseOperation(DISPOSE_ON_CLOSE); setSize(1050, 580); setLocationRelativeTo(null);
        JPanel root = new JPanel(new BorderLayout()); root.setBackground(UITheme.BG); setContentPane(root);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.SURFACE);
        header.setBorder(BorderFactory.createCompoundBorder(new MatteBorder(0,0,1,0,UITheme.BORDER),new EmptyBorder(14,24,14,24)));
        header.setPreferredSize(new Dimension(0,60));
        header.add(UITheme.label("👩‍🏫  Teacher Management",18f,true),BorderLayout.WEST);
        jButton2 = UITheme.button("✕  Close",UITheme.MUTED); jButton2.addActionListener(e->dispose());
        header.add(jButton2,BorderLayout.EAST); root.add(header,BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(20,0)); body.setBackground(UITheme.BG); body.setBorder(new EmptyBorder(20,20,20,20));

        JPanel form = new JPanel(); form.setBackground(UITheme.CARD); form.setLayout(new BoxLayout(form,BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createCompoundBorder(new LineBorder(UITheme.BORDER,1,true),new EmptyBorder(20,20,20,20)));
        form.setPreferredSize(new Dimension(270,0));

        JLabel st=UITheme.label("Teacher Details",12f,true); st.setForeground(UITheme.ACCENT); st.setAlignmentX(LEFT_ALIGNMENT); form.add(st); form.add(Box.createVerticalStrut(12));

        txtname    = addF(form,"Full Name");
        txtqual    = addF(form,"Qualification");
        txtsal     = addF(form,"Salary");
        txtphone   = addF(form,"Phone Number");
        txtemail   = addF(form,"Email");
        txtaddress = addF(form,"Address");
        form.add(Box.createVerticalStrut(16));

        btnsave  = UITheme.button("💾 Save",UITheme.ACCENT);
        jButton3 = UITheme.button("🗑 Delete",UITheme.DANGER);
        edit     = UITheme.button("✏ Edit",UITheme.WARNING);
        for(JButton b:new JButton[]{btnsave,jButton3,edit}){b.setAlignmentX(LEFT_ALIGNMENT);b.setMaximumSize(new Dimension(Integer.MAX_VALUE,36));form.add(b);form.add(Box.createVerticalStrut(8));}

        btnsave.addActionListener(e -> {
            try {
                // Save as user with Teacher type
                pst = con.prepareStatement("INSERT INTO USERS(NAME,PHONE,ADDRESS,UNAME,PASSWORD,UTYPE) VALUES(?,?,?,?,?,?)");
                pst.setString(1,txtname.getText()); pst.setString(2,txtphone.getText());
                pst.setString(3,txtaddress.getText());
                pst.setString(4,txtname.getText().split(" ")[0]); // username = first name
                pst.setString(5,"1234"); pst.setString(6,"Teacher");
                pst.executeUpdate(); JOptionPane.showMessageDialog(this,"✅ Teacher added."); Teacher_Load(); clearForm();
            } catch(SQLException ex){Logger.getLogger(teacher.class.getName()).log(Level.SEVERE,null,ex);}
        });
        jButton3.addActionListener(e -> {
            int row=teachertable.getSelectedRow(); if(row==-1){JOptionPane.showMessageDialog(this,"Select a teacher.");return;}
            try{pst=con.prepareStatement("DELETE FROM USERS WHERE ID=?"); pst.setString(1,d.getValueAt(row,0).toString()); pst.executeUpdate(); JOptionPane.showMessageDialog(this,"🗑 Teacher deleted."); Teacher_Load(); btnsave.setEnabled(true);}
            catch(SQLException ex){Logger.getLogger(teacher.class.getName()).log(Level.SEVERE,null,ex);}
        });
        edit.addActionListener(e -> {
            int row=teachertable.getSelectedRow(); if(row==-1){JOptionPane.showMessageDialog(this,"Select a teacher.");return;}
            try{
                pst=con.prepareStatement("UPDATE USERS SET NAME=?,PHONE=?,ADDRESS=? WHERE ID=?");
                pst.setString(1,txtname.getText()); pst.setString(2,txtphone.getText()); pst.setString(3,txtaddress.getText());
                pst.setString(4,d.getValueAt(row,0).toString()); pst.executeUpdate();
                JOptionPane.showMessageDialog(this,"✏ Teacher updated."); Teacher_Load(); btnsave.setEnabled(true);
            }catch(SQLException ex){Logger.getLogger(teacher.class.getName()).log(Level.SEVERE,null,ex);}
        });

        // Table
        teachertable = new JTable(new DefaultTableModel(new Object[][]{},new String[]{"ID","Name","Phone","Address","Username"}){public boolean isCellEditable(int r,int c){return false;}});
        UITheme.styleTable(teachertable);
        teachertable.addMouseListener(new MouseAdapter(){public void mouseClicked(MouseEvent e){
            int row=teachertable.getSelectedRow(); if(row==-1)return;
            txtname.setText(safeStr(d.getValueAt(row,1))); txtphone.setText(safeStr(d.getValueAt(row,2))); txtaddress.setText(safeStr(d.getValueAt(row,3)));
            btnsave.setEnabled(false);
        }});
        jScrollPane1 = UITheme.scrollPane(teachertable);

        body.add(form,BorderLayout.WEST); body.add(jScrollPane1,BorderLayout.CENTER);
        root.add(body,BorderLayout.CENTER);
    }

    private JTextField addF(JPanel p,String label){JLabel l=UITheme.mutedLabel(label);l.setAlignmentX(LEFT_ALIGNMENT);JTextField f=UITheme.textField("");f.setMaximumSize(new Dimension(Integer.MAX_VALUE,34));f.setAlignmentX(LEFT_ALIGNMENT);p.add(l);p.add(Box.createVerticalStrut(3));p.add(f);p.add(Box.createVerticalStrut(10));return f;}
    private void clearForm(){txtname.setText("");txtqual.setText("");txtsal.setText("");txtphone.setText("");txtemail.setText("");txtaddress.setText("");btnsave.setEnabled(true);}
    private String safeStr(Object o){return o==null?"":o.toString();}

    public static void main(String[] args){SwingUtilities.invokeLater(()->new teacher().setVisible(true));}
}
