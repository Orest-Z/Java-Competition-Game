import javax.swing.*;
import java.awt.*;

public class LeaderboardDialog extends JDialog {

    public LeaderboardDialog(JFrame parent) {
        super(parent, "🏆  Leaderboard", true);
        setSize(380, 420);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(new Color(10, 10, 20));
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("🏆  TOP 10", SwingConstants.CENTER);
        title.setFont(new Font("Monospaced", Font.BOLD, 22));
        title.setForeground(new Color(255, 215, 0));
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));
        add(title, BorderLayout.NORTH);

        JLabel loadingLabel = new JLabel("Duke ngarkuar...", SwingConstants.CENTER);
        loadingLabel.setForeground(new Color(150, 150, 200));
        loadingLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));
        add(loadingLabel, BorderLayout.CENTER);

        JButton closeBtn = new JButton("✕  MBYLL");
        closeBtn.setFont(new Font("Monospaced", Font.BOLD, 13));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setBackground(new Color(50, 50, 80));
        closeBtn.setFocusPainted(false);
        closeBtn.setBorderPainted(false);
        closeBtn.addActionListener(e -> dispose());
        JPanel south = new JPanel();
        south.setBackground(new Color(10, 10, 20));
        south.add(closeBtn);
        add(south, BorderLayout.SOUTH);

        new Thread(() -> {
            String[][] data = FirebaseManager.getLeaderboard();
            SwingUtilities.invokeLater(() -> {
                remove(loadingLabel);

                if (data.length == 0) {
                    JLabel empty = new JLabel("Nuk ka ende lojtarë!", SwingConstants.CENTER);
                    empty.setForeground(new Color(150, 150, 200));
                    empty.setFont(new Font("Monospaced", Font.PLAIN, 14));
                    add(empty, BorderLayout.CENTER);
                } else {
                    JPanel listPanel = new JPanel();
                    listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
                    listPanel.setBackground(new Color(10, 10, 20));

                    Color[] rankColors = {
                            new Color(255, 215, 0),
                            new Color(192, 192, 192),
                            new Color(205, 127, 50),
                    };
                    String[] medals = {"🥇", "🥈", "🥉"};

                    for (int i = 0; i < data.length; i++) {
                        String name  = data[i][0];
                        String score = data[i][1];

                        JPanel row = new JPanel(new BorderLayout());
                        row.setBackground(i % 2 == 0
                                ? new Color(20, 20, 35)
                                : new Color(15, 15, 28));
                        row.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
                        row.setMaximumSize(new Dimension(380, 45));

                        String prefix = i < 3 ? medals[i] + " " : (i + 1) + ".  ";
                        JLabel nameLabel = new JLabel(prefix + name);
                        nameLabel.setFont(new Font("Monospaced", Font.BOLD, 15));
                        nameLabel.setForeground(i < 3 ? rankColors[i] : Color.WHITE);

                        JLabel scoreLabel = new JLabel(score + " pts");
                        scoreLabel.setFont(new Font("Monospaced", Font.BOLD, 15));
                        scoreLabel.setForeground(new Color(0, 200, 255));

                        row.add(nameLabel, BorderLayout.WEST);
                        row.add(scoreLabel, BorderLayout.EAST);
                        listPanel.add(row);
                    }

                    JScrollPane scroll = new JScrollPane(listPanel);
                    scroll.setBorder(null);
                    scroll.getViewport().setBackground(new Color(10, 10, 20));
                    add(scroll, BorderLayout.CENTER);
                }
                revalidate();
                repaint();
            });
        }).start();

        setVisible(true);
    }
}