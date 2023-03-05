package org.machinemc.api.entities.player;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Container with additional player information
 * from auth server, such as UUID, name, and textures.
 */
public interface PlayerProfile {

    /**
     * @return player's username
     */
    String getUsername();

    /**
     * @return player's uuid
     */
    UUID getUuid();

    /**
     * @return player's skin textures
     */
    @Nullable PlayerTextures getTextures();

    /**
     * @return if the profile was created with information from
     * auth server
     */
    boolean isOnline();

}
