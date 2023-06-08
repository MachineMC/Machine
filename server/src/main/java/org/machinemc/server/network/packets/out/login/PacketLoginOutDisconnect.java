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
package org.machinemc.server.network.packets.out.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.machinemc.scriptive.components.Component;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.api.utils.FriendlyByteBuf;
import org.machinemc.api.utils.ServerBuffer;

@AllArgsConstructor
@ToString
public class PacketLoginOutDisconnect extends PacketOut {

    private static final int ID = 0x00;

    static {
        register(PacketLoginOutDisconnect.class, ID, PacketState.LOGIN_OUT,
                PacketLoginOutDisconnect::new
        );
    }

    @Getter @Setter
    private Component message;

    public PacketLoginOutDisconnect(final ServerBuffer buf) {
        message = buf.readComponent();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public PacketState getPacketState() {
        return PacketState.LOGIN_OUT;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeComponent(message)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketLoginOutDisconnect(new FriendlyByteBuf(serialize()));
    }

}
