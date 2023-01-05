package me.pesekjak.machine.translation.translators.in;

import me.pesekjak.machine.entities.ServerPlayer;
import me.pesekjak.machine.translation.PacketTranslator;
import me.pesekjak.machine.network.ClientConnection;
import me.pesekjak.machine.network.packets.in.play.PacketPlayInClientInformation;
import org.jetbrains.annotations.NotNull;

public class TranslatorPlayInClientInformation extends PacketTranslator<PacketPlayInClientInformation> {

    @Override
    public boolean translate(@NotNull ClientConnection connection, @NotNull PacketPlayInClientInformation packet) {
        return true;
    }

    @Override
    public void translateAfter(@NotNull ClientConnection connection, @NotNull PacketPlayInClientInformation packet) {
        ServerPlayer player = connection.getOwner();
        if(player == null)
            return;
        player.setLocale(packet.getLocale());
        player.setViewDistance(packet.getViewDistance());
        player.setChatMode(packet.getChatMode());
        player.setDisplayedSkinParts(packet.getDisplayedSkinParts());
        player.setMainHand(packet.getMainHand());
    }

    @Override
    public @NotNull Class<PacketPlayInClientInformation> packetClass() {
        return PacketPlayInClientInformation.class;
    }

}
