package me.pesekjak.machine.events;

import lombok.AllArgsConstructor;
import me.pesekjak.machine.network.Channel;
import me.pesekjak.machine.network.PacketHandler;
import me.pesekjak.machine.network.PacketReader;
import me.pesekjak.machine.network.PacketWriter;

@AllArgsConstructor
public class TranslatorHandler extends PacketHandler {

    private final TranslatorDispatcher dispatcher;

    public PacketReader read(Channel channel, PacketReader read) {
        if(read.getPacket() != null)
            dispatcher.playIn(channel.getConnection(), read.getPacket());
        return super.read(channel, read);
    }

    public PacketWriter write(Channel channel, PacketWriter write) {
        if(write.getPacket() != null)
            dispatcher.playOut(channel.getConnection(), write.getPacket());
        return super.write(channel, write);
    }

}
