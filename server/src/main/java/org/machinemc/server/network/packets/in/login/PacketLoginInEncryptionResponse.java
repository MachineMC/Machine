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
package org.machinemc.server.network.packets.in.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.server.network.packets.PacketIn;
import org.machinemc.server.utils.FriendlyByteBuf;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketLoginInEncryptionResponse extends PacketIn {

    private static final int ID = 0x01;

    private byte[] secret;
    private byte[] verifyToken;

    static {
        register(PacketLoginInEncryptionResponse.class, ID, PacketState.LOGIN_IN,
                PacketLoginInEncryptionResponse::new);
    }

    public PacketLoginInEncryptionResponse(final ServerBuffer buf) {
        secret = buf.readByteArray();
        verifyToken = buf.readByteArray();
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public PacketState getPacketState() {
        return PacketState.LOGIN_IN;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeByteArray(secret)
                .writeByteArray(verifyToken)
                .bytes();
    }

    @Override
    public PacketIn clone() {
        return new PacketLoginInEncryptionResponse(new FriendlyByteBuf(serialize()));
    }

}
