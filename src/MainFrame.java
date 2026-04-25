import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    // The layout manager that handles switching
    private CardLayout cardLayout;

    private GamePanel gamePanel;

    // The single container that holds ALL screens
    private JPanel mainContainer;

    private ShopPanel shopPanel;

    // String keys — CardLayout uses these to identify which card to show
    public static final String MENU  = "MENU";
    public static final String GAME  = "GAME";

    //Metoda per ta kthyer lojen ne full screen
    private boolean isFullscreen = false;

    // Deklarojme klasen AudioManager
    private AudioManager audioManager;

    // Ekonomia dhe progresi
    public static int highScore   = 0;
    public static int totalMoney  = 0;

    // Zgjedhjet e Shop
    public static String currentMap  = "DEFAULT";  // "DEFAULT", "SNOW", "DESERT"
    public static String currentSkin = "NORMAL";   // "NORMAL", "POLICE", "MOTO"

    public boolean isFullscreen() { return isFullscreen; }

    public void toggleFullscreen() {
        //Rregullova nje bug ku pas klikimit te butonit apply makina del posht enemy cars
        // dhe loja nuk perfundonte kurre
        mainContainer.revalidate();
        mainContainer.repaint();
        isFullscreen = !isFullscreen;
        dispose();
        setUndecorated(isFullscreen);
        if (isFullscreen) {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        } else {
            setExtendedState(JFrame.NORMAL);
        }
        setVisible(true);
        if (!isFullscreen) {
            pack();                      // ← pas setVisible, jo para
            setLocationRelativeTo(null);
        }
    }
    //Getters per audion qe te aksesohet nga panelet e tjera
    public AudioManager getAudioManager() { return audioManager; }

    public MainFrame() {
        //Ngarkojme te dhenat nga skedari me load
        int[] saveData = SaveManager.load();
        highScore  = saveData[0];
        totalMoney = saveData[1];

        setTitle("Neon Highway");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);


        //Bejme krijojme nje objekt te klases audio manager
        audioManager = new AudioManager();
        //Bejme load muziken tone specifike ne folderin ku ndodhet
        audioManager.loadMusic("assets/gameMusic.wav");
        audioManager.playMusic();

        cardLayout    = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        MenuPanel menuPanel = new MenuPanel(this);
        gamePanel = new GamePanel(this);

        // Wrapper i zi që centron GamePanel në fullscreen
        JPanel gameWrapper = new JPanel(new GridBagLayout());
        gameWrapper.setBackground(Color.BLACK);
        gameWrapper.add(gamePanel, new GridBagConstraints());

        JPanel menuWrapper = new JPanel(new GridBagLayout());
        menuWrapper.setBackground(Color.BLACK);
        menuWrapper.add(menuPanel, new GridBagConstraints());
        mainContainer.add(menuWrapper, MENU);
        mainContainer.add(gameWrapper, GAME);

        //Inicializohet nje objekt i panelit te dyqanit qe u shtua
        shopPanel = new ShopPanel(this);
        JPanel shopWrapper = new JPanel(new GridBagLayout());
        shopWrapper.setBackground(Color.BLACK);
        shopWrapper.add(shopPanel, new GridBagConstraints());
        mainContainer.add(shopWrapper, "SHOP");

        add(mainContainer);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                audioManager.cleanup();
            }
        });
        pack();
        setLocationRelativeTo(null);
    }

    /** Called by any panel that wants to switch screens */
    public void switchTo(String key) {
        cardLayout.show(mainContainer, key);

        if (key.equals(GAME)) {
            SwingUtilities.invokeLater(() -> {
                gamePanel.revalidate();
                gamePanel.requestFocusInWindow();
                gamePanel.startGame();  // ← thirr direkt, mos u mbështet në ComponentListener
            });
        }
        //Nqs klikohet menu luhet muzika by default
        if (key.equals(MENU)) {
            audioManager.playMusic();  // ← shto këtë
        }
        //Nqs klikohet shop behet refresh paneli
        if (key.equals("SHOP")) {
            shopPanel.refresh();  // ← rifresohet çdo herë që hapet
        }
    }

    public static void main(String[] args) {
        FlatDarkLaf.setup(); // ← shto këtë rresht
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}