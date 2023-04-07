package org.machinemc.api.network;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.machinemc.api.auth.PublicKeyData;
import org.machinemc.api.entities.Player;
import org.machinemc.api.network.packets.Packet;
import org.machinemc.api.server.ServerProperty;
import org.jetbrains.annotations.*;
import org.machinemc.scriptive.components.Component;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.machinemc.api.network.packets.Packet.PacketState.*;

/**
 * Represents a connection of a client.
 */
public interface PlayerConnection extends ServerProperty, AutoCloseable {

    /**
     * @return client state of the connection
     */
    ClientState getClientState();

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
    Player getOwner();

    /**
     * Starts listening to incoming the packets.
     */
    @Async.Execute
    void start();

    /**
     * Disconnects the client and closes the connection.
     * @param reason reason for the disconnection
     */
    void disconnect(Component reason);

    /**
     * Sends packet to the connection.
     * @param packet packet to send
     * @return if the operation was successful
     * @throws IOException if an I/O error occurs during writing the bytes
     */
    boolean sendPacket(Packet packet) throws IOException;

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
        public static ClientState[] fromState(Packet.PacketState state) {
            final Set<ClientState> clientStates = new LinkedHashSet<>();
            for(ClientState clientState : values()) {
                if(clientState.in == state || clientState.out == state)
                    clientStates.add(clientState);
            }
            return clientStates.toArray(new ClientState[0]);
        }
    }

}
