package me.pesekjak.machine.network.packets.out;

import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.utils.NamespacedKey;

import java.nio.charset.StandardCharsets;

public class PacketPlayOutPluginMessage extends PacketOut {

    public static final int ID = 0x16;

    @Getter @Setter
    private NamespacedKey channel;
    @Getter @Setter
    private FriendlyByteBuf data;

    static {
        PacketOut.register(PacketPlayOutPluginMessage.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutPluginMessage::new
        );
    }

    public PacketPlayOutPluginMessage(FriendlyByteBuf buf) {
        channel = NamespacedKey.parse(buf.readString(StandardCharsets.UTF_8));
        data = new FriendlyByteBuf(buf.finish());
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeString(channel.toString(), StandardCharsets.UTF_8)
                .writeBytes(data.bytes())
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutPluginMessage(new FriendlyByteBuf(serialize()));
    }

    public static NamespacedKey BRAND_CHANNEL = NamespacedKey.minecraft("brand");

    public static PacketPlayOutPluginMessage getBrandPacket(String brand) {
        FriendlyByteBuf buf = new FriendlyByteBuf()
                .writeString(BRAND_CHANNEL.toString(), StandardCharsets.UTF_8)
                .writeBytes(
                        new FriendlyByteBuf()
                        .writeString(brand, StandardCharsets.UTF_8)
                        .bytes()
                );
        return new PacketPlayOutPluginMessage(buf);
    }

}