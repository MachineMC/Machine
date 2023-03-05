package org.machinemc.server.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.utils.FriendlyByteBuf;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.api.world.BlockPosition;
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
