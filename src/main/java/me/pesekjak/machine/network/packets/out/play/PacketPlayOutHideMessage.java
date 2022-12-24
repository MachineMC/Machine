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
public class PacketPlayOutHideMessage extends PacketOut {

    private static final int ID = 0x18;

    @Getter @Setter
    private byte @NotNull [] signature;

    static {
        register(PacketPlayOutHideMessage.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutHideMessage::new);
    }

    public PacketPlayOutHideMessage(@NotNull ServerBuffer buf) {
        signature = buf.readByteArray();
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
                .writeByteArray(signature)
                .bytes();
    }

    @Override
    public @NotNull PacketOut clone() {
        return new PacketPlayOutHideMessage(new FriendlyByteBuf(serialize()));
    }

}
