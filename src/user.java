import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

/** User account management — dark themed CRUD form */
public class user extends JFrame {

    Connection con;
    PreparedStatement pst;
    ResultSet rs;
    DefaultTableModel d;

    private JTextField     txtName, txtPhone, txtAddress, txtUname;
    private JPasswordField txtPwd;
    private JComboBox<String> txtUtype;
    private JTable   jTable1;
    private JButton  save, edit;

    public user() {
        UITheme.applyGlobalDefaults();
        buildUI();
        Connect();
        User_Load();
    }

    public void Connect() {
        try { Class.forName("org.h2.Driver"); con = DriverManager.getConnection(UITheme.dbUrl(), "sa", ""); }
        catch (Exception ex) { Logger.getLogger(user.class.getName()).log(Level.SEVERE, null, ex); }
    }

    public void User_Load() {
        try {
            pst = con.prepareStatement("SELECT * FROM USERS ORDER BY ID");
            rs = pst.executeQuery(); d = (DefaultTableModel) jTable1.getModel(); d.setRowCount(0);
            while (rs.next()) {
                d.addRow(new Object[]{rs.getString("ID"), rs.getString("NAME"), rs.getString("PHONE"), rs.getString("ADDRESS"), rs.getString("UNAME"), rs.getString("UTYPE")});
            }
        } catch (SQLException ex) { Logger.getLogger(user.class.getName()).log(Level.SEVERE, null, ex); }
    }

    private void buildUI() {
        setTitle("Users - EduManage"); setDefaultCloseOperation(DISPOSE_ON_CLOSE); setSize(1100, 600); setLocationRelativeTo(null);
        JPanel root = new JPanel(new BorderLayout()); root.setBackground(UITheme.BG); setContentPane(root);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.SURFACE);
        header.setBorder(BorderFactory.createCompoundBorder(new MatteBorder(0,0,1,0,UITheme.BORDER),new EmptyBorder(14,24,14,24)));
        header.setPreferredSize(new Dimension(0,60));
        header.add(UITheme.label("User Management",18f,true),BorderLayout.WEST);
        root.add(header,BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(20,0)); body.setBackground(UITheme.BG); body.setBorder(new EmptyBorder(20,20,20,20));

        JPanel form = new JPanel(); form.setBackground(UITheme.CARD); form.setLayout(new BoxLayout(form,BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createCompoundBorder(new LineBorder(UITheme.BORDER,1,true),new EmptyBorder(20,20,20,20)));
        form.setPreferredSize(new Dimension(280,0));

        JLabel st=UITheme.label("User Details",12f,true); st.setForeground(UITheme.ACCENT); st.setAlignmentX(LEFT_ALIGNMENT); form.add(st); form.add(Box.createVerticalStrut(12));

        txtName    = addF(form,"Full Name");
        txtPhone   = addF(form,"Phone Number");
        txtAddress = addF(form,"Address");
        txtUname   = addF(form,"Username");

        JLabel lp=UITheme.mutedLabel("Password"); lp.setAlignmentX(LEFT_ALIGNMENT); form.add(lp); form.add(Box.createVerticalStrut(3));
        txtPwd = UITheme.passwordField(); txtPwd.setMaximumSize(new Dimension(Integer.MAX_VALUE,34)); txtPwd.setAlignmentX(LEFT_ALIGNMENT);
        form.add(txtPwd); form.add(Box.createVerticalStrut(10));

        JLabel lt=UITheme.mutedLabel("User Type"); lt.setAlignmentX(LEFT_ALIGNMENT); form.add(lt); form.add(Box.createVerticalStrut(3));
        txtUtype=UITheme.comboBox(new String[]{"Admin","Teacher"}); txtUtype.setMaximumSize(new Dimension(Integer.MAX_VALUE,34)); txtUtype.setAlignmentX(LEFT_ALIGNMENT);
        form.add(txtUtype); form.add(Box.createVerticalStrut(20));

        save     = UITheme.button("Save",   UITheme.ACCENT);
        edit     = UITheme.button("Edit",    UITheme.WARNING);
        JButton del = UITheme.button("Delete",  UITheme.DANGER);
        JButton clr = UITheme.button("Clear",   UITheme.MUTED);
        for(JButton b:new JButton[]{save,edit,del,clr}){b.setAlignmentX(LEFT_ALIGNMENT);b.setMaximumSize(new Dimension(Integer.MAX_VALUE,36));form.add(b);form.add(Box.createVerticalStrut(8));}

        save.addActionListener(e -> saveActionPerformed());
        edit.addActionListener(e -> editActionPerformed());
        del.addActionListener(e -> deleteUser());
        clr.addActionListener(e -> clearForm());

        // Table
        jTable1 = new JTable(new DefaultTableModel(new Object[][]{},new String[]{"ID","Name","Phone","Address","Username","User Type"}){public boolean isCellEditable(int r,int c){return false;}});
        UITheme.styleTable(jTable1);
        jTable1.addMouseListener(new MouseAdapter(){public void mouseClicked(MouseEvent e){loadRow();}});
        JScrollPane sp = UITheme.scrollPane(jTable1);

        body.add(form,BorderLayout.WEST); body.add(sp,BorderLayout.CENTER);
        root.add(body,BorderLayout.CENTER);
    }

