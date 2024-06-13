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
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.machinemc.network.protocol.FriendlyByteBuf;
import org.machinemc.network.protocol.ProtocolUtils;

/**
 * Channel handler that prefix the outgoing packets with their length, where all packets are
 * prefixed with a var-int.
 */
public class LengthEncoder extends MessageToByteEncoder<ByteBuf> {

    private static final int MAX_LENGTH = 3;

    @Override
    protected void encode(final ChannelHandlerContext channelHandlerContext, final ByteBuf msg, final ByteBuf out) {
        final int size = msg.readableBytes();

        final int varIntSize = ProtocolUtils.varIntLength(size);
        Preconditions.checkArgument(varIntSize <= MAX_LENGTH, "Packet is too large: " + size);

        final FriendlyByteBuf friendlyOut = new FriendlyByteBuf(out);
        friendlyOut.writeVarInt(size);
        friendlyOut.writeBytes(msg);
    }

}
