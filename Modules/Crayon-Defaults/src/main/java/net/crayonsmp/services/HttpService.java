package net.crayonsmp.services;

// Wichtig: Nutzen Sie okhttp3 für die moderne Version

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.crayonsmp.CrayonDefault;
import okhttp3.*;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Formatter;
import java.util.List;

public class HttpService {
    public static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");
    private static final OkHttpClient client = new OkHttpClient();
    private static final String url = "http://localhost:8076/update";
    private static final Gson gson = new Gson();

    public static void sendStatsUpdate(int items, int blocks, int furnitures, List<Player> players) throws IOException {
        JsonObject jsonPayload = new JsonObject();

        jsonPayload.addProperty("totp", generateTimeToken(30));

        jsonPayload.addProperty("registered_items", items);
        jsonPayload.addProperty("registered_blocks", blocks);
        jsonPayload.addProperty("registered_furniture", furnitures);
        jsonPayload.addProperty("registered_mobs", 0);
        jsonPayload.addProperty("last_server_reload", "2025-06-10 22:00:00");
        jsonPayload.addProperty("next_server_reload", "2025-06-10 22:00:00");


        JsonArray playersArray = new JsonArray();
        for (Player player : players) {
            playersArray.add(player.getName());
        }
        jsonPayload.add("online_players", playersArray);

        String jsonString = gson.toJson(jsonPayload);

        RequestBody body = RequestBody.create(jsonString, JSON_MEDIA_TYPE);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.err.println("Error sending json. HTTP-Statuscode: " + response.code());
                throw new IOException("Unerwarteter HTTP-Code: " + response);
            }
        }
    }

    public void sendPushMassage(String title, String message) throws IOException {
        JsonObject jsonPayload = new JsonObject();

        jsonPayload.addProperty("title", title);
        jsonPayload.addProperty("message", message);

        String jsonString = gson.toJson(jsonPayload);

        RequestBody body = RequestBody.create(jsonString, JSON_MEDIA_TYPE);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.err.println("Fehler beim Senden der Daten. HTTP-Statuscode: " + response.code());
                System.err.println("Antwort Body: " + response.body().string());
                throw new IOException("Unerwarteter HTTP-Code: " + response);
            }
        }
    }

    public static String generateTimeToken(int window) {

        String secret = CrayonDefault.config.getString("secret");

        if (window <= 0) {
            throw new IllegalArgumentException("Window must be a positive integer.");
        }

        long currentTimestamp = Instant.now().getEpochSecond();
        long timeSlot = currentTimestamp / window;

        // Combine the secret with the calculated time slot
        String combined = secret + timeSlot;

        // Calculate SHA-256 hash
        String hashDigest;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(combined.getBytes(StandardCharsets.UTF_8));
            hashDigest = toHexString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }

        // Convert the first 8 characters of the hash to a number,
        // calculate modulo 10^6, and format as a 6-digit string
        long number = Long.parseLong(hashDigest.substring(0, 8), 16) % 1000000;
        return String.format("%06d", number);
    }

    private static String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }
}
