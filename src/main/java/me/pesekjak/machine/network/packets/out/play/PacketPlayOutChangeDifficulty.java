package me.pesekjak.machine.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.world.Difficulty;

@AllArgsConstructor
public class PacketPlayOutChangeDifficulty extends PacketOut {

    private static final int ID = 0x0B;

    @Getter @Setter
    private Difficulty difficulty;
    // Always locked in multiplayer
    @Getter
    private final boolean isLocked = true;

    static {
        register(PacketPlayOutChangeDifficulty.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutChangeDifficulty::new);
    }

    public PacketPlayOutChangeDifficulty(FriendlyByteBuf buf) {
        difficulty = Difficulty.fromID(buf.readByte());
        buf.readBoolean();
    }

    @Override
    public int getID() {
        return ID;
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
