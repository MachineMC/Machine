package org.machinemc.network.protocol.listeners;

import com.google.common.base.Preconditions;
import org.machinemc.Machine;
import org.machinemc.barebones.profile.GameProfile;
import org.machinemc.network.ClientConnection;
import org.machinemc.network.protocol.login.LoginPacketListener;
import org.machinemc.network.protocol.login.clientbound.S2CLoginSuccessPacket;
import org.machinemc.network.protocol.login.serverbound.C2SHelloPacket;
import org.machinemc.network.protocol.login.serverbound.C2SLoginAcknowledgedPacket;

public class ServerLoginPacketListener implements LoginPacketListener {

    private final ClientConnection connection;

    public ServerLoginPacketListener(ClientConnection connection) {
        this.connection = Preconditions.checkNotNull(connection, "Client connection can not be null");
    }

    @Override
    public void onHello(C2SHelloPacket packet) {
        Machine server = connection.getServer();
        if (server.getServerProperties().doesAuthenticate()) {
            // TODO encryption
        }
        continueLogin(GameProfile.forOfflinePlayer(packet.getUsername()));
    }

    private void continueLogin(GameProfile profile) {
        // TODO set compression
        connection.sendPacket(new S2CLoginSuccessPacket(profile, true), true);
    }

    @Override
    public void onLoginAcknowledged(C2SLoginAcknowledgedPacket packet) {
        // TODO switch to configuration
        throw new UnsupportedOperationException();
    }

}
