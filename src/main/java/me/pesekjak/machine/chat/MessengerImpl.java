package me.pesekjak.machine.chat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.entities.Player;
import me.pesekjak.machine.network.packets.out.play.PacketPlayOutSystemChatMessage;
import mx.kenzie.nbt.NBTCompound;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

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
    private final @NotNull Machine server;

    @Getter @Setter
    private @NotNull Component cannotSendMessage = Component.translatable("chat.cannotSend", NamedTextColor.RED);

    // TODO Player Message impl once it's done
    @Override
    public boolean sendMessage(@NotNull Player player, @NotNull Component message, @NotNull MessageType messageType) {
        if(Messenger.accepts(player, messageType)) {
            player.sendPacket(new PacketPlayOutSystemChatMessage(message, false));
            return true;
        }
        return false;
    }

    @Override
    public void sendRejectionMessage(@NotNull Player player) {
        player.sendPacket(new PacketPlayOutSystemChatMessage(cannotSendMessage, false));
    }

    @Override
    public @NotNull String getCodecType() {
        return CODEC_TYPE;
    }

    @Override
    public @NotNull List<NBTCompound> getCodecElements() {
        return new ArrayList<>(Arrays.stream(ChatType.values())
                .map(ChatType::toNBT)
                .toList());
    }

}
