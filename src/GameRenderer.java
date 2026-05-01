import java.awt.*;
import java.awt.geom.*;

public class GameRenderer {

    // ── Panel / road dimensions ──────────────────────────────────────────────
    private static final int PANEL_WIDTH  = 480;
    private static final int PANEL_HEIGHT = 600;

    private static final int ROAD_LEFT  = 40;
    private static final int ROAD_RIGHT = 440;

    // ── Colors ───────────────────────────────────────────────────────────────
    private static final Color COLOR_GRASS      = new Color(34,  139, 34);
    private static final Color COLOR_ROAD       = new Color(50,  50,  50);
    private static final Color COLOR_KERB       = new Color(220, 220, 220);
    private static final Color COLOR_LANE_MARK  = new Color(255, 220, 0, 120);
    private static final Color COLOR_CAR_BODY   = new Color(220, 40,  40);
    private static final Color COLOR_CAR_ROOF   = new Color(180, 30,  30);
    private static final Color COLOR_WINDOW     = new Color(160, 220, 255, 200);
    private static final Color COLOR_TYRE       = new Color(20,  20,  20);
    private static final Color COLOR_HEADLIGHT  = new Color(255, 255, 180);

    /** Draws the road surface, kerb strips, and scrolling lane markers. */
    void drawRoad(Graphics2D g2, int laneMarkerOffset) {

            // Ngjyrat ndryshojnë bazuar në currentMap
            Color grassColor = switch (MainFrame.currentMap) {
                case "SNOW"   -> new Color(220, 235, 245);  // bardhë-gri si dëborë
                case "DESERT" -> new Color(194, 154, 89);   // kafe-verdhë si rërë
                default       -> COLOR_GRASS;               // jeshil normal
            };

            Color roadColor = switch (MainFrame.currentMap) {
                case "SNOW"   -> new Color(180, 195, 210);  // gri-blu si asfalt i ngrirë
                case "DESERT" -> new Color(160, 120, 70);   // kafe e errët si dhe
                default       -> COLOR_ROAD;
            };

            Color kerbColor1 = switch (MainFrame.currentMap) {
                case "SNOW"   -> new Color(180, 210, 230);  // blu e zbehtë
                case "DESERT" -> new Color(210, 170, 90);   // portokalli i zbehtë
                default       -> Color.RED;
            };

        // Vizato terrain-in (grass/snow/desert) — mbush gjithë panelit
        g2.setColor(grassColor);
        g2.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);

