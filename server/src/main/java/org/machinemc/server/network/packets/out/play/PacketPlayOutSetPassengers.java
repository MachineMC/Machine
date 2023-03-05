package org.machinemc.server.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.utils.FriendlyByteBuf;
import org.machinemc.api.utils.ServerBuffer;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayOutSetPassengers extends PacketOut {

    private static final int ID = 0x57;

    private int entityId;
    private int @NotNull [] passengers;

    static {
        register(PacketPlayOutSetPassengers.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutSetPassengers::new);
    }

    public PacketPlayOutSetPassengers(@NotNull ServerBuffer buf) {
        entityId = buf.readVarInt();
        passengers = buf.readVarIntArray();
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
                .writeVarIntArray(passengers)
                .bytes();
    }

    @Override
    public @NotNull PacketOut clone() {
        return new PacketPlayOutSetPassengers(new FriendlyByteBuf(serialize()));
    }

}
