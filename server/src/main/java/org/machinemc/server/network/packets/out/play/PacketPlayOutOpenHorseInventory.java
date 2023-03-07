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
@Getter @Setter
public class PacketPlayOutOpenHorseInventory extends PacketOut {

    private static final int ID = 0x1E;

    private byte windowId;
    private int slotCount;
    private int entityId;

    static {
        register(PacketPlayOutOpenHorseInventory.class, ID, Packet.PacketState.PLAY_OUT,
                PacketPlayOutOpenHorseInventory::new);
    }

    public PacketPlayOutOpenHorseInventory(ServerBuffer buf) {
        windowId = buf.readByte();
        slotCount = buf.readVarInt();
        entityId = buf.readInt();
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
                .writeByte(windowId)
                .writeVarInt(slotCount)
                .writeInt(entityId)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutOpenHorseInventory(new FriendlyByteBuf(serialize()));
    }

}
