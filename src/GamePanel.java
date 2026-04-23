import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * GamePanel is the main canvas for the car dodging game.
 *
 * Responsibilities:
 *  - Renders the road, player car, and HUD via paintComponent / Graphics2D
 *  - Handles keyboard input (LEFT / RIGHT arrow keys) via KeyListener
 *  - Runs a fixed-timestep game loop via javax.swing.Timer (~60 FPS)
 *  - Enforces window-boundary clamping so the car never leaves the road
 */
public class GamePanel extends JPanel implements KeyListener {

    // ── Panel / road dimensions ──────────────────────────────────────────────
    private static final int PANEL_WIDTH  = 480;
    private static final int PANEL_HEIGHT = 600;

    private static final int ROAD_LEFT  = 40;   // left edge of drivable road
    private static final int ROAD_RIGHT = 440;  // right edge of drivable road

    // ── Player car properties ────────────────────────────────────────────────
    static final int CAR_WIDTH   = 40;
    static final int CAR_HEIGHT  = 70;
    private static final int MOVE_SPEED  = 6;   // pixels per frame

    /** Top-left X of the player car (Y is fixed near the bottom). */
    private int carX;
    private final int carY;

    //Lista per enemy Cars
    private ArrayList<EnemyCar> enemies = new ArrayList<>();
    private int spawnTimer = 0;


    //Shtimi per sistemin e pikeve dhe niveleve te lojes(dita 4)
    private int score        = 0;   // rritet çdo frame
    private int level        = 1;   // niveli aktual
    private int enemySpeed   = 4;   // fillon me 4, rritet me level

    // ── Input state ──────────────────────────────────────────────────────────
    private boolean movingLeft  = false;
    private boolean movingRight = false;

    //GamePanel duhet ta njohe mainframe ne menyre qe kur loja te mbaroje te ket mundesi te kthehet tek ai me buton
    private MainFrame mainFrame;

    // Keto 2 variabla do na duhen per diten e neserme pasi te shtoj dhe muziken dhe sound effects
    public static boolean musicEnabled  = true;
    public static boolean sfxEnabled    = true;

    //Boolean per te percaktuar nqfs loja ka mbaruar ose jo
    private boolean gameOver = false;

    //Buton per te restartuar lojen
    private JButton restartButton;

    //Butoni per tu kthyer ne menune kryesore
    private JButton menuButton;

    // ── Road lane-marker animation ───────────────────────────────────────────
    private int laneMarkerOffset = 0;   // scrolls downward each tick

    // ── Game loop timer (~60 FPS) ────────────────────────────────────────────
    private Timer gameTimer;

    // ── Colors ───────────────────────────────────────────────────────────────
    private static final Color COLOR_GRASS      = new Color(34,  139, 34);
    private static final Color COLOR_ROAD       = new Color(50,  50,  50);
    private static final Color COLOR_KERB       = new Color(220, 220, 220);
    private static final Color COLOR_LANE_MARK  = new Color(255, 220, 0);
    private static final Color COLOR_CAR_BODY   = new Color(220, 40,  40);
    private static final Color COLOR_CAR_ROOF   = new Color(180, 30,  30);
    private static final Color COLOR_WINDOW     = new Color(160, 220, 255, 200);
    private static final Color COLOR_TYRE       = new Color(20,  20,  20);
    private static final Color COLOR_HEADLIGHT  = new Color(255, 255, 180);

    // ─────────────────────────────────────────────────────────────────────────

    // Ngjyrat e mundshme për makinat enemy
    private static final Color[] ENEMY_COLORS = {
            new Color(30,  144, 255),  // blu
            new Color(255, 165,   0),  // portokalli
            new Color(50,  205,  50),  // gjelbër
            new Color(148,   0, 211),  // vjollcë
            new Color(255, 215,   0),  // ari
    };


