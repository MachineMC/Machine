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
package org.machinemc.client.resourcepack;

import java.util.UUID;

/**
 * Represents an entity capable of accepting and downloading resource packs.
 */
public interface ResourcePackReceiver {

    /**
     * Sends a resource pack request.
     *
     * @param request request
     * @param callback callback
     */
    void sendResourcePack(ResourcePackRequest request, ResourcePackRequest.Callback callback);

    /**
     * Sends a resource pack request.
     *
     * @param request request
     */
    default void sendResourcePack(ResourcePackRequest request) {
        sendResourcePack(request, ((pack, status, receiver) -> { }));
    }

    /**
     * Removes resource pack with given id.
     *
     * @param id id
     */
    void removeResourcePack(UUID id);

    /**
     * Removes resource pack.
     *
     * @param pack pack
     */
    default void removeResourcePack(ResourcePackInfo pack) {
        removeResourcePack(pack.id());
    }

    /**
     * Removes all active resource pack.
     */
    void removeAllResourcePacks();

}
