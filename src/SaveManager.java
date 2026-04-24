import java.io.*;

public class SaveManager {
    private static final String FILE = "save.txt";

    public static void save(int highScore, int totalMoney) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE))) {
            pw.println(highScore);
            pw.println(totalMoney);
        } catch (IOException e) {
            System.out.println("Save error: " + e.getMessage());
        }
    }

    public static int[] load() {
        // Kthen [highScore, totalMoney]
        int[] data = {0, 0};
        try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
            data[0] = Integer.parseInt(br.readLine()); // highScore
            data[1] = Integer.parseInt(br.readLine()); // totalMoney
        } catch (Exception e) {
            System.out.println("No save found, starting fresh.");
        }
        return data;
    }
}