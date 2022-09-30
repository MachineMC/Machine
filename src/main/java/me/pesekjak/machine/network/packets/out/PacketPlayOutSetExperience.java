package me.pesekjak.machine.network.packets.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;

@AllArgsConstructor
public class PacketPlayOutSetExperience extends PacketOut {

    private static final int ID = 0x54;

    @Getter @Setter
    private float experienceBar; // Between 0 and 1
    @Getter @Setter
    private int level, totalExperience;

    static {
        register(PacketPlayOutSetExperience.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutSetExperience::new);
    }

    public PacketPlayOutSetExperience(FriendlyByteBuf buf) {
        experienceBar = buf.readFloat();
        level = buf.readVarInt();
        totalExperience = buf.readVarInt();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeFloat(experienceBar)
                .writeVarInt(level)
                .writeVarInt(totalExperience)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutSetExperience(new FriendlyByteBuf(serialize()));
    }

}
