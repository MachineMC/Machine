package me.pesekjak.machine.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;

@AllArgsConstructor
@ToString
public class PacketPlayOutRemoveEntities extends PacketOut {

    private static final int ID = 0x3B;

    @Getter @Setter
    private int[] entityIds;

    static {
        register(PacketPlayOutRemoveEntities.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutRemoveEntities::new);
    }

    public PacketPlayOutRemoveEntities(FriendlyByteBuf buf) {
        entityIds = buf.readVarIntArray();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeVarIntArray(entityIds)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutRemoveEntities(new FriendlyByteBuf(serialize()));
    }

}
