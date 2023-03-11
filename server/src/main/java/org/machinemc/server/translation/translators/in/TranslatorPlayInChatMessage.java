package org.machinemc.server.translation.translators.in;

import org.machinemc.api.chat.ChatUtils;
import org.machinemc.api.chat.Messenger;
import org.machinemc.api.chunk.Section;
import org.machinemc.api.entities.Player;
import org.machinemc.nbt.NBTCompound;
import org.machinemc.nbt.NBTLongArray;
import org.machinemc.server.chunk.ChunkUtils;
import org.machinemc.server.chunk.data.ChunkData;
import org.machinemc.server.entities.ServerPlayer;
import org.machinemc.server.network.packets.out.play.PacketPlayOutChunkData;
import org.machinemc.server.translation.PacketTranslator;
import org.machinemc.server.network.ClientConnection;
import org.machinemc.server.network.packets.in.play.PacketPlayInChatMessage;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.text.Component;
import org.machinemc.server.utils.FriendlyByteBuf;
import org.machinemc.server.utils.math.MathUtils;

import java.util.Map;

public class TranslatorPlayInChatMessage extends PacketTranslator<PacketPlayInChatMessage> {

    @Override
    public boolean translate(ClientConnection connection, PacketPlayInChatMessage packet) {
        ServerPlayer player = connection.getOwner();
        if (player == null)
            return false;
        if(!Messenger.canReceiveMessage(player)) {
            connection.getServer().getMessenger().sendRejectionMessage(player);
            return false;
        }
        return true;
    }

    @Override
    public void translateAfter(ClientConnection connection, PacketPlayInChatMessage packet) {
        ServerPlayer player = connection.getOwner();
        if (player == null)
            return;
        String message = ChatUtils.DEFAULT_CHAT_FORMAT
                .replace("%name%", player.getName())
                .replace("%message%", packet.getMessage());
        for(Player serverPlayer : connection.getServer().getPlayerManager().getPlayers())
            serverPlayer.sendMessage(player, Component.text(message), MessageType.SYSTEM);
        connection.getServer().getConsole().info(message);

        if(!packet.getMessage().contains("chunk"))
            return;

        player.getWorld().getChunk(player.getLocation()).sendChunk(player);
    }

    @Override
    public Class<PacketPlayInChatMessage> packetClass() {
        return PacketPlayInChatMessage.class;
    }

}
