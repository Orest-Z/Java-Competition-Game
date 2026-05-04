import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class FirebaseManager {

    private static final String DB_URL;

    static {
        String url = "";

        // PRIORITET 1: brenda JAR-it (getResourceAsStream)
        try (InputStream in = FirebaseManager.class
                .getResourceAsStream("/config/firebase.properties")) {
            if (in != null) {
                Properties props = new Properties();
                props.load(in);
                url = props.getProperty("firebase.url", "").trim();
                System.out.println("[Firebase] URL ngarkuar nga brenda JAR.");
            }
        } catch (Exception e) {
            System.err.println("[Firebase] Resource error: " + e.getMessage());
        }

        // PRIORITET 2: fallback për IDE run (working directory)
        if (url.isEmpty()) {
            try {
                File propsFile = new File("config/firebase.properties");
                if (propsFile.exists()) {
                    Properties props = new Properties();
                    try (InputStream in = new FileInputStream(propsFile)) {
                        props.load(in);
                    }
                    url = props.getProperty("firebase.url", "").trim();
                    System.out.println("[Firebase] URL ngarkuar nga: " + propsFile.getAbsolutePath());
                } else {
                    System.err.println("[Firebase] Nuk u gjet config/firebase.properties.");
                }
            } catch (Exception e) {
                System.err.println("[Firebase] File error: " + e.getMessage());
            }
        }

        DB_URL = url;
    }

    private static boolean isConfigured() {
        if (DB_URL == null || DB_URL.isEmpty()) {
            System.err.println("[Firebase] Nuk është konfiguruar. Kontrollo config/firebase.properties.");
            return false;
        }
        return true;
    }

    // ── Submit score (ekzekutohet në thread të veçantë) ──────────────────────
    public static void submitScore(String username, int score) {
        if (!isConfigured()) return;

        new Thread(() -> {
            try {
                int existingScore = getScore(username);
                if (score <= existingScore) {
                    System.out.println("[Firebase] Score nuk u dërgua (ekzistues: " + existingScore + " >= " + score + ")");
                    return;
                }

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
                conn.setConnectTimeout(8000);
                conn.setReadTimeout(8000);

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(json.getBytes(StandardCharsets.UTF_8));
                }

                int code = conn.getResponseCode();
                if (code == 200) {
                    System.out.println("[Firebase] Score u dërgua: " + username + " → " + score);
                } else {
                    // Lexo error body për diagnostikim
                    InputStream errStream = conn.getErrorStream();
                    String errBody = errStream != null ? new String(errStream.readAllBytes()) : "(pa body)";
                    System.err.println("[Firebase] HTTP " + code + " gjatë submitScore: " + errBody);
                }
                conn.disconnect();

            } catch (Exception e) {
                System.err.println("[Firebase] submitScore exception: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    // ── Merr score-in ekzistues të një lojtari ────────────────────────────────
    public static int getScore(String username) {
        if (!isConfigured()) return 0;

        try {
            URL url = new URL(DB_URL + "/leaderboard/" + encode(username) + ".json");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(8000);

            int code = conn.getResponseCode();
            if (code != 200) {
                System.err.println("[Firebase] getScore HTTP " + code);
                conn.disconnect();
                return 0;
            }

            String response = readResponse(conn);
            conn.disconnect();

            if (response == null || response.equals("null")) return 0;

            // Parsim i sigurt — kërkon "score" kudo në JSON
            return extractInt(response, "score");

        } catch (Exception e) {
            System.err.println("[Firebase] getScore exception: " + e.getMessage());
            return 0;
        }
    }

    // ── Merr top 10 leaderboard ───────────────────────────────────────────────
    public static String[][] getLeaderboard() {
        if (!isConfigured()) return new String[0][0];

        try {
            // RËNDËSI: Nuk përdorim orderBy sepse kërkon indexim në Firebase Rules.
            // Marrim të gjithë leaderboard-in dhe e renditim vetë në Java.
            URL url = new URL(DB_URL + "/leaderboard.json");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(8000);

            int code = conn.getResponseCode();
            if (code != 200) {
                InputStream errStream = conn.getErrorStream();
                String errBody = errStream != null ? new String(errStream.readAllBytes()) : "";
                System.err.println("[Firebase] getLeaderboard HTTP " + code + ": " + errBody);
                conn.disconnect();
                return new String[0][0];
            }

            String json = readResponse(conn);
            conn.disconnect();

            if (json == null || json.trim().equals("null") || json.trim().isEmpty()) {
                return new String[0][0];
            }

            System.out.println("[Firebase] Leaderboard JSON: " + json);

            return parseLeaderboard(json);

        } catch (Exception e) {
            System.err.println("[Firebase] getLeaderboard exception: " + e.getMessage());
            e.printStackTrace();
            return new String[0][0];
        }
    }

    // ── Parser i leaderboard-it ───────────────────────────────────────────────
    // JSON format nga Firebase: {"username1":{"score":100,"timestamp":...},"username2":{...}}
    private static String[][] parseLeaderboard(String json) {
        java.util.List<String[]> entries = new java.util.ArrayList<>();

        // Heqim kllapën e jashtme { }
        json = json.trim();
        if (json.startsWith("{")) json = json.substring(1);
        if (json.endsWith("}"))   json = json.substring(0, json.length() - 1);

        // Ndajmë çdo rekord: "username":{...}
        // Gjejmë çdo "key":{...} me kërkim manual
        int pos = 0;
        while (pos < json.length()) {
            // Gjej fillimin e emrit
            int nameStart = json.indexOf('"', pos);
            if (nameStart == -1) break;
            int nameEnd = json.indexOf('"', nameStart + 1);
            if (nameEnd == -1) break;

            String name = json.substring(nameStart + 1, nameEnd);

            // Gjej { që hap objektin e vlerave
            int objStart = json.indexOf('{', nameEnd);
            if (objStart == -1) break;

            // Gjej } mbyllës (duke mbajtur parasysh nested {})
            int depth = 1;
            int objEnd = objStart + 1;
            while (objEnd < json.length() && depth > 0) {
                char c = json.charAt(objEnd);
                if (c == '{') depth++;
                else if (c == '}') depth--;
                objEnd++;
            }

            String objJson = json.substring(objStart, objEnd); // {"score":..., "timestamp":...}

            // Ekstrakto score nga brenda objektit
            int scoreVal = extractInt(objJson, "score");

            if (!name.isEmpty() && scoreVal > 0) {
                entries.add(new String[]{ name, String.valueOf(scoreVal) });
            }

            pos = objEnd;
        }

        // Rendit zbritshëm sipas score
        entries.sort((a, b) -> Integer.parseInt(b[1]) - Integer.parseInt(a[1]));

        int size = Math.min(entries.size(), 10);
        String[][] result = new String[size][2];
        for (int i = 0; i < size; i++) result[i] = entries.get(i);

        System.out.println("[Firebase] Parsed " + size + " entries nga leaderboard.");
        return result;
    }

    // ── Utility: lexo response body ───────────────────────────────────────────
    private static String readResponse(HttpURLConnection conn) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

    // ── Utility: ekstrakto int nga JSON pa librari ────────────────────────────
    // Kërkon "key": NUMBER kudo në string, pa u shqetësuar për rendin e fushave
    private static int extractInt(String json, String key) {
        String search = "\"" + key + "\":";
        int idx = json.indexOf(search);
        if (idx == -1) return 0;

        int valStart = idx + search.length();
        // Hiq hapësirat
        while (valStart < json.length() && json.charAt(valStart) == ' ') valStart++;

        int valEnd = valStart;
        while (valEnd < json.length()) {
            char c = json.charAt(valEnd);
            if (c == ',' || c == '}' || c == ' ') break;
            valEnd++;
        }

        try {
            return Integer.parseInt(json.substring(valStart, valEnd).trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // ── Utility: URL encode ───────────────────────────────────────────────────
    private static String encode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (Exception e) {
            return s;
        }
    }
}