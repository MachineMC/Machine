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
import org.machinemc.scriptive.components.Component;
import org.jetbrains.annotations.Nullable;
import org.machinemc.api.network.packets.Packet;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.api.utils.FriendlyByteBuf;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayOutChatPreview extends PacketOut {

    private static final int ID = 0x0C;

    private int queryID;
    private @Nullable Component preview;

    static {
        register(PacketPlayOutChatPreview.class, ID, Packet.PacketState.PLAY_OUT,
                PacketPlayOutChatPreview::new);
    }

    public PacketPlayOutChatPreview(final ServerBuffer buf) {
        queryID = buf.readVarInt();
        if (buf.readBoolean())
            preview = buf.readComponent();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public Packet.PacketState getPacketState() {
        return Packet.PacketState.PLAY_OUT;
    }

    @Override
    public byte[] serialize() {
        final FriendlyByteBuf buf = new FriendlyByteBuf()
                .writeInt(queryID)
                .writeBoolean(preview != null);
        if (preview != null) {
            buf.writeComponent(preview);
        }
        return buf.bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutChatPreview(new FriendlyByteBuf(serialize()));
    }

}
