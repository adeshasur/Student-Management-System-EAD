import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

/** Subject management — dark themed CRUD form */
public class subject extends JFrame {

    Connection con;
    PreparedStatement pst;
    ResultSet rs;
    DefaultTableModel d;

    private JTextField txtsubject;
    private JTable subjecttable;
    private JScrollPane jScrollPane1;
    private JButton btnsave, jButton3, jButton4, jButton1;

    public subject() {
        UITheme.applyGlobalDefaults();
        buildUI();
        Connect();
        Subject_Load();
    }

    public void Connect() {
        try {
            Class.forName("org.h2.Driver");
            con = DriverManager.getConnection(UITheme.dbUrl(), "sa", "");
        } catch (Exception ex) { Logger.getLogger(subject.class.getName()).log(Level.SEVERE, null, ex); }
    }

    public void Subject_Load() {
        try {
            pst = con.prepareStatement("SELECT * FROM SUBJECT ORDER BY SID");
            rs = pst.executeQuery();
            d = (DefaultTableModel) subjecttable.getModel();
            d.setRowCount(0);
            while (rs.next()) {
                d.addRow(new Object[]{rs.getString("SID"), rs.getString("SUBJECTNAME")});
            }
        } catch (SQLException ex) { Logger.getLogger(subject.class.getName()).log(Level.SEVERE, null, ex); }
    }

    private void buildUI() {
        setTitle("Subjects — EduManage");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(820, 500);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG);
        setContentPane(root);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.SURFACE);
        header.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0,0,1,0,UITheme.BORDER), new EmptyBorder(14,24,14,24)));
        header.setPreferredSize(new Dimension(0,60));
        header.add(UITheme.label("📚  Subject Management", 18f, true), BorderLayout.WEST);
        jButton1 = UITheme.button("✕  Close", UITheme.MUTED);
        jButton1.addActionListener(e -> dispose());
        header.add(jButton1, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(20, 0));
        body.setBackground(UITheme.BG);
        body.setBorder(new EmptyBorder(20,20,20,20));

        // Form card
        JPanel form = new JPanel();
        form.setBackground(UITheme.CARD);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(UITheme.BORDER, 1, true), new EmptyBorder(20,20,20,20)));
        form.setPreferredSize(new Dimension(240,0));

        JLabel sTitle = UITheme.label("Add Subject", 12f, true);
        sTitle.setForeground(UITheme.ACCENT); sTitle.setAlignmentX(LEFT_ALIGNMENT);
        form.add(sTitle); form.add(Box.createVerticalStrut(14));

        JLabel lb = UITheme.mutedLabel("Subject Name"); lb.setAlignmentX(LEFT_ALIGNMENT);
        form.add(lb); form.add(Box.createVerticalStrut(4));
        txtsubject = UITheme.textField("e.g. Mathematics");
        txtsubject.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34)); txtsubject.setAlignmentX(LEFT_ALIGNMENT);
        form.add(txtsubject); form.add(Box.createVerticalStrut(20));

        btnsave  = UITheme.button("💾 Save",   UITheme.ACCENT);
        jButton3 = UITheme.button("🗑 Delete",  UITheme.DANGER);
        jButton4 = UITheme.button("✕ Clear",   UITheme.MUTED);

        for (JButton b : new JButton[]{btnsave, jButton3, jButton4}) {
            b.setAlignmentX(LEFT_ALIGNMENT);
            b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            form.add(b); form.add(Box.createVerticalStrut(8));
        }

        btnsave.addActionListener(e -> {
            String sub = txtsubject.getText().trim();
            if (sub.isEmpty()) { JOptionPane.showMessageDialog(this, "Enter subject name."); return; }
            try {
                pst = con.prepareStatement("INSERT INTO SUBJECT(SUBJECTNAME) VALUES(?)");
                pst.setString(1, sub); pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "✅ Subject added.");
                txtsubject.setText(""); Subject_Load(); txtsubject.requestFocus();
            } catch (SQLException ex) { Logger.getLogger(subject.class.getName()).log(Level.SEVERE, null, ex); }
        });
        jButton3.addActionListener(e -> {
            int row = subjecttable.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(this, "Select a subject to delete."); return; }
            try {
                pst = con.prepareStatement("DELETE FROM SUBJECT WHERE SID=?");
                pst.setString(1, d.getValueAt(row, 0).toString()); pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "🗑 Subject deleted.");
                txtsubject.setText(""); Subject_Load(); btnsave.setEnabled(true);
            } catch (SQLException ex) { Logger.getLogger(subject.class.getName()).log(Level.SEVERE, null, ex); }
        });
        jButton4.addActionListener(e -> { txtsubject.setText(""); btnsave.setEnabled(true); txtsubject.requestFocus(); });

        // Table
        subjecttable = new JTable(new DefaultTableModel(new Object[][]{}, new String[]{"ID","Subject Name"}) {
            public boolean isCellEditable(int r, int c) { return false; }
        });
        UITheme.styleTable(subjecttable);
        subjecttable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = subjecttable.getSelectedRow();
                if (row == -1) return;
                txtsubject.setText(d.getValueAt(row, 1).toString());
                btnsave.setEnabled(false);
            }
        });
        jScrollPane1 = UITheme.scrollPane(subjecttable);

        body.add(form, BorderLayout.WEST);
        body.add(jScrollPane1, BorderLayout.CENTER);
        root.add(body, BorderLayout.CENTER);
    }

    public static void main(String[] args) { SwingUtilities.invokeLater(() -> new subject().setVisible(true)); }
}
