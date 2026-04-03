import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

/**
 * Student management — Add / Edit / Delete students with dark themed UI.
 */
public class student extends JFrame {

    Connection con;
    PreparedStatement pst;
    ResultSet rs;
    DefaultTableModel d;

    // Form fields
    private JTextField   txtstname, txtpname, txtphone, txtaddress;
    private JTextField   txtdob_str;   // plain text DOB (yyyy-MM-dd)
    private JComboBox<String> txtgender, txtclass, txtsection;
    private JTable       studenttable;
    private JScrollPane  jScrollPane1;
    private JButton      btnsave, jButton3 /*delete*/, edit, jButton4 /*clear*/, jButton1 /*close*/;
    private JTextField   searchField;

    public student() {
        UITheme.applyGlobalDefaults();
        buildUI();
        Connect();
        Load_Class();
        Load_Section();
        Student_Load();
    }

    // ─── DB ─────────────────────────────────────────────────────────────
    public void Connect() {
        try {
            Class.forName("org.h2.Driver");
            con = DriverManager.getConnection(UITheme.dbUrl(), "sa", "");
        } catch (Exception ex) { Logger.getLogger(student.class.getName()).log(Level.SEVERE, null, ex); }
    }

    public void Load_Class() {
        try {
            pst = con.prepareStatement("SELECT DISTINCT CLASSNAME FROM CLASS ORDER BY CLASSNAME");
            rs = pst.executeQuery();
            txtclass.removeAllItems();
            while (rs.next()) txtclass.addItem(rs.getString("CLASSNAME"));
        } catch (SQLException ex) { Logger.getLogger(student.class.getName()).log(Level.SEVERE, null, ex); }
    }

    public void Load_Section() {
        try {
            pst = con.prepareStatement("SELECT DISTINCT SECTION FROM CLASS ORDER BY SECTION");
            rs = pst.executeQuery();
            txtsection.removeAllItems();
            while (rs.next()) txtsection.addItem(rs.getString("SECTION"));
        } catch (SQLException ex) { Logger.getLogger(student.class.getName()).log(Level.SEVERE, null, ex); }
    }

    public void Student_Load() {
        try {
            pst = con.prepareStatement("SELECT * FROM STUDENT ORDER BY STUDENTID");
            rs = pst.executeQuery();
            d = (DefaultTableModel) studenttable.getModel();
            d.setRowCount(0);
            while (rs.next()) {
                d.addRow(new Object[]{
                    rs.getString("STUDENTID"), rs.getString("STNAME"), rs.getString("PNAME"),
                    rs.getString("DOB"), rs.getString("GENDER"), rs.getString("PHONE"),
                    rs.getString("ADDRESS"), rs.getString("CLASS"), rs.getString("SECTION")
                });
            }
        } catch (SQLException ex) { Logger.getLogger(student.class.getName()).log(Level.SEVERE, null, ex); }
    }

