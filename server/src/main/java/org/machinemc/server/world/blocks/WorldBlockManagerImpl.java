package org.machinemc.server.world.blocks;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.Getter;
import lombok.Synchronized;
import org.jetbrains.annotations.Async;
import org.machinemc.api.server.schedule.Scheduler;
import org.machinemc.api.world.BlockPosition;
import org.machinemc.api.world.World;
import org.machinemc.api.world.blocks.BlockType;
import org.machinemc.api.world.blocks.WorldBlock;
import org.machinemc.api.world.blocks.WorldBlockManager;
import org.machinemc.nbt.NBTCompound;

import java.util.concurrent.Future;
import java.util.function.Function;

@SuppressWarnings("UnstableApiUsage")
public class WorldBlockManagerImpl implements WorldBlockManager {

    @Getter
    private final World world;
    private final Function<BlockPosition, BlockType> typeFunction;
    private final Function<BlockPosition, NBTCompound> nbtFunction;

    private final Scheduler scheduler;

    private final Cache<BlockPosition, WorldBlock> cached = CacheBuilder.newBuilder()
            .weakValues()
            .build();

    public WorldBlockManagerImpl(World world,
                                 Function<BlockPosition, BlockType> typeFunction,
                                 Function<BlockPosition, NBTCompound> nbtFunction) {
        this.world = world;
        this.typeFunction = typeFunction;
        this.nbtFunction = nbtFunction;
        scheduler = world.getServer().getScheduler();
    }

    @Synchronized
    @Override
    public WorldBlock get(BlockPosition position) {
        WorldBlock block = cached.getIfPresent(position);
        if(block != null) return block;
        block = new WorldBlockImpl(() -> typeFunction.apply(position), position, world, nbtFunction.apply(position));
        cached.put(position, block);
        return block;
    }

    @Async.Execute
    @Override
    public Future<WorldBlock> getAsync(BlockPosition position) {
        return scheduler.getThreadPoolExecutor().submit(() -> get(position));
    }

}
