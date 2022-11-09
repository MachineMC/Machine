package me.pesekjak.machine.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;

@AllArgsConstructor
public class PacketPlayOutPickupItem extends PacketOut {

    private static final int ID = 0x65;

    @Getter @Setter
    private int collectedEntityId, collectorEntityId, pickupItemCount;

    static {
        register(PacketPlayOutPickupItem.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutPickupItem::new);
    }

    public PacketPlayOutPickupItem(FriendlyByteBuf buf) {
        collectedEntityId = buf.readVarInt();
        collectorEntityId = buf.readVarInt();
        pickupItemCount = buf.readVarInt();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeVarInt(collectedEntityId)
                .writeVarInt(collectorEntityId)
                .writeVarInt(pickupItemCount)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutPickupItem(new FriendlyByteBuf(serialize()));
    }

}
