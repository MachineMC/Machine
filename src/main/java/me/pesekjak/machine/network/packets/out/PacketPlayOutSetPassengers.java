package me.pesekjak.machine.network.packets.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;

@AllArgsConstructor
public class PacketPlayOutSetPassengers extends PacketOut {

    private static final int ID = 0x57;

    @Getter @Setter
    private int entityId;
    @Getter @Setter
    private int[] passengers;

    static {
        register(PacketPlayOutSetPassengers.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutSetPassengers::new);
    }

    public PacketPlayOutSetPassengers(FriendlyByteBuf buf) {
        entityId = buf.readVarInt();
        passengers = buf.readVarIntArray();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeVarInt(entityId)
                .writeVarIntArray(passengers)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutSetPassengers(new FriendlyByteBuf(serialize()));
    }

}
