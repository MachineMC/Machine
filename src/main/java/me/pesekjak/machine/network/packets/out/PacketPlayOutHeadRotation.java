package me.pesekjak.machine.network.packets.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;

@AllArgsConstructor
public class PacketPlayOutHeadRotation extends PacketOut {

    private static final int ID = 0x3F;

    @Getter
    private int entityId;
    @Getter @Setter
    private float angle;

    static {
        register(PacketPlayOutHeadRotation.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutHeadRotation::new);
    }

    public PacketPlayOutHeadRotation(FriendlyByteBuf buf) {
        entityId = buf.readVarInt();
        angle = buf.readAngle();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeVarInt(entityId)
                .writeAngle(angle)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutHeadRotation(new FriendlyByteBuf(serialize()));
    }

}
