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

import lombok.*;
import org.machinemc.scriptive.components.Component;
import org.jetbrains.annotations.Nullable;
import org.machinemc.api.network.packets.Packet;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.scriptive.serialization.ComponentProperties;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.api.utils.FriendlyByteBuf;

import java.nio.charset.StandardCharsets;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class PacketPlayOutResourcePack extends PacketOut {

    private static final int ID = 0x40;

    @Getter(AccessLevel.NONE)
    private String url;
    private String hash;
    private boolean forced;
    private @Nullable ComponentProperties promptMessage;

    static {
        register(PacketPlayOutResourcePack.class, ID, Packet.PacketState.PLAY_OUT,
                PacketPlayOutResourcePack::new);
    }

    public PacketPlayOutResourcePack(final ServerBuffer buf) {
        url = buf.readString(StandardCharsets.UTF_8);
        hash = buf.readString(StandardCharsets.UTF_8);
        forced = buf.readBoolean();
        promptMessage = buf.readOptional(ServerBuffer::readComponent).orElse(null);
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public Packet.PacketState getPacketState() {
        return Packet.PacketState.PLAY_OUT;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeString(url, StandardCharsets.UTF_8)
                .writeString(hash, StandardCharsets.UTF_8)
                .writeBoolean(forced)
                .writeOptional(promptMessage, ServerBuffer::writeComponent)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutResourcePack(new FriendlyByteBuf(serialize()));
    }

    /**
     * @return URL of the resource pack
     */
    public String getURL() {
        return url;
    }
}
