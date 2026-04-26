import javax.swing.*;
import java.awt.*;

public class ShopPanel extends JPanel {
    private final MainFrame mainFrame;
    private JLabel moneyLabel;
    private Image backgroundImage; //Variabli per imazhin

    public ShopPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        //Ngarkojme imazhin per background
        backgroundImage = new ImageIcon("assets/backgroundShop.png").getImage();
        setPreferredSize(new Dimension(480, 600));
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
        gbc.insets = new Insets(8, 30, 8, 30);

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
        addShopButton("🏙  Default",     0, () -> MainFrame.currentMap = "DEFAULT", gbc, 5);

        // ── Seksioni Skins ────────────────────────────────────
        addSectionLabel("── SKINS ──", gbc, 6);
        addShopButton("🚔  Police Car",   5000, () -> MainFrame.currentSkin = "POLICE", gbc, 7);
        addShopButton("🏍  Motorcycle",  7000, () -> MainFrame.currentSkin = "MOTO",   gbc, 8);
        addShopButton("🚗  Normal",        0, () -> MainFrame.currentSkin = "NORMAL", gbc, 9);

        // ── Butoni BACK ───────────────────────────────────────
        JButton backBtn = new JButton("←  BACK");
        backBtn.setFont(new Font("Monospaced", Font.BOLD, 14));
        backBtn.setForeground(Color.WHITE);
        backBtn.setBackground(new Color(50, 50, 80));
        backBtn.setFocusPainted(false);
        backBtn.setBorderPainted(false);
        backBtn.addActionListener(e -> mainFrame.switchTo(MainFrame.MENU));
        gbc.gridy = 10;
        add(backBtn, gbc);
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
                SaveManager.save(MainFrame.highScore, MainFrame.totalMoney);
                onBuy.run();  // ekzekuton: currentMap = "SNOW" etj.
                moneyLabel.setText("💰  " + MainFrame.totalMoney + " coins");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Ju nuk keni mjaftueshem para! Të duhen " + cost + " monedha.",
                        "Shop", JOptionPane.WARNING_MESSAGE);
            }
        });

        gbc.gridy = row;
        add(btn, gbc);
    }
}