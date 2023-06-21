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
import org.machinemc.api.entities.player.PlayerTextures;
import org.machinemc.api.network.packets.Packet;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.api.utils.FriendlyByteBuf;
import org.machinemc.api.utils.ServerBuffer;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketLoginOutSuccess extends PacketOut {

    private static final int ID = 0x02;

    private UUID uuid;
    private String userName;
    private @Nullable PlayerTextures textures;

    static {
        register(PacketLoginOutSuccess.class, ID, Packet.PacketState.LOGIN_OUT,
                PacketLoginOutSuccess::new
        );
    }

    public PacketLoginOutSuccess(final ServerBuffer buf) {
        uuid = buf.readUUID();
        userName = buf.readString(StandardCharsets.UTF_8);
        textures = buf.readTextures();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public Packet.PacketState getPacketState() {
        return Packet.PacketState.LOGIN_OUT;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeUUID(uuid)
                .writeString(userName, StandardCharsets.UTF_8)
                .writeTextures(textures)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketLoginOutSuccess(new FriendlyByteBuf(serialize()));
    }

}
