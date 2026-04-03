import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * Login screen — modern split-panel dark theme.
 * Left: school branding. Right: login form.
 */
public class login extends JFrame {

    Connection con;
    PreparedStatement pst;
    ResultSet rs;

    // UI fields (kept by name for business logic compatibility)
    private JTextField       txtuname;
    private JPasswordField   txtpass;
    private JComboBox<String> txtutype;
    private JButton          jButton1;   // Login button

    public login() {
        UITheme.applyGlobalDefaults();
        buildUI();
        Connect();
        setVisible(true);
    }

    // ─── Database ────────────────────────────────────────────────────────
    public void Connect() {
        try {
            Class.forName("org.h2.Driver");
            String dbPath = System.getProperty("db.path", "./data/schoolmanagment");
            System.out.println("Connecting to database at: " + dbPath);
            con = DriverManager.getConnection(
                "jdbc:h2:" + dbPath + ";MODE=MySQL;AUTO_SERVER=TRUE", "sa", "");
            System.out.println("Database connected successfully.");

            Statement stmt = con.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS USERS (ID INT PRIMARY KEY AUTO_INCREMENT, NAME VARCHAR(255), PHONE INT, ADDRESS VARCHAR(255), UNAME VARCHAR(255), PASSWORD VARCHAR(255), UTYPE VARCHAR(255))");
            stmt.execute("CREATE TABLE IF NOT EXISTS CLASS (CID INT PRIMARY KEY AUTO_INCREMENT, CLASSNAME VARCHAR(255), SECTION VARCHAR(255))");
            stmt.execute("CREATE TABLE IF NOT EXISTS SUBJECT (SID INT PRIMARY KEY AUTO_INCREMENT, SUBJECTNAME VARCHAR(255))");
            stmt.execute("CREATE TABLE IF NOT EXISTS STUDENT (STUDENTID INT PRIMARY KEY AUTO_INCREMENT, STNAME VARCHAR(255), PNAME VARCHAR(255), DOB DATE, GENDER VARCHAR(255), PHONE VARCHAR(255), ADDRESS VARCHAR(255), CLASS VARCHAR(255), SECTION VARCHAR(255))");
            stmt.execute("CREATE TABLE IF NOT EXISTS EXAM (EXAMID INT PRIMARY KEY AUTO_INCREMENT, EXAMNAME VARCHAR(255), DATE DATE, CLASS VARCHAR(255), SECTION VARCHAR(255), SUBJECT VARCHAR(255))");
            stmt.execute("CREATE TABLE IF NOT EXISTS MARKS (MARKID INT PRIMARY KEY AUTO_INCREMENT, STID INT, STNAME VARCHAR(255), CLASS VARCHAR(255), SUBJECT VARCHAR(255), MARKS INT)");

            ResultSet rsCheck = stmt.executeQuery("SELECT COUNT(*) FROM USERS");
            if (rsCheck.next() && rsCheck.getInt(1) == 0) {
                stmt.execute("INSERT INTO USERS (NAME,PHONE,ADDRESS,UNAME,PASSWORD,UTYPE) VALUES ('Amal Silva',715689652,'56/8A Yakkala, Gampaha','admin','admin','Admin')");
                stmt.execute("INSERT INTO USERS (NAME,PHONE,ADDRESS,UNAME,PASSWORD,UTYPE) VALUES ('Kasuni Perera',771234567,'12/B Kandy Road, Colombo','Kasuni','1234','Teacher')");
                stmt.execute("INSERT INTO USERS (NAME,PHONE,ADDRESS,UNAME,PASSWORD,UTYPE) VALUES ('Nimal Fernando',762345678,'34/C Galle Road, Matara','Nimal','1234','Teacher')");
                stmt.execute("INSERT INTO USERS (NAME,PHONE,ADDRESS,UNAME,PASSWORD,UTYPE) VALUES ('Sanduni Jayawardena',753456789,'78/D Negombo Road, Ja-Ela','Sanduni','1234','Teacher')");
                stmt.execute("INSERT INTO CLASS (CLASSNAME,SECTION) VALUES ('Grade 6','A')");
                stmt.execute("INSERT INTO CLASS (CLASSNAME,SECTION) VALUES ('Grade 6','B')");
                stmt.execute("INSERT INTO CLASS (CLASSNAME,SECTION) VALUES ('Grade 7','A')");
                stmt.execute("INSERT INTO CLASS (CLASSNAME,SECTION) VALUES ('Grade 7','B')");
                stmt.execute("INSERT INTO CLASS (CLASSNAME,SECTION) VALUES ('Grade 8','A')");
                stmt.execute("INSERT INTO CLASS (CLASSNAME,SECTION) VALUES ('Grade 8','B')");
                stmt.execute("INSERT INTO CLASS (CLASSNAME,SECTION) VALUES ('Grade 9','A')");
                stmt.execute("INSERT INTO CLASS (CLASSNAME,SECTION) VALUES ('Grade 9','B')");
                stmt.execute("INSERT INTO CLASS (CLASSNAME,SECTION) VALUES ('Grade 10','A')");
                stmt.execute("INSERT INTO CLASS (CLASSNAME,SECTION) VALUES ('Grade 10','B')");
                stmt.execute("INSERT INTO SUBJECT (SUBJECTNAME) VALUES ('Mathematics')");
                stmt.execute("INSERT INTO SUBJECT (SUBJECTNAME) VALUES ('Science')");
                stmt.execute("INSERT INTO SUBJECT (SUBJECTNAME) VALUES ('English')");
                stmt.execute("INSERT INTO SUBJECT (SUBJECTNAME) VALUES ('Sinhala')");
                stmt.execute("INSERT INTO SUBJECT (SUBJECTNAME) VALUES ('History')");
                stmt.execute("INSERT INTO SUBJECT (SUBJECTNAME) VALUES ('ICT')");
                stmt.execute("INSERT INTO SUBJECT (SUBJECTNAME) VALUES ('Art')");
                stmt.execute("INSERT INTO SUBJECT (SUBJECTNAME) VALUES ('Health & PE')");
                stmt.execute("INSERT INTO STUDENT (STNAME,PNAME,DOB,GENDER,PHONE,ADDRESS,CLASS,SECTION) VALUES ('Kavya Rathnayake','Sunil Rathnayake','2012-03-14','Female','0771110001','23 Lake Rd, Colombo','Grade 6','A')");
                stmt.execute("INSERT INTO STUDENT (STNAME,PNAME,DOB,GENDER,PHONE,ADDRESS,CLASS,SECTION) VALUES ('Dineth Kumara','Pradeep Kumara','2012-07-22','Male','0772220002','45 Hill St, Kandy','Grade 6','A')");
                stmt.execute("INSERT INTO STUDENT (STNAME,PNAME,DOB,GENDER,PHONE,ADDRESS,CLASS,SECTION) VALUES ('Shenal Wijesinghe','Rohan Wijesinghe','2012-11-05','Male','0773330003','67 Beach Ave, Galle','Grade 6','A')");
                stmt.execute("INSERT INTO STUDENT (STNAME,PNAME,DOB,GENDER,PHONE,ADDRESS,CLASS,SECTION) VALUES ('Amaya Dissanayake','Chaminda Dissanayake','2012-01-18','Female','0774440004','89 Park Ln, Negombo','Grade 6','B')");
                stmt.execute("INSERT INTO STUDENT (STNAME,PNAME,DOB,GENDER,PHONE,ADDRESS,CLASS,SECTION) VALUES ('Thisara Jayasekara','Lasitha Jayasekara','2012-09-30','Male','0775550005','12 River Rd, Matale','Grade 6','B')");
                stmt.execute("INSERT INTO STUDENT (STNAME,PNAME,DOB,GENDER,PHONE,ADDRESS,CLASS,SECTION) VALUES ('Senuri Pathirana','Aruna Pathirana','2011-04-12','Female','0776660006','34 Temple Rd, Kurunegala','Grade 7','A')");
                stmt.execute("INSERT INTO STUDENT (STNAME,PNAME,DOB,GENDER,PHONE,ADDRESS,CLASS,SECTION) VALUES ('Muditha Bandara','Gamini Bandara','2011-06-25','Male','0777770007','56 Main St, Anuradhapura','Grade 7','A')");
                stmt.execute("INSERT INTO STUDENT (STNAME,PNAME,DOB,GENDER,PHONE,ADDRESS,CLASS,SECTION) VALUES ('Hasini Samarakoon','Upul Samarakoon','2011-12-08','Female','0778880008','78 Fort Rd, Trincomalee','Grade 7','A')");
                stmt.execute("INSERT INTO STUDENT (STNAME,PNAME,DOB,GENDER,PHONE,ADDRESS,CLASS,SECTION) VALUES ('Sachith Madushanka','Bandula Madushanka','2011-02-14','Male','0779990009','90 Galle Rd, Moratuwa','Grade 7','B')");
                stmt.execute("INSERT INTO STUDENT (STNAME,PNAME,DOB,GENDER,PHONE,ADDRESS,CLASS,SECTION) VALUES ('Dilhara Weerasinghe','Ajith Weerasinghe','2011-08-19','Female','0770001010','11 Baseline Rd, Colombo 9','Grade 7','B')");
                stmt.execute("INSERT INTO EXAM (EXAMNAME,DATE,CLASS,SECTION,SUBJECT) VALUES ('Term 1 Maths','2026-02-10','Grade 6','A','Mathematics')");
                stmt.execute("INSERT INTO EXAM (EXAMNAME,DATE,CLASS,SECTION,SUBJECT) VALUES ('Term 1 Science','2026-02-11','Grade 6','A','Science')");
                stmt.execute("INSERT INTO MARKS (STID,STNAME,CLASS,SUBJECT,MARKS) VALUES (1,'Kavya Rathnayake','Grade 6','Mathematics',88)");
                stmt.execute("INSERT INTO MARKS (STID,STNAME,CLASS,SUBJECT,MARKS) VALUES (2,'Dineth Kumara','Grade 6','Mathematics',74)");
                stmt.execute("INSERT INTO MARKS (STID,STNAME,CLASS,SUBJECT,MARKS) VALUES (3,'Shenal Wijesinghe','Grade 6','Mathematics',91)");
                System.out.println("Sample data seeded.");
            }
            rsCheck.close();
            stmt.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ─── UI Construction ─────────────────────────────────────────────────
    private void buildUI() {
        setTitle("Student Management System — Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(920, 580);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG);
        setContentPane(root);

        // ── Left branding panel ──
        JPanel left = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(0x1A2C6B),
                    0, getHeight(), new Color(0x0F1117));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        left.setPreferredSize(new Dimension(360, 580));
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBorder(new EmptyBorder(80, 50, 60, 50));

        // Logo circle
        JLabel logo = new JLabel("🎓", SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x4F8EF7, false));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        logo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 52));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        logo.setPreferredSize(new Dimension(100, 100));
        logo.setMaximumSize(new Dimension(100, 100));

