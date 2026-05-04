import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class FirebaseManager {

    private static final String DB_URL;

    // Ngarkohet një herë kur klasa inicializohet
    static {
        String url = "";
        try (InputStream in = new FileInputStream("config/firebase.properties")) {
            Properties props = new Properties();
            props.load(in);
            url = props.getProperty("firebase.url", "").trim();
            if (url.isEmpty()) {
                System.err.println("firebase.url mungon në config/firebase.properties");
            }
        } catch (IOException e) {
            System.err.println("Nuk u gjet config/firebase.properties: " + e.getMessage());
        }
        DB_URL = url;
    }

    // Kontrollo nëse konfigurimi është ngarkuar para çdo thirrjeje
    private static boolean isConfigured() {
        if (DB_URL == null || DB_URL.isEmpty()) {
            System.err.println("Firebase nuk është konfiguruar. Kontrollo config/firebase.properties");
            return false;
        }
        return true;
    }

    public static void submitScore(String username, int score) {
        if (!isConfigured()) return;

        new Thread(() -> {
            try {
                int existingScore = getScore(username);
                if (score <= existingScore) return;

                String json = String.format(
                        "{\"score\":%d,\"timestamp\":%d}",
                        score,
                        System.currentTimeMillis()
                );

                URL url = new URL(DB_URL + "/leaderboard/" + encode(username) + ".json");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(json.getBytes(StandardCharsets.UTF_8));
                }

                int code = conn.getResponseCode();
                if (code == 200) {
                    System.out.println("Score u dërgua: " + username + " → " + score);
                } else {
                    System.err.println("Firebase HTTP error: " + code);
                }
                conn.disconnect();

            } catch (Exception e) {
                System.err.println("Firebase submit error: " + e.getMessage());
            }
        }).start();
    }

    public static int getScore(String username) {
        if (!isConfigured()) return 0;

        try {
            URL url = new URL(DB_URL + "/leaderboard/" + encode(username) + ".json");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            reader.close();
            conn.disconnect();

            String response = sb.toString();
            if (response.equals("null")) return 0;

            int scoreIdx = response.indexOf("\"score\":") + 8;
            int scoreEnd = response.indexOf(",", scoreIdx);
            if (scoreEnd == -1 || scoreEnd > response.indexOf("}", scoreIdx))
                scoreEnd = response.indexOf("}", scoreIdx);
            return Integer.parseInt(response.substring(scoreIdx, scoreEnd).trim());

        } catch (Exception e) {
            System.err.println("Firebase getScore error: " + e.getMessage());
            return 0;
        }
    }

    public static String[][] getLeaderboard() {
        if (!isConfigured()) return new String[0][0];

        try {
            URL url = new URL(DB_URL +
                    "/leaderboard.json?orderBy=\"score\"&limitToLast=10");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            reader.close();
            conn.disconnect();

            String json = sb.toString();
            if (json.equals("null")) return new String[0][0];

            java.util.List<String[]> entries = new java.util.ArrayList<>();
            int pos = 0;
            while (pos < json.length()) {
                int nameStart = json.indexOf("\"", pos);
                if (nameStart == -1) break;
                int nameEnd = json.indexOf("\"", nameStart + 1);
                String name = json.substring(nameStart + 1, nameEnd);

                int scoreIdx = json.indexOf("\"score\":", nameEnd) + 8;
                if (scoreIdx < 8) break;
                int scoreEnd = json.indexOf(",", scoreIdx);
                if (scoreEnd == -1 || scoreEnd > json.indexOf("}", scoreIdx))
                    scoreEnd = json.indexOf("}", scoreIdx);
                String scoreStr = json.substring(scoreIdx, scoreEnd).trim();

                entries.add(new String[]{name, scoreStr});
                pos = scoreEnd + 1;
            }

            entries.sort((a, b) -> Integer.parseInt(b[1]) - Integer.parseInt(a[1]));

            int size = Math.min(entries.size(), 10);
            String[][] result = new String[size][2];
            for (int i = 0; i < size; i++) result[i] = entries.get(i);
            return result;

        } catch (Exception e) {
            System.err.println("Firebase leaderboard error: " + e.getMessage());
            return new String[0][0];
        }
    }

    private static String encode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (Exception e) {
            return s;
        }
    }
}