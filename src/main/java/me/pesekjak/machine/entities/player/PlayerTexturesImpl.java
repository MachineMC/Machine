package me.pesekjak.machine.entities.player;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;

/**
 * Default implementation of player textures.
 */
public record PlayerTexturesImpl(@NotNull String value, @Nullable String signature, @NotNull URL skinUrl, @Nullable URL capeUrl, @NotNull SkinModel skinModel) implements PlayerTextures {

    /**
     * Creates the player textures from given skin's texture value and signature.
     * @param value textures value of the skin
     * @param signature signature of the skin
     * @throws MalformedURLException if texture value contains malformed URL format
     * @throws JsonSyntaxException if texture value contains malformed JSON format
     * @return player textures
     */
    public static @NotNull PlayerTexturesImpl buildSkin(@NotNull String value, @Nullable String signature) throws MalformedURLException {
        JsonElement decoded = new JsonParser().parse(new String(Base64.getDecoder().decode(value)));
        if (!decoded.isJsonObject()) throw new JsonSyntaxException("Texture value of the skin contains malformed JSON format");
        JsonObject textures = decoded.getAsJsonObject().getAsJsonObject("textures");
        JsonObject skinJson = textures.getAsJsonObject("SKIN");
        URL skinUrl = new URL(skinJson.get("url").getAsString());
        URL capeUrl = textures.has("CAPE") ? new URL(textures.getAsJsonObject("CAPE").get("url").getAsString()) : null;
        SkinModel skinModel = skinJson.has("metadata") ?
                SkinModel.valueOf(skinJson.get("metadata").getAsJsonObject().get("model").getAsString().toUpperCase()) : SkinModel.CLASSIC;
        return new PlayerTexturesImpl(value, signature, skinUrl, capeUrl, skinModel);
    }

    /**
     * Creates new player textures from a json object.
     * @param jsonElement json of the player textures
     * @return player textures from the json
     */
    public static @Nullable PlayerTexturesImpl buildSkin(@NotNull JsonElement jsonElement) {
        if (!jsonElement.isJsonObject())
            return null;
        JsonObject texturesJson = jsonElement.getAsJsonObject();
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
