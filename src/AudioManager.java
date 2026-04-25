import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

public class AudioManager {

    // Dy Clip të ndara — njëra për muzikë, tjetra për SFX
    private Clip musicClip;
    private Clip sfxClip;

    // Metoda që ngarkon një skedar dhe kthen një Clip
    private Clip loadClip(String path) {
        try {
            // Krijojmë një objekt File duke përdorur path-in relativ
            File audioFile = new File(path);

            if (!audioFile.exists()) {
                System.err.println("GABIM: Skedari nuk u gjet te: " + audioFile.getAbsolutePath());
                return null;
            }

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(audioFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            return clip;
        } catch (Exception e) {
            System.out.println("Audio error: " + e.getMessage());
            return null;
        }
    }
    public void loadMusic(String path) {
        musicClip = loadClip(path);

    }

    public void playMusic() {
        if (musicClip == null) return;
        if (!GamePanel.musicEnabled) return;  // respekton checkbox-in

        if (musicClip.isRunning()) return;//Nqs muzika eshte on mos bej asgje
        musicClip.setFramePosition(0);               // fillo nga fillimi
        musicClip.loop(Clip.LOOP_CONTINUOUSLY);      // ← loop i pafund
        musicClip.start();
    }

    public void stopMusic() {
        if (musicClip != null && musicClip.isRunning()) {
            musicClip.stop();
        }
    }

    public void playSFX(String path) {
        if (!GamePanel.sfxEnabled) return;

        Clip sfx = loadClip(path);
        if (sfx == null) return;

        sfx.start();

        // Kur mbaron tingullin, liron memorien automatikisht
        sfx.addLineListener(event -> {
            if (event.getType() == javax.sound.sampled.LineEvent.Type.STOP) {
                sfx.close();
            }
        });
    }

    public void cleanup() {
        if (musicClip != null) {
            musicClip.stop();
            musicClip.close();
        }
    }

}