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
 * If not, see <https://www.gnu.org/licenses/>.
 */
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
