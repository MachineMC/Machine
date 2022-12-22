package me.pesekjak.machine.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.utils.ServerBuffer;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@ToString
public class PacketPlayOutAcknowledgeBlockChange extends PacketOut {

    private static final int ID = 0x05;

    @Getter @Setter
    private int sequenceId;

    static {
        register(PacketPlayOutAcknowledgeBlockChange.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutAcknowledgeBlockChange::new);
    }

    public PacketPlayOutAcknowledgeBlockChange(@NotNull ServerBuffer buf) {
        sequenceId = buf.readVarInt();
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public @NotNull PacketState getPacketState() {
        return PacketState.PLAY_OUT;
    }

    @Override
    public byte @NotNull [] serialize() {
        return new FriendlyByteBuf()
                .writeVarInt(sequenceId)
                .bytes();
    }

    @Override
    public @NotNull PacketOut clone() {
        return new PacketPlayOutAcknowledgeBlockChange(new FriendlyByteBuf(serialize()));
    }

}
