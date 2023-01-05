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
@Getter @Setter
public class PacketPlayOutOpenHorseInventory extends PacketOut {

    private static final int ID = 0x1E;

    private byte windowId;
    private int slotCount;
    private int entityId;

    static {
        register(PacketPlayOutOpenHorseInventory.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutOpenHorseInventory::new);
    }

    public PacketPlayOutOpenHorseInventory(@NotNull ServerBuffer buf) {
        windowId = buf.readByte();
        slotCount = buf.readVarInt();
        entityId = buf.readInt();
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
                .writeByte(windowId)
                .writeVarInt(slotCount)
                .writeInt(entityId)
                .bytes();
    }

    @Override
    public @NotNull PacketOut clone() {
        return new PacketPlayOutOpenHorseInventory(new FriendlyByteBuf(serialize()));
    }

}
