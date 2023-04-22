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
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.AllArgsConstructor;
import org.machinemc.server.exception.ClientException;

import javax.crypto.Cipher;
import java.util.List;

/**
 * Channel handler that decrypts incoming packets.
 */
@AllArgsConstructor
public class CipherDecoder extends ByteToMessageDecoder {

    private final ClientConnection connection;

    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf msg, final List<Object> out) {
        if (connection.encryptionContext == null) {
            out.add(msg.readBytes(msg.readableBytes()));
            return;
        }

        final Cipher cipher = connection.encryptionContext.decrypt();
        final byte[] data = new byte[msg.readableBytes()];
        msg.readBytes(data);
        final ByteBuf output = ctx.alloc().buffer(cipher.getOutputSize(msg.readableBytes()));
        output.writeBytes(cipher.update(data));
        out.add(output);
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        connection.getServer().getExceptionHandler().handle(new ClientException(connection, cause));
    }

}
