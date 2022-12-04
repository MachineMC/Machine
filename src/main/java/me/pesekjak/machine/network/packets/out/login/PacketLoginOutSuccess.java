package me.pesekjak.machine.network.packets.out.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.entities.player.PlayerTexturesImpl;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketLoginOutSuccess extends PacketOut {

    private static final int ID = 0x02;

    @NotNull
    private UUID uuid;
    @NotNull
    private String userName;
    @Nullable
    private PlayerTexturesImpl textures;

    static {
        register(PacketLoginOutSuccess.class, ID, PacketState.LOGIN_OUT,
                PacketLoginOutSuccess::new
        );
    }

    public PacketLoginOutSuccess(FriendlyByteBuf buf) {
        uuid = buf.readUUID();
        userName = buf.readString(StandardCharsets.UTF_8);
        textures = buf.readTextures();
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public @NotNull PacketState getPacketState() {
        return PacketState.LOGIN_OUT;
    }

    @Override
    public byte @NotNull [] serialize() {
        return new FriendlyByteBuf()
                .writeUUID(uuid)
                .writeString(userName, StandardCharsets.UTF_8)
                .writeTextures(textures)
                .bytes();
    }

    @Override
    public @NotNull PacketOut clone() {
        return new PacketLoginOutSuccess(new FriendlyByteBuf(serialize()));
    }

}
