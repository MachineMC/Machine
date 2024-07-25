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
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.RequiredArgsConstructor;
import org.machinemc.network.protocol.*;
import org.machinemc.paklet.PacketFactory;
import org.machinemc.paklet.netty.NettyDataVisitor;

/**
 * Encodes outgoing packets.
 */
@RequiredArgsConstructor
public class PacketEncoder extends MessageToByteEncoder<Packet<?>> {

    private static final PacketFlow FLOW = PacketFlow.CLIENTBOUND;

    private final PacketFactory packetFactory;
    private final Supplier<ConnectionState> state;

    @Override
    protected void encode(final ChannelHandlerContext ctx, final Packet<?> packet, final ByteBuf out) {
        Preconditions.checkState(packet.flow() == FLOW, "Decoded server-bound packet in client-bound context");
        packetFactory.write(packet, PacketGroups.getGroup(state.get(), FLOW), new NettyDataVisitor(out));
        System.out.println("OUTGOING: " + packet);
    }

}
