package me.pesekjak.machine.entities.player;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import me.pesekjak.machine.auth.MojangAuth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PlayerProfile {

    private String username;
    private final UUID uuid;
    private final PlayerTextures textures;
    private final boolean online;

    public static PlayerProfile online(@NotNull String username, @NotNull UUID uuid, @Nullable PlayerTextures textures) {
        return new PlayerProfile(
                username,
                uuid,
                textures,
                true
        );
    }

    public static PlayerProfile offline(@NotNull String username) {
        return new PlayerProfile(
                username,
                MojangAuth.getOfflineUUID(username),
                null,
                false
        );
    }

}
