import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import java.awt.*;

public class ShopPanel extends JPanel {
    private final MainFrame mainFrame;
    private JLabel moneyLabel;
    private Image backgroundImage; //Variabli per imazhin
    private final AudioManager audioManager; // Shtova reference per AudioManager — fix per bug te new AudioManager()

    public ShopPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.audioManager = mainFrame.getAudioManager(); // Marrim AudioManager nga MainFrame sic behet ne MenuPanel
        //Ngarkojme imazhin per background
        backgroundImage = new ImageIcon("assets/backgroundShop.png").getImage();
        setPreferredSize(new Dimension(480, 750)); // Zmadhova lartesine qe te mos jene te ngjeshura butonat
        setBackground(new Color(10, 10, 20));
        setLayout(new GridBagLayout());
        buildUI();
    }
    // 3. Metoda që vizaton imazhin përpara se të vizatohen butonat
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            // Vizaton imazhin në të gjithë përmasën e panelit
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

            // që butonat dhe teksti të lexohen më mirë
            g.setColor(new Color(0, 0, 0, 120)); // E zezë me transparencë
            g.fillRect(0, 0, getWidth(), getHeight());
        }}

    private void buildUI() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill  = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(12, 30, 12, 30); // Shtova me shume hapesire vertikale midis rreshtave

        // ── Titulli ──────────────────────────────────────────
        JLabel title = new JLabel("🛒  SHOP", SwingConstants.CENTER);
        title.setFont(new Font("Monospaced", Font.BOLD, 24));
        title.setForeground(new Color(0, 200, 255));
        gbc.gridy = 0;
        add(title, gbc);

        // ── Monedhat ─────────────────────────────────────────
        moneyLabel = new JLabel("💰  " + MainFrame.totalMoney + " coins", SwingConstants.CENTER);
        moneyLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        moneyLabel.setForeground(new Color(255, 215, 0));
        gbc.gridy = 1;
        add(moneyLabel, gbc);

        // ── Seksioni Maps ─────────────────────────────────────
        addSectionLabel("── MAPS ──", gbc, 2);
        addShopButton("❄  Snow Map",    4000, () -> MainFrame.currentMap = "SNOW",    gbc, 3);
        addShopButton("🏜  Desert Map", 9000, () -> MainFrame.currentMap = "DESERT",  gbc, 4);
        addShopButton("🏙  Default",       0, () -> MainFrame.currentMap = "DEFAULT", gbc, 5);

        // ── Seksioni Skins ────────────────────────────────────
        addSectionLabel("── SKINS ──", gbc, 6);
        addShopButton("🚔  Police Car",  5000, () -> MainFrame.currentSkin = "POLICE", gbc, 7);
        addShopButton("🏍  Motorcycle",  7000, () -> MainFrame.currentSkin = "MOTO",   gbc, 8);
        addShopButton("🚗  Normal",          0, () -> MainFrame.currentSkin = "NORMAL", gbc, 9);

        // ── Seksioni Trails ───────────────────────────────────
        // Nje buton i vetem "Trail" me nje JComboBox djathtas per zgjedhjen e ngjyres
        addSectionLabel("── TRAILS ──", gbc, 10);
        addTrailRow(gbc, 11);

        // ── Butoni BACK ───────────────────────────────────────
        JButton backBtn = new JButton("←  BACK");
        backBtn.setFont(new Font("Monospaced", Font.BOLD, 14));
        backBtn.setForeground(Color.WHITE);
        backBtn.setBackground(new Color(50, 50, 80));
        backBtn.setFocusPainted(false);
        backBtn.setBorderPainted(false);
        backBtn.addActionListener(e -> mainFrame.switchTo(MainFrame.MENU));
        gbc.gridy = 12;
        add(backBtn, gbc);
    }

    // Metoda per rreshtin e trail-it — nje buton buy dhe nje JComboBox me ngjyra per ngjyren ne te njejtin rresht
    private void addTrailRow(GridBagConstraints gbc, int row) {
        // Panel i brendshem qe mban butonin dhe combobox-in krah per krah
        JPanel trailRow = new JPanel(new BorderLayout(10, 0));
        trailRow.setOpaque(false);

        // Butoni i blerjes se trail-it
        JButton buyBtn = new JButton("🎨  Trail  [2000💵]");
        buyBtn.setFont(new Font("SansSerif", Font.BOLD, 15));
        buyBtn.setForeground(Color.WHITE);
        buyBtn.setBackground(new Color(30, 30, 60));
        buyBtn.setFocusPainted(false);
        buyBtn.setBorderPainted(false);

        // Opsionet e ngjyrave me emrat e tyre — perdoren per matching me currentTrail
        String[] trailOptions = {"🔴 Red", "🔵 Blue", "🟢 Green", "🟣 Purple", "🌈 Rainbow", "✕ None"};
        JComboBox<String> colorPicker = new JComboBox<>(trailOptions);
        colorPicker.setFont(new Font("SansSerif", Font.BOLD, 13));
        colorPicker.setBackground(new Color(40, 40, 80));
        colorPicker.setForeground(Color.WHITE);
        colorPicker.setFocusable(false);
        colorPicker.setPreferredSize(new Dimension(130, 34));

        // Renderer custom — jep cdo opsion ngjyren e vet si background dhe foreground
        // Renderer custom — jep cdo opsion ngjyren e vet si background dhe foreground
        colorPicker.setRenderer(new BasicComboBoxRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                String text = value == null ? "" : value.toString();

                // Ngjyra e background-it per cdo opsion
                Color bg;
                if      (text.contains("Red"))     bg = new Color(120, 20,  20);
                else if (text.contains("Blue"))    bg = new Color(20,  50,  140);
                else if (text.contains("Green"))   bg = new Color(20,  100, 30);
                else if (text.contains("Purple"))  bg = new Color(80,  20,  130);
                else if (text.contains("Rainbow")) bg = new Color(40,  10,  60);
                else                               bg = new Color(35,  35,  35);

                // Ngjyra e tekstit — e ndritshme per cdo lloj
                Color fg;
                if      (text.contains("Red"))     fg = new Color(255, 100, 100);
                else if (text.contains("Blue"))    fg = new Color(100, 180, 255);
                else if (text.contains("Green"))   fg = new Color(80,  230, 100);
                else if (text.contains("Purple"))  fg = new Color(200, 100, 255);
                else if (text.contains("Rainbow")) fg = new Color(255, 200, 80);
                else                               fg = new Color(180, 180, 180);

                setBackground(isSelected ? bg.brighter() : bg);
                setForeground(fg);
                setFont(new Font("SansSerif", Font.BOLD, 13));
                setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                return this;
            }
        });

        // Kur klikohet butoni, zbritet çmimi dhe aplikohet ngjyra e zgjedhur
        buyBtn.addActionListener(e -> {
            String selected = (String) colorPicker.getSelectedItem();

            // "None" eshte falas — nuk zbritet para
            boolean isFree = selected != null && selected.contains("None");
            int cost = isFree ? 0 : 2000;

            if (MainFrame.totalMoney >= cost) {
                MainFrame.totalMoney -= cost;
                audioManager.playSFX("assets/succsesPurchase.wav");
                SaveManager.save(MainFrame.highScore, MainFrame.totalMoney);
                moneyLabel.setText("💰  " + MainFrame.totalMoney + " coins");

                // Aplikojme ngjyren e zgjedhur nga combobox
                assert selected != null;
                if      (selected.contains("Red"))     GamePanel.currentTrail = "RED";
                else if (selected.contains("Blue"))    GamePanel.currentTrail = "BLUE";
                else if (selected.contains("Green"))   GamePanel.currentTrail = "GREEN";
                else if (selected.contains("Purple"))  GamePanel.currentTrail = "PURPLE";
                else if (selected.contains("Rainbow")) GamePanel.currentTrail = "RAINBOW";
                else                                   GamePanel.currentTrail = "NONE";

            } else {
                audioManager.playSFX("assets/failedPurchase.wav");
                JOptionPane.showMessageDialog(this,
                        "Ju nuk keni mjaftueshem para! Të duhen " + cost + " monedha.",
                        "Shop", JOptionPane.WARNING_MESSAGE);
            }
        });

        trailRow.add(buyBtn, BorderLayout.CENTER);
        trailRow.add(colorPicker, BorderLayout.EAST);

        gbc.gridy = row;
        add(trailRow, gbc);
    }

    //Metoda per te bere refrash labelin e totalit te monedhave
    public void refresh() {
        moneyLabel.setText("💰  " + MainFrame.totalMoney + " coins");
    }

    private void addSectionLabel(String text, GridBagConstraints gbc, int row) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("Monospaced", Font.BOLD, 13));
        lbl.setForeground(new Color(150, 150, 200));
        gbc.gridy = row;
        add(lbl, gbc);
    }

    private void addShopButton(String text, int cost, Runnable onBuy,
                               GridBagConstraints gbc, int row) {
        String label = cost > 0 ? text + "  [" + cost + "💵]" : text + "  [FREE]";
        JButton btn = new JButton(label);
        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(30, 30, 60));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);

        btn.addActionListener(e -> {
            if (MainFrame.totalMoney >= cost) {
                MainFrame.totalMoney -= cost;
                audioManager.playSFX("assets/succsesPurchase.wav");
                SaveManager.save(MainFrame.highScore, MainFrame.totalMoney);
                onBuy.run();  // ekzekuton: currentMap = "SNOW" etj.
                moneyLabel.setText("💰  " + MainFrame.totalMoney + " coins");
            } else {
                audioManager.playSFX("assets/failedPurchase.wav");
                JOptionPane.showMessageDialog(this,
                        "Ju nuk keni mjaftueshem para! Të duhen " + cost + " monedha.",
                        "Shop", JOptionPane.WARNING_MESSAGE);
            }
        });

        gbc.gridy = row;
        add(btn, gbc);
    }
}