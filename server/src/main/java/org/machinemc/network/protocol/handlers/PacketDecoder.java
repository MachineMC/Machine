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
package org.machinemc.network.protocol.handlers;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.RequiredArgsConstructor;
import org.machinemc.network.protocol.ConnectionState;
import org.machinemc.network.protocol.Packet;
import org.machinemc.network.protocol.PacketFlow;
import org.machinemc.network.protocol.PacketGroups;
import org.machinemc.paklet.PacketFactory;
import org.machinemc.paklet.netty.NettyDataVisitor;

import java.util.List;

/**
 * Decodes incoming packets.
 */
@RequiredArgsConstructor
public class PacketDecoder extends ByteToMessageDecoder {

    private static final PacketFlow FLOW = PacketFlow.SERVERBOUND;

    private final PacketFactory packetFactory;
    private final Supplier<ConnectionState> state;

    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) {
        if (!in.isReadable()) return;
        final Packet<?> packet = packetFactory.create(PacketGroups.getGroup(state.get(), FLOW), new NettyDataVisitor(in));
        Preconditions.checkState(packet.flow() == FLOW, "Decoded client-bound packet in server-bound context");
        System.out.println("INCOMING: " + packet);
        out.add(packet);
    }

}
