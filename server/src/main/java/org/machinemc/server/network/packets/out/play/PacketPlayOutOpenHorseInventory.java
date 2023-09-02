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
import org.machinemc.api.network.packets.Packet;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.api.utils.FriendlyByteBuf;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class PacketPlayOutOpenHorseInventory extends PacketOut {

    private static final int ID = 0x20;

    private byte windowID;
    private int slotCount;
    private int entityID;

    static {
        register(PacketPlayOutOpenHorseInventory.class, ID, Packet.PacketState.PLAY_OUT,
                PacketPlayOutOpenHorseInventory::new);
    }

    public PacketPlayOutOpenHorseInventory(final ServerBuffer buf) {
        windowID = buf.readByte();
        slotCount = buf.readVarInt();
        entityID = buf.readInt();
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
        return new FriendlyByteBuf()
                .writeByte(windowID)
                .writeVarInt(slotCount)
                .writeInt(entityID)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutOpenHorseInventory(new FriendlyByteBuf(serialize()));
    }

}
