package me.pesekjak.machine.network;

import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.Packet;
import me.pesekjak.machine.network.packets.PacketFactory;
import me.pesekjak.machine.network.packets.PacketIn;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

/**
 * Holder of a packet that was read from a client connection
 * and will be read by a server.
 */
public class PacketReader implements PacketHolder {

    @Getter @Setter @Nullable
    private PacketIn packet;

    public PacketReader(FriendlyByteBuf buf, Packet.PacketState state) {
        buf.readVarInt(); // Size
        packet = PacketFactory.produceIn(
                PacketFactory.getPacketInById(buf.readVarInt() | state.getMask()),
                buf
        );
    }

}
