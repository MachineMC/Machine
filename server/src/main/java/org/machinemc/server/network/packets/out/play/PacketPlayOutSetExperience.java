package org.machinemc.server.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.utils.FriendlyByteBuf;

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

    public PacketPlayOutSetExperience(ServerBuffer buf) {
        experienceBar = buf.readFloat();
        level = buf.readVarInt();
        totalExperience = buf.readVarInt();
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
