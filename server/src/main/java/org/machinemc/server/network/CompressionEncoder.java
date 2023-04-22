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
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;
import org.machinemc.server.utils.FriendlyByteBuf;
import org.machinemc.server.utils.ZLib;

import java.io.IOException;

/**
 * Channel handler that compresses outgoing packets.
 */
@AllArgsConstructor
public class CompressionEncoder extends MessageToByteEncoder<ByteBuf> {

    private final ClientConnection connection;

    @Override
    protected void encode(final ChannelHandlerContext ctx, final ByteBuf msg, final ByteBuf out) {
        if (!connection.isCompressed()) {
            out.writeBytes(msg);
            return;
        }

        final FriendlyByteBuf buf = new FriendlyByteBuf(msg);
        final int length = buf.readVarInt();

        final FriendlyByteBuf data = new FriendlyByteBuf();
        final FriendlyByteBuf output = new FriendlyByteBuf(out);

        if (length < connection.getCompressionThreshold()) {
            data.writeVarInt(0).writeBytes(buf.readBytes(buf.readableBytes()));
            output.writeVarInt(data.readableBytes()).writeBytes(data.readBytes(data.readableBytes()));
            return;
        }

        try {
            final byte[] compressed = ZLib.compress(buf.readBytes(buf.readableBytes()));
            data.writeVarInt(length).writeBytes(compressed);
            output.writeVarInt(data.writerIndex()).writeBytes(data.readBytes(data.readableBytes()));
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

    }

}
