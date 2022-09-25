package me.pesekjak.machine.events.translations.translators.in;

import me.pesekjak.machine.events.translations.PacketTranslator;
import me.pesekjak.machine.network.ClientConnection;
import me.pesekjak.machine.network.packets.in.PacketPlayInKeepAlive;
import org.jetbrains.annotations.NotNull;

public class TranslatorPlayInKeepAlive extends PacketTranslator<PacketPlayInKeepAlive> {

    @Override
    public boolean translate(ClientConnection connection, PacketPlayInKeepAlive packet) {
        if(packet.getKeepAliveId() != connection.getKeepAliveKey()) return false;
        connection.keepAlive();
        return true;
    }

    @Override
    public void translateAfter(ClientConnection connection, PacketPlayInKeepAlive packet) {

    }

    @Override
    public @NotNull Class<PacketPlayInKeepAlive> packetClass() {
        return PacketPlayInKeepAlive.class;
    }

}
