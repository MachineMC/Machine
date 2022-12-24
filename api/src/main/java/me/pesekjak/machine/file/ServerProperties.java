package me.pesekjak.machine.file;

import me.pesekjak.machine.server.ServerProperty;
import me.pesekjak.machine.utils.NamespacedKey;
import me.pesekjak.machine.world.Difficulty;
import me.pesekjak.machine.world.WorldType;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.awt.image.BufferedImage;

public interface ServerProperties extends ServerFile, ServerProperty {

    /**
     * @return server ip defined in the server's properties
     */
    @NotNull @NonNls String getServerIp();

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
    @NotNull Component getMotd();

    /**
     * @return name of the default world defined in server's properties
     */
    @NotNull NamespacedKey getDefaultWorld();

    /**
     * @return default difficulty used by the server defined in server's properties
     */
    @NotNull Difficulty getDefaultDifficulty();

    /**
     * @return default world type used by the server defined in server's properties
     */
    @NotNull WorldType getDefaultWorldType();

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
    @NotNull @NonNls String getServerBrand();

    /**
     * @return server's icon
     */
    @Nullable BufferedImage getIcon();

    /**
     * @return encoded server icon
     */
    @Nullable @NonNls String getEncodedIcon();

}
