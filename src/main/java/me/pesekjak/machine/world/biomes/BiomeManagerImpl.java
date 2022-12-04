package me.pesekjak.machine.world.biomes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.server.ServerProperty;
import me.pesekjak.machine.server.codec.CodecPart;
import me.pesekjak.machine.utils.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Manages multiple biomes of the server, each biome
 * has to reference manager it was created for.
 */
@RequiredArgsConstructor
public class BiomeManagerImpl implements BiomeManager {

    protected final AtomicInteger ID_COUNTER = new AtomicInteger(0);
    private static final String CODEC_TYPE = "minecraft:worldgen/biome";

    private final Set<Biome> biomes = new CopyOnWriteArraySet<>();
    @Getter
    private final Machine server;

    /**
     * Creates manager with default settings.
     * @param server server to create manager for
     * @return newly created manager
     */
    public static BiomeManagerImpl createDefault(Machine server) {
        BiomeManagerImpl manager = new BiomeManagerImpl(server);
        manager.addBiome(BiomeImpl.createDefault());
        return manager;
    }

    /**
     * Registers the biome to this manager if it's not registered already
     * in a different one.
     * @param biome biome to register
     */
    public void addBiome(Biome biome) {
        if(biome.getManager() != null && biome.getManager() != this)
            throw new IllegalStateException("Biome '" + biome.getName() + "' is already registered in a different BiomeManager");
        biome.getManagerReference().set(this);
        biome.getIdReference().set(ID_COUNTER.getAndIncrement());
        biomes.add(biome);
    }

    /**
     * Removes the biome from the manager if it's registered in this manager.
     * @param biome biome that should be removed
     * @return true if the biome was removed successfully
     */
    public boolean removeBiome(Biome biome) {
        if(biome.getManager() != this) return false;
        if(biomes.remove(biome)) {
            biome.getManagerReference().set(null);
            biome.getIdReference().set(-1);
            return true;
        }
        return false;
    }

    /**
     * Checks if the biome with given name exists.
     * @param name name of the biome
     * @return true if the biome exists
     */
    public boolean isRegistered(@NotNull NamespacedKey name) {
        final Biome biome = getBiome(name);
        if(biome == null) return false;
        return isRegistered(biome);
    }

    /**
     * Checks if the biome is registered in this manager.
     * @param biome biome to check for
     * @return true if the biome is registered in this manager
     */
    public boolean isRegistered(@NotNull Biome biome) {
        return biomes.contains(biome);
    }

    /**
     * Searches for registered biome with the given name in this manager.
     * @param name name of the biome to search for
     * @return biome with the given name
     */
    public Biome getBiome(@NotNull NamespacedKey name) {
        for(Biome biome : getBiomes()) {
            if(!(biome.getName().equals(name))) continue;
            return biome;
        }
        return null;
    }

    /**
     * Searches for registered biome with the given id in this manager.
     * @param id id of the biome to search for
     * @return biome with the given id
     */
    public Biome getById(int id) {
        for(Biome biome : getBiomes()) {
            if (biome.getIdReference().get() != id) continue;
            return biome;
        }
        return null;
    }

    /**
     * Collection of all registered biomes in this manager
     * @return collection of all registered biomes
     */
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
