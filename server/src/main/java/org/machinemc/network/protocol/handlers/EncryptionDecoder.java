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

import javax.crypto.Cipher;
import java.util.List;

/**
 * Channel handler that decrypts the incoming data.
 */
@RequiredArgsConstructor
public class EncryptionDecoder extends ByteToMessageDecoder {

    private final Cipher cipher;

    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) {
        final byte[] data = new byte[in.readableBytes()];
        in.readBytes(data);
        final ByteBuf output = ctx.alloc().buffer(cipher.getOutputSize(in.readableBytes()));
        output.writeBytes(cipher.update(data));
        out.add(output);
    }

}
