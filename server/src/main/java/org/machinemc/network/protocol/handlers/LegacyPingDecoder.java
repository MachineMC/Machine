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
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * While not technically part of the current protocol,
 * legacy clients may send this packet to initiate Server List Ping,
 * and modern servers should handle it correctly.
 * <p>
 * The format of this packet is a remnant of the pre-Netty age,
 * before the switch to Netty in 1.7 brought the standard format that is recognized now.
 * This packet merely exists to inform legacy clients that they can't join our modern server.
 */
public class LegacyPingDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) {
        // first handler of the netty server, if not readable return
        if (!in.isReadable()) return;

        // if the channel is closed, skip the incoming data
        if (!ctx.channel().isActive()) {
            in.skipBytes(in.readableBytes());
            return;
        }

        in.markReaderIndex();

        final short first = in.readUnsignedByte();

        // Legacy handshake
        if (first == 0xFE) {

            if (!in.isReadable()) {
                // beta 1.8 - 1.3 handshake
                return;
            }

            final short next = in.readUnsignedByte();
            if (next == 0x01 && !in.isReadable()) {
                // 1.4 - 1.5 handshake
                return;
            }

            // 1.6 handshake
            return;
        }

        in.resetReaderIndex();
        ctx.pipeline().remove(this);
    }

}
