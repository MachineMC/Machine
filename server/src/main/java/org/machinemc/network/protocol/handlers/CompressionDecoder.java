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
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.RequiredArgsConstructor;
import org.machinemc.network.protocol.FriendlyByteBuf;

import java.util.List;
import java.util.zip.InflaterInputStream;

/**
 * Channel handler that changes the packet format of ingoing packets
 * to the standard format and decompresses packet data if they
 * are compressed.
 */
@RequiredArgsConstructor
public class CompressionDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) throws Exception {
        final FriendlyByteBuf friendlyIn = new FriendlyByteBuf(in);

        final int size = friendlyIn.readVarInt();

        // packet is not compressed
        if (size == 0) {
            out.add(friendlyIn.readBytes(friendlyIn.readableBytes()));
            return;
        }

        final InflaterInputStream inputStream = new InflaterInputStream(new ByteBufInputStream(friendlyIn));
        final byte[] data = inputStream.readNBytes(friendlyIn.readableBytes());
        inputStream.close();

        Preconditions.checkState(
                data.length == size,
                "Expected decompressed data of length " + size + " but got " + data.length
        );

        final ByteBuf decompressed = Unpooled.buffer();
        decompressed.writeBytes(data);
        out.add(decompressed);
    }

}
