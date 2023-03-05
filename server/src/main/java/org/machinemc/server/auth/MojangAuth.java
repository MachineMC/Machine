package org.machinemc.server.auth;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.experimental.UtilityClass;
import org.machinemc.server.entities.player.PlayerTexturesImpl;
import org.machinemc.server.utils.UUIDUtils;
import org.jetbrains.annotations.NotNull;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Utility class for interacting with Mojang auth.
 */
@UtilityClass
public class MojangAuth {

    // TODO Ability to change these in java arguments on start
    public static final String AUTH_URL = "https://sessionserver.mojang.com/session/minecraft/hasJoined?username=%s&serverId=%s";
    public static final String USER_PROFILE_URL = "https://api.mojang.com/users/profiles/minecraft/%s";
    public static final String MINECRAFT_PROFILE_URL = "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false";

    /**
     * Checks for auth data created by client if the game is
     * licensed for the given server id.
     * @param serverId encrypted id of the server
     * @param username player's username
     * @return obtained json
     */
    public static @NotNull CompletableFuture<JsonObject> getAuthData(@NotNull String serverId, @NotNull String username) {
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
    public static @NotNull UUID getOfflineUUID(@NotNull String username) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Returns online UUID of registered account.
     * @param username username of the account
     * @return online UUID of account, null if the account doesn't exist
     */
    public static @NotNull CompletableFuture<UUID> getUUID(@NotNull String username) {
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
    public static @NotNull CompletableFuture<PlayerTexturesImpl> getSkin(@NotNull UUID uuid) {
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
    public static @NotNull CompletableFuture<PlayerTexturesImpl> getSkin(@NotNull String username) {
        return CompletableFuture.supplyAsync(() -> getSkin(getUUID(username).join()).join());
    }

}
