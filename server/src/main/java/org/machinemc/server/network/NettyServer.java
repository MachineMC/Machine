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
package org.machinemc.server.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.machinemc.api.network.PlayerConnection;
import org.machinemc.api.network.ServerConnection;
import org.machinemc.api.network.packets.Packet;
import org.machinemc.scriptive.components.TranslationComponent;
import org.machinemc.server.Machine;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/**
 * Server connection implementation using netty.
 */
public class NettyServer implements ServerConnection {

    public static final int READ_IDLE_TIMEOUT = 30000;
    public static final int KEEP_ALIVE_FREQ = 20000;

    private final EventLoopGroup bossGroup = new NioEventLoopGroup();
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private @Nullable ChannelFuture bindFuture;

    @Getter
    private final Machine server;

    protected final Set<ClientConnection> connections = ConcurrentHashMap.newKeySet();

    private final String ip;
    @Getter
    private final int port;
    @Getter
    private boolean running = false;

    public NettyServer(final Machine server) {
        this.server = Objects.requireNonNull(server, "Server can not be null");
        this.ip = server.getIP();
        this.port = server.getServerPort();
    }

    @Override
    public String getIP() {
        return ip;
    }

    @Override
    public Optional<InetSocketAddress> getAddress() {
        if (bindFuture == null) return Optional.empty();
        return Optional.of((InetSocketAddress) bindFuture.channel().localAddress());
    }

    @Override
    public @Unmodifiable Set<PlayerConnection> getClients() {
        return Collections.unmodifiableSet(connections);
    }

    @Override
    public ChannelFuture start() {
        final ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new Initializer())
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        bindFuture = bootstrap.bind(getIP(), getPort()).addListener(future -> {
            if (future.isSuccess()) running = true;
        });
        return bindFuture;
    }

    @Override
    public ChannelFuture close() {
        if (bindFuture == null) throw new UnsupportedOperationException("Server hasn't been started yet");
        final ChannelFuture future = bindFuture.channel().close();
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
        return future;
    }

    @Override
    public void broadcastPacket(final Packet packet) {
        Objects.requireNonNull(packet, "Packet can not be null");
        getClients().forEach(connection -> connection.send(packet));
    }

    @Override
    public void broadcastPacket(final Packet packet, final Predicate<PlayerConnection> predicate) {
        Objects.requireNonNull(packet, "Packet can not be null");
        Objects.requireNonNull(predicate);
        getClients().forEach(connection -> {
            if (predicate.test(connection))
                connection.send(packet);
        });
    }

    @Override
    public ChannelFuture disconnect(final PlayerConnection connection) {
        Objects.requireNonNull(connection, "Connection can not be null");
        if (connection.getServerConnection() != this)
            throw new IllegalArgumentException("Provided connection is not connected to this server");
        return connection.disconnect(TranslationComponent.of("disconnect.disconnected"));
    }

    /**
     * Initializes incoming client connections.
     */
    class Initializer extends ChannelInitializer<SocketChannel> {
        @Override
        public void initChannel(final @NotNull SocketChannel ch) {
            final ClientConnection connection = new ClientConnection(NettyServer.this, ch);
            ch.config().setKeepAlive(true);
            ch.pipeline().addLast(
                    // Decoding
                    // Cipher -> Length -> Compression -> Packet -> SERVER
                    new CipherDecoder(connection),
                    new LengthDecoder(),
                    new CompressionDecoder(connection),
                    new PacketDecoder(connection), // is last, has to handle exceptions for decoding
                    // Encoding
                    // CLIENT <- Cipher <- Compression <- Packet
                    new CipherEncoder(connection),
                    new CompressionEncoder(connection),
                    new PacketEncoder(connection)
            );
            connections.add(connection);
        }
    }

    @Override
    public String toString() {
        return "ServerConnection("
                + "server=" + server
                + ')';
    }

}
