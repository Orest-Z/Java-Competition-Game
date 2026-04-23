import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    // The layout manager that handles switching
    private CardLayout cardLayout;

    private GamePanel gamePanel;

    // The single container that holds ALL screens
    private JPanel mainContainer;

    // String keys — CardLayout uses these to identify which card to show
    public static final String MENU  = "MENU";
    public static final String GAME  = "GAME";

    //Metoda per ta kthyer lojen ne full screen
    private boolean isFullscreen = false;

    public boolean isFullscreen() { return isFullscreen; }

    public void toggleFullscreen() {
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
    public MainFrame() {
        setTitle("Neon Highway");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        cardLayout    = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        MenuPanel menuPanel = new MenuPanel(this);
        gamePanel = new GamePanel();

        // Wrapper i zi që centron GamePanel në fullscreen
        JPanel gameWrapper = new JPanel(new GridBagLayout());
        gameWrapper.setBackground(Color.BLACK);
        gameWrapper.add(gamePanel, new GridBagConstraints());

        JPanel menuWrapper = new JPanel(new GridBagLayout());
        menuWrapper.setBackground(Color.BLACK);
        menuWrapper.add(menuPanel, new GridBagConstraints());
        mainContainer.add(menuWrapper, MENU);
        mainContainer.add(gameWrapper, GAME);

        add(mainContainer);
        pack();
        setLocationRelativeTo(null);
    }

    /** Called by any panel that wants to switch screens */
    public void switchTo(String key) {
        cardLayout.show(mainContainer, key);

        if (key.equals(GAME)) {
            SwingUtilities.invokeLater(() -> {
                gamePanel.requestFocusInWindow();
                gamePanel.startGame();  // ← thirr direkt, mos u mbështet në ComponentListener
            });
        }
    }

    public static void main(String[] args) {
        FlatDarkLaf.setup(); // ← shto këtë rresht
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}