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
public class PacketPlayOutRenderDistance extends PacketOut {

    private static final int ID = 0x4C;

    @Getter @Setter
    private int viewDistance; // (2-32)

    static {
        register(PacketPlayOutRenderDistance.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutRenderDistance::new);
    }

    public PacketPlayOutRenderDistance(@NotNull ServerBuffer buf) {
        viewDistance = buf.readVarInt();
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
                .writeVarInt(viewDistance)
                .bytes();
    }

    @Override
    public @NotNull PacketOut clone() {
        return new PacketPlayOutRenderDistance(new FriendlyByteBuf(serialize()));
    }

}
