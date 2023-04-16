package org.machinemc.server.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;
import org.machinemc.api.network.PlayerConnection;
import org.machinemc.api.network.packets.Packet;
import org.machinemc.server.translation.TranslatorDispatcher;

/**
 * Channel handler that encodes outgoing packets to bytes.
 */
@AllArgsConstructor
public class PacketEncoder extends MessageToByteEncoder<Packet> {

    private final ClientConnection connection;

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet msg, ByteBuf out) {
        assert connection.getState() != null && connection.getState() != PlayerConnection.ClientState.DISCONNECTED;
        final Packet.PacketState packetState = connection.getState().getOut();
        assert packetState != null;

        final TranslatorDispatcher dispatcher = connection.getServer().getTranslatorDispatcher();
        if(!dispatcher.playIn(connection, msg)) return;
        dispatcher.playInAfter(connection, msg);

        out.writeBytes(msg.rawSerialize());
    }

}
