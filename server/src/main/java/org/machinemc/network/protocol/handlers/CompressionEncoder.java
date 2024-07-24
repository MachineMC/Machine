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
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.RequiredArgsConstructor;
import org.machinemc.network.protocol.FriendlyByteBuf;

import java.util.zip.DeflaterOutputStream;

/**
 * Channel handler that changes the packet format of outgoing packets
 * to the compressed format and compresses packet data if the packet
 * size is equal of above the threshold.
 */
@RequiredArgsConstructor
public class CompressionEncoder extends MessageToByteEncoder<ByteBuf> {

    private final int threshold;

    @Override
    protected void encode(final ChannelHandlerContext ctx, final ByteBuf msg, final ByteBuf out) throws Exception {
        final int size = msg.readableBytes();

        final FriendlyByteBuf friendlyOut = new FriendlyByteBuf(out);

        if (size < threshold) {
            friendlyOut.writeVarInt(0); // for non-compressed packets
            friendlyOut.writeBytes(msg);
            return;
        }

        final byte[] packetData = new byte[size];
        msg.readBytes(packetData);
        final ByteBuf compressed = Unpooled.buffer();
        final DeflaterOutputStream outputStream = new DeflaterOutputStream(new ByteBufOutputStream(compressed));
        outputStream.write(packetData);
        outputStream.finish();
        outputStream.close();
        friendlyOut.writeVarInt(size);
        friendlyOut.writeBytes(compressed);
    }

}
