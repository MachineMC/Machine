package org.machinemc.server.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.machinemc.scriptive.components.Component;
import org.jetbrains.annotations.Nullable;
import org.machinemc.api.network.packets.Packet;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.utils.FriendlyByteBuf;

import java.nio.charset.StandardCharsets;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayOutResourcePack extends PacketOut {

    private static final int ID = 0x3D;

    private String url;
    private String hash;
    private boolean forced;
    private @Nullable Component promptMessage;

    static {
        register(PacketPlayOutResourcePack.class, ID, Packet.PacketState.PLAY_OUT,
                PacketPlayOutResourcePack::new);
    }

    public PacketPlayOutResourcePack(final ServerBuffer buf) {
        url = buf.readString(StandardCharsets.UTF_8);
        hash = buf.readString(StandardCharsets.UTF_8);
        forced = buf.readBoolean();
        if (buf.readBoolean())
            promptMessage = buf.readComponent();
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public Packet.PacketState getPacketState() {
        return Packet.PacketState.PLAY_OUT;
    }

    @Override
    public byte[] serialize() {
        final FriendlyByteBuf buf = new FriendlyByteBuf()
                .writeString(url, StandardCharsets.UTF_8)
                .writeString(hash, StandardCharsets.UTF_8)
                .writeBoolean(forced)
                .writeBoolean(promptMessage != null);
        if (promptMessage != null)
            buf.writeComponent(promptMessage);
        return buf.bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutResourcePack(new FriendlyByteBuf(serialize()));
    }

}
