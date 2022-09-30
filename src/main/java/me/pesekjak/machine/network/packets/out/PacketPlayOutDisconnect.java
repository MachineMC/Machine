package me.pesekjak.machine.network.packets.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import net.kyori.adventure.text.Component;

@AllArgsConstructor
public class PacketPlayOutDisconnect extends PacketOut {

    private static final int ID = 0x19;

    @Getter @Setter
    private Component reason;

    static {
        register(PacketPlayOutDisconnect.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutDisconnect::new);
    }

    public PacketPlayOutDisconnect(FriendlyByteBuf buf) {
        reason = buf.readComponent();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeComponent(reason)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutDisconnect(new FriendlyByteBuf(serialize()));
    }

}
