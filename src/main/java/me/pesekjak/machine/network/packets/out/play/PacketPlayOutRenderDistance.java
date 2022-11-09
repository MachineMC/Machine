package me.pesekjak.machine.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;

@AllArgsConstructor
public class PacketPlayOutRenderDistance extends PacketOut {

    private static final int ID = 0x4B;

    @Getter @Setter
    private int viewDistance; // (2-32)

    static {
        register(PacketPlayOutRenderDistance.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutRenderDistance::new);
    }

    public PacketPlayOutRenderDistance(FriendlyByteBuf buf) {
        viewDistance = buf.readVarInt();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeVarInt(viewDistance)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutRenderDistance(new FriendlyByteBuf(serialize()));
    }

}
