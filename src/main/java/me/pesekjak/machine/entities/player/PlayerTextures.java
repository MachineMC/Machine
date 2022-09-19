package me.pesekjak.machine.entities.player;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;

public record PlayerTextures(String value, String signature, URL skinUrl, @Nullable URL capeUrl, SkinModel skinModel) {

    public static PlayerTextures buildSkin(String value, String signature) {
        try {
            JsonElement decoded = new JsonParser().parse(new InputStreamReader(new ByteArrayInputStream(Base64.getDecoder().decode(value))));
            if (!decoded.isJsonObject())
                return null;
            JsonObject textures = decoded.getAsJsonObject().getAsJsonObject("textures");
            JsonObject skinJson = textures.getAsJsonObject("SKIN");
            URL skinUrl = new URL(skinJson.get("url").getAsString());
            URL capeUrl = textures.has("CAPE") ? new URL(textures.getAsJsonObject("CAPE").get("url").getAsString()) : null;
            SkinModel skinModel = skinJson.has("metadata") ?
                    SkinModel.valueOf(skinJson.get("metadata").getAsJsonObject().get("model").getAsString().toUpperCase()) : SkinModel.CLASSIC;
            return new PlayerTextures(value, signature, skinUrl, capeUrl, skinModel);
        }
        catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static PlayerTextures buildSkin(JsonElement jsonElement) {
        if (!jsonElement.isJsonObject())
            return null;
        JsonObject texturesJson = jsonElement.getAsJsonObject();
        if (!(texturesJson.has("value") || texturesJson.has("signature")))
            return null;
        String value = texturesJson.get("value").getAsString();
        String signature = texturesJson.get("signature").getAsString();
        return buildSkin(value, signature);
    }

}
