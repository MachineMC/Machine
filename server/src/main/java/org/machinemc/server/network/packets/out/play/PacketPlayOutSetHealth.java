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
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.api.utils.FriendlyByteBuf;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class PacketPlayOutSetHealth extends PacketOut {

    private static final int ID = 0x57;

    private float health;
    private int food;
    private float saturation;

    static {
        register(PacketPlayOutSetHealth.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutSetHealth::new);
    }

    public PacketPlayOutSetHealth(final ServerBuffer buf) {
        health = buf.readFloat();
        food = buf.readVarInt();
        saturation = buf.readFloat();
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
                .writeFloat(health)
                .writeVarInt(food)
                .writeFloat(saturation)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutSetHealth(new FriendlyByteBuf(serialize()));
    }

}
