package org.machinemc.server.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.machinemc.api.network.packets.Packet;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.utils.FriendlyByteBuf;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.api.utils.ServerBuffer;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayOutPluginMessage extends PacketOut {

    private static final int ID = 0x16;

    private @NotNull NamespacedKey channel;
    private @NotNull ServerBuffer data;

    static {
        register(PacketPlayOutPluginMessage.class, ID, Packet.PacketState.PLAY_OUT,
                PacketPlayOutPluginMessage::new
        );
    }

    public PacketPlayOutPluginMessage(@NotNull ServerBuffer buf) {
        channel = buf.readNamespacedKey();
        data = new FriendlyByteBuf(buf.finish());
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public @NotNull Packet.PacketState getPacketState() {
        return Packet.PacketState.PLAY_OUT;
    }

    @Override
    public byte @NotNull [] serialize() {
        return new FriendlyByteBuf()
                .writeNamespacedKey(channel)
                .writeBytes(data.bytes())
                .bytes();
    }

    @Override
    public @NotNull PacketOut clone() {
        return new PacketPlayOutPluginMessage(new FriendlyByteBuf(serialize()));
    }

    public static NamespacedKey BRAND_CHANNEL = NamespacedKey.minecraft("brand");

    public static PacketPlayOutPluginMessage getBrandPacket(String brand) {
        return new PacketPlayOutPluginMessage(
                BRAND_CHANNEL,
                new FriendlyByteBuf().
                        writeString(brand, StandardCharsets.UTF_8)
        );
    }

}
