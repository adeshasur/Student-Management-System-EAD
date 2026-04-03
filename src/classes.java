import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

/** Class management — dark themed CRUD form */
public class classes extends JFrame {

    Connection con;
    PreparedStatement pst;
    ResultSet rs;
    DefaultTableModel d;

    private JComboBox<String> txtclass, txtsection;
    private JTable classtable;
    private JScrollPane jScrollPane1;
    private JButton btnsave, jButton2, btncreate, jButton3;

    public classes() {
        UITheme.applyGlobalDefaults();
        buildUI();
        Connect();
        Class_Load();
    }

    public void Connect() {
        try {
            Class.forName("org.h2.Driver");
            con = DriverManager.getConnection(UITheme.dbUrl(), "sa", "");
        } catch (Exception ex) { Logger.getLogger(classes.class.getName()).log(Level.SEVERE, null, ex); }
    }

    public void Class_Load() {
        try {
            pst = con.prepareStatement("SELECT * FROM CLASS ORDER BY CID");
            rs = pst.executeQuery();
            d = (DefaultTableModel) classtable.getModel();
            d.setRowCount(0);
            while (rs.next()) {
                d.addRow(new Object[]{rs.getString("CID"), rs.getString("CLASSNAME"), rs.getString("SECTION")});
            }
        } catch (SQLException ex) { Logger.getLogger(classes.class.getName()).log(Level.SEVERE, null, ex); }
    }

    private void buildUI() {
        setTitle("Classes — EduManage");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(900, 540);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG);
        setContentPane(root);

        // Header
        JPanel header = buildHeader("🏫  Class Management");
        jButton3 = UITheme.button("✕  Close", UITheme.MUTED);
        jButton3.addActionListener(e -> dispose());
        header.add(jButton3, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(20, 0));
        body.setBackground(UITheme.BG);
        body.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Form card
        JPanel form = new JPanel();
        form.setBackground(UITheme.CARD);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(UITheme.BORDER, 1, true), new EmptyBorder(20, 20, 20, 20)));
        form.setPreferredSize(new Dimension(260, 0));

        addSectionTitle(form, "Add New Class");

        JLabel lb1 = UITheme.mutedLabel("Class Name"); lb1.setAlignmentX(LEFT_ALIGNMENT); form.add(lb1); form.add(Box.createVerticalStrut(4));
        txtclass = UITheme.comboBox(new String[]{"Grade 1","Grade 2","Grade 3","Grade 4","Grade 5","Grade 6","Grade 7","Grade 8","Grade 9","Grade 10","Grade 11","Grade 12","Grade 13"});
        txtclass.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34)); txtclass.setAlignmentX(LEFT_ALIGNMENT);
        form.add(txtclass); form.add(Box.createVerticalStrut(12));

        JLabel lb2 = UITheme.mutedLabel("Section"); lb2.setAlignmentX(LEFT_ALIGNMENT); form.add(lb2); form.add(Box.createVerticalStrut(4));
        txtsection = UITheme.comboBox(new String[]{"A","B","C","D"});
        txtsection.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34)); txtsection.setAlignmentX(LEFT_ALIGNMENT);
        form.add(txtsection); form.add(Box.createVerticalStrut(24));

        btnsave  = UITheme.button("💾 Save",   UITheme.ACCENT);
        jButton2 = UITheme.button("🗑 Delete", UITheme.DANGER);
        btncreate = UITheme.button("✕ Clear",  UITheme.MUTED);

        for (JButton b : new JButton[]{btnsave, jButton2, btncreate}) {
            b.setAlignmentX(LEFT_ALIGNMENT);
            b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            form.add(b); form.add(Box.createVerticalStrut(8));
        }

        btnsave.addActionListener(e -> {
            try {
                pst = con.prepareStatement("INSERT INTO CLASS(CLASSNAME,SECTION) VALUES(?,?)");
                pst.setString(1, txtclass.getSelectedItem().toString());
                pst.setString(2, txtsection.getSelectedItem().toString());
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "✅ Class added.");
                Class_Load(); btnsave.setEnabled(true);
            } catch (SQLException ex) { Logger.getLogger(classes.class.getName()).log(Level.SEVERE, null, ex); }
        });
        jButton2.addActionListener(e -> {
            int row = classtable.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(this, "Select a class to delete."); return; }
            try {
                pst = con.prepareStatement("DELETE FROM CLASS WHERE CID=?");
                pst.setString(1, d.getValueAt(row, 0).toString());
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "🗑 Class deleted.");
                Class_Load(); btnsave.setEnabled(true);
            } catch (SQLException ex) { Logger.getLogger(classes.class.getName()).log(Level.SEVERE, null, ex); }
        });
        btncreate.addActionListener(e -> { if (txtclass.getItemCount()>0) txtclass.setSelectedIndex(0); if (txtsection.getItemCount()>0) txtsection.setSelectedIndex(0); btnsave.setEnabled(true); });

        // Table
        classtable = new JTable(new DefaultTableModel(new Object[][]{}, new String[]{"ID","Class Name","Section"}) {
            public boolean isCellEditable(int r, int c) { return false; }
        });
        UITheme.styleTable(classtable);
        classtable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = classtable.getSelectedRow();
                if (row == -1) return;
                txtclass.setSelectedItem(d.getValueAt(row, 1));
                txtsection.setSelectedItem(d.getValueAt(row, 2));
                btnsave.setEnabled(false);
            }
        });
        jScrollPane1 = UITheme.scrollPane(classtable);

        body.add(form, BorderLayout.WEST);
        body.add(jScrollPane1, BorderLayout.CENTER);
        root.add(body, BorderLayout.CENTER);
    }

    private JPanel buildHeader(String title) {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(UITheme.SURFACE);
        h.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0,0,1,0, UITheme.BORDER), new EmptyBorder(14,24,14,24)));
        h.setPreferredSize(new Dimension(0, 60));
        h.add(UITheme.label(title, 18f, true), BorderLayout.WEST);
        return h;
    }

    private void addSectionTitle(JPanel p, String t) {
        JLabel l = UITheme.label(t, 12f, true); l.setForeground(UITheme.ACCENT); l.setAlignmentX(LEFT_ALIGNMENT);
        p.add(l); p.add(Box.createVerticalStrut(12));
    }

    public static void main(String[] args) { SwingUtilities.invokeLater(() -> new classes().setVisible(true)); }
}
