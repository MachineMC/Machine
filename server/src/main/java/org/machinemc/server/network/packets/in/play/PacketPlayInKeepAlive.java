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
import org.machinemc.server.network.packets.PacketIn;
import org.machinemc.api.utils.FriendlyByteBuf;
import org.machinemc.api.utils.ServerBuffer;

@ToString
@AllArgsConstructor
public class PacketPlayInKeepAlive extends PacketIn {

    private static final int ID = 0x12;

    @Getter @Setter
    private long keepAliveID;

    static {
        register(PacketPlayInKeepAlive.class, ID, PacketState.PLAY_IN,
                PacketPlayInKeepAlive::new);
    }

    public PacketPlayInKeepAlive(final ServerBuffer buf) {
        keepAliveID = buf.readLong();
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
                .writeLong(keepAliveID)
                .bytes();
    }

    @Override
    public PacketIn clone() {
        return new PacketPlayInKeepAlive(new FriendlyByteBuf(serialize()));
    }

}
