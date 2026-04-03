import java.awt.*;
import java.awt.event.*;
import java.sql.*;

import javax.swing.*;
import javax.swing.border.*;

/**
 * Admin dashboard — sidebar navigation + stat cards + quick-access table.
 */
public class main extends JFrame {

    private String uname;
    private String usertype;

    // Stat card value labels (updated from DB)
    private JLabel statStudents, statTeachers, statClasses, statSubjects;

    private Connection con;

    public main() { this(0, "Admin", "Admin"); }

    public main(int id, String username, String utype) {
        UITheme.applyGlobalDefaults();
        this.uname    = username;
        this.usertype = utype;
        buildUI();
        connectAndRefreshStats();
    }

    // ─── Build UI ─────────────────────────────────────────────────────────
    private void buildUI() {
        setTitle("Student Management System - EAD2 — Admin Dashboard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 680);
        setLocationRelativeTo(null);
        setResizable(true);
        setMinimumSize(new Dimension(900, 600));

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG);
        setContentPane(root);

        root.add(buildSidebar(),  BorderLayout.WEST);
        root.add(buildMain(),     BorderLayout.CENTER);
    }

    // ── Sidebar ──────────────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel sb = new JPanel();
        sb.setBackground(UITheme.SIDEBAR);
        sb.setPreferredSize(new Dimension(210, 0));
        sb.setLayout(new BoxLayout(sb, BoxLayout.Y_AXIS));
        sb.setBorder(new MatteBorder(0, 0, 0, 1, UITheme.BORDER));

        // Logo area
        JLabel logo = new JLabel("EduManage");
        logo.setFont(UITheme.fontBold(16f));
        logo.setForeground(UITheme.ACCENT);
        logo.setBorder(new EmptyBorder(24, 20, 24, 20));
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        sb.add(logo);

        // Separator
        JSeparator sep1 = new JSeparator();
        sep1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep1.setForeground(UITheme.BORDER);
        sb.add(sep1);
        sb.add(Box.createVerticalStrut(8));

        sb.add(UITheme.sectionHeader("MAIN MENU"));

        // Nav buttons
        JButton btnstudent = UITheme.navButton("", "Students");
        JButton btnclass   = UITheme.navButton("", "Classes");
        JButton btnsubject = UITheme.navButton("", "Subjects");
        JButton btnteacher = UITheme.navButton("", "Teachers");
        JButton btnexam    = UITheme.navButton("", "Exams");
        JButton btnuser    = UITheme.navButton("", "Users");

        for (JButton b : new JButton[]{btnstudent, btnclass, btnsubject, btnteacher, btnexam, btnuser}) {
            b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
            b.setAlignmentX(Component.LEFT_ALIGNMENT);
            sb.add(b);
        }

        sb.add(Box.createVerticalGlue());

        JSeparator sep2 = new JSeparator();
        sep2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep2.setForeground(UITheme.BORDER);
        sb.add(sep2);

        // Logout
        JButton btnLogout = UITheme.button("Logout", UITheme.DANGER);
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnLogout.setOpaque(false);
        sb.add(btnLogout);
        sb.add(Box.createVerticalStrut(16));

        // Wire actions
        btnstudent.addActionListener(e -> new student().setVisible(true));
        btnclass.addActionListener(e   -> new classes().setVisible(true));
        btnsubject.addActionListener(e -> new subject().setVisible(true));
        btnteacher.addActionListener(e -> new teacher().setVisible(true));
        btnexam.addActionListener(e    -> new exam().setVisible(true));
        btnuser.addActionListener(e    -> new user().setVisible(true));
        btnLogout.addActionListener(e  -> logout());

