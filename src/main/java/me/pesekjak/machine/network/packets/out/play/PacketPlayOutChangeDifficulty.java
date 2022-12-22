package me.pesekjak.machine.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.utils.ServerBuffer;
import me.pesekjak.machine.world.Difficulty;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@ToString
@Getter
public class PacketPlayOutChangeDifficulty extends PacketOut {

    private static final int ID = 0x0B;

    @Setter
    private @NotNull Difficulty difficulty;
    // Always locked in multiplayer
    private final boolean isLocked = true;

    static {
        register(PacketPlayOutChangeDifficulty.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutChangeDifficulty::new);
    }

    public PacketPlayOutChangeDifficulty(@NotNull ServerBuffer buf) {
        difficulty = Difficulty.fromID(buf.readByte());
        buf.readBoolean();
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
                .writeByte((byte) difficulty.getId())
                .writeBoolean(isLocked)
                .bytes();
    }

    @Override
    public @NotNull PacketOut clone() {
        return new PacketPlayOutChangeDifficulty(new FriendlyByteBuf(serialize()));
    }
}
