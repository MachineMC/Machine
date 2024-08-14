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

/**
 * Status of the resource pack request.
 */
public enum ResourcePackStatus {

    /**
     * The client accepted the pack and is beginning a download of it.
     */
    ACCEPTED,

    /**
     * The client refused to accept the resource pack.
     */
    DECLINED,

    /**
     * The pack was discarded by the client.
     */
    DISCARDED,

    /**
     * The client successfully downloaded the pack.
     */
    DOWNLOADED,

    /**
     * The client accepted the pack, but download failed.
     */
    FAILED_DOWNLOAD,

    /**
     * The client was unable to reload the pack.
     */
    FAILED_RELOAD,

    /**
     * The pack URL was invalid.
     */
    INVALID_URL,

    /**
     * The resource pack has been successfully downloaded and applied to the client.
     */
    SUCCESSFULLY_LOADED

}
