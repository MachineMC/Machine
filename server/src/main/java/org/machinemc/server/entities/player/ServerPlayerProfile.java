/*
 * This file is part of Machine.
 *
 * Machine is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Machine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Machine.
 * If not, see https://www.gnu.org/licenses/.
 */
package org.machinemc.server.entities.player;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.machinemc.api.entities.player.PlayerProfile;
import org.machinemc.api.entities.player.PlayerTextures;
import org.machinemc.server.auth.MojangAuth;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Default implementation for the player profile.
 */
@Data
public final class ServerPlayerProfile implements PlayerProfile {

    private String username;
    @Getter(AccessLevel.NONE)
    private final UUID uuid;
    @Getter(AccessLevel.NONE)
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
                                       final @Nullable PlayerTextures textures) {
        return new ServerPlayerProfile(
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
        return new ServerPlayerProfile(
                username,
                MojangAuth.getOfflineUUID(username),
                null,
                false
        );
    }

    private ServerPlayerProfile(final String username,
                                final UUID uuid,
                                final @Nullable PlayerTextures textures,
                                final boolean online) {
        this.username = Objects.requireNonNull(username, "Username can not be null");
        this.uuid = Objects.requireNonNull(uuid, "UUID can not be null");
        this.textures = textures;
        this.online = online;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public Optional<PlayerTextures> getTextures() {
        return Optional.ofNullable(textures);
    }

}
