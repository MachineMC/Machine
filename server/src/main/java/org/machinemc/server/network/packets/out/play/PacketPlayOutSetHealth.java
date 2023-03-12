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
public class PacketPlayOutSetHealth extends PacketOut {

    private static final int ID = 0x55;

    private float health;
    private int food;
    private float saturation;

    static {
        register(PacketPlayOutSetHealth.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutSetHealth::new);
    }

    public PacketPlayOutSetHealth(ServerBuffer buf) {
        health = buf.readFloat();
        food = buf.readVarInt();
        saturation = buf.readFloat();
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
