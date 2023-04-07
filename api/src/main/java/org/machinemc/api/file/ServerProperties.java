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
