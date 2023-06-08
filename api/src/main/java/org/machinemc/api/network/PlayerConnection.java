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
package org.machinemc.api.network;

import io.netty.channel.ChannelFuture;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.machinemc.api.auth.PublicKeyData;
import org.machinemc.api.entities.Player;
import org.machinemc.api.network.packets.Packet;
import org.machinemc.api.server.ServerProperty;
import org.jetbrains.annotations.*;
import org.machinemc.scriptive.components.Component;
import org.machinemc.scriptive.components.TranslationComponent;

import java.net.InetSocketAddress;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.machinemc.api.network.packets.Packet.PacketState.*;

/**
 * Represents a connection of a client.
 */
public interface PlayerConnection extends ServerProperty {

    /**
     * @return client state of the connection
     */
    ClientState getState();

    /**
     * @return public key data of the connection
     */
    @Nullable PublicKeyData getPublicKeyData();

    /**
     * @return login username of the connection
     */
    String getLoginUsername();

    /**
     * @return address with which the client connected
     */
    InetSocketAddress getAddress();

    /**
     * @return owner of the connection
     */
    @Nullable Player getOwner();

    /**
     * Disconnects the client and closes the connection.
     * @param reason reason for the disconnection
     * @return close channel future
     */
    ChannelFuture disconnect(Component reason);

    /**
     * Disconnects the player with the default reason.
     */
    default void disconnect() {
        disconnect(TranslationComponent.of("disconnect.disconnected"));
    }

    /**
     * Closes the client connection.
     * @return close channel future
     */
    ChannelFuture close();

    /**
     * Sends packet to the connection.
     * @param packet packet to send
     * @return send message future
     */
    ChannelFuture send(Packet packet);

    /**
     * Whether the connection is open and can receive packets.
     * @return whether the connection is open
     */
    boolean isOpen();

    /**
     * Client state of the connection, use to determinate the correct
     * group of packets to write/read.
     */
    @Getter
    @AllArgsConstructor
    enum ClientState {
        HANDSHAKE(HANDSHAKING_IN, HANDSHAKING_OUT),
        STATUS(STATUS_IN, STATUS_OUT),
        LOGIN(LOGIN_IN, LOGIN_OUT),
        PLAY(PLAY_IN, PLAY_OUT),
        DISCONNECTED(null, null);

        protected final @Nullable Packet.PacketState in;
        protected final @Nullable Packet.PacketState out;

        /**
         * Returns client states using the given packet state.
         * @param state state of the client state
         * @return client states
         */
        @Contract(pure = true)
        public static ClientState[] fromState(final Packet.PacketState state) {
            final Set<ClientState> clientStates = new LinkedHashSet<>();
            for (final ClientState clientState : values()) {
                if (clientState.in == state || clientState.out == state)
                    clientStates.add(clientState);
            }
            return clientStates.toArray(new ClientState[0]);
        }
    }

}
