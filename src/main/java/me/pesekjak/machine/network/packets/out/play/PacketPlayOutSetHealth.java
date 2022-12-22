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
public class PacketPlayOutSetHealth extends PacketOut {

    private static final int ID = 0x55;

    private float health;
    private int food;
    private float saturation;

    static {
        register(PacketPlayOutSetHealth.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutSetHealth::new);
    }

    public PacketPlayOutSetHealth(@NotNull ServerBuffer buf) {
        health = buf.readFloat();
        food = buf.readVarInt();
        saturation = buf.readFloat();
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
                .writeFloat(health)
                .writeVarInt(food)
                .writeFloat(saturation)
                .bytes();
    }

    @Override
    public @NotNull PacketOut clone() {
        return new PacketPlayOutSetHealth(new FriendlyByteBuf(serialize()));
    }

}
