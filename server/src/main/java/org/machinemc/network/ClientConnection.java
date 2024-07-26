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
import lombok.Setter;
import org.machinemc.Machine;
import org.machinemc.entity.player.PlayerSettings;
import org.machinemc.network.protocol.*;
import org.machinemc.network.protocol.handlers.CompressionDecoder;
import org.machinemc.network.protocol.handlers.CompressionEncoder;
import org.machinemc.network.protocol.listeners.ServerHandshakePacketListener;
import org.machinemc.network.protocol.login.clientbound.S2CSetCompressionPacket;
import org.machinemc.utils.FunctionalFutureCallback;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Represents a client connected to the server.
 * <p>
 * At the same time works as a handler of incoming packets.
 */
@Getter
public class ClientConnection extends SimpleChannelInboundHandler<Packet<PacketListener>> {

    private final Machine server;
    private final Channel channel;

    private ConnectionState incomingState, outgoingState;

    private PacketListener packetListener;

    private @Setter PlayerSettings playerSettings;

    public ClientConnection(final Machine server, final Channel channel) {
        this.server = Preconditions.checkNotNull(server, "Server can not be null");
        this.channel = Preconditions.checkNotNull(channel, "Client channel can not be null");
    }

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
    protected void channelRead0(final ChannelHandlerContext channelHandlerContext, final Packet<PacketListener> packet) throws Exception {
        if (!channel.isOpen()) return;
        Preconditions.checkNotNull(packetListener, "Received packets before initialization");

        // runs the Synced packets on the tick thread
        if (!packet.getClass().isAnnotationPresent(Synced.class)) {
            packet.handle(packetListener);
        } else {
            final CompletableFuture<Void> future = server.getTicker().runNextTick(() -> packet.handle(packetListener));
            if (packet.getClass().getAnnotation(Synced.class).blocks()) future.get();
        }
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        server.getLogger().warn("Client generated uncaught exception and has been disconnected", cause);
        channel.close();
    }

    /**
     * Sends outgoing packet to the client.
     *
     * @param packet packet
     * @param flush whether to flush the channel
     * @param <T> packet type
     * @return future
     */
    public <T extends Packet<?>> CompletableFuture<T> sendPacket(final T packet, final boolean flush) {
        final CompletableFuture<T> future = new CompletableFuture<>();
        sendPacket(packet, flush, FunctionalFutureCallback.create(future::complete, future::completeExceptionally));
        return future;
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
        getServer().getLogger().debug("Changing inbound state to: {}", state); // TODO add information about player name if available
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
        getServer().getLogger().debug("Changing outbound state to: {}", state); // TODO add information about player name if available
    }

    /**
     * Changes the compression state for this connection.
     *
     * @param threshold compression threshold, any negative number disables the compression
     * @return future of when compression has been enabled
     */
    public CompletableFuture<Void> setCompression(final int threshold) {
        Preconditions.checkState(incomingState == ConnectionState.LOGIN, "The connection is not in login state");
        return sendPacket(new S2CSetCompressionPacket(threshold), true)
                .handle((result, exception) -> {
                    if (exception != null) return null;
                    final ChannelPipeline pipeline = channel.pipeline();
                    final List<String> handlers = pipeline.names();
                    if (handlers.contains(HandlerNames.COMPRESSION_DECODER)) pipeline.remove(HandlerNames.COMPRESSION_DECODER);
                    if (handlers.contains(HandlerNames.COMPRESSION_ENCODER)) pipeline.remove(HandlerNames.COMPRESSION_ENCODER);
                    if (threshold < 0) return null;
                    pipeline.addAfter(HandlerNames.LENGTH_DECODER, HandlerNames.COMPRESSION_DECODER, new CompressionDecoder());
                    pipeline.addAfter(HandlerNames.LENGTH_ENCODER, HandlerNames.COMPRESSION_ENCODER, new CompressionEncoder(threshold));
                    return null;
                });
    }

}
