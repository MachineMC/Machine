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
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.api.utils.FriendlyByteBuf;

import java.nio.charset.StandardCharsets;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class PacketPlayOutServerData extends PacketOut {

    private static final int ID = 0x45;

    @Getter(AccessLevel.NONE)
    private Component motd;
    private @Nullable String icon;
    private boolean enforcedSecureChat;

    static {
        register(PacketPlayOutServerData.class, ID, Packet.PacketState.PLAY_OUT,
                PacketPlayOutServerData::new);
    }

    public PacketPlayOutServerData(final ServerBuffer buf) {
        motd = buf.readComponent();
        icon = buf.readOptional(serverBuffer -> serverBuffer.readString(StandardCharsets.UTF_8)).orElse(null);
        enforcedSecureChat = buf.readBoolean();
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
                .writeComponent(motd)
                .writeOptional(icon, (serverBuffer, s) -> serverBuffer.writeString(s, StandardCharsets.UTF_8))
                .writeBoolean(enforcedSecureChat)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutServerData(new FriendlyByteBuf(serialize()));
    }

    /**
     * @return MOTD of the server
     */
    public Component getMOTD() {
        return motd;
    }

}
