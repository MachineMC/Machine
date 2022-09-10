package me.pesekjak.machine.world;

import lombok.Builder;
import lombok.Getter;
import me.pesekjak.machine.entities.Entity;
import me.pesekjak.machine.entities.Player;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.network.packets.out.PacketPlayOutChangeDifficulty;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.utils.NamespacedKey;
import me.pesekjak.machine.world.dimensions.DimensionType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Builder
public class World {

    public static final World MAIN = World.builder()
            .name(NamespacedKey.machine("main"))
            .dimensionType(DimensionType.OVERWORLD)
            .seed(1)
            .difficulty(Difficulty.DEFAULT_DIFFICULTY)
            .build();

    @Getter
    private final NamespacedKey name;
    @Getter
    private final DimensionType dimensionType;
    @Getter
    private final List<Entity> entityList = new ArrayList<>();
    @Getter
    private final long seed;
    @Getter
    private Difficulty difficulty;

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
        FriendlyByteBuf buf = new FriendlyByteBuf()
                .writeByte((byte) difficulty.getId());
        PacketOut packet = new PacketPlayOutChangeDifficulty(buf);
        for (Entity entity : entityList) {
            if (!(entity instanceof Player player))
                continue;
            try {
                player.getConnection().sendPacket(packet);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
