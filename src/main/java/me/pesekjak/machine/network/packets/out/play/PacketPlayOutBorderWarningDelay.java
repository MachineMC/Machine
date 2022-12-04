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
public class PacketPlayOutBorderWarningDelay extends PacketOut {

    private static final int ID = 0x47;

    @Getter @Setter
    private int warningTime;

    static {
        register(PacketPlayOutBorderWarningDelay.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutBorderWarningDelay::new);
    }

    public PacketPlayOutBorderWarningDelay(FriendlyByteBuf buf) {
        warningTime = buf.readVarInt();
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
                .writeVarInt(warningTime)
                .bytes();
    }

    @Override
    public @NotNull PacketOut clone() {
        return new PacketPlayOutBorderWarningDelay(new FriendlyByteBuf(serialize()));
    }

}
