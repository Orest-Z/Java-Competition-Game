import java.awt.*;
import javax.swing.*;


public class MenuPanel extends JPanel {

    private final MainFrame mainFrame;
    private Image backgroundImage;

    public MenuPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        // Load image from project root (place the .jpg next to your .java files)
        backgroundImage = new ImageIcon("resources/backgroundMENU.png").getImage();
        setPreferredSize(new Dimension(480, 600));
        setLayout(new GridBagLayout());  // entire panel is one grid

        addButtons();
    }


    private void addButtons() {
        GridBagConstraints gbc = new GridBagConstraints();

        // ── Shared constraints for all buttons ──────────────────────────────
        gbc.gridx    = 0;          // column 0 (only column — centered by GBL)
        gbc.anchor   = GridBagConstraints.CENTER;   // center within the cell
        gbc.fill     = GridBagConstraints.HORIZONTAL; // stretch to same width
        gbc.ipadx    = 40;         // internal padding: makes button wider
        gbc.ipady    = 12;         // internal padding: makes button taller
        gbc.insets   = new Insets(8, 0, 8, 0); // external gap: 8px top & bottom

        // ── Play button ─────────────────────────────────────────────────────
        gbc.gridy = 0;             // row 0
        JButton playBtn = createStyledButton("▶  PLAY");
        playBtn.addActionListener(e -> mainFrame.switchTo(MainFrame.GAME));
        add(playBtn, gbc);

        // ── Settings button ─────────────────────────────────────────────────
        gbc.gridy = 1;             // row 1
        JButton settingsBtn = createStyledButton("⚙  SETTINGS");
        settingsBtn.addActionListener(e -> openSettings());
        add(settingsBtn, gbc);

        // ── Exit button ──────────────────────────────────────────────────────
        gbc.gridy = 2;             // row 2
        JButton exitBtn = createStyledButton("✕  EXIT");
        exitBtn.addActionListener(e -> System.exit(0));
        add(exitBtn, gbc);
    }



    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                // Ngjyra ndryshon kur mausi është sipër (hover)
                if (getModel().isRollover()) {
                    // Hover: më i ndritshëm, blu-gri neon
                    g2.setColor(new Color(0, 200, 255, 80));
                } else {
                    // Normal: i errët, gjysmë-transparent
                    g2.setColor(new Color(0, 0, 0, 140));
                }

                // Pikturon sfondin me kënde të rrumbullakëta
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);

                // Pikturon borderin neon
                g2.setColor(new Color(0, 200, 255, 180));  // ngjyrë cyan neon
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 14, 14);

                g2.dispose();
                super.paintComponent(g); // pikturon tekstin sipër
            }
        };

        btn.setFont(new Font("Monospaced", Font.BOLD, 16));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false); // ne e menaxhojmë vetë sfondin
        btn.setOpaque(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void openSettings() {
        /*Aktualisht thjesht kam vendosur nje dialog qe tregon se do te shtoj me shume
        settings ne ditet ne vazhdim si psh Full Screen themes etj. */
            JDialog dialog = new JDialog(mainFrame, "Settings", true);
            dialog.setLayout(new FlowLayout());
            dialog.getContentPane().setBackground(new Color(20, 20, 20));
            dialog.setSize(300, 150);

            JLabel label = new JLabel("Settings do të shtohen së shpejti!");
            label.setForeground(Color.WHITE);
            dialog.add(label);

            dialog.setLocationRelativeTo(mainFrame);
            dialog.setVisible(true);

    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw background image scaled to fill the entire panel
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}