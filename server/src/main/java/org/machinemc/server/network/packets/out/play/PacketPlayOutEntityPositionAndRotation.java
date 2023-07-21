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
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.api.world.EntityPosition;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.api.utils.FriendlyByteBuf;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class PacketPlayOutEntityPositionAndRotation  extends PacketOut {

    private static final int ID = 0x29;

    static {
        register(PacketPlayOutEntityPositionAndRotation.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutEntityPositionAndRotation::new);
    }

    private int entityID;
    private short deltaX, deltaY, deltaZ;
    private float yaw, pitch;
    private boolean onGround;

    public PacketPlayOutEntityPositionAndRotation(final ServerBuffer buf) {
        entityID = buf.readVarInt();
        deltaX = buf.readShort();
        deltaY = buf.readShort();
        deltaZ = buf.readShort();
        yaw = buf.readAngle();
        pitch = buf.readAngle();
        onGround = buf.readBoolean();
    }

    public PacketPlayOutEntityPositionAndRotation(final int entityID,
                                                  final EntityPosition previousPosition,
                                                  final EntityPosition newPosition,
                                                  final boolean onGround) {
        this.entityID = entityID;
        this.deltaX = (short) ((newPosition.getX() * 32 - previousPosition.getX() * 32) * 128);
        this.deltaY = (short) ((newPosition.getY() * 32 - previousPosition.getY() * 32) * 128);
        this.deltaZ = (short) ((newPosition.getZ() * 32 - previousPosition.getZ() * 32) * 128);
        this.yaw = newPosition.getYaw();
        this.pitch = newPosition.getPitch();
        this.onGround = onGround;
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
                .writeVarInt(entityID)
                .writeShort(deltaX)
                .writeShort(deltaY)
                .writeShort(deltaZ)
                .writeAngle(yaw)
                .writeAngle(pitch)
                .writeBoolean(onGround)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutEntityPositionAndRotation(new FriendlyByteBuf(serialize()));
    }

}
