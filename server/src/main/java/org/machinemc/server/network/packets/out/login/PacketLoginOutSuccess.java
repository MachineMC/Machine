package org.machinemc.server.network.packets.out.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.machinemc.api.entities.player.PlayerTextures;
import org.machinemc.api.network.packets.Packet;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.utils.FriendlyByteBuf;
import org.machinemc.api.utils.ServerBuffer;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketLoginOutSuccess extends PacketOut {

    private static final int ID = 0x02;

    private UUID uuid;
    private String userName;
    private @Nullable PlayerTextures textures;

    static {
        register(PacketLoginOutSuccess.class, ID, Packet.PacketState.LOGIN_OUT,
                PacketLoginOutSuccess::new
        );
    }

    public PacketLoginOutSuccess(final ServerBuffer buf) {
        uuid = buf.readUUID();
        userName = buf.readString(StandardCharsets.UTF_8);
        textures = buf.readTextures();
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public Packet.PacketState getPacketState() {
        return Packet.PacketState.LOGIN_OUT;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeUUID(uuid)
                .writeString(userName, StandardCharsets.UTF_8)
                .writeTextures(textures)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketLoginOutSuccess(new FriendlyByteBuf(serialize()));
    }

}
