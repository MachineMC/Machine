package org.machinemc.server.translation.translators.in;

import org.machinemc.api.chat.MessageType;
import org.machinemc.api.chat.Messenger;
import org.machinemc.api.entities.Player;
import org.machinemc.scriptive.components.TextComponent;
import org.machinemc.scriptive.util.ChatUtils;
import org.machinemc.server.entities.ServerPlayer;
import org.machinemc.server.network.ClientConnection;
import org.machinemc.server.network.packets.in.play.PacketPlayInChatMessage;
import org.machinemc.server.translation.PacketTranslator;

public class TranslatorPlayInChatMessage extends PacketTranslator<PacketPlayInChatMessage> {

    @Override
    public boolean translate(final ClientConnection connection, final PacketPlayInChatMessage packet) {
        ServerPlayer player = connection.getOwner();
        if (player == null)
            return false;
        if (!Messenger.canReceiveMessage(player)) {
            connection.getServer().getMessenger().sendRejectionMessage(player);
            return false;
        }
        return true;
    }

    @Override
    public void translateAfter(final ClientConnection connection, final PacketPlayInChatMessage packet) {
        ServerPlayer player = connection.getOwner();
        if (player == null)
            return;
        String message = ChatUtils.DEFAULT_CHAT_FORMAT
                .replace("%name%", player.getName())
                .replace("%message%", packet.getMessage());
        for (Player serverPlayer : connection.getServer().getPlayerManager().getPlayers())
            serverPlayer.sendMessage(player.getUuid(), TextComponent.of(message), MessageType.SYSTEM);
        connection.getServer().getConsole().info(message);
    }

    @Override
    public Class<PacketPlayInChatMessage> packetClass() {
        return PacketPlayInChatMessage.class;
    }

}
