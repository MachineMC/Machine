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

import lombok.*;
import org.machinemc.api.auth.PublicKeyData;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.server.network.packets.PacketIn;
import org.machinemc.server.utils.FriendlyByteBuf;

import java.util.UUID;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayInPlayerSession extends PacketIn {

    private static final int ID = 0x06;

    static {
        register(PacketPlayInPlayerSession.class, ID, PacketState.PLAY_IN,
                PacketPlayInPlayerSession::new);
    }

    private UUID sessionId;
    private PublicKeyData publicKey;

    public PacketPlayInPlayerSession(final ServerBuffer buf) {
        sessionId = buf.readUUID();
        publicKey = buf.readPublicKey();
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public PacketState getPacketState() {
        return PacketState.PLAY_IN;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeUUID(sessionId)
                .writePublicKey(publicKey)
                .bytes();
    }

    @Override
    public PacketIn clone() {
        return new PacketPlayInPlayerSession(new FriendlyByteBuf(serialize()));
    }

}
