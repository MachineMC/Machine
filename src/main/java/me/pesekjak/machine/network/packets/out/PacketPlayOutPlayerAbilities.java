package me.pesekjak.machine.network.packets.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.utils.NamespacedKey;

@AllArgsConstructor
public class PacketPlayOutPlayerAbilities extends PacketOut {

    private static final int ID = 0x31;

    @Getter @Setter
    private byte flags;
    @Getter @Setter
    private float flyingSpeed, fovModifier;

    static {
        register(PacketPlayOutPlayerAbilities.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutPlayerAbilities::new);
    }

    public PacketPlayOutPlayerAbilities(FriendlyByteBuf buf) {
        flags = buf.readByte();
        flyingSpeed = buf.readFloat();
        fovModifier = buf.readFloat();
    }

    @Override
    public int getID() {
        return ID;
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
