package me.pesekjak.machine.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;

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

    public PacketPlayOutLinkEntities(FriendlyByteBuf buf) {
        attachedEntityId = buf.readInt();
        holdingEntityId = buf.readInt();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeInt(attachedEntityId)
                .writeInt(holdingEntityId)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutLinkEntities(new FriendlyByteBuf(serialize()));
    }

}
