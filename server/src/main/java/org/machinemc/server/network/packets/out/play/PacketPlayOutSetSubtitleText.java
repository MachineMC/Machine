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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.machinemc.scriptive.components.Component;
import org.machinemc.api.network.packets.Packet;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.scriptive.serialization.ComponentProperties;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.api.utils.FriendlyByteBuf;

@ToString
@AllArgsConstructor
public class PacketPlayOutSetSubtitleText extends PacketOut {

    private static final int ID = 0x5D;

    @Getter @Setter
    private ComponentProperties text;

    static {
        register(PacketPlayOutSetSubtitleText.class, ID, Packet.PacketState.PLAY_OUT,
                PacketPlayOutSetSubtitleText::new);
    }

    public PacketPlayOutSetSubtitleText(final ServerBuffer buf) {
        text = buf.readComponent();
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
                .writeComponent(text)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutSetSubtitleText(new FriendlyByteBuf(serialize()));
    }

}
