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
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) {
        if(connection.encryptionContext == null) {
            out.writeBytes(msg);
            return;
        }

        final byte[] data = new byte[msg.readableBytes()];
        msg.readBytes(data);
        final byte[] encrypted = connection.encryptionContext.encrypt().update(data);
        out.writeBytes(encrypted);
    }

}
