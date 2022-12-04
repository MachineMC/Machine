package me.pesekjak.machine.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.utils.math.Vector3;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayOutSpawnExperienceOrb extends PacketOut {

    private static final int ID = 0x01;

    private int entityId;
    @NotNull
    private Vector3 position;
    private short count;

    static {
        register(PacketPlayOutSpawnExperienceOrb.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutSpawnExperienceOrb::new);
    }

    public PacketPlayOutSpawnExperienceOrb(FriendlyByteBuf buf) {
        entityId = buf.readVarInt();
        position = Vector3.of(buf.readDouble(), buf.readDouble(), buf.readDouble());
        count = buf.readByte();
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
        FriendlyByteBuf buf = new FriendlyByteBuf()
                .writeVarInt(entityId)
                .writeDouble(position.getX())
                .writeDouble(position.getY())
                .writeDouble(position.getZ());
        return buf.writeShort(count)
                .bytes();
    }

    @Override
    public @NotNull PacketOut clone() {
        return new PacketPlayOutSpawnExperienceOrb(new FriendlyByteBuf(serialize()));
    }

}
