package org.machinemc.server.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.machinemc.api.network.packets.Packet;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.utils.FriendlyByteBuf;
import org.machinemc.api.utils.ServerBuffer;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@ToString
public class PacketPlayOutPickupItem extends PacketOut {

    private static final int ID = 0x65;

    @Getter @Setter
    private int collectedEntityId, collectorEntityId, pickupItemCount;

    static {
        register(PacketPlayOutPickupItem.class, ID, Packet.PacketState.PLAY_OUT,
                PacketPlayOutPickupItem::new);
    }

    public PacketPlayOutPickupItem(@NotNull ServerBuffer buf) {
        collectedEntityId = buf.readVarInt();
        collectorEntityId = buf.readVarInt();
        pickupItemCount = buf.readVarInt();
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public @NotNull Packet.PacketState getPacketState() {
        return Packet.PacketState.PLAY_OUT;
    }

    @Override
    public byte @NotNull [] serialize() {
        return new FriendlyByteBuf()
                .writeVarInt(collectedEntityId)
                .writeVarInt(collectorEntityId)
                .writeVarInt(pickupItemCount)
                .bytes();
    }

    @Override
    public @NotNull PacketOut clone() {
        return new PacketPlayOutPickupItem(new FriendlyByteBuf(serialize()));
    }

}
