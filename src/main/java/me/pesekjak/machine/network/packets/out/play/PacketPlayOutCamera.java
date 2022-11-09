package me.pesekjak.machine.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;

@AllArgsConstructor
@ToString
public class PacketPlayOutCamera extends PacketOut {

    private static final int ID = 0x49;

    @Getter @Setter
    private int cameraId;

    static {
        register(PacketPlayOutCamera.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutCamera::new);
    }

    public PacketPlayOutCamera(FriendlyByteBuf buf) {
        cameraId = buf.readVarInt();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeVarInt(cameraId)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutCamera(new FriendlyByteBuf(serialize()));
    }

}
