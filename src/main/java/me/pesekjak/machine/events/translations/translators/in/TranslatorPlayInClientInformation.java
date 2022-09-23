package me.pesekjak.machine.events.translations.translators.in;

import me.pesekjak.machine.entities.Player;
import me.pesekjak.machine.events.translations.PacketTranslator;
import me.pesekjak.machine.network.ClientConnection;
import me.pesekjak.machine.network.packets.in.PacketPlayInClientInformation;
import org.jetbrains.annotations.NotNull;

public class TranslatorPlayInClientInformation extends PacketTranslator<PacketPlayInClientInformation> {

    @Override
    public boolean translate(ClientConnection connection, PacketPlayInClientInformation packet) {
        return true;
    }

    @Override
    public void translateAfter(ClientConnection connection, PacketPlayInClientInformation packet) {
        Player player = connection.getOwner();
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