    private void loadRow() {
        int row=jTable1.getSelectedRow(); if(row==-1)return;
        txtName.setText(safeStr(d.getValueAt(row,1))); txtPhone.setText(safeStr(d.getValueAt(row,2)));
        txtAddress.setText(safeStr(d.getValueAt(row,3))); txtUname.setText(safeStr(d.getValueAt(row,4)));
        txtUtype.setSelectedItem(safeStr(d.getValueAt(row,5)));
        save.setEnabled(false);
    }

    private void saveActionPerformed() {
        try {
            pst = con.prepareStatement("INSERT INTO USERS(NAME,PHONE,ADDRESS,UNAME,PASSWORD,UTYPE) VALUES(?,?,?,?,?,?)");
            pst.setString(1,txtName.getText()); pst.setString(2,txtPhone.getText()); pst.setString(3,txtAddress.getText());
            pst.setString(4,txtUname.getText()); pst.setString(5,new String(txtPwd.getPassword()));
            pst.setString(6,txtUtype.getSelectedItem().toString()); pst.executeUpdate();
            JOptionPane.showMessageDialog(this,"User added."); clearForm(); User_Load();
        } catch(SQLException ex){Logger.getLogger(user.class.getName()).log(Level.SEVERE,null,ex);}
    }

    private void editActionPerformed() {
        int row=jTable1.getSelectedRow(); if(row==-1){JOptionPane.showMessageDialog(this,"Select a user.");return;}
        try {
            pst = con.prepareStatement("UPDATE USERS SET NAME=?,PHONE=?,ADDRESS=?,UNAME=?,PASSWORD=?,UTYPE=? WHERE ID=?");
            pst.setString(1,txtName.getText()); pst.setString(2,txtPhone.getText()); pst.setString(3,txtAddress.getText());
            pst.setString(4,txtUname.getText()); pst.setString(5,new String(txtPwd.getPassword()));
            pst.setString(6,txtUtype.getSelectedItem().toString()); pst.setInt(7,Integer.parseInt(d.getValueAt(row,0).toString()));
            pst.executeUpdate(); JOptionPane.showMessageDialog(this,"User updated."); clearForm(); User_Load(); save.setEnabled(true);
        } catch(SQLException ex){Logger.getLogger(user.class.getName()).log(Level.SEVERE,null,ex);}
    }

    private void deleteUser() {
        int row=jTable1.getSelectedRow(); if(row==-1){JOptionPane.showMessageDialog(this,"Select a user.");return;}
        try{
            pst=con.prepareStatement("DELETE FROM USERS WHERE ID=?"); pst.setInt(1,Integer.parseInt(d.getValueAt(row,0).toString()));
            pst.executeUpdate(); JOptionPane.showMessageDialog(this,"User deleted."); clearForm(); User_Load(); save.setEnabled(true);
        }catch(SQLException ex){Logger.getLogger(user.class.getName()).log(Level.SEVERE,null,ex);}
    }

    private void clearForm(){txtName.setText("");txtPhone.setText("");txtAddress.setText("");txtUname.setText("");txtPwd.setText("");if(txtUtype.getItemCount()>0)txtUtype.setSelectedIndex(0);save.setEnabled(true);txtName.requestFocus();}
    private JTextField addF(JPanel p,String label){JLabel l=UITheme.mutedLabel(label);l.setAlignmentX(LEFT_ALIGNMENT);JTextField f=UITheme.textField("");f.setMaximumSize(new Dimension(Integer.MAX_VALUE,34));f.setAlignmentX(LEFT_ALIGNMENT);p.add(l);p.add(Box.createVerticalStrut(3));p.add(f);p.add(Box.createVerticalStrut(10));return f;}
    private String safeStr(Object o){return o==null?"":o.toString();}

    public static void main(String[] args){SwingUtilities.invokeLater(()->new user().setVisible(true));}
}
