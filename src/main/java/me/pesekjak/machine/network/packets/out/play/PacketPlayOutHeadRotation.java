package me.pesekjak.machine.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayOutHeadRotation extends PacketOut {

    private static final int ID = 0x3F;

    private int entityId;
    private float angle;

    static {
        register(PacketPlayOutHeadRotation.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutHeadRotation::new);
    }

    public PacketPlayOutHeadRotation(FriendlyByteBuf buf) {
        entityId = buf.readVarInt();
        angle = buf.readFloat();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeVarInt(entityId)
                .writeFloat(angle)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutHeadRotation(new FriendlyByteBuf(serialize()));
    }

}
