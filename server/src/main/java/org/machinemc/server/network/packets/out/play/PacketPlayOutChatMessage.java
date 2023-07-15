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
package org.machinemc.server.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;
import org.machinemc.api.auth.MessageSignature;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.scriptive.components.Component;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.utils.FriendlyByteBuf;

import java.util.UUID;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayOutChatMessage extends PacketOut {

    private static final int ID = 0x35;

    private Component signedMessage;
    private @Nullable Component unsignedMessage;
    private int chatType;
    private UUID uuid;
    private Component displayName;
    private @Nullable Component teamName;
    private MessageSignature messageSignature;

    static {
        register(PacketPlayOutChatMessage.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutChatMessage::new);
    }

    public PacketPlayOutChatMessage(final ServerBuffer buf) {
        signedMessage = buf.readComponent();
        if (buf.readBoolean()) // has unsigned content
            unsignedMessage = buf.readComponent();
        chatType = buf.readVarInt();
        uuid = buf.readUUID();
        displayName = buf.readComponent();
        if (buf.readBoolean()) // has team
            teamName = buf.readComponent();
        messageSignature = buf.readSignature();
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public PacketState getPacketState() {
        return PacketState.PLAY_OUT;
    }

    @Override
    public byte[] serialize() {
        final FriendlyByteBuf buf = new FriendlyByteBuf()
                .writeComponent(signedMessage)
                .writeBoolean(unsignedMessage != null);
        if (unsignedMessage != null)
            buf.writeComponent(unsignedMessage);
        buf.writeVarInt(chatType)
                .writeUUID(uuid)
                .writeComponent(displayName)
                .writeBoolean(teamName != null);
        if (teamName != null)
            buf.writeComponent(teamName);
        return buf.writeSignature(messageSignature)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutChatMessage(new FriendlyByteBuf(serialize()));
    }

}