    public GamePanel(MainFrame mainFrame) {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(COLOR_GRASS);// Required to receive key events;
        setFocusable(true);
        addKeyListener(this);

        // Krijo butonin por fshihe — shfaqet vetëm pas Game Over
        restartButton = new JButton("↺  RESTART");
        restartButton.setFont(new Font("Monospaced", Font.BOLD, 16));
        restartButton.setForeground(Color.WHITE);
        restartButton.setBackground(new Color(180, 30, 30));
        restartButton.setFocusPainted(false);
        restartButton.setBorderPainted(false);
        restartButton.setVisible(false);  // i fshehur në fillim

        restartButton.addActionListener(e -> restartGame());

// Pozicionoje në qendër të panelit
        setLayout(null);  // layout manual
        restartButton.setBounds(
                PANEL_WIDTH / 2 - 80,   // x: i centruar
                PANEL_HEIGHT / 2 + 40,  // y: poshtë tekstit GAME OVER
                160,                     // gjerësia
                40                       // lartësia
        );
        add(restartButton);

        /*Shtova nje buton te ri Menu i cili te kthen ne faqen e pare
        Ky buton eshte i ngjashem me restart game gjithashtu sepse
        i ben reset score dhe level por te jep mundesine qe te ndryshosh
        settings ose te dalesh nga loja*/
        menuButton = new JButton("⌂  MENU");
        menuButton.setFont(new Font("Monospaced", Font.BOLD, 16));
        menuButton.setForeground(Color.WHITE);
        menuButton.setBackground(new Color(30, 30, 100));
        menuButton.setFocusPainted(false);
        menuButton.setBorderPainted(false);
        menuButton.setVisible(false);

        menuButton.addActionListener(e -> {
            // Rivendos gjendjen e lojës
            gameOver   = false;
            score      = 0;
            level      = 1;
            enemySpeed = 4;
            spawnTimer = 0;
            enemies.clear();
            carX = (ROAD_LEFT + ROAD_RIGHT) / 2 - CAR_WIDTH / 2;

            // Fshih butonat
            restartButton.setVisible(false);
            menuButton.setVisible(false);

            // Kthehu te menu
            mainFrame.switchTo(MainFrame.MENU);
        });

        menuButton.setBounds(
                PANEL_WIDTH / 2 - 80,
                PANEL_HEIGHT / 2 + 90,  // ← poshtë butonit RESTART
                160, 40
        );
        add(menuButton);

        // Start car centered on the road, near the bottom
        carX = (ROAD_LEFT + ROAD_RIGHT) / 2 - CAR_WIDTH / 2;
        carY = PANEL_HEIGHT - CAR_HEIGHT - 30;

        // Game loop: fires ~60 times per second
        gameTimer = new Timer(16, e -> {
            update();
            repaint();
        });

    }

    /** Call after the window is visible so the panel can receive focus. */
    public void startGame() {
        requestFocusInWindow();
        if (!gameTimer.isRunning()) {
            gameTimer.start();
        }
    }

    //Metoda qe perdoret per te rifilluar lojen
    private void restartGame() {
        // Rivendos variablat
        gameOver    = false;
        spawnTimer  = 0;
        enemies.clear();  // fshi të gjitha makinat armike

        // Rivendos pozicionin e lojtarit në qendër
        carX = (ROAD_LEFT + ROAD_RIGHT) / 2 - CAR_WIDTH / 2;

        // Fshih butonin dhe rinis loop-in
        restartButton.setVisible(false);
        gameTimer.start();
        requestFocusInWindow();

        //Behet reset niveli dhe piket
        score       = 0;
        level       = 1;
        enemySpeed  = 4;
    }

    // ── Game logic update ─────────────────────────────────────────────────────

    private void update() {
        // Move car according to held keys
        if (movingLeft) carX -= MOVE_SPEED;
        if (movingRight) carX += MOVE_SPEED;

        // Clamp car so it stays on the road (accounting for car width)
        carX = Math.max(ROAD_LEFT, Math.min(carX, ROAD_RIGHT - CAR_WIDTH));



        // [2] Spawning — çdo 90 frame shto një makinë të re
        spawnTimer++;
        if (spawnTimer >= 90) {
            spawnTimer = 0;

            int totalLanes = 4;
            int laneWidth = (ROAD_RIGHT - ROAD_LEFT) / totalLanes;
            int randomLane = (int) (Math.random() * totalLanes);

            // Formula per spawnin e makinave te tjera
            int enemyX = ROAD_LEFT + (randomLane * laneWidth) + (laneWidth / 2) - (CAR_WIDTH / 2);
            int enemyY = -CAR_HEIGHT;        // ku duhet të shfaqet — sipër ekranit

            //update qe zgjidhet nje ngjyra random per makinat enemy sa here bejne spawn ne pozicione te ndryshme
            Color randomColor = ENEMY_COLORS[(int)(Math.random() * ENEMY_COLORS.length)];
            enemies.add(new EnemyCar(enemyX, enemyY, enemySpeed, randomColor));        }

        Iterator<EnemyCar> it = enemies.iterator();
        while (it.hasNext()) {
            EnemyCar enemy = it.next();
            enemy.update();
            if (enemy.y > PANEL_HEIGHT) {   // doli nga ekrani
                it.remove();                // ← e sigurt për heqje gjatë loop-it
            }
        }

        checkCollisions();

        // Scroll lane markers downward to create a sense of forward motion
        laneMarkerOffset = (laneMarkerOffset + 4) % 60;
        // Score rritet çdo frame që jeton
        score++;

        // Çdo 500 pikë, rritet niveli dhe shpejtësia
        if (score % 500 == 0) {
            level++;
            enemySpeed = Math.min(enemySpeed + 1, 12); // maksimumi 12
            spawnTimer = Math.max(spawnTimer - 5, 40); // spawn më shpesh, min 40 frame
        }
    }

