package me.pesekjak.machine.chat;

import me.pesekjak.machine.entities.Player;
import me.pesekjak.machine.entities.player.ChatMode;
import me.pesekjak.machine.network.packets.out.PacketPlayOutSystemChatMessage;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.utils.NamespacedKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;
import org.jglrxavpok.hephaistos.nbt.NBTType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Messenger {

    public static final Component CANNOT_SEND_MESSAGE = Component.translatable("chat.cannotSend", NamedTextColor.RED);

    private static final UUID NO_SENDER = new UUID(0, 0);
    private static final PacketPlayOutSystemChatMessage CANNOT_SEND_PACKET = new PacketPlayOutSystemChatMessage(
            new FriendlyByteBuf()
            .writeComponent(CANNOT_SEND_MESSAGE)
            .writeBoolean(false)
    );

    public static final NBTCompound CHAT_REGISTRY;

    static {
        final List<NBT> chatTypes = new ArrayList<>();
        for(ChatType chatType : ChatType.values())
            chatTypes.add(chatType.toNBT());
        CHAT_REGISTRY = NBT.Compound((chatType) -> {
            chatType.setString("type", NamespacedKey.minecraft("chat_type").toString());
            chatType.set("value", NBT.List(
                    NBTType.TAG_Compound,
                    chatTypes
            ));
        });
    }

    public static boolean canReceiveMessage(@NotNull Player player) {
        return player.getChatMode() == ChatMode.ENABLED;
    }

    public static boolean canReceiveCommand(@NotNull Player player) {
        return player.getChatMode() != ChatMode.HIDDEN;
    }

    // TODO Replace with actual chat message once we get the Player Chat working
    public static boolean sendChatMessage(@NotNull Player player, @NotNull Component message) {
        if(!canReceiveMessage(player)) {
            sendRejectionMessage(player);
            return false;
        }
        return sendSystemMessage(player, message);
    }

    public static boolean sendSystemMessage(@NotNull Player player, @NotNull Component message) {
        if(!canReceiveCommand(player)) {
            sendRejectionMessage(player);
            return false;
        }
        player.sendMessage(message);
        return true;
    }

    public static void sendRejectionMessage(@NotNull Player player) {
        try {
            player.getConnection().sendPacket(CANNOT_SEND_PACKET);
        } catch (IOException ignored) { }
    }

}
