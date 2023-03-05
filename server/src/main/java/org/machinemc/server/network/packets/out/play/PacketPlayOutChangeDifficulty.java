package org.machinemc.server.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.api.world.Difficulty;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.utils.FriendlyByteBuf;

@AllArgsConstructor
@ToString
@Getter
public class PacketPlayOutChangeDifficulty extends PacketOut {

    private static final int ID = 0x0B;

    @Setter
    private Difficulty difficulty;
    // Always locked in multiplayer
    private final boolean isLocked = true;

    static {
        register(PacketPlayOutChangeDifficulty.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutChangeDifficulty::new);
    }

    public PacketPlayOutChangeDifficulty(ServerBuffer buf) {
        difficulty = Difficulty.fromID(buf.readByte());
        buf.readBoolean();
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public PacketState getPacketState() {
        return PacketState.PLAY_OUT;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeByte((byte) difficulty.getId())
                .writeBoolean(isLocked)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutChangeDifficulty(new FriendlyByteBuf(serialize()));
    }
}
