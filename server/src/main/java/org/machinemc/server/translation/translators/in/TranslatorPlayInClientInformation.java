package org.machinemc.server.translation.translators.in;

import org.machinemc.server.entities.ServerPlayer;
import org.machinemc.server.network.ClientConnection;
import org.machinemc.server.translation.PacketTranslator;
import org.machinemc.server.network.packets.in.play.PacketPlayInClientInformation;

public class TranslatorPlayInClientInformation extends PacketTranslator<PacketPlayInClientInformation> {

    @Override
    public boolean translate(ClientConnection connection, PacketPlayInClientInformation packet) {
        return true;
    }

    @Override
    public void translateAfter(ClientConnection connection, PacketPlayInClientInformation packet) {
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
    public Class<PacketPlayInClientInformation> packetClass() {
        return PacketPlayInClientInformation.class;
    }

}
