package org.machinemc.server.translation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.machinemc.server.network.Channel;
import org.machinemc.server.network.PacketHandler;
import org.machinemc.server.network.PacketReader;
import org.machinemc.server.network.PacketWriter;
import org.machinemc.api.network.packets.Packet;

/**
 * PacketHandler for translators, bridge between translator dispatcher and channel.
 */
@AllArgsConstructor
public class TranslatorHandler extends PacketHandler {

    @Getter
    private final TranslatorDispatcher dispatcher;

    @Override
    public PacketReader read(Channel channel, PacketReader read) {
        if(read.getPacket() != null && !dispatcher.playIn(channel.getConnection(), read.getPacket()))
            read.setPacket(null);
        return super.read(channel, read);
    }

    @Override
    public PacketWriter write(Channel channel, PacketWriter write) {
        if(write.getPacket() != null && !dispatcher.playOut(channel.getConnection(), write.getPacket()))
            write.setPacket(null);
        return super.write(channel, write);
    }

    @Override
    public void afterRead(Channel channel, Packet packet) {
        dispatcher.playInAfter(channel.getConnection(), packet);
        super.afterRead(channel, packet);
    }

    @Override
    public void afterWrite(Channel channel, Packet packet) {
        dispatcher.playOutAfter(channel.getConnection(), packet);
        super.afterWrite(channel, packet);
    }

}
