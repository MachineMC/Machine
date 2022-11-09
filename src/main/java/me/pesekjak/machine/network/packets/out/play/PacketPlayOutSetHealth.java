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
public class PacketPlayOutSetHealth extends PacketOut {

    private static final int ID = 0x55;

    private float health;
    private int food;
    private float saturation;

    static {
        register(PacketPlayOutSetHealth.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutSetHealth::new);
    }

    public PacketPlayOutSetHealth(FriendlyByteBuf buf) {
        health = buf.readFloat();
        food = buf.readVarInt();
        saturation = buf.readFloat();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeFloat(health)
                .writeVarInt(food)
                .writeFloat(saturation)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutSetHealth(new FriendlyByteBuf(serialize()));
    }

}