        return sb;
    }

    // ── Main content area ─────────────────────────────────────────────────
    private JPanel buildMain() {
        JPanel content = new JPanel(new BorderLayout(0, 0));
        content.setBackground(UITheme.BG);

        // Top header bar
        JPanel header = buildHeader();
        content.add(header, BorderLayout.NORTH);

        // Body (stats + recent table)
        JPanel body = new JPanel();
        body.setBackground(UITheme.BG);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(28, 28, 28, 28));

        // Page title
        JLabel pageTitle = UITheme.label("Dashboard Overview", 22f, true);
        pageTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel pageSub = UITheme.mutedLabel("Welcome back, " + uname + "  ·  " + usertype);
        pageSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(pageTitle);
        body.add(Box.createVerticalStrut(4));
        body.add(pageSub);
        body.add(Box.createVerticalStrut(28));

        // Stat cards row
        JPanel statsRow = buildStatsRow();
        statsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(statsRow);
        body.add(Box.createVerticalStrut(32));

        // Quick nav grid
        JLabel qNav = UITheme.label("Quick Access", 16f, true);
        qNav.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(qNav);
        body.add(Box.createVerticalStrut(12));

        JPanel quickGrid = buildQuickGrid();
        quickGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(quickGrid);

        content.add(body, BorderLayout.CENTER);
        return content;
    }

    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(UITheme.SURFACE);
        h.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 1, 0, UITheme.BORDER),
            new EmptyBorder(14, 28, 14, 24)
        ));
        h.setPreferredSize(new Dimension(0, 64));

        JLabel title = UITheme.label("Student Management System", 16f, true);
        title.setForeground(UITheme.TEXT);

        // Avatar + user info on right
        JPanel userInfo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        userInfo.setOpaque(false);

        // Initials circle
        String initials = uname.length() >= 2
            ? uname.substring(0, 2).toUpperCase()
            : uname.toUpperCase();
        JLabel avatar = new JLabel(initials) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.ACCENT);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        avatar.setFont(UITheme.fontBold(13f));
        avatar.setForeground(Color.WHITE);
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(36, 36));

        JLabel jLabel1 = UITheme.label(uname, 13f, true);
        JLabel jLabel2 = UITheme.mutedLabel(usertype);

        JPanel nameCol = new JPanel(new GridLayout(2, 1, 0, 0));
        nameCol.setOpaque(false);
        nameCol.add(jLabel1);
        nameCol.add(jLabel2);

        userInfo.add(nameCol);
        userInfo.add(avatar);

        h.add(title, BorderLayout.WEST);
        h.add(userInfo, BorderLayout.EAST);
        return h;
    }

    private JPanel buildStatsRow() {
        JPanel p = new JPanel(new GridLayout(1, 4, 16, 0));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        statStudents = new JLabel("--");
        statTeachers = new JLabel("--");
        statClasses  = new JLabel("--");
        statSubjects = new JLabel("--");

        p.add(buildStatCard("Total Students",  statStudents, "", UITheme.ACCENT));
        p.add(buildStatCard("Teachers",        statTeachers, "", UITheme.PURPLE));
        p.add(buildStatCard("Classes",         statClasses,  "", UITheme.SUCCESS));
        p.add(buildStatCard("Subjects",        statSubjects, "", UITheme.WARNING));
        return p;
    }

    private JPanel buildStatCard(String title, JLabel value, String ignore, Color accent) {
        JPanel p = new JPanel(new BorderLayout(0, 8)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(accent);
                g2.fillRoundRect(0, 0, 6, getHeight(), 16, 0); // Side indicator
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel tl = UITheme.mutedLabel(title.toUpperCase());
        tl.setFont(UITheme.fontBold(10f));

        value.setFont(UITheme.fontBold(24f));
        value.setForeground(Color.WHITE);

        p.add(tl, BorderLayout.NORTH);
        p.add(value, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildQuickGrid() {
        JPanel grid = new JPanel(new GridLayout(2, 3, 14, 14));
        grid.setOpaque(false);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        String[][] items = {
            {"", "Students",  "Add, edit or view students"},
            {"", "Classes",   "Manage class groups"},
            {"", "Subjects",  "Curriculum subjects"},
            {"","Teachers",  "Staff management"},
            {"", "Exams",     "Schedule & track exams"},
            {"", "Users",     "User accounts & access"},
        };
        ActionListener[] acts = {
            e -> new student().setVisible(true),
            e -> new classes().setVisible(true),
            e -> new subject().setVisible(true),
            e -> new teacher().setVisible(true),
            e -> new exam().setVisible(true),
            e -> new user().setVisible(true),
        };

        for (int i = 0; i < items.length; i++) {
            String[] it = items[i];
            ActionListener al = acts[i];
            JPanel card = buildQuickCard(it[0], it[1], it[2], al);
            grid.add(card);
        }
        return grid;
    }

    private JPanel buildQuickCard(String icon, String title, String sub, ActionListener action) {
        JPanel p = new JPanel(new BorderLayout(10, 0)) {
            boolean hovered = false;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); }
                public void mouseExited(MouseEvent e)  { hovered = false; repaint(); setCursor(Cursor.getDefaultCursor()); }
                public void mouseClicked(MouseEvent e) { action.actionPerformed(null); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hovered ? new Color(0x1F232D) : UITheme.CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                if (hovered) {
                    g2.setColor(UITheme.ACCENT);
                    g2.setStroke(new BasicStroke(1.2f));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                }
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel ic = new JLabel(icon);
        ic.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));
        ic.setPreferredSize(new Dimension(36, 36));
        ic.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel tl = UITheme.label(title, 14f, true);
        JLabel sl = UITheme.mutedLabel(sub);
        sl.setFont(UITheme.fontPlain(11f));

        JPanel text = new JPanel(new GridLayout(2, 1, 0, 2));
        text.setOpaque(false);
        text.add(tl);
        text.add(sl);

        p.add(ic, BorderLayout.WEST);
        p.add(text, BorderLayout.CENTER);
        return p;
    }

    // ─── DB stats refresh ────────────────────────────────────────────────
    private void connectAndRefreshStats() {
        try {
            Class.forName("org.h2.Driver");
            con = DriverManager.getConnection(UITheme.dbUrl(), "sa", "");
            refreshStats();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void refreshStats() {
        try {
            if (con == null || con.isClosed()) return;
            Statement st = con.createStatement();
            statStudents.setText(queryCount(st, "SELECT COUNT(*) FROM STUDENT"));
            statTeachers.setText(queryCount(st, "SELECT COUNT(*) FROM USERS WHERE UTYPE='Teacher'"));
            statClasses.setText(queryCount(st,  "SELECT COUNT(*) FROM CLASS"));
            statSubjects.setText(queryCount(st, "SELECT COUNT(*) FROM SUBJECT"));
            st.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String queryCount(Statement st, String sql) {
        try {
            ResultSet r = st.executeQuery(sql);
            if (r.next()) return String.valueOf(r.getInt(1));
        } catch (Exception ignored) {}
        return "--";
    }

    // ─── Logout ──────────────────────────────────────────────────────────
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            new login();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new main(0, "Admin", "Admin").setVisible(true));
    }
}
