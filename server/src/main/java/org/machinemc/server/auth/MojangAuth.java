/*
 * This file is part of Machine.
 *
 * Machine is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Machine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Machine.
 * If not, see https://www.gnu.org/licenses/.
 */
package org.machinemc.server.auth;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.machinemc.api.entities.player.PlayerTextures;
import org.machinemc.server.utils.UUIDUtils;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Utility class for interacting with Mojang auth.
 */
public final class MojangAuth {

    // TODO Ability to change these in java arguments on start
    public static final String AUTH_URL
            = "https://sessionserver.mojang.com/session/minecraft/hasJoined?username=%s&serverId=%s";
    public static final String USER_PROFILE_URL
            = "https://api.mojang.com/users/profiles/minecraft/%s";
    public static final String MINECRAFT_PROFILE_URL
            = "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false";

    private MojangAuth() {
        throw new UnsupportedOperationException();
    }

    /**
     * Checks for auth data created by client if the game is
     * licensed for the given server id.
     * @param serverID encrypted id of the server
     * @param username player's username
     * @return obtained json
     */
    public static CompletableFuture<JsonObject> getAuthData(final String serverID, final String username) {
        Objects.requireNonNull(serverID);
        Objects.requireNonNull(username);
        final String url = String.format(MojangAuth.AUTH_URL, username, serverID);
        return CompletableFuture.supplyAsync(() -> {
            try {
                try (HttpClient client = HttpClient.newHttpClient()) {
                    final HttpRequest request = HttpRequest.newBuilder(new URI(url))
                            .timeout(Duration.ofSeconds(5))
                            .GET().build();
                    final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    if (response.statusCode() == HttpURLConnection.HTTP_OK)
                        return (JsonObject) JsonParser.parseString(response.body());
                }
            } catch (Exception ignored) { }
            return null;
        });
    }

    /**
     * Creates offline mode uuid from a player's nickname.
     * @param username player's nickname
     * @return offline mode uuid
     */
    public static UUID getOfflineUUID(final String username) {
        Objects.requireNonNull(username);
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Returns online UUID of registered account.
     * @param username username of the account
     * @return online UUID of account, empty if the account doesn't exist
     */
    public static CompletableFuture<Optional<UUID>> getUUID(final String username) {
        Objects.requireNonNull(username);
        final String url = String.format(USER_PROFILE_URL, username);
        return CompletableFuture.supplyAsync(() -> {
            try {
                final HttpRequest request = HttpRequest.newBuilder(new URI(url))
                        .timeout(Duration.ofSeconds(5))
                        .GET().build();
                try (HttpClient client = HttpClient.newHttpClient()) {
                    final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    if (!(response.statusCode() == HttpURLConnection.HTTP_OK))
                        return Optional.empty();
                    final JsonObject json = (JsonObject) JsonParser.parseString(response.body());
                    return UUIDUtils.parseUUID(json.get("id").getAsString());
                }
            } catch (Exception ignored) { }
            return Optional.empty();
        });
    }

    /**
     * Returns skin of registered account.
     * @param uuid online uuid of the account
     * @return skin of the registered account, empty if the account doesn't exist
     */
    public static CompletableFuture<Optional<PlayerTextures>> getSkin(final UUID uuid) {
        Objects.requireNonNull(uuid);
        final String url = String.format(MINECRAFT_PROFILE_URL, uuid);
        return CompletableFuture.supplyAsync(() -> {
            try {
                final HttpRequest request = HttpRequest.newBuilder(new URI(url))
                        .timeout(Duration.ofSeconds(5))
                        .GET().build();
                try (HttpClient client = HttpClient.newHttpClient()) {
                    final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    if (!(response.statusCode() == HttpURLConnection.HTTP_OK))
                        return Optional.empty();
                    final JsonObject json = (JsonObject) JsonParser.parseString(response.body());
                    final JsonObject properties = json.getAsJsonArray("properties").get(0).getAsJsonObject();
                    return PlayerTextures.buildSkin(properties);
                }
            } catch (Exception ignored) { }
            return Optional.empty();
        });
    }

    /**
     * Returns skin of registered account.
     * @param username username of the account
     * @return skin of the registered account, empty if the account doesn't exist
     */
    public static CompletableFuture<Optional<PlayerTextures>> getSkin(final String username) {
        Objects.requireNonNull(username);
        return CompletableFuture.supplyAsync(() -> getSkin(getUUID(username).join().orElse(null)).join());
    }

}
