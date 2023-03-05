package org.machinemc.server.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.utils.FriendlyByteBuf;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayOutPlaceGhostRecipe extends PacketOut {

    private static final int ID = 0x30;

    private byte windowId;
    private NamespacedKey recipe;

    static {
        register(PacketPlayOutPlaceGhostRecipe.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutPlaceGhostRecipe::new);
    }

    public PacketPlayOutPlaceGhostRecipe(ServerBuffer buf) {
        windowId = buf.readByte();
        recipe = buf.readNamespacedKey();
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
                .writeByte(windowId)
                .writeNamespacedKey(recipe)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutPlaceGhostRecipe(new FriendlyByteBuf(serialize()));
    }

}
