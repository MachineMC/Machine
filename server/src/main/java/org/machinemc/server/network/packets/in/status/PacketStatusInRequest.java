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
package org.machinemc.server.network.packets.in.status;

import lombok.AllArgsConstructor;
import lombok.ToString;
import org.machinemc.server.network.packets.PacketIn;
import org.machinemc.api.utils.FriendlyByteBuf;
import org.machinemc.api.utils.ServerBuffer;

@ToString
@AllArgsConstructor
public class PacketStatusInRequest extends PacketIn {

    private static final int ID = 0x00;

    static {
        register(PacketStatusInRequest.class, ID, PacketState.STATUS_IN,
                PacketStatusInRequest::new
        );
    }

    public PacketStatusInRequest(final ServerBuffer buf) {

    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public PacketState getPacketState() {
        return PacketState.STATUS_IN;
    }

    @Override
    public byte[] serialize() {
        return new byte[1];
    }

    @Override
    public PacketIn clone() {
        return new PacketStatusInRequest(new FriendlyByteBuf(serialize()));
    }

}
