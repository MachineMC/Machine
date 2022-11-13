package me.pesekjak.machine.events.translations.translators.in;

import me.pesekjak.machine.auth.MojangAuth;
import me.pesekjak.machine.auth.OnlineServer;
import me.pesekjak.machine.entities.Player;
import me.pesekjak.machine.entities.player.PlayerProfile;
import me.pesekjak.machine.entities.player.PlayerTextures;
import me.pesekjak.machine.events.translations.PacketTranslator;
import me.pesekjak.machine.exception.ClientException;
import me.pesekjak.machine.network.ClientConnection;
import me.pesekjak.machine.network.packets.in.login.PacketLoginInEncryptionResponse;
import me.pesekjak.machine.network.packets.out.login.PacketLoginOutSuccess;
import me.pesekjak.machine.utils.UUIDUtils;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static me.pesekjak.machine.network.packets.out.login.PacketLoginOutEncryptionRequest.SERVER_ID;

public class TranslatorLoginInEncryptionResponse extends PacketTranslator<PacketLoginInEncryptionResponse> {

    @Override
    public boolean translate(ClientConnection connection, PacketLoginInEncryptionResponse packet) {
        return true;
    }

    @Override
    public void translateAfter(ClientConnection connection, PacketLoginInEncryptionResponse packet) {
        OnlineServer onlineServer = connection.getServer().getOnlineServer();
        if(onlineServer == null) {
            connection.disconnect();
            return;
        }
        if(connection.getLoginUsername() == null) {
            connection.disconnect();
            return;
        }

        SecretKey secretkey = onlineServer.getSecretKey(onlineServer.getKey().getPrivate(), packet.getSecret());
        connection.setSecretKey(secretkey);

        final String serverId = (new BigInteger(onlineServer.digestData(SERVER_ID, onlineServer.getKey().getPublic(), secretkey))).toString(16);
        final String username = URLEncoder.encode(connection.getLoginUsername(), StandardCharsets.UTF_8);

        MojangAuth.getAuthData(serverId, username).thenAccept(json -> {
            if(json == null) {
                try {
                    connection.disconnect(Component.translatable("disconnect.loginFailedInfo.invalidSession"));
                } catch (Exception exception) {
                    throw new RuntimeException(exception);
                }
                return;
            }
            UUID authUUID = UUIDUtils.parseUUID(json.get("id").getAsString());
            if (authUUID == null) {
                connection.disconnect();
                return;
            }
            String authUsername = json.get("name").getAsString();
            PlayerTextures playerTextures = PlayerTextures.buildSkin(json.getAsJsonArray("properties").get(0));
            final PlayerProfile profile = PlayerProfile.online(authUsername, authUUID, playerTextures);
            try {
                connection.sendPacket(new PacketLoginOutSuccess(authUUID, authUsername, profile.getTextures()));
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
            if(connection.getClientState() == ClientConnection.ClientState.DISCONNECTED)
                return;
            connection.setClientState(ClientConnection.ClientState.PLAY);
            Player.spawn(connection.getServer(), profile, connection);
        }).exceptionally(exception -> {
            connection.getServer().getExceptionHandler().handle(new ClientException(connection, exception.getCause()));
            return null;
        });
    }

    @Override
    public @NotNull Class<PacketLoginInEncryptionResponse> packetClass() {
        return PacketLoginInEncryptionResponse.class;
    }

}
