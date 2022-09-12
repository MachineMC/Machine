package me.pesekjak.machine.chat;

import me.pesekjak.machine.network.packets.out.PacketPlayOutSystemChatMessage;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.utils.NamespacedKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;
import org.jglrxavpok.hephaistos.nbt.NBTType;

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

}
