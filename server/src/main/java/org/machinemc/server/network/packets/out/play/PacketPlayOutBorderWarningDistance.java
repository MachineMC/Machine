package org.machinemc.server.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.utils.FriendlyByteBuf;
import org.machinemc.api.utils.ServerBuffer;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@ToString
public class PacketPlayOutBorderWarningDistance extends PacketOut {

    private static final int ID = 0x48;

    @Getter @Setter
    private int warningBlocks;

    static {
        register(PacketPlayOutBorderWarningDistance.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutBorderWarningDistance::new);
    }

    public PacketPlayOutBorderWarningDistance(@NotNull ServerBuffer buf) {
        warningBlocks = buf.readVarInt();
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
                .writeVarInt(warningBlocks)
                .bytes();
    }

    @Override
    public @NotNull PacketOut clone() {
        return new PacketPlayOutBorderWarningDistance(new FriendlyByteBuf(serialize()));
    }

}
