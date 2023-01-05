package me.pesekjak.machine.translation.translators.in;

import me.pesekjak.machine.translation.PacketTranslator;
import me.pesekjak.machine.network.ClientConnection;
import me.pesekjak.machine.network.packets.in.play.PacketPlayInKeepAlive;
import org.jetbrains.annotations.NotNull;

public class TranslatorPlayInKeepAlive extends PacketTranslator<PacketPlayInKeepAlive> {

    @Override
    public boolean translate(@NotNull ClientConnection connection, @NotNull PacketPlayInKeepAlive packet) {
        if(packet.getKeepAliveId() != connection.getKeepAliveKey()) return false;
        connection.keepAlive();
        return true;
    }

    @Override
    public void translateAfter(@NotNull ClientConnection connection, @NotNull PacketPlayInKeepAlive packet) {

    }

    @Override
    public @NotNull Class<PacketPlayInKeepAlive> packetClass() {
        return PacketPlayInKeepAlive.class;
    }

}
