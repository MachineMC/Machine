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

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.AllArgsConstructor;
import org.machinemc.server.utils.FriendlyByteBuf;
import org.machinemc.server.utils.ZLib;

import java.io.IOException;
import java.util.List;

/**
 * Channel handler that decompresses incoming packets.
 */
@AllArgsConstructor
public class CompressionDecoder extends ByteToMessageDecoder {

    private final ClientConnection connection;

    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) {
        if (!connection.isCompressed()) {
            out.add(in.readBytes(in.readableBytes()));
            return;
        }

        final FriendlyByteBuf buf = new FriendlyByteBuf(in);
        final int length = buf.readVarInt();

        // Is not compressed
        if (length == 0) {
            out.add(in.readBytes(in.readableBytes()));
            return;
        }

        try {
            final byte[] uncompressed = ZLib.decompress(buf.readBytes(buf.readableBytes()));
            final ByteBuf output = ctx.alloc().buffer(length);
            output.writeBytes(uncompressed);
            out.add(output);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

}
