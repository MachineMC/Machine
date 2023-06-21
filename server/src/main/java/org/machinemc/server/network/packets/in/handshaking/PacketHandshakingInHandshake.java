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
package org.machinemc.server.network.packets.in.handshaking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.machinemc.server.network.packets.PacketIn;
import org.machinemc.api.utils.FriendlyByteBuf;
import org.machinemc.api.utils.ServerBuffer;
import org.jetbrains.annotations.Range;

import java.nio.charset.StandardCharsets;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketHandshakingInHandshake extends PacketIn {

    private static final int ID = 0x00;

    private int protocolVersion;
    private String serverAddress;
    private int serverPort;
    private HandshakeType handshakeType;

    static {
        register(PacketHandshakingInHandshake.class, ID, PacketState.HANDSHAKING_IN,
                PacketHandshakingInHandshake::new
        );
    }

    public PacketHandshakingInHandshake(final ServerBuffer buf) {
        protocolVersion = buf.readVarInt();
        serverAddress = buf.readString(StandardCharsets.UTF_8);
        serverPort = buf.readShort() & 0xFFFF;
        handshakeType = HandshakeType.fromID(buf.readVarInt());
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public PacketState getPacketState() {
        return PacketState.HANDSHAKING_IN;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeVarInt(protocolVersion)
                .writeString(serverAddress, StandardCharsets.UTF_8)
                .writeShort((short) (serverPort & 0xFFFF))
                .writeVarInt(handshakeType.id)
                .bytes();
    }

    @Override
    public PacketIn clone() {
        return new PacketHandshakingInHandshake(new FriendlyByteBuf(serialize()));
    }

    @AllArgsConstructor
    public enum HandshakeType {
        STATUS(1),
        LOGIN(2);

        private final int id;

        /**
         * @return id of the handshake type
         */
        public int getID() {
            return id;
        }

        /**
         * Returns handshake with given id.
         * @param id id
         * @return handshake
         */
        public static HandshakeType fromID(final @Range(from = 1, to = 2) int id) {
            for (final HandshakeType type : HandshakeType.values()) {
                if (type.getID() == id) return type;
            }
            throw new RuntimeException("Unsupported Handshake type");
        }
    }

}
