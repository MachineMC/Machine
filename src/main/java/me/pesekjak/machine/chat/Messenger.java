package me.pesekjak.machine.chat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.entities.Player;
import me.pesekjak.machine.network.packets.out.PacketPlayOutSystemChatMessage;
import me.pesekjak.machine.server.ServerProperty;
import me.pesekjak.machine.server.codec.CodecPart;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Sends a messages to a players, respecting their chat settings, handles
 * 'cannot send' messages and chat codec.
 */
@RequiredArgsConstructor
public class Messenger implements CodecPart, ServerProperty {

    private static final String CODEC_TYPE = "minecraft:worldgen/biome";

    @Getter
    private final Machine server;

    @Getter @Setter
    private Component cannotSendMessage = Component.translatable("chat.cannotSend", NamedTextColor.RED);

    public static boolean canReceiveMessage(@NotNull Player player) {
        return player.getChatMode() == ChatMode.ENABLED;
    }

    public static boolean canReceiveCommand(@NotNull Player player) {
        return player.getChatMode() != ChatMode.HIDDEN;
    }

    private static boolean accepts(@NotNull Player player, MessageType messageType) {
        if(messageType == MessageType.CHAT && canReceiveMessage(player))
            return true;
        return messageType == MessageType.SYSTEM && canReceiveCommand(player);
    }

    /**
     * Sends a message to a player, respecting their chat settings.
     * @param player the player
     * @param message message to receive
     * @param messageType type of the message
     * @return true if the message has been successfully received
     */
    // TODO Player Message impl once it's done
    public boolean sendMessage(Player player, Component message, MessageType messageType) {
        if(accepts(player, messageType)) {
            player.sendPacket(new PacketPlayOutSystemChatMessage(message, false));
            return true;
        }
        return false;
    }

    /**
     * Sends default message send rejection message to player.
     * @param player the player
     */
    public void sendRejectionMessage(@NotNull Player player) {
        player.sendPacket(new PacketPlayOutSystemChatMessage(cannotSendMessage, false));
    }

    @Override
    public String getCodecType() {
        return "minecraft:chat_type";
    }

    @Override
    public List<NBT> getCodecElements() {
        return new ArrayList<>(Arrays.stream(ChatType.values())
                .map(ChatType::toNBT)
                .toList());
    }

}
