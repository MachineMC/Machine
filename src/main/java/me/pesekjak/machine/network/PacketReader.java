package me.pesekjak.machine.network;

import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.PacketImpl;
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

    public PacketReader(FriendlyByteBuf buf, PacketImpl.PacketState state) {
        buf.readVarInt(); // Size
        packet = PacketFactory.produceIn(
                PacketFactory.getPacketInById(buf.readVarInt() | state.getMask()),
                buf
        );
    }

}
