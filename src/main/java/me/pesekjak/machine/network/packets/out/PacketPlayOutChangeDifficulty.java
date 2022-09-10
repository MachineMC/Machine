package me.pesekjak.machine.network.packets.out;

import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;

public class PacketPlayOutChangeDifficulty extends PacketOut {

    private static final int ID = 0x0B;

    @Getter @Setter
    private byte difficulty;
    // Always locked in multiplayer
    @Getter
    private final boolean isLocked = true;

    static {
        PacketOut.register(PacketPlayOutChangeDifficulty.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutChangeDifficulty::new);
    }

    public PacketPlayOutChangeDifficulty(FriendlyByteBuf buf) {
        difficulty = (byte) buf.readUnsignedByte();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeByte(difficulty)
                .writeBoolean(isLocked)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutChangeDifficulty(new FriendlyByteBuf(serialize()));
    }
}
