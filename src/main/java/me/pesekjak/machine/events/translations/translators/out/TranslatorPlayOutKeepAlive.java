package me.pesekjak.machine.events.translations.translators.out;

import me.pesekjak.machine.events.translations.PacketTranslator;
import me.pesekjak.machine.network.ClientConnection;
import me.pesekjak.machine.network.packets.out.PacketPlayOutKeepAlive;
import org.jetbrains.annotations.NotNull;

public class TranslatorPlayOutKeepAlive extends PacketTranslator<PacketPlayOutKeepAlive> {

    @Override
    public boolean translate(ClientConnection connection, PacketPlayOutKeepAlive packet) {
        if(packet.getKeepAliveId() != connection.getKeepAliveKey()) return false;
        connection.sendKeepAlive();
        return true;
    }

    @Override
    public void translateAfter(ClientConnection connection, PacketPlayOutKeepAlive packet) {

    }

    @Override
    public @NotNull Class<PacketPlayOutKeepAlive> packetClass() {
        return PacketPlayOutKeepAlive.class;
    }

}
