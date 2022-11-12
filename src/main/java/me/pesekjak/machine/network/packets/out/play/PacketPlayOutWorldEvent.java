package me.pesekjak.machine.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.world.BlockPosition;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayOutWorldEvent extends PacketOut {

    private static final int ID = 0x22;

    private int event, data;
    @NotNull
    private BlockPosition position;
    private boolean disableRelativeVolume;

    static {
        register(PacketPlayOutWorldEvent.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutWorldEvent::new);
    }

    public PacketPlayOutWorldEvent(FriendlyByteBuf buf) {
        event = buf.readInt();
        position = buf.readBlockPos();
        data = buf.readInt();
        disableRelativeVolume = buf.readBoolean();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeInt(event)
                .writeBlockPos(position)
                .writeInt(data)
                .writeBoolean(disableRelativeVolume)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutWorldEvent(new FriendlyByteBuf(serialize()));
    }

}
