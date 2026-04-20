import javax.swing.*;

/**
 * Entry point for the top-down car dodging game.
 * Run this class to launch the game window.
 */
public class CarDodgeGame {
    public static void main(String[] args) {
        // Run UI on the Event Dispatch Thread (Swing best practice)
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Car Dodge Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);

            // Create and add the game panel
            GamePanel gamePanel = new GamePanel();
            frame.add(gamePanel);

            // Pack the frame around the panel's preferred size
            frame.pack();
            frame.setLocationRelativeTo(null); // Center on screen
            frame.setVisible(true);

            // Start the game loop
            gamePanel.startGame();
        });
    }
}
