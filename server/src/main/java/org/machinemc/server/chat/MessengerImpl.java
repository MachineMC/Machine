package org.machinemc.server.chat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.machinemc.nbt.NBTCompound;
import org.machinemc.server.Machine;
import org.machinemc.api.chat.Messenger;
import org.machinemc.api.entities.Player;
import org.machinemc.server.network.packets.out.play.PacketPlayOutSystemChatMessage;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

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
    private Component cannotSendMessage = Component.translatable("chat.cannotSend", NamedTextColor.RED);

    // TODO Player Message impl once it's done
    @Override
    public boolean sendMessage(Player player, Component message, MessageType messageType) {
        if(Messenger.accepts(player, messageType)) {
            player.sendPacket(new PacketPlayOutSystemChatMessage(message, false));
            return true;
        }
        return false;
    }

    @Override
    public void sendRejectionMessage(Player player) {
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