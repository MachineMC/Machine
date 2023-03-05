package org.machinemc.server.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.machinemc.api.network.packets.Packet;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.utils.FriendlyByteBuf;

@AllArgsConstructor
@ToString
public class PacketPlayOutAcknowledgeBlockChange extends PacketOut {

    private static final int ID = 0x05;

    @Getter @Setter
    private int sequenceId;

    static {
        register(PacketPlayOutAcknowledgeBlockChange.class, ID, Packet.PacketState.PLAY_OUT,
                PacketPlayOutAcknowledgeBlockChange::new);
    }

    public PacketPlayOutAcknowledgeBlockChange(ServerBuffer buf) {
        sequenceId = buf.readVarInt();
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public Packet.PacketState getPacketState() {
        return Packet.PacketState.PLAY_OUT;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeVarInt(sequenceId)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutAcknowledgeBlockChange(new FriendlyByteBuf(serialize()));
    }

}
