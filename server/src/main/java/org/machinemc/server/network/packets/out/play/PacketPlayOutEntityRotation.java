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
import org.machinemc.server.utils.FriendlyByteBuf;

@ToString
@Getter @Setter
@AllArgsConstructor
public class PacketPlayOutEntityRotation  extends PacketOut {

    private static final int ID = 0x2A;

    static {
        register(PacketPlayOutEntityRotation.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutEntityRotation::new);
    }

    private int entityId;
    private float yaw, pitch;
    private boolean onGround;

    public PacketPlayOutEntityRotation(final ServerBuffer buf) {
        entityId = buf.readVarInt();
        yaw = buf.readAngle();
        pitch = buf.readAngle();
        onGround = buf.readBoolean();
    }

    public PacketPlayOutEntityRotation(final int entityId, final EntityPosition newPosition, final boolean onGround) {
        this.entityId = entityId;
        this.yaw = newPosition.getYaw();
        this.pitch = newPosition.getPitch();
        this.onGround = onGround;
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
        return new FriendlyByteBuf()
                .writeVarInt(entityId)
                .writeAngle(yaw)
                .writeAngle(pitch)
                .writeBoolean(onGround)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutEntityRotation(new FriendlyByteBuf(serialize()));
    }

}
