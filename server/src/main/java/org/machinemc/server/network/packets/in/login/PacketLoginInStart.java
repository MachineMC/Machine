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
import org.jetbrains.annotations.Nullable;
import org.machinemc.api.utils.ServerBuffer;
import lombok.*;
import org.machinemc.api.auth.PublicKeyData;
import org.machinemc.server.network.packets.PacketIn;
import org.machinemc.server.utils.FriendlyByteBuf;
import org.machinemc.api.utils.FriendlyByteBuf;
import org.machinemc.api.utils.ServerBuffer;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class PacketLoginInStart extends PacketIn {

    private static final int ID = 0x00;

    private String username;
    @Getter(AccessLevel.NONE)
    private @Nullable UUID uuid;

    static {
        register(PacketLoginInStart.class, ID, PacketState.LOGIN_IN,
                PacketLoginInStart::new
        );
    }

    public PacketLoginInStart(final ServerBuffer buf) {
        username = buf.readString(StandardCharsets.UTF_8);
        uuid = buf.readOptional(ServerBuffer::readUUID).orElse(null);
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public PacketState getPacketState() {
        return PacketState.LOGIN_IN;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeString(username, StandardCharsets.UTF_8)
                .writeOptional(uuid, ServerBuffer::writeUUID)
                .bytes();
    }

    @Override
    public PacketIn clone() {
        return new PacketLoginInStart(new FriendlyByteBuf(serialize()));
    }

    /**
     * @return UUID of the player
     */
    public UUID getUUID() {
        return uuid;
    }

}
