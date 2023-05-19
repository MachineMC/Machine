/*
 * This file is part of Machine.
 *
 * Machine is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Machine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Machine.
 * If not, see https://www.gnu.org/licenses/.
 */
package org.machinemc.server.network.packets.in.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.machinemc.api.chat.ChatMode;
import org.machinemc.api.entities.player.Hand;
import org.machinemc.api.entities.player.SkinPart;
import org.machinemc.server.network.packets.PacketIn;
import org.machinemc.server.utils.FriendlyByteBuf;
import org.machinemc.api.utils.ServerBuffer;

import java.nio.charset.StandardCharsets;
import java.util.Set;

@ToString
@Getter @Setter
@AllArgsConstructor
public class PacketPlayInClientInformation extends PacketIn {

    private static final int ID = 0x08;

    private String locale;
    private byte viewDistance;
    private ChatMode chatMode;
    private boolean chatColor;
    private Set<SkinPart> displayedSkinParts;
    private Hand mainHand;
    private boolean enableTextFiltering;
    private boolean allowServerListings;

    static {
        register(PacketPlayInClientInformation.class, ID, PacketState.PLAY_IN,
                PacketPlayInClientInformation::new);
    }

    public PacketPlayInClientInformation(final ServerBuffer buf) {
        locale = buf.readString(StandardCharsets.UTF_8);
        viewDistance = buf.readByte();
        chatMode = ChatMode.fromID(buf.readVarInt());
        chatColor = buf.readBoolean();
        displayedSkinParts = SkinPart.fromMask(buf.readByte());
        mainHand = Hand.fromID(buf.readVarInt());
        enableTextFiltering = buf.readBoolean();
        allowServerListings = buf.readBoolean();
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public PacketState getPacketState() {
        return PacketState.PLAY_IN;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeString(locale, StandardCharsets.UTF_8)
                .writeByte(viewDistance)
                .writeVarInt(chatMode.getID())
                .writeBoolean(chatColor)
                .writeByte((byte) SkinPart.skinMask(displayedSkinParts.toArray(new SkinPart[0])))
                .writeVarInt(mainHand.getId())
                .writeBoolean(enableTextFiltering)
                .writeBoolean(allowServerListings)
                .bytes();
    }

    @Override
    public PacketIn clone() {
        return new PacketPlayInClientInformation(new FriendlyByteBuf(serialize()));
    }

}
