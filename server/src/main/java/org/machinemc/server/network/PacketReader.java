package org.machinemc.server.network;

import lombok.Getter;
import lombok.Setter;
import org.machinemc.api.network.packets.Packet;
import org.machinemc.server.network.packets.PacketImpl;
import org.machinemc.server.network.packets.PacketFactory;
import org.machinemc.server.utils.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

/**
 * Holder of a packet that was read from a client connection
 * and will be read by a server.
 */
public class PacketReader implements PacketHolder {

    @Getter @Setter
    private @Nullable Packet packet;

    public PacketReader(FriendlyByteBuf buf, PacketImpl.PacketState state) {
        buf.readVarInt(); // Size
        Class<? extends Packet> packetClass = PacketFactory.getPacketByRawId(buf.readVarInt(), state);
        if(packetClass == null) return;
        packet = PacketFactory.produce(
                packetClass,
                buf
        );
    }

}
