package org.machinemc.server.translation.translators.in;

import org.machinemc.api.auth.OnlineServer;
import org.machinemc.api.network.PlayerConnection;
import org.machinemc.server.entities.ServerPlayer;
import org.machinemc.api.entities.player.PlayerProfile;
import org.machinemc.server.entities.player.PlayerProfileImpl;
import org.machinemc.server.translation.PacketTranslator;
import org.machinemc.server.network.ClientConnection;
import org.machinemc.server.network.packets.in.login.PacketLoginInStart;
import org.machinemc.server.network.packets.out.login.PacketLoginOutEncryptionRequest;
import org.machinemc.server.network.packets.out.login.PacketLoginOutSuccess;

public class TranslatorLoginInStart extends PacketTranslator<PacketLoginInStart> {

    @Override
    public boolean translate(final ClientConnection connection, final PacketLoginInStart packet) {
        return true;
    }

    @Override
    public void translateAfter(final ClientConnection connection, final PacketLoginInStart packet) {
        connection.setLoginUsername(packet.getUsername());
        if (!connection.getServer().isOnline()) {
            final PlayerProfile profile = PlayerProfileImpl.offline(packet.getUsername());
            connection.send(new PacketLoginOutSuccess(profile.getUuid(), profile.getUsername(), profile.getTextures()));
            if (connection.getState() == PlayerConnection.ClientState.DISCONNECTED)
                return;
            connection.setState(PlayerConnection.ClientState.PLAY);
            ServerPlayer.spawn(connection.getServer(), profile, connection);
            return;
        }
        OnlineServer onlineServer = connection.getServer().getOnlineServer();
        if (onlineServer == null) {
            connection.disconnect();
            throw new IllegalStateException("Online server hasn't been initialized");
        }
        byte[] publicKey = onlineServer.getKey().getPublic().getEncoded();
        byte[] verifyToken = onlineServer.nextVerifyToken();

        connection.setPublicKeyData(packet.getPublicKeyData());
        connection.send(new PacketLoginOutEncryptionRequest(publicKey, verifyToken));
    }

    @Override
    public Class<PacketLoginInStart> packetClass() {
        return PacketLoginInStart.class;
    }

}
