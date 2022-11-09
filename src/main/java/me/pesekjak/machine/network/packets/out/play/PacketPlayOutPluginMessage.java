package me.pesekjak.machine.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.utils.NamespacedKey;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayOutPluginMessage extends PacketOut {

    private static final int ID = 0x16;

    @NotNull
    private NamespacedKey channel;
    @NotNull
    private FriendlyByteBuf data;

    static {
        register(PacketPlayOutPluginMessage.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutPluginMessage::new
        );
    }

    public PacketPlayOutPluginMessage(FriendlyByteBuf buf) {
        channel = NamespacedKey.parse(buf.readString());
        data = new FriendlyByteBuf(buf.finish());
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeString(channel.toString())
                .writeBytes(data.bytes())
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutPluginMessage(new FriendlyByteBuf(serialize()));
    }

    public static NamespacedKey BRAND_CHANNEL = NamespacedKey.minecraft("brand");

    public static PacketPlayOutPluginMessage getBrandPacket(String brand) {
        return new PacketPlayOutPluginMessage(
                BRAND_CHANNEL,
                new FriendlyByteBuf().
                        writeString(brand)
        );
    }

}
