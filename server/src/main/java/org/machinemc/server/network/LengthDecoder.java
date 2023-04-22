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
 * If not, see <https://www.gnu.org/licenses/>.
 */
package org.machinemc.server.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import static org.machinemc.server.utils.FriendlyByteBuf.*;

/**
 * Channel handler that decodes the length of incoming packets.
 */
public class LengthDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) {
        in.markReaderIndex();
        if (!in.isReadable()) return;

        int length = 0;
        int position = 0;
        byte currentByte;

        while (in.isReadable()) {
            currentByte = in.readByte();
            length |= (currentByte & SEGMENT_BITS) << position;
            if (!((currentByte & CONTINUE_BIT) == 0)) {
                position += 7;
                if (position >= 32) throw new RuntimeException("Length VarInt for packet is too big");
                continue;
            }
            if (!in.isReadable(length)) {
                in.resetReaderIndex();
                return;
            }
            out.add(in.readBytes(length));
            return;
        }
        in.resetReaderIndex();
    }

}
