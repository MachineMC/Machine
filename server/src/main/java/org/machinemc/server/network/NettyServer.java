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

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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

    @Getter
    private final String ip;
    @Getter
    private final int port;
    @Getter
    private boolean running = false;

    public NettyServer(final Machine server) {
        this.server = server;
        this.ip = server.getIp();
        this.port = server.getServerPort();
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
        bindFuture = bootstrap.bind(port).addListener(future -> {
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
        getClients().forEach(connection -> connection.send(packet));
    }

    @Override
    public ChannelFuture disconnect(final PlayerConnection connection) {
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

        }
    }

}
