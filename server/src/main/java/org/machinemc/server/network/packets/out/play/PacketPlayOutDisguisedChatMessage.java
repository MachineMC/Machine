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
import org.machinemc.api.chat.MessageType;
import org.machinemc.api.utils.FriendlyByteBuf;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.scriptive.components.Component;
import org.machinemc.server.network.packets.PacketOut;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class PacketPlayOutDisguisedChatMessage extends PacketOut {

    private static final int ID = 0x1B;

    private Component message;
    private MessageType messageType;
    private Component source;
    private @Nullable Component target;

    static {
        register(PacketPlayOutDisguisedChatMessage.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutDisguisedChatMessage::new);
    }

    public PacketPlayOutDisguisedChatMessage(final ServerBuffer buf) {
        message = buf.readComponent();
        messageType = MessageType.fromID(buf.readVarInt());
        source = buf.readComponent();
        target = buf.readOptional(ServerBuffer::readComponent).orElse(null);
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public PacketState getPacketState() {
        return PacketState.PLAY_OUT;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeComponent(message)
                .writeVarInt(messageType.getID())
                .writeComponent(source)
                .writeOptional(target, ServerBuffer::writeComponent)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutDisguisedChatMessage(new FriendlyByteBuf(serialize()));
    }

}
