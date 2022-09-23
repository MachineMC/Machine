package me.pesekjak.machine.events.translations.translators.in;

import me.pesekjak.machine.chat.ChatUtils;
import me.pesekjak.machine.chat.Messenger;
import me.pesekjak.machine.entities.Player;
import me.pesekjak.machine.events.translations.PacketTranslator;
import me.pesekjak.machine.network.ClientConnection;
import me.pesekjak.machine.network.packets.in.PacketPlayInChatMessage;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class TranslatorPlayInChatMessage extends PacketTranslator<PacketPlayInChatMessage> {

    @Override
    public boolean translate(ClientConnection connection, PacketPlayInChatMessage packet) {
        return true;
    }

    @Override
    public void translateAfter(ClientConnection connection, PacketPlayInChatMessage packet) {
        Player player = connection.getOwner();
        if (player == null)
            return;
        String message = ChatUtils.DEFAULT_CHAT_FORMAT
                .replace("%name%", player.getName())
                .replace("%message%", packet.getMessage());
        Messenger.sendChatMessage(player, Component.text(message));
    }

    @Override
    public @NotNull Class<PacketPlayInChatMessage> packetClass() {
        return PacketPlayInChatMessage.class;
    }

}
