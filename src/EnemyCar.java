import java.awt.*;
import java.util.ArrayList;

public class EnemyCar {
    int x, y, speed;
    Rectangle hitbox;   // për collision detection
    Color bodyColor;
    public EnemyCar(int x, int y, int speed, Color bodyColor) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.bodyColor = bodyColor;
        this.hitbox = new Rectangle(x - 2, y + 4, GamePanel.CAR_WIDTH + 4, GamePanel.CAR_HEIGHT - 8);
    }

    //Shtova metoden draw qe i vizaton makinat enemy te ngjashme si metoda e vizatimit te makines se lojtarit
    public void draw(Graphics2D g2) {
        int w = GamePanel.CAR_WIDTH;
        int h = GamePanel.CAR_HEIGHT;

        // ── Gomat (4 këndet) ─────────────────────────────────
        g2.setColor(new Color(20, 20, 20));
        int tyreW = 8, tyreH = 16;
        g2.fillRoundRect(x - tyreW + 2, y + 6,              tyreW, tyreH, 3, 3); // para-majtas
        g2.fillRoundRect(x + w - 2,     y + 6,              tyreW, tyreH, 3, 3); // para-djathtas
        g2.fillRoundRect(x - tyreW + 2, y + h - 6 - tyreH, tyreW, tyreH, 3, 3); // prapa-majtas
        g2.fillRoundRect(x + w - 2,     y + h - 6 - tyreH, tyreW, tyreH, 3, 3); // prapa-djathtas

        // ── Trupi i makinës ───────────────────────────────────
        g2.setColor(bodyColor);
        g2.fillRoundRect(x, y, w, h, 10, 10);

        // ── Çatia (më e errët se trupi) ───────────────────────
        g2.setColor(bodyColor.darker());
        int roofInset = 6;
        int roofTop   = y + (int)(h * 0.25);
        int roofH     = (int)(h * 0.45);
        g2.fillRoundRect(x + roofInset, roofTop, w - roofInset * 2, roofH, 6, 6);

        // ── Xhamat ────────────────────────────────────────────
        g2.setColor(new Color(160, 220, 255, 180));
        int wsInset = roofInset + 3;
        g2.fillRoundRect(x + wsInset, roofTop + 4,
                w - wsInset * 2, (int)(roofH * 0.4), 4, 4);
        g2.fillRoundRect(x + wsInset, roofTop + (int)(roofH * 0.55),
                w - wsInset * 2, (int)(roofH * 0.38), 4, 4);

        // ── Fenerët e pasëm (të kuq — armiku vjen nga lart) ──
        g2.setColor(new Color(255, 60, 60));
        int lightW = 10, lightH = 5;
        g2.fillRoundRect(x + 4,              y + h - 4 - lightH, lightW, lightH, 3, 3);
        g2.fillRoundRect(x + w - 4 - lightW, y + h - 4 - lightH, lightW, lightH, 3, 3);

        // ── Fenerët e përparmë (të bardhë/të verdhë) ─────────
        g2.setColor(new Color(255, 255, 180));
        g2.fillRoundRect(x + 4,              y + 4, lightW, lightH, 3, 3);
        g2.fillRoundRect(x + w - 4 - lightW, y + 4, lightW, lightH, 3, 3);

        // ── Konturi i trupit ──────────────────────────────────
        g2.setColor(new Color(0, 0, 0, 60));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(x, y, w, h, 10, 10);
        g2.setStroke(new BasicStroke(1f));
    }


    public void update() {
        y += speed;                    // lëviz poshtë
        hitbox.setLocation(x, y);     // hitbox-i ndjek trupin

    }
}