import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

public class Shield {
    int x, y;
    Rectangle hitbox;

    // Animacion bob (njësoj si Coin)
    private float bobOffset = 0f;
    private float bobTimer  = 0f;
    private static final float BOB_SPEED  = 0.06f;
    private static final float BOB_HEIGHT = 5f;

    private static final int SIZE = 34; // pak më i madh se coin

    // Imazhi ngarkohet një herë — static
    private static BufferedImage shieldImage;
    static {
        try {
            shieldImage = ImageIO.read(new File("assets/shieldImage.png"));
        } catch (Exception e) {
            System.out.println("Shield image error: " + e.getMessage());
            shieldImage = null;
        }
    }

    public Shield(int x, int y) {
        this.x = x;
        this.y = y;
        this.bobTimer = (float)(Math.random() * Math.PI * 2); // fillon random
        this.hitbox   = new Rectangle(x, y, SIZE, SIZE);
    }

    public void update() {
        y += 3; // lëviz poshtë me të njëjtën shpejtësi si coins
        bobTimer += BOB_SPEED;
        bobOffset = (float)(Math.sin(bobTimer) * BOB_HEIGHT);
        hitbox.setLocation(x, y);
    }

    public void draw(Graphics2D g2) {
        int drawY = (int)(y + bobOffset);

        // Halo/glow blu rreth shield-it
        for (int i = 4; i >= 1; i--) {
            float alpha = 0.06f * i;
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2.setColor(new Color(0, 180, 255));
            g2.fillOval(x - i * 3, drawY - i * 3, SIZE + i * 6, SIZE + i * 6);
        }
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

        // Shadow
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
        g2.setColor(Color.BLACK);
        g2.fillOval(x + 2, drawY + SIZE + 2, SIZE - 4, 6);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

        // Imazhi ose fallback
        if (shieldImage != null) {
            g2.drawImage(shieldImage, x, drawY, SIZE, SIZE, null);
        } else {
            // Fallback: rreth blu neon nëse mungon imazhi
            g2.setColor(new Color(0, 180, 255, 200));
            g2.fillOval(x, drawY, SIZE, SIZE);
            g2.setColor(new Color(255, 255, 255, 180));
            g2.setStroke(new BasicStroke(2.5f));
            g2.drawOval(x, drawY, SIZE, SIZE);
            g2.setStroke(new BasicStroke(1f));
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Monospaced", Font.BOLD, 18));
            g2.drawString("S", x + 10, drawY + 22);
        }
    }

    public boolean isOffScreen(int panelHeight) {
        return y > panelHeight;
    }
}