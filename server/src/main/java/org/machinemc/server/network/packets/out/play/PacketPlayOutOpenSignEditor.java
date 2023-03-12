package org.machinemc.server.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.api.world.BlockPosition;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.utils.FriendlyByteBuf;

@AllArgsConstructor
@ToString
public class PacketPlayOutOpenSignEditor extends PacketOut {

    private static final int ID = 0x2E;

    @Getter @Setter
    private BlockPosition position;

    static {
        register(PacketPlayOutOpenSignEditor.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutOpenSignEditor::new);
    }

    public PacketPlayOutOpenSignEditor(ServerBuffer buf) {
        position = buf.readBlockPos();
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public PacketState getPacketState() {
        return PacketState.PLAY_OUT;
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
