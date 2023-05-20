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
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.server.network.packets.PacketIn;
import org.machinemc.server.utils.FriendlyByteBuf;

@ToString
@Getter @Setter
@AllArgsConstructor
public class PacketPlayInPlayerOnGround extends PacketIn {

    private static final int ID = 0x17;

    static {
        register(PacketPlayInPlayerOnGround.class, ID, PacketState.PLAY_IN,
                PacketPlayInPlayerOnGround::new);
    }

    private boolean onGround;

    public PacketPlayInPlayerOnGround(final ServerBuffer buf) {
        onGround = buf.readBoolean();
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
                .writeBoolean(onGround)
                .bytes();
    }

    @Override
    public PacketIn clone() {
        return new PacketPlayInPlayerOnGround(new FriendlyByteBuf(serialize()));
    }

}
