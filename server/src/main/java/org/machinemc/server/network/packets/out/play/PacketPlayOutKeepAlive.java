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
import org.machinemc.server.utils.FriendlyByteBuf;

@AllArgsConstructor
@ToString
public class PacketPlayOutKeepAlive extends PacketOut {

    private static final int ID = 0x20;

    @Getter @Setter
    private long keepAliveID;

    static {
        register(PacketPlayOutKeepAlive.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutKeepAlive::new);
    }

    public PacketPlayOutKeepAlive(final ServerBuffer buf) {
        keepAliveID = buf.readLong();
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
                .writeLong(keepAliveID)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutKeepAlive(new FriendlyByteBuf(serialize()));
    }

}
