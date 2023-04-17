package org.machinemc.server.world.blocks;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Synchronized;
import org.machinemc.api.world.BlockPosition;
import org.machinemc.api.world.World;
import org.machinemc.api.world.blocks.BlockType;
import org.machinemc.api.world.blocks.WorldBlock;

import java.util.function.Function;

/**
 * Manager of world blocks, prevents multiple WorldBlock instances of a single block to exists,
 * provides suppliers of block types and nbt to the world blocks.
 */
@SuppressWarnings("UnstableApiUsage")
@AllArgsConstructor
public class WorldBlockManager {

    @Getter
    private final World world;
    private final Function<BlockPosition, BlockType> blockTypeSupplier;

    private final Cache<BlockPosition, WorldBlock> cached = CacheBuilder.newBuilder()
            .weakValues()
            .build();

    /**
     * Returns a world block instance of a given position.
     * @param position position
     * @return world block at given position
     */
    @Synchronized
    public WorldBlock get(final BlockPosition position) {
        try {
            return cached.get(position, () -> new WorldBlockImpl(
                    world,
                    position,
                    () -> blockTypeSupplier.apply(position)));
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

}
