package org.machinemc.server.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.utils.FriendlyByteBuf;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayOutEntityEvent extends PacketOut {

    private static final int ID = 0x1A;

    private int entityId;
    private byte event;

    static {
        register(PacketPlayOutEntityEvent.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutEntityEvent::new);
    }

    public PacketPlayOutEntityEvent(final ServerBuffer buf) {
        entityId = buf.readVarInt();
        event = buf.readByte();
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
                .writeVarInt(entityId)
                .writeByte(event)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutEntityEvent(new FriendlyByteBuf(serialize()));
    }

}
