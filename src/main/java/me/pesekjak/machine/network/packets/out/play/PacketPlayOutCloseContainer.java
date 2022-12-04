package me.pesekjak.machine.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@ToString
public class PacketPlayOutCloseContainer extends PacketOut {

    private static final int ID = 0x10;

    @Getter @Setter
    private byte windowId;

    static {
        register(PacketPlayOutCloseContainer.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutCloseContainer::new);
    }

    public PacketPlayOutCloseContainer(FriendlyByteBuf buf) {
        windowId = buf.readByte();
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
                .bytes();
    }

    @Override
    public @NotNull PacketOut clone() {
        return new PacketPlayOutCloseContainer(new FriendlyByteBuf(serialize()));
    }

}
