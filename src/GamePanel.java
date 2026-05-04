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

    //Variablet per scenery ne krah te rruges
    private int sceneryOffset1 = 0;  // pemë/ndërtesa — lëvizin ngadalë
    private int sceneryOffset2 = 0;  // objekte të afërta — lëvizin shpejt

    //Nje timer qe perdoret per nottification sahere kalon nivelin
    private int levelUpTimer  = 0;   // sa frame mbetet visible
    private float levelUpAlpha = 0f; // transparenca

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

    //Perdorim kete variabel per te numeruar coinsat per raund
    private int coinsCollectedThisRun = 0;

    //Shtova 3 variablat qe do te perdoren per shake-un e ekranit pas perplasjes
    private int shakeDuration = 0;    // sa frame mbetet shake
    private int shakeX = 0;           // offset horizontal
    private int shakeY = 0;           // offset vertikal

    // Variablat per trail — dy vija anesore (ose 1 per motorrin) pas makines se lojtarit
    private static final int TRAIL_LENGTH = 14;
    private int[] trailX = new int[TRAIL_LENGTH];
    private int[] trailY = new int[TRAIL_LENGTH];
    private int trailIndex = 0;
    private float trailLeanOffset = 0f;  // lean drejt drejtimit te levizjes
    private int rainbowTick = 0;         // per animacionin e rainbow trail
    public static String currentTrail = "NONE"; // "NONE","RED","BLUE","GREEN","PURPLE","RAINBOW"

    private int scorePulse     = 0;   // sa frame mbetet pulse
    private int lastScore      = 0;   // për të detektuar ndryshimin

    //Boolean per te percaktuar nqfs loja ka mbaruar ose jo
    private boolean gameOver = false;

    //Boolean per te percaktuar nqfs loja eshte bere pause
    private boolean paused = false;

    //Variabla qe perdoret per te detektuar nqfs crash sound eshte bere play ose jo
    private boolean crashPlayed = false;

    //Buton per te restartuar lojen
    private JButton restartButton;

    //Butoni per tu kthyer ne menune kryesore
    private JButton menuButton;

    //Deklarojme variablen per aksesimin e AudioManager per muziken dhe sound effect crash
    private AudioManager audioManager;

    // ── Road lane-marker animation ───────────────────────────────────────────
    private int laneMarkerOffset = 0;   // scrolls downward each tick

    // ── Game loop timer (~60 FPS) ────────────────────────────────────────────
    private Timer gameTimer;

    // ── Delegated subsystems ─────────────────────────────────────────────────
    private final EntityManager entityManager = new EntityManager();
    private final GameRenderer  renderer      = new GameRenderer();

    // ─────────────────────────────────────────────────────────────────────────

    public GamePanel(MainFrame mainFrame) {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(new Color(34, 139, 34));// Required to receive key events;
        setFocusable(true);
        addKeyListener(this);
        //Shtojme audioManager
        this.audioManager = mainFrame.getAudioManager();

        // Krijo butonin por fshihe — shfaqet vetëm pas Game Over
        restartButton = new JButton("↺  RESTART");
        restartButton.setFont(new Font("Monospaced", Font.BOLD, 16));
        restartButton.setForeground(Color.WHITE);
        restartButton.setBackground(new Color(180, 30, 30));
        restartButton.setFocusPainted(false);
        restartButton.setBorderPainted(false);
        restartButton.setVisible(false);  // i fshehur në fillim

        restartButton.addActionListener(e ->{
            audioManager.playSFX("assets/click.wav");
            restartGame();
        });

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
            //Kur klikohet luhet soundfx i click.wav
            audioManager.playSFX("assets/click.wav");
            // Rivendos gjendjen e lojës
            gameOver   = false;
            score      = 0;
            level      = 1;
            enemySpeed = 4;
            entityManager.spawnTimer = 0;
            entityManager.enemies.clear();

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
        gameOver    = false;
        crashPlayed = false;    //Sa here ristartojme lojen crashPLayed kthehet False. Ky ishte buggu
        paused = false;
        entityManager.clearAll();
        shakeDuration=0;    //reset shake
        //resetime te trail
        trailIndex    = 0;
        trailX        = new int[TRAIL_LENGTH];
        trailY        = new int[TRAIL_LENGTH];
        trailLeanOffset = 0f;
        rainbowTick   = 0;
        coinsCollectedThisRun = 0;
        carX = (ROAD_LEFT + ROAD_RIGHT) / 2 - CAR_WIDTH / 2;//pozicioni i startit te makines se lojtarit
        restartButton.setVisible(false);
        menuButton.setVisible(false);
        requestFocusInWindow();
        if (!gameTimer.isRunning()) {
            gameTimer.start();
            audioManager.playMusic();//Muzika nis bashke me lojen
        }
    }

    //Metoda qe ben triger shake-un
    private void triggerShake() {
        shakeDuration = 12;  // ~200ms në 60fps
    }

        private void checkCollisions() {
            int hitW = MainFrame.currentSkin.equals("MOTO") ? 22 : CAR_WIDTH - 6;
            int hitX = MainFrame.currentSkin.equals("MOTO")
                    ? carX + (CAR_WIDTH - 22) / 2
                    : carX + 3;

            Rectangle playerHitbox = new Rectangle(hitX, carY + 4, hitW, CAR_HEIGHT - 8);
            for (EnemyCar enemy : entityManager.enemies) {
                if (playerHitbox.intersects(enemy.hitbox) && !crashPlayed && !entityManager.shieldActive) {
                    crashPlayed = true;
                    gameOver    = true;

                    // Efektet ndodhin MENJËHERË
                    triggerShake();
                    entityManager.spawnParticles(carX, carY);

                    audioManager.playSFX("assets/crashSFX.wav");


                    // Ekonomia
                    MainFrame.totalMoney += score / 10;
                    coinsCollectedThisRun += score / 10;
                    if (score > MainFrame.highScore) MainFrame.highScore = score;
                    SaveManager.save(MainFrame.highScore, MainFrame.totalMoney);

                    // UI — shfaq butonat menjëherë
                    restartButton.setVisible(true);
                    menuButton.setVisible(true);

                    return;  // ← dil menjëherë, mos kontrollo armiqtë e tjerë
                }
            }
        }

    //Metoda qe perdoret per te rifilluar lojen
    private void restartGame() {
        // Rivendos variablat
        gameOver    = false;
        paused = false;
        entityManager.clearAll();
        crashPlayed = false;  // ← mund të crash-ohet serisht

        requestFocusInWindow();

        //Behet reset niveli dhe piket
        score       = 0;
        level       = 1;
        enemySpeed  = 4;
        //Reset particles dhe shake-ut
        scorePulse  = 0;
        coinsCollectedThisRun = 0;
        shakeDuration = 0;
        shakeX = 0;
        shakeY = 0;

        // Rivendos pozicionin e lojtarit në qendër
        carX = (ROAD_LEFT + ROAD_RIGHT) / 2 - CAR_WIDTH / 2;

        restartButton.setVisible(false);
        menuButton.setVisible(false); // Rregullova nje bug qe butoni Menu ngelte ne ekran pasi klikohej restart

        requestFocusInWindow();
        if (!gameTimer.isRunning()) {
            gameTimer.start();
        }
        //Muzika rinis kur shkon ne Menu
        audioManager.playMusic();

        //Reset i trail-it
        trailIndex = 0;
        trailX = new int[TRAIL_LENGTH];
        trailY = new int[TRAIL_LENGTH];
        trailLeanOffset = 0f;
        rainbowTick = 0;
    }

    // ── Game logic update ─────────────────────────────────────────────────────

    private void update() {

        // ── Efektet vizuale — vazhdojnë edhe pas gameOver ──
        if (shakeDuration > 0) {
            shakeDuration--;
            shakeX = (int)(Math.random() * 10) - 5;
            shakeY = (int)(Math.random() * 10) - 5;
        } else {
            shakeX = 0;
            shakeY = 0;
        }

        // Particles update ndodh brenda entityManager.update() — por ndalon logjika kryesore nese loja mbaroi
        if (gameOver) {
            // Vetem particles vazhdojne pas game over
            Iterator<EntityManager.Particle> pit = entityManager.particles.iterator();
            while (pit.hasNext()) {
                EntityManager.Particle p = pit.next();
                p.update();
                if (p.isDead()) pit.remove();
            }
            return;
        }

        // ── Logjika e lojës ──
        if (movingLeft)  carX -= MOVE_SPEED;
        if (movingRight) carX += MOVE_SPEED;
        carX = Math.max(ROAD_LEFT, Math.min(carX, ROAD_RIGHT - CAR_WIDTH));

        // Lean offset drifton drejt drejtimit qe lojtari shtyp — per efektin e trails
        if (movingLeft)       trailLeanOffset = Math.max(trailLeanOffset - 1.5f, -8f);
        else if (movingRight) trailLeanOffset = Math.min(trailLeanOffset + 1.5f,  8f);
        else                  trailLeanOffset *= 0.75f;

        // Ruajme pozicionin ne buffer cirkular per trail
        trailX[trailIndex] = carX;
        trailY[trailIndex] = carY;
        trailIndex = (trailIndex + 1) % TRAIL_LENGTH;

        // Rainbow tick per animacion
        if (currentTrail.equals("RAINBOW")) rainbowTick++;

        // ── Update i te gjitha entiteteve (enemies, coins, shields, particles) ──
        coinsCollectedThisRun += entityManager.update(carX, carY, level, enemySpeed, audioManager);

        checkCollisions();

        laneMarkerOffset = (laneMarkerOffset + 6) % 50; // nga 4%60 → 6%50 — lëvizje më e shpejtë
        sceneryOffset1 = (sceneryOffset1 + 3) % (PANEL_HEIGHT + 60);  // pemë
        sceneryOffset2 = (sceneryOffset2 + 2) % (PANEL_HEIGHT + 60);  // gurë — ngadalë
        score++;

        if (score / 10 > lastScore / 10) scorePulse = 20;
        lastScore = score;
        if (scorePulse > 0) scorePulse--;

        if (score % 500 == 0) {
            level++;
            enemySpeed = Math.min(enemySpeed + 1, 12);
            levelUpTimer = 90;  // ~1.5 sekonda në 60fps
        }
        if (levelUpTimer > 0) levelUpTimer--;
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

        //Ndodh shake ne kordinatat e percaktuara random per cdo frame
        g2.translate(shakeX, shakeY);

        renderer.drawRoad(g2, laneMarkerOffset);
        renderer.drawScenery(g2, sceneryOffset1, sceneryOffset2);
        renderer.drawCoins(g2, entityManager.coins);
        renderer.drawShields(g2, entityManager.shields);//therras metoden per te vizatuar shield
        renderer.drawTrail(g2, trailX, trailY, trailIndex, TRAIL_LENGTH,
                trailLeanOffset, rainbowTick); // Vizatojme trail-in perpara makines qe te shfaqet nen te
        switch (MainFrame.currentSkin) {
            case "POLICE" -> renderer.drawPoliceCar(g2, carX, carY, laneMarkerOffset);
            case "MOTO"   -> renderer.drawMotorcycle(g2, carX, carY);
            default       -> renderer.drawPlayerCar(g2, carX, carY);
        }
        renderer.drawShieldOverlay(g2, carX, carY,
                entityManager.shieldActive, entityManager.shieldPulse);//overlay qe tregon se shieldi eshte aktiv
        renderer.drawEnemies(g2, entityManager.enemies);
        renderer.drawParticles(g2, entityManager.particles);//Vizatojme particles
        g2.translate(-shakeX, -shakeY); // ← RESET para HUD — HUD nuk duhet të shake-ohet

        renderer.drawHUD(g2, score, level, scorePulse,
                entityManager.shieldActive, entityManager.shieldTimer, levelUpTimer);

        //Nqs loja eshte bere Pause shfaqen opsionet
        if (paused) {
            renderer.drawPauseOverlay(g2);
        }

        //Nqs mbaron loja shfaqim me ngjyre te kuqe mesazhin game over
        if (gameOver) {
            renderer.drawGameOver(g2, score, coinsCollectedThisRun);
        }
    }

    // ── KeyListener ──────────────────────────────────────────────────────────

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT  -> movingLeft  = true;
            case KeyEvent.VK_RIGHT -> movingRight = true;
            case KeyEvent.VK_ESCAPE -> {
                paused = !paused;
                if (paused) {
                    gameTimer.stop();
                    repaint();
                }
                else        gameTimer.start();
            }
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
