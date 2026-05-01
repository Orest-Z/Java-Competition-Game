import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

public class EntityManager {

    //Lista per enemy Cars
    ArrayList<EnemyCar> enemies = new ArrayList<>();
    int spawnTimer = 0;

    //Variablat per coins
    int coinSpawnTimer = 0;
    ArrayList<Coin> coins = new ArrayList<>();
    private final int COIN_SPAWN_INTERVAL = 60;

    // ── Variablat per mburojen(shield) ───────────────────────────────────────────────────────
    ArrayList<Shield> shields = new ArrayList<>();
    int shieldSpawnTimer  = 0;
    static final int SHIELD_SPAWN_INTERVAL = 300; // spawn çdo ~5 sekonda (60fps × 5)

    boolean shieldActive  = false;   // a është aktiv shield-i
    int     shieldTimer   = 0;       // sa frame mbetet aktiv (300 = 5 sek)
    static final int SHIELD_DURATION = 300; // 5 sekonda × 60fps
    float   shieldPulse   = 0f;      // për animacionin e halo-s rreth makinës

    //ArrayLista per particles
    ArrayList<Particle> particles = new ArrayList<>();

    // Ngjyrat e mundshme për makinat enemy
    private static final Color[] ENEMY_COLORS = {
            new Color(30,  144, 255),  // blu
            new Color(255, 165,   0),  // portokalli
            new Color(50,  205,  50),  // gjelbër
            new Color(148,   0, 211),  // vjollcë
            new Color(255, 215,   0),  // ari
    };

    private static final int ROAD_LEFT  = 40;
    private static final int ROAD_RIGHT = 440;
    private static final int PANEL_HEIGHT = 600;

    //Metoda per spawnin e monedhave
    void spawnCoin() {
        int totalLanes = 4;
        int laneWidth  = (ROAD_RIGHT - ROAD_LEFT) / totalLanes;
        int coinSize   = 28;

        // Provoj lane te ndryshme derisa gjej nje qe nuk ka enemy
        // Shuffle lanes per randomness
        int[] lanes = {0, 1, 2, 3};
        for (int i = 3; i > 0; i--) {
            int j = (int)(Math.random() * (i + 1));
            int tmp = lanes[i]; lanes[i] = lanes[j]; lanes[j] = tmp;
        }

        for (int lane : lanes) {
            int coinX = ROAD_LEFT + (lane * laneWidth) + (laneWidth / 2) - (coinSize / 2);
            int coinY = -coinSize; // spawno jashte ekranit siper

            // Kontrolloj qe asnje enemy nuk eshte ne kete lane afersisht
            boolean laneClear = true;
            for (EnemyCar enemy : enemies) {
                // Nese enemy eshte ne te njejten X zone dhe afert siper
                if (Math.abs(enemy.x - coinX) < laneWidth && enemy.y < 200) {
                    laneClear = false;
                    break;
                }
            }

            if (laneClear) {
                coins.add(new Coin(coinX, coinY));
                break; // spawno vetem nje coin per here
            }
        }
    }

    //Metoda per spawnin e mburojave
    void spawnShield() {
        int totalLanes = 4;
        int laneWidth  = (ROAD_RIGHT - ROAD_LEFT) / totalLanes;
        int shieldSize = 34;

        int[] lanes = {0, 1, 2, 3};
        for (int i = 3; i > 0; i--) {
            int j = (int)(Math.random() * (i + 1));
            int tmp = lanes[i]; lanes[i] = lanes[j]; lanes[j] = tmp;
        }

        for (int lane : lanes) {
            int sx = ROAD_LEFT + (lane * laneWidth) + (laneWidth / 2) - (shieldSize / 2);
            int sy = -shieldSize;

            boolean laneClear = true;
            for (EnemyCar enemy : enemies) {
                if (Math.abs(enemy.x - sx) < laneWidth && enemy.y < 200) {
                    laneClear = false;
                    break;
                }
            }
            if (laneClear) {
                shields.add(new Shield(sx, sy));
                break;
            }
        }
    }

    //Metoda per spawnin e Particles
    void spawnParticles(int x, int y) {
        for (int i = 0; i < 25; i++) {  // 25 grimca
            particles.add(new Particle(x + GamePanel.CAR_WIDTH / 2f, y + GamePanel.CAR_HEIGHT / 2f));
        }
    }

    // Spawnim i makinave armike sipas nivelit
    void spawnEnemies(int level, int enemySpeed) {
        int totalLanes = 4;
        int laneWidth  = (ROAD_RIGHT - ROAD_LEFT) / totalLanes;

        // Zgjedh korsi të ndryshme — shuffle i thjeshtë
        int[] lanes = {0, 1, 2, 3};
        for (int i = 3; i > 0; i--) {
            int j = (int)(Math.random() * (i + 1));
            int tmp = lanes[i]; lanes[i] = lanes[j]; lanes[j] = tmp;
        }

        //Ndryshova menyren e mundesive qe ka qe te beje spawn me shume se 1 makine pernjeheresh
        int carsToSpawn = 1;
        if (level >= 3 && Math.random() < 0.25 + (level * 0.04)) carsToSpawn = 2;
        if (level >= 6 && Math.random() < 0.15 + (level * 0.02)) carsToSpawn = 3;

        // Cap at 3 so it's always physically possible to dodge
        carsToSpawn = Math.min(carsToSpawn, 3);

        for (int i = 0; i < carsToSpawn; i++) {
            int enemyX = ROAD_LEFT + (lanes[i] * laneWidth) + (laneWidth / 2) - (GamePanel.CAR_WIDTH / 2);
            Color randomColor = ENEMY_COLORS[(int)(Math.random() * ENEMY_COLORS.length)];
            enemies.add(new EnemyCar(enemyX, -GamePanel.CAR_HEIGHT, enemySpeed, randomColor));
        }
    }

