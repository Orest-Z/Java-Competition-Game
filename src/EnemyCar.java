import java.awt.*;
import java.util.ArrayList;

public class EnemyCar {
    int x, y, speed;
    Rectangle hitbox;   // për collision detection

    public EnemyCar(int x, int y, int speed) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.hitbox = new Rectangle(x, y, GamePanel.CAR_WIDTH, GamePanel.CAR_HEIGHT);
    }

    public void update() {
        y += speed;                    // lëviz poshtë
        hitbox.setLocation(x, y);     // hitbox-i ndjek trupin

    }
}