    // ── Rendering ─────────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Smooth rendering
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,         RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,    RenderingHints.VALUE_STROKE_PURE);

        drawRoad(g2);
        drawPlayerCar(g2);
        drawEnemies(g2);
        drawHUD(g2);
    }

    /** Draws the road surface, kerb strips, and scrolling lane markers. */
    private void drawRoad(Graphics2D g2) {
        // Road surface
        g2.setColor(COLOR_ROAD);
        g2.fillRect(ROAD_LEFT, 0, ROAD_RIGHT - ROAD_LEFT, PANEL_HEIGHT);

        // Kerb stripes (left)
        drawKerb(g2, ROAD_LEFT - 12, 12);
        // Kerb stripes (right)
        drawKerb(g2, ROAD_RIGHT, 12);


        g2.setColor(COLOR_LANE_MARK);

        //Konfigurojmë penelin që të jetë me ndërprerje (dashed)

        float[] dashPattern = {30f, 30f};
        g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                10f, dashPattern, laneMarkerOffset));    // laneMarkerOffset ben qe vija te "levize"
                                                                    //.CAP_BUTT BUTT do te thote që vija pritet drejt

        //Ndertimi i 4 korsive  me ane te nje cikli for(update day2)
        int totalLanes = 4;
        int laneWidth = (ROAD_RIGHT - ROAD_LEFT) / totalLanes;
        for (int i = 1; i < totalLanes; i++) {
            int lineX = ROAD_LEFT + (i * laneWidth);
            g2.drawLine(lineX, 0, lineX, PANEL_HEIGHT);
        }

        g2.setStroke(new BasicStroke(1f));

    }

    /** Draws alternating red/white kerb blocks along a vertical strip. */
    private void drawKerb(Graphics2D g2, int x, int width) {
        int blockHeight = 24;
        for (int y = -blockHeight; y < PANEL_HEIGHT + blockHeight; y += blockHeight) {
            int adjustedY = y + (laneMarkerOffset % blockHeight);
            boolean red = ((adjustedY / blockHeight) % 2 == 0);
            g2.setColor(red ? Color.RED : Color.WHITE);
            g2.fillRect(x, adjustedY, width, blockHeight);
        }
    }

    /**
     * Draws a stylised top-down car at (carX, carY).
     * Layers: tyres → body → roof → windows → headlights/tail-lights
     */
    private void drawPlayerCar(Graphics2D g2) {
        int x = carX;
        int y = carY;
        int w = CAR_WIDTH;
        int h = CAR_HEIGHT;

        // ── Tyres (four corners) ────────────────────────────────────────────
        g2.setColor(COLOR_TYRE);
        int tyreW = 8, tyreH = 16;
        g2.fillRoundRect(x - tyreW + 2,       y + 6,           tyreW, tyreH, 3, 3); // front-left
        g2.fillRoundRect(x + w - 2,            y + 6,           tyreW, tyreH, 3, 3); // front-right
        g2.fillRoundRect(x - tyreW + 2,        y + h - 6 - tyreH, tyreW, tyreH, 3, 3); // rear-left
        g2.fillRoundRect(x + w - 2,             y + h - 6 - tyreH, tyreW, tyreH, 3, 3); // rear-right

        // ── Car body ─────────────────────────────────────────────────────────
        g2.setColor(COLOR_CAR_BODY);
        g2.fillRoundRect(x, y, w, h, 10, 10);

        // ── Roof (slightly inset, darker) ─────────────────────────────────
        g2.setColor(COLOR_CAR_ROOF);
        int roofInset = 6;
        int roofTop   = y + (int)(h * 0.25);
        int roofH     = (int)(h * 0.45);
        g2.fillRoundRect(x + roofInset, roofTop, w - roofInset * 2, roofH, 6, 6);

        // ── Windscreen (front) ────────────────────────────────────────────
        g2.setColor(COLOR_WINDOW);
        int wsInset = roofInset + 3;
        g2.fillRoundRect(x + wsInset, roofTop + 4, w - wsInset * 2, (int)(roofH * 0.4), 4, 4);

        // ── Rear window ──────────────────────────────────────────────────
        g2.fillRoundRect(x + wsInset, roofTop + (int)(roofH * 0.55), w - wsInset * 2, (int)(roofH * 0.38), 4, 4);

        // ── Headlights ────────────────────────────────────────────────────
        g2.setColor(COLOR_HEADLIGHT);
        int lightW = 10, lightH = 5;
        g2.fillRoundRect(x + 4,          y + 4, lightW, lightH, 3, 3);
        g2.fillRoundRect(x + w - 4 - lightW, y + 4, lightW, lightH, 3, 3);

        // ── Tail-lights (red) ────────────────────────────────────────────
        g2.setColor(new Color(255, 60, 60));
        g2.fillRoundRect(x + 4,               y + h - 4 - lightH, lightW, lightH, 3, 3);
        g2.fillRoundRect(x + w - 4 - lightW,  y + h - 4 - lightH, lightW, lightH, 3, 3);

        // ── Body outline ──────────────────────────────────────────────────
        g2.setColor(new Color(0, 0, 0, 60));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(x, y, w, h, 10, 10);
        g2.setStroke(new BasicStroke(1f));
    }
    //Ndryshova metoden e vizatimit te enemy cars duke ja lene ate klases me vete tek EnemyCar.java
    private void drawEnemies(Graphics2D g2) {
        for (EnemyCar enemy : enemies) {
            enemy.draw(g2);
        }
    }

    /** Draws a simple HUD showing the control hint. */
    private void drawHUD(Graphics2D g2) {
        g2.setColor(new Color(255, 255, 255, 160));
        g2.setFont(new Font("Monospaced", Font.BOLD, 12));
        g2.drawString("← → ARROW KEYS to move", ROAD_LEFT + 8, PANEL_HEIGHT - 10);

        //Ndertohet nje HUD i ri per te treguar me tekst numrin e pikeve dhe nivelin qe ndodhet lojtari
        g2.setColor(new Color(255, 255, 255, 200));
        g2.setFont(new Font("Monospaced", Font.BOLD, 14));
        g2.drawString("SCORE: " + score,           ROAD_LEFT + 8, 24);
        g2.drawString("LEVEL: " + level,  ROAD_RIGHT - 80,        24);

        //Nqs mbaron loja shfaqim me ngjyre te kuqe mesazhin game over
        if (gameOver) {
            g2.setColor(Color.RED);
            g2.setFont(new Font("Monospaced", Font.BOLD, 36));
            g2.drawString("GAME OVER", 150, PANEL_HEIGHT / 2);
        }
    }

        private void checkCollisions() {
            Rectangle playerHitbox = new Rectangle(
                    carX + 5,           // inset 5px nga e majta
                    carY + 5,           // inset 5px nga sipër
                    CAR_WIDTH - 10,     // më i ngushtë se vizuali
                    CAR_HEIGHT - 10
            );

            for (EnemyCar enemy : enemies) {
                if (playerHitbox.intersects(enemy.hitbox)) {
                    gameOver = true;
                    gameTimer.stop();
                    restartButton.setVisible(true);
                    menuButton.setVisible(true);  // ← shto këtë
                }
            }
        }
    // ── KeyListener ──────────────────────────────────────────────────────────

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT  -> movingLeft  = true;
            case KeyEvent.VK_RIGHT -> movingRight = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT  -> movingLeft  = false;
            case KeyEvent.VK_RIGHT -> movingRight = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) { /* unused */ }
}
