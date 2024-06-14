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
package org.machinemc.network.protocol.legacy;

/**
 * Represents a type of server status request sent with legacy
 * Minecraft clients.
 */
public enum LegacyPingType {

    /**
     * Server ping sent between versions b1.8 and 1.3.
     */
    V1_3,

    /**
     * Server ping sent between versions 1.4 and 1.5.
     */
    V1_5,

    /**
     * Server ping sent in version 1.6.
     */
    V1_6

}
