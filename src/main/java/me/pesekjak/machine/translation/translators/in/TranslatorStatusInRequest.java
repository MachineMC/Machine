package me.pesekjak.machine.translation.translators.in;

import me.pesekjak.machine.translation.PacketTranslator;
import me.pesekjak.machine.network.ClientConnection;
import me.pesekjak.machine.network.packets.in.status.PacketStatusInRequest;
import me.pesekjak.machine.network.packets.out.status.PacketStatusOutResponse;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class TranslatorStatusInRequest extends PacketTranslator<PacketStatusInRequest> {

    @Override
    public boolean translate(@NotNull ClientConnection connection, @NotNull PacketStatusInRequest packet) {
        return true;
    }

    @Override
    public void translateAfter(@NotNull ClientConnection connection, @NotNull PacketStatusInRequest packet) {
        try {
            connection.sendPacket(new PacketStatusOutResponse(connection.getServer().statusJson()));
        } catch (IOException ignored) { }
    }

    @Override
    public @NotNull Class<PacketStatusInRequest> packetClass() {
        return PacketStatusInRequest.class;
    }

}
