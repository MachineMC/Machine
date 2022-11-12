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
public class PacketPlayOutEntityVelocity extends PacketOut {

    private static final int ID = 0x52;

    private int entityId;
    private short velocityX, velocityY, velocityZ;

    static {
        register(PacketPlayOutEntityVelocity.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutEntityVelocity::new);
    }

    public PacketPlayOutEntityVelocity(FriendlyByteBuf buf) {
        entityId = buf.readVarInt();
        velocityX = buf.readShort();
        velocityY = buf.readShort();
        velocityZ = buf.readShort();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeVarInt(entityId)
                .writeShort(velocityX)
                .writeShort(velocityY)
                .writeShort(velocityZ)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutEntityVelocity(new FriendlyByteBuf(serialize()));
    }

}
