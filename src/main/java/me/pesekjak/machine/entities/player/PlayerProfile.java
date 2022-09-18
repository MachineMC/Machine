package me.pesekjak.machine.entities.player;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Data
public class PlayerProfile {

    private String username;
    private final UUID uuid;
    private final boolean online;

    public PlayerProfile(String username, UUID uuid, boolean online) {
        this.username = username;
        this.uuid = uuid;
        this.online = online;
    }

    public static PlayerProfile online(@NotNull String username, @NotNull UUID uuid) {
        return new PlayerProfile(
                username,
                uuid,
                true
        );
    }

    public static PlayerProfile offline(@NotNull String username) {
        return new PlayerProfile(
                username,
                UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8)),
                false
        );
    }

}
