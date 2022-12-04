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
                .writeVarInt(cameraId)
                .bytes();
    }

    @Override
    public @NotNull PacketOut clone() {
        return new PacketPlayOutCamera(new FriendlyByteBuf(serialize()));
    }

}
