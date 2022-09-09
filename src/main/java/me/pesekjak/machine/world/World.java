package me.pesekjak.machine.world;

import lombok.Builder;
import lombok.Getter;
import me.pesekjak.machine.utils.NamespacedKey;
import me.pesekjak.machine.world.dimensions.DimensionType;

@Builder
public class World {

    public static final World MAIN = World.builder()
            .name(NamespacedKey.machine("main"))
            .dimensionType(DimensionType.OVERWORLD)
            .seed(1)
            .build();

    @Getter
    private final NamespacedKey name;
    @Getter
    private final DimensionType dimensionType;
    @Getter
    private final long seed;

}
