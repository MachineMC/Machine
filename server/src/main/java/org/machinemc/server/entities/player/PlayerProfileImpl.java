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
