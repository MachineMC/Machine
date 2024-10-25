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
package org.machinemc.api.entities.player;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.jetbrains.annotations.Nullable;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.api.utils.Writable;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents player's skin textures.
 * @param value texture value
 * @param signature signature of the texture
 * @param skinURL url for the skin
 * @param capeURL url for the cape
 * @param skinModel model of the skin
 */
public record PlayerTextures(String value,
                             @Nullable String signature,
                             URL skinURL,
                             @Nullable URL capeURL,
                             SkinModel skinModel) implements Writable {

    /**
     * Creates the player textures from given skin's texture value and signature.
     * @param value textures value of the skin
     * @param signature signature of the skin
     * @throws MalformedURLException if texture value contains malformed URL format
     * @throws JsonSyntaxException if texture value contains malformed JSON format
     * @return player textures
     */
    public static PlayerTextures buildSkin(final String value,
                                           final @Nullable String signature) throws MalformedURLException, URISyntaxException {
        final JsonElement decoded = JsonParser.parseString(new String(Base64.getDecoder().decode(value)));
        if (!decoded.isJsonObject()) throw new JsonSyntaxException("Texture value of the skin contains "
                + "malformed JSON format");
        final JsonObject textures = decoded.getAsJsonObject().getAsJsonObject("textures");
        final JsonObject skinJson = textures.getAsJsonObject("SKIN");
        final URL skinURL = new URI(skinJson.get("url").getAsString()).toURL();
        final URL capeURL = textures.has("CAPE")
                ? new URI(textures.getAsJsonObject("CAPE").get("url").getAsString()).toURL()
                : null;
        final SkinModel skinModel = skinJson.has("metadata")
                ? SkinModel.valueOf(skinJson.get("metadata")
                        .getAsJsonObject()
                        .get("model")
                        .getAsString()
                        .toUpperCase())
                : SkinModel.CLASSIC;
        return new PlayerTextures(value, signature, skinURL, capeURL, skinModel);
    }

    /**
     * Creates new player textures from a json object.
     * @param jsonElement json of the player textures
     * @return player textures from the json
     */
    public static Optional<PlayerTextures> buildSkin(final JsonElement jsonElement) {
        if (!jsonElement.isJsonObject())
            return Optional.empty();
        final JsonObject texturesJson = jsonElement.getAsJsonObject();
        if (!(texturesJson.has("value") && texturesJson.has("signature")))
            return Optional.empty();
        try {
            return Optional.of(buildSkin(
                    texturesJson.get("value").getAsString(),
                    texturesJson.get("signature").getAsString()
            ));
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    public PlayerTextures {
        Objects.requireNonNull(value, "Value of the skin can not be null");
        Objects.requireNonNull(skinURL, "Skin url can not be null");
        Objects.requireNonNull(skinModel, "Skin model can not be null");
    }

    /**
     * Writes the player textures into a buffer.
     * @param buf buffer to write into
     */
    @Override
    public void write(final ServerBuffer buf) {
        buf.writeTextures(this);
    }

}
