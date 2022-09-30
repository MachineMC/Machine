package me.pesekjak.machine.network.packets.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;

@AllArgsConstructor
public class PacketPlayOutEntityEvent extends PacketOut {

    private static final int ID = 0x1A;

    @Getter
    private int entityId;
    @Getter @Setter
    private byte event;

    static {
        register(PacketPlayOutEntityEvent.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutEntityEvent::new);
    }

    public PacketPlayOutEntityEvent(FriendlyByteBuf buf) {
        entityId = buf.readVarInt();
        event = buf.readByte();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeVarInt(entityId)
                .writeByte(event)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutEntityEvent(new FriendlyByteBuf(serialize()));
    }

}
