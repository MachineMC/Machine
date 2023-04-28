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
package org.machinemc.api.file;

import org.machinemc.api.server.ServerProperty;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.api.world.Difficulty;
import org.machinemc.api.world.WorldType;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.machinemc.scriptive.components.Component;

import java.awt.image.BufferedImage;

public interface ServerProperties extends ServerFile, ServerProperty {

    /**
     * @return server ip defined in the server's properties
     */
    String getServerIp();

    /**
     * @return server port defined in the server's properties
     */
    @Range(from = 0, to = 65536) int getServerPort();

    /**
     * @return online-mode option in server properties
     */
    boolean isOnline();

    /**
     * @return max players count defined in the server's properties
     */
    int getMaxPlayers();

    /**
     * @return server's motd defined in the server's properties
     */
    Component getMotd();

    /**
     * @return name of the default world defined in server's properties
     */
    NamespacedKey getDefaultWorld();

    /**
     * @return default difficulty used by the server defined in server's properties
     */
    Difficulty getDefaultDifficulty();

    /**
     * @return default world type used by the server defined in server's properties
     */
    WorldType getDefaultWorldType();

    /**
     * @return reduced-debug-screen option in server properties
     */
    boolean isReducedDebugScreen();

    /**
     * @return view distance defined in the server's properties
     */
    int getViewDistance();

    /**
     * @return simulation distance defined in the server's properties
     */
    int getSimulationDistance();

    /**
     * @return tps defined in the server's properties
     */
    int getTps();

    /**
     * @return server responsiveness defined in the server's properties
     */
    int getServerResponsiveness();

    /**
     * @return server's brand defined in the server's properties
     */
    String getServerBrand();

    /**
     * @return server's icon
     */
    @Nullable BufferedImage getIcon();

    /**
     * @return encoded server icon
     */
    @Nullable String getEncodedIcon();

}
