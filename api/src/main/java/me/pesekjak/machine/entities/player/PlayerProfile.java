package me.pesekjak.machine.entities.player;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
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
    @NotNull @NonNls String getUsername();

    /**
     * @return player's uuid
     */
    @NotNull UUID getUuid();

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
