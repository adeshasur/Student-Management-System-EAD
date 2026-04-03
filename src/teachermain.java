import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * Teacher dashboard — same sidebar pattern as admin but with limited nav.
 */
public class teachermain extends JFrame {

    int iid;
    String uname;
    String usertype;

    private JLabel jLabel1, jLabel2;
    private JButton btnmarks, btnstudent1, jButton5;

    public teachermain() { this(0, "Teacher", "Teacher"); }

    public teachermain(int id, String username, String utype) {
        UITheme.applyGlobalDefaults();
        this.iid      = id;
        this.uname    = username;
        this.usertype = utype;
        buildUI();
    }

    private void buildUI() {
        setTitle("Student Management System - EAD2 — Teacher Dashboard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 620);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 540));

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG);
        setContentPane(root);

        root.add(buildSidebar(),  BorderLayout.WEST);
        root.add(buildMain(),     BorderLayout.CENTER);
    }

    private JPanel buildSidebar() {
        JPanel sb = new JPanel();
        sb.setBackground(UITheme.SIDEBAR);
        sb.setPreferredSize(new Dimension(210, 0));
        sb.setLayout(new BoxLayout(sb, BoxLayout.Y_AXIS));
        sb.setBorder(new MatteBorder(0, 0, 0, 1, UITheme.BORDER));

        JLabel logo = new JLabel("EduManage");
        logo.setFont(UITheme.fontBold(16f));
        logo.setForeground(UITheme.ACCENT);
        logo.setBorder(new EmptyBorder(24, 20, 24, 20));
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        sb.add(logo);

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(UITheme.BORDER);
        sb.add(sep);
        sb.add(Box.createVerticalStrut(8));
        sb.add(UITheme.sectionHeader("TEACHER MENU"));

        btnstudent1 = UITheme.navButton("", "Students");
        btnmarks    = UITheme.navButton("", "Marks");

        for (JButton b : new JButton[]{btnstudent1, btnmarks}) {
            b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
            b.setAlignmentX(Component.LEFT_ALIGNMENT);
            sb.add(b);
        }

        sb.add(Box.createVerticalGlue());

        JSeparator sep2 = new JSeparator();
        sep2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep2.setForeground(UITheme.BORDER);
        sb.add(sep2);

        jButton5 = UITheme.button("Logout", UITheme.DANGER);
        jButton5.setAlignmentX(Component.CENTER_ALIGNMENT);
        jButton5.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        jButton5.setOpaque(false); // Changed to false for better button look
        sb.add(jButton5);
        sb.add(Box.createVerticalStrut(16));

        btnstudent1.addActionListener(e -> new student().setVisible(true));
        btnmarks.addActionListener(e    -> new marks().setVisible(true));
        jButton5.addActionListener(e    -> { this.dispose(); new login(); });

        return sb;
    }

    private JPanel buildMain() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(UITheme.BG);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.SURFACE);
        header.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 1, 0, UITheme.BORDER),
            new EmptyBorder(14, 28, 14, 24)));
        header.setPreferredSize(new Dimension(0, 64));

        JLabel title = UITheme.label("Teacher Dashboard", 16f, true);

        JPanel userInfo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        userInfo.setOpaque(false);

        String initials = uname.length() >= 2 ? uname.substring(0, 2).toUpperCase() : uname.toUpperCase();
        JLabel avatar = new JLabel(initials) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.PURPLE);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        avatar.setFont(UITheme.fontBold(13f));
        avatar.setForeground(Color.WHITE);
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(36, 36));

        jLabel1 = UITheme.label(uname, 13f, true);
        jLabel2 = UITheme.mutedLabel(usertype);

        JPanel nameCol = new JPanel(new GridLayout(2, 1));
        nameCol.setOpaque(false);
        nameCol.add(jLabel1);
        nameCol.add(jLabel2);

        userInfo.add(nameCol);
        userInfo.add(avatar);

        header.add(title, BorderLayout.WEST);
        header.add(userInfo, BorderLayout.EAST);
        content.add(header, BorderLayout.NORTH);

        // Body
        JPanel body = new JPanel();
        body.setBackground(UITheme.BG);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(32, 32, 32, 32));

        JLabel pg = UITheme.label("Welcome, " + uname, 22f, true);
        pg.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel sub = UITheme.mutedLabel("You are logged in as a Teacher. Use the sidebar to navigate.");
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        body.add(pg);
        body.add(Box.createVerticalStrut(6));
        body.add(sub);
        body.add(Box.createVerticalStrut(36));

        // Quick grid for teacher
        JPanel grid = new JPanel(new GridLayout(1, 2, 16, 16));
        grid.setOpaque(false);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        grid.setAlignmentX(Component.LEFT_ALIGNMENT);

        grid.add(quickCard("", "Students", "Add and view student records", e -> new student().setVisible(true)));
        grid.add(quickCard("", "Marks",    "Enter and manage student marks",  e -> new marks().setVisible(true)));

        body.add(grid);
        content.add(body, BorderLayout.CENTER);
        return content;
    }

    private JPanel quickCard(String ignore, String title, String sub, ActionListener action) {
        JPanel p = new JPanel(new BorderLayout(12, 0)) {
            boolean hovered = false;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hovered=true; repaint(); setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); }
                public void mouseExited(MouseEvent e)  { hovered=false; repaint(); setCursor(Cursor.getDefaultCursor()); }
                public void mouseClicked(MouseEvent e) { action.actionPerformed(null); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hovered ? new Color(0x1F232D) : UITheme.CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(UITheme.ACCENT);
                g2.fillRoundRect(0, 0, 8, getHeight(), 16, 0); // Side indicator
                g2.dispose();
            }
        };
        p.setOpaque(false);
        JLabel tl = UITheme.label(title, 15f, true);
        JLabel sl = UITheme.mutedLabel(sub); sl.setFont(UITheme.fontPlain(11f));

        JPanel text = new JPanel(new GridLayout(2, 1, 0, 4));
        text.setOpaque(false); text.add(tl); text.add(sl);
        p.add(text, BorderLayout.CENTER);
        return p;
    }

    public static void main(String[] args) { SwingUtilities.invokeLater(() -> new teachermain(0, "Teacher", "Teacher").setVisible(true)); }
}
