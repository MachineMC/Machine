package org.machinemc.api.entities;

import org.jetbrains.annotations.Nullable;
import org.machinemc.api.chat.ChatMode;
import org.machinemc.api.commands.CommandExecutor;
import org.machinemc.api.entities.player.*;
import org.machinemc.api.network.PlayerConnection;
import org.machinemc.api.network.packets.Packet;
import org.machinemc.api.server.NBTSerializable;

/**
 * Represents a player on the server.
 */
public interface Player extends HumanEntity, CommandExecutor, NBTSerializable {

    /**
     * @return player's connection
     */
    PlayerConnection getConnection();

    /**
     * Sends a packet to the player.
     * @param packet packet to send
     */
    void sendPacket(Packet packet);

    /**
     * @return previous gamemode used by the player
     */
    @Nullable Gamemode getPreviousGamemode();

    /**
     * @return player's locale
     */
    String getLocale();

    /**
     * @return player's view distance
     */
    byte getViewDistance();

    /**
     * @return chat mode used by the player
     */
    ChatMode getChatMode();

    /**
     * @return player's latency
     */
    int getLatency();

    /**
     * Saves players data into server files.
     */
    void save();

}
