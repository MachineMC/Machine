package me.pesekjak.machine.network.packets.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;

@AllArgsConstructor
public class PacketPlayOutOpenHorseInventory extends PacketOut {

    private static final int ID = 0x1E;

    @Getter @Setter
    private byte windowId;
    @Getter @Setter
    private int slotCount;
    @Getter @Setter
    private int entityId;

    static {
        register(PacketPlayOutOpenHorseInventory.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutOpenHorseInventory::new);
    }

    public PacketPlayOutOpenHorseInventory(FriendlyByteBuf buf) {
        windowId = buf.readByte();
        slotCount = buf.readVarInt();
        entityId = buf.readInt();
    }

    @Override
    public int getID() {
        return ID;
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
