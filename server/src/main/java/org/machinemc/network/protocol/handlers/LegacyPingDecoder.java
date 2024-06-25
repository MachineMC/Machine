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
import lombok.RequiredArgsConstructor;
import org.machinemc.Server;
import org.machinemc.network.protocol.legacy.LegacyKick;
import org.machinemc.network.protocol.legacy.LegacyPingType;
import org.machinemc.scriptive.components.TextComponent;

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
@RequiredArgsConstructor
public class LegacyPingDecoder extends ByteToMessageDecoder {

    private final Server server;

    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) {
        if (!in.isReadable()) return;

        // if the channel is closed, skip the incoming data
        if (!ctx.channel().isActive()) {
            in.skipBytes(in.readableBytes());
            return;
        }

        in.markReaderIndex();

        final short first = in.readUnsignedByte();

        // Legacy handshake requesting server status
        if (first == 0xFE) {
            ctx.channel().writeAndFlush(LegacyKick.fromStatus(server.getServerStatus(), determinatePingType(in)));
            return;
        }

        // Legacy handshake initiating the server connection
        if (first == 0x02 && in.isReadable()) {
            in.skipBytes(in.readableBytes());
            // TODO event
            ctx.channel().writeAndFlush(LegacyKick.withReason(TextComponent.of("Outdated client")));
            return;
        }

        in.resetReaderIndex();

        // keeping the handlers is useless because
        // the connection can not be legacy at this point
        ctx.pipeline().remove(this);
        ctx.pipeline().remove(LegacyPingEncoder.class);
    }

    /**
     * Determinate legacy ping type from incoming data.
     *
     * @param in incoming data
     * @return legacy ping type
     */
    private LegacyPingType determinatePingType(final ByteBuf in) {
        if (!in.isReadable()) return LegacyPingType.V1_3;

        final short next = in.readUnsignedByte();
        if (next == 0x01 && !in.isReadable()) return LegacyPingType.V1_5;

        in.skipBytes(in.readableBytes());
        return LegacyPingType.V1_6;
    }

}
