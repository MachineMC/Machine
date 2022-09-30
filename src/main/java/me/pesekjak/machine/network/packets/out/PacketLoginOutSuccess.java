package me.pesekjak.machine.network.packets.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.entities.player.PlayerTextures;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@AllArgsConstructor
public class PacketLoginOutSuccess extends PacketOut {

    private static final int ID = 0x02;

    @Getter @Setter @NotNull
    private UUID uuid;
    @Getter @Setter @NotNull
    private String userName;
    @Getter @Setter @Nullable
    private PlayerTextures textures;

    static {
        register(PacketLoginOutSuccess.class, ID, PacketState.LOGIN_OUT,
                PacketLoginOutSuccess::new
        );
    }

    public PacketLoginOutSuccess(FriendlyByteBuf buf) {
        uuid = buf.readUUID();
        userName = buf.readString();
        textures = buf.readTextures();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeUUID(uuid)
                .writeString(userName)
                .writeTextures(textures)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketLoginOutSuccess(new FriendlyByteBuf(serialize()));
    }

}
