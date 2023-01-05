package me.pesekjak.machine.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.utils.ServerBuffer;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayOutSetExperience extends PacketOut {

    private static final int ID = 0x54;

    private float experienceBar; // Between 0 and 1
    private int level, totalExperience;

    static {
        register(PacketPlayOutSetExperience.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutSetExperience::new);
    }

    public PacketPlayOutSetExperience(@NotNull ServerBuffer buf) {
        experienceBar = buf.readFloat();
        level = buf.readVarInt();
        totalExperience = buf.readVarInt();
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
                .writeFloat(experienceBar)
                .writeVarInt(level)
                .writeVarInt(totalExperience)
                .bytes();
    }

    @Override
    public @NotNull PacketOut clone() {
        return new PacketPlayOutSetExperience(new FriendlyByteBuf(serialize()));
    }

}
