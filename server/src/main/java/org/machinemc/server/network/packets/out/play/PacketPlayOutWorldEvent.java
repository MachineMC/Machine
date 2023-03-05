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
@Getter @Setter
public class PacketPlayOutWorldEvent extends PacketOut {

    private static final int ID = 0x22;

    private int event, data;
    private BlockPosition position;
    private boolean disableRelativeVolume;

    static {
        register(PacketPlayOutWorldEvent.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutWorldEvent::new);
    }

    public PacketPlayOutWorldEvent(ServerBuffer buf) {
        event = buf.readInt();
        position = buf.readBlockPos();
        data = buf.readInt();
        disableRelativeVolume = buf.readBoolean();
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
                .writeInt(event)
                .writeBlockPos(position)
                .writeInt(data)
                .writeBoolean(disableRelativeVolume)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutWorldEvent(new FriendlyByteBuf(serialize()));
    }

}
