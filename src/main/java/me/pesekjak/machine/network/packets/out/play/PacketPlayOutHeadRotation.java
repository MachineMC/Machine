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
        angle = buf.readAngle();
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
                .writeAngle(angle)
                .bytes();
    }

    @Override
    public @NotNull PacketOut clone() {
        return new PacketPlayOutHeadRotation(new FriendlyByteBuf(serialize()));
    }

}
