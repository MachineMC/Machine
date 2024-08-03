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
import lombok.RequiredArgsConstructor;

import javax.crypto.Cipher;

/**
 * Channel handler that encrypts the outgoing data.
 */
@RequiredArgsConstructor
public class EncryptionEncoder extends MessageToByteEncoder<ByteBuf> {

    private final Cipher cipher;

    @Override
    protected void encode(final ChannelHandlerContext ctx, final ByteBuf msg, final ByteBuf out) {
        final byte[] data = new byte[msg.readableBytes()];
        msg.readBytes(data);
        final byte[] encrypted = cipher.update(data);
        out.writeBytes(encrypted);
    }

}
