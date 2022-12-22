package me.pesekjak.machine.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.utils.ServerBuffer;
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

    public PacketPlayOutWorldEvent(@NotNull ServerBuffer buf) {
        event = buf.readInt();
        position = buf.readBlockPos();
        data = buf.readInt();
        disableRelativeVolume = buf.readBoolean();
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
                .writeInt(event)
                .writeBlockPos(position)
                .writeInt(data)
                .writeBoolean(disableRelativeVolume)
                .bytes();
    }

    @Override
    public @NotNull PacketOut clone() {
        return new PacketPlayOutWorldEvent(new FriendlyByteBuf(serialize()));
    }

}
