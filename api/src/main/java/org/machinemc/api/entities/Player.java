package org.machinemc.api.entities;

import org.machinemc.api.chat.ChatMode;
import org.machinemc.api.entities.player.*;
import org.machinemc.api.network.PlayerConnection;
import org.machinemc.api.network.packets.Packet;
import org.machinemc.api.server.NBTSerializable;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.*;

/**
 * Represents a player on the server.
 */
public interface Player extends HumanEntity, Audience, NBTSerializable {

    /**
     * @return player's connection
     */
    @NotNull PlayerConnection getConnection();

    /**
     * Sends a packet to the player.
     * @param packet packet to send
     */
    void sendPacket(@NotNull Packet packet);

    /**
     * @return previous gamemode used by the player
     */
    @Nullable Gamemode getPreviousGamemode();

    /**
     * @return player's locale
     */
    @NonNls @NotNull String getLocale();

    /**
     * @return player's view distance
     */
    @Range(from = 0, to = Byte.MAX_VALUE) byte getViewDistance();

    /**
     * @return chat mode used by the player
     */
    @NotNull ChatMode getChatMode();

    /**
     * @return player's latency
     */
    int getLatency();

    /**
     * Saves players data into server files.
     */
    void save();

}