        JLabel school = UITheme.label("EduManage", 28f, true);
        school.setForeground(Color.WHITE);
        school.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tagline = UITheme.label("Student Management System", 13f, false);
        tagline.setForeground(new Color(0xA0AEC0));
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);

        left.add(Box.createVerticalGlue());
        left.add(logo);
        left.add(Box.createVerticalStrut(24));
        left.add(school);
        left.add(Box.createVerticalStrut(8));
        left.add(tagline);
        left.add(Box.createVerticalStrut(48));

        // Feature bullets
        String[] features = {"📊  Live grade tracking", "👩‍🏫  Teacher dashboard", "📋  Exam & marks management"};
        for (String f : features) {
            JLabel fl = UITheme.mutedLabel(f);
            fl.setForeground(new Color(0x90A0BE));
            fl.setFont(UITheme.fontPlain(13f));
            fl.setAlignmentX(Component.CENTER_ALIGNMENT);
            left.add(fl);
            left.add(Box.createVerticalStrut(10));
        }
        left.add(Box.createVerticalGlue());

        // Version badge
        JLabel ver = UITheme.mutedLabel("v2.0 — 2026");
        ver.setAlignmentX(Component.CENTER_ALIGNMENT);
        ver.setForeground(new Color(0x4A5568));
        left.add(ver);

        // ── Right form panel ──
        JPanel right = new JPanel();
        right.setBackground(UITheme.SURFACE);
        right.setLayout(new GridBagLayout());

        JPanel form = new JPanel();
        form.setBackground(UITheme.SURFACE);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setPreferredSize(new Dimension(340, 440));

        JLabel welcome = UITheme.label("Welcome Back", 26f, true);
        welcome.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = UITheme.mutedLabel("Sign in to continue to EduManage");
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Username
        JLabel lbUser = UITheme.label("Username", 12f, false);
        lbUser.setForeground(UITheme.TEXT_MUTED);
        lbUser.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtuname = UITheme.textField("");
        txtuname.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtuname.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Password
        JLabel lbPass = UITheme.label("Password", 12f, false);
        lbPass.setForeground(UITheme.TEXT_MUTED);
        lbPass.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtpass = UITheme.passwordField();
        txtpass.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtpass.setAlignmentX(Component.LEFT_ALIGNMENT);

        // User type
        JLabel lbType = UITheme.label("User Type", 12f, false);
        lbType.setForeground(UITheme.TEXT_MUTED);
        lbType.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtutype = UITheme.comboBox(new String[]{"Admin", "Teacher"});
        txtutype.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtutype.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Login button
        jButton1 = UITheme.button("  Sign In  →", UITheme.ACCENT);
        jButton1.setAlignmentX(Component.LEFT_ALIGNMENT);
        jButton1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        jButton1.addActionListener(e -> loginToSystem());

        // Enter key on password field triggers login
        txtpass.addActionListener(e -> loginToSystem());
        txtuname.addActionListener(e -> txtpass.requestFocus());

        // Hint label
        JLabel hint = UITheme.mutedLabel("Default: admin / admin  (Admin)");
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);
        hint.setFont(UITheme.fontPlain(11f));

        form.add(welcome);
        form.add(Box.createVerticalStrut(4));
        form.add(sub);
        form.add(Box.createVerticalStrut(36));
        form.add(lbUser);
        form.add(Box.createVerticalStrut(6));
        form.add(txtuname);
        form.add(Box.createVerticalStrut(18));
        form.add(lbPass);
        form.add(Box.createVerticalStrut(6));
        form.add(txtpass);
        form.add(Box.createVerticalStrut(18));
        form.add(lbType);
        form.add(Box.createVerticalStrut(6));
        form.add(txtutype);
        form.add(Box.createVerticalStrut(28));
        form.add(jButton1);
        form.add(Box.createVerticalStrut(16));
        form.add(hint);

        right.add(form);

        // Divider strip between panels
        JSeparator div = new JSeparator(JSeparator.VERTICAL);
        div.setPreferredSize(new Dimension(1, 1));
        div.setForeground(UITheme.BORDER);

        root.add(left, BorderLayout.WEST);
        root.add(right, BorderLayout.CENTER);
    }

    // ─── Login logic ─────────────────────────────────────────────────────
    private void loginToSystem() {
        String username = txtuname.getText().trim();
        String pass     = new String(txtpass.getPassword()).trim();
        String utype    = txtutype.getSelectedItem().toString();

        if (username.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            if (con == null) {
                JOptionPane.showMessageDialog(this, "Database not connected.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            pst = con.prepareStatement("SELECT * FROM USERS WHERE UNAME = ? AND PASSWORD = ? AND UTYPE = ?");
            pst.setString(1, username);
            pst.setString(2, pass);
            pst.setString(3, utype);
            rs = pst.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("ID");
                System.out.println("Login successful: " + username);
                this.dispose();
                if (utype.equals("Admin")) {
                    new main(id, username, utype).setVisible(true);
                } else {
                    new teachermain(id, username, utype).setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                txtuname.setText("");
                txtpass.setText("");
                txtuname.requestFocus();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Login Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(login::new);
    }
}
