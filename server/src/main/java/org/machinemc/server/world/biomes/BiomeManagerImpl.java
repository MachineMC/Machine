package org.machinemc.server.world.biomes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.machinemc.nbt.NBTCompound;
import org.machinemc.server.Machine;
import org.machinemc.api.utils.NamespacedKey;
import org.jetbrains.annotations.Nullable;
import org.machinemc.api.world.biomes.Biome;
import org.machinemc.api.world.biomes.BiomeManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Default implementation of biome manager.
 */
@RequiredArgsConstructor
public class BiomeManagerImpl implements BiomeManager {

    protected final AtomicInteger ID_COUNTER = new AtomicInteger(0);
    private static final String CODEC_TYPE = "minecraft:worldgen/biome";

    private final Map<Integer, Biome> biomes = new ConcurrentHashMap<>();
    @Getter
    private final Machine server;

    /**
     * Creates manager with default settings.
     * @param server server to create manager for
     * @return newly created manager
     */
    public static BiomeManager createDefault(Machine server) {
        BiomeManagerImpl manager = new BiomeManagerImpl(server);
        manager.addBiome(BiomeImpl.createDefault());
        return manager;
    }

    @Override
    public void addBiome(Biome biome) {
        if(isRegistered(biome))
            throw new IllegalStateException("Biome '" + biome.getName() + "' is already registered");
        biomes.put(ID_COUNTER.getAndIncrement(), biome);
    }

    @Override
    public boolean removeBiome(Biome biome) {
        return biomes.remove(getBiomeId(biome)) == null;
    }

    @Override
    public boolean isRegistered(Biome biome) {
        return biomes.containsValue(biome);
    }

    @Override
    public @Nullable Biome getBiome(NamespacedKey name) {
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
    public Set<Biome> getBiomes() {
        return biomes.values().stream().collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public NBTCompound getBiomeNBT(Biome biome) {
        if(!isRegistered(biome))
            throw new IllegalStateException();
        NBTCompound nbtCompound = biome.toNBT();
        return new NBTCompound(Map.of(
                "name", biome.getName().toString(),
                "id", getBiomeId(biome),
                "element", nbtCompound
        ));
    }

    @Override
    public String getCodecType() {
        return CODEC_TYPE;
    }

    @Override
    public List<NBTCompound> getCodecElements() {
        return new ArrayList<>(biomes.values().stream()
                .map(this::getBiomeNBT)
                .toList());
    }

}
