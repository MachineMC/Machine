package me.pesekjak.machine.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.utils.ServerBuffer;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@ToString
public class PacketPlayOutLinkEntities extends PacketOut {

    private static final int ID = 0x51;

    @Getter @Setter
    private int attachedEntityId, holdingEntityId;

    static {
        register(PacketPlayOutLinkEntities.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutLinkEntities::new);
    }

    public PacketPlayOutLinkEntities(@NotNull ServerBuffer buf) {
        attachedEntityId = buf.readInt();
        holdingEntityId = buf.readInt();
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
                .writeInt(attachedEntityId)
                .writeInt(holdingEntityId)
                .bytes();
    }

    @Override
    public @NotNull PacketOut clone() {
        return new PacketPlayOutLinkEntities(new FriendlyByteBuf(serialize()));
    }

}
