import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

/**
 * UITheme — Shared design system for Student Management System
 * Dark modern palette with accent colours, custom renderers and helpers.
 */
public class UITheme {

    // ─── Palette ───────────────────────────────────────────────────────────
    public static final Color BG          = new Color(0x0B0D11); // Deeper black
    public static final Color SURFACE     = new Color(0x13161C); // Richer slate surface
    public static final Color SIDEBAR     = new Color(0x0E1116);
    public static final Color CARD        = new Color(0x191D24);
    public static final Color ACCENT      = new Color(0x4078D1); // More professional blue
    public static final Color ACCENT_DARK = new Color(0x2D5AA3);
    public static final Color PURPLE      = new Color(0x6A52AD);
    public static final Color SUCCESS     = new Color(0x27AE60);
    public static final Color WARNING     = new Color(0xE67E22);
    public static final Color DANGER      = new Color(0xC0392B);
    public static final Color MUTED       = new Color(0x3B4354);
    public static final Color TEXT        = new Color(0xF0F0F0);
    public static final Color TEXT_MUTED  = new Color(0x8C9AB2);
    public static final Color BORDER      = new Color(0x2D3150);
    public static final Color ROW_ALT     = new Color(0x1A1D2E);
    public static final Color ROW_SEL     = new Color(0x2A3A5E);

    // ─── Fonts ─────────────────────────────────────────────────────────────
    public static Font font(int style, float size) {
        return new Font("Segoe UI", style, (int) size);
    }
    public static Font fontBold(float size)   { return font(Font.BOLD,  size); }
    public static Font fontPlain(float size)  { return font(Font.PLAIN, size); }

    // ─── DB path helper ────────────────────────────────────────────────────
    public static String dbUrl() {
        String p = System.getProperty("db.path", "./data/schoolmanagment");
        return "jdbc:h2:" + p + ";MODE=MySQL;AUTO_SERVER=TRUE";
    }

    // ─── Global LookAndFeel init ────────────────────────────────────────────
    public static void applyGlobalDefaults() {
        UIManager.put("Panel.background",           BG);
        UIManager.put("OptionPane.background",      SURFACE);
        UIManager.put("OptionPane.messageForeground",TEXT);
        UIManager.put("Button.background",          ACCENT);
        UIManager.put("Button.foreground",          TEXT);
        UIManager.put("Label.foreground",           TEXT);
        UIManager.put("TextField.background",       CARD);
        UIManager.put("TextField.foreground",       TEXT);
        UIManager.put("TextField.caretForeground",  ACCENT);
        UIManager.put("ComboBox.background",        CARD);
        UIManager.put("ComboBox.foreground",        TEXT);
        UIManager.put("Table.background",           BG);
        UIManager.put("Table.foreground",           TEXT);
        UIManager.put("Table.selectionBackground",  ROW_SEL);
        UIManager.put("Table.selectionForeground",  TEXT);
        UIManager.put("TableHeader.background",     SIDEBAR);
        UIManager.put("TableHeader.foreground",     ACCENT);
        UIManager.put("ScrollPane.background",      BG);
        UIManager.put("ScrollBar.thumb",            MUTED);
        UIManager.put("ScrollBar.track",            SURFACE);
    }

