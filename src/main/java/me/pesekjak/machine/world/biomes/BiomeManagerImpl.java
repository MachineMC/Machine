package me.pesekjak.machine.world.biomes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.utils.NamespacedKey;
import mx.kenzie.nbt.NBTCompound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Default implementation of biome manager.
 */
@RequiredArgsConstructor
public class BiomeManagerImpl implements BiomeManager {

    protected final @NotNull AtomicInteger ID_COUNTER = new AtomicInteger(0);
    private static final String CODEC_TYPE = "minecraft:worldgen/biome";

    private final @NotNull Map<Integer, Biome> biomes = new ConcurrentHashMap<>();
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
        if(isRegistered(biome))
            throw new IllegalStateException("Biome '" + biome.getName() + "' is already registered");
        biomes.put(ID_COUNTER.getAndIncrement(), biome);
    }

    @Override
    public boolean removeBiome(@NotNull Biome biome) {
        return biomes.remove(getBiomeId(biome)) == null;
    }

    @Override
    public boolean isRegistered(@NotNull Biome biome) {
        return biomes.containsValue(biome);
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
        return biomes.get(id);
    }

    @Override
    public int getBiomeId(Biome biome) {
        for (Map.Entry<Integer, Biome> entry : biomes.entrySet()) {
            if (entry.getValue().equals(biome))
                return entry.getKey();
        }
        return -1;
    }

    @Override
    public @NotNull Set<Biome> getBiomes() {
        return biomes.values().stream().collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public NBTCompound getBiomeNBT(Biome biome) {
        NBTCompound nbtCompound = biome.toNBT();
        return new NBTCompound(Map.of(
                "name", biome.getName().toString(),
                "id", ID_COUNTER.get(),
                "element", nbtCompound
        ));
    }

    @Override
    public @NotNull String getCodecType() {
        return CODEC_TYPE;
    }

    @Override
    public @NotNull List<NBTCompound> getCodecElements() {
        return new ArrayList<>(biomes.values().stream()
                .map(Biome::toNBT)
                .toList());
    }

}
