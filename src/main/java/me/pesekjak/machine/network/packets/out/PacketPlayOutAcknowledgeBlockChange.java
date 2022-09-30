package me.pesekjak.machine.network.packets.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;

@AllArgsConstructor
public class PacketPlayOutAcknowledgeBlockChange extends PacketOut {

    private static final int ID = 0x05;

    @Getter @Setter
    private int sequenceId;

    static {
        register(PacketPlayOutAcknowledgeBlockChange.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutAcknowledgeBlockChange::new);
    }

    public PacketPlayOutAcknowledgeBlockChange(FriendlyByteBuf buf) {
        sequenceId = buf.readVarInt();
    }

    @Override
    public int getID() {
        return ID;
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
