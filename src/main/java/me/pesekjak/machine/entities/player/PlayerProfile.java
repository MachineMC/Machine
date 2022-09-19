package me.pesekjak.machine.entities.player;

import lombok.Data;
import me.pesekjak.machine.utils.MojangAPI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Data
public class PlayerProfile {

    private String username;
    private final UUID uuid;
    private final PlayerTextures textures;
    private final boolean online;

    public PlayerProfile(String username, UUID uuid, PlayerTextures textures, boolean online) {
        this.username = username;
        this.uuid = uuid;
        this.textures = textures;
        this.online = online;
    }

    public static PlayerProfile online(@NotNull String username, @NotNull UUID uuid, @Nullable PlayerTextures textures) {
        return new PlayerProfile(
                username,
                uuid,
                textures,
                true
        );
    }

    public static PlayerProfile offline(@NotNull String username) {
        PlayerTextures playerTextures = null;
        try {
            playerTextures = MojangAPI.getSkin(username);
        }
        catch (Exception ignore) { }
        return new PlayerProfile(
                username,
                UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8)),
                playerTextures,
                false
        );
    }

}
