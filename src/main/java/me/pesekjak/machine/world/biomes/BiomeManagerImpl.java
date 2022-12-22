package me.pesekjak.machine.world.biomes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.utils.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jglrxavpok.hephaistos.nbt.NBT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Default implementation of biome manager.
 */
@RequiredArgsConstructor
public class BiomeManagerImpl implements BiomeManager {

    protected final @NotNull AtomicInteger ID_COUNTER = new AtomicInteger(0);
    private static final String CODEC_TYPE = "minecraft:worldgen/biome";

    private final @NotNull Set<Biome> biomes = new CopyOnWriteArraySet<>();
    @Getter
    private final @NotNull Machine server;

    /**
     * Creates manager with default settings.
     * @param server server to create manager for
     * @return newly created manager
     */
    public static @NotNull BiomeManager createDefault(@NotNull Machine server) {
        BiomeManagerImpl manager = new BiomeManagerImpl(server);
        manager.addBiome(BiomeImpl.createDefault());
        return manager;
    }

    @Override
    public void addBiome(@NotNull Biome biome) {
        if(biome.getManager() != null && biome.getManager() != this)
            throw new IllegalStateException("Biome '" + biome.getName() + "' is already registered in a different BiomeManager");
        biome.getManagerReference().set(this);
        biome.getIdReference().set(ID_COUNTER.getAndIncrement());
        biomes.add(biome);
    }

    @Override
    public boolean removeBiome(@NotNull Biome biome) {
        if(biome.getManager() != this) return false;
        if(biomes.remove(biome)) {
            biome.getManagerReference().set(null);
            biome.getIdReference().set(-1);
            return true;
        }
        return false;
    }

    @Override
    public boolean isRegistered(@NotNull NamespacedKey name) {
        final Biome biome = getBiome(name);
        if(biome == null) return false;
        return isRegistered(biome);
    }

    @Override
    public boolean isRegistered(@NotNull Biome biome) {
        return biomes.contains(biome);
    }

    @Override
    public @Nullable Biome getBiome(@NotNull NamespacedKey name) {
        for(Biome biome : getBiomes()) {
            if(!(biome.getName().equals(name))) continue;
            return biome;
        }
        return null;
    }

    @Override
    public @Nullable Biome getById(int id) {
        for(Biome biome : getBiomes()) {
            if (biome.getIdReference().get() != id) continue;
            return biome;
        }
        return null;
    }

    @Override
    public @NotNull Set<Biome> getBiomes() {
        return Collections.unmodifiableSet(biomes);
    }

    @Override
    public @NotNull String getCodecType() {
        return CODEC_TYPE;
    }

    @Override
    public @NotNull List<NBT> getCodecElements() {
        return new ArrayList<>(biomes.stream()
                .map(Biome::toNBT)
                .toList());
    }

}
