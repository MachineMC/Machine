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
 * If not, see <https://www.gnu.org/licenses/>.
 */
package org.machinemc.server.auth;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.machinemc.server.entities.player.PlayerTexturesImpl;
import org.machinemc.server.utils.UUIDUtils;

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
     * @param serverId encrypted id of the server
     * @param username player's username
     * @return obtained json
     */
    public static CompletableFuture<JsonObject> getAuthData(final String serverId, final String username) {
        final String url = String.format(MojangAuth.AUTH_URL, username, serverId);
        return CompletableFuture.supplyAsync(() -> {
            try {
                final HttpRequest request = HttpRequest.newBuilder(new URI(url))
                        .timeout(Duration.ofSeconds(5))
                        .GET().build();
                final HttpResponse<String> response = HttpClient.newHttpClient()
                        .send(request, HttpResponse.BodyHandlers.ofString());
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
    public static UUID getOfflineUUID(final String username) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Returns online UUID of registered account.
     * @param username username of the account
     * @return online UUID of account, null if the account doesn't exist
     */
    public static CompletableFuture<UUID> getUUID(final String username) {
        final String url = String.format(USER_PROFILE_URL, username);
        return CompletableFuture.supplyAsync(() -> {
            try {
                final HttpRequest request = HttpRequest.newBuilder(new URI(url))
                        .timeout(Duration.ofSeconds(5))
                        .GET().build();
                final HttpResponse<String> response = HttpClient.newHttpClient()
                        .send(request, HttpResponse.BodyHandlers.ofString());
                if (!(response.statusCode() == HttpURLConnection.HTTP_OK))
                    return null;
                final JsonObject json = (JsonObject) new JsonParser().parse(response.body());
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
    public static CompletableFuture<PlayerTexturesImpl> getSkin(final UUID uuid) {
        final String url = String.format(MINECRAFT_PROFILE_URL, uuid);
        return CompletableFuture.supplyAsync(() -> {
            try {
                final HttpRequest request = HttpRequest.newBuilder(new URI(url))
                        .timeout(Duration.ofSeconds(5))
                        .GET().build();
                final HttpResponse<String> response = HttpClient.newHttpClient()
                        .send(request, HttpResponse.BodyHandlers.ofString());
                if (!(response.statusCode() == HttpURLConnection.HTTP_OK))
                    return null;
                final JsonObject json = (JsonObject) new JsonParser().parse(response.body());
                final JsonObject properties = json.getAsJsonArray("properties").get(0).getAsJsonObject();
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
    public static CompletableFuture<PlayerTexturesImpl> getSkin(final String username) {
        return CompletableFuture.supplyAsync(() -> getSkin(getUUID(username).join()).join());
    }

}
