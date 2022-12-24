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
public class PacketPlayOutHeldItem extends PacketOut {

    private static final int ID = 0x4A;

    @Getter @Setter
    private byte slot;

    static {
        register(PacketPlayOutHeldItem.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutHeldItem::new);
    }

    public PacketPlayOutHeldItem(@NotNull ServerBuffer buf) {
        slot = buf.readByte();
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
                .writeByte(slot)
                .bytes();
    }

    @Override
    public @NotNull PacketOut clone() {
        return new PacketPlayOutHeldItem(new FriendlyByteBuf(serialize()));
    }

}
