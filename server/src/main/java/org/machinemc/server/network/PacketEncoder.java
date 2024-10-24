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
    protected void encode(final ChannelHandlerContext ctx, final Packet msg, final ByteBuf out) throws Exception {
        assert connection.getState().map(state -> state != PlayerConnection.ClientState.DISCONNECTED).orElse(false);
        final Packet.PacketState packetState = connection.getState().get().getOut();
        assert packetState != null;

        final TranslatorDispatcher dispatcher = connection.getServer().getTranslatorDispatcher();
        if (!dispatcher.playOut(connection, msg)) return;
        dispatcher.playOutAfter(connection, msg);

        out.writeBytes(msg.rawSerialize());
    }

}
