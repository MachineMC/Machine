package me.pesekjak.machine.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.utils.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayOutPlaceGhostRecipe extends PacketOut {

    private static final int ID = 0x30;

    private byte windowId;
    @NotNull
    private NamespacedKey recipe;

    static {
        register(PacketPlayOutPlaceGhostRecipe.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutPlaceGhostRecipe::new);
    }

    public PacketPlayOutPlaceGhostRecipe(FriendlyByteBuf buf) {
        windowId = buf.readByte();
        recipe = buf.readNamespacedKey();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeByte(windowId)
                .writeNamespacedKey(recipe)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutPlaceGhostRecipe(new FriendlyByteBuf(serialize()));
    }

}
