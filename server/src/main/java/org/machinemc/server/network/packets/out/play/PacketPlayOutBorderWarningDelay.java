package org.machinemc.server.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.machinemc.api.network.packets.Packet;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.utils.FriendlyByteBuf;
import org.machinemc.api.utils.ServerBuffer;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@ToString
public class PacketPlayOutBorderWarningDelay extends PacketOut {

    private static final int ID = 0x47;

    @Getter @Setter
    private int warningTime;

    static {
        register(PacketPlayOutBorderWarningDelay.class, ID, Packet.PacketState.PLAY_OUT,
                PacketPlayOutBorderWarningDelay::new);
    }

    public PacketPlayOutBorderWarningDelay(@NotNull ServerBuffer buf) {
        warningTime = buf.readVarInt();
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public @NotNull Packet.PacketState getPacketState() {
        return Packet.PacketState.PLAY_OUT;
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
