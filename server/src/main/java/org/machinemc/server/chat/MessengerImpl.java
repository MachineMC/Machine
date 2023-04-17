package org.machinemc.server.chat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.machinemc.api.chat.MessageType;
import org.machinemc.nbt.NBTCompound;
import org.machinemc.scriptive.components.Component;
import org.machinemc.scriptive.components.TranslationComponent;
import org.machinemc.scriptive.style.ChatColor;
import org.machinemc.server.Machine;
import org.machinemc.api.chat.Messenger;
import org.machinemc.api.entities.Player;
import org.machinemc.server.network.packets.out.play.PacketPlayOutSystemChatMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Default implementation of server's messenger.
 */
@RequiredArgsConstructor
public class MessengerImpl implements Messenger {

    private static final String CODEC_TYPE = "minecraft:chat_type";

    @Getter
    private final Machine server;

    @Getter @Setter
    private TranslationComponent cannotSendMessage = TranslationComponent.of("chat.cannotSend").modify()
            .color(ChatColor.RED)
            .finish();

    // TODO Player Message impl once it's done
    @Override
    public boolean sendMessage(final Player player, final Component message, final MessageType messageType) {
        if (Messenger.accepts(player, messageType)) {
            player.sendPacket(new PacketPlayOutSystemChatMessage(message, false));
            return true;
        }
        return false;
    }

    @Override
    public void sendRejectionMessage(final Player player) {
        player.sendPacket(new PacketPlayOutSystemChatMessage(cannotSendMessage, false));
    }

    @Override
    public String getCodecType() {
        return CODEC_TYPE;
    }

    @Override
    public List<NBTCompound> getCodecElements() {
        return new ArrayList<>(Arrays.stream(ChatType.values())
                .map(ChatType::toNBT)
                .toList());
    }

}
