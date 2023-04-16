package org.machinemc.server.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import static org.machinemc.server.utils.FriendlyByteBuf.*;

/**
 * Channel handler that decodes the length of incoming packets.
 */
public class LengthDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) {
        in.markReaderIndex();
        if (!in.isReadable()) return;

        int length = 0;
        int position = 0;
        byte currentByte;

        while(in.isReadable()) {
            currentByte = in.readByte();
            length |= (currentByte & SEGMENT_BITS) << position;
            if (!((currentByte & CONTINUE_BIT) == 0)) {
                position += 7;
                if (position >= 32) throw new RuntimeException("Length VarInt for packet is too big");
                continue;
            }
            if(!in.isReadable(length)) {
                in.resetReaderIndex();
                return;
            }
            out.add(in.readBytes(length));
            return;
        }
        in.resetReaderIndex();
    }

}
