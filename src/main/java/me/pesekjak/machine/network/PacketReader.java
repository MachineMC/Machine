package me.pesekjak.machine.network;

import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.Packet;
import me.pesekjak.machine.network.packets.PacketImpl;
import me.pesekjak.machine.network.packets.PacketFactory;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Holder of a packet that was read from a client connection
 * and will be read by a server.
 */
public class PacketReader implements PacketHolder {

    @Getter @Setter
    private @Nullable Packet packet;

    public PacketReader(@NotNull FriendlyByteBuf buf, @NotNull PacketImpl.PacketState state) {
        buf.readVarInt(); // Size
        Class<? extends Packet> packetClass = PacketFactory.getPacketByRawId(buf.readVarInt(), state);
        if(packetClass == null) return;
        packet = PacketFactory.produce(
                packetClass,
                buf
        );
    }

}
