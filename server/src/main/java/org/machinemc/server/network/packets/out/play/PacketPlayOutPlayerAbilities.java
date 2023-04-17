package org.machinemc.server.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.utils.FriendlyByteBuf;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayOutPlayerAbilities extends PacketOut {

    private static final int ID = 0x31;

    private byte flags;
    private float flyingSpeed, fovModifier;

    static {
        register(PacketPlayOutPlayerAbilities.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutPlayerAbilities::new);
    }

    public PacketPlayOutPlayerAbilities(final ServerBuffer buf) {
        flags = buf.readByte();
        flyingSpeed = buf.readFloat();
        fovModifier = buf.readFloat();
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public PacketState getPacketState() {
        return PacketState.PLAY_OUT;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeByte(flags)
                .writeFloat(flyingSpeed)
                .writeFloat(fovModifier)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutPlayerAbilities(new FriendlyByteBuf(serialize()));
    }

}