        //Update rruges qe te duket pak me e bukur vizualisht
        GradientPaint roadGradient = new GradientPaint(
                ROAD_LEFT,  0, roadColor.darker(),   // majtas — më e errët
                ROAD_RIGHT, 0, roadColor.brighter()  // djathtas — pak më e ndritshme
        );
        g2.setPaint(roadGradient);
        g2.fillRect(ROAD_LEFT, 0, ROAD_RIGHT - ROAD_LEFT, PANEL_HEIGHT);
        g2.setPaint(null);

// Vijat e kurbeve anash
        drawKerb(g2, ROAD_LEFT - 12, 12, kerbColor1, laneMarkerOffset);
        drawKerb(g2, ROAD_RIGHT,     12, kerbColor1, laneMarkerOffset);

// Vijat e korsive
        g2.setColor(COLOR_LANE_MARK);
        // Vijat ndarëse — lëvizin sipas laneMarkerOffset për iluzion lëvizjeje
        float[] dashPattern = {25f, 25f};
        g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                10f, dashPattern, laneMarkerOffset * 1.5f)); // ← *1.5f bën lëvizjen më të dukshme
        g2.setColor(new Color(255, 220, 0, 100)); // pak më transparent — jo strobe

        //Ndertimi i 4 korsive  me ane te nje cikli for(update day2)
        int totalLanes = 4;
        int laneWidth = (ROAD_RIGHT - ROAD_LEFT) / totalLanes;
        for (int i = 1; i < totalLanes; i++) {
            int lineX = ROAD_LEFT + (i * laneWidth);
            g2.drawLine(lineX, 0, lineX, PANEL_HEIGHT);
        }

        g2.setStroke(new BasicStroke(1f));
    }

    //Metoda per vizatimin e pemeve dhe objekteve anesore
    void drawScenery(Graphics2D g2, int sceneryOffset1, int sceneryOffset2) {
        // ── Pemë majtas — 4 pemë me detaje ───────────────────────
        int[] treeOffsetsLeft = {0, 160, 320, 480};
        for (int offset : treeOffsetsLeft) {
            int y = (sceneryOffset1 + offset) % (PANEL_HEIGHT + 60) - 30;

            // Hija e pemës
            g2.setColor(new Color(0, 0, 0, 40));
            g2.fillOval(4, y + 28, 20, 8);

            // Trungu
            g2.setColor(new Color(90, 55, 30));
            g2.fillRoundRect(10, y + 18, 7, 22, 3, 3);

            // Kurora e jashtme (e errët)
            g2.setColor(new Color(20, 90, 20, 200));
            g2.fillOval(0, y, 28, 28);

            // Kurora e brendshme (highlight)
            g2.setColor(new Color(40, 140, 40, 160));
            g2.fillOval(4, y + 2, 18, 18);

            // Shkëlqimi i vogël
            g2.setColor(new Color(100, 200, 80, 80));
            g2.fillOval(8, y + 3, 8, 7);
        }

        // ── Gurë djathtas ─────────────────────────────────────────
        int[] rockOffsets = {0, 150, 300, 450};
        int[] rockWidths  = {22, 16, 26, 18};
        int[] rockHeights = {14, 10, 18, 12};

        for (int i = 0; i < rockOffsets.length; i++) {
            int y = (sceneryOffset2 + rockOffsets[i]) % (PANEL_HEIGHT + 60) - 30;
            int rx = 448;
            int rw = rockWidths[i];
            int rh = rockHeights[i];

            // Hija e gurit
            g2.setColor(new Color(0, 0, 0, 50));
            g2.fillOval(rx + 2, y + rh - 3, rw - 4, 6);

            // Trupi kryesor i gurit — forma e rrumbullakët
            g2.setColor(new Color(100, 95, 90));
            g2.fillRoundRect(rx, y, rw, rh, 8, 8);

            // Shtresa e dytë — pak më e çelur (volumi)
            g2.setColor(new Color(130, 125, 118));
            g2.fillRoundRect(rx + 2, y + 1, rw - 6, rh - 5, 6, 6);

            // Highlight i vogël — drita nga lart
            g2.setColor(new Color(180, 175, 168, 140));
            g2.fillOval(rx + 4, y + 2, rw / 3, rh / 4);

            // Krisje/detaj — një vijë e errët diagonale
            g2.setColor(new Color(70, 65, 60, 160));
            g2.setStroke(new BasicStroke(1f));
            g2.drawLine(rx + rw / 2, y + 3, rx + rw - 4, y + rh - 4);

            g2.setStroke(new BasicStroke(1f));
        }
    }

    /** Draws alternating red/white kerb blocks along a vertical strip. */
    private void drawKerb(Graphics2D g2, int x, int width, Color accentColor, int laneMarkerOffset) {
        int blockHeight = 24;
        for (int y = -blockHeight; y < PANEL_HEIGHT + blockHeight; y += blockHeight) {
            int adjustedY = y + (laneMarkerOffset % blockHeight);
            boolean alternate = ((adjustedY / blockHeight) % 2 == 0);
            g2.setColor(alternate ? accentColor : Color.WHITE);
            g2.fillRect(x, adjustedY, width, blockHeight);
        }
    }

    /**
     * Draws a stylised top-down car at (carX, carY).
     * Layers: tyres → body → roof → windows → headlights/tail-lights
     */
    void drawPlayerCar(Graphics2D g2, int carX, int carY) {
        int x = carX;
        int y = carY;
        int w = GamePanel.CAR_WIDTH;
        int h = GamePanel.CAR_HEIGHT;

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

    // Metoda per vizatimin e trail-it — dy vija anesore per makina, nje vije qendrore per motorr
    void drawTrail(Graphics2D g2, int[] trailX, int[] trailY, int trailIndex,
                   int trailLength, float trailLeanOffset, int rainbowTick) {
        if (GamePanel.currentTrail.equals("NONE")) return;

        boolean isMoto = MainFrame.currentSkin.equals("MOTO");

        for (int i = 0; i < trailLength; i++) {
            int idx = (trailIndex - 1 - i + trailLength) % trailLength;

            // Alpha ulet me distance — frame me i afert eshte me i dukshem
            float fraction = 1f - ((float) i / trailLength);
            int alpha = (int)(fraction * fraction * 180);
            if (alpha <= 0) continue;

            Color baseColor = getTrailColor(i, alpha, rainbowTick);

            int tx = trailX[idx];
            int ty = trailY[idx];
            int lean = (int) trailLeanOffset;

            if (isMoto) {
                // Nje vije e vetme qendrore nga prapa motorrit — me e gjate
                int motoW = 18;
                int centerX = tx + (GamePanel.CAR_WIDTH - motoW) / 2 + motoW / 2;
                int lineW = Math.max(3, (int)(fraction * 6));
                int lineH = Math.max(6, (int)(fraction * 22)); // zmadhova gjatesine
                g2.setColor(baseColor);
                g2.fillRoundRect(centerX - lineW / 2 + lean, ty + GamePanel.CAR_HEIGHT - 2,
                        lineW, lineH, 3, 3);
            } else {
                // Dy vija anesore — majtas dhe djathtas e trupit te makines — me te gjata
                int lineW = Math.max(3, (int)(fraction * 7));   // pak me te gjera
                int lineH = Math.max(8, (int)(fraction * 30)); // zmadhova gjatesine nga 14 → 30
                int insetX = 4; // pak me afer skajes se makines

                g2.setColor(baseColor);
                // Vija e majte
                g2.fillRoundRect(tx + insetX + lean, ty + GamePanel.CAR_HEIGHT - 2,
                        lineW, lineH, 4, 4);
                // Vija e djathte
                g2.fillRoundRect(tx + GamePanel.CAR_WIDTH - insetX - lineW + lean, ty + GamePanel.CAR_HEIGHT - 2,
                        lineW, lineH, 4, 4);
            }
        }
    }

    // Metoda per ngjyren e trail-it bazuar ne zgjedhjen e lojtarit nga shop
    private Color getTrailColor(int frameAge, int alpha, int rainbowTick) {
        return switch (GamePanel.currentTrail) {
            case "RED"    -> new Color(255, 40,  40,  alpha);
            case "BLUE"   -> new Color(40,  140, 255, alpha);
            case "GREEN"  -> new Color(40,  220, 80,  alpha);
            case "PURPLE" -> new Color(180, 40,  255, alpha);
            case "RAINBOW" -> {
                // Cdo segment merr nje ngjyre te ndryshme hue, e animuar me kalimin e kohes
                float hue = ((rainbowTick - frameAge * 4) % 360) / 360f;
                Color c = Color.getHSBColor(Math.abs(hue), 1f, 1f);
                yield new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
            }
            default -> new Color(80, 180, 255, alpha);
        };
    }

    //Shtova nje skin Makine Policie
    void drawPoliceCar(Graphics2D g2, int carX, int carY, int laneMarkerOffset) {
        // Vizato makinën normale fillimisht
        drawPlayerCar(g2, carX, carY);

        // Dritat pulsante — alternojnë çdo 15 frame
        boolean showRed = (laneMarkerOffset / 15) % 2 == 0;

        // Drita e majtë
        g2.setColor(showRed ? Color.RED : Color.BLUE);
        g2.fillRect(carX + 4, carY + 8, 12, 6);

        // Drita e djathtë
        g2.setColor(showRed ? Color.BLUE : Color.RED);
        g2.fillRect(carX + GamePanel.CAR_WIDTH - 16, carY + 8, 12, 6);
    }

    //Shtova nje skin per motorr
    void drawMotorcycle(Graphics2D g2, int carX, int carY) {
        int motoW = 18;  // Trupi kryesor është më i ngushtë se makina
        int x = carX + (GamePanel.CAR_WIDTH - motoW) / 2;
        int y = carY;
        int h = GamePanel.CAR_HEIGHT;

        // 1. Rrotat (Para dhe Mbrapa) - Pozicionohen në qendër të aksit gjatësor
        g2.setColor(new Color(30, 30, 30));
        // Rrota e përparme
        g2.fillRoundRect(x + (motoW/2) - 3, y + 2, 6, 15, 4, 4);
        // Rrota e pasme
        g2.fillRoundRect(x + (motoW/2) - 3, y + h - 18, 6, 16, 4, 4);

        // 2. Timoni (Handlebars)
        g2.setColor(new Color(50, 50, 50));
        g2.setStroke(new BasicStroke(3f));
        g2.drawLine(x - 4, y + 18, x + motoW + 4, y + 18); // Vija horizontale e timonit
        // Dorezat e timonit
        g2.fillRect(x - 6, y + 16, 4, 6);
        g2.fillRect(x + motoW + 2, y + 16, 4, 6);

        // 3. Trupi kryesor / Shasia
        g2.setColor(new Color(40, 40, 40));
        g2.fillRoundRect(x, y + 15, motoW, h - 30, 10, 10);

        // 4. Serbatori (Fuel Tank) - Pjesa më e gjerë dhe me ngjyrë
        g2.setColor(new Color(200, 20, 20)); // Mund ta bësh edhe sipas një variable ngjyre
        g2.fillOval(x + 2, y + 22, motoW - 4, 20);

        // 5. Sedilja (Seat)
        g2.setColor(new Color(20, 20, 20));
        g2.fillRoundRect(x + 3, y + 44, motoW - 6, 15, 5, 5);

        // 6. Drita e përparme (Headlight)
        g2.setColor(new Color(255, 255, 200, 220));
        g2.fillOval(x + (motoW/2) - 4, y + 5, 8, 8);

        // 7. Drita e pasme (Brake light)
        g2.setColor(new Color(255, 0, 0));
        g2.fillRect(x + (motoW/2) - 4, y + h - 6, 8, 4);

        // 8. Pasqyrat (Mirrors)
        g2.setColor(new Color(80, 80, 80));
        g2.fillOval(x - 8, y + 12, 5, 3);
        g2.fillOval(x + motoW + 3, y + 12, 5, 3);

        // Reset stroke
        g2.setStroke(new BasicStroke(1f));
    }

    //Ndryshova metoden e vizatimit te enemy cars duke ja lene ate klases me vete tek EnemyCar.java
    void drawEnemies(Graphics2D g2, java.util.ArrayList<EnemyCar> enemies) {
        for (EnemyCar enemy : enemies) {
            enemy.draw(g2);
        }
    }

    //Metoda e vizatimit te monedhave
    void drawCoins(Graphics2D g2, java.util.ArrayList<Coin> coins) {
        for (Coin coin : coins) {
            coin.draw(g2);
        }
    }

    //Metoda per vizatimin e mburojes
    void drawShields(Graphics2D g2, java.util.ArrayList<Shield> shields) {
        for (Shield s : shields) {
            s.draw(g2);
        }
    }

    // Halo blu rreth makinës kur shield është aktiv
    void drawShieldOverlay(Graphics2D g2, int carX, int carY, boolean shieldActive, float shieldPulse) {
        if (!shieldActive) return;

        // Pulse — intensiteti ndryshon me kohën
        float alpha = 0.25f + shieldPulse * 0.25f;
        int expand  = (int)(shieldPulse * 8);

        // Shtresa e jashtme glow
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha * 0.5f));
        g2.setColor(new Color(0, 180, 255));
        g2.fillOval(carX - 12 - expand, carY - 12 - expand,
                GamePanel.CAR_WIDTH + 24 + expand * 2, GamePanel.CAR_HEIGHT + 24 + expand * 2);

        // Rreth kryesor
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2.setColor(new Color(0, 220, 255));
        g2.setStroke(new BasicStroke(3f));
        g2.drawOval(carX - 10, carY - 10, GamePanel.CAR_WIDTH + 20, GamePanel.CAR_HEIGHT + 20);
        g2.setStroke(new BasicStroke(1f));

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }

    //Metoda e vizatimit te particles
    void drawParticles(Graphics2D g2, java.util.ArrayList<EntityManager.Particle> particles) {
        for (EntityManager.Particle p : particles) {
            // Opacity ulet me jetën e particle-it
            float alpha = (float) p.life / 50f;
            alpha = Math.min(1f, alpha);
            g2.setColor(new Color(
                    p.color.getRed(),
                    p.color.getGreen(),
                    p.color.getBlue(),
                    (int)(alpha * 255)
            ));
            g2.fillOval((int)p.x - 3, (int)p.y - 3, 6, 6);
        }
    }

    /** Draws a simple HUD showing the control hint. */
    void drawHUD(Graphics2D g2, int score, int level, int scorePulse,
                 boolean shieldActive, int shieldTimer, int levelUpTimer) {

        g2.setColor(new Color(255, 255, 255, 160));
        g2.setFont(new Font("Monospaced", Font.BOLD, 12));
        g2.drawString("← → ARROW KEYS to move", ROAD_LEFT + 8, PANEL_HEIGHT - 10);

        //Ndertohet nje HUD i ri per te treguar me tekst numrin e pikeve dhe nivelin qe ndodhet lojtari
        g2.setColor(new Color(255, 255, 255, 200));
        g2.setFont(new Font("Monospaced", Font.BOLD, 14));
        // Font normal = 14, pulse = deri 22
        int scoreSize = 14 + (scorePulse > 0 ? (int) (scorePulse * 0.4f) : 0);
        Color scoreColor;
        if (score > MainFrame.highScore && score > 0) {
            scoreColor = new Color(255, 60, 60);   // ← rekord aktiv — e kuqe
        } else if (scorePulse > 0) {
            scoreColor = new Color(255, 215, 0);   // ← pulse — ari
        } else {
            scoreColor = new Color(255, 255, 255, 200); // ← normal
        }

        // Vizato imazhin e coins prane totalit
        if (Coin.getCoinImage() != null) {
            g2.drawImage(Coin.getCoinImage(), ROAD_LEFT + 8, PANEL_HEIGHT - 52, 18, 18, null);
            g2.setColor(new Color(255, 215, 0));
            g2.setFont(new Font("Monospaced", Font.BOLD, 13));
            g2.drawString("" + MainFrame.totalMoney, ROAD_LEFT + 30, PANEL_HEIGHT - 38);
        }

        // ── Shield HUD — shirit blu me kohën mbetur ───────────────────────
        if (shieldActive) {
            int barW = 120;
            int barH = 10;
            int barX = PANEL_WIDTH / 2 - barW / 2;
            int barY = 38;

            float ratio = (float) shieldTimer / EntityManager.SHIELD_DURATION;

            // Sfond i errët
            g2.setColor(new Color(0, 0, 0, 160));
            g2.fillRoundRect(barX - 2, barY - 2, barW + 4, barH + 4, 6, 6);

            // Mbushja blu
            g2.setColor(new Color(0, 180, 255));
            g2.fillRoundRect(barX, barY, (int)(barW * ratio), barH, 5, 5);

            // Border
            g2.setColor(new Color(0, 220, 255, 180));
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(barX, barY, barW, barH, 5, 5);

            // Etiketa
            g2.setColor(new Color(0, 220, 255));
            g2.setFont(new Font("Monospaced", Font.BOLD, 11));
            g2.drawString("🛡 SHIELD", barX + barW / 2 - 28, barY - 3);
        }

        g2.setFont(new Font("Monospaced", Font.BOLD, scoreSize));
        g2.setColor(scoreColor);
        g2.drawString("SCORE: " + score, ROAD_LEFT + 8, 24);

        // LEVEL mbetet normal
        g2.setFont(new Font("Monospaced", Font.BOLD, 14));
        g2.setColor(new Color(255, 255, 255, 200));
        g2.drawString("LEVEL: " + level, ROAD_RIGHT - 80, 24);

        //Timer per njoftimin e kalimit te nivelit
        if (levelUpTimer > 0) {
            // Alpha ulet me kalimin e kohës — fade out
            float alpha = Math.min(1f, levelUpTimer / 30f);
            g2.setColor(new Color(255, 215, 0, (int)(alpha * 255)));
            g2.setFont(new Font("Monospaced", Font.BOLD, 42));
            g2.drawString("LEVEL UP!", 120, PANEL_HEIGHT / 2 - 60);
        }
    }

    // Vizatimi i overlay-it PAUSED
    void drawPauseOverlay(Graphics2D g2) {
        // Overlay gjysmë-transparent
        g2.setColor(new Color(0, 0, 0, 140));
        g2.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);
        // Teksti
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Monospaced", Font.BOLD, 40));
        g2.drawString("PAUSED", 150, PANEL_HEIGHT / 2);
        g2.setFont(new Font("Monospaced", Font.PLAIN, 14));
        g2.setColor(new Color(200, 200, 200));
        g2.drawString("ESC për të vazhduar", 155, PANEL_HEIGHT / 2 + 35);
    }

    // Vizatimi i Game Over box
    void drawGameOver(Graphics2D g2, int score) {
        // Box i madh që përfshin gjithçka
        int boxX = PANEL_WIDTH / 2 - 155;
        int boxY = PANEL_HEIGHT / 2 - 50;
        int boxW = 310;
        int boxH = 230;

        // Sfond i errët me border të kuq
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRoundRect(boxX, boxY, boxW, boxH, 18, 18);
        g2.setColor(new Color(200, 30, 30));
        g2.setStroke(new BasicStroke(2.5f));
        g2.drawRoundRect(boxX, boxY, boxW, boxH, 18, 18);
        g2.setStroke(new BasicStroke(1f));

        // GAME OVER
        g2.setColor(Color.RED);
        g2.setFont(new Font("Monospaced", Font.BOLD, 34));
        g2.drawString("GAME OVER", boxX + 55, boxY + 35);

        // Monedhat — me ngjyrë ari dhe font më të qartë
        int coinsEarned = score / 10;
        g2.setColor(new Color(255, 215, 0));
        g2.setFont(new Font("Monospaced", Font.BOLD, 16));
        g2.drawString("Fituat: " + coinsEarned + " monedha", boxX + 65, boxY + 62);

        // High score
        if (score >= MainFrame.highScore && score > 0) {
            g2.setColor(new Color(0, 220, 255));
            g2.setFont(new Font("Monospaced", Font.BOLD, 13));
            g2.drawString("★  REKORD I RI: " + score, boxX + 55, boxY + 82);
        }
        // Butonat janë pozicionuar me setBounds brenda këtij box-i
    }
}
