import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    // The layout manager that handles switching
    private CardLayout cardLayout;

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
        dispose();                          // lëshon window-in aktual
        setUndecorated(isFullscreen);       // heq title bar në fullscreen
        if (isFullscreen) {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        } else {
            setExtendedState(JFrame.NORMAL);
            pack();
            setLocationRelativeTo(null);
        }
        setVisible(true);
    }

    public MainFrame() {
        setTitle("Neon Highway");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        cardLayout     = new CardLayout();
        mainContainer  = new JPanel(cardLayout);  // container uses CardLayout

        // Create screens — pass 'this' so panels can call switchTo()
        MenuPanel menuPanel = new MenuPanel(this);
        GamePanel gamePanel = new GamePanel();

        // Add each panel with its unique string key
        // Think of it as: cardLayout.register("MENU", menuPanel)
        mainContainer.add(menuPanel, MENU);
        mainContainer.add(gamePanel, GAME);

        add(mainContainer);   // frame holds just the one container
        pack();
        setLocationRelativeTo(null);
    }

    /** Called by any panel that wants to switch screens */
    public void switchTo(String key) {

        cardLayout.show(mainContainer, key);

        // If switching to the game, request focus so KeyListener works
        if (key.equals(GAME)) {
            // small delay lets the card finish painting before grabbing focus
            SwingUtilities.invokeLater(() ->
                    mainContainer.getComponent(1).requestFocusInWindow()
            );
        }
    }

    public static void main(String[] args) {
        FlatDarkLaf.setup(); // ← shto këtë rresht
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}