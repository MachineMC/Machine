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
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.machinemc.Machine;
import org.machinemc.network.protocol.HandlerNames;
import org.machinemc.network.protocol.handlers.*;
import org.machinemc.paklet.PacketFactory;

import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Running server using netty library.
 */
public class NettyServer {

    private final Machine server;

    private final PacketFactory packetFactory;
    private final InetSocketAddress address;
    private final TransportType transport;

    private final Set<ClientConnection> connections = ConcurrentHashMap.newKeySet();

    private @Nullable ChannelFuture channelFuture;

    public NettyServer(final Machine server, final PacketFactory packetFactory, final InetSocketAddress address) {
        this(server, packetFactory, address, TransportType.getInstance());
    }

    public NettyServer(final Machine server, final PacketFactory packetFactory, final InetSocketAddress address, final TransportType transport) {
        this.server = Preconditions.checkNotNull(server, "Server can not be null");
        this.packetFactory = Preconditions.checkNotNull(packetFactory, "Packet factory can not be null");
        this.address = Preconditions.checkNotNull(address, "Server Address can not be null");
        this.transport = Preconditions.checkNotNull(transport, "Transport type can not be null");
    }

    /**
     * Create a new server channel and binds it.
     *
     * @return future
     */
    public ChannelFuture bind() {
        Preconditions.checkState(channelFuture == null, "Server channel already exists");
        channelFuture = new ServerBootstrap()
                .channelFactory(transport::createServerSocketChannel)
                .group(transport.createEventLoopGroup(), transport.createEventLoopGroup())
                .option(ChannelOption.SO_BACKLOG, 128)

                .childHandler(new Initializer())

                // We prioritize low latency,
                // TCP_NODELAY disables Nagle's algorithm, this makes channel flushing
                // useless, but we might change it to KEEP_ALIVE someday, thus when sending
                // packets you have to specify flush argument
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.IP_TOS, 0b00011000) // 3 - throughput, 4 - low delay

                .localAddress(address)
                .bind();
        return channelFuture;
    }

    /**
     * Returns server channel future or empty if
     * the server channel does not exist.
     *
     * @return channel future
     * @see #bind()
     */
    public Optional<ChannelFuture> getChannelFuture() {
        return Optional.ofNullable(channelFuture);
    }

    /**
     * Initializer of incoming connections.
     */
    private final class Initializer extends ChannelInitializer<SocketChannel> {

        @Override
        public void initChannel(final @NotNull SocketChannel channel) {
            final ClientConnection connection = new ClientConnection(server, channel);
            channel.config().setKeepAlive(true);
            channel.pipeline()
                    .addLast(HandlerNames.LEGACY_PING_DECODER, new LegacyPingDecoder(server))
                    .addLast(HandlerNames.LENGTH_DECODER, new LengthDecoder())
                    .addLast(HandlerNames.PACKET_DECODER, new PacketDecoder(packetFactory, connection::getIncomingState))
                    .addLast(HandlerNames.PACKET_HANDLER, connection)

                    .addLast(HandlerNames.LEGACY_PING_ENCODER, new LegacyPingEncoder())
                    .addLast(HandlerNames.LENGTH_ENCODER, new LengthEncoder())
                    .addLast(HandlerNames.PACKET_ENCODER, new PacketEncoder(packetFactory, connection::getOutgoingState));

            connections.add(connection);
        }

    }

}
