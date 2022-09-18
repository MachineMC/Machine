package me.pesekjak.machine.events.translations.translators.in;

import me.pesekjak.machine.auth.OnlineServer;
import me.pesekjak.machine.entities.Player;
import me.pesekjak.machine.entities.player.PlayerProfile;
import me.pesekjak.machine.events.translations.PacketTranslator;
import me.pesekjak.machine.network.ClientConnection;
import me.pesekjak.machine.network.packets.in.PacketLoginInStart;
import me.pesekjak.machine.network.packets.out.PacketLoginOutEncryptionRequest;
import me.pesekjak.machine.network.packets.out.PacketLoginOutSuccess;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class TranslatorLoginInStart extends PacketTranslator<PacketLoginInStart> {

    @Override
    public boolean translate(ClientConnection connection, PacketLoginInStart packet) {
        return true;
    }

    @Override
    public void translateAfter(ClientConnection connection, PacketLoginInStart packet) {
        connection.setLoginUsername(packet.getUsername());
        if(!connection.getServer().isOnline()) {
            final PlayerProfile profile = PlayerProfile.offline(packet.getUsername());
            try {
                connection.sendPacket(new PacketLoginOutSuccess(profile.getUuid(), profile.getUsername(), 0));
            } catch (IOException exception) {
                connection.disconnect();
                return;
            }
            connection.setClientState(ClientConnection.ClientState.PLAY);
            new Player(connection.getServer(), profile, connection);
            return;
        }
        OnlineServer onlineServer = connection.getServer().getOnlineServer();
        if(onlineServer == null) {
            connection.disconnect();
            throw new IllegalStateException("Online server hasn't been initialized");
        }
        byte[] publicKey = onlineServer.getKey().getPublic().getEncoded();
        byte[] verifyToken = onlineServer.nextVerifyToken();

        try {
            connection.sendPacket(new PacketLoginOutEncryptionRequest(publicKey, verifyToken));
        } catch (IOException exception) {
            connection.disconnect();
        }
    }

    @Override
    public @NotNull Class<PacketLoginInStart> packetClass() {
        return PacketLoginInStart.class;
    }

}
