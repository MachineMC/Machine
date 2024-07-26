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
package org.machinemc.network.protocol.pluginmessage.clientbound;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.machinemc.barebones.key.NamespacedKey;
import org.machinemc.network.protocol.PacketFlow;
import org.machinemc.network.protocol.PacketGroups;
import org.machinemc.network.protocol.PacketIDMap;
import org.machinemc.network.protocol.pluginmessage.PluginMesagePackets;
import org.machinemc.network.protocol.pluginmessage.PluginMessagePacketListener;
import org.machinemc.paklet.CustomPacket;
import org.machinemc.paklet.DataVisitor;
import org.machinemc.paklet.Packet;
import org.machinemc.paklet.PacketID;
import org.machinemc.paklet.serialization.Serializer;
import org.machinemc.paklet.serialization.SerializerContext;

/**
 * Plugin message packet used to send data to the client.
 */
@Data
@Packet(
        id = Packet.DYNAMIC_PACKET,
        group = {
                PacketGroups.Configuration.ClientBound.NAME,
                PacketGroups.Play.ClientBound.NAME
        },
        catalogue = PluginMesagePackets.class
)
@NoArgsConstructor
@AllArgsConstructor
public class S2CPluginMessagePacket implements org.machinemc.network.protocol.Packet<PluginMessagePacketListener>, CustomPacket {

    @PacketID
    private static int id() {
        return PacketIDMap.compute(
                PacketGroups.Configuration.ClientBound.NAME, PacketGroups.Configuration.ClientBound.PLUGIN_MESSAGE,
                PacketGroups.Play.ClientBound.NAME, PacketGroups.Play.ClientBound.PLUGIN_MESSAGE
        );
    }

    private NamespacedKey channel;
    private byte[] data;

    @Override
    public void handle(final PluginMessagePacketListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PacketFlow flow() {
        return PacketFlow.CLIENTBOUND;
    }

    @Override
    public void construct(final SerializerContext context, final DataVisitor visitor) {
        final Serializer<NamespacedKey> serializer = context.serializerProvider().getFor(NamespacedKey.class);
        channel = visitor.read(context, serializer);
        data = visitor.finish();
    }

    @Override
    public void deconstruct(final SerializerContext context, final DataVisitor visitor) {
        final Serializer<NamespacedKey> serializer = context.serializerProvider().getFor(NamespacedKey.class);
        visitor.write(context, serializer, channel);
        visitor.writeBytes(data);
    }

}
