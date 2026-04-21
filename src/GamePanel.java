import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

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
    private static final int CAR_WIDTH   = 40;
    private static final int CAR_HEIGHT  = 70;
    private static final int MOVE_SPEED  = 6;   // pixels per frame

    /** Top-left X of the player car (Y is fixed near the bottom). */
    private int carX;
    private final int carY;

    // ── Input state ──────────────────────────────────────────────────────────
    private boolean movingLeft  = false;
    private boolean movingRight = false;

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

    public GamePanel() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(COLOR_GRASS);// Required to receive key events;
        setFocusable(true);
        addKeyListener(this);

        // Start car centered on the road, near the bottom
        carX = (ROAD_LEFT + ROAD_RIGHT) / 2 - CAR_WIDTH / 2;
        carY = PANEL_HEIGHT - CAR_HEIGHT - 30;

        // Game loop: fires ~60 times per second
        gameTimer = new Timer(16, e -> {
            update();
            repaint();
        });

        /*Kur behet klikimi i butonit PLAY ben te mundur ndryshimin e panelit dhe
        startimit te lojes */
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                // Thirret automatikisht nga CardLayout kur ky panel bëhet i dukshëm
                requestFocusInWindow();
                if (!gameTimer.isRunning()) {
                    gameTimer.start();
                }
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                // Ndalon loop-in kur kthehet te menuja — kursen CPU
                gameTimer.stop();
            }
        });
    }

    /** Call after the window is visible so the panel can receive focus. */
    public void startGame() {
        requestFocusInWindow();
        gameTimer.start();
    }

    // ── Game logic update ─────────────────────────────────────────────────────

    private void update() {
        // Move car according to held keys
        if (movingLeft)  carX -= MOVE_SPEED;
        if (movingRight) carX += MOVE_SPEED;

        // Clamp car so it stays on the road (accounting for car width)
        carX = Math.max(ROAD_LEFT,  Math.min(carX, ROAD_RIGHT - CAR_WIDTH));

        // Scroll lane markers downward to create a sense of forward motion
        laneMarkerOffset = (laneMarkerOffset + 4) % 60;
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

    /** Draws a simple HUD showing the control hint. */
    private void drawHUD(Graphics2D g2) {
        g2.setColor(new Color(255, 255, 255, 160));
        g2.setFont(new Font("Monospaced", Font.BOLD, 12));
        g2.drawString("← → ARROW KEYS to move", ROAD_LEFT + 8, PANEL_HEIGHT - 10);
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
