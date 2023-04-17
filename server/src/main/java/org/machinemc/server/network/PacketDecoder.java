package org.machinemc.server.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.AllArgsConstructor;
import org.machinemc.api.network.PlayerConnection;
import org.machinemc.api.network.packets.Packet;
import org.machinemc.server.exception.ClientException;
import org.machinemc.server.network.packets.PacketFactory;
import org.machinemc.server.translation.TranslatorDispatcher;
import org.machinemc.server.utils.FriendlyByteBuf;

import java.util.List;

/**
 * Channel handler that decodes incoming bytes to packets.
 */
@AllArgsConstructor
public class PacketDecoder extends ByteToMessageDecoder {

    private final ClientConnection connection;

    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) {
        assert connection.getState() != null && connection.getState() != PlayerConnection.ClientState.DISCONNECTED;
        final Packet.PacketState packetState = connection.getState().getIn();
        assert packetState != null;

        final FriendlyByteBuf buf = new FriendlyByteBuf(in);
        final Packet packet;
        try {
            packet = PacketFactory.produce(PacketFactory.getPacketByRawId(buf.readVarInt(), packetState), buf);
            if (packet == null) return;
        } catch (Throwable throwable) {
            return;
        }

        final TranslatorDispatcher dispatcher = connection.getServer().getTranslatorDispatcher();

        if (!dispatcher.playIn(connection, packet)) return;
        dispatcher.playInAfter(connection, packet);
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        connection.getServer().getExceptionHandler().handle(new ClientException(connection, cause));
    }

}
