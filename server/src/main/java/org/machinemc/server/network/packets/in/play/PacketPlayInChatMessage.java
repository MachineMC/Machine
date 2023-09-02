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
import org.jetbrains.annotations.Nullable;
import org.machinemc.server.chat.MessageSignature;
import org.machinemc.server.network.packets.PacketIn;
import org.machinemc.api.utils.FriendlyByteBuf;
import org.machinemc.api.utils.ServerBuffer;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.BitSet;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class PacketPlayInChatMessage extends PacketIn {

    private static final int ID = 0x05;

    private static final int BITSET_SIZE = 20;

    private String message;
    private Instant timestamp;
    private long salt;
    private @Nullable MessageSignature messageSignature;
    private int messageCount;
    private BitSet acknowledged;

    static {
        register(PacketPlayInChatMessage.class, ID, PacketState.PLAY_IN,
                PacketPlayInChatMessage::new);
    }

    public PacketPlayInChatMessage(final ServerBuffer buf) {
        message = buf.readString(StandardCharsets.UTF_8);
        timestamp = buf.readInstant();
        salt = buf.readLong();
        messageSignature = buf.readOptional(MessageSignature::new).orElse(null);
        messageCount = buf.readVarInt();
        acknowledged = buf.readBitSet(20);
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public PacketState getPacketState() {
        return PacketState.PLAY_IN;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeString(message, StandardCharsets.UTF_8)
                .writeInstant(timestamp)
                .writeLong(salt)
                .writeOptional(messageSignature, ServerBuffer::write)
                .writeVarInt(messageCount)
                .writeBitSet(acknowledged, 20)
                .bytes();
    }

    @Override
    public PacketIn clone() {
        return new PacketPlayInChatMessage(new FriendlyByteBuf(serialize()));
    }

}
