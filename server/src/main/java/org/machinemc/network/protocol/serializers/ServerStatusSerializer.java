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
package org.machinemc.network.protocol.serializers;

import com.google.common.base.Preconditions;
import com.google.gson.*;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.machinemc.barebones.profile.GameProfile;
import org.machinemc.scriptive.components.ClientComponent;
import org.machinemc.server.ServerStatus;
import org.machinemc.paklet.DataVisitor;
import org.machinemc.paklet.serialization.Serializer;
import org.machinemc.paklet.serialization.SerializerContext;
import org.machinemc.paklet.serialization.Supports;
import org.machinemc.scriptive.components.Component;
import org.machinemc.scriptive.serialization.JSONPropertiesSerializer;
import org.machinemc.text.ComponentProcessor;

import java.util.List;
import java.util.UUID;

/**
 * Network serializer for {@link ServerStatus}.
 * <p>
 * This is only used by the status packets.
 */
@RequiredArgsConstructor
@Supports(ServerStatus.class)
public class ServerStatusSerializer implements Serializer<ServerStatus> {

    private final Gson gson;
    private final ComponentProcessor componentProcessor;
    private final JSONPropertiesSerializer propertiesSerializer = new JSONPropertiesSerializer();

    @Override
    public void serialize(final SerializerContext context, final DataVisitor visitor, final ServerStatus serverStatus) {
        final JsonObject response = new JsonObject();

        response.add("version", getVersionJSON(serverStatus.version()));

        final JsonObject players;
        if ((players = getPlayersJSON(serverStatus.players())) != null)
            response.add("players", players);

        if (serverStatus.description() != null) {
            final ClientComponent transformed = componentProcessor.transform(serverStatus.description());
            final String descriptionJSON = componentProcessor.getSerializer().serialize(transformed, propertiesSerializer);
            final JsonObject description = JsonParser.parseString(descriptionJSON).getAsJsonObject();
            response.add("description", description);
        }

        if (serverStatus.favicon() != null)
            response.addProperty("favicon", serverStatus.favicon().asString());

        response.addProperty("enforcesSecureChat", serverStatus.enforcesSecureChat());

        final String responseString = gson.toJson(response);
        context.serializerProvider().getFor(String.class).serialize(context, visitor, responseString);
    }

    @Override
    public ServerStatus deserialize(final SerializerContext context, final DataVisitor visitor) {
        final String responseString = context.serializerProvider().getFor(String.class).deserialize(context, visitor);
        final JsonObject response = JsonParser.parseString(responseString).getAsJsonObject();

        final ServerStatus.Version version = fromVersionJSON(response.getAsJsonObject("version"));

        ServerStatus.Players players = null;
        if (response.has("players"))
            players = fromPlayersJSON(response.getAsJsonObject("players"));

        Component description = null;
        if (response.has("description")) {
            description = componentProcessor.getSerializer().deserialize(response.getAsJsonPrimitive("description").getAsString(), propertiesSerializer);
        }

        ServerStatus.Favicon favicon = null;
        if (response.has("favicon"))
            favicon = ServerStatus.Favicon.fromString(response.getAsJsonPrimitive("favicon").getAsString());

        boolean enforcesSecureChat = false;
        if (response.has("enforcesSecureChat"))
            enforcesSecureChat = response.getAsJsonPrimitive("enforcesSecureChat").getAsBoolean();

        return new ServerStatus(version, players, description, favicon, enforcesSecureChat);
    }

    /**
     * Converts {@link ServerStatus.Version} to JSON.
     *
     * @param version version
     * @return json
     */
    private JsonObject getVersionJSON(final ServerStatus.Version version) {
        final JsonObject json = new JsonObject();
        json.addProperty("protocol", version.protocolVersion());
        if (version.version() != null) json.addProperty("name", version.version());
        return json;
    }

    /**
     * Converts JSON to {@link ServerStatus.Version}.
     *
     * @param json json
     * @return version
     */
    private ServerStatus.Version fromVersionJSON(final JsonObject json) {
        return new ServerStatus.Version(
                json.has("name")
                        ? json.getAsJsonPrimitive("name").getAsString()
                        : null,
                json.getAsJsonPrimitive("protocol").getAsInt()
        );
    }

    /**
     * Converts {@link ServerStatus.Players} to JSON.
     *
     * @param players players
     * @return json
     */
    private @Nullable JsonObject getPlayersJSON(final @Nullable ServerStatus.Players players) {
        if (players == null) return null;
        final JsonObject json = new JsonObject();
        json.addProperty("max", players.max());
        json.addProperty("online", players.online());
        if (players.hasSample()) {
            final JsonArray sampleArray = new JsonArray();
            final List<GameProfile> sample = players.sample();
            Preconditions.checkNotNull(sample);
            for (final GameProfile profile : players.sample()) {
                final JsonObject profileJSON = new JsonObject();
                profileJSON.addProperty("name", profile.name());
                profileJSON.addProperty("id", profile.uuid().toString());
                sampleArray.add(profileJSON);
            }
            json.add("sample", sampleArray);
        }
        return json;
    }

    /**
     * Converts JSON to {@link ServerStatus.Players}.
     *
     * @param json json
     * @return players
     */
    private @Nullable ServerStatus.Players fromPlayersJSON(final @Nullable JsonObject json) {
        if (json == null) return null;
        final int max = json.getAsJsonPrimitive("max").getAsInt();
        final int online = json.getAsJsonPrimitive("online").getAsInt();
        if (!json.has("sample")) return new ServerStatus.Players(max, online, null);
        final List<GameProfile> sample = json.getAsJsonArray("sample").asList().stream()
                .map(JsonElement::getAsJsonObject)
                .map(jsonSample -> {
                    final String name = jsonSample.getAsJsonPrimitive("name").getAsString();
                    final UUID id = UUID.fromString(jsonSample.getAsJsonPrimitive("id").getAsString());
                    return new GameProfile(id, name);
                })
                .toList();
        return new ServerStatus.Players(max, online, sample);
    }

}
