package me.pesekjak.machine.translation.translators.in;

import me.pesekjak.machine.translation.PacketTranslator;
import me.pesekjak.machine.network.ClientConnection;
import me.pesekjak.machine.network.packets.in.handshaking.PacketHandshakingInHandshake;
import org.jetbrains.annotations.NotNull;

public class TranslatorHandshakingInHandshake extends PacketTranslator<PacketHandshakingInHandshake> {

    @Override
    public boolean translate(@NotNull ClientConnection connection, @NotNull PacketHandshakingInHandshake packet) {
        return true;
    }

    @Override
    public void translateAfter(@NotNull ClientConnection connection, @NotNull PacketHandshakingInHandshake packet) {
        if(packet.getHandshakeType() == PacketHandshakingInHandshake.HandshakeType.STATUS) {
            connection.setClientState(ClientConnection.ClientState.STATUS);
        } else if(packet.getHandshakeType() == PacketHandshakingInHandshake.HandshakeType.LOGIN) {
            connection.setClientState(ClientConnection.ClientState.LOGIN);
        }
    }

    @Override
    public @NotNull Class<PacketHandshakingInHandshake> packetClass() {
        return PacketHandshakingInHandshake.class;
    }

}
