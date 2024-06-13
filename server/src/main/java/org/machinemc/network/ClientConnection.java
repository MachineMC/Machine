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
package org.machinemc.network;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.FutureCallback;
import io.netty.channel.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.machinemc.network.protocol.ConnectionState;
import org.machinemc.network.protocol.Packet;
import org.machinemc.network.protocol.PacketListener;
import org.machinemc.network.protocol.listeners.ServerHandshakePacketListener;

import javax.annotation.Nullable;

/**
 * Represents a client connected to the server.
 * <p>
 * At the same time works as a handler of incoming packets.
 */
@RequiredArgsConstructor
public class ClientConnection extends SimpleChannelInboundHandler<Packet<PacketListener>> {

    private final Channel channel;

    @Getter
    private ConnectionState incomingState, outgoingState;

    private PacketListener packetListener;

    @Override
    public void handlerAdded(final ChannelHandlerContext ctx) {
        // handshaking is the initial state
        setupInboundProtocol(ConnectionState.HANDSHAKING, new ServerHandshakePacketListener(this));
        setupOutboundProtocol(ConnectionState.HANDSHAKING);
    }

    /**
     * Handles incoming packets from the client.
     *
     * @param channelHandlerContext context
     * @param packet incoming packet
     */
    @Override
    protected void channelRead0(final ChannelHandlerContext channelHandlerContext, final Packet<PacketListener> packet) {
        if (!channel.isOpen()) return;
        Preconditions.checkNotNull(packetListener, "Received packets before initialization");
        packet.handle(packetListener);
    }

    /**
     * Sends outgoing packet to the client.
     *
     * @param packet packet
     * @param flush whether to flush the channel
     * @param <T> packet type
     */
    public <T extends Packet<?>> void sendPacket(final T packet, final boolean flush) {
        sendPacket(packet, flush, null);
    }

    /**
     * Sends outgoing packet to the client.
     *
     * @param packet packet
     * @param flush whether to flush the channel
     * @param callback future to run after packet has been sent
     * @see org.machinemc.utils.FunctionalFutureCallback
     * @param <T> packet type
     */
    public <T extends Packet<?>> void sendPacket(final T packet, final boolean flush, final @Nullable FutureCallback<T> callback) {
        final EventLoop eventLoop = channel.eventLoop();
        if (!eventLoop.inEventLoop()) {
            eventLoop.execute(() -> sendPacket(packet, flush, callback));
            return;
        }

        final ChannelFuture future = flush
                ? channel.writeAndFlush(packet)
                : channel.write(packet);
        if (callback == null) return;

        future.addListener(f -> {
            if (f.isSuccess()) callback.onSuccess(packet);
            else callback.onFailure(f.cause());
        });
    }

    /**
     * Changes the state for incoming (server-bound) packets.
     *
     * @param state new connection state
     * @param packetListener new packet listener
     */
    public void setupInboundProtocol(final ConnectionState state, final PacketListener packetListener) {
        Preconditions.checkNotNull(state, "Connection state can not be null");
        Preconditions.checkNotNull(packetListener, "Packet Listener can not be null");
        incomingState = state;
        this.packetListener = packetListener;
    }

    /**
     * Changes the state for outgoing (client-bound) packets.
     *
     * @param state new connection state
     */
    // packet listener is provided only for incoming packets because we do
    // not listen to outgoing packets because that makes no sense
    public void setupOutboundProtocol(final ConnectionState state) {
        outgoingState = Preconditions.checkNotNull(state, "Connection state can not be null");
    }

}
