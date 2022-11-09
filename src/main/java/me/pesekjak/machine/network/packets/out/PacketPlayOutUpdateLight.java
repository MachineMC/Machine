package me.pesekjak.machine.network.packets.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.chunk.data.LightData;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayOutUpdateLight extends PacketOut {

    private static final int ID = 0x24;

    private int chunkX;
    private int chunkZ;
    @NotNull
    private LightData lightData;

    static {
        register(PacketPlayOutUpdateLight.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutUpdateLight::new);
    }

    public PacketPlayOutUpdateLight(FriendlyByteBuf buf) {
        chunkX = buf.readVarInt();
        chunkZ = buf.readVarInt();
        lightData = new LightData(buf);
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        FriendlyByteBuf buf = new FriendlyByteBuf()
                .writeVarInt(chunkX)
                .writeVarInt(chunkZ);
        lightData.write(buf);
        return buf.bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutUpdateLight(new FriendlyByteBuf(serialize()));
    }

}
