package me.pesekjak.machine.events.translations;

import lombok.AllArgsConstructor;
import me.pesekjak.machine.network.Channel;
import me.pesekjak.machine.network.PacketHandler;
import me.pesekjak.machine.network.PacketReader;
import me.pesekjak.machine.network.PacketWriter;
import me.pesekjak.machine.network.packets.PacketIn;
import me.pesekjak.machine.network.packets.PacketOut;

@AllArgsConstructor
public class TranslatorHandler extends PacketHandler {

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
    public void afterRead(Channel channel, PacketIn packet) {
        dispatcher.playInAfter(channel.getConnection(), packet);
        super.afterRead(channel, packet);
    }

    @Override
    public void afterWrite(Channel channel, PacketOut packet) {
        dispatcher.playOutAfter(channel.getConnection(), packet);
        super.afterWrite(channel, packet);
    }

}
