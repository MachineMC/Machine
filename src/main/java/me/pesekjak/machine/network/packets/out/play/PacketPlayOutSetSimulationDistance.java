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
public class PacketPlayOutSetSimulationDistance extends PacketOut {

    private static final int ID = 0x5A;

    @Getter @Setter
    private int distance;

    static {
        register(PacketPlayOutSetSimulationDistance.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutSetSimulationDistance::new);
    }

    public PacketPlayOutSetSimulationDistance(FriendlyByteBuf buf) {
        distance = buf.readVarInt();
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
                .writeVarInt(distance)
                .bytes();
    }

    @Override
    public @NotNull PacketOut clone() {
        return new PacketPlayOutSetSimulationDistance(new FriendlyByteBuf(serialize()));
    }

}
