package me.pesekjak.machine.network.packets.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.world.BlockPosition;

@AllArgsConstructor
public class PacketPlayOutOpenSignEditor extends PacketOut {

    private static final int ID = 0x2E;

    @Getter @Setter
    private BlockPosition position;

    static {
        register(PacketPlayOutOpenSignEditor.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutOpenSignEditor::new);
    }

    public PacketPlayOutOpenSignEditor(FriendlyByteBuf buf) {
        position = buf.readBlockPos();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeBlockPos(position)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutOpenSignEditor(new FriendlyByteBuf(serialize()));
    }

}
