package me.pesekjak.machine.entities.player;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import me.pesekjak.machine.auth.MojangAuth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Default implementation for the player profile.
 */
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PlayerProfileImpl implements PlayerProfile {

    private @NotNull String username;
    private final @NotNull UUID uuid;
    private final @Nullable PlayerTextures textures;
    private final boolean online;

    /**
     * Creates new player profile implementation for player in online mode.
     * @param username username of the player
     * @param uuid uuid of the player
     * @param textures textures of the player
     * @return new player profile
     */
    public static @NotNull PlayerProfile online(@NotNull String username, @NotNull UUID uuid, @Nullable PlayerTexturesImpl textures) {
        return new PlayerProfileImpl(
                username,
                uuid,
                textures,
                true
        );
    }

    /**
     * Creates new player profile implementation for player in offline mode.
     * @param username username of the player
     * @return new player profile
     */
    public static @NotNull PlayerProfile offline(@NotNull String username) {
        return new PlayerProfileImpl(
                username,
                MojangAuth.getOfflineUUID(username),
                null,
                false
        );
    }

}
