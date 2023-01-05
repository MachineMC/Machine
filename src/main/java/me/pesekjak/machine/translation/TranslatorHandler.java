package me.pesekjak.machine.translation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.pesekjak.machine.network.Channel;
import me.pesekjak.machine.network.PacketHandler;
import me.pesekjak.machine.network.PacketReader;
import me.pesekjak.machine.network.PacketWriter;
import me.pesekjak.machine.network.packets.Packet;
import org.jetbrains.annotations.NotNull;

/**
 * PacketHandler for translators, bridge between translator dispatcher and channel.
 */
@AllArgsConstructor
public class TranslatorHandler extends PacketHandler {

    @Getter
    private final @NotNull TranslatorDispatcher dispatcher;

    @Override
    public @NotNull PacketReader read(@NotNull Channel channel, @NotNull PacketReader read) {
        if(read.getPacket() != null && !dispatcher.playIn(channel.getConnection(), read.getPacket()))
            read.setPacket(null);
        return super.read(channel, read);
    }

    @Override
    public @NotNull PacketWriter write(@NotNull Channel channel, @NotNull PacketWriter write) {
        if(write.getPacket() != null && !dispatcher.playOut(channel.getConnection(), write.getPacket()))
            write.setPacket(null);
        return super.write(channel, write);
    }

    @Override
    public void afterRead(@NotNull Channel channel, @NotNull Packet packet) {
        dispatcher.playInAfter(channel.getConnection(), packet);
        super.afterRead(channel, packet);
    }

    @Override
    public void afterWrite(@NotNull Channel channel, @NotNull Packet packet) {
        dispatcher.playOutAfter(channel.getConnection(), packet);
        super.afterWrite(channel, packet);
    }

}
