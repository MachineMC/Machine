package me.pesekjak.machine.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

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
                .writeVarInt(entityId)
                .writeShort(velocityX)
                .writeShort(velocityY)
                .writeShort(velocityZ)
                .bytes();
    }

    @Override
    public @NotNull PacketOut clone() {
        return new PacketPlayOutEntityVelocity(new FriendlyByteBuf(serialize()));
    }

}
