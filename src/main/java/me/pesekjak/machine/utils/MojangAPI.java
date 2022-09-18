package me.pesekjak.machine.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.pesekjak.machine.entities.player.PlayerTextures;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.regex.Pattern;

public class MojangAPI {

    private MojangAPI() {
        throw new UnsupportedOperationException();
    }

    public static final String API = "https://api.mojang.com/";
    public static final String SESSION_SERVER = "https://sessionserver.mojang.com/";

    public static final Pattern noDashesUUIDPattern = Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})");

    public static UUID getUUID(String name, boolean onlineMode) {
        if (!onlineMode)
            return UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8));
        try {
            URL url = new URL(API.concat("users/profiles/minecraft/" + name));
            JsonElement jsonElement = new JsonParser().parse(new InputStreamReader(url.openStream()));
            if (jsonElement.isJsonNull())
                return null;
            String withoutDashes = jsonElement.getAsJsonObject().get("id").getAsString();
            return UUID.fromString(noDashesUUIDPattern.matcher(withoutDashes).replaceAll("$1-$2-$3-$4-$5"));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static PlayerTextures getSkin(UUID uuid) {
        try {
            URL url = new URL(SESSION_SERVER.concat("session/minecraft/profile/" + uuid + "?unsigned=false"));
            JsonElement jsonElement = new JsonParser().parse(new InputStreamReader(url.openStream()));
            if (!jsonElement.isJsonObject())
                return null;

            JsonObject json = jsonElement.getAsJsonObject();
            JsonObject properties = json.getAsJsonArray("properties").get(0).getAsJsonObject();
            return PlayerTextures.buildSkin(properties);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static PlayerTextures getSkin(String name) {
        return getSkin(getUUID(name, true));
    }

}