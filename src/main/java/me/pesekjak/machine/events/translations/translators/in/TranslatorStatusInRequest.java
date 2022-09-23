package me.pesekjak.machine.events.translations.translators.in;

import me.pesekjak.machine.events.translations.PacketTranslator;
import me.pesekjak.machine.network.ClientConnection;
import me.pesekjak.machine.network.packets.in.PacketStatusInRequest;
import me.pesekjak.machine.network.packets.out.PacketStatusOutResponse;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class TranslatorStatusInRequest extends PacketTranslator<PacketStatusInRequest> {

    @Override
    public boolean translate(ClientConnection connection, PacketStatusInRequest packet) {
        return true;
    }

    @Override
    public void translateAfter(ClientConnection connection, PacketStatusInRequest packet) {
        try {
            connection.sendPacket(new PacketStatusOutResponse(connection.getServer().statusJson()));
        } catch (IOException ignored) { }
    }

    @Override
    public @NotNull Class<PacketStatusInRequest> packetClass() {
        return PacketStatusInRequest.class;
    }

}
