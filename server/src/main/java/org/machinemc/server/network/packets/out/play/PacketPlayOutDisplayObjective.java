package org.machinemc.server.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.utils.FriendlyByteBuf;
import org.machinemc.api.utils.ServerBuffer;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayOutDisplayObjective extends PacketOut {

    private static final int ID = 0x4F;

    // The position of the scoreboard. 0: list, 1: sidebar, 2: below name, 3 - 18: team specific sidebar, indexed as 3 + team color
    // TODO rework as enum
    private byte position;
    @NotNull
    private String objectiveName;

    static {
        register(PacketPlayOutDisplayObjective.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutDisplayObjective::new);
    }

    public PacketPlayOutDisplayObjective(@NotNull ServerBuffer buf) {
        position = buf.readByte();
        objectiveName = buf.readString(StandardCharsets.UTF_8);
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
                .writeByte(position)
                .writeString(objectiveName, StandardCharsets.UTF_8)
                .bytes();
    }

    @Override
    public @NotNull PacketOut clone() {
        return new PacketPlayOutDisplayObjective(new FriendlyByteBuf(serialize()));
    }

}