    // ─── Build UI ────────────────────────────────────────────────────────
    private void buildUI() {
        setTitle("Students — EduManage");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1100, 680);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 580));

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG);
        setContentPane(root);

        // ── Header ──
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.SURFACE);
        header.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 1, 0, UITheme.BORDER),
            new EmptyBorder(14, 24, 14, 24)));
        header.setPreferredSize(new Dimension(0, 60));

        JLabel title = UITheme.label("🎓  Student Registration", 18f, true);
        jButton1 = UITheme.button("✕  Close", UITheme.MUTED);
        jButton1.addActionListener(e -> dispose());

        header.add(title, BorderLayout.WEST);
        header.add(jButton1, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        // ── Body ──
        JPanel body = new JPanel(new BorderLayout(20, 0));
        body.setBackground(UITheme.BG);
        body.setBorder(new EmptyBorder(20, 20, 20, 20));

        body.add(buildFormPanel(), BorderLayout.WEST);
        body.add(buildTablePanel(), BorderLayout.CENTER);

        root.add(body, BorderLayout.CENTER);
    }

    private JPanel buildFormPanel() {
        JPanel wrap = new JPanel();
        wrap.setBackground(UITheme.CARD);
        wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));
        wrap.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(UITheme.BORDER, 1, true),
            new EmptyBorder(20, 20, 20, 20)));
        wrap.setPreferredSize(new Dimension(290, 0));

        // Section: Student Info
        addSectionTitle(wrap, "Student Information");
        txtstname = addLabeledField(wrap, "Full Name");
        txtpname  = addLabeledField(wrap, "Parent Name");
        txtdob_str = addLabeledField(wrap, "Date of Birth (yyyy-MM-dd)");
        wrap.add(Box.createVerticalStrut(10));

        addSectionTitle(wrap, "Academic Details");
        txtgender  = addLabeledCombo(wrap, "Gender", new String[]{"Male", "Female"});
        txtphone   = addLabeledField(wrap, "Phone Number");
        txtaddress = addLabeledField(wrap, "Address");
        txtclass   = addLabeledCombo(wrap, "Class", new String[]{});
        txtsection = addLabeledCombo(wrap, "Section", new String[]{});
        wrap.add(Box.createVerticalStrut(20));

        // Buttons
        btnsave  = UITheme.button("💾 Save",   UITheme.ACCENT);
        jButton3 = UITheme.button("🗑 Delete",  UITheme.DANGER);
        edit     = UITheme.button("✏ Edit",     UITheme.WARNING);
        jButton4 = UITheme.button("✕ Clear",    UITheme.MUTED);

        for (JButton b : new JButton[]{btnsave, jButton3, edit, jButton4}) {
            b.setAlignmentX(Component.LEFT_ALIGNMENT);
            b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            wrap.add(b);
            wrap.add(Box.createVerticalStrut(8));
        }

        btnsave.addActionListener(e  -> saveStudent());
        jButton3.addActionListener(e -> deleteStudent());
        edit.addActionListener(e     -> editStudent());
        jButton4.addActionListener(e -> clearForm());

        return wrap;
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(UITheme.BG);

        // Search bar
        searchField = UITheme.textField("🔍  Search students...");
        searchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { filterTable(searchField.getText()); }
        });

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(UITheme.BG);
        top.add(searchField, BorderLayout.CENTER);
        panel.add(top, BorderLayout.NORTH);

        // Table
        studenttable = new JTable(new DefaultTableModel(
            new Object[][]{},
            new String[]{"ID", "Name", "Parent", "DOB", "Gender", "Phone", "Address", "Class", "Section"}
        ) {
            public boolean isCellEditable(int r, int c) { return false; }
        });
        UITheme.styleTable(studenttable);
        studenttable.getColumnModel().getColumn(0).setPreferredWidth(40);
        studenttable.getColumnModel().getColumn(1).setPreferredWidth(130);
        studenttable.getColumnModel().getColumn(2).setPreferredWidth(120);

        studenttable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { loadRowToForm(); }
        });

        jScrollPane1 = UITheme.scrollPane(studenttable);
        panel.add(jScrollPane1, BorderLayout.CENTER);
        return panel;
    }

    private void filterTable(String query) {
        DefaultTableModel model = (DefaultTableModel) studenttable.getModel();
        if (query.isBlank()) { Student_Load(); return; }
        try {
            pst = con.prepareStatement(
                "SELECT * FROM STUDENT WHERE STNAME LIKE ? OR PNAME LIKE ? OR CLASS LIKE ?");
            String q = "%" + query + "%";
            pst.setString(1, q); pst.setString(2, q); pst.setString(3, q);
            rs = pst.executeQuery();
            model.setRowCount(0);
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("STUDENTID"), rs.getString("STNAME"), rs.getString("PNAME"),
                    rs.getString("DOB"), rs.getString("GENDER"), rs.getString("PHONE"),
                    rs.getString("ADDRESS"), rs.getString("CLASS"), rs.getString("SECTION")
                });
            }
        } catch (SQLException ex) { Logger.getLogger(student.class.getName()).log(Level.SEVERE, null, ex); }
    }

    // ─── CRUD ────────────────────────────────────────────────────────────
    private void saveStudent() {
        try {
            pst = con.prepareStatement(
                "INSERT INTO STUDENT(STNAME,PNAME,DOB,GENDER,PHONE,ADDRESS,CLASS,SECTION) VALUES(?,?,?,?,?,?,?,?)");
            pst.setString(1, txtstname.getText());
            pst.setString(2, txtpname.getText());
            pst.setString(3, txtdob_str.getText());
            pst.setString(4, txtgender.getSelectedItem().toString());
            pst.setString(5, txtphone.getText());
            pst.setString(6, txtaddress.getText());
            pst.setString(7, txtclass.getSelectedItem() != null ? txtclass.getSelectedItem().toString() : "");
            pst.setString(8, txtsection.getSelectedItem() != null ? txtsection.getSelectedItem().toString() : "");
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "✅  Student added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm(); Student_Load();
        } catch (SQLException ex) {
            Logger.getLogger(student.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteStudent() {
        int row = studenttable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a student to delete."); return; }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this student?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            String id = d.getValueAt(row, 0).toString();
            pst = con.prepareStatement("DELETE FROM STUDENT WHERE STUDENTID = ?");
            pst.setString(1, id);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "🗑  Student deleted.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
            clearForm(); Student_Load(); btnsave.setEnabled(true);
        } catch (SQLException ex) { Logger.getLogger(student.class.getName()).log(Level.SEVERE, null, ex); }
    }

    private void editStudent() {
        int row = studenttable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a student to edit."); return; }
        String id = d.getValueAt(row, 0).toString();
        try {
            pst = con.prepareStatement(
                "UPDATE STUDENT SET STNAME=?,PNAME=?,DOB=?,GENDER=?,PHONE=?,ADDRESS=?,CLASS=?,SECTION=? WHERE STUDENTID=?");
            pst.setString(1, txtstname.getText());
            pst.setString(2, txtpname.getText());
            pst.setString(3, txtdob_str.getText());
            pst.setString(4, txtgender.getSelectedItem().toString());
            pst.setString(5, txtphone.getText());
            pst.setString(6, txtaddress.getText());
            pst.setString(7, txtclass.getSelectedItem() != null ? txtclass.getSelectedItem().toString() : "");
            pst.setString(8, txtsection.getSelectedItem() != null ? txtsection.getSelectedItem().toString() : "");
            pst.setString(9, id);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "✏  Student updated.", "Updated", JOptionPane.INFORMATION_MESSAGE);
            clearForm(); Student_Load(); btnsave.setEnabled(true);
        } catch (SQLException ex) {
            Logger.getLogger(student.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadRowToForm() {
        int row = studenttable.getSelectedRow();
        if (row == -1) return;
        d = (DefaultTableModel) studenttable.getModel();
        txtstname.setText(safeStr(d.getValueAt(row, 1)));
        txtpname.setText(safeStr(d.getValueAt(row, 2)));
        txtdob_str.setText(safeStr(d.getValueAt(row, 3)));
        txtgender.setSelectedItem(safeStr(d.getValueAt(row, 4)));
        txtphone.setText(safeStr(d.getValueAt(row, 5)));
        txtaddress.setText(safeStr(d.getValueAt(row, 6)));
        txtclass.setSelectedItem(safeStr(d.getValueAt(row, 7)));
        txtsection.setSelectedItem(safeStr(d.getValueAt(row, 8)));
        btnsave.setEnabled(false);
    }

    private void clearForm() {
        txtstname.setText(""); txtpname.setText(""); txtdob_str.setText("");
        txtphone.setText(""); txtaddress.setText("");
        if (txtgender.getItemCount() > 0) txtgender.setSelectedIndex(0);
        if (txtclass.getItemCount() > 0) txtclass.setSelectedIndex(0);
        if (txtsection.getItemCount() > 0) txtsection.setSelectedIndex(0);
        btnsave.setEnabled(true); txtstname.requestFocus();
    }

    // ─── Helpers ─────────────────────────────────────────────────────────
    private JTextField addLabeledField(JPanel p, String label) {
        JLabel lbl = UITheme.mutedLabel(label);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        JTextField f = UITheme.textField("");
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(lbl); p.add(Box.createVerticalStrut(3)); p.add(f); p.add(Box.createVerticalStrut(10));
        return f;
    }

    private JComboBox<String> addLabeledCombo(JPanel p, String label, String[] items) {
        JLabel lbl = UITheme.mutedLabel(label);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        JComboBox<String> c = UITheme.comboBox(items);
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(lbl); p.add(Box.createVerticalStrut(3)); p.add(c); p.add(Box.createVerticalStrut(10));
        return c;
    }

    private void addSectionTitle(JPanel p, String title) {
        JLabel l = UITheme.label(title, 12f, true);
        l.setForeground(UITheme.ACCENT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(l); p.add(Box.createVerticalStrut(8));
    }

    private String safeStr(Object o) { return o == null ? "" : o.toString(); }

    public static void main(String[] args) { SwingUtilities.invokeLater(() -> new student().setVisible(true)); }
}
