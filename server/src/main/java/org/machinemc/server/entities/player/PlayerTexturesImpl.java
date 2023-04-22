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
package org.machinemc.server.entities.player;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.jetbrains.annotations.Nullable;
import org.machinemc.api.entities.player.PlayerTextures;
import org.machinemc.api.entities.player.SkinModel;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;

/**
 * Default implementation of player textures.
 * @param value texture value
 * @param signature signature of the texture
 * @param skinUrl url for the skin
 * @param capeUrl url for the cape
 * @param skinModel model of the skin
 */
public record PlayerTexturesImpl(String value,
                                 @Nullable String signature,
                                 URL skinUrl,
                                 @Nullable URL capeUrl,
                                 SkinModel skinModel) implements PlayerTextures {

    /**
     * Creates the player textures from given skin's texture value and signature.
     * @param value textures value of the skin
     * @param signature signature of the skin
     * @throws MalformedURLException if texture value contains malformed URL format
     * @throws JsonSyntaxException if texture value contains malformed JSON format
     * @return player textures
     */
    public static PlayerTexturesImpl buildSkin(final String value,
                                               final @Nullable String signature) throws MalformedURLException {
        final JsonElement decoded = new JsonParser().parse(new String(Base64.getDecoder().decode(value)));
        if (!decoded.isJsonObject()) throw new JsonSyntaxException("Texture value of the skin contains "
                + "malformed JSON format");
        final JsonObject textures = decoded.getAsJsonObject().getAsJsonObject("textures");
        final JsonObject skinJson = textures.getAsJsonObject("SKIN");
        final URL skinUrl = new URL(skinJson.get("url").getAsString());
        final URL capeUrl = textures.has("CAPE")
                ? new URL(textures.getAsJsonObject("CAPE").get("url").getAsString())
                : null;
        final SkinModel skinModel = skinJson.has("metadata")
                ? SkinModel.valueOf(skinJson.get("metadata")
                        .getAsJsonObject()
                        .get("model")
                        .getAsString()
                        .toUpperCase())
                : SkinModel.CLASSIC;
        return new PlayerTexturesImpl(value, signature, skinUrl, capeUrl, skinModel);
    }

    /**
     * Creates new player textures from a json object.
     * @param jsonElement json of the player textures
     * @return player textures from the json
     */
    public static @Nullable PlayerTexturesImpl buildSkin(final JsonElement jsonElement) {
        if (!jsonElement.isJsonObject())
            return null;
        final JsonObject texturesJson = jsonElement.getAsJsonObject();
        if (!(texturesJson.has("value") && texturesJson.has("signature")))
            return null;
        try {
            return buildSkin(
                    texturesJson.get("value").getAsString(),
                    texturesJson.get("signature").getAsString()
            );
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

}
