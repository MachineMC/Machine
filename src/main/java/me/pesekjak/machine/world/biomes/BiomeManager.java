package me.pesekjak.machine.world.biomes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.server.ServerProperty;
import me.pesekjak.machine.server.codec.CodecPart;
import me.pesekjak.machine.utils.NamespacedKey;
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
public class BiomeManager implements CodecPart, ServerProperty {

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
    public static BiomeManager createDefault(Machine server) {
        BiomeManager manager = new BiomeManager(server);
        manager.addBiome(Biome.createDefault());
        return manager;
    }

    /**
     * Registers the biome to this manager if it's not registered already
     * in a different one.
     * @param biome biome to register
     */
    public void addBiome(Biome biome) {
        if(biome.getManager().get() != null && biome.getManager().get() != this)
            throw new IllegalStateException("Biome '" + biome.getName() + "' is already registered in a different BiomeManager");
        biome.getManager().set(this);
        biome.id.set(ID_COUNTER.getAndIncrement());
        biomes.add(biome);
    }

    /**
     * Removes the biome from the manager if it's registered in this manager.
     * @param biome biome that should be removed
     * @return true if the biome was removed successfully
     */
    public boolean removeBiome(Biome biome) {
        if(biome.getManager().get() != this) return false;
        if(biomes.remove(biome)) {
            biome.getManager().set(null);
            biome.id.set(-1);
            return true;
        }
        return false;
    }

    /**
     * Checks if the biome with given name exists.
     * @param name name of the biome
     * @return true if the biome exists
     */
    public boolean isRegistered(NamespacedKey name) {
        return isRegistered(getBiome(name));
    }

    /**
     * Checks if the biome is registered in this manager.
     * @param biome biome to check for
     * @return true if the biome is registered in this manager
     */
    public boolean isRegistered(Biome biome) {
        return biomes.contains(biome);
    }

    /**
     * Searches for registered biome with the given name in this manager.
     * @param name name of the biome to search for
     * @return biome with the given name
     */
    public Biome getBiome(NamespacedKey name) {
        for(Biome biome : getBiomes()) {
            if(!(biome.getName().equals(name))) continue;
            return biome;
        }
        return null;
    }

    /**
     * Collection of all registered biomes in this manager
     * @return collection of all registered biomes
     */
    public Set<Biome> getBiomes() {
        return Collections.unmodifiableSet(biomes);
    }

    @Override
    public String getCodecType() {
        return CODEC_TYPE;
    }

    @Override
    public List<NBT> getCodecElements() {
        return new ArrayList<>(biomes.stream()
                .map(Biome::toNBT)
                .toList());
    }

}
