import java.awt.*;
import javax.swing.*;
import javax.sound.*;

public class MenuPanel extends JPanel {

    private final MainFrame mainFrame;
    private Image backgroundImage;

    private final AudioManager audioManager;

    public MenuPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.audioManager = mainFrame.getAudioManager();  // ← shto

        // Load image from project root (place the .jpg next to your .java files)
        backgroundImage = new ImageIcon("assets/backgroundMENU.png").getImage();
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
        playBtn.addActionListener(e -> {
            audioManager.playSFX("assets/click.wav");
            mainFrame.switchTo(MainFrame.GAME);
        });
        add(playBtn, gbc);

        // SHOP — gridy = 1 (midis PLAY dhe SETTINGS)
        gbc.gridy = 1;
        JButton shopBtn = createStyledButton("🛒  SHOP");
        shopBtn.addActionListener(e -> {
            audioManager.playSFX("assets/click.wav");
            mainFrame.switchTo("SHOP");
        });
        add(shopBtn, gbc);



        // ── Settings button ─────────────────────────────────────────────────
        gbc.gridy = 2;             // row 1
        JButton settingsBtn = createStyledButton("⚙  SETTINGS");
        settingsBtn.addActionListener(e -> {
            audioManager.playSFX("assets/click.wav");//Luhet soundi i klikimit
            openSettings();
        });
        add(settingsBtn, gbc);

        // ── Exit button ──────────────────────────────────────────────────────
        gbc.gridy = 3;             // row 2
        JButton exitBtn = createStyledButton("✕  EXIT");
        exitBtn.addActionListener(e -> {
            audioManager.playSFX("assets/click.wav");
            System.exit(0);
        });
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


    //Metoda per te stiluar checkboxet
    private void styleCheckbox(JCheckBox box) {
        box.setForeground(Color.WHITE);
        box.setBackground(new Color(15, 15, 25));
        box.setFont(new Font("Monospaced", Font.PLAIN, 13));
        box.setFocusPainted(false);
    }

    private void openSettings() {
        JDialog dialog = new JDialog(mainFrame, "Settings", true);
        dialog.setLayout(new GridBagLayout());
        dialog.setSize(320, 260);
        dialog.getContentPane().setBackground(new Color(15, 15, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx   = 0;
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.insets  = new Insets(10, 20, 10, 20);

        // ── Titulli ───────────────────────────────────────────
        JLabel title = new JLabel("⚙  SETTINGS");
        title.setForeground(new Color(0, 200, 255));
        title.setFont(new Font("Monospaced", Font.BOLD, 16));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        dialog.add(title, gbc);

        // ── Fullscreen checkbox ────────────────────────────────
        JCheckBox fullscreenBox = new JCheckBox("🖥  Fullscreen Mode");
        styleCheckbox(fullscreenBox);
        fullscreenBox.setSelected(mainFrame.isFullscreen());
        gbc.gridy = 1;
        dialog.add(fullscreenBox, gbc);

        // ── Music checkbox ─────────────────────────────────────
        JCheckBox musicBox = new JCheckBox("🎵  Background Music");
        styleCheckbox(musicBox);
        musicBox.setSelected(GamePanel.musicEnabled);
        gbc.gridy = 2;
        dialog.add(musicBox, gbc);

        // ── SFX checkbox ───────────────────────────────────────
        JCheckBox sfxBox = new JCheckBox("🔊  Sound Effects");
        styleCheckbox(sfxBox);
        sfxBox.setSelected(GamePanel.sfxEnabled);
        gbc.gridy = 3;
        dialog.add(sfxBox, gbc);

        // ── Butoni Apply ───────────────────────────────────────
        JButton applyBtn = new JButton("✔  APPLY");
        applyBtn.setFont(new Font("Monospaced", Font.BOLD, 14));
        applyBtn.setForeground(Color.WHITE);
        applyBtn.setBackground(new Color(0, 120, 160));
        applyBtn.setFocusPainted(false);
        applyBtn.setBorderPainted(false);
        applyBtn.addActionListener(e -> {
            //Luhet nje sound i shkurter klikimi
            audioManager.playSFX("assets/click.wav");
            // Fullscreen
            if (fullscreenBox.isSelected() != mainFrame.isFullscreen()) {
                mainFrame.toggleFullscreen();
            }
            // Audio flags — nesër do i lexojë sistemi i audio
            GamePanel.musicEnabled = musicBox.isSelected();
            GamePanel.sfxEnabled   = sfxBox.isSelected();

            if(GamePanel.musicEnabled) {
                audioManager.playMusic();
            }
            else if (!GamePanel.musicEnabled) {
                audioManager.stopMusic();
            }
            dialog.dispose();
        });
        gbc.gridy = 4;
        dialog.add(applyBtn, gbc);

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