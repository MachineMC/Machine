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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import org.machinemc.network.protocol.ProtocolUtils;

import java.util.List;

/**
 * Channel handler that decodes the length of incoming packets, where all packets are
 * prefixed with a var-int.
 */
public class LengthDecoder extends ByteToMessageDecoder {

    private static final int MAX_LENGTH = 3;

    /**
     * Buffer that holds the length of the packet once decoded.
     */
    private final ByteBuf lengthHolder = Unpooled.directBuffer(MAX_LENGTH);

    /**
     * Tries to copy var-int from source buffer to targeted buffer.
     * <p>
     * Returns true if the whole var int was successfully read, else false.
     *
     * @param source source buffer
     * @param target target buffer
     * @return result
     */
    private static boolean copyLength(final ByteBuf source, final ByteBuf target) {
        for (int i = 0; i < MAX_LENGTH; i++) {
            if (!source.isReadable()) return false; // the var-int is not whole

            final byte next = source.readByte();
            target.writeByte(next);

            if (ProtocolUtils.hasContinuationBit(next)) // there is more
                continue;

            return true;
        }

        throw new CorruptedFrameException("Packet is too large");
    }

    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) {
        // if the channel is closed, skip the incoming data
        if (!ctx.channel().isActive()) {
            in.skipBytes(in.readableBytes());
            return;
        }

        in.markReaderIndex();
        lengthHolder.clear();

        if (!copyLength(in, lengthHolder)) {
            in.resetReaderIndex();
            return;
        }

        final int length = ProtocolUtils.readVarInt(lengthHolder);

        if (in.readableBytes() < length) { // missing bytes to finish rest of the packet
            in.resetReaderIndex();
            return;
        }

        out.add(in.readBytes(length));
    }

}
