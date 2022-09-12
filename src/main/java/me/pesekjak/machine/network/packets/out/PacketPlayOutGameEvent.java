package me.pesekjak.machine.network.packets.out;

import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;

public class PacketPlayOutGameEvent extends PacketOut {

    private static final int ID = 0x1D;

    @Getter @Setter
    private byte event; // TODO Enum for the event
    @Getter @Setter
    private float value;

    static {
        PacketOut.register(PacketPlayOutGameEvent.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutGameEvent::new);
    }

    public PacketPlayOutGameEvent(FriendlyByteBuf buf) {
        event = buf.readByte();
        value = buf.readFloat();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeByte(event)
                .writeFloat(value)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutGameEvent(new FriendlyByteBuf(serialize()));
    }
}
