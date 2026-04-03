import java.awt.*;
import java.awt.event.*;
import java.sql.*;
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

    private JTextField       txtuname;
    private JPasswordField   txtpass;
    private JComboBox<String> txtutype;
    private JButton          jButton1;

    public login() {
        UITheme.applyGlobalDefaults();
        buildUI();
        Connect();
        setVisible(true);
    }

    public void Connect() {
        try {
            Class.forName("org.h2.Driver");
            String dbPath = System.getProperty("db.path", "./data/schoolmanagment");
            con = DriverManager.getConnection("jdbc:h2:" + dbPath + ";MODE=MySQL;AUTO_SERVER=TRUE", "sa", "");
            
            Statement stmt = con.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS USERS (ID INT PRIMARY KEY AUTO_INCREMENT, NAME VARCHAR(255), PHONE INT, ADDRESS VARCHAR(255), UNAME VARCHAR(255), PASSWORD VARCHAR(255), UTYPE VARCHAR(255))");
            stmt.execute("CREATE TABLE IF NOT EXISTS CLASS (CID INT PRIMARY KEY AUTO_INCREMENT, CLASSNAME VARCHAR(255), SECTION VARCHAR(255))");
            stmt.execute("CREATE TABLE IF NOT EXISTS SUBJECT (SID INT PRIMARY KEY AUTO_INCREMENT, SUBJECTNAME VARCHAR(255))");
            stmt.execute("CREATE TABLE IF NOT EXISTS STUDENT (STUDENTID INT PRIMARY KEY AUTO_INCREMENT, STNAME VARCHAR(255), PNAME VARCHAR(255), DOB DATE, GENDER VARCHAR(255), PHONE VARCHAR(255), ADDRESS VARCHAR(255), CLASS VARCHAR(255), SECTION VARCHAR(255))");
            stmt.execute("CREATE TABLE IF NOT EXISTS EXAM (EID INT PRIMARY KEY AUTO_INCREMENT, ENAME VARCHAR(255), TERM VARCHAR(255), CLASS VARCHAR(255), SECTION VARCHAR(255), EDATE DATE)");
            stmt.execute("CREATE TABLE IF NOT EXISTS MARKS (MARKID INT PRIMARY KEY AUTO_INCREMENT, STID INT, STNAME VARCHAR(255), CLASS VARCHAR(255), SUBJECT VARCHAR(255), MARKS INT)");

            ResultSet rsCheck = stmt.executeQuery("SELECT COUNT(*) FROM USERS");
            if (rsCheck.next() && rsCheck.getInt(1) == 0) {
                stmt.execute("INSERT INTO USERS (NAME,PHONE,ADDRESS,UNAME,PASSWORD,UTYPE) VALUES ('Amal Silva',715689652,'56/8A Yakkala, Gampaha','admin','admin','Admin')");
                stmt.execute("INSERT INTO USERS (NAME,PHONE,ADDRESS,UNAME,PASSWORD,UTYPE) VALUES ('Kasuni Perera',771234567,'12/B Kandy Road, Colombo','Kasuni','1234','Teacher')");
            }
            rsCheck.close(); stmt.close();
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void buildUI() {
        setTitle("EduManage — Login"); setDefaultCloseOperation(EXIT_ON_CLOSE); setSize(920, 580); setLocationRelativeTo(null); setResizable(false);
        JPanel root = new JPanel(new BorderLayout()); root.setBackground(UITheme.BG); setContentPane(root);

        // Sidebar Branding
        JPanel left = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0,0,new Color(0x1A2C6B),0,getHeight(),new Color(0x0F1117)));
                g2.fillRect(0,0,getWidth(),getHeight()); g2.dispose();
            }
        };
        left.setPreferredSize(new Dimension(360, 580)); left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS)); left.setBorder(new EmptyBorder(80, 50, 60, 50));

        JLabel logo = new JLabel("", SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x4F8EF7)); g2.fillOval(0,0,getWidth(),getHeight()); g2.dispose();
                super.paintComponent(g);
            }
        };
        logo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 52)); logo.setAlignmentX(CENTER_ALIGNMENT); logo.setPreferredSize(new Dimension(80,80)); logo.setMaximumSize(new Dimension(80,80));

        JLabel school = UITheme.label("EduManage", 28f, true); school.setForeground(Color.WHITE); school.setAlignmentX(CENTER_ALIGNMENT);
        JLabel tagline = UITheme.label("Student Management System", 13f, false); tagline.setForeground(new Color(0xA0AEC0)); tagline.setAlignmentX(CENTER_ALIGNMENT);

        left.add(Box.createVerticalGlue()); left.add(logo); left.add(Box.createVerticalStrut(24)); left.add(school); left.add(Box.createVerticalStrut(8)); left.add(tagline);
        left.add(Box.createVerticalStrut(48));

        String[] highlights = {"Comprehensive Dashboard", "Student & Teacher Portal", "Automated Reports"};
        for(String h : highlights){ JLabel hl=UITheme.mutedLabel(h); hl.setForeground(new Color(0x90A0BE)); hl.setAlignmentX(CENTER_ALIGNMENT); left.add(hl); left.add(Box.createVerticalStrut(10)); }
        left.add(Box.createVerticalGlue());
        JLabel ver = UITheme.mutedLabel("v2.0 — Dark Edition"); ver.setAlignmentX(CENTER_ALIGNMENT); left.add(ver);

        // Form Area
        JPanel right = new JPanel(new GridBagLayout()); right.setBackground(UITheme.SURFACE);
        JPanel form = new JPanel(); form.setBackground(UITheme.SURFACE); form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS)); form.setPreferredSize(new Dimension(340, 440));

        JLabel welcome = UITheme.label("Welcome Back", 26f, true); welcome.setAlignmentX(LEFT_ALIGNMENT);
        JLabel sub = UITheme.mutedLabel("Sign in to your account"); sub.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lbU = UITheme.label("Username", 12f, false); lbU.setForeground(UITheme.TEXT_MUTED); lbU.setAlignmentX(LEFT_ALIGNMENT);
        txtuname = UITheme.textField(""); txtuname.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38)); txtuname.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lbP = UITheme.label("Password", 12f, false); lbP.setForeground(UITheme.TEXT_MUTED); lbP.setAlignmentX(LEFT_ALIGNMENT);
        txtpass =UITheme.passwordField(); txtpass.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38)); txtpass.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lbT = UITheme.label("Account Type", 12f, false); lbT.setForeground(UITheme.TEXT_MUTED); lbT.setAlignmentX(LEFT_ALIGNMENT);
        txtutype = UITheme.comboBox(new String[]{"Admin", "Teacher"}); txtutype.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38)); txtutype.setAlignmentX(LEFT_ALIGNMENT);

        jButton1 = UITheme.button("Sign In  →", UITheme.ACCENT); jButton1.setAlignmentX(LEFT_ALIGNMENT); jButton1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        jButton1.addActionListener(e -> loginToSystem());
        txtpass.addActionListener(e -> loginToSystem());

        form.add(welcome); form.add(Box.createVerticalStrut(4)); form.add(sub); form.add(Box.createVerticalStrut(36));
        form.add(lbU); form.add(Box.createVerticalStrut(6)); form.add(txtuname); form.add(Box.createVerticalStrut(18));
        form.add(lbP); form.add(Box.createVerticalStrut(6)); form.add(txtpass); form.add(Box.createVerticalStrut(18));
        form.add(lbT); form.add(Box.createVerticalStrut(6)); form.add(txtutype); form.add(Box.createVerticalStrut(32));
        form.add(jButton1); form.add(Box.createVerticalStrut(16));
        JLabel hint = UITheme.mutedLabel("admin / admin"); hint.setFont(UITheme.fontPlain(11f)); form.add(hint);

        right.add(form); root.add(left, BorderLayout.WEST); root.add(right, BorderLayout.CENTER);
    }

    private void loginToSystem() {
        String u = txtuname.getText().trim(), p = new String(txtpass.getPassword()).trim(), t = txtutype.getSelectedItem().toString();
        if (u.isEmpty() || p.isEmpty()) return;
        try {
            pst = con.prepareStatement("SELECT * FROM USERS WHERE UNAME=? AND PASSWORD=? AND UTYPE=?");
            pst.setString(1, u); pst.setString(2, p); pst.setString(3, t);
            rs = pst.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("ID"); this.dispose();
                if (t.equals("Admin")) new main(id, u, t).setVisible(true);
                else new teachermain(id, u, t).setVisible(true);
            } else { JOptionPane.showMessageDialog(this, "Invalid credentials"); }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    public static void main(String[] args) { SwingUtilities.invokeLater(login::new); }
}
