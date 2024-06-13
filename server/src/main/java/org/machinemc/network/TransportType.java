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

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.*;
import io.netty.channel.kqueue.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.function.Supplier;

/**
 * Represents a transport type used by {@link NettyServer}.
 */
public interface TransportType {

    /**
     * Standard asynchronous non-blocking transport.
     */
    TransportType NIO = new TransportTypeImpl(
            NioServerSocketChannel::new, NioSocketChannel::new, NioEventLoopGroup::new, NioDatagramChannel::new
    );

    /**
     * Linux native transport.
     */
    TransportType EPOLL = new TransportTypeImpl(
            EpollServerSocketChannel::new, EpollSocketChannel::new, EpollEventLoopGroup::new, EpollDatagramChannel::new
    );

    /**
     * MacOS/BSD native transport.
     */
    TransportType KQUEUE = new TransportTypeImpl(
            KQueueServerSocketChannel::new, KQueueSocketChannel::new, KQueueEventLoopGroup::new, KQueueDatagramChannel::new
    );

    /**
     * Returns the best available transport.
     *
     * @return transport type for current system
     */
    static TransportType getInstance() {
        if (Epoll.isAvailable()) return EPOLL;
        if (KQueue.isAvailable()) return KQUEUE;
        return NIO;
    }

    /**
     * Creates new server socket channel using this transport.
     *
     * @return new server socket channel
     */
    ServerSocketChannel createServerSocketChannel();

    /**
     * Creates new socket channel using this transport.
     *
     * @return new socket channel
     */
    SocketChannel createSocketChannel();

    /**
     * Creates new event loop group using this transport.
     *
     * @return new event loop group
     */
    EventLoopGroup createEventLoopGroup();

    /**
     * Creates new datagram socket channel using this transport.
     *
     * @return new server datagram channel
     */
    DatagramChannel createDatagramChannel();

}


record TransportTypeImpl(Supplier<ServerSocketChannel> serverSocketChannel,
                         Supplier<SocketChannel> socketChannel,
                         Supplier<EventLoopGroup> eventLoopGroup,
                         Supplier<DatagramChannel> datagramChannel) implements TransportType {

    @Override
    public ServerSocketChannel createServerSocketChannel() {
        return serverSocketChannel.get();
    }

    @Override
    public SocketChannel createSocketChannel() {
        return socketChannel.get();
    }

    @Override
    public EventLoopGroup createEventLoopGroup() {
        return eventLoopGroup.get();
    }

    @Override
    public DatagramChannel createDatagramChannel() {
        return datagramChannel.get();
    }

}