    // ─── Styled JButton ────────────────────────────────────────────────────
    public static JButton button(String text, Color bg) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = getModel().isPressed()  ? bg.darker() :
                          getModel().isRollover() ? bg.brighter() : bg;
                g2.setColor(c);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(fontBold(13f));
        b.setForeground(Color.WHITE);
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        b.setBorder(new EmptyBorder(10, 20, 10, 20));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setFocusPainted(false);
        return b;
    }

    // ─── Sidebar nav button ────────────────────────────────────────────────
    public static JButton navButton(String icon, String label) {
        String text = (icon == null || icon.isEmpty()) ? label : icon + "  " + label;
        JButton b = new JButton(text);
        b.setFont(fontPlain(14f));
        b.setForeground(TEXT_MUTED);
        b.setBackground(SIDEBAR);
        b.setContentAreaFilled(false);
        b.setOpaque(true);
        b.setBorder(new EmptyBorder(12, 24, 12, 20));
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setFocusPainted(false);
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                b.setForeground(TEXT);
                b.setBackground(new Color(0x1F232D));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                b.setForeground(TEXT_MUTED);
                b.setBackground(SIDEBAR);
            }
        });
        return b;
    }

    // ─── Styled text field ─────────────────────────────────────────────────
    public static JTextField textField(String placeholder) {
        JTextField f = new JTextField(placeholder);
        f.setFont(fontPlain(13f));
        f.setForeground(TEXT);
        f.setBackground(CARD);
        f.setCaretColor(ACCENT);
        f.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(6, 10, 6, 10)
        ));
        return f;
    }

    // ─── Styled password field ─────────────────────────────────────────────
    public static JPasswordField passwordField() {
        JPasswordField f = new JPasswordField();
        f.setFont(fontPlain(13f));
        f.setForeground(TEXT);
        f.setBackground(CARD);
        f.setCaretColor(ACCENT);
        f.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(6, 10, 6, 10)
        ));
        return f;
    }

    // ─── Styled combo box ──────────────────────────────────────────────────
    public static <T> JComboBox<T> comboBox(T[] items) {
        JComboBox<T> c = new JComboBox<>(items);
        c.setFont(fontPlain(13f));
        c.setForeground(TEXT);
        c.setBackground(CARD);
        c.setBorder(new LineBorder(BORDER, 1, true));
        c.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean sel, boolean focus) {
                super.getListCellRendererComponent(list, value, index, sel, focus);
                setBackground(sel ? ACCENT_DARK : CARD);
                setForeground(TEXT);
                setBorder(new EmptyBorder(5, 10, 5, 10));
                return this;
            }
        });
        return c;
    }

    // ─── Label ─────────────────────────────────────────────────────────────
    public static JLabel label(String text, float size, boolean bold) {
        JLabel l = new JLabel(text);
        l.setFont(bold ? fontBold(size) : fontPlain(size));
        l.setForeground(TEXT);
        return l;
    }
    public static JLabel mutedLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(fontPlain(12f));
        l.setForeground(TEXT_MUTED);
        return l;
    }

    // ─── Card panel ────────────────────────────────────────────────────────
    public static JPanel card() {
        JPanel p = new JPanel();
        p.setBackground(CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(20, 20, 20, 20)
        ));
        return p;
    }

    // ─── Section header ────────────────────────────────────────────────────
    public static JLabel sectionHeader(String title) {
        JLabel l = new JLabel(title);
        l.setFont(fontBold(11f));
        l.setForeground(TEXT_MUTED);
        l.setBorder(new EmptyBorder(8, 20, 4, 0));
        return l;
    }

    // ─── Table styling ─────────────────────────────────────────────────────
    public static void styleTable(JTable table) {
        table.setBackground(BG);
        table.setForeground(TEXT);
        table.setFont(fontPlain(13f));
        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(ROW_SEL);
        table.setSelectionForeground(TEXT);
        table.setFillsViewportHeight(true);
        table.setFocusable(false);
        
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                if (sel) {
                    setBackground(ROW_SEL);
                } else {
                    setBackground(row % 2 == 0 ? BG : ROW_ALT);
                }
                setForeground(TEXT);
                setFont(fontPlain(13f));
                setBorder(new EmptyBorder(0, 16, 0, 16));
                return this;
            }
        });
        
        JTableHeader header = table.getTableHeader();
        header.setBackground(SIDEBAR);
        header.setForeground(ACCENT);
        header.setFont(fontBold(12f));
        header.setPreferredSize(new Dimension(0, 44));
        header.setBorder(new MatteBorder(0, 0, 1, 0, BORDER));
        header.setReorderingAllowed(false);
        
        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        headerRenderer.setBorder(new EmptyBorder(0, 16, 0, 16));
    }

    // ─── Styled scroll pane ────────────────────────────────────────────────
    public static JScrollPane scrollPane(JTable table) {
        JScrollPane sp = new JScrollPane(table);
        sp.setBackground(BG);
        sp.getViewport().setBackground(BG);
        sp.setBorder(new LineBorder(BORDER, 1));
        sp.getVerticalScrollBar().setBackground(SURFACE);
        sp.getHorizontalScrollBar().setBackground(SURFACE);
        return sp;
    }

    // ─── Stat card ─────────────────────────────────────────────────────────
    public static JPanel statCard(String title, String value, String ignore, Color accent) {
        JPanel p = new JPanel(new BorderLayout(0, 8)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(accent);
                g2.fillRoundRect(0, 0, 6, getHeight(), 16, 0); // Side indicator
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel titleLabel = new JLabel(title.toUpperCase());
        titleLabel.setFont(UITheme.fontBold(10f));
        titleLabel.setForeground(TEXT_MUTED);

        JLabel valLabel = new JLabel(value);
        valLabel.setFont(UITheme.fontBold(26f));
        valLabel.setForeground(Color.WHITE);

        p.add(titleLabel, BorderLayout.NORTH);
        p.add(valLabel, BorderLayout.CENTER);
        return p;
    }
}
