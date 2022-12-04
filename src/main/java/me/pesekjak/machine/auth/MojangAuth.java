package me.pesekjak.machine.auth;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.pesekjak.machine.entities.player.PlayerTexturesImpl;
import me.pesekjak.machine.utils.UUIDUtils;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MojangAuth {


    public static final String AUTH_URL = "https://sessionserver.mojang.com/session/minecraft/hasJoined?username=%s&serverId=%s";
    public static final String USER_PROFILE_URL = "https://api.mojang.com/users/profiles/minecraft/%s";
    public static final String MINECRAFT_PROFILE_URL = "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false";

    /**
     * Checks for auth data created by client if the game is
     * licensed for the given server id.
     * @param serverId encrypted id of the server
     * @param username player's username
     * @return
     */
    public static CompletableFuture<JsonObject> getAuthData(String serverId, String username) {
        final String url = String.format(MojangAuth.AUTH_URL, username, serverId);
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder(new URI(url))
                        .timeout(Duration.ofSeconds(5))
                        .GET().build();
                HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == HttpURLConnection.HTTP_OK)
                    return (JsonObject) new JsonParser().parse(response.body());
            } catch (Exception ignored) { }
            return null;
        });
    }

    /**
     * Creates offline mode uuid from a player's nickname.
     * @param username player's nickname
     * @return offline mode uuid
     */
    public static UUID getOfflineUUID(String username) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Returns online UUID of registered account.
     * @param username username of the account
     * @return online UUID of account, null if the account doesn't exist
     */
    public static CompletableFuture<UUID> getUUID(String username) {
        final String url = String.format(USER_PROFILE_URL, username);
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder(new URI(url))
                        .timeout(Duration.ofSeconds(5))
                        .GET().build();
                HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                if (!(response.statusCode() == HttpURLConnection.HTTP_OK))
                    return null;
                JsonObject json = (JsonObject) new JsonParser().parse(response.body());
                return UUIDUtils.parseUUID(json.get("id").getAsString());
            } catch (Exception ignored) { }
            return null;
        });
    }

    /**
     * Returns skin of registered account.
     * @param uuid online uuid of the account
     * @return skin of the registered account, null if the account doesn't exist
     */
    public static CompletableFuture<PlayerTexturesImpl> getSkin(UUID uuid) {
        final String url = String.format(MINECRAFT_PROFILE_URL, uuid);
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder(new URI(url))
                        .timeout(Duration.ofSeconds(5))
                        .GET().build();
                HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                if (!(response.statusCode() == HttpURLConnection.HTTP_OK))
                    return null;
                JsonObject json = (JsonObject) new JsonParser().parse(response.body());
                JsonObject properties = json.getAsJsonArray("properties").get(0).getAsJsonObject();
                return PlayerTexturesImpl.buildSkin(properties);
            } catch (Exception ignored) { }
            return null;
        });
    }

    /**
     * Returns skin of registered account.
     * @param username username of the account
     * @return skin of the registered account, null if the account doesn't exist
     */
    public static CompletableFuture<PlayerTexturesImpl> getSkin(String username) {
        return CompletableFuture.supplyAsync(() -> getSkin(getUUID(username).join()).join());
    }

}
