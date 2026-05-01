import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

public class Coin {
    int x, y;
    Rectangle hitbox;

    // Animacion — levizje e vogel lart-posht (bob)
    private float bobOffset = 0f;
    private float bobTimer  = 0f;
    private static final float BOB_SPEED  = 0.08f; // sa shpejt leviz
    private static final float BOB_HEIGHT = 4f;    // sa pixel leviz lart-posht

    private static final int SIZE = 28; // madhesia e coins ne ekran

    //Getter per imazhin e monedhes
    public static BufferedImage getCoinImage() {
        return coinImage;
    }

    // Imazhi ngarkohet nje here per te gjithe coins — static
    private static BufferedImage coinImage;
    static {
        try {
            coinImage = ImageIO.read(new File("assets/coinImage.png"));
        } catch (Exception e) {
            System.out.println("Coin image error: " + e.getMessage());
            coinImage = null;
        }
    }

    public Coin(int x, int y) {
        this.x = x;
        this.y = y;
        // bobTimer fillon random per secilen monedhe — keshtu nuk levizin te gjitha njekohesisht
        this.bobTimer = (float)(Math.random() * Math.PI * 2);
        this.hitbox = new Rectangle(x, y, SIZE, SIZE);
    }

    public void update() {
        // Leviz poshte si enemy cars
        y += 3;

        // Bob animacion — sinus per levizje te bute lart-posht
        bobTimer += BOB_SPEED;
        bobOffset = (float)(Math.sin(bobTimer) * BOB_HEIGHT);

        hitbox.setLocation(x, y);
    }

    public void draw(Graphics2D g2) {
        int drawY = (int)(y + bobOffset); // pozicioni i animuar

        // ── Shadow — nje oval e shtypur nen monedhen ──────────
        int shadowW = SIZE - 4;
        int shadowH = 6;
        int shadowX = x + 2;
        int shadowY = drawY + SIZE + 2; // pak nen monedhen

        // Transparenca e shadows ndryshon me bobOffset — kur coin eshte lart, shadow eshte me e hapur
        float shadowAlpha = 0.35f - (bobOffset / BOB_HEIGHT) * 0.12f;
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                Math.max(0.1f, shadowAlpha)));
        g2.setColor(new Color(0, 0, 0));
        g2.fillOval(shadowX, shadowY, shadowW, shadowH);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f)); // reset

        // ── Imazhi i monudhes ─────────────────────────────────
        if (coinImage != null) {
            g2.drawImage(coinImage, x, drawY, SIZE, SIZE, null);
        } else {
            // Fallback nese imazhi nuk u ngarkua — vizato nje rreth te verdhe
            g2.setColor(new Color(255, 215, 0));
            g2.fillOval(x, drawY, SIZE, SIZE);
            g2.setColor(new Color(200, 160, 0));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawOval(x, drawY, SIZE, SIZE);
            g2.setStroke(new BasicStroke(1f));
        }
    }

    public boolean isOffScreen(int panelHeight) {
        return y > panelHeight;
    }
}