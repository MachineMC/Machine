package org.machinemc.server.entities.player;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.machinemc.server.auth.MojangAuth;
import org.jetbrains.annotations.Nullable;
import org.machinemc.api.entities.player.PlayerProfile;
import org.machinemc.api.entities.player.PlayerTextures;

import java.util.UUID;

/**
 * Default implementation for the player profile.
 */
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PlayerProfileImpl implements PlayerProfile {

    private String username;
    private final UUID uuid;
    private final @Nullable PlayerTextures textures;
    private final boolean online;

    /**
     * Creates new player profile implementation for player in online mode.
     * @param username username of the player
     * @param uuid uuid of the player
     * @param textures textures of the player
     * @return new player profile
     */
    public static PlayerProfile online(final String username,
                                       final UUID uuid,
                                       final @Nullable PlayerTexturesImpl textures) {
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
    public static PlayerProfile offline(final String username) {
        return new PlayerProfileImpl(
                username,
                MojangAuth.getOfflineUUID(username),
                null,
                false
        );
    }

}
