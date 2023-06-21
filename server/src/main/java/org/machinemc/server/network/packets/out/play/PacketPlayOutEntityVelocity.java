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

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayOutEntityVelocity extends PacketOut {

    private static final int ID = 0x52;

    private int entityID;
    private short velocityX, velocityY, velocityZ;

    static {
        register(PacketPlayOutEntityVelocity.class, ID, Packet.PacketState.PLAY_OUT,
                PacketPlayOutEntityVelocity::new);
    }

    public PacketPlayOutEntityVelocity(final ServerBuffer buf) {
        entityID = buf.readVarInt();
        velocityX = buf.readShort();
        velocityY = buf.readShort();
        velocityZ = buf.readShort();
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
                .writeVarInt(entityID)
                .writeShort(velocityX)
                .writeShort(velocityY)
                .writeShort(velocityZ)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutEntityVelocity(new FriendlyByteBuf(serialize()));
    }

}
