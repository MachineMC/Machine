package me.pesekjak.machine.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.chunk.data.LightData;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.utils.ServerBuffer;
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
