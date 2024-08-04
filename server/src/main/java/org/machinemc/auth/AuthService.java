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
package org.machinemc.auth;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.Nullable;
import org.machinemc.barebones.profile.GameProfile;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service for authenticating incoming connections.
 */
@RequiredArgsConstructor
public class AuthService {

    private final URL authService;

    /**
     * Checks for auth data created by client if the game is licensed for the given server id.
     *
     * @param serverHash hash of the server
     * @param username player's username
     * @return obtained json
     */
    @SneakyThrows
    public CompletableFuture<Optional<GameProfile>> getGameProfile(final String serverHash, final String username) {
        Preconditions.checkNotNull(serverHash, "Server ID can not be null");
        Preconditions.checkNotNull(username, "Player username can not be null");

        String serviceURL = authService.toString();
        if (!serviceURL.endsWith("/")) serviceURL = serviceURL + "/";
        serviceURL = String.format(
                serviceURL + "session/minecraft/hasJoined?username=%s&serverId=%s",
                username,
                serverHash
        );
        final URI formattedURL = new URI(serviceURL);

        return CompletableFuture.supplyAsync(() -> {
            final HttpRequest request = HttpRequest.newBuilder(formattedURL).GET().build();
            try (HttpClient client = HttpClient.newHttpClient()) {
                final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != HttpURLConnection.HTTP_OK) return Optional.empty();
                final JsonObject json = (JsonObject) JsonParser.parseString(response.body());
                return Optional.of(parseGameProfile(json));
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        });
    }

    /**
     * Creates game profile from a json object.
     *
     * @param json json
     * @return game profile
     */
    private static GameProfile parseGameProfile(final JsonObject json) {
        final UUID authUUID = parseUUID(json.get("id").getAsString()).orElseThrow();
        final String authUsername = json.get("name").getAsString();
        final List<GameProfile.Property> properties = new ArrayList<>();
        json.getAsJsonArray("properties").asList().stream()
                .map(JsonElement::getAsJsonObject)
                .forEach(property -> {
                    final String name = property.get("name").getAsString();
                    final String value = property.get("value").getAsString();
                    final String signature = property.has("signature")
                            ? property.get("signature").getAsString()
                            : null;
                    properties.add(new GameProfile.Property(name, value, signature));
                });
        return new GameProfile(authUUID, authUsername, properties);
    }

    private static final Pattern UUID_PATTERN = Pattern.compile(
            "^(\\p{XDigit}{8})-?"
                    + "(\\p{XDigit}{4})-?"
                    + "(\\p{XDigit}{4})-?"
                    + "(\\p{XDigit}{4})-?"
                    + "(\\p{XDigit}{12})$");
    private static final @RegExp String DASHES_UUID_REPLACE = "$1-$2-$3-$4-$5";

    /**
     * Parses uuid string both with or without dashes to a classic uuid.
     *
     * @param string the string uuid
     * @return parsed uuid
     */
    private static Optional<UUID> parseUUID(final @Nullable String string) {
        if (string == null) return Optional.empty();
        final Matcher matcher = UUID_PATTERN.matcher(string);
        if (!matcher.matches()) return Optional.empty();
        return Optional.of(UUID.fromString(matcher.replaceFirst(DASHES_UUID_REPLACE)));
    }

}
