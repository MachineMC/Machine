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

/**
 * Channel handler that encrypts outgoing packets.
 */
@AllArgsConstructor
public class CipherEncoder extends MessageToByteEncoder<ByteBuf> {

    private final ClientConnection connection;

    @Override
    protected void encode(final ChannelHandlerContext ctx, final ByteBuf msg, final ByteBuf out) {
        if (connection.encryptionContext == null) {
            out.writeBytes(msg);
            return;
        }

        final byte[] data = new byte[msg.readableBytes()];
        msg.readBytes(data);
        final byte[] encrypted = connection.encryptionContext.encrypt().update(data);
        out.writeBytes(encrypted);
    }

}