    // Update i te gjitha entiteteve — thirret cdo frame nga GamePanel.update()
    void update(int carX, int carY, int level, int enemySpeed,
                AudioManager audioManager) {

        // ── Particles — vazhdojnë edhe pas gameOver ──
        Iterator<Particle> pit = particles.iterator();
        while (pit.hasNext()) {
            Particle p = pit.next();
            p.update();
            if (p.isDead()) pit.remove();
        }

        // ── Spawn dhe update enemies ──
        spawnTimer++;
        // Interval shrinks each level — floor of 18 frames so it never gets impossible
        int spawnInterval = Math.max(18, 40 - (level * 3));
        if (spawnTimer >= spawnInterval) {
            spawnTimer = 0;
            spawnEnemies(level, enemySpeed);
        }

        Iterator<EnemyCar> it = enemies.iterator();
        while (it.hasNext()) {
            EnemyCar enemy = it.next();
            enemy.update();    // ← lëviz poshtë
            if (enemy.y > PANEL_HEIGHT) it.remove();
        }

        // ── Spawn coins ───────────────────────────────────────
        coinSpawnTimer++;
        if (coinSpawnTimer >= COIN_SPAWN_INTERVAL) {
            coinSpawnTimer = 0;
            spawnCoin();
        }

        // ── Update dhe collect coins ──────────────────────────
        Iterator<Coin> cit = coins.iterator();
        while (cit.hasNext()) {
            Coin coin = cit.next();
            coin.update();
            if (coin.isOffScreen(PANEL_HEIGHT)) {
                cit.remove();
                continue;
            }
            // Collision me lojtarin
            Rectangle playerHitbox = new Rectangle(carX + 5, carY + 5,
                    GamePanel.CAR_WIDTH - 10, GamePanel.CAR_HEIGHT - 10);
            if (playerHitbox.intersects(coin.hitbox)) {
                MainFrame.totalMoney += 10; // 10 monedha per coin
                audioManager.playSFX("assets/click.wav"); // ose nje sfx te vecante
                SaveManager.save(MainFrame.highScore, MainFrame.totalMoney);
                cit.remove();
            }
        }

        // ── Spawn shields ─────────────────────────────────────────────────────
        shieldSpawnTimer++;
        if (shieldSpawnTimer >= SHIELD_SPAWN_INTERVAL) {
            shieldSpawnTimer = 0;
            spawnShield();
        }

        // ── Menyra e spawnit te mburojave e ngjashme me coins por pak me rralle──────────
        Iterator<Shield> sit = shields.iterator();
        while (sit.hasNext()) {
            Shield shield = sit.next();
            shield.update();
            if (shield.isOffScreen(PANEL_HEIGHT)) {
                sit.remove();
                continue;
            }
            Rectangle playerHitbox = new Rectangle(carX + 5, carY + 5,
                    GamePanel.CAR_WIDTH - 10, GamePanel.CAR_HEIGHT - 10);
            if (playerHitbox.intersects(shield.hitbox)) {
                shieldActive = true;
                shieldTimer  = SHIELD_DURATION;
                audioManager.playSFX("assets/succsesPurchase.wav"); // ose shieldSFX.wav nëse ke
                sit.remove();
            }
        }

        // ── Shield countdown ──────────────────────────────────────────────────
        if (shieldActive) {
            shieldTimer--;
            shieldPulse = (float)(Math.sin(shieldTimer * 0.2f) * 0.5f + 0.5f); // 0.0 → 1.0
            if (shieldTimer <= 0) {
                shieldActive = false;
                shieldPulse  = 0f;
            }
        }
    }

    // Pastrim i plotë i të gjitha entiteteve — thirret nga startGame() dhe restartGame()
    void clearAll() {
        enemies.clear();
        particles.clear();
        coins.clear();
        coinSpawnTimer = 0;
        shields.clear();
        shieldSpawnTimer = 0;
        shieldActive     = false;
        shieldTimer      = 0;
        shieldPulse      = 0f;
        spawnTimer       = 0;
    }

    //Ndertova nje klase Particles e cila do perdoret per therrimet e vogla qe ndodhin pas perplasjes
    static class Particle {
        float x, y;          // pozicioni
        float vx, vy;        // shpejtesia (velocity x,y)
        int life;            // sa frame jeton
        Color color;

        Particle(float x, float y) {
            this.x = x;
            this.y = y;
            // Drejtim random në të gjitha anët
            double angle = Math.random() * Math.PI * 2;
            float speed  = (float)(Math.random() * 4 + 1);
            this.vx    = (float)(Math.cos(angle) * speed);
            this.vy    = (float)(Math.sin(angle) * speed);
            this.life  = (int)(Math.random() * 30 + 20); // 20-50 frame
            // Ngjyrë random mes të kuqes dhe portokallisë
            this.color = Math.random() > 0.5
                    ? new Color(255, 80, 0)
                    : new Color(255, 220, 0);
        }

        void update() {
            x    += vx;
            y    += vy;
            vy   += 0.15f;  // gravitet i lehtë
            life--;
        }

        boolean isDead() { return life <= 0; }
    }
}
