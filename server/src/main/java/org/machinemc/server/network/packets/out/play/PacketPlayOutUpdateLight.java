package org.machinemc.server.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.machinemc.server.chunk.data.LightData;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.utils.FriendlyByteBuf;
import org.machinemc.api.utils.ServerBuffer;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayOutUpdateLight extends PacketOut {

    private static final int ID = 0x24;

    private int chunkX, chunkZ;
    private @NotNull LightData lightData;

    static {
        register(PacketPlayOutUpdateLight.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutUpdateLight::new);
    }

    public PacketPlayOutUpdateLight(@NotNull ServerBuffer buf) {
        chunkX = buf.readVarInt();
        chunkZ = buf.readVarInt();
        lightData = new LightData(buf);
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public @NotNull PacketState getPacketState() {
        return PacketState.PLAY_OUT;
    }

    @Override
    public byte @NotNull [] serialize() {
        return new FriendlyByteBuf()
                .writeVarInt(chunkX)
                .writeVarInt(chunkZ)
                .write(lightData)
                .bytes();
    }

    @Override
    public @NotNull PacketOut clone() {
        return new PacketPlayOutUpdateLight(new FriendlyByteBuf(serialize()));
    }

}
