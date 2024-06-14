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
import io.netty.handler.codec.MessageToByteEncoder;
import org.machinemc.network.protocol.legacy.LegacyKick;

import java.nio.charset.StandardCharsets;

/**
 * While not technically part of the current protocol,
 * legacy clients may send legacy handshake packet to initiate Server List Ping,
 * and modern servers should handle it correctly.
 * <p>
 * This handler serves as encoder of responses to such packets.
 *
 * @see LegacyKick
 */
public class LegacyPingEncoder extends MessageToByteEncoder<LegacyKick> {

    @Override
    protected void encode(final ChannelHandlerContext ctx, final LegacyKick msg, final ByteBuf out) {
        out.writeByte(0xff);
        out.writeShort(msg.data().length());
        out.writeCharSequence(msg.data(), StandardCharsets.UTF_16BE);
    }

}
