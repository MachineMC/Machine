package me.pesekjak.machine.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.utils.ServerBuffer;
import me.pesekjak.machine.world.BlockPosition;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@ToString
public class PacketPlayOutOpenSignEditor extends PacketOut {

    private static final int ID = 0x2E;

    @Getter @Setter
    private @NotNull BlockPosition position;

    static {
        register(PacketPlayOutOpenSignEditor.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutOpenSignEditor::new);
    }

    public PacketPlayOutOpenSignEditor(@NotNull ServerBuffer buf) {
        position = buf.readBlockPos();
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
                .writeBlockPos(position)
                .bytes();
    }

    @Override
    public @NotNull PacketOut clone() {
        return new PacketPlayOutOpenSignEditor(new FriendlyByteBuf(serialize()));
    }

}
