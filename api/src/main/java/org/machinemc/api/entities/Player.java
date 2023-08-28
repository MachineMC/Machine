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
package org.machinemc.api.entities;

import org.jetbrains.annotations.Nullable;
import org.machinemc.api.chat.ChatMode;
import org.machinemc.api.chat.MessageType;
import org.machinemc.api.chat.PlayerMessage;
import org.machinemc.api.commands.CommandExecutor;
import org.machinemc.api.entities.player.Gamemode;
import org.machinemc.api.network.PlayerConnection;
import org.machinemc.api.network.packets.Packet;
import org.machinemc.api.server.NBTSerializable;
import org.machinemc.scriptive.components.Component;

import java.util.Optional;

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
    Optional<Gamemode> getPreviousGamemode();

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
     * @return whether the player should be listed on the player list
     */
    boolean isListed();

    /**
     * Sets whether the player should be listed on the player list.
     * @param listed new value
     */
    void setListed(boolean listed);

    /**
     * @return player's latency
     */
    int getLatency();

    /**
     * Sends disguised chat message to a player.
     * @param message message to send
     * @param type type of the message
     * @param source source
     * @param target target
     */
    void sendMessage(Component message, MessageType type, Component source, @Nullable Component target);

    /**
     * Sends player message to a player.
     * @param message message to send
     */
    void sendMessage(PlayerMessage message);

    /**
     * Deletes previously sent player message from player's chat.
     * @param message message to delete
     */
    void deletePlayerMessage(PlayerMessage message);

    /**
     * Saves players data into server files.
     */
    void save();

}